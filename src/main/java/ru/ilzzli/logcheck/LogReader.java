package ru.ilzzli.logcheck;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class LogReader {
	
	private static final Logger logger = Logger.getLogger(LogReader.class.getName());
	
	private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy:HH:mm:ss xx");
	private Pattern logItemPattern = Pattern.compile("^.+\\[(?<date>.+?)\\]\\s\".+\"\\s(?<code>\\d{3})\\s\\d+\\s(?<delay>\\d+\\.\\d+?)\\s.+");

	private IntervalProcessor intervalProcessor;
	
	public LogReader(String[] args) {
    	initProcessor(parseArgs(args));
	}
	
	public void read() {
		if (intervalProcessor != null) {
			try (
					InputStreamReader is = new InputStreamReader(System.in);
					BufferedReader reader = new BufferedReader(is);
			) {
				System.out.print("\nProcessing started.. \n\n");
				reader.lines().forEach(line -> parseLine(line));
				intervalProcessor.showFinalResult();
			} catch (Exception e) {
				System.out.println(String.format("Reading error: %s", e.getMessage()));
			}
		} else {
			System.out.println("IntervalProcessor not initialized, aborted");
		}
	}
	
	private void parseLine(String line) {
		Matcher matcher = logItemPattern.matcher(line);
		if (matcher.matches()) {
			LogItem logItem = new LogItem(
					Integer.valueOf(matcher.group("code")),
					Double.valueOf(matcher.group("delay")),
					LocalDateTime.parse(matcher.group("date"), formatter));
			intervalProcessor.registerLogItem(logItem);
		}
	}
	
	private String[] parseArgs(String[] args) {
		Options cliOptions = new Options();
		cliOptions.addOption("u", "u", true, "Failure rate threshold");
		cliOptions.addOption("t", "t", true, "Max delay value");
		CommandLineParser parser = new DefaultParser();
		try {
			CommandLine cmd = parser.parse(cliOptions, args);
			if (cmd != null) {
				String[] values = new String[] {cmd.getOptionValue("u"), cmd.getOptionValue("t")};
				if (checkArgs(values))
					return values;
			}
		} catch (ParseException e) {
			System.out.println(String.format("Error parsing CLI options: %s", e.getMessage()));
		}
		return null;
	}
	
	private boolean checkArgs(String[] values) {
		if (values[0] == null || values[0].isEmpty() || !values[0].matches("^\\d{1,2}(\\.\\d)?$")) {
			System.out.println("Argument Error. Invalid -u argument value.");
			values[0] = null;
		}
		if (values[1] == null || values[1].isEmpty() || !values[1].matches("^\\d+$")) {
			System.out.println("Argument Error. Invalid -t argument value.");
			values[1] = null;
		}
		return values[0] != null && values[1] != null;
	}
	
	private void initProcessor(String[] argValues) {
		if (argValues != null)
			intervalProcessor = new IntervalProcessor(Double.valueOf(argValues[0]), Integer.valueOf(argValues[1]));
	}
}
