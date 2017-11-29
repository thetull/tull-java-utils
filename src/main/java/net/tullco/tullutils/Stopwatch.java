package net.tullco.tullutils;

import java.util.ArrayList;
import java.util.List;

public class Stopwatch {
	
	private boolean running;
	
	private long startTime;
	private long stopTime;
	
	private List<Pair<Long, Long>> intervalList;
	
	public Stopwatch(){
		startTime = Long.MIN_VALUE;
		stopTime = Long.MIN_VALUE;
		running = false;
		intervalList = new ArrayList<Pair<Long, Long>>();
	}
	
	public void start(){
		if(running)
			return;
		if(startTime != Long.MIN_VALUE){
			intervalList.add(Pair.<Long,Long>of(startTime, stopTime));
		}
		startTime = System.nanoTime();
		stopTime = Long.MIN_VALUE;
		running = true;
	}
	
	public void stop(){
		if(!running)
			return;
		stopTime = System.nanoTime();
		running = false;
	}
	
	public void stopStart(){
		if (!running)
			return;
		long now = System.nanoTime();
		intervalList.add(Pair.<Long,Long>of(startTime, now));
		startTime = now;
		stopTime = Long.MIN_VALUE;
	}
	public long countIntervals(){
		long intervals = intervalList.size();
		if (running || stopTime == Long.MIN_VALUE)
			return intervals;
		else
			return intervals + 1L;
	}
	
	public void clear(){
		startTime = Long.MIN_VALUE;
		stopTime = Long.MIN_VALUE;
		intervalList.clear();
		running = false;
	}
	
	public boolean isRunning(){
		return running;
	}
	
	public long getLastNanos(){
		if(startTime == Long.MIN_VALUE)
			return 0L;
		else if(stopTime == Long.MIN_VALUE)
			return System.nanoTime() - startTime;
		else
			return stopTime - startTime;
	}
	public long getLastMillis(){
		return getLastNanos() / 1000000L;
	}
	public long getLastSeconds(){
		return getLastMillis() / 1000L;
	}
	public long getLastMinutes(){
		return getLastMinutes() / 60L;
	}
	public long getLastHours(){
		return getLastMinutes() / 60L;
	}
	public long getLastDays(){
		return getLastHours() / 24L;
	}
	
	public long getTotalNanos(){
		long totalNanos = 0;
		for(Pair<Long, Long> p: intervalList){
			totalNanos += (p.right() - p.left());
		}
		totalNanos += getLastNanos();
		return totalNanos;
	}
	public long getTotalMillis(){
		return getTotalNanos() / 1000000L;
	}
	public long getTotalSeconds(){
		return getTotalMillis() / 1000L;
	}
	public long getTotalMinutes(){
		return getTotalSeconds() / 60L;
	}
	public long getTotalHours(){
		return getTotalMinutes() / 60L;
	}
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
