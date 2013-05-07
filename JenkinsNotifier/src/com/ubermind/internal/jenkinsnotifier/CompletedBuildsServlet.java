package com.ubermind.internal.jenkinsnotifier;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.logging.Logger;

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
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		Logger.getLogger(getServletName()).info("CompletedBuildsServlet.doPost()");

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
