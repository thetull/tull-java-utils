package net.tullco.tullutils.test_classes;

import static org.junit.Assert.*;

import org.junit.Test;

import net.tullco.tullutils.Pair;

public class PairTest {

	@Test
	public void testLeft() {
		Pair<String,String> pair = Pair.<String,String>of("Tull","Beverly");
		assertEquals(pair.left(),"Tull");
	}

	@Test
	public void testRight() {
		Pair<String,String> pair = Pair.<String,String>of("Tull","Beverly");
		assertEquals(pair.right(),"Beverly");
	}

	@Test
	public void testGetKey() {
		Pair<String,String> pair = Pair.<String,String>of("Tull","Beverly");
		assertEquals(pair.getKey(),"Tull");
	}

	@Test
	public void testGetValue() {
		Pair<String,String> pair = Pair.<String,String>of("Tull","Beverly");
		assertEquals(pair.getValue(),"Beverly");
	}

	@Test
	public void testOf() {
		Pair<String,String> pair = Pair.<String,String>of("Tull","Beverly");
		assertTrue(pair instanceof Pair);
		assertTrue(pair.left() instanceof String);
		assertTrue(pair.right() instanceof String);
	}

}
