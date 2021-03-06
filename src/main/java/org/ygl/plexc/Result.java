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

/**
 *
 * @author ylegall
 *
 */
public class Result {

	private final boolean success;
	private final String message;

	/**
	 *
	 * @param success
	 */
	public Result(boolean success) {
		this(success, "");
	}

	/**
	 *
	 * @param success
	 * @param message
	 */
	public Result(boolean success, String message) {
		super();
		this.success = success;
		this.message = message;
	}

	/**
	 * Get the success status of this Response.
	 * @return
	 */
	public boolean isSuccess() {
		return success;
	}

	/**
	 * Get the result string of this Response.
	 * @return
	 */
	public String getMessage() {
		return message;
	}

}
