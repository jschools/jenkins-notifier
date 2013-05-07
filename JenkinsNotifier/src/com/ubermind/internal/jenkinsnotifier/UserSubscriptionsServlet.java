package com.ubermind.internal.jenkinsnotifier;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ubermind.internal.jenkinsnotifier.persistence.JenkinsJobs;
import com.ubermind.internal.jenkinsnotifier.persistence.UserSubscriptions;
import com.ubermind.internal.jenkinsnotifier.persistence.UserSubscriptions.SubscriptionLevel;
import com.ubermind.internal.jenkinsnotifier.util.AuthUtil;
import com.ubermind.internal.jenkinsnotifier.util.WebUtil;

@SuppressWarnings("serial")
public class UserSubscriptionsServlet extends HttpServlet {

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String userId = AuthUtil.getUserId(req);

		Map<String, SubscriptionLevel> subscriptionStates = new HashMap<String, SubscriptionLevel>();

		List<String> jobNames = JenkinsJobs.getAllJobs();
		for (String job : jobNames) {
			String paramValue = req.getParameter(job);
			SubscriptionLevel subscriptionLevel = SubscriptionLevel.parseString(paramValue);

			subscriptionStates.put(job, subscriptionLevel);
		}

		if (userId == null || userId.isEmpty()) {
			resp.sendError(401, "Unauthorized");
			return;
		}

		boolean success = UserSubscriptions.setUserSubscriptions(userId, subscriptionStates);
		if (success) {
			WebUtil.setFlash(req, "Subscriptions successfully updated");
		}
		else {
			WebUtil.setFlash(req, "Subscription update failed.");
		}

		resp.sendRedirect(WebUtil.buildUrl(req, "/jobs.jsp"));
	}

}
