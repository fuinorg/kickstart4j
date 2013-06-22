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

import org.fuin.utils4j.ToDebugStringCapable;
import org.fuin.utils4j.Utils4J;

/**
 * A directory inside a base directory. This class is immutable.
 */
public final class MkDir implements ToDebugStringCapable {

    /** Path relative to the base directory. */
    private final String path;

    /**
     * Constructor with base directory and directory.
     * 
     * @param baseDir
     *            Base directory for calculating the relative path.
     * @param dir
     *            Directory inside the base directory.
     */
    public MkDir(final File baseDir, final File dir) {
        this(Utils4J.getRelativePath(baseDir, dir).replace(File.separatorChar, '/'));
    }

    /**
     * Constructor with all arguments.
     * 
     * @param path
     *            Path relative to the base directory.
     */
    public MkDir(final String path) {
        super();
        if (path == null) {
            this.path = "";
        } else {
            this.path = path.replace('/', File.separatorChar);
        }
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
     * Returns the absolute directory based on a given base directory as String.
     * 
     * @param baseDir
     *            Base directory.
     * 
     * @return Path inside the base directory.
     */
    public final String getCanonicalPath(final File baseDir) {
        try {
            final File dir = new File(baseDir, path);
            return dir.getCanonicalPath();
        } catch (final IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Returns the absolute file based on a given base directory.
     * 
     * @param baseDir
     *            Base directory.
     * 
     * @return File inside the base directory.
     */
    public final File getDestDir(final File baseDir) {
        final String name = getCanonicalPath(baseDir);
        return new File(name);
    }

    /**
     * Returns the path with a slash ("/") used as separator.
     * 
     * @return Path.
     */
    public final String getSlashPath() {
        return path.replace(File.separatorChar, '/');
    }

    /**
     * Creates an XML representation of the object.
     * 
     * @return XML for element "mkdir"
     */
    public final String toXML() {
        return "<mkdir path=\"" + getSlashPath() + "\"" + "/>";
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
        final MkDir other = (MkDir) obj;
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
        return path;
    }

    /**
     * {@inheritDoc}
     */
    public final String toString() {
        return getSlashPath();
    }
    
}
