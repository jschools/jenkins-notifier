package com.ubermind.internal.jenkinsnotifier.notify;

import java.io.IOException;

import com.ubermind.internal.jenkinsnotifier.jenkins.JenkinsNotification;

public class GlassNotifier implements UserBuildNotifier {
	@Override
	public boolean notifyUser(String userId, JenkinsNotification buildInfo) throws IOException {
		// TODO Auto-generated method stub
		return false;
	}
}