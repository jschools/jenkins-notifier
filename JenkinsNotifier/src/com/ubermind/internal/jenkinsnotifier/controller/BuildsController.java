package com.ubermind.internal.jenkinsnotifier.controller;

import java.util.logging.Logger;

import com.google.appengine.api.datastore.Key;
import com.ubermind.internal.jenkinsnotifier.jenkins.JenkinsNotification;
import com.ubermind.internal.jenkinsnotifier.persistence.JenkinsBuilds;

public class BuildsController {

	public static void onBuildInfoPosted(JenkinsNotification buildInfo) {
		Logger.getLogger("BuildsController").info("BuildsController.onBuildInfoPosted()");
		Key buildKey = JenkinsBuilds.insertBuild(buildInfo);
		BuildNotificationController.enqueueNotifyTask(buildInfo, buildKey);
	}

}
