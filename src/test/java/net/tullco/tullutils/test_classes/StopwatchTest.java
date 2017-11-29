package net.tullco.tullutils.test_classes;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import net.tullco.tullutils.Stopwatch;

public class StopwatchTest {

	private static final long NANO_MILLI_CONVERSION = 1000000L;
	private static final long TEST_TIME_PERIOD_MILLIS = 1000L;
	private static final long TEST_TIME_PERIOD_NANOS = TEST_TIME_PERIOD_MILLIS * NANO_MILLI_CONVERSION;
	private static final long MILLI_BUFFER = 2L;
	private static final long NANO_BUFFER = MILLI_BUFFER * NANO_MILLI_CONVERSION;
	
	private Stopwatch watch;
	
	@Before
	public void setUp() throws Exception {
		watch = new Stopwatch();
	}

	@Test
	public void testStart() {
		assertFalse(watch.isRunning());
		watch.start();
		assertTrue(watch.isRunning());
	}

	@Test
	public void testStop() {
		assertFalse(watch.isRunning());
		watch.start();
		assertTrue(watch.isRunning());
		watch.stop();
		assertFalse(watch.isRunning());
	}

	@Test
	public void testStopStart() {
		assertFalse(watch.isRunning());
		watch.stopStart();
		assertFalse(watch.isRunning());
		watch.start();
		assertTrue(watch.isRunning());
		watch.stopStart();
		assertTrue(watch.isRunning());
		assertEquals(1, watch.countIntervals());
		watch.stopStart();
		assertEquals(2, watch.countIntervals());
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
		assertTrue(Math.abs(watch.getLastNanos() - TEST_TIME_PERIOD_NANOS) < NANO_BUFFER);
		assertFalse(watch.isRunning());
		watch.clear();
		assertEquals(0, watch.countIntervals());
		assertEquals(0, watch.getLastNanos());
		watch.start();
		assertTrue(watch.isRunning());
		watch.clear();
		assertFalse(watch.isRunning());
	}

	@Test
	public void testGetLastNanos() {
		assertEquals(0, watch.getLastNanos());
		watch.start();
		try {
			Thread.sleep(TEST_TIME_PERIOD_MILLIS);
		} catch (InterruptedException e) {
			fail("Interrupted");
		}
		long runningNanos = watch.getLastNanos();
		watch.stop();
		long stoppedNanos = watch.getLastNanos();
		assertTrue(Math.abs(runningNanos - stoppedNanos) < NANO_BUFFER);
		assertTrue(Math.abs(stoppedNanos - TEST_TIME_PERIOD_NANOS) < NANO_BUFFER);
		assertTrue(Math.abs(runningNanos - TEST_TIME_PERIOD_NANOS) < NANO_BUFFER);
		watch.start();
		try {
			Thread.sleep(TEST_TIME_PERIOD_MILLIS);
		} catch (InterruptedException e) {
			fail("Interrupted");
		}
		runningNanos = watch.getLastNanos();
		watch.stop();
		stoppedNanos = watch.getLastNanos();
		assertTrue(Math.abs(runningNanos - stoppedNanos) < NANO_BUFFER);
		assertTrue(Math.abs(stoppedNanos - TEST_TIME_PERIOD_NANOS) < NANO_BUFFER);
		assertTrue(Math.abs(runningNanos - TEST_TIME_PERIOD_NANOS) < NANO_BUFFER);
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
		runningNanos = watch.getLastNanos();
		watch.stop();
		stoppedNanos = watch.getLastNanos();
		assertTrue(Math.abs(runningNanos - stoppedNanos) < NANO_BUFFER);
		assertTrue(Math.abs(stoppedNanos - TEST_TIME_PERIOD_NANOS) < NANO_BUFFER);
		assertTrue(Math.abs(runningNanos - TEST_TIME_PERIOD_NANOS) < NANO_BUFFER);
	}

	@Test
	public void testGetTotalNanos() {
		assertEquals(0, watch.getTotalNanos());
		watch.start();
		try {
			Thread.sleep(TEST_TIME_PERIOD_MILLIS);
		} catch (InterruptedException e) {
			fail("Interrupted");
		}
		long runningNanos = watch.getTotalNanos();
		watch.stop();
		long stoppedNanos = watch.getTotalNanos();
		assertTrue(Math.abs(runningNanos - stoppedNanos) < NANO_BUFFER);
		assertTrue(Math.abs(stoppedNanos - TEST_TIME_PERIOD_NANOS) < NANO_BUFFER);
		assertTrue(Math.abs(runningNanos - TEST_TIME_PERIOD_NANOS) < NANO_BUFFER);
		assertEquals(watch.getTotalNanos(), watch.getLastNanos());
		
		watch.start();
		try {
			Thread.sleep(TEST_TIME_PERIOD_MILLIS);
		} catch (InterruptedException e) {
			fail("Interrupted");
		}
		assertTrue(Math.abs(watch.getTotalNanos() - (2 * TEST_TIME_PERIOD_NANOS)) < NANO_BUFFER);
		watch.stopStart();
		assertTrue(Math.abs(watch.getTotalNanos() - (2 * TEST_TIME_PERIOD_NANOS)) < NANO_BUFFER);
		try {
			Thread.sleep(TEST_TIME_PERIOD_MILLIS);
		} catch (InterruptedException e) {
			fail("Interrupted");
		}
		assertTrue(Math.abs(watch.getTotalNanos() - (3 * TEST_TIME_PERIOD_NANOS)) < NANO_BUFFER);
		watch.stop();
		assertTrue(Math.abs(watch.getTotalNanos() - (3 * TEST_TIME_PERIOD_NANOS)) < NANO_BUFFER);
	}
}
