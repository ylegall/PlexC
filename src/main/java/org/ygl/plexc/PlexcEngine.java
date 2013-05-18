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
import java.util.concurrent.ExecutionException;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.apache.log4j.Logger;
import org.ygl.plexc.models.User;

import alice.tuprolog.NoSolutionException;
import alice.tuprolog.Struct;

import com.avaje.ebean.annotation.Transactional;
import com.google.common.base.Preconditions;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

/**
 * PlexC Engine
 * Central component of the PlexC system.
 * It manages loading and unloading user policies and coordinates
 * invocation of the embedded TuProlog engines.
 * @author ylegall ylegall@gmail.com
 */
public enum PlexcEngine implements PlexcService
{
	INSTANCE;

	private static final Logger LOG = Logger.getLogger("plexc.engine");

	private LoadingCache<String, PlexcPolicy> policyCache;

	// create the in-memory cache
	private PlexcEngine() {
		this.policyCache = CacheBuilder.newBuilder()
			.maximumSize(128)
			.build(new CacheLoader<String, PlexcPolicy>() {
				@Override
				public PlexcPolicy load(String email) throws Exception {
					email = Utils.unquote(email);
					User user = User.findByEmail(email);
					return new PlexcPolicy(user);
				}
			});
	}

	@Override
	public boolean canQuery(String requester, String target, String predicate) throws Exception {
		LOG.debug("canQuery: " + requester + " " + target);
		Preconditions.checkNotNull(requester);
		Preconditions.checkNotNull(target);
		PlexcPolicy policy = policyCache.get(target);
		return policy.canQuery(requester, predicate);
	}

	/**
	 * execute a remote query to another user's policy.
	 * @param remoteUser
	 * @param predicate
	 * @return
	 * @throws Exception
	 */
	boolean remoteQuery(String remoteUser, Struct predicate) {
		LOG.debug("remoteQuery: " + remoteUser + " " + predicate);
		Preconditions.checkNotNull(remoteUser);
		Preconditions.checkNotNull(predicate);
		PlexcPolicy policy = null;
		try {
			policy = policyCache.get(remoteUser);
		} catch (ExecutionException e) {
			LOG.warn(e.getMessage());
		}
		try {
			return policy.query(predicate).isSuccess();
		} catch (NoSolutionException e) {
			return false;
		}
	}

	@Override
	public boolean canAccess(String issuer, String target) throws Exception {
		return canAccess(issuer,target, null);
	}

	@Override
	public boolean canAccess(String issuer, String target, Map<String, String> options) throws Exception {
		LOG.debug("canAccess: " + issuer + " " + target);
		Preconditions.checkNotNull(issuer);
		Preconditions.checkNotNull(target);
		PlexcPolicy policy = policyCache.get(target);
		return policy.canAccess(issuer);
	}

	@Transactional
	@Override
	public void updatePolicy(String email, String policy) {
		LOG.debug("updatePolicy(): " + email);
		User user = User.findByEmail(email);
		if (user != null) {
			String newPolicy = StringUtils.trimToEmpty(policy);
			user.setPolicy(newPolicy);
			LOG.debug("SAVING POLICY: " + newPolicy);
			User.save(user);
			policyCache.put(email, new PlexcPolicy(user));
		}
	}

	@Override
	public String getPolicy(String email) throws Exception {
		Validate.notBlank(email, "user not found");
		email = StringUtils.trimToEmpty(email);
		PlexcPolicy policy = policyCache.get(email);
		Validate.notNull(policy, "policy not found");
		return policy.getUser().getPolicy();
	}
}
