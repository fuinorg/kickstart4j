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

import org.fuin.utils4j.ToDebugStringCapable;

/**
 * A file inside a local base directory. This class is immutable.
 */
public final class DestFile extends AbstractFile implements ToDebugStringCapable {

    /**
     * Constructor with base directory and file.
     * 
     * @param baseDir
     *            Base directory for calculating the relative path.
     * @param file
     *            File inside the base directory.
     */
    public DestFile(final File baseDir, final File file) {
        super(baseDir, file);
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
    public DestFile(final String path, final String filename, final String md5Hash) {
        super(path, filename, md5Hash);
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
        sb.append("filename=" + getFilename() + ", ");
        sb.append("md5Hash=" + getMd5Hash());
        return sb.toString();
    }

}
