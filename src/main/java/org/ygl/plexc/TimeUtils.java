/*
 * PlexC: A policy Language for Exposure Control
 * Copyright (C) 2013  Yann G. Le Gall
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.ygl.plexc;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.log4j.Logger;

/**
 *
 * @author ylegall ylegall@gmail.com
 */
public class TimeUtils {

	public static final int MSEC_S = 1000;
	public static final int MSEC_M = MSEC_S * 60;
	public static final int MSEC_H = MSEC_M * 60;
	public static final int MSEC_D = MSEC_H * 24;
	public static final int MSEC_W = MSEC_D * 7;

	private static final Logger LOG = Logger.getLogger(TimeUtils.class);

	private TimeUtils() {
	}

	/**
	 *
	 * @param timeStamp
	 * @return
	 */
	public static String formatDate(long timeStamp) {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd kk:mm:ss");
		Date date = new Date(timeStamp);
		return dateFormat.format(date);
	}

	/**
	 *
	 * @param duration
	 * @return
	 */
	public static long getTimeBefore(String duration) {
		duration = duration.trim().toLowerCase().substring(1, duration.length() - 1);
		// String[] tokens = duration.split("[^\\.\\w]+");
		String[] tokens = duration.split("\\s+");
		try {
			double count = Double.parseDouble(tokens[0]);
			return getTimeBefore(count, tokens[1]);
		} catch (NumberFormatException nfe) {
			LOG.warn(nfe.getMessage());
			return 0;
		}
	}

	/**
	 *
	 * @param duration
	 * @return
	 */
	public static Timestamp getTimestamp(String duration) {
		long time = getTimeBefore(duration);
		// time /= 1000;
		return new Timestamp(time);
	}

	/**
	 *
	 * @param count
	 * @param unit
	 * @return
	 */
	public static long getTimeBefore(double count, String unit) {
		long msecs = 0;

		switch (unit) {
		case "min":
		case "minute":
			msecs = MSEC_M;
			break;
		case "hr":
		case "hour":
			msecs = MSEC_H;
			break;
		case "day":
			msecs = MSEC_D;
			break;
		case "week":
			msecs = MSEC_W;
			break;
		}
		msecs = (long) (msecs * count);
		return new Date(new Date().getTime() - msecs).getTime();
	}

	/**
	 * Gets the current day of the week
	 * @return the current day of the week as a string
	 */
	public static String getDay() {
		int i = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
		switch (i) {
		case Calendar.MONDAY:
			return "monday";
		case Calendar.TUESDAY:
			return "tuesday";
		case Calendar.WEDNESDAY:
			return "wednesday";
		case Calendar.THURSDAY:
			return "thursday";
		case Calendar.FRIDAY:
			return "friday";
		case Calendar.SATURDAY:
			return "saturday";
		case Calendar.SUNDAY:
			return "sunday";
			default:
				return "";
		}
	}

}