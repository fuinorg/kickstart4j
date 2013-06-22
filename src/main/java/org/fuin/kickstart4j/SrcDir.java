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
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.fuin.utils4j.ToDebugStringCapable;
import org.fuin.utils4j.Utils4J;

/**
 * A directory inside a base directory. This class is immutable.
 */
public final class SrcDir implements ToDebugStringCapable {

	/** Path relative to the base directory. */
	private final String path;

	/**
	 * URL where the source directory is located - Contains the variable
	 * ${filename}.
	 */
	private final String srcPathUrl;

	/**
	 * Constructor with all arguments.
	 * 
	 * @param path
	 *            Path relative to the base directory.
	 * @param srcPathUrl
	 *            URL where the source directory is located - Contains the
	 *            variable ${filename}.
	 */
	public SrcDir(final String path, final String srcPathUrl) {
		super();
		if (path == null) {
			this.path = "";
		} else {
			this.path = path.replace('/', File.separatorChar);
		}
		Utils4J.checkNotNull("srcPathUrl", srcPathUrl);
		this.srcPathUrl = srcPathUrl;
	}

	/**
	 * Returns the path of the file.
	 * 
	 * @return Path relative to the base directory.
	 */
	public final String getPath() {
		return path;
	}

	/**
	 * Returns the URL where the source directory is located.
	 * 
	 * @return URL (as String) - Contains the variable: ${filename}.
	 */
	public final String getSrcPathUrl() {
		return srcPathUrl;
	}

	/**
	 * Returns the URL where the source file is located. A
	 * <code>MalformedURLException</code> is wrapped into a
	 * <code>RuntimeException</code>.
	 * 
	 * @param filename
	 *            Name of the file to return a URL for.
	 * 
	 * @return URL (without Variables).
	 */
	public final URL getSrcFileURL(final String filename) {
		Utils4J.checkNotNull("filename", filename);
		if (srcPathUrl == null) {
			return null;
		}
		final Map vars = new HashMap();
		vars.put("filename", filename);
		try {
			return new URL(Utils4J.replaceVars(srcPathUrl, vars));
		} catch (final MalformedURLException ex) {
			throw new RuntimeException(ex);
		}
	}

	/**
	 * Returns the absolute file based on a given base directory as String.
	 * 
	 * @param baseDir
	 *            Base directory.
	 * @param filename
	 *            Name of a file inside the directory.
	 * 
	 * @return File path and name inside the base directory.
	 */
	public final String getCanonicalPathAndFilename(final File baseDir,
			final String filename) {
		try {
			final File dir = new File(baseDir, path);
			final File destFile = new File(dir, filename);
			return destFile.getCanonicalPath();
		} catch (final IOException ex) {
			throw new RuntimeException(ex);
		}
	}

	/**
	 * Returns a relative path and filename. A slash ("/") is used as path
	 * separator.
	 * 
	 * @param filename
	 *            Name of a file inside the directory.
	 * 
	 * @return Path and filename.
	 */
	public final String getRelativeSlashPathAndFilename(final String filename) {
		return getSlashPath() + "/" + filename;
	}

	/**
	 * Returns the path with a slash ("/") used as path separator.
	 * 
	 * @return Path.
	 */
	public final String getSlashPath() {
		return path.replace(File.separatorChar, '/');
	}

	/**
	 * Returns the absolute file based on a given base directory.
	 * 
	 * @param baseDir
	 *            Base directory.
	 * @param filename
	 *            Name of a file inside the directory.
	 * 
	 * @return File inside the base directory.
	 */
	public final File getDestFile(final File baseDir, final String filename) {
		final String name = getCanonicalPathAndFilename(baseDir, filename);
		return new File(name);
	}

	/**
	 * Creates an XML representation of the object.
	 * 
	 * @return XML for element "dir"
	 */
	public final String toXML() {
		return "<dir path=\"" + getSlashPath() + "\" srcPathUrl=\""
				+ Utils.escapeXml(srcPathUrl) + "\"" + "/>";
	}

	/**
	 * {@inheritDoc}
	 */
	public final int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((path == null) ? 0 : path.hashCode());
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	public final boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final SrcDir other = (SrcDir) obj;
		if (path == null) {
			if (other.path != null) {
				return false;
			}
		} else if (!path.equals(other.path)) {
			return false;
		}
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	public final String toDebugString() {
		final StringBuffer sb = new StringBuffer();
		sb.append("path=" + path + ", ");
		sb.append("srcPathUrl=" + srcPathUrl);
		return sb.toString();
	}

	/**
	 * {@inheritDoc}
	 */
	public final String toString() {
		return getSlashPath();
	}

}
