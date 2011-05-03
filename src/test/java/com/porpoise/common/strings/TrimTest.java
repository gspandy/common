package com.porpoise.common.strings;

import org.junit.Assert;
import org.junit.Test;

/**
 * tests for the {@link Trim} utility class
 */
public class TrimTest {

	private final static String	TEXT	= "1234567890";

	/**
	 * test we can trim the middle characters
	 */
	@Test
	public void testTrimMiddle() {
		Assert.assertEquals(TEXT, Trim.middle(TEXT, TEXT.length(), "..."));
		Assert.assertEquals(TEXT, Trim.middle(TEXT, TEXT.length() + 1, "..."));
		Assert.assertEquals("123...890", Trim.middle(TEXT, TEXT.length() - 1, "..."));
		Assert.assertEquals("1...0", Trim.middle(TEXT, 5, "..."));
		Assert.assertEquals("12-90", Trim.middle(TEXT, 5, "-"));
		Assert.assertEquals("12390", Trim.middle(TEXT, 5, ""));
	}

}
