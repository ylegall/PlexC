package org.ygl.plexc.admin;

import java.io.File;
import java.util.List;

import org.apache.log4j.Logger;
import org.ygl.plexc.PlexcException;
import org.ygl.plexc.PlexcService;
import org.ygl.plexc.models.AuditRecord;
import org.ygl.plexc.models.AuthState;
import org.ygl.plexc.models.User;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.annotation.Transactional;

/**
 *
 * @author ylegall
 *
 */
public class AdminTools {

	private final PlexcService service;
	private static final Logger LOG = Logger.getLogger(AdminTools.class);

	public AdminTools(PlexcService service) {
		this.service = service;
	}

	/**
	 *
	 * @param email
	 */
	public void addUser(String email) {
		try {
			User user = User.findByEmail(email);
			if (user != null) {
				throw new PlexcException("user already exists");
			}
			user = new User();
			user.setEmail(email);
			user.setPassword("pass");
			user.setSalt("salt");
			user.setPolicy("");
			User.save(user);
		} catch (PlexcException e) {
			LOG.warn(e);
		}
	}

	/**
	 *
	 */
	public void clearData() {
		Ebean.createSqlUpdate("truncate State cascade").execute();
		Ebean.createSqlUpdate("truncate AuditLog cascade").execute();
	}

	/**
	 *
	 * @param user
	 * @param policyPath
	 */
	public void updatePolicy(String user, File policyPath) {

	}

	/**
	 *
	 * @param user
	 * @param policy
	 */
	public void updatePolicy(String user, String policy) {

	}

	/**
	 *
	 * @param email
	 */
	public void removeUser(String email) {
		User user = User.findByEmail(email);
		if (user != null) {
			Ebean.delete(user);
		}
	}

	/**
	 * Clears the audit log history for the given user.
	 * @param email
	 */
	@Transactional
	public void clearAccessLogs(String email) {
		List<AuditRecord> records = Ebean.find(AuditRecord.class).where().eq("issuer", email).findList();
		Ebean.delete(records);
	}

	/**
	 * Clears the auth state for the given user.
	 * @param email
	 * @param key
	 */
	@Transactional
	public void clearState(String email) {
		List<AuthState> rows = Ebean.find(AuthState.class).where().eq("issuer", email).findList();
		Ebean.delete(rows);
	}

}
