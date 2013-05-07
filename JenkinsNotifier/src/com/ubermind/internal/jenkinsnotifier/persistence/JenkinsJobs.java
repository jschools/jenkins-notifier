package com.ubermind.internal.jenkinsnotifier.persistence;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;
import com.ubermind.internal.jenkinsnotifier.persistence.UserSubscriptions.SubscriptionLevel;


public class JenkinsJobs {

	public static Key insertJob(String jobName) {
		// get datastore
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

		// get the previous job
		Entity job;
		try {
			job = datastore.get(getJobKey(jobName));
		}
		catch (EntityNotFoundException e) {
			job = null;
		}

		// insert a new one if necessary
		if (job == null) {
			job = new Entity(getJobKey(jobName));
			job.setProperty(DsConst.PROP_JOB_NAME, jobName);

			datastore.put(job);
		}

		return job.getKey();
	}

	public static List<String> getAllJobs() {
		List<String> jobs = new ArrayList<String>();

		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		Query query = new Query(DsConst.KIND_JOB, getAllJobsKey());

		List<Entity> result = datastore.prepare(query).asList(FetchOptions.Builder.withDefaults());
		for (Entity e : result) {
			jobs.add((String) e.getProperty(DsConst.PROP_JOB_NAME));
		}

		return jobs;
	}

	public static Map<String, SubscriptionLevel> getSubscribedJobsForUser() {
		Map<String, SubscriptionLevel> result = new HashMap<String, SubscriptionLevel>();

		result.put("JenkinsNotifier.TestJob2", SubscriptionLevel.Success);

		return result;
	}

	static Key getJobKey(String jobName) {
		return KeyFactory.createKey(getAllJobsKey(), DsConst.KIND_JOB, jobName);
	}

	static Key getAllJobsKey() {
		return KeyFactory.createKey(DsConst.KIND_JOB_LIST, DsConst.NAME_ALL_JOBS);
	}

	private static interface DsConst {
		public static final String KIND_JOB_LIST = "job_list";
		public static final String NAME_ALL_JOBS = "all_jobs";

		public static final String KIND_JOB = "job";

		public static final String PROP_JOB_NAME = "name";
	}
}
