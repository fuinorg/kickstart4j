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

import java.awt.Dimension;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.ByteArrayOutputStream;
import java.util.Locale;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.apache.commons.exec.ExecuteException;
import org.fuin.utils4j.Utils4J;
import org.fuin.utils4swing.common.ScreenCenterPositioner;
import org.fuin.utils4swing.common.Utils4Swing;

/**
 * Shows an error message and terminates the program via
 * <code>System.exit(1)</code>.
 */
public final class ErrorDialog implements Runnable {

    private final String message;

    private final int exitCode;

    /**
     * Constructor with stream, exception and exit code.
     * 
     * @param errStream
     *            Error stream filled by <code>Executor</code>.
     * @param executeException
     *            Exception from <code>onProcessFailed(..)</code> method.
     * @param exitCode
     *            Code for <code>System.exit(..)</code>.
     */
    private ErrorDialog(final ByteArrayOutputStream errStream,
            final ExecuteException executeException, final int exitCode) {
        Utils4J.checkNotNull("errStream", errStream);
        Utils4J.checkNotNull("executeException", executeException);
        final String msg = errStream.toString();
        if (msg.length() == 0) {
            message = executeException.getMessage();
        } else {
            message = msg;
        }
        this.exitCode = exitCode;
    }

    /**
     * Constructor with message and exit code.
     * 
     * @param message
     *            Error message.
     * @param exitCode
     *            Code for <code>System.exit(..)</code>.
     */
    private ErrorDialog(final String message, final int exitCode) {
        Utils4J.checkNotNull("message", message);
        this.message = message;
        this.exitCode = exitCode;
    }

    /**
     * {@inheritDoc}
     */
    public void run() {
        final ErrorPanel panel = new ErrorPanel();
        panel.setMessage(message);
        panel.setPreferredSize(new Dimension(600, 400));
        panel.setCloseListener(new ErrorPanel.CloseListener() {
            public void onClose(final ErrorPanel panel) {
                System.exit(exitCode);
            }
        });
        final JFrame frame = Utils4Swing.createShowAndPosition(
                new MessagesWrapper(Locale.getDefault()).getErrorMessageDialogTitle(), panel,
                false, new ScreenCenterPositioner());
        frame.addComponentListener(new ComponentAdapter() {
            public void componentHidden(final ComponentEvent e) {
                System.exit(exitCode);
            }
        });

    }

    /**
     * Show an error message dialog using stream or exception.
     * 
     * @param errStream
     *            Error stream filled by <code>Executor</code>.
     * @param executeException
     *            Exception from <code>onProcessFailed(..)</code> method.
     * @param exitCode
     *            Code for <code>System.exit(..)</code>.
     */
    public static void showAndExit(final ByteArrayOutputStream errStream,
            final ExecuteException executeException, final int exitCode) {
        SwingUtilities.invokeLater(new ErrorDialog(errStream, executeException, exitCode));
    }

    /**
     * Show an error message dialog.
     * 
     * @param message
     *            Error message.
     * @param exitCode
     *            Code for <code>System.exit(..)</code>.
     */
    public static void showAndExit(final String message, final int exitCode) {
        SwingUtilities.invokeLater(new ErrorDialog(message, exitCode));
    }

}
