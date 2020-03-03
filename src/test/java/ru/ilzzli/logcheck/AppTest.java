package ru.ilzzli.logcheck;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class AppTest {

	private static final ByteArrayOutputStream baosTest = new ByteArrayOutputStream();
	private static final PrintStream originalOut = System.out;
	private static final List<String[]> badArguments = new ArrayList<>();

	@Before
	public void setUpData() {
		badArguments.add(new String[] { "-u", "99", "40" });
		badArguments.add(new String[] { "-u", "-t", "140" });
		badArguments.add(new String[] { "", "1", "-u", "0.1" });
		badArguments.add(new String[] { "" });
		badArguments.add(new String[] { "sd" });
		
		System.setOut(new PrintStream(baosTest));
	}
	
	@After
	public void restoreStreams() {
	    System.setOut(originalOut);
	}

	@Test
	public void testLogReaderBadArgs() {
		for (String[] args : badArguments) {
			try (
					ByteArrayInputStream is = new ByteArrayInputStream(baosTest.toByteArray());
					Scanner scanner = new Scanner(is);
			) {
				LogReader reader = new LogReader(args);
				assertNotNull(reader);
				while (scanner.hasNextLine()) {
					String line = scanner.nextLine();
					boolean result = line.contains("Argument Error.") || line.contains("Error parsing CLI");
					assertTrue(result);
					if (line == null || line.isEmpty()) {
						break;
					}
				}
			} catch (Exception e) {
				fail(String.format("Test error: %s", e.getMessage()));
			}
		}
	}

}
