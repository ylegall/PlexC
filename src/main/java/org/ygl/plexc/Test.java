package org.ygl.plexc;


/**
 *
 * @author ylegall
 *
 */
public class Test {

	private static PlexcEngine engine;

	public static void main(String[] args) throws Exception
	{
		engine = PlexcEngine.INSTANCE;

		final String alice = "alice@plexc.com";
		final String bob = "bob@plexc.com";

		//engine.updatePolicy(bob, "friend(charlie).\ncanQuery(\"alice@plexc.com\", friend(charlie)).");
		//engine.updatePolicy(alice, "canAccess(X) :- remote(\"bob@plexc.com\", friend(X)).");

		getPolicy(alice);
		getPolicy(bob);

//		addUser(alice);
//		addUser(bob);
		//engine.updatePolicy(alice, "canAccess(X) :- friend(X).\nfriend(\"bob@plexc.com\").");
//		String policy = engine.getPolicy(bob);
//		System.out.println("bob's policy: " + policy);

//		boolean success = engine.canAccess(bob, alice, null);
//		boolean success = engine.canAccess("charlie", alice, null);
//		System.out.println(success);
	}

	public static void getPolicy(String email) {
		try {
			String policy = engine.getPolicy(email);
			System.out.println("policy for " + email + ":\n");
			System.out.println(policy);
		} catch (Exception e) {
			System.err.println(e);
		}
	}
}
