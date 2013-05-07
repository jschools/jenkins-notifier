<%@page import="com.google.api.client.json.jackson2.JacksonFactory"%>
<%@page import="com.google.api.client.extensions.appengine.http.UrlFetchTransport"%>
<%@page import="com.google.api.services.oauth2.model.Userinfo"%>
<%@page import="com.google.api.services.oauth2.Oauth2"%>
<%@page import="com.google.api.services.oauth2.Oauth2.Builder"%>
<%@page import="com.ubermind.internal.jenkinsnotifier.persistence.UserSubscriptions"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%@ page import="com.ubermind.internal.jenkinsnotifier.util.AuthUtil" %>
<%@ page import="com.ubermind.internal.jenkinsnotifier.util.WebUtil" %>
<%@ page import="com.ubermind.internal.jenkinsnotifier.persistence.JenkinsJobs" %>
<%@ page import="com.ubermind.internal.jenkinsnotifier.persistence.UserSubscriptions.SubscriptionLevel" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>JenkinsNotifier | Jenkins Jobs</title>
</head>
<body>
<h1>Jenkins Jobs:</h1>

<form method="post" action="userSubscriptions">
<table>
	<tr>
	<th>Job name:</th>
	<th colspan="4">Subscription level:</th>
	</tr>
<%
	List<String> jobNames = JenkinsJobs.getAllJobs();
	Map<String, SubscriptionLevel> userSubscriptions = UserSubscriptions.getSubscriptionsForUser(AuthUtil.getUserId(request));
	
	final String radioSelected = " checked=\"yes\"";
	for (String job : jobNames) {		
		SubscriptionLevel subscriptionLevel = userSubscriptions.get(job);
		if (subscriptionLevel == null) {
	subscriptionLevel = SubscriptionLevel.None;
		}
		
		getServletContext().setAttribute("jobName", job);
%>
		<tr>
		<td>${jobName}</td>
		<%
		for (int i = 0; i < SubscriptionLevel.getCount(); i++) {
			String selectedLevelString = subscriptionLevel.getLevelInt() == i ? radioSelected : "";
			getServletContext().setAttribute("selectedLevelString", selectedLevelString);
			getServletContext().setAttribute("levelName", SubscriptionLevel.getLevel(i).getTitle());
			getServletContext().setAttribute("levelNumber", Integer.valueOf(i));
			%>
			<td><input name="${jobName}" type="radio" ${selectedLevelString} value="${levelNumber}" />${levelName}</td>
			<%
		}
		%>
		</tr>
		<%
	}
%>
	</table>
	<br />
	<input type="submit" value="Update subscriptions" />
</form>
<%
	String flash = WebUtil.getClearFlash(request);
	getServletContext().setAttribute("flashMessage", flash);
%>
<p>${flashMessage}</p>
<br />
<hr />
<%
	Oauth2 oauthService = new Oauth2.Builder(new UrlFetchTransport(), new JacksonFactory(), null).setApplicationName("Jenkins Notifier").build();
	Userinfo userInfo = oauthService.userinfo().get().setFields("email").setOauthToken(AuthUtil.getCredential(request).getAccessToken()).execute();
	getServletContext().setAttribute("email", userInfo.getEmail());
%>
<p>Logged in as ${email}</p>
<a href="/signout">Log out</a>

</body>
</html>