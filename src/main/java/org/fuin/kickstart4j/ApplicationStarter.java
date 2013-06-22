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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import javax.swing.JFrame;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteException;
import org.apache.commons.exec.ExecuteResultHandler;
import org.apache.commons.exec.Executor;
import org.apache.commons.exec.PumpStreamHandler;
import org.fuin.utils4j.Utils4J;

/**
 * Helper class that starts the target application.
 */
public final class ApplicationStarter {

    /** Directory of the application. */
    private final File destDir;

    /** Full command line used to start the application. */
    private final CommandLine commandLine;

    /** Frame displaying the "Starting application..." message. */
    private final JFrame startFrame;

    /** Listener to be informed if the startup is complete. */
    private final Kickstart4JListener listener;

    /** Configuration. */
    private final Config config;

    /** Signal if starting the process has failed. */
    private volatile boolean failed = false;

    /**
     * Constructor with all necessary data.
     * 
     * @param destDir
     *            Target directory - Cannot be <code>null</code>.
     * @param commandLine
     *            Command line - Cannot be <code>null</code>.
     * @param startFrame
     *            Frame displaying the "Starting application..." message - May
     *            be <code>null</code>.
     * @param listener
     *            Listener to be informed about startup completion - Cannot be
     *            <code>null</code>
     * @param config
     *            Current configuration - Cannot be <code>null</code>.
     */
    public ApplicationStarter(final File destDir, final CommandLine commandLine,
            final JFrame startFrame, final Kickstart4JListener listener, final Config config) {
        super();

        Utils4J.checkNotNull("destDir", destDir);
        Utils4J.checkNotNull("commandLine", commandLine);
        Utils4J.checkNotNull("listener", listener);
        Utils4J.checkNotNull("config", config);

        this.destDir = destDir;
        this.commandLine = commandLine;
        this.startFrame = startFrame;
        this.listener = listener;
        this.config = config;
    }

    /**
     * Execute the target application.
     */
    public void execute() {

        final ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        final ByteArrayOutputStream errStream = new ByteArrayOutputStream();
        final Executor exec = new DefaultExecutor();
        exec.setWorkingDirectory(destDir);
        exec.setStreamHandler(new PumpStreamHandler(outStream, errStream));
        try {

            // Spawns an separate thread
            exec.execute(commandLine, new ExecuteResultHandler() {
                public void onProcessFailed(final ExecuteException ex) {
                    failed = true;
                    if (startFrame != null) {
                        startFrame.setVisible(false);
                    }
                    System.out.print(errStream.toString());
                    ErrorDialog.showAndExit(errStream, ex, 1);
                }

                public void onProcessComplete(final int exitValue) {
                    // We never get here because the Kickstart4J process
                    // will be killed with "System.exit(..)" before...
                }
            });

            if (startFrame != null) {
                startFrameSleep();
                startFrame.setVisible(false);
            }

            // TODO Any idea for a better handling of this situation?
            // Should be a rare situation but "onProcessFailed(..)" and this
            // thread may interfere... If this part is faster than the
            // "Executor"
            // this may call "System.exit(..)" and kills the error message
            // display!

            // Abort processing if the concurrent thread signals a failure
            if (failed) {
                return;
            }
            System.out.print(outStream.toString());
            if (failed) {
                return;
            }
            if (config.isExitAfterExecute()) {
                if (failed) {
                    return;
                }
                System.exit(0);
            }
            if (failed) {
                return;
            }
            listener.startupComplete();
        } catch (final IOException ex) {
            throw new RuntimeException("Error executing target application!", ex);
        }

    }

    private void startFrameSleep() {
        if (config.getStartFrameDelaySeconds() > 0) {
            try {
                Thread.sleep(1000 * config.getStartFrameDelaySeconds());
            } catch (final InterruptedException ex) {
                // Ignore
            }
        }
    }

}
