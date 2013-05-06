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
import com.ubermind.internal.jenkinsnotifier.controller.BuildsController;
import com.ubermind.internal.jenkinsnotifier.jenkins.JenkinsNotification;

@SuppressWarnings("serial")
public class CompletedBuildsServlet extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.setCharacterEncoding("UTF-8");
		resp.setContentType("application/json");

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
		DateTime requestTimestamp = new DateTime(new Date().getTime(), 0);

		JsonFactory f = new JacksonFactory();
		JenkinsNotification buildInfo = f.fromReader(req.getReader(), JenkinsNotification.class);
		buildInfo.setTimestamp(requestTimestamp);

		resp.setContentType("text/plain");
		PrintWriter writer = resp.getWriter();
		writer.println(buildInfo);

		// call the controller
		BuildsController.onBuildInfoPosted(buildInfo);
	}


}
