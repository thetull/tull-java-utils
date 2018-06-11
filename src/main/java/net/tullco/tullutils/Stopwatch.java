package net.tullco.tullutils;

import java.util.ArrayList;
import java.util.List;

public class Stopwatch {
	
	private boolean running;
	
	private long startTime;
	private long stopTime;
	
	private List<Pair<Long, Long>> intervalList;
	
	/**
	 * Create a new stopwatch with default settings.
	 */
	public Stopwatch(){
		startTime = Long.MIN_VALUE;
		stopTime = Long.MIN_VALUE;
		running = false;
		intervalList = new ArrayList<Pair<Long, Long>>();
	}
	
	/**
	 * Start the stopwatch. This must be called before any of the get time methods to anything.
	 * Calling start() on a started stopwatch does nothing.
	 */
	public void start(){
		if(running)
			return;
		if(startTime != Long.MIN_VALUE){
			intervalList.add(Pair.<Long,Long>of(startTime, stopTime));
		}
		startTime = System.currentTimeMillis();
		stopTime = Long.MIN_VALUE;
		running = true;
	}
	
	/**
	 * Stops the stopwatch. This means that time is no longer accruing on the watch.
	 * Calling stop() on a stopwatch that isn't started does nothing.
	 */
	public void stop(){
		if(!running)
			return;
		stopTime = System.currentTimeMillis();
		running = false;
	}
	
	/**
	 * Stops and then starts the stopwatch.
	 * This is the same as calling stop and then start, except for the fact that the interval window will be completely continuous
	 * , with the stop time being identical to the start time of the next interval.
	 */
	public void stopStart(){
		if (!running){
			start();
			return;
		}
		long now = System.currentTimeMillis();
		intervalList.add(Pair.<Long,Long>of(startTime, now));
		startTime = now;
		stopTime = Long.MIN_VALUE;
	}
	
	/**
	 * Return the number of intervals currently logged in the watch, including the currently active time period.
	 * @return The number of intervals the watch currently has stored, including the current one if the watch is running
	 */
	public long countIntervals(){
		long intervals = intervalList.size();
		if (running || (!running && stopTime > Long.MIN_VALUE))
			return intervals + 1L;
		else
			return intervals;
	}
	
	/**
	 * Reset the stopwatch. All stored intervals will be cleared, and the watch will be forced into a non-running state.
	 */
	public void clear(){
		startTime = Long.MIN_VALUE;
		stopTime = Long.MIN_VALUE;
		intervalList.clear();
		running = false;
	}
	
	/**
	 * Check if the watch is currently running.
	 * @return True if the watch is running, false if it's not
	 */
	public boolean isRunning(){
		return running;
	}
	
	/**
	 * If the watch is running, get the length of the currently running interval in millis.
	 * If the watch is not running, get the length of the last interval in millis.
	 * @return The interval length in millis.
	 */
	public long getLastMillis(){
		if(startTime == Long.MIN_VALUE)
			return 0L;
		else if(stopTime == Long.MIN_VALUE)
			return System.currentTimeMillis() - startTime;
		else
			return stopTime - startTime;
	}
	
	/**
	 * If the watch is running, get the length of the currently running interval in seconds.
	 * If the watch is not running, get the length of the last interval in seconds.
	 * @return The interval length in seconds.
	 */
	public long getLastSeconds(){
		return getLastMillis() / 1000L;
	}
	
	/**
	 * If the watch is running, get the length of the currently running interval in minutes.
	 * If the watch is not running, get the length of the last interval in minutes.
	 * @return The interval length in minutes.
	 */
	public long getLastMinutes(){
		return getLastMinutes() / 60L;
	}
	
	/**
	 * If the watch is running, get the length of the currently running interval in hours.
	 * If the watch is not running, get the length of the last interval in hours.
	 * @return The interval length in hours.
	 */
	public long getLastHours(){
		return getLastMinutes() / 60L;
	}
	
	/**
	 * If the watch is running, get the length of the currently running interval in days.
	 * If the watch is not running, get the length of the last interval in days.
	 * @return The interval length in days.
	 */
	public long getLastDays(){
		return getLastHours() / 24L;
	}

	/**
	 * Get the total number of whole millis the watch has been running for, including the current interval if it is running.
	 * @return The millis the watch has been running for.
	 */
	public long getTotalMillis(){
		long totalMillis = 0;
		for(Pair<Long, Long> p: intervalList){
			totalMillis += (p.right() - p.left());
		}
		totalMillis += getLastMillis();
		return totalMillis;
	}

	/**
	 * Get the total number of whole seconds the watch has been running for, including the current interval if it is running.
	 * @return The seconds the watch has been running for.
	 */
	public long getTotalSeconds(){
		return getTotalMillis() / 1000L;
	}

	/**
	 * Get the total number of whole minutes the watch has been running for, including the current interval if it is running.
	 * @return The minutes the watch has been running for.
	 */
	public long getTotalMinutes(){
		return getTotalSeconds() / 60L;
	}

	/**
	 * Get the total number of whole hours the watch has been running for, including the current interval if it is running.
	 * @return The hours the watch has been running for.
	 */
	public long getTotalHours(){
		return getTotalMinutes() / 60L;
	}

	/**
	 * Get the total number of whole days the watch has been running for including the current interval if it is running.
	 * @return The days the watch has been running for.
	 */
	public long getTotalDays(){
		return getTotalHours() / 24L;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((intervalList == null) ? 0 : intervalList.hashCode());
		result = prime * result + (running ? 1231 : 1237);
		result = prime * result + (int) (startTime ^ (startTime >>> 32));
		result = prime * result + (int) (stopTime ^ (stopTime >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Stopwatch other = (Stopwatch) obj;
		if (intervalList == null) {
			if (other.intervalList != null)
				return false;
		} else if (!intervalList.equals(other.intervalList))
			return false;
		if (running != other.running)
			return false;
		if (startTime != other.startTime)
			return false;
		if (stopTime != other.stopTime)
			return false;
		return true;
	}
}
