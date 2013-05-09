package com.ubermind.internal.jenkinsnotifier.controller;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.ubermind.internal.jenkinsnotifier.jenkins.JenkinsNotification;
import com.ubermind.internal.jenkinsnotifier.notify.TasksNotifier;
import com.ubermind.internal.jenkinsnotifier.notify.UserBuildNotifier;

public class UserNotificationController {

	private static final UserBuildNotifier notifier = new TasksNotifier();

	public static void notifyUserOfBuild(String userId, JenkinsNotification buildInfo) {
		boolean success = false;
		try {
			success = notifier.notifyUser(userId, buildInfo);
		}
		catch (Exception e) {
			Logger.getLogger("UserNotificationController").log(Level.SEVERE, e.toString());
			System.err.println(e.getMessage());
		}

		if (!success) {
			System.err.println("error notifying user");
		}
	}

}
