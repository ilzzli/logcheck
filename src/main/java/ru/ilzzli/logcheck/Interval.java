package ru.ilzzli.logcheck;

import java.time.LocalDateTime;

public class Interval {

	private LocalDateTime firstGoodTime;
	private LocalDateTime lastTime;
	private int totalCount;
	private int denyCount;
	
	public LocalDateTime getFirstGoodTime() {
		return firstGoodTime;
	}

	public void setFirstGoodTime(LocalDateTime firstGoodTime) {
		this.firstGoodTime = firstGoodTime;
	}

	public LocalDateTime getLastTime() {
		return lastTime;
	}

	public void setLastTime(LocalDateTime lastTime) {
		this.lastTime = lastTime;
	}

	public int getDenyCount() {
		return denyCount;
	}
	
	public int getTotalCount() {
		return totalCount;
	}

	public void addDenyCount(int count) {
		this.denyCount += count;
		addTotalCount(count);
	}
	
	public void addTotalCount(int count) {
		this.totalCount += count > 0 ? count : 0;
	}
	
	public void merge(Interval interval) {
		if (interval != null) {
			this.totalCount += interval.getTotalCount();
			this.lastTime = interval.getFirstGoodTime();
		}
	}
	
	public double getGoodPercent() {
		return getGoodPercent(0);
	}
	
	public double getGoodPercent(int extraCount) {
		int total = totalCount + (extraCount >= 0 ? extraCount : 0);
		return 100 - ((double) denyCount / total) * 100;
	}
	
}
