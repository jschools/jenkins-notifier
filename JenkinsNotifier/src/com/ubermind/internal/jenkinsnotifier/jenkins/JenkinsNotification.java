package com.ubermind.internal.jenkinsnotifier.jenkins;

import com.google.api.client.util.Key;
import com.google.api.client.util.Value;
import com.google.appengine.api.datastore.Entity;

public class JenkinsNotification {

	@Key(Json.JOB_NAME)
	private String jobName;

	@Key(Json.BUILD_RESULT)
	private BuildResult buildResult;

	public JenkinsNotification() {
		// default constructor
	}

	public String getJobName() {
		return jobName;
	}

	public void setJobName(String jobName) {
		this.jobName = jobName;
	}

	public String getFullUrl() {
		return buildResult.fullUrl;
	}

	public void setFullUrl(String fullUrl) {
		buildResult.fullUrl = fullUrl;
	}

	public int getNumber() {
		return buildResult.number;
	}

	public void setNumber(int number) {
		buildResult.number = number;
	}

	public String getPhase() {
		return buildResult.phase;
	}

	public void setPhase(String phase) {
		buildResult.phase = phase;
	}

	public BuildStatus getStatus() {
		return buildResult.status;
	}

	public void setStatus(BuildStatus status) {
		buildResult.status = status;
	}

	public void populateProperties(Entity entity) {
		entity.setUnindexedProperty(DsKey.FULL_URL, getFullUrl());
		entity.setProperty(DsKey.NUMBER, Integer.valueOf(getNumber()));
		entity.setUnindexedProperty(DsKey.PHASE, getPhase());
		entity.setProperty(DsKey.STATUS, getStatus().name());
	}

	public static class BuildResult {

		@Key(Json.FULL_URL)
		private String fullUrl;

		@Key(Json.NUMBER)
		private int number;

		@Key(Json.PHASE)
		private String phase;

		@Key(Json.STATUS)
		private BuildStatus status;

		public BuildResult() {
			// default constructor
		}
	}

	public enum BuildStatus {
		@Value
		SUCCESS,

		@Value
		UNSTABLE,

		@Value
		FAILURE,
		;
	}

	public static interface DsKey {
		public static final String FULL_URL = "full_url";
		public static final String NUMBER = "number";
		public static final String PHASE = "phase";
		public static final String STATUS = "status";
	}

	public static interface Json {
		public static final String JOB_NAME = "name";
		public static final String BUILD_RESULT = "build";
		public static final String FULL_URL = "full_url";
		public static final String NUMBER = "number";
		public static final String PHASE = "phase";
		public static final String STATUS = "status";
	}
}
