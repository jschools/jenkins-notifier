package com.ubermind.internal.jenkinsnotifier.jenkins;

import java.util.Date;

import com.google.api.client.util.DateTime;
import com.google.api.client.util.Key;
import com.google.api.client.util.Value;
import com.google.appengine.api.datastore.Entity;
import com.ubermind.internal.jenkinsnotifier.util.DatastoreUtil;

public class JenkinsNotification {

	@Key(Json.JOB_NAME)
	private String jobName;

	@Key(Json.BUILD_RESULT)
	private BuildResult buildResult;

	private DateTime timestamp;

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

	public DateTime getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(DateTime timestamp) {
		this.timestamp = timestamp;
	}

	public JenkinsNotification(Entity entity) {
		this.jobName = (String) entity.getProperty(DsKey.JOB_NAME);
		buildResult = new BuildResult();
		buildResult.fullUrl = (String) entity.getProperty(DsKey.FULL_URL);
		buildResult.number = DatastoreUtil.getEntityPropertyInt(entity, DsKey.NUMBER, BuildResult.UNKNOWN_INT);
		buildResult.phase = (String) entity.getProperty(DsKey.PHASE);
		buildResult.status = BuildStatus.parseString((String) entity.getProperty(DsKey.STATUS));
		timestamp = new DateTime((Date) entity.getProperty(DsKey.TIMESTAMP));
	}

	public void populateProperties(Entity entity) {
		entity.setUnindexedProperty(DsKey.JOB_NAME, getJobName());
		entity.setUnindexedProperty(DsKey.FULL_URL, getFullUrl());
		entity.setProperty(DsKey.NUMBER, Integer.valueOf(getNumber()));
		entity.setUnindexedProperty(DsKey.PHASE, getPhase());
		entity.setProperty(DsKey.STATUS, getStatus().name());
		entity.setProperty(DsKey.TIMESTAMP, new Date(getTimestamp().getValue()));
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();

		builder.append(String.format("Job: %s\n", getJobName()));
		builder.append(String.format("Build number: %d\n", Integer.valueOf(getNumber())));
		builder.append(String.format("Status: %s\n", getStatus().name()));
		builder.append(String.format("Timestamp: %s\n", timestamp.toStringRfc3339()));
		builder.append(String.format("URL: %s", getFullUrl()));

		return builder.toString();
	}

	public static class BuildResult {

		public static final int UNKNOWN_INT = -1;

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

		public static BuildStatus parseString(String string) {
			try {
				return BuildStatus.valueOf(string);
			}
			catch (Exception e) {
				// not found
			}

			return null;
		}
	}

	public static interface DsKey {
		public static final String JOB_NAME = "job_name";
		public static final String FULL_URL = "full_url";
		public static final String NUMBER = "number";
		public static final String PHASE = "phase";
		public static final String STATUS = "status";
		public static final String TIMESTAMP = "timestamp";
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
