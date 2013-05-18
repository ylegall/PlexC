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

import static org.ygl.plexc.TimeUtils.getDay;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;
import org.apache.log4j.Logger;
import org.ygl.plexc.models.AuditRecord;
import org.ygl.plexc.models.AuthState;

import alice.tuprolog.Int;
import alice.tuprolog.Struct;
import alice.tuprolog.Term;

import com.avaje.ebean.Ebean;
import com.google.common.base.Preconditions;

/**
 *
 * This class extends the Prolog environment with new PlexC functors and
 * predicates.
 */
public class PlexcLibrary extends alice.tuprolog.Library {

	private static final Logger LOG = Logger.getLogger(PlexcLibrary.class);
	private static final long serialVersionUID = -6489231941215309754L;

	/**
	 *
	 */
	public PlexcLibrary() {
		super();
	}

	private String getCurrentUser() {
		return ((PrologEngine) getEngine()).getUser().email;
	}

	/**
	 *
	 * @param duration
	 * @return
	 */
	public Term accessCount_1(Term duration) {
		Timestamp timestamp = TimeUtils.getTimestamp(duration.toString());
		int count = AuditRecord.accessCount(getCurrentUser(), timestamp);
		return new Int(count);
	}

	/**
	 *
	 * @param duration
	 * @return
	 */
	public Term accessCount_2(Term issuer, Term duration) {
		Timestamp timestamp = TimeUtils.getTimestamp(duration.toString());
		int count = AuditRecord.accessCount(issuer.toString(), getCurrentUser(), timestamp);
		return new Int(count);
	}

	/**
	 * Gets the day of the week
	 * @return
	 */
	public Term day_0() {
		return Term.createTerm(getDay());
	}

	/**
	 * Determines if the current day of the week is a weekday
	 * @return
	 */
	public boolean weekday_0() {
		String day = getDay();
		if (day.equals("saturday") || day.equals("sunday")) {
			return false;
		}
		return true;
	}

	/**
	 *
	 * @param message
	 * @return
	 */
	public boolean debug_1(Term term) {
		//LOG.info(message);
		System.out.println(term.getTerm().toString());
		return true;
	}

	/**
	 * Gets the current unix timestamp.
	 * @return
	 */
	public Term now() {
		return new alice.tuprolog.Long(System.currentTimeMillis());
	}

	/**
	 * Predicate to determine if the hour of the day is between the two
	 * specified values.
	 *
	 * @param start the start hour, 0-23
	 * @param end the end hour, 0-23
	 * @return
	 */
	public boolean hourBetween_2(Int start, Int end) {
		Preconditions.checkElementIndex(start.intValue(), 24);
		Preconditions.checkElementIndex(end.intValue(), 24);
		int time = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
		return (time >= start.intValue() && time <= end.intValue());
	}

	/**
	 *
	 * @param localUser
	 * @param remoteUser
	 * @param predicate
	 * @return
	 */
	public boolean remote_2(Term remoteUser, Struct predicate) {
		LOG.debug("remote(): " + remoteUser + ", " + predicate);
		PlexcEngine engine = PlexcEngine.INSTANCE;
		String localUser = getCurrentUser();

		// see if the current user can query the remote user:
		try {
			if (!engine.canQuery(localUser, remoteUser.toString(), predicate.toString())) {
				return false;
			}
		} catch (Exception e) {
			LOG.warn(e.getMessage());
			e.printStackTrace();
			return false;
		}

		try {
			return engine.remoteQuery(remoteUser.toString(), predicate);
		} catch (Exception e) {
			LOG.warn(e.getMessage());
			e.printStackTrace();
			return false;
		}
	}

	/**
	 *
	 * @param owner
	 * @param target
	 * @param args
	 * @return
	 */
	public boolean insertState_2(Term target, Term args) {
		PrologEngine e = (PrologEngine) this.engine;
		String user = e.getUser().email;
		AuthState state = new AuthState(user, target.toString(), args.toString());
		Ebean.save(state);
		return true;
	}

	/**
	 * get the state stored
	 * @param target
	 * @return
	 */
	public Term getState_1(Term targetUser) {
		String target = targetUser.toString();
		if (StringUtils.isEmpty(target)) {
			return Term.createTerm("");
		}
		String issuer = getCurrentUser();
		AuthState state = Ebean.find(AuthState.class)
				.where()
				.eq("issuer", issuer)
				.eq("target", target)
				.findUnique();
		return Term.createTerm(state.args);
	}

	/**
	 * remove the state associated with the specified user.
	 * @param owner
	 * @param target
	 * @param args
	 * @return
	 */
	public boolean removeState_1(Term targetUser) {
		String target = targetUser.toString();
		if (StringUtils.isEmpty(target)) {
			return true;
		}
		String issuer = getCurrentUser();
		Ebean.beginTransaction();
		List<AuthState> states = Ebean.find(AuthState.class)
				.where()
				.eq("issuer", issuer)
				.eq("target", target)
				.findList();
		Ebean.delete(states);
		Ebean.endTransaction();
		return true;
	}

	/**
	 * Test if the specified state is associated with the specified target user.
	 * @param owner
	 * @param target
	 * @param args
	 * @return
	 */
	public boolean testState_2(Term targetUser, Term args) {
		String target = targetUser.toString();
		if (StringUtils.isEmpty(target)) {
			return false;
		}
		String issuer = getCurrentUser();
		int rows = Ebean.find(AuthState.class)
				.where()
				.eq("issuer", issuer)
				.eq("target", target)
				.eq("args", args.toString())
				.findRowCount();
		return rows > 0;
	}

	/**
	 * Sends an email or text notification upon successful access.
	 * @param requester
	 * @return
	 */
	public boolean notify_1(Term requester) {
		final String user = getCurrentUser();
		final String host = "localhost";
		final String subject = "PlexC access notification";
		final String text = String.format(
				"%s, %s has been granted access to your location.",
				user,
				requester.getTerm().toString());

		Email email = new SimpleEmail();
		email.setSubject(subject);
		email.setHostName(host);
		email.setSmtpPort(465);

		try {
			email.setFrom("noreply@plexc.com");
			email.setMsg(text);
			email.addTo(user);
			email.send();
		} catch (EmailException e) {
			LOG.warn(e.getMessage());
			return false;
		}
		return true;
	}
}