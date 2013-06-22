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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.fuin.utils4j.Utils4J;

/**
 * Creates or updates a configuration.
 */
public final class ConfigUpdater {

    /** Configuration to update. */
    private final Config config;

    /** Listener to inform about progress. */
    private final ConfigUpdaterListener listener;

    /**
     * Default constructor.
     */
    public ConfigUpdater() {
        super();
        this.config = new Config();
        this.listener = new DefaultListener();
    }

    /**
     * Constructor with listener.
     * 
     * @param listener
     *            Used for writing log messages - Cannot be <code>null</code>.
     */
    public ConfigUpdater(final ConfigUpdaterListener listener) {
        super();
        this.config = new Config();
        Utils4J.checkNotNull("listener", listener);
        this.listener = listener;
    }

    /**
     * Constructor with configuration file.
     * 
     * @param configFile
     *            Existing configuration file.
     * 
     * @throws InvalidConfigException
     *             Error parsing an existing configuration.
     */
    public ConfigUpdater(final File configFile) throws InvalidConfigException {
        this(configFile, new DefaultListener());
    }

    /**
     * Constructor with configuration file and log printer.
     * 
     * @param configFile
     *            Existing configuration file.
     * @param listener
     *            Used for writing log messages.
     * 
     * @throws InvalidConfigException
     *             Error parsing an existing configuration.
     */
    public ConfigUpdater(final File configFile, final ConfigUpdaterListener listener)
            throws InvalidConfigException {
        super();
        Utils4J.checkNotNull("configFile", configFile);
        Utils4J.checkValidFile(configFile);
        this.config = ConfigParser.create(configFile);
        Utils4J.checkNotNull("listener", listener);
        this.listener = listener;
    }

    /**
     * Copies the remote file to a local file and informs the listener.
     * 
     * @param remoteFile
     *            Remote file to copy.
     * @param file
     *            Target file.
     * @param nr
     *            Current file number.
     * @param max
     *            Total files.
     * 
     * @throws IOException
     *             Error while copying.
     */
    private void copyToFile(final RemoteFile remoteFile, final File file, final int nr,
            final int max) throws IOException {

        try {
            FileUtils.copyURLToFile(remoteFile.getSrcFileUrl(), file);
            listener.onCopy(remoteFile, file, nr, max);
        } catch (final FileNotFoundException ex) {
            if (remoteFile.isErrorIfNotFound()) {
                throw ex;
            } else {
                listener.onNotFound(remoteFile, file, nr, max);
            }
        }

    }

    /**
     * Locate a source file by it's path and filename in the list.
     * 
     * @param srcFiles
     *            List of <code>SrcFile</code> objects.
     * @param remoteFile
     *            Remote file to find.
     * 
     * @return Source file or <code>null</code> if nothing was found.
     */
    private SrcFile findSrcFile(final List srcFiles, final RemoteFile remoteFile) {
        for (int i = 0; i < srcFiles.size(); i++) {
            final SrcFile srcFile = (SrcFile) srcFiles.get(i);
            if (remoteFile.getDestSlashPath().equals(srcFile.getSlashPath())
                    && remoteFile.getDestFilename().equals(srcFile.getFilename())) {
                return srcFile;
            }
        }
        return null;
    }

    /**
     * Clears the source file list in the configuration (!) and adds the remote
     * files in the list. Informations (like "unzip", "loadAlways" or
     * "addToClasspath") of known source files will be preserved.
     * 
     * @param remoteFileList
     *            List with <code>RemoteFile</code> objects.
     * 
     * @throws IOException
     *             Error while copying.
     */
    public final void update(final List remoteFileList) throws IOException {

        final List oldSrcFiles = new ArrayList();
        oldSrcFiles.addAll(config.getSrcFiles());
        config.getSrcFiles().clear();

        final File localFile = File.createTempFile("kickstart4j-config-creator-", ".tmp");

        final int size = remoteFileList.size();
        for (int i = 0; i < size; i++) {
            final RemoteFile remoteFile = (RemoteFile) remoteFileList.get(i);
            final String fileMd5Hash;
            final long fileLength;
            if ((remoteFile.getMd5Hash() != null) && (remoteFile.getLength() > 0)) {
                fileMd5Hash = remoteFile.getMd5Hash();
                fileLength = remoteFile.getLength();
            } else {
                copyToFile(remoteFile, localFile, (i + 1), size);
                fileMd5Hash = Utils4J.createHashMD5(localFile);
                fileLength = localFile.length();
            }

            final SrcFile oldSrcFile = findSrcFile(oldSrcFiles, remoteFile);
            final SrcFile newSrcFile;
            if (oldSrcFile == null) {
                // New file
                newSrcFile = new SrcFile(remoteFile.getDestPath(), remoteFile.getDestFilename(),
                        fileMd5Hash, fileLength, false, false, false, remoteFile.getSrcFileUrl()
                                .toExternalForm(), 0);
            } else {
                // Updated file
                newSrcFile = new SrcFile(remoteFile.getDestPath(), remoteFile.getDestFilename(),
                        fileMd5Hash, fileLength, oldSrcFile.isUnzip(), oldSrcFile.isLoadAlways(),
                        oldSrcFile.isAddToClasspath(), remoteFile.getSrcFileUrl().toExternalForm(),
                        oldSrcFile.getOrder());
            }
            config.getSrcFiles().add(newSrcFile);

        }

        localFile.delete();

    }

    /**
     * Adds all files in a directory and it's sub directories to the remote file
     * list.
     * 
     * @param baseDir
     *            Base directory.
     * @param dir
     *            Directory inside the base directory or one of it's sub
     *            directories.
     * @param remoteFiles
     *            Remote file list to add the files to.
     */
    private static void addSrcFiles(final File baseDir, final File dir, final List remoteFiles) {

        final File[] files = dir.listFiles();
        for (int i = 0; i < files.length; i++) {
            if (files[i].isDirectory()) {
                addSrcFiles(baseDir, files[i], remoteFiles);
            } else {
                final String path = getRelativePath(baseDir, dir);
                final String filename = files[i].getName();
                try {
                    final RemoteFile remoteFile = new RemoteFile(files[i].toURI().toURL(), path,
                            filename, false);
                    remoteFiles.add(remoteFile);
                } catch (final MalformedURLException ex) {
                    throw new RuntimeException(ex);
                }
            }
        }

    }

    /**
     * Returns a relative path for the directory.
     * 
     * @param baseDir
     *            Base directory.
     * @param dir
     *            Directory inside the base directory.
     * 
     * @return Relative path with slash instead of system path separator.
     */
    private static String getRelativePath(final File baseDir, final File dir) {
        return Utils4J.getRelativePath(baseDir, dir).replace(File.separatorChar, '/');
    }

    /**
     * Creates a remote file list of the directory and it's sub directories.
     * 
     * @param dir
     *            Directory to parse.
     * 
     * @return List of <code>RemoteFile</code> objects.
     */
    private static List createRemoteFileList(final File dir) {
        final List remoteFiles = new ArrayList();
        addSrcFiles(dir, dir, remoteFiles);
        return remoteFiles;
    }

    /**
     * Clears the source file list in the configuration (!) and adds all files
     * in the directory (and it's sub directories). Informations (like "unzip",
     * "loadAlways" or "addToClasspath") of known source files will be
     * preserved.
     * 
     * @param baseDir
     *            Source directory to create a configuration for.
     * 
     * @throws IOException
     *             Error while copying.
     */
    public final void update(final File baseDir) throws IOException {
        final List remoteFiles = createRemoteFileList(baseDir);
        update(remoteFiles);
    }

    /**
     * Returns the configuration.
     * 
     * @return Configuration.
     */
    public final Config getConfig() {
        return config;
    }

    /**
     * Returns the listener to inform about progress.
     * 
     * @return Listener - Always non-<code>null</code>.
     */
    public final ConfigUpdaterListener getListener() {
        return listener;
    }

    /**
     * Outputs events to the console.
     */
    protected static final class DefaultListener implements ConfigUpdaterListener {

        /**
         * {@inheritDoc}
         */
        public void onCopy(final RemoteFile remoteFile, final File file, final int nr, final int max) {

            System.out.println("COPY " + nr + "/" + max + ": " + remoteFile.getSrcFileUrl());

        }

        /**
         * {@inheritDoc}
         */
        public void onNotFound(final RemoteFile remoteFile, final File file, final int nr,
                final int max) {

            System.out.println("NOT FOUND " + nr + "/" + max + ": " + remoteFile.getSrcFileUrl());

        }

    }

    /**
     * Creates a new configuration file or updates an existing one.
     * 
     * @param args
     *            First argument is the path and name of the configuration file.
     *            Second argument is the directory to create the configuration
     *            for.
     * 
     * @throws InvalidConfigException
     *             Error parsing an existing configuration file.
     * @throws IOException
     *             I/O-Error.
     */
    public static void main(final String[] args) throws InvalidConfigException, IOException {

        // We have exactly two arguments
        if ((args == null) || (args.length != 2)) {
            System.out.println("java -classpath <CP> " + ConfigUpdater.class.getName()
                    + " <CONFIG-FILE> <APP-DIR>");
            System.out.println("    <CONFIG-FILE> = Path and name of configuration file");
            System.out.println("    <APP-DIR> = Path and name of existing application directory");
            return;
        }

        // Check if arguments are valid file and directory
        final File baseDir = new File(args[1]);
        Utils4J.checkValidDir(baseDir);
        final File configFile = new File(args[0]);
        Utils4J.checkValidDir(configFile.getParentFile());

        // Create updater and start
        final ConfigUpdater updater;
        if (configFile.exists()) {
            System.out.println("READ " + configFile);
            updater = new ConfigUpdater(configFile);
        } else {
            updater = new ConfigUpdater();
        }
        updater.update(baseDir);

        // Save configuration to disk
        updater.getConfig().writeToVarXML(configFile, true);
        System.out.println("SAVED " + configFile);

    }

}
