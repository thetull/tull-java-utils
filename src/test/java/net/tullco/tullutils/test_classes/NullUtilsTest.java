package net.tullco.tullutils.test_classes;

import static org.junit.Assert.*;

import org.junit.Test;

import net.tullco.tullutils.NullUtils;

public class NullUtilsTest {

	@Test
	public void testCoalesce() {
		String nullString = null;
		String textString = "I'm not null";
		assertNotNull(NullUtils.coalesce(nullString,textString));
		assertEquals(textString,NullUtils.coalesce(nullString,textString));
	}

	@Test
	public void testNullif() {
		String nullified = "null me";
		String normal = "don't null me";
		String nullifiable = "null me";
		assertNull(NullUtils.nullif(nullifiable,nullified));
		assertEquals(normal,NullUtils.nullif(normal,nullified));
	}

	@Test
	public void testNullToEmpty() {
		String nullString = null;
		String emptyString = "";
		String textString = "I'm not null";
		assertEquals(emptyString,NullUtils.nullToEmpty(nullString));
		assertEquals(emptyString,NullUtils.nullToEmpty(emptyString));
		assertEquals(textString,NullUtils.nullToEmpty(textString));
	}

	@Test
	public void testEmptyToNull() {
		String nullString = null;
		String emptyString = "";
		String textString = "I'm not null";
		assertNull(NullUtils.emptyToNull(emptyString));
		assertNull(NullUtils.emptyToNull(nullString));
		assertEquals(textString,NullUtils.emptyToNull(textString));
	}

}
