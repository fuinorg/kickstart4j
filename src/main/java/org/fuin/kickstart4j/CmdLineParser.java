/**
 * Copyright (C) 2009 Future Invent Informationsmanagement GmbH. All rights
 * reserved. <http://www.fuin.org/>
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 3 of the License, or (at your option) any
 * later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library. If not, see <http://www.gnu.org/licenses/>.
 */
package org.fuin.kickstart4j;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Parses the command line and set the appropriate config object values.
 */
public final class CmdLineParser {

	/** System dependent line separator. */
	private static final String LINE_SEPARATOR = System
			.getProperty("line.separator");

	/** Known options. */
	private final List cmdLineOptions;

	/** Parsed options. */
	private final Map options;

	/** Messages for command line. */
	private final MessagesWrapper messages;

	/**
	 * Constructor with locale.
	 * 
	 * @param locale
	 *            Locale.
	 */
	public CmdLineParser(final Locale locale) {
		super();
		this.messages = new MessagesWrapper(locale);
		this.cmdLineOptions = createCommandLineOptions(messages);
		this.options = new HashMap();
	}

	/**
	 * Parse the command line and set the values in the target config object.
	 * 
	 * @param args
	 *            Command line arguments.
	 * 
	 * @throws CmdLineException
	 *             Illegal arguments or missing required arguments.
	 */
	public final void parse(final String[] args) throws CmdLineException {

		// Create a list of required options
		final List requiredKeys = createRequiredKeys();

		// Any arguments?
		if ((args == null) || (args.length == 0)) {
			checkRequiredKeys(requiredKeys);
			return;
		}

		// Read arguments
		int i = 0;
		while (i < args.length) {
			final String arg = args[i];
			if (!arg.startsWith("-")) {
				throw new CmdLineException(messages
						.getErrorOptionWithoutPrefix(arg));
			}
			i++;
			final String option = arg.substring(1);
			if (i >= args.length) {
				throw new CmdLineException(messages
						.getErrorValueMissingForOption(option));
			}
			final String value = args[i];
			i++;

			// Store option in map
			options.put(option, value);

			// Remove key from required values
			final int idx = requiredKeys.indexOf(option);
			if (idx > -1) {
				requiredKeys.remove(idx);
			}

		}

		checkRequiredKeys(requiredKeys);

	}

	/**
	 * Returns an option value by it's key.
	 * 
	 * @param key
	 *            Key to find.
	 * 
	 * @return Value or <code>null</code> if the key was not found.
	 */
	public String get(final String key) {
		return (String) options.get(key);
	}

	/**
	 * Removes an option key.
	 * 
	 * @param key
	 *            Option to remove.
	 * 
	 * @return Previously removed value.
	 */
	public String remove(final String key) {
		return (String) options.remove(key);
	}

	/**
	 * Copy the parsed values to the configuration.
	 * 
	 * @param config
	 *            Config to fill with parsed values.
	 */
	public void copyToConfig(final Config config) {
		final Iterator it = options.keySet().iterator();
		while (it.hasNext()) {
			final String key = (String) it.next();
			final String value = (String) options.get(key);
			if (key.equals("configFileUrl")) {
				config.setConfigFileUrl(value);
			}
			config.getCmdLineOptions().put(key, value);
		}
	}

	/**
	 * Create a list of all required keys.
	 * 
	 * @return LIst of <code>String</code> objects.
	 */
	private List createRequiredKeys() {
		final List requiredKeys = new ArrayList();
		for (int i = 0; i < cmdLineOptions.size(); i++) {
			final CmdLineOption option = (CmdLineOption) cmdLineOptions.get(i);
			if (option.isRequired()) {
				requiredKeys.add(option.getKey());
			}
		}
		return requiredKeys;
	}

	/**
	 * Check if the list of required keys is empty and if not throw an
	 * exception.
	 * 
	 * @param requiredKeys
	 *            List of (still) required keys.
	 * 
	 * @throws CmdLineException
	 *             Not all keys were found while parsing the command line.
	 */
	private void checkRequiredKeys(final List requiredKeys)
			throws CmdLineException {
		if (requiredKeys.size() > 0) {
			final StringBuffer sb = new StringBuffer(messages
					.getErrorMissingReuiredOptions());
			sb.append(" ");
			for (int i = 0; i < requiredKeys.size(); i++) {
				if (i > 0) {
					sb.append(", ");
				}
				sb.append("-");
				sb.append(requiredKeys.get(i));
			}
			throw new CmdLineException(sb.toString());
		}
	}

	/**
	 * Prints the command line usage.
	 * 
	 * @param ps
	 *            Stream to use.
	 */
	public final void printUsage(final PrintStream ps) {
		for (int i = 0; i < cmdLineOptions.size(); i++) {
			final CmdLineOption option = (CmdLineOption) cmdLineOptions.get(i);
			ps.println(option.getOptionText(messages));
		}
	}

	/**
	 * Prints the command line usage.
	 * 
	 * @param out
	 *            Stream to use.
	 */
	public final void printUsage(final OutputStream out) {
		for (int i = 0; i < cmdLineOptions.size(); i++) {
			final CmdLineOption option = (CmdLineOption) cmdLineOptions.get(i);
			final String str = option.getOptionText(messages) + LINE_SEPARATOR;
			try {
				out.write(str.getBytes());
			} catch (final IOException ex) {
				throw new RuntimeException(
						"Error printing usage on 'OutputStream'!", ex);
			}
		}
	}

	/**
	 * Creates a list with all known command line options.
	 * 
	 * @param msg
	 *            Error messages.
	 * 
	 * @return List with <code>CommandLineOption</code> objects.
	 */
	private static List createCommandLineOptions(final MessagesWrapper msg) {
		final List list = new ArrayList();
		list.add(new CmdLineOption("configFileUrl", "(URL)", msg
				.getOptionUsageConfigFileUrl(), true));
		return list;
	}

	/**
	 * {@inheritDoc}
	 */
	public final String toString() {
		final StringBuffer sb = new StringBuffer();
		final Iterator it = options.keySet().iterator();
		while (it.hasNext()) {
			final String key = (String) it.next();
			final String value = (String) options.get(key);
			if (sb.length() > 0) {
			    sb.append(", ");
			}
			sb.append(key);
			sb.append("='");
			sb.append(value);
            sb.append("'");
		}
		return sb.toString();
	}

	/**
	 * Test method.
	 * 
	 * @param args
	 *            Not used.
	 */
	public static void main(final String[] args) {

		final String[] arguments = new String[] { "-src",
				"http://www.fuin.org/test/", "-dest", "d:\\temp",
				"-idFilename", "myapp" };

		final CmdLineParser parser = new CmdLineParser(Locale.getDefault());
		try {
			parser.parse(arguments);
			System.out.println(parser);
		} catch (final CmdLineException ex) {
			ex.printStackTrace();
		}

	}

}
