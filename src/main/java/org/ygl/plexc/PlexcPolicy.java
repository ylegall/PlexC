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


import org.apache.log4j.Logger;
import org.ygl.plexc.models.AuditRecord;
import org.ygl.plexc.models.User;

import alice.tuprolog.InvalidLibraryException;
import alice.tuprolog.InvalidTheoryException;
import alice.tuprolog.NoSolutionException;
import alice.tuprolog.SolveInfo;
import alice.tuprolog.Struct;
import alice.tuprolog.Term;
import alice.tuprolog.Theory;

import com.avaje.ebean.Ebean;
import com.google.common.base.Preconditions;


/**
 * @author ylegall
 * ylegall@gmail.com
 */
public class PlexcPolicy
{
	private static final Logger LOG = Logger.getLogger("plexc.kb.policy");

	private final User user;
	private PrologEngine engine;

	/**
	 *
	 * @param user
	 * @param terms
	 * @param theory
	 */
	public PlexcPolicy(User user) {
		Preconditions.checkNotNull(user, "Cannot create a PlexcPolicy for a null user");
		this.user = user;
		try {
			engine = new PrologEngine(user);
			engine.loadLibrary("alice.tuprolog.lib.BasicLibrary");
			engine.loadLibrary("alice.tuprolog.lib.IOLibrary");
			engine.loadLibrary("org.ygl.plexc.PlexcLibrary");
			engine.addTheory(new Theory(user.policy));
		} catch (InvalidLibraryException | InvalidTheoryException e) {
			e.printStackTrace();
			LOG.error(e.getMessage());
		}
	}

	/**
	 *
	 * @param issuer
	 * @return
	 */
	boolean canAccess(String issuer) throws Exception {
		//Term goal = new Struct("canAccess", Term.createTerm(user.email), Term.createTerm(issuer));
		Term goal = new Struct("canAccess", Term.createTerm(Utils.surround(issuer, '"')));
		SolveInfo info = engine.solve(goal);
		boolean success = info.isSuccess();

		// record the access in the auditlog:
		AuditRecord record = new AuditRecord(issuer, user.email, success);
		LOG.debug(record.toString());
		Ebean.save(record);
		return success;
	}

	/**
	 *
	 * @param goal
	 * @return
	 * @throws Exception
	 */
	Result query(Struct goal) throws NoSolutionException {
		LOG.debug("query(): " + goal.toString());
		SolveInfo info = engine.solve(goal);
		return new Result(info.isSuccess(), info.getSolution().toString());
	}

	/**
	 *
	 * @param requester
	 * @param predicate
	 * @return
	 * @throws Exception
	 */
	boolean canQuery(String requester, String predicate) throws Exception {
		Term goal = Term.createTerm(String.format("canQuery(%s,%s)",
				Utils.surround(requester, "\""),
				predicate));
		LOG.debug("canQuery(): " + goal.toString());
		SolveInfo info = engine.solve(goal);
		return info.isSuccess();
	}

	/**
	 *
	 * @return
	 */
	public User getUser() {
		return user;
	}

}