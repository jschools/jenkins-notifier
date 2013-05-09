package com.ubermind.internal.jenkinsnotifier.notify;

import java.io.IOException;
import java.text.DateFormat;
import java.util.Arrays;
import java.util.Date;

import com.google.api.client.extensions.appengine.http.UrlFetchTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.mirror.Mirror;
import com.google.api.services.mirror.model.MenuItem;
import com.google.api.services.mirror.model.NotificationConfig;
import com.google.api.services.mirror.model.TimelineItem;
import com.ubermind.internal.jenkinsnotifier.jenkins.JenkinsNotification;
import com.ubermind.internal.jenkinsnotifier.util.AuthUtil;

public class GlassNotifier implements UserBuildNotifier {
	@Override
	public boolean notifyUser(String userId, JenkinsNotification buildInfo) throws IOException {
		String oauthToken = AuthUtil.getOauthToken(userId);

		Mirror mirror = new Mirror.Builder(new UrlFetchTransport(), new JacksonFactory(), null)
			.setApplicationName("Jenkins Notifier")
			.build();

		TimelineItem content = new TimelineItem();

		/*
		 * <article>
		 * <section>
		 * <p class="text-large red">
		 * FAILURE</p>
		 * <p class="text-small">
		 * Target.Android.SocSave.Debug
		 * </p>
		 * <p class="text-small">Build #3</p> <p class="text-small">2013-05-09 13:46:40</p>
		 * </section>
		 * </article>
		 */

		String htmlTemplate = "<article>"
							+ "<section>"
							+ "<p class=\"text-large %s\">%s</p>"		// 1: color, 2: status
							+ "<p class=\"text-small\">%s</p>"			// 3: job name
							+ "<p class=\"text-small\">%s</p>"			// 4: date
							+ "</section>"
							+ "<footer>Build #%d</footer>"				// 5: build (int)
							+ "</article>";
		String speakableTemplate = "%s." // 1: job name
								 + " build number %d." // 2: build (int)
								 + " status: %s"; // 3: status

		String color = getColorString(buildInfo);
		String status = buildInfo.getStatus().name();
		String jobName = buildInfo.getJobName();
		Integer buildNum = Integer.valueOf(buildInfo.getNumber());
		String date = DateFormat.getDateTimeInstance().format(new Date(buildInfo.getTimestamp().getValue()));

		String html = String.format(htmlTemplate, color, status, jobName, date, buildNum);
		String read = String.format(speakableTemplate, jobName, buildNum, status);

		content.setBundleId(jobName);
		content.setHtml(html);
		content.setSpeakableText(read);
		content.setNotification(new NotificationConfig().setLevel("DEFAULT"));
		content.setMenuItems(Arrays.asList(
				new MenuItem().setAction("DELETE"),
				new MenuItem().setAction("TOGGLE_PINNED"),
				new MenuItem().setAction("READ_ALOUD")));

		TimelineItem result = mirror.timeline().insert(content).setOauthToken(oauthToken).execute();

		return result != null;
	}

	private static String getColorString(JenkinsNotification buildInfo) {
		switch (buildInfo.getStatus()) {
		case FAILURE:
			return "red";
		case UNSTABLE:
			return "yellow";
		case SUCCESS:
			return "green";
		default:
			return "";
		}
	}
}