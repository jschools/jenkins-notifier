package com.ubermind.internal.jenkinsnotifier.controller;

import java.util.List;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.ubermind.internal.jenkinsnotifier.NotifySubscribersServlet;
import com.ubermind.internal.jenkinsnotifier.jenkins.JenkinsNotification;
import com.ubermind.internal.jenkinsnotifier.persistence.UserSubscriptions;

public class BuildNotificationController {

	public static void enqueueNotifyTask(JenkinsNotification buildInfo, Key buildKey) {
		// enqueue a notifier task
		Queue queue = QueueFactory.getDefaultQueue();
		TaskOptions options = TaskOptions.Builder.withUrl(NotifyConstants.URL).param("key", KeyFactory.keyToString(buildKey));
		queue.add(options);
	}

	/**
	 * Called by {@link NotifySubscribersServlet}
	 */
	public static void notifySubscribedUsers(JenkinsNotification buildInfo) {
		// get subscriptions for buildInfo.getJobName()
		List<String> subscribers = UserSubscriptions.getSubscribersForBuild(buildInfo);

		// notify the users of this build
		for (String userId : subscribers) {
			UserNotificationController.notifyUserOfBuild(userId, buildInfo);
		}
	}

	private static interface NotifyConstants {
		public static final String URL = "/notifySubscribers";
	}
}
