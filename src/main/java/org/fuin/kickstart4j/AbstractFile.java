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

import org.fuin.utils4j.Utils4J;

/**
 * A file inside a base directory. This class is immutable.
 */
public abstract class AbstractFile {

    /** Filename without path. */
    private final String filename;

    /** Path relative to the base directory. */
    private final String path;

    /** MD5 hash code of the file. */
    private final String md5Hash;

    /**
     * Constructor with base directory and file.
     * 
     * @param baseDir
     *            Base directory for calculating the relative path.
     * @param file
     *            File inside the base directory.
     */
    public AbstractFile(final File baseDir, final File file) {
        this(Utils4J.getRelativePath(baseDir, file.getParentFile())
                .replace(File.separatorChar, '/'), file.getName(), Utils4J.createHashMD5(file));
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
     */
    public AbstractFile(final String path, final String filename, final String md5Hash) {
        super();
        if (path == null) {
            this.path = "";
        } else {
            this.path = path.replace('/', File.separatorChar);
        }

        if (filename == null) {
            throw new IllegalArgumentException("The argument 'filename' cannot be null!");
        }
        this.filename = filename;

        if (md5Hash == null) {
            throw new IllegalArgumentException("The argument 'md5Hash' cannot be null!");
        }
        this.md5Hash = md5Hash;

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
     * Returns the filename.
     * 
     * @return Filename without path.
     */
    public final String getFilename() {
        return filename;
    }

    /**
     * Returns the hash code.
     * 
     * @return MD5 hash code of the file.
     */
    public final String getMd5Hash() {
        return md5Hash;
    }

    /**
     * Returns the absolute file based on a given base directory as String.
     * 
     * @param baseDir
     *            Base directory.
     * 
     * @return File path and name inside the base directory.
     */
    public final String getCanonicalPathAndFilename(final File baseDir) {
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
     * @return Path and filename.
     */
    public final String getRelativeSlashPathAndFilename() {
        return getSlashPath() + "/" + getFilename();
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
     * 
     * @return File inside the base directory.
     */
    public final File getDestFile(final File baseDir) {
        final String name = getCanonicalPathAndFilename(baseDir);
        return new File(name);
    }

    /**
     * {@inheritDoc}
     */
    public final int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((filename == null) ? 0 : filename.hashCode());
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
        final AbstractFile other = (AbstractFile) obj;
        if (filename == null) {
            if (other.filename != null) {
                return false;
            }
        } else if (!filename.equals(other.filename)) {
            return false;
        }
        if (path == null) {
            if (other.path != null) {
                return false;
            }
        } else if (!path.equals(other.path)) {
            return false;
        }
        return true;
    }

}
