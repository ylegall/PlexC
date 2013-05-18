package org.ygl.plexc;

import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author ylegall
 *
 */
public class Utils {

	private Utils() {}

	/**
	 * Surround a string with the specified character(s).
	 * @param str
	 * @param affix
	 * @return
	 */
	public static <T> String surround(String str, T affix) {
		return affix + str + affix;
	}

	/**
	 * remove any quotes (single or double) and return the result.
	 * @param email
	 * @return
	 */
	public static String unquote(String email) {
		email = StringUtils.trimToEmpty(email);
		int start = 0;
		int end = 0;
		if (email.startsWith("\"") || email.startsWith("'")) {
			start = 1;
		}
		if (email.endsWith("\"") || email.endsWith("'")) {
			end = 1;
		}
		return email.substring(start, email.length() - end);
	}
}
