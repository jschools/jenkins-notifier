package com.ubermind.internal.jenkinsnotifier.persistence;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;


public class JenkinsJobs {

	public static Key insertJob(String jobName) {
		// get datastore
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

		// get the previous job
		Entity job;
		try {
			job = datastore.get(getJobKey(jobName));
		}
		catch (EntityNotFoundException e) {
			job = null;
		}

		// insert a new one if necessary
		if (job == null) {
			job = new Entity(getJobKey(jobName));
			job.setProperty(DsConst.PROP_JOB_NAME, jobName);

			datastore.put(job);
		}

		return job.getKey();
	}

	static Key getJobKey(String jobName) {
		return KeyFactory.createKey(getAllJobsKey(), DsConst.KIND_JOB, jobName);
	}

	static Key getAllJobsKey() {
		return KeyFactory.createKey(DsConst.KIND_JOB_LIST, DsConst.NAME_ALL_JOBS);
	}

	private static interface DsConst {
		public static final String KIND_JOB_LIST = "job_list";
		public static final String NAME_ALL_JOBS = "all_jobs";

		public static final String KIND_JOB = "job";

		public static final String PROP_JOB_NAME = "name";
	}
}
