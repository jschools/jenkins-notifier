package com.ubermind.internal.jenkinsnotifier.notify;

import java.io.IOException;

import com.ubermind.internal.jenkinsnotifier.jenkins.JenkinsNotification;

public interface UserBuildNotifier {
	public boolean notifyUser(String userId, JenkinsNotification buildInfo) throws IOException;
}