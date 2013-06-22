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

import java.net.URL;

/**
 * A configuration is invalid.
 */
public final class InvalidConfigException extends Exception {

    private static final long serialVersionUID = 1L;

    /**
     * Constructor with message.
     * 
     * @param msg
     *            Message.
     */
    public InvalidConfigException(final String msg) {
        super(msg);
    }

    /**
     * Constructor with URL and cause.
     * 
     * @param url
     *            URL with the XML config.
     * @param cause
     *            Cause for the problem.
     */
    public InvalidConfigException(final URL url, final Throwable cause) {
        super("Error parsing configuration '" + url + "'!", cause);
    }

}
