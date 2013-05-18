package org.ygl.plexc;

import static org.testng.Assert.*;
import org.testng.annotations.Test;


public class UtilsTest {

	/**
	 *
	 */
	@Test
	public void testSurround() {
		String str = "surround";
		assertEquals("'surround'", Utils.surround(str, '\''));
	}

	/**
	 *
	 */
	@Test
	public void testUnquote() {
		String str = "'surround'";
		assertEquals("surround", Utils.unquote(str));
	}
}
