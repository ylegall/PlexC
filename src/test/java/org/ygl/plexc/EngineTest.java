package org.ygl.plexc;


import static org.testng.Assert.*;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.ygl.plexc.admin.AdminTools;

/**
 * Tests the primary language features of PlexC.
 *
 * @author ylegall
 */
public class EngineTest {

	private PlexcService engine = PlexcEngine.INSTANCE;
	private AdminTools tools;

	private static final String USER1 = "user1";
	private static final String USER2 = "user2";
	private static final String USER3 = "user3";

	/**
	 * Creates test users.
	 */
	@BeforeClass
	public void setup() {
		engine = PlexcEngine.INSTANCE;
		tools = new AdminTools(engine);
		tools.addUser(USER1);
		tools.addUser(USER2);
	}

	/**
	 * Tests the update of a policy.
	 */
	@Test
	public void testUpdatePolicy() {
		final String policy1 = "canAccess(X) :- friend(X).";
		engine.updatePolicy(USER1, policy1);
		String policy2 = "";
		try {
			policy2 = engine.getPolicy(USER1);
		} catch (Exception e) {
			e.printStackTrace();
		}
		assertEquals(policy1, policy2);
	}

	/**
	 * Tests role based access control.
	 */
	@Test
	public void testRole() throws Exception {
		final String policy1 = String.format("friend(%s).\nparent(%s).\ncanAccess(X) :- friend(X).", USER2, USER3);
		engine.updatePolicy(USER1, policy1);
		assertTrue(engine.canAccess(USER2, USER1));
		assertFalse(engine.canAccess(USER3, USER1));
	}

	/**
	 * Tests aggregate and rate limiting policies.
	 */
	@Test
	public void testAggregate() throws Exception {
		final String policy = "canAccess(X) :- accessCount(\"1 day\") < 2.";
		engine.updatePolicy(USER1, policy);
		tools.clearAccessLogs(USER1);
		assertTrue(engine.canAccess(USER2, USER1));
		assertTrue(engine.canAccess(USER2, USER1));
		assertFalse(engine.canAccess(USER2, USER1));
		assertFalse(engine.canAccess(USER2, USER1));
	}

	/**
	 * Tests inserting and retrieving authorization state.
	 */
	@Test
	public void testStateModification() throws Exception {
		final String policy = "canAccess(X) :- not testState(X, \"seen\"), insertState(X,\"seen\").";
		engine.updatePolicy(USER1, policy);
		tools.clearState(USER1);
		assertTrue(engine.canAccess(USER2, USER1));
		assertFalse(engine.canAccess(USER2, USER1));
	}

}
