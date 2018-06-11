package net.tullco.tullutils.test_classes;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import net.tullco.tullutils.Stopwatch;

public class StopwatchTest {

	private static final long TEST_TIME_PERIOD_MILLIS = 100L;
	private static final long MILLI_BUFFER = 2L;
	
	private Stopwatch watch;
	
	@Before
	public void setUp() throws Exception {
		watch = new Stopwatch();
	}

	@Test
	public void testStart() {
		assertFalse(watch.isRunning());
		assertEquals(0,watch.countIntervals());
		watch.start();
		assertTrue(watch.isRunning());
		assertEquals(1,watch.countIntervals());
	}

	@Test
	public void testStop() {
		assertFalse(watch.isRunning());
		assertEquals(0,watch.countIntervals());
		watch.start();
		assertTrue(watch.isRunning());
		assertEquals(1,watch.countIntervals());
		watch.stop();
		assertEquals(1,watch.countIntervals());
		assertFalse(watch.isRunning());
	}

	@Test
	public void testStopStart() {
		assertFalse(watch.isRunning());
		assertEquals(0, watch.countIntervals());
		watch.stopStart();
		assertEquals(1, watch.countIntervals());
		assertTrue(watch.isRunning());
		watch.start();
		assertTrue(watch.isRunning());
		watch.stopStart();
		assertTrue(watch.isRunning());
		assertEquals(2, watch.countIntervals());
		watch.stopStart();
		assertEquals(3, watch.countIntervals());
	}

	@Test
	public void testClear() {
		assertFalse(watch.isRunning());
		watch.start();
		assertTrue(watch.isRunning());
		try {
			Thread.sleep(TEST_TIME_PERIOD_MILLIS);
		} catch (InterruptedException e) {
			fail("Interrupted");
		}
		watch.stop();
		assertEquals(1, watch.countIntervals());
		assertTrue(Math.abs(watch.getLastMillis() - TEST_TIME_PERIOD_MILLIS) < MILLI_BUFFER);
		watch.stopStart();
		assertEquals(2, watch.countIntervals());
		watch.stop();
		assertEquals(2, watch.countIntervals());
		assertFalse(watch.isRunning());
		watch.clear();
		assertEquals(0, watch.countIntervals());
		assertEquals(0, watch.getTotalMillis());
		watch.start();
		assertTrue(watch.isRunning());
		assertEquals(1, watch.countIntervals());
		watch.clear();
		assertFalse(watch.isRunning());
		assertEquals(0, watch.getTotalMillis());
	}

	@Test
	public void testGetLastMillis() {
		assertEquals(0, watch.getLastMillis());
		watch.start();
		try {
			Thread.sleep(TEST_TIME_PERIOD_MILLIS);
		} catch (InterruptedException e) {
			fail("Interrupted");
		}
		long runningMillis = watch.getLastMillis();
		watch.stop();
		long stoppedMillis = watch.getLastMillis();
		assertTrue(Math.abs(runningMillis - stoppedMillis) < MILLI_BUFFER);
		assertTrue(Math.abs(stoppedMillis - TEST_TIME_PERIOD_MILLIS) < MILLI_BUFFER);
		assertTrue(Math.abs(runningMillis - TEST_TIME_PERIOD_MILLIS) < MILLI_BUFFER);
		watch.start();
		try {
			Thread.sleep(TEST_TIME_PERIOD_MILLIS);
		} catch (InterruptedException e) {
			fail("Interrupted");
		}
		runningMillis = watch.getLastMillis();
		watch.stop();
		stoppedMillis = watch.getLastMillis();
		assertTrue(Math.abs(runningMillis - stoppedMillis) < MILLI_BUFFER);
		assertTrue(Math.abs(stoppedMillis - TEST_TIME_PERIOD_MILLIS) < MILLI_BUFFER);
		assertTrue(Math.abs(runningMillis - TEST_TIME_PERIOD_MILLIS) < MILLI_BUFFER);
		watch.start();
		try {
			Thread.sleep(TEST_TIME_PERIOD_MILLIS);
		} catch (InterruptedException e) {
			fail("Interrupted");
		}
		watch.stopStart();
		try {
			Thread.sleep(TEST_TIME_PERIOD_MILLIS);
		} catch (InterruptedException e) {
			fail("Interrupted");
		}
		runningMillis = watch.getLastMillis();
		watch.stop();
		stoppedMillis = watch.getLastMillis();
		assertTrue(Math.abs(runningMillis - stoppedMillis) < MILLI_BUFFER);
		assertTrue(Math.abs(stoppedMillis - TEST_TIME_PERIOD_MILLIS) < MILLI_BUFFER);
		assertTrue(Math.abs(runningMillis - TEST_TIME_PERIOD_MILLIS) < MILLI_BUFFER);
	}

	@Test
	public void testGetTotalMillis() {
		assertEquals(0, watch.getTotalMillis());
		watch.start();
		try {
			Thread.sleep(TEST_TIME_PERIOD_MILLIS);
		} catch (InterruptedException e) {
			fail("Interrupted");
		}
		long runningNanos = watch.getTotalMillis();
		watch.stop();
		long stoppedNanos = watch.getTotalMillis();
		assertTrue(Math.abs(runningNanos - stoppedNanos) < MILLI_BUFFER);
		assertTrue(Math.abs(stoppedNanos - TEST_TIME_PERIOD_MILLIS) < MILLI_BUFFER);
		assertTrue(Math.abs(runningNanos - TEST_TIME_PERIOD_MILLIS) < MILLI_BUFFER);
		assertEquals(watch.getTotalMillis(), watch.getLastMillis());
		
		watch.start();
		try {
			Thread.sleep(TEST_TIME_PERIOD_MILLIS);
		} catch (InterruptedException e) {
			fail("Interrupted");
		}
		assertTrue(Math.abs(watch.getTotalMillis() - (2 * TEST_TIME_PERIOD_MILLIS)) < MILLI_BUFFER);
		watch.stopStart();
		assertTrue(Math.abs(watch.getTotalMillis() - (2 * TEST_TIME_PERIOD_MILLIS)) < MILLI_BUFFER);
		try {
			Thread.sleep(TEST_TIME_PERIOD_MILLIS);
		} catch (InterruptedException e) {
			fail("Interrupted");
		}
		assertTrue(Math.abs(watch.getTotalMillis() - (3 * TEST_TIME_PERIOD_MILLIS)) < MILLI_BUFFER);
		watch.stop();
		assertTrue(Math.abs(watch.getTotalMillis() - (3 * TEST_TIME_PERIOD_MILLIS)) < MILLI_BUFFER);
	}
}
