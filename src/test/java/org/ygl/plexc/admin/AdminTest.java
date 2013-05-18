package org.ygl.plexc.admin;

import java.util.List;

import junit.framework.Assert;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.ygl.plexc.PlexcEngine;
import org.ygl.plexc.models.AuditRecord;
import org.ygl.plexc.models.AuthState;
import org.ygl.plexc.models.User;

import com.avaje.ebean.Ebean;

/**
 *
 * @author ylegall
 *
 */
public class AdminTest {

	private static final String USER1 = "user1";

	private AdminTools tools;

	@BeforeClass
	public void setup() {
		tools = new AdminTools(PlexcEngine.INSTANCE);
	}

	/**
	 * Test removing a user from the System.
	 */
	@Test
	public void testClearData() {
		tools.clearData();
		List<AuthState> states = Ebean.find(AuthState.class).findList();
		Assert.assertTrue(states.isEmpty());
		List<AuditRecord> audits = Ebean.find(AuditRecord.class).findList();
		Assert.assertTrue(audits.isEmpty());
	}

	/**
	 * Test adding a user to the System.
	 */
	@Test
	public void testAddUser() {
		tools.addUser(USER1);
		User user = User.findByEmail(USER1);
		Assert.assertNotNull(user);
	}

	/**
	 * Test removing a user from the System.
	 */
	@Test
	public void testRemoveUser() {
		tools.addUser(USER1);
		tools.removeUser(USER1);
		User user = User.findByEmail(USER1);
		Assert.assertNull(user);
	}

}
