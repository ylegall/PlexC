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

import java.util.Map;

/**
 *
 * @author ylegall
 */
public interface PlexcService {

	/**
	 * Determine if the requester is permitted to access the The result is
	 * logged to.
	 * @param requester
	 * @param target
	 * @return
	 */
	boolean canAccess(String requester, String target) throws Exception;

	/**
	 * Determine if the requester is permitted to access the The result is
	 * logged to.
	 * @param requester
	 * @param target
	 * @param options
	 * @return
	 */
	boolean canAccess(String requester, String target, Map<String, String> options)
			throws Exception;

	/**
	 *
	 * @param requester
	 * @param target
	 * @param predicate
	 * @return
	 * @throws Exception
	 */
	boolean canQuery(String requester, String target, String predicate) throws Exception;

	/**
	 *
	 * @param user
	 * @param policy
	 */
	void updatePolicy(String user, String policy);

	/**
	 *
	 * @param user
	 * @return
	 * @throws Exception
	 */
	String getPolicy(String user) throws Exception;
}