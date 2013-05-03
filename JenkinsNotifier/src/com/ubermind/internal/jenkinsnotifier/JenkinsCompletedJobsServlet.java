package com.ubermind.internal.jenkinsnotifier;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.JsonGenerator;
import com.google.api.client.json.jackson.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;

@SuppressWarnings("serial")
public class JenkinsCompletedJobsServlet extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.setCharacterEncoding("UTF-8");
		resp.setContentType(Constants.CONTENT_TYPE_JSON);

		Query query = new Query(Constants.KIND_COMPLETED_BUILD, getCompletedBuildsKey());
		query.addSort("fullDisplayName", SortDirection.ASCENDING);

		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		List<Entity> entities = datastore.prepare(query).asList(FetchOptions.Builder.withDefaults());

		List<JenkinsBuild> builds = new ArrayList<JenkinsBuild>(entities.size());
		for (Entity entity : entities) {
			JenkinsBuild build = new JenkinsBuild(entity);
			builds.add(build);
		}

		JsonFactory f = new JacksonFactory();
		JsonGenerator generator = f.createJsonGenerator(resp.getWriter());

		generator.serialize(builds);
		generator.flush();
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		JsonFactory f = new JacksonFactory();
		JenkinsBuild buildInfo = f.fromReader(req.getReader(), JenkinsBuild.class);

		DateTime date = new DateTime(buildInfo.timestamp, 0);

		String formattedDate = date.toStringRfc3339();

		resp.setContentType(Constants.CONTENT_TYPE_PLAINTEXT);
		PrintWriter writer = resp.getWriter();
		writer.printf("Build: %s\n", buildInfo.fullDisplayName);
		writer.printf("Build number: %d\n", Integer.valueOf(buildInfo.number));
		writer.printf("Duration: %.3f seconds\n", Float.valueOf(buildInfo.duration / 1000f));
		writer.printf("Status: %s\n", buildInfo.result);
		writer.printf("Timestamp: %s\n", formattedDate);
		writer.printf("URL: %s", buildInfo.url);

		Entity buildEntity = new Entity(Constants.KIND_COMPLETED_BUILD, getCompletedBuildsKey());
		buildInfo.populateProperties(buildEntity);

		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		datastore.put(buildEntity);
	}

	private static Key getCompletedBuildsKey() {
		return KeyFactory.createKey(Constants.KIND_COMPLETED_BUILD, Constants.NAME_COMPLETED_BUILDS);
	}

	private static interface Constants {
		public static final String CONTENT_TYPE_JSON = "application/json";
		public static final String CONTENT_TYPE_PLAINTEXT = "text/plain";

		public static final String KIND_COMPLETED_BUILD = "completed_build";
		public static final String NAME_COMPLETED_BUILDS = "completed_builds";
	}
}
