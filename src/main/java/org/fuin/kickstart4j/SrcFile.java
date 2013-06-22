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
import java.net.MalformedURLException;
import java.net.URL;

import org.fuin.utils4j.ToDebugStringCapable;
import org.fuin.utils4j.Utils4J;

/**
 * A file at the source directory. This class is immutable.
 */
public final class SrcFile extends AbstractFile implements ToDebugStringCapable {

	/** Token used to mark archives to be unzipped at the target location. */
	public static final String UNZIP_TOKEN = "UNZIP";

	/** Size of the file. */
	private final long size;

	/** ZIP file that needs to be decompressed at the target location. */
	private final boolean unzip;

	/** Load file always (no matter if lazy loading is enabled or not). */
	private final boolean loadAlways;

	/** JAR file to be added to the classpath. */
	private final boolean addToClasspath;

	/** URL where the source file is located. */
	private final String srcFileUrl;

	/** Order the files should be installed or updated. */
	private final int order;

	/**
	 * Constructor with base directory and file.
	 * 
	 * @param baseDir
	 *            Base directory for calculating the relative path.
	 * @param file
	 *            File inside the base directory.
	 * @param unzip
	 *            If the file is an archive and should be decompressed at the
	 *            target location <code>true</code> else <code>false</code>.
	 * @param loadAlways
	 *            Load file always (no matter if lazy loading is enabled or
	 *            not).
	 * @param addToClasspath
	 *            JAR file to be added to the classpath.
	 * @param srcFileUrl
	 *            URL where the source file is located.
	 */
	public SrcFile(final File baseDir, final File file, final boolean unzip,
			final boolean loadAlways, final boolean addToClasspath,
			final String srcFileUrl) {
		super(baseDir, file);
		this.size = file.length();
		this.unzip = unzip;
		this.loadAlways = loadAlways;
		this.addToClasspath = addToClasspath;
		Utils4J.checkNotNull("srcFileUrl", srcFileUrl);
		this.srcFileUrl = srcFileUrl;
		this.order = 0;
	}

	/**
	 * Constructor with all arguments.
	 * 
	 * @param path
	 *            Path relative to the base directory.
	 * @param filename
	 *            Filename without path.
	 * @param md5Hash
	 *            MD5 hash code of the file.
	 * @param size
	 *            Size of the file.
	 * @param unzip
	 *            If the file is an archive and should be decompressed at the
	 *            target location <code>true</code> else <code>false</code>.
	 * @param loadAlways
	 *            Load file always (no matter if lazy loading is enabled or
	 *            not).
	 * @param addToClasspath
	 *            JAR file to be added to the classpath.
	 * @param srcFileUrl
	 *            URL where the source file is located.
	 * @param order
	 *            Order the files should be updated or installed.
	 */
	public SrcFile(final String path, final String filename,
			final String md5Hash, final long size, final boolean unzip,
			final boolean loadAlways, final boolean addToClasspath,
			final String srcFileUrl, final int order) {
		super(path, filename, md5Hash);
		this.size = size;
		this.unzip = unzip;
		this.loadAlways = loadAlways;
		this.addToClasspath = addToClasspath;
		Utils4J.checkNotNull("srcFileUrl", srcFileUrl);
		this.srcFileUrl = srcFileUrl;
		this.order = order;
	}

	/**
	 * Copy-Constructor with source file and local file.
	 * 
	 * @param srcFile
	 *            Source file to copy values from (except hash and file size).
	 * @param file
	 *            File to create hash for and get file size from.
	 */
	public SrcFile(final SrcFile srcFile, final File file) {
		this(srcFile.getPath(), srcFile.getFilename(), Utils4J
				.createHashMD5(file), file.length(), srcFile.isUnzip(), srcFile
				.isLoadAlways(), srcFile.isAddToClasspath(), srcFile
				.getSrcFileUrl(), srcFile.getOrder());
	}

	/**
	 * Returns the size of the file.
	 * 
	 * @return File size.
	 */
	public final long getSize() {
		return size;
	}

	/**
	 * Returns the size as integer value. If the size is greater than
	 * <code>Integer.MAX_VALUE</code> is returned.
	 * 
	 * @return Size.
	 */
	public final int getSizeAsInt() {
		if (size > Integer.MAX_VALUE) {
			return Integer.MAX_VALUE;
		} else {
			return (int) size;
		}
	}

	/**
	 * Returns if the file is an atchive and should be decompressed at the
	 * target location.
	 * 
	 * @return Unzip the file at the destination directory <code>true</code>
	 *         else <code>false</code>
	 */
	public final boolean isUnzip() {
		return unzip;
	}

	/**
	 * Returns if this is a JAR file to be added to the classpath.
	 * 
	 * @return If added to the classpath <code>true</code> else
	 *         <code>false</code>.
	 */
	public final boolean isAddToClasspath() {
		return addToClasspath;
	}

	/**
	 * Returns if this file sould be loaded always (no matter if lazy loading is
	 * enabled or not).
	 * 
	 * @return If loaded always <code>true</code> else <code>false</code>.
	 */
	public final boolean isLoadAlways() {
		return loadAlways;
	}

	/**
	 * Returns the URL where the source file is located.
	 * 
	 * @return URL (as String).
	 */
	public final String getSrcFileUrl() {
		return srcFileUrl;
	}

	/**
	 * Returns the URL where the source file is located. A
	 * <code>MalformedURLException</code> is wrapped into a
	 * <code>RuntimeException</code>.
	 * 
	 * @return URL.
	 */
	public final URL getSrcFileURL() {
		if (srcFileUrl == null) {
			return null;
		}
		try {
			return new URL(srcFileUrl);
		} catch (final MalformedURLException ex) {
			throw new RuntimeException(ex);
		}
	}

	/**
	 * Returns the order the files should be installed or updated.
	 * 
	 * @return Number starting with 0 (default).
	 */
	public final int getOrder() {
		return order;
	}
	
    /**
     * Creates an XML representation of the object.
     * 
     * @return XML for element "file"
     */
	public final String toXML() {
		final String orderStr;
		if (order == 0) {
			orderStr = "";
		} else {
			orderStr = " order=\"" + order + "\"";
		}
		return "<file path=\"" + getSlashPath() + "\" file=\"" + getFilename()
				+ "\" hash=\"" + getMd5Hash() + "\" size=\"" + size
				+ "\" unzip=\"" + unzip + "\" loadAlways=\"" + loadAlways
				+ "\" addToClasspath=\"" + addToClasspath + "\" srcFileUrl=\""
				+ Utils.escapeXml(srcFileUrl) + "\"" + orderStr + "/>";
	}

	/**
	 * {@inheritDoc}
	 */
	public final String toString() {
		return getRelativeSlashPathAndFilename();
	}

	/**
	 * {@inheritDoc}
	 */
	public final String toDebugString() {
		final StringBuffer sb = new StringBuffer();
		sb.append("path=" + getPath() + ", ");
		sb.append("file=" + getFilename() + ", ");
		sb.append("hash=" + getMd5Hash() + ", ");
		sb.append("size=" + size + ", ");
		sb.append("unzip=" + unzip + ", ");
		sb.append("loadAlways=" + loadAlways + ", ");
		sb.append("addToClasspath=" + addToClasspath + ", ");
		sb.append("srcFileUrl=" + srcFileUrl + ", ");
		sb.append("order=" + order);
		return sb.toString();
	}

}
