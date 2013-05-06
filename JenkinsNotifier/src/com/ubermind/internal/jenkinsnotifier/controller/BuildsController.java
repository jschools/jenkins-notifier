package com.ubermind.internal.jenkinsnotifier.controller;

import com.google.appengine.api.datastore.Key;
import com.ubermind.internal.jenkinsnotifier.jenkins.JenkinsNotification;
import com.ubermind.internal.jenkinsnotifier.persistence.JenkinsBuilds;

public class BuildsController {

	public static void onBuildInfoPosted(JenkinsNotification buildInfo) {
		Key buildKey = JenkinsBuilds.insertBuild(buildInfo);
		BuildNotificationController.enqueueNotifyTask(buildInfo, buildKey);

	}

}
