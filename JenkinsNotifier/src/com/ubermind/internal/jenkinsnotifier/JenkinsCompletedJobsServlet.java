package com.ubermind.internal.jenkinsnotifier;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.ubermind.internal.jenkinsnotifier.jenkins.JenkinsNotification;

@SuppressWarnings("serial")
public class JenkinsCompletedJobsServlet extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.setCharacterEncoding("UTF-8");
		resp.setContentType(DsConst.CONTENT_TYPE_JSON);

		// Query query = new Query(DsConst.KIND_BUILD, getCompletedBuildsKey());
		// query.addSort("fullDisplayName", SortDirection.ASCENDING);
		//
		// DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		// List<Entity> entities = datastore.prepare(query).asList(FetchOptions.Builder.withDefaults());
		//
		// List<JenkinsBuild> builds = new ArrayList<JenkinsBuild>(entities.size());
		// for (Entity entity : entities) {
		// JenkinsBuild build = new JenkinsBuild(entity);
		// builds.add(build);
		// }
		//
		// JsonFactory f = new JacksonFactory();
		// JsonGenerator generator = f.createJsonGenerator(resp.getWriter());
		//
		// generator.serialize(builds);
		// generator.flush();
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		JsonFactory f = new JacksonFactory();
		JenkinsNotification buildInfo = f.fromReader(req.getReader(), JenkinsNotification.class);

		DateTime date = new DateTime(new Date().getTime(), 0);
		String formattedDate = date.toStringRfc3339();

		resp.setContentType(DsConst.CONTENT_TYPE_PLAINTEXT);
		PrintWriter writer = resp.getWriter();
		writer.printf("Job: %s\n", buildInfo.getJobName());
		writer.printf("Build number: %d\n", Integer.valueOf(buildInfo.getNumber()));
		writer.printf("Status: %s\n", buildInfo.getStatus().name());
		writer.printf("Timestamp: %s\n", formattedDate);
		writer.printf("URL: %s", buildInfo.getFullUrl());

		// get datastore
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

		// get the previous job
		Filter jobFilter = new FilterPredicate(DsConst.PROP_JOB_NAME, FilterOperator.EQUAL, buildInfo.getJobName());
		Query jobQuery = new Query(DsConst.KIND_JOB).setKeysOnly().setFilter(jobFilter);
		Entity job = datastore.prepare(jobQuery).asSingleEntity();

		// insert a new one if necessary
		if (job == null) {
			job = new Entity(getJobKey(buildInfo.getJobName()));
			job.setProperty(DsConst.PROP_JOB_NAME, buildInfo.getJobName());

			datastore.put(job);
		}

		// insert a new build under the job
		Entity buildEntity = new Entity(DsConst.KIND_BUILD, job.getKey());
		buildInfo.populateProperties(buildEntity);
		datastore.put(buildEntity);
	}

	private static Key getAllJobsKey() {
		return KeyFactory.createKey(DsConst.KIND_JOB_LIST, DsConst.NAME_ALL_JOBS);
	}

	private static Key getJobKey(String jobName) {
		return KeyFactory.createKey(getAllJobsKey(), DsConst.KIND_JOB, jobName);
	}

	private static Key getBuildKey(String jobName, int buildNumber) {
		return KeyFactory.createKey(DsConst.KIND_BUILD, String.format("%s.%04d", jobName, Integer.valueOf(buildNumber)));
	}

	private static interface DsConst {
		public static final String CONTENT_TYPE_JSON = "application/json";
		public static final String CONTENT_TYPE_PLAINTEXT = "text/plain";

		public static final String KIND_JOB_LIST = "job_list";
		public static final String NAME_ALL_JOBS = "all_jobs";

		public static final String KIND_JOB = "job";

		public static final String KIND_BUILD = "build";

		public static final String PROP_JOB_NAME = "name";
	}
}
