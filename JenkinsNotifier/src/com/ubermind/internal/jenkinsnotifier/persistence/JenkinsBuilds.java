package com.ubermind.internal.jenkinsnotifier.persistence;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.ubermind.internal.jenkinsnotifier.jenkins.JenkinsNotification;

public class JenkinsBuilds {

	public static Key insertBuild(JenkinsNotification buildInfo) {
		Key jobKey = JenkinsJobs.insertJob(buildInfo.getJobName());

		// get datastore
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

		// insert a new build under the job
		Entity buildEntity = new Entity(DsConst.KIND_BUILD, jobKey);
		buildInfo.populateProperties(buildEntity);
		datastore.put(buildEntity);

		return buildEntity.getKey();
	}

	private static interface DsConst {
		public static final String KIND_BUILD = "build";
	}
}
