package com.ubermind.internal.jenkinsnotifier.controller;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.ubermind.internal.jenkinsnotifier.jenkins.JenkinsNotification;

public class BuildNotificationController {

	public static void enqueueNotifyTask(JenkinsNotification buildInfo, Key buildKey) {
		// enqueue a notifier task
		Queue queue = QueueFactory.getDefaultQueue();
		TaskOptions options = TaskOptions.Builder.withUrl(NotifyConstants.URL).param("key", KeyFactory.keyToString(buildKey));
		queue.add(options);
	}

	private static interface NotifyConstants {
		public static final String URL = "/notifySubscribers";
	}
}
