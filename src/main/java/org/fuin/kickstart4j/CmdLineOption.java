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

import org.fuin.utils4j.Utils4J;

/**
 * Represents a single command line option.
 */
public final class CmdLineOption {

	/** Key (short name) of the option. */
	private final String key;

	/** Name of the value displayed in the usage message. */
	private final String valueName;

	/** Determines if the value is required or optional. */
	private final boolean required;

	/** Text displayed in the usage message. */
	private final String usage;

	/**
	 * Constructor for a optional option.
	 * 
	 * @param key
	 *            Key (short name) of the option.
	 * @param valueName
	 *            Name of the value displayed in the usage message.
	 * @param usage
	 *            Text displayed in the usage message.
	 */
	public CmdLineOption(final String key, final String valueName,
			final String usage) {
		this(key, valueName, usage, false);
	}

	/**
	 * Constructor with required argument.
	 * 
	 * @param key
	 *            Key (short name) of the option.
	 * @param valueName
	 *            Name of the value displayed in the usage message.
	 * @param usage
	 *            Text displayed in the usage message.
	 * @param required
	 *            Determines if the value is required or optional.
	 */
	public CmdLineOption(final String key, final String valueName,
			final String usage, final boolean required) {
		super();
		Utils4J.checkNotNull("key", key);
		Utils4J.checkNotNull("valueName", valueName);
		Utils4J.checkNotNull("usage", usage);
		this.key = key;
		this.valueName = valueName;
		this.usage = usage;
		this.required = required;
	}

	/**
	 * Returns the key of the option.
	 * 
	 * @return Short name.
	 */
	public final String getKey() {
		return key;
	}

	/**
	 * Returns the text displayed in the usage message.
	 * 
	 * @return Message explaining the option.
	 */
	public final String getUsage() {
		return usage;
	}

	/**
	 * Returns if the the value is required or optional.
	 * 
	 * @return If required <code>true</code> else <code>false</code>.
	 */
	public final boolean isRequired() {
		return required;
	}

	/**
	 * Returns the name of the value displayed in the usage message.
	 * 
	 * @return Name of the value.
	 */
	public final String getValueName() {
		return valueName;
	}

	/**
	 * Returns the option text.
	 * 
	 * @param msg
	 *            Localized message texts.
	 * 
	 * @return Option text.
	 */
	public final String getOptionText(final MessagesWrapper msg) {
		final String requiredStr;
		if (required) {
			requiredStr = " [" + msg.getOptionRequired() + "]";
		} else {
			requiredStr = "";
		}
		return "-" + key + " " + valueName + "  " + usage + requiredStr;
	}

}
