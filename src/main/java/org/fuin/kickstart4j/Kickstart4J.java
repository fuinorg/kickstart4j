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
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URL;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.apache.commons.exec.CommandLine;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.fuin.utils4j.Cancelable;
import org.fuin.utils4j.Utils4J;
import org.fuin.utils4swing.common.ScreenCenterPositioner;
import org.fuin.utils4swing.common.Utils4Swing;
import org.fuin.utils4swing.dialogs.CanceledException;
import org.fuin.utils4swing.dialogs.DirectorySelector;
import org.fuin.utils4swing.progress.FileCopyProgressListener;
import org.fuin.utils4swing.progress.FileCopyProgressMonitor;
import org.fuin.utils4swing.threadsafe.ThreadSafeJOptionPane;

/**
 * Main application.
 */
public final class Kickstart4J {

    private static final Logger LOG = Logger.getLogger(Kickstart4J.class);

    private static final String PROGRAM_TERMINATED_WITH_ERROR = "Program terminated with error!";

    private static final String INCOMPLETE_FILE = ".incomplete";

    private static final String PROGRAM_DIRECTORY_KEY = "program-directory";

    /** Configuration used for the application. */
    private final Config config;

    /** Listens to life cycle events. */
    private Kickstart4JListener listener;

    /**
     * Constructor with configuration.
     * 
     * @param config
     *            Configuration to use.
     */
    public Kickstart4J(final Config config) {
        super();
        if (config == null) {
            throw new IllegalArgumentException("The argument 'config' cannot be null!");
        }
        this.config = config;
        this.listener = new DefaultListener();
    }

    /**
     * Returns the life cycle listener.
     * 
     * @return Listener - Always non-<code>null</code>.
     */
    public final Kickstart4JListener getListener() {
        return listener;
    }

    /**
     * Sets the life cycle listener.
     * 
     * @param listener
     *            Listener - Will be set to a default listener if
     *            <code>null</code>.
     */
    public final void setListener(final Kickstart4JListener listener) {
        if (listener == null) {
            this.listener = new DefaultListener();
        } else {
            this.listener = listener;
        }
    }

    /**
     * Initialize file logging with configuration values.
     */
    private void initLogging() {

        try {
            final File logFile = new File(config.getLogFilename()).getCanonicalFile();

            boolean ok = true;
            if (!logFile.getParentFile().exists()) {
                ok = logFile.getParentFile().mkdirs();
            }

            if (ok) {
                final Properties props = new Properties();
                props.put("log4j.rootLogger", "INFO, FILE");
                props.put("log4j.appender.FILE", "org.apache.log4j.RollingFileAppender");
                props.put("log4j.appender.FILE.File", logFile.toString());
                props.put("log4j.appender.FILE.MaxFileSize", "1MB");
                props.put("log4j.appender.FILE.MaxBackupIndex", "1");
                props.put("log4j.appender.FILE.layout", "org.apache.log4j.PatternLayout");
                props.put("log4j.appender.FILE.layout.ConversionPattern", "%d [%t] %-5p %c - %m%n");
                PropertyConfigurator.configure(props);
            } else {
                LOG.error("Cannot create log directory: " + logFile.getParentFile());
            }
        } catch (final IOException ex) {
            LOG.error("Cannot create log!", ex);
        }

    }

    /**
     * Executes the installer/updater.
     * 
     * @throws CanceledException
     *             The user canceled the installation.
     * @throws InvalidConfigException
     *             The configuration is invalid.
     */
    public final void execute() throws CanceledException, InvalidConfigException {

        Locale.setDefault(config.getLocale());
        final File destDir = getDestDir();
        config.getCmdLineOptions().put("destDir", destDir.toString());

        // Check configuration AFTER destination directory is set
        // and logging is initialized
        config.check();
        initLogging();
        listener.initComplete();

        // Start the update
        final UpdateSet updateSet = new UpdateSet(config.getSrcFiles(), config.getMkDirs(),
                destDir, config.isLazyLoading());
        if (updateSet.isUpdateNecessary()) {
            if (LOG.isInfoEnabled()) {
                LOG.info("An update is available: New=" + updateSet.getNewFiles().size()
                        + ", Changed=" + updateSet.getChangedFiles().size() + ", Deleted="
                        + updateSet.getDeletedFiles().size() + ", SilentInstall="
                        + config.isSilentInstall() + ", SilentUpdate=" + config.isSilentUpdate()
                        + ", FirstInstallation=" + config.isFirstInstallation());
            }
            if (config.isSilentUpdate() || config.isFirstInstallation()
                    || isAnswerYes(config.getMessages().getUpdateAvailable())) {
                execute(updateSet);
                final File installationIncompleteFile = new File(destDir, INCOMPLETE_FILE);
                if (installationIncompleteFile.exists()) {
                    installationIncompleteFile.delete();
                }
            }
        } else {
            LOG.info("Files are up to date");
        }

        final JFrame startFrame = showStartFrame();

        config.getCmdLineOptions().put("classpath", updateSet.createClasspath());

        // Write the config to the target directory
        saveConfigToTargetDir(destDir);

        // Run the target application
        final CommandLine commandLine = new CommandLine(config.getJavaExe());
        commandLine.addArguments(config.getJavaArgs(), false);
        logStart(destDir, commandLine.toString());
        new ApplicationStarter(destDir, commandLine, startFrame, listener, config).execute();

    }

    private JFrame showStartFrame() {
        if (config.isShowStartFrame()) {
            final JFrame startFrame = Utils4Swing.createShowAndPosition(config.getMessages()
                    .getStartDialogTitle(), new StartPanel(), false, new ScreenCenterPositioner());
            startFrame.setResizable(false);
            return startFrame;
        } else {
            return null;
        }
    }

    private void saveConfigToTargetDir(final File destDir) {
        final File appXmlFile = new File(destDir, "application.xml");
        try {
            final String localConfigFileUrl = appXmlFile.toURI().toURL().toString();
            config.setConfigFileUrl(localConfigFileUrl);
            config.getCmdLineOptions().put("configFileUrl", localConfigFileUrl);
            config.writeToStaticXML(appXmlFile, true);
        } catch (final IOException ex) {
            throw new RuntimeException("Error writing " + appXmlFile + "!", ex);
        }
    }

    private File getIdFile(final Config config) {
        return new File(Utils4J.getUserHomeDir(), config.getIdFilename());
    }

    private void execute(final UpdateSet updateSet) throws CanceledException {
        executeMkdirs(updateSet.getDestDir(), updateSet.getMkDirs());

        final List orderList = updateSet.getOrderList();
        for (int i = 0; i < orderList.size(); i++) {
            final int order = ((Integer) orderList.get(i)).intValue();

            final List newFiles = updateSet.getNewFiles(order);
            final List changedFiles = updateSet.getChangedFiles(order);
            final List deletedFiles = updateSet.getDeletedFiles(order);
            executeCopy(updateSet.getDestDir(), newFiles, changedFiles, deletedFiles);

            final List decompressFiles = updateSet.getDecompressFiles(order);
            executeDecompress(updateSet.getDestDir(), decompressFiles);

        }

    }

    private void executeMkdirs(final File destDir, final List mkdirs) {

        for (int i = 0; i < mkdirs.size(); i++) {
            final MkDir mkDir = (MkDir) mkdirs.get(i);
            final File dirToCreate = mkDir.getDestDir(destDir);
            if (dirToCreate.exists()) {
                LOG.info("MKDIR: " + dirToCreate + " (Already exists)");
            } else {
                final boolean ok = dirToCreate.mkdirs();
                if (LOG.isInfoEnabled()) {
                    if (ok) {
                        LOG.info("MKDIR: " + dirToCreate);
                    } else {
                        LOG.info("MKDIR FAILED: " + dirToCreate);
                    }
                }
            }
        }

    }

    private void executeCopy(final File destDir, final List newFiles, final List changedFiles,
            final List deletedFiles) throws CanceledException {

        final int max = newFiles.size() + changedFiles.size();
        if (max > 0) {

            final Cancelable cancelable = new Cancelable() {

                private volatile boolean canceled = false;

                public void cancel() {
                    canceled = true;
                }

                public boolean isCanceled() {
                    return canceled;
                }
            };

            final FileCopyProgressMonitor monitor = new FileCopyProgressMonitor(cancelable,
                    config.getTitle(), config.getMessages().getProgressMonitorTransferText(),
                    config.getMessages().getProgressMonitorSrcLabelText(), config.getMessages()
                            .getProgressMonitorDestLabelText(), max);

            int count = 0;

            monitor.open();
            try {

                // New files
                if (cancelable.isCanceled()) {
                    throw new CanceledException();
                }
                count = copyFiles(destDir, cancelable, monitor, newFiles, count, "NEW");

                // Changed files
                if (cancelable.isCanceled()) {
                    throw new CanceledException();
                }
                count = copyFiles(destDir, cancelable, monitor, changedFiles, count, "CHANGED");

                // No longer existent (deleted) files
                if (cancelable.isCanceled()) {
                    throw new CanceledException();
                }
                for (int i = 0; i < deletedFiles.size(); i++) {
                    if (cancelable.isCanceled()) {
                        break;
                    }
                    count = count + 1;
                    final String file = (String) deletedFiles.get(i);
                    final File destFile = new File(destDir, file);
                    final boolean ok = destFile.delete();
                    if (LOG.isInfoEnabled()) {
                        if (ok) {
                            LOG.info("DELETED: " + destFile);
                        } else {
                            LOG.info("DELETE FAILED: " + destFile);
                        }
                    }
                    monitor.updateFile("", destFile.toString(), count, 0);
                }

            } finally {
                monitor.close();
            }

        }
    }

    private void executeDecompress(final File destDir, final List compressedFiles)
            throws CanceledException {

        final int max = compressedFiles.size();
        if (max > 0) {

            final Cancelable cancelable = new Cancelable() {

                private volatile boolean canceled = false;

                public void cancel() {
                    canceled = true;
                }

                public boolean isCanceled() {
                    return canceled;
                }
            };

            final FileCopyProgressMonitor monitor = new FileCopyProgressMonitor(cancelable,
                    config.getTitle(), config.getMessages().getProgressMonitorDecompressText(),
                    config.getMessages().getProgressMonitorSrcLabelText(), config.getMessages()
                            .getProgressMonitorDestLabelText(), max);

            monitor.open();
            try {

                for (int i = 0; i < max; i++) {
                    final SrcFile file = (SrcFile) compressedFiles.get(i);
                    final File compressedFile = file.getDestFile(destDir);
                    if (LOG.isInfoEnabled()) {
                        LOG.info("Decompressing: " + compressedFile);
                    }
                    Utils.unzip(monitor, compressedFile, (i + 1), destDir, cancelable);
                }

            } finally {
                monitor.close();
            }

        }
    }

    private int copyFiles(final File destDir, final Cancelable cancelable,
            final FileCopyProgressListener listener, final List files, final int total,
            final String type) {

        int count = total;

        for (int i = 0; i < files.size(); i++) {
            if (cancelable.isCanceled()) {
                break;
            }
            count = count + 1;
            final SrcFile file = (SrcFile) files.get(i);
            final URL srcFileUrl = file.getSrcFileURL();
            final File destFile = file.getDestFile(destDir);
            try {
                Utils.copyURLToFile(listener, srcFileUrl, destFile, count, file.getSizeAsInt());
            } catch (final FileNotFoundException ex) {
                throw new RuntimeException("Source file not found!", ex);
            }
            final String hash = Utils4J.createHashMD5(destFile);
            if (!hash.equals(file.getMd5Hash())) {
                LOG.error("Hash local file (" + hash + ") is different from configuration hash ("
                        + file.getMd5Hash() + ")! [" + srcFileUrl + "]");
            }
            if (LOG.isInfoEnabled()) {
                LOG.info(type + ": " + srcFileUrl + " => " + destFile);
            }
        }

        return count;

    }

    private void logStart(final File dir, final String commandLine) {
        try {
            final File file = new File(dir, "start.log");
            final FileWriter writer = new FileWriter(file);
            try {
                writer.write(commandLine);
            } finally {
                writer.close();
            }
        } catch (final IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    private File getDestDir() throws CanceledException {
        final File installationIncompleteFile;
        final File dir;
        final File idFile = getIdFile(config);
        if (idFile.exists()) {
            // Update
            final Properties props = Utils4J.loadProperties(idFile);
            final String dirStr = props.getProperty(PROGRAM_DIRECTORY_KEY);
            if (dirStr == null) {
                throw new IllegalStateException("The property '" + PROGRAM_DIRECTORY_KEY
                        + "' was not found inside '" + idFile + "'!");
            }
            dir = new File(dirStr);
            Utils4J.checkValidDir(dir);
            installationIncompleteFile = new File(dir, INCOMPLETE_FILE);
        } else {
            // First installation or file removed
            final String dirStr;
            final Properties props = new Properties();
            if (config.isSilentInstall()) {
                dirStr = config.getDestDir().toString();
            } else {
                // Ask User for destination directory
                dirStr = DirectorySelector.selectDirectory(
                        config.getMessages().getSelectDestinationDirectory(),
                        config.getDestDir().toString()).getDirectory();
            }
            props.setProperty(PROGRAM_DIRECTORY_KEY, dirStr);
            Utils4J.saveProperties(idFile, props,
                    "# --- DO NOT EDIT OR DELETE --- Generated by Kickstart4J ---");
            dir = new File(dirStr);
            installationIncompleteFile = new File(dir, INCOMPLETE_FILE);
            if (!dir.exists()) {
                try {
                    dir.mkdirs();
                    installationIncompleteFile.createNewFile();
                } catch (final IOException ex) {
                    throw new RuntimeException("Cannot create file '" + installationIncompleteFile
                            + "'!");
                }
            }
        }
        config.setFirstInstallation(installationIncompleteFile.exists());
        return dir;
    }

    private static boolean isAnswerYes(final String message) {
        final int result = ThreadSafeJOptionPane.showConfirmDialog(null, message, "TITLE",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        return result == JOptionPane.YES_OPTION;
    }

    /**
     * Shows a message and exits the program with <code>System.exit(..)</code>.
     * 
     * @param message
     *            Message to display.
     * @param exitCode
     *            System exit code.
     */
    public static void showMessageAndExit(final String message, final int exitCode) {
        ThreadSafeJOptionPane.showMessageDialog(null, message, "Hint",
                JOptionPane.INFORMATION_MESSAGE);
        System.exit(exitCode);
    }

    /**
     * Shows an error message and exits the program with
     * <code>System.exit(..)</code>.
     * 
     * @param parser
     *            Parser for usage display.
     * @param ex
     *            Exception to display.
     * @param exitCode
     *            System exit code.
     */
    public static void displayCmdLineExceptionAndExit(final CmdLineParser parser,
            final CmdLineException ex, final int exitCode) {
        final StringBuffer sb = new StringBuffer();
        sb.append(ex.getMessage());
        sb.append("\n");
        sb.append("\n");
        sb.append("java Kickstart4J [options]");
        sb.append("\n");
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        parser.printUsage(out);
        sb.append(out.toString());
        sb.append("\n");
        System.out.println(sb);
        ErrorDialog.showAndExit(sb.toString(), exitCode);
    }

    /**
     * Shows an error message and exits the program with
     * <code>System.exit(..)</code>.
     * 
     * @param ex
     *            Exception to display.
     * @param exitCode
     *            System exit code.
     */
    public static void displayExceptionAndExit(final Exception ex, final int exitCode) {
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        ex.printStackTrace(new PrintStream(out));
        final String msg = out.toString();
        System.err.println(msg);
        ErrorDialog.showAndExit(msg, exitCode);
    }

    /**
     * Empty implementation.
     */
    private static final class DefaultListener implements Kickstart4JListener {

        /**
         * {@inheritDoc}
         */
        public final void initComplete() {
            // Do nothing
        }

        /**
         * {@inheritDoc}
         */
        public void startupComplete() {
            // Do nothing
        }

    }

    /**
     * Main method used to start the installer/updater. If you want to start it
     * from another Java application you can simply use
     * <code>new Kickstart4J(kickstart4JConfig).execute()</code> instead of
     * calling this method.
     * 
     * @param args
     *            Command line arguments.
     */
    public static void main(final String[] args) {

        final Logger log = Logger.getLogger(Kickstart4J.class);

        final Config config = new Config();
        try {

            // Parse command line
            final CmdLineParser cmdLineParser = new CmdLineParser(Locale.getDefault());
            try {
                cmdLineParser.parse(args);
                if (log.isDebugEnabled()) {
                    log.info("Command line arguments: " + cmdLineParser);
                }

                // Set user defined options from command line
                cmdLineParser.copyToConfig(config);

                // Load the configuration and start update
                try {
                    ConfigParser.parse(config, config.getConfigFileURL());
                    if (log.isInfoEnabled()) {
                        log.info("Configuration: " + config);
                    }

                    Utils4Swing.initLookAndFeel(config.getLookAndFeelClassName());
                    (new Kickstart4J(config)).execute();

                } catch (final CanceledException ex) {
                    log.info("Operation canceled by user!");
                    showMessageAndExit(config.getMessages().getOperationCanceled(), 1);
                } catch (final InvalidConfigException ex) {
                    log.error(PROGRAM_TERMINATED_WITH_ERROR, ex);
                    displayExceptionAndExit(ex, 1);
                }

            } catch (final CmdLineException ex) {
                log.error(PROGRAM_TERMINATED_WITH_ERROR, ex);
                displayCmdLineExceptionAndExit(cmdLineParser, ex, 1);
            }

        } catch (final RuntimeException ex) {
            log.error(PROGRAM_TERMINATED_WITH_ERROR, ex);
            displayExceptionAndExit(ex, 1);
        }

    }

}
