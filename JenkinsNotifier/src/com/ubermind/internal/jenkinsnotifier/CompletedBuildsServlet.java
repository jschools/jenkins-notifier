package com.ubermind.internal.jenkinsnotifier;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;

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
		DateTime requestTimestamp = new DateTime(new Date().getTime(), 0);

//		// uncomment these and the line below to dump POST body to stderr
//		StringBuffer out = new StringBuffer(64);
//		StringBufferOutputStream os = new StringBufferOutputStream(out);
//		IOUtils.copy(req.getInputStream(), os);
//		String body = out.toString();
//		System.err.println(body);

		JsonFactory f = new JacksonFactory();
		JenkinsNotification buildInfo = f.fromReader(req.getReader(), JenkinsNotification.class);
//		JenkinsNotification buildInfo = f.fromString(body, JenkinsNotification.class);
		buildInfo.setTimestamp(requestTimestamp);

		resp.setContentType("text/plain");

		if (buildInfo.isValid()) {
			PrintWriter writer = resp.getWriter();
			writer.println(buildInfo);

			// call the controller
			BuildsController.onBuildInfoPosted(buildInfo);
		}
		else {
			resp.getWriter().println("invalid build JSON");
		}
	}


}
