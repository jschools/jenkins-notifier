package com.ubermind.internal.jenkinsnotifier.util;

import com.google.appengine.api.datastore.Entity;

public final class DatastoreUtil {

	private DatastoreUtil() {
		// do not instantiate
	}

	public static int getEntityPropertyInt(Entity entity, String property, int defaultVal) {
		Object value = entity.getProperty(property);
		if (value instanceof Number) {
			return ((Number) value).intValue();
		}

		return defaultVal;
	}

}
