package com.ubermind.internal.jenkinsnotifier.persistence;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Transaction;
import com.ubermind.internal.jenkinsnotifier.util.DatastoreUtil;

public class UserSubscriptions {

	public static boolean setUserSubscriptions(String userId, Map<String, SubscriptionLevel> subscriptionStates) {
		if (userId == null || userId.isEmpty()) {
			return false;
		}

		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

		Transaction txn = datastore.beginTransaction();
		try {
			Entity userEntity = new Entity(getUserKey(userId));
			datastore.put(userEntity);

			// delete all subscriptions for this user
			Query query = new Query(DsConst.KIND_SUBSCRIPTION, userEntity.getKey());
			query.setKeysOnly();
			List<Entity> oldSettings = datastore.prepare(query).asList(FetchOptions.Builder.withDefaults());
			List<Key> oldKeys = new ArrayList<Key>(oldSettings.size());
			for (Entity oldSetting : oldSettings) {
				oldKeys.add(oldSetting.getKey());
			}
			datastore.delete(oldKeys);

			// insert new subscriptions for this user
			for (String jobName : subscriptionStates.keySet()) {
				SubscriptionLevel level = subscriptionStates.get(jobName);

				Entity subscriptionEntity = new Entity(DsConst.KIND_SUBSCRIPTION, userEntity.getKey());
				subscriptionEntity.setUnindexedProperty(DsConst.PROP_LEVEL, Integer.valueOf(level.getLevelInt()));
				subscriptionEntity.setUnindexedProperty(DsConst.PROP_JOB_NAME, jobName);

				datastore.put(subscriptionEntity);
			}

			txn.commit();
		}
		finally {
			if (txn.isActive()) {
				txn.rollback();
				return false;
			}
		}

		return true;
	}

	public static Map<String, SubscriptionLevel> getUserSubscriptions(String userId) {
		Map<String, SubscriptionLevel> subscriptions = new HashMap<String, SubscriptionLevel>();

		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		Query query = new Query(DsConst.KIND_SUBSCRIPTION, getUserKey(userId));

		List<Entity> entities = datastore.prepare(query).asList(FetchOptions.Builder.withDefaults());
		for (Entity entity : entities) {
			String jobName = (String) entity.getProperty(DsConst.PROP_JOB_NAME);
			int level = DatastoreUtil.getEntityPropertyInt(entity, DsConst.PROP_LEVEL, -1);

			subscriptions.put(jobName, SubscriptionLevel.getLevel(level));
		}

		return subscriptions;
	}

	private static Key getUserKey(String userId) {
		return KeyFactory.createKey(DsConst.KIND_USER, userId);
	}

	public static enum SubscriptionLevel {
		None("Not subscribed"),
		Error("Errors"),
		Unstable("Unstable"),
		Success("Successful"),
		;

		private String title;

		private SubscriptionLevel(String title) {
			this.title = title;
		}

		public int getLevelInt() {
			return ordinal();
		}

		public String getTitle() {
			return title;
		}

		public static SubscriptionLevel getLevel(int level) {
			try {
				return values()[level];
			}
			catch (Exception e) {
				// unkown
			}

			return None;
		}

		public static SubscriptionLevel parseString(String string) {
			try {
				return getLevel(Integer.parseInt(string));
			}
			catch (Exception e) {
				// unknown
			}

			return SubscriptionLevel.None;
		}

		public static int getCount() {
			return values().length;
		}
	}

	private interface DsConst {
		public static final String KIND_USER = "user";
		public static final String KIND_SUBSCRIPTION = "subscription";

		public static final String PROP_JOB_NAME = "job_name";
		public static final String PROP_LEVEL = "level";
	}

}
