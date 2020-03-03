package ru.ilzzli.logcheck;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class IntervalProcessor {	

	private double failurePercentThreshold;
	private int delayThreshold;
	
	private Interval badInterval;
	private Interval goodInterval;
	
	private DateTimeFormatter outputTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
	private String outputFormat = "%-10s%-12s %4.2f%%%10d(%d)\n";
	private String resultFormat = "Log items count bad(total):  %10s(%s)\n\n";

	private int countBad = 0;
	private int countTotal = 0;
	
	public IntervalProcessor(double failurePercentThreshold, int delayThreshold) {
		this.failurePercentThreshold = failurePercentThreshold;
		this.delayThreshold = delayThreshold;
		this.goodInterval = new Interval();
	}
	
	public void registerLogItem(LogItem logItem) {
		recalculate(logItem);
	}
	
	public void showFinalResult() {
		if (badInterval != null) {
			badInterval.merge(goodInterval);
		}
		outputBadResult();
		System.out.format(resultFormat, countBad, countTotal);
		resetIntervals();
	}
	
	private void recalculate(LogItem logItem) {
		if (logItem.isGood(delayThreshold)) {
			if (goodInterval == null) {
				goodInterval = new Interval();
				goodInterval.setFirstGoodTime(logItem.getDate());
			}
			goodInterval.addTotalCount(1);
			if (!checkIsNeighbor()) {
				outputBadResult();
				resetIntervals();
			}
		} else {
			if (badInterval == null) {
				badInterval = new Interval();
				badInterval.setFirstGoodTime(getFirstGoodOrCurrentTime(logItem));
			}
			badInterval.setLastTime(logItem.getDate());
			badInterval.merge(goodInterval);
			badInterval.addDenyCount(1);
			goodInterval = null;
			countBad++;
		}
		countTotal++;
	}
	
	private boolean checkIsNeighbor() {
		boolean isNeighbor = true;
		if (goodInterval != null && goodInterval.getTotalCount() > 0) {
			isNeighbor = getDistanceGoodPercent(goodInterval.getTotalCount()) < failurePercentThreshold;
		}
		return isNeighbor;
	}
	
	private double getDistanceGoodPercent(int count) {
		return 100 - ((double) 1 / count) * 100;
	}
	
	private LocalDateTime getFirstGoodOrCurrentTime(LogItem logItem) {
		LocalDateTime firsGoodTime;
		if (goodInterval != null && goodInterval.getFirstGoodTime() != null) {
			firsGoodTime = goodInterval.getFirstGoodTime();
		} else {
			firsGoodTime = logItem.getDate();
		}
		return firsGoodTime;
	}
	
	private void outputBadResult() {
		if (badInterval != null) {
			System.out.format(
				outputFormat,
				badInterval.getFirstGoodTime().format(outputTimeFormatter),
				badInterval.getLastTime().format(outputTimeFormatter),
				badInterval.getGoodPercent(),
				badInterval.getDenyCount(),
				badInterval.getTotalCount()
			);
		}
	}
	
	private void resetIntervals() {
		badInterval = null;
		goodInterval = null;
	}
	
}
