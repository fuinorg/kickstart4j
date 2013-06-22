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

import java.net.MalformedURLException;
import java.net.URL;

import org.fuin.utils4j.Utils4J;

/**
 * Remote file.
 */
public final class RemoteFile {

    /** URL of the remote file. */
    private final URL srcFileUrl;

    /** The (relative) path at the destination location. */
    private final String destPath;

    /** The filename at the destination location (without path). */
    private final String destFilename;

    /**
     * The copy process will be aborted when this file was not found at the
     * <code>srcFileUrl</code>.
     */
    private final boolean errorIfNotFound;

    /** MD5 hash of the file or <code>null</code> for automatic calculation. */
    private final String md5Hash;

    /** Length of the file or zero for automatic calculation. */
    private final long length;

    /**
     * Constructor with all arguments.
     * 
     * @param srcFileUrlStr
     *            URL of the remote file as String.
     * @param destPath
     *            The (relative) path at the destination location.
     * @param destFilename
     *            The filename at the destination location (without path).
     * @param errorIfNotFound
     *            The copy process will be aborted when this file was not found
     *            at the <code>srcFileUrl</code>.
     * 
     * @throws MalformedURLException
     *             The <code>srcFileUrl</code> cannot be converted into an
     *             <code>URL</code>.
     */
    public RemoteFile(final String srcFileUrlStr, final String destPath, final String destFilename,
            final boolean errorIfNotFound) throws MalformedURLException {
        this(new URL(srcFileUrlStr), destPath, destFilename, errorIfNotFound);
    }

    /**
     * Constructor with all arguments.
     * 
     * @param srcFileUrl
     *            URL of the remote file.
     * @param destPath
     *            The (relative) path at the destination.
     * @param destFilename
     *            The filename at the destination (without path).
     * @param errorIfNotFound
     *            The copy process will be aborted when this file was not found
     *            at the <code>srcFileUrl</code>.
     */
    public RemoteFile(final URL srcFileUrl, final String destPath, final String destFilename,
            final boolean errorIfNotFound) {
        this(srcFileUrl, destPath, destFilename, errorIfNotFound, null, 0);
    }

    /**
     * Constructor with all arguments.
     * 
     * @param srcFileUrl
     *            URL of the remote file.
     * @param destPath
     *            The (relative) path at the destination.
     * @param destFilename
     *            The filename at the destination (without path).
     * @param errorIfNotFound
     *            The copy process will be aborted when this file was not found
     *            at the <code>srcFileUrl</code>.
     * @param md5Hash
     *            MD5 hash of the file or <code>null</code> for automatic
     *            calculation. You have to set both values <code>md5Hash</code>
     *            and <code>length</code> to avoid a download of the file for
     *            automatic determination of hash and length.
     * @param length
     *            Length of the file or zero for automatic calculation. You have
     *            to set both values <code>md5Hash</code> and
     *            <code>length</code> to avoid a download of the file for
     *            automatic determination of hash and length
     */
    public RemoteFile(final URL srcFileUrl, final String destPath, final String destFilename,
            final boolean errorIfNotFound, final String md5Hash, final long length) {
        super();

        Utils4J.checkNotNull("srcFileUrl", srcFileUrl);
        Utils4J.checkNotNull("destPath", destPath);
        Utils4J.checkNotNull("destFilename", destFilename);

        this.srcFileUrl = srcFileUrl;
        this.destPath = destPath;
        this.destFilename = destFilename;
        this.errorIfNotFound = errorIfNotFound;
        this.md5Hash = md5Hash;
        this.length = length;
    }

    /**
     * Returns the URL of the remote file.
     * 
     * @return Source file URL.
     */
    public final URL getSrcFileUrl() {
        return srcFileUrl;
    }

    /**
     * Returns the (relative) path at the destination.
     * 
     * @return Path.
     */
    public final String getDestPath() {
        return destPath;
    }

    /**
     * Returns the (relative) path at the destination.
     * 
     * @return Path.
     */
    public final String getDestSlashPath() {
        return destPath;
    }

    /**
     * Returns the filename at the destination.
     * 
     * @return Filename (without path).
     */
    public final String getDestFilename() {
        return destFilename;
    }

    /**
     * Returns the MD5 hash of the file.
     * 
     * @return Hash or <code>null</code> if the hash should be calculated
     *         automatically.
     */
    public final String getMd5Hash() {
        return md5Hash;
    }

    /**
     * Returns the size of the file.
     * 
     * @return File length or zero if the size should be calculated
     *         automatically.
     */
    public final long getLength() {
        return length;
    }

    /**
     * Returns if the copy process will be aborted when this file was not found
     * at the <code>srcFileUrl</code>.
     * 
     * @return If an exception is throws when not found <code>true</code> else
     *         <code>false</code>.
     */
    public final boolean isErrorIfNotFound() {
        return errorIfNotFound;
    }

}
