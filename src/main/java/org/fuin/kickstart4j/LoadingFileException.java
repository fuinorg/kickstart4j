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

/**
 * Loading a file failed for some reason.
 */
public final class LoadingFileException extends Exception {

    private static final long serialVersionUID = 1L;

    /**
     * Constructor with message.
     * 
     * @param message
     *            Error message.
     */
    public LoadingFileException(final String message) {
        super(message);
    }

    /**
     * Constructor with message and cause.
     * 
     * @param message
     *            Message.
     * @param cause
     *            Cause.
     */
    public LoadingFileException(final String message, final Throwable cause) {
        super(message, cause);
    }

}
