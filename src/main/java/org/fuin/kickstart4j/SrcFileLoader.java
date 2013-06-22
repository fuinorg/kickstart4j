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
import java.net.URL;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.fuin.utils4j.Utils4J;
import org.fuin.utils4swing.progress.FileCopyProgressListener;

/**
 * Supports loading files defined in a update set into a local directory.
 */
public final class SrcFileLoader implements FileLoader {

	private static final Logger LOG = Logger.getLogger(SrcFileLoader.class);

	/** Configuration with known source files. */
	private final Config config;

	/**
	 * Default constructor. Assumes the configuration file is named
	 * "application.xml" and is located in the current directory.
	 * 
	 * @throws InvalidConfigException
	 *             Cannot read the configuration XML file.
	 */
	public SrcFileLoader() throws InvalidConfigException {
		super();
		try {
			final File configFile = new File("application.xml").getCanonicalFile();
			Utils4J.checkValidFile(configFile);
			this.config = ConfigParser.create(configFile);
		} catch (final IOException ex) {
			throw new RuntimeException("Cannot get canonical file!", ex);
		}
	}

	/**
	 * Constructor with configuration path and filename.
	 * 
	 * @param configPathAndFilename
	 *            Path and filename of XML configuration.
	 * 
	 * @throws InvalidConfigException
	 *             Cannot read the configuration XML file.
	 */
	public SrcFileLoader(final String configPathAndFilename) throws InvalidConfigException {
		super();
		try {
			final File configFile = new File(configPathAndFilename).getCanonicalFile();
			Utils4J.checkValidFile(configFile);
			this.config = ConfigParser.create(configFile);
		} catch (final IOException ex) {
			throw new RuntimeException("Cannot get canonical file!", ex);
		}
	}

	/**
	 * Constructor with URL.
	 * 
	 * @param configFileURL
	 *            URL of the configuration file.
	 * 
	 * @throws InvalidConfigException
	 *             Error reading the file.
	 */
	public SrcFileLoader(final URL configFileURL) throws InvalidConfigException {
		super();
		Utils4J.checkNotNull("configFileURL", configFileURL);
		this.config = ConfigParser.create(configFileURL);
	}

	/**
	 * Constructor with configuration.
	 * 
	 * @param config
	 *            Configuration.
	 */
	public SrcFileLoader(final Config config) {
		super();
		Utils4J.checkNotNull("config", config);
		this.config = config;
	}

	/**
	 * Returns the configuration.
	 * 
	 * @return Configuration used for loading the files.
	 */
	public final Config getConfig() {
		return config;
	}

	private File loadHashedFile(final String path, final String filename,
			final FileCopyProgressListener listener) throws SrcFileNotFoundException,
			FileNotFoundException {

		final SrcFile srcFile = config.findSrcFile(path, filename);
		final URL srcFileUrl = srcFile.getSrcFileURL();
		final File destFile = srcFile.getDestFile(config.getDestDir());

		if (destFile.exists()) {
			final String destHash = Utils4J.createHashMD5(destFile);
			if (!srcFile.getMd5Hash().equals(destHash)) {
				// Changed file
				Utils.copyURLToFile(listener, srcFileUrl, destFile, 1, srcFile.getSizeAsInt());
				if (LOG.isInfoEnabled()) {
					LOG.info("CHANGED: " + srcFileUrl + " => " + destFile);
				}
			}
		} else {
			// New file
			Utils.copyURLToFile(listener, srcFileUrl, destFile, 1, srcFile.getSizeAsInt());
			if (LOG.isInfoEnabled()) {
				LOG.info("NEW: " + srcFileUrl + " => " + destFile);
			}
		}
		return destFile;
	}

	private File loadByDirectory(final String path, final String filename,
			final FileCopyProgressListener listener) throws SrcDirNotFoundException,
			FileNotFoundException {

		final SrcDir srcDir = config.findSrcDir(path);
		final URL srcFileUrl = srcDir.getSrcFileURL(filename);
		final File destFile = srcDir.getDestFile(config.getDestDir(), filename);
		Utils.copyURLToFile(listener, srcFileUrl, destFile, 1, 0);
		if (LOG.isInfoEnabled()) {
			LOG.info("COPY: " + srcFileUrl + " => " + destFile);
		}
		return destFile;

	}

	/**
	 * {@inheritDoc}
	 */
	public File loadFile(final String path, final String filename) throws LoadingFileException {
		return loadFile(path, filename, null);
	}

	/**
	 * {@inheritDoc}
	 */
	public File loadFile(final String path, final String filename,
			final FileCopyProgressListener listener) throws LoadingFileException {

		Utils4J.checkNotNull("path", path);
		Utils4J.checkNotNull("filename", filename);

		try {
			try {
				return loadHashedFile(path, filename, listener);
			} catch (final SrcFileNotFoundException ex) {
				return loadByDirectory(path, filename, listener);
			}
		} catch (final FileNotFoundException ex) {
			if (LOG.isInfoEnabled()) {
				LOG.info("NOT FOUND: " + ex.getMessage());
			}
			throw new LoadingFileException("Source file not found! [path='" + path
					+ "', filename='" + filename + "']", ex);
		} catch (final SrcDirNotFoundException ex) {
			throw new LoadingFileException("Unknown file! [path='" + path + "', filename='"
					+ filename + "']", ex);
		} catch (final RuntimeException ex) {
			throw new LoadingFileException("Load error! [path='" + path + "', filename='"
					+ filename + "']", ex);
		}

	}

	/**
	 * {@inheritDoc}
	 */
	public final void deleteFile(final String path, final String filename)
			throws DeleteException {

		Utils4J.checkNotNull("path", path);
		Utils4J.checkNotNull("filename", filename);
		final String trimmedFilename = filename.trim();
		Utils4J.checkNotEmpty("filename", trimmedFilename);

		final String trimmedPath = path.trim();
		try {
			final File dir;
			if (trimmedPath.length() == 0) {
				dir = config.getDestDir();
			} else {
				dir = new File(config.getDestDir(), trimmedPath);
			}
			final File file = new File(dir, trimmedFilename);
			if (file.exists()) {
				if (file.delete()) {
					LOG.info("DELETED " + file);
				} else {
					throw new DeleteException("Cannot delete file '" + file + "'!");
				}
			} else {
				LOG.info("DELETE (does not exist) " + file);
			}
		} catch (final RuntimeException ex) {
			throw new DeleteException("Unexpected delete error! [path='" + path
					+ "', filename='" + filename + "']", ex);
		}

	}

	/**
	 * {@inheritDoc}
	 */
	public final void deletePath(final String path) throws DeleteException {

		Utils4J.checkNotNull("path", path);
		final String trimmedPath = path.trim();
		Utils4J.checkNotEmpty("path", trimmedPath);
		try {
			final File dir = new File(config.getDestDir(), trimmedPath);
			if (dir.exists()) {
				try {
					FileUtils.deleteDirectory(dir);
					LOG.info("DELETED " + dir);
				} catch (final IOException e) {
					throw new DeleteException("Cannot delete directory '" + dir + "'!");
				}
			} else {
				LOG.info("DELETE (does not exist) " + dir);
			}
		} catch (final RuntimeException ex) {
			throw new DeleteException("Unexpected delete error! [path='" + path + "']", ex);
		}

	}

}
