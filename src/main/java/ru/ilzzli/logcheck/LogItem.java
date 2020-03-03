package ru.ilzzli.logcheck;

import java.time.LocalDateTime;

public class LogItem {
	
	private Integer code;
	private double delay;
	private LocalDateTime date;

	public LogItem() {}
	
	public LogItem(int code, double delay, LocalDateTime date) {
		this.code = code;
		this.delay = delay;
		this.date = date;
	}
	
	public LocalDateTime getDate() {
		return date;
	}
	public int getCode() {
		return code;
	}
	public double getDelay() {
		return delay;
	}
	
	public boolean isGood(double delayTreshold) {
		return code < 500 && delay < delayTreshold;
	}
	
}
