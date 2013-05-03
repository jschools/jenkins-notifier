package com.ubermind.internal.jenkinsnotifier;

import java.util.Date;

import com.google.api.client.util.Key;
import com.google.appengine.api.datastore.Entity;

public class JenkinsBuild {

	@Key("duration")
	public long duration;

	@Key("fullDisplayName")
	public String fullDisplayName;

	@Key("number")
	public int number;

	@Key("result")
	public String result;

	@Key("timestamp")
	public long timestamp;

	@Key("url")
	public String url;

	public JenkinsBuild() {
		// default constructor
	}

	public JenkinsBuild(Entity entity) {
		duration = ((Long) entity.getProperty("duration")).longValue();
		fullDisplayName = (String) entity.getProperty("fullDisplayName");
		number = ((Long) entity.getProperty("number")).intValue();
		result = (String) entity.getProperty("result");
		timestamp = ((Date) entity.getProperty("timestamp")).getTime();
		url = (String) entity.getProperty("url");
	}

	public void populateProperties(Entity entity) {
		entity.setProperty("duration", Long.valueOf(duration));
		entity.setProperty("fullDisplayName", fullDisplayName);
		entity.setProperty("number", Integer.valueOf(number));
		entity.setProperty("result", result);
		entity.setProperty("timestamp", new Date(timestamp));
		entity.setProperty("url", url);
	}

}
