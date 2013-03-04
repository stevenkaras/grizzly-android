package org.sek.grizzly.model;

import java.util.Date;

public class GrizzlyUtils {

	public static String getHumanInterval(Date start, Date end) {
		if (end == null) {
			return "ongoing";
		}
		long t = end.getTime() - start.getTime();
		if (t < 1000) {
			return t + "ms";
		}
		t /= 1000;
		if (t < 60) {
			return t + " seconds";
		}
		t /= 60;
		if (t < 60) {
			return t + " minutes";
		}
		t /= 60;
		if (t < 24) {
			return t + " hours";
		}
		t /= 24;
		if (t < 7) {
			return t + " days";
		}
		long weeks = t / 7;
		if (t < 365) {
			return weeks + " weeks";
		}
		t /= 365;
		return t + " years";
	}
}
