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

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;

import org.fuin.utils4j.Utils4J;

/**
 * Encapsulates access to the "message[_xx][_XX].properties" files.
 */
public final class MessagesWrapper {

	private static final String DEFAULT_MESSAGES = "messages";

	private final Properties props;

	/**
	 * Constructor with locale.
	 * 
	 * @param locale
	 *            Locale to use.
	 */
	MessagesWrapper(final Locale locale) {
		super();
		final ResourceBundle res = ResourceBundle.getBundle(Utils4J
				.getPackagePath(this.getClass())
				+ "/" + DEFAULT_MESSAGES, locale);
		final Properties defaultProperties = new Properties();
		final Enumeration enu = res.getKeys();
		while (enu.hasMoreElements()) {
			final String key = (String) enu.nextElement();
			final String value = res.getString(key);
			defaultProperties.put(key, value);
		}
		this.props = defaultProperties;
	}

	/**
	 * Constructor with message properties.
	 * 
	 * @param props
	 *            Messages.
	 */
	MessagesWrapper(final Properties props) {
		super();
		if (props == null) {
			throw new IllegalArgumentException(
					"The argument 'props' cannot be null!");
		}
		this.props = props;
	}

	/**
	 * Returns the "update available" message.
	 * 
	 * @return Message.
	 */
	public final String getUpdateAvailable() {
		return props.getProperty("update-available");
	}

	/**
	 * Returns the transfer text for the progress monitor.
	 * 
	 * @return Message.
	 */
	public final String getProgressMonitorTransferText() {
		return props.getProperty("progress-monitor-transfertext");
	}

	/**
	 * Returns the text for the "source" label of the progress monitor.
	 * 
	 * @return Message.
	 */
	public final String getProgressMonitorSrcLabelText() {
		return props.getProperty("progress-monitor-srclabeltext");
	}

	/**
	 * Returns the text for the "destination" label of the progress monitor.
	 * 
	 * @return Message.
	 */
	public final String getProgressMonitorDestLabelText() {
		return props.getProperty("progress-monitor-destlabeltext");
	}

	/**
	 * Returns the decompress text for the progress monitor.
	 * 
	 * @return Message.
	 */
	public final String getProgressMonitorDecompressText() {
		return props.getProperty("progress-monitor-decompresstext");
	}

	/**
	 * Returns the destination directory selection message.
	 * 
	 * @return Message.
	 */
	public final String getSelectDestinationDirectory() {
		return props.getProperty("select-destination-directory");
	}

	/**
	 * Returns the operation canceled message.
	 * 
	 * @return Message.
	 */
	public final String getOperationCanceled() {
		return props.getProperty("operation-canceled");
	}

	/**
	 * Returns the command line option usage text for "configFileUrl".
	 * 
	 * @return Message.
	 */
	public final String getOptionUsageConfigFileUrl() {
		return props.getProperty("option-usage-configFileUrl");
	}

	/**
	 * Returns the command line option usage text for "locale".
	 * 
	 * @return Message.
	 */
	public final String getOptionUsageLocale() {
		return props.getProperty("option-usage-locale");
	}

	/**
	 * Returns the command line option required text.
	 * 
	 * @return Message.
	 */
	public final String getOptionRequired() {
		return props.getProperty("option-required");
	}

	/**
	 * Returns error message for missing options.
	 * 
	 * @return Message.
	 */
	public final String getErrorMissingReuiredOptions() {
		return props.getProperty("error.missing-required-options");
	}

	/**
	 * Returns error message for option without prefix.
	 * 
	 * @param arg
	 *            Option key to display in error message.
	 * 
	 * @return Message.
	 */
	public final String getErrorOptionWithoutPrefix(final String arg) {
		return replace(props.getProperty("error.option-without-prefix"),
				"$ARG", arg);
	}

	/**
	 * Returns error message for option without value.
	 * 
	 * @param arg
	 *            Option key to display in error message.
	 * 
	 * @return Message.
	 */
	public final String getErrorValueMissingForOption(final String arg) {
		return replace(props.getProperty("error.value-missing-for-option"),
				"$ARG", arg);
	}

	/**
	 * Returns error message dialog title.
	 * 
	 * @return Title.
	 */
	public final String getErrorMessageDialogTitle() {
		return props.getProperty("error-message-dialog.title");
	}

	/**
	 * Returns start message dialog title.
	 * 
	 * @return Title.
	 */
	public final String getStartDialogTitle() {
		return props.getProperty("start-dialog.title");
	}
	
	/**
	 * Replace a variable with a value.
	 * 
	 * @param str
	 *            String with variable to replace.
	 * @param var
	 *            Variable to find - Cannot be <code>null</code>.
	 * @param val
	 *            Value to replace the variable with - A <code>null</code> value
	 *            deletes the variable.
	 * 
	 * @return Replaced variable or unchanged text when the variable was not
	 *         found.
	 */
	private static final String replace(final String str, final String var,
			final String val) {
		Utils4J.checkNotNull("var", var);
		if ((str == null) || (str.length() == 0)) {
			return str;
		}
		final Map vars = new HashMap();
		vars.put(var, val);
		return Utils4J.replaceVars(str, vars);
	}

}
