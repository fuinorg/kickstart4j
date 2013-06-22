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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.fuin.utils4j.Cancelable;
import org.fuin.utils4j.CancelableVolatile;
import org.fuin.utils4j.Utils4J;
import org.fuin.utils4swing.common.Utils4Swing;
import org.fuin.utils4swing.progress.FileCopyProgressInputStream;
import org.fuin.utils4swing.progress.FileCopyProgressListener;
import org.fuin.utils4swing.progress.FileCopyProgressMonitor;

/**
 * Utilities for the package.
 */
public final class Utils {

    private static final Logger LOG = Logger.getLogger(Utils.class);

    private static final Map XML_MAP;

    static {
        XML_MAP = new HashMap();
        XML_MAP.put("\"", "quot");
        XML_MAP.put("&", "amp");
        XML_MAP.put("<", "lt");
        XML_MAP.put(">", "gt");
        XML_MAP.put("'", "apos");
    };

    /**
     * Private constructor.
     */
    private Utils() {
        throw new UnsupportedOperationException("Creating instances is not allowed!");
    }

    /**
     * Copies a file from an URL to a local destination.
     * <code>IOException</code>s are mapped into a <code>RuntimeException</code>
     * .
     * 
     * @param listener
     *            Monitor to use - Can be <code>null</code> if no progress
     *            information is needed.
     * @param srcFileUrl
     *            Source file URL.
     * @param destFile
     *            Destination file.
     * @param fileNo
     *            Number of the current file.
     * @param fileSize
     *            File size.
     * 
     * @throws FileNotFoundException
     *             The <code>srcFileUrl</code> was not found.
     */
    public static void copyURLToFile(final FileCopyProgressListener listener, final URL srcFileUrl,
            final File destFile, final int fileNo, final int fileSize) throws FileNotFoundException {

        if (listener != null) {
            listener.updateFile(srcFileUrl.toString(), destFile.toString(), fileNo, fileSize);
        }
        try {
            final InputStream input = new FileCopyProgressInputStream(listener, srcFileUrl
                    .openStream(), fileSize);
            try {
                final FileOutputStream output = FileUtils.openOutputStream(destFile);
                try {
                    IOUtils.copy(input, output);
                } finally {
                    IOUtils.closeQuietly(output);
                }
            } finally {
                IOUtils.closeQuietly(input);
            }
        } catch (final FileNotFoundException ex) {
            throw ex;
        } catch (final IOException ex) {
            throw new RuntimeException(ex);
        }

    }

    /**
     * Unzips a file into a local directory. WARNING: Only relative path entries
     * are allowed inside the archive! <code>IOException</code>s are mapped into
     * a <code>RuntimeException</code> .
     * 
     * @param listener
     *            Listener to inform - Can be <code>null</code> if no progress
     *            information is needed.
     * @param zipFile
     *            Source ZIP file - Cannot be <code>null</code> and must be a
     *            valid ZIP file.
     * @param zipFileNo
     *            Number of the zip file.
     * @param destDir
     *            Destination directory - Cannot be <code>null</code> and must
     *            exist.
     * @param cancelable
     *            Signals if the unzip should be canceled - Can be
     *            <code>null</code> if no cancel option is required.
     */
    public static void unzip(final FileCopyProgressListener listener, final File zipFile,
            final int zipFileNo, final File destDir, final Cancelable cancelable) {
        try {
            Utils4J.unzip(zipFile, destDir, new Utils4J.UnzipInputStreamWrapper() {
                public InputStream wrapInputStream(final InputStream in, final ZipEntry entry,
                        final File destFile) {
                    if (entry.getSize() > Integer.MAX_VALUE) {
                        throw new IllegalArgumentException("Cannot handle files larger than "
                                + Integer.MAX_VALUE + " bytes!");
                    }
                    if (listener != null) {
                        listener.updateFile(zipFile.toString(), destFile.toString(), zipFileNo,
                                (int) entry.getSize());
                    }
                    if (LOG.isInfoEnabled()) {
                        LOG.info("UNZIP " + zipFile + " => " + destFile);
                    }
                    return new FileCopyProgressInputStream(listener, in, (int) entry.getSize());
                }
            }, cancelable);
        } catch (final IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Copies bytes from the URL source to a file destination. The directories
     * up to destination will be created if they don't already exist.
     * destination will be overwritten if it already exists. Possible
     * <code>IOException</code>s are wrapped into <code>RuntimeException</code>.
     * 
     * @param url
     *            The URL to copy bytes from, must not be <code>null</code>. A
     *            possible <code>MalformedURLException</code> when converting
     *            this argument into an URL is wrapped into a
     *            <code>RuntimeException</code>.
     * @param file
     *            The non-directory File to write bytes to (possibly
     *            overwriting), must not be <code>null</code>.
     */
    public static void copyURLToFile(final String url, final File file) {
        try {
            FileUtils.copyURLToFile(new URL(url), file);
        } catch (final IOException ex) {
            throw new RuntimeException("Error copying URL to file!", ex);
        }
    }

    /**
     * Escapes the five basic XML entities (gt, lt, quot, amp, apos).
     * 
     * @param str
     *            Text to escape or <code>null</code>.
     * 
     * @return Escaped text or <code>null</code> if argument was
     *         <code>null</code>.
     */
    public static final String escapeXml(final String str) {
        if (str == null) {
            return null;
        }
        final StringBuffer sb = new StringBuffer();
        final int len = str.length();
        for (int i = 0; i < len; i++) {
            final char c = str.charAt(i);
            final String entityName = (String) XML_MAP.get("" + c);
            if (entityName == null) {
                if (c > 0x7F) {
                    sb.append("&#");
                    sb.append(Integer.toString(c, 10));
                    sb.append(';');
                } else {
                    sb.append(c);
                }
            } else {
                sb.append('&');
                sb.append(entityName);
                sb.append(';');
            }
        }
        return sb.toString();
    }

    /**
     * Compares two objects with their equals(..) method in a <code>null</code>
     * safe way.
     * 
     * @param obj1
     *            First object or <code>null</code>.
     * @param obj2
     *            First object or <code>null</code>.
     * 
     * @return If both objects are equal <code>true</code> else
     *         <code>false</code>.
     */
    public static boolean nullSafeEquals(final Object obj1, final Object obj2) {
        if (obj1 == null) {
            if (obj2 == null) {
                return true;
            }
            return false;
        }
        return obj1.equals(obj2);
    }

    /**
     * Main method to test the monitor. Only for testing purposes.
     * 
     * @param args
     *            Not used.
     */
    public static void main(final String[] args) {

        // This runs in the "main" thread (outside the EDT)

        // Initialize the L&F in a thread safe way
        Utils4Swing.initSystemLookAndFeel();

        // Create an cancel tracker
        final Cancelable cancelable = new CancelableVolatile();

        // Create a dummy list
        final List zipFiles = new ArrayList();
        zipFiles.add(new File("C:\\test.zip"));

        // Create the monitor dialog
        final FileCopyProgressMonitor monitor = new FileCopyProgressMonitor(cancelable,
                "Unzip Test", zipFiles.size());

        // Make the UI visible
        monitor.open();
        try {

            final long start = System.currentTimeMillis();

            // Loop through all ZIPs
            for (int i = 0; i < zipFiles.size(); i++) {
                // Check if the user canceled the copy process
                if (cancelable.isCanceled()) {
                    break;
                }
                unzip(monitor, (File) zipFiles.get(i), i + 1, new File("C:\\temp"), cancelable);
            }

            System.out.println(System.currentTimeMillis() - start);

        } finally {
            // Hide the UI
            monitor.close();
        }

    }

}
