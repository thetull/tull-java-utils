package net.tullco.tullutils.test_classes;

import static org.junit.Assert.*;

import org.junit.Test;

import net.tullco.tullutils.StringUtils;

public class StringUtilsTest {

	@Test
	public void testLeft() {
		String testString = "Beverly";
		assertEquals("Beverly",StringUtils.left(testString, 99999));
		assertEquals("",StringUtils.left(testString, -8));
		assertEquals("B",StringUtils.left(testString, 1));
		assertEquals("Bever",StringUtils.left(testString, 5));
		assertEquals("B",StringUtils.left(testString, -6));
		assertEquals("Bever",StringUtils.left(testString, -2));
		assertEquals("",StringUtils.left(testString, 0));
		assertNull(StringUtils.left(null, 1));
	}

	@Test
	public void testRight() {
		String testString = "Beverly";
		assertEquals("Beverly",StringUtils.right(testString, 99999));
		assertEquals("",StringUtils.right(testString, -8));
		assertEquals("y",StringUtils.right(testString, 1));
		assertEquals("verly",StringUtils.right(testString, 5));
		assertEquals("y",StringUtils.right(testString, -6));
		assertEquals("verly",StringUtils.right(testString, -2));
		assertEquals("",StringUtils.right(testString, 0));
		assertNull(StringUtils.right(null, 1));
	}

	@Test
	public void testAssureStartsWith() {
		String testString = "Night";
		assertEquals("Late Night", StringUtils.assureStartsWith(testString, "Late "));
		assertEquals("Night", StringUtils.assureStartsWith(testString, "Ni"));
	}

	@Test
	public void testAssureEndsWith() {
		String testString = "Day";
		assertEquals("Daytime", StringUtils.assureEndsWith(testString, "time"));
		assertEquals("Day", StringUtils.assureEndsWith(testString, "ay"));
	}

	@Test
	public void testAssureNotStartsWith() {
		String testString = "Late Night";
		assertEquals("Late Night", StringUtils.assureNotStartsWith(testString, "Night"));
		assertEquals("Night", StringUtils.assureNotStartsWith(testString, "Late "));
		testString = "LateLate Night";
		assertEquals(" Night", StringUtils.assureNotStartsWith(testString, "Late"));
	}

	@Test
	public void testAssureNotEndsWith() {
		String testString = "DayTime";
		assertEquals("Day", StringUtils.assureNotEndsWith(testString, "Time"));
		assertEquals("DayTime", StringUtils.assureNotEndsWith(testString, "Day"));
		testString = "DayTimeTime";
		assertEquals("Day", StringUtils.assureNotEndsWith(testString, "Time"));
	}

	@Test
	public void testTrim(){
		String testString = ",oops,";
		assertEquals("oops", StringUtils.trim(testString, ","));
		assertEquals(",oops,", StringUtils.trim(testString, "."));
		testString = ",,oops,,,";
		assertEquals("oops", StringUtils.trim(testString, ","));
	}
	
	@Test
	public void testLeftPad() {
		String testString = "Ryuji";
		assertEquals("Ryuji",StringUtils.leftPad(testString, '!', 0));
		assertEquals("Ryuji",StringUtils.leftPad(testString, '!', 5));
		assertEquals("Ryuji",StringUtils.leftPad(testString, '!', 4));
		assertEquals("!Ryuji",StringUtils.leftPad(testString, '!', 6));
		assertEquals("!!!Ryuji",StringUtils.leftPad(testString, '!', 8));
	}

	@Test
	public void testRightPad() {
		String testString = "Ryuji";
		assertEquals("Ryuji",StringUtils.rightPad(testString, '!', 0));
		assertEquals("Ryuji",StringUtils.rightPad(testString, '!', 5));
		assertEquals("Ryuji",StringUtils.rightPad(testString, '!', 4));
		assertEquals("Ryuji!",StringUtils.rightPad(testString, '!', 6));
		assertEquals("Ryuji!!!",StringUtils.rightPad(testString, '!', 8));
	}

}
