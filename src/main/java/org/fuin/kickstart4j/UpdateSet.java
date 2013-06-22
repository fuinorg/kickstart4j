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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.fuin.utils4j.Utils4J;

/**
 * Set of update operations based on a list fo source files and a local
 * directory.
 */
public final class UpdateSet {

	private final File destDir;

	private final List newFiles = new ArrayList();

	private final List changedFiles = new ArrayList();

	private final List deletedFiles = new ArrayList();

	private final List unchangedFiles = new ArrayList();

	private final List decompressFiles = new ArrayList();

	private final List classpathJarFiles = new ArrayList();

	private final List mkDirs = new ArrayList();

	private final List orderList = new ArrayList();

	/**
	 * Constructor with source URL and destination directory.
	 * 
	 * @param srcFiles
	 *            List of <code>SrcFile</code> objects - Cannot be
	 *            <code>null</code>.
	 * @param mkDirs
	 *            List of <code>MkDir</code> objects - Cannot be
	 *            <code>null</code>.
	 * @param destDir
	 *            Destination directory - Cannot be <code>null</code> and must
	 *            exist!
	 * @param lazyLoading
	 *            If lazy loading is active <code>true</code> else
	 *            <code>false</code>.
	 */
	public UpdateSet(final List srcFiles, final List mkDirs,
			final File destDir, final boolean lazyLoading) {
		super();

		Utils4J.checkNotNull("srcFiles", srcFiles);
		Utils4J.checkNotNull("mkDirs", mkDirs);
		Utils4J.checkNotNull("destDir", destDir);
		Utils4J.checkValidDir(destDir);
		this.destDir = destDir;

		this.mkDirs.addAll(mkDirs);

		for (int i = 0; i < srcFiles.size(); i++) {
			final SrcFile srcFile = (SrcFile) srcFiles.get(i);

			final Integer order = new Integer(srcFile.getOrder());
			if (!orderList.contains(order)) {
				orderList.add(order);
			}

			if ((!lazyLoading)
					|| (lazyLoading && (srcFile.isLoadAlways() || srcFile
							.isAddToClasspath()))) {

				final File dir = new File(destDir, srcFile.getPath());
				final File dest = new File(dir, srcFile.getFilename());
				if (dest.exists()) {
					handleExistingFile(srcFile, dest);
				} else {
					handleNewFile(srcFile);
				}
				if (srcFile.isAddToClasspath()) {
					classpathJarFiles.add(srcFile);
				}

			}
		}

		Collections.sort(orderList);

	}

	/**
	 * Adds a new file to the appropriate lists.
	 * 
	 * @param srcFile
	 *            New file to add.
	 */
	private void handleNewFile(final SrcFile srcFile) {
		newFiles.add(srcFile);
		if (srcFile.isUnzip()) {
			decompressFiles.add(srcFile);
		}
	}

	/**
	 * Adds an existing file to the appropriate lists.
	 * 
	 * @param srcFile
	 *            Source file to add.
	 * @param dest
	 *            Corresponding local file.
	 */
	private void handleExistingFile(final SrcFile srcFile, final File dest) {
		final String destHash = Utils4J.createHashMD5(dest);
		if (srcFile.getMd5Hash().equals(destHash)) {
			unchangedFiles.add(srcFile);
		} else {
			changedFiles.add(srcFile);
			if (srcFile.isUnzip()) {
				decompressFiles.add(srcFile);
			}
		}
	}

	/**
	 * Returns the destination directory.
	 * 
	 * @return Local program directory.
	 */
	public final File getDestDir() {
		return destDir;
	}

	/**
	 * Returns a list of files that are new.
	 * 
	 * @return List of <code>SrcFile</code> that are not present in the local
	 *         directory.
	 */
	public final List getNewFiles() {
		return newFiles;
	}

	/**
	 * Returns a list of files that are new and have a given order.
	 * 
	 * @param order
	 *            Order number.
	 * 
	 * @return List of <code>SrcFile</code> that are not present in the local
	 *         directory.
	 */
	public final List getNewFiles(final int order) {
		final List list = new ArrayList();
		for (int i = 0; i < newFiles.size(); i++) {
			final SrcFile srcFile = (SrcFile) newFiles.get(i);
			if (srcFile.getOrder() == order) {
				list.add(srcFile);
			}
		}
		return list;
	}

	/**
	 * Returns a list of files that changed.
	 * 
	 * @return List of <code>SrcFile</code> with a difference between remote and
	 *         local directory.
	 */
	public final List getChangedFiles() {
		return changedFiles;
	}

	/**
	 * Returns a list of files that changed and have a given order.
	 * 
	 * @param order
	 *            Order number.
	 * 
	 * @return List of <code>SrcFile</code> with a difference between remote and
	 *         local directory.
	 */
	public final List getChangedFiles(final int order) {
		final List list = new ArrayList();
		for (int i = 0; i < changedFiles.size(); i++) {
			final SrcFile srcFile = (SrcFile) changedFiles.get(i);
			if (srcFile.getOrder() == order) {
				list.add(srcFile);
			}
		}
		return list;
	}

	/**
	 * Returns a list of files that are present in the local directory but no
	 * longer needed.
	 * 
	 * @return List of <code>SrcFile</code> no longer present on remote
	 *         directory.
	 */
	public final List getDeletedFiles() {
		return deletedFiles;
	}

	/**
	 * Returns a list of files that are present in the local directory but no
	 * longer needed and have a given order.
	 * 
	 * @param order
	 *            Order number.
	 * 
	 * @return List of <code>SrcFile</code> no longer present on remote
	 *         directory.
	 */
	public final List getDeletedFiles(final int order) {
		final List list = new ArrayList();
		for (int i = 0; i < deletedFiles.size(); i++) {
			final SrcFile srcFile = (SrcFile) deletedFiles.get(i);
			if (srcFile.getOrder() == order) {
				list.add(srcFile);
			}
		}
		return list;
	}

	/**
	 * Returns a list of files that haven't changed.
	 * 
	 * @return List of <code>SrcFile</code> without a difference between remote
	 *         and local directory.
	 */
	public final List getUnchangedFiles() {
		return unchangedFiles;
	}

	/**
	 * Returns a list of files that haven't changed and have a given order.
	 * 
	 * @param order
	 *            Order number.
	 * 
	 * @return List of <code>SrcFile</code> without a difference between remote
	 *         and local directory.
	 */
	public final List getUnchangedFiles(final int order) {
		final List list = new ArrayList();
		for (int i = 0; i < unchangedFiles.size(); i++) {
			final SrcFile srcFile = (SrcFile) unchangedFiles.get(i);
			if (srcFile.getOrder() == order) {
				list.add(srcFile);
			}
		}
		return list;
	}

	/**
	 * Returns a list of files that needs to be decompressed after the copy.
	 * 
	 * @return List of <code>SrcFile</code> that are unzipped at the destination
	 *         directory.
	 */
	public final List getDecompressFiles() {
		return decompressFiles;
	}

	/**
	 * Returns a list of files that needs to be decompressed after the copy and
	 * have a given order.
	 * 
	 * @param order
	 *            Order number.
	 * 
	 * @return List of <code>SrcFile</code> that are unzipped at the destination
	 *         directory.
	 */
	public final List getDecompressFiles(final int order) {
		final List list = new ArrayList();
		for (int i = 0; i < decompressFiles.size(); i++) {
			final SrcFile srcFile = (SrcFile) decompressFiles.get(i);
			if (srcFile.getOrder() == order) {
				list.add(srcFile);
			}
		}
		return list;
	}

	/**
	 * Returns a list of jar files needed for starting the target application.
	 * 
	 * @return List of <code>SrcFile</code> that are added to the classpath.
	 */
	public final List getClasspathJarFiles() {
		return classpathJarFiles;
	}

	/**
	 * Returns a list of directories to create at the target location.
	 * 
	 * @return List of <code>MkDir</code> objects.
	 */
	public final List getMkDirs() {
		return mkDirs;
	}

	/**
	 * Returns a list of order numbers included in "newFiles", "changedFiles",
	 * "unchangedFiles" or "decompressFiles".
	 * 
	 * @return Sorted list of order numbers (lowest to highest).
	 */
	public final List getOrderList() {
		return orderList;
	}

	/**
	 * Checks if there is are new or changed files or files to be deleted.
	 * 
	 * @return If an update needs to be performed <code>true</code> else
	 *         <code>false</code>.
	 */
	public final boolean isUpdateNecessary() {
		return (newFiles.size() + changedFiles.size() + deletedFiles.size()) > 0;
	}

	/**
	 * Creates a classpath from the entries in the
	 * <code>classpathJarFiles</code> list.
	 * 
	 * @return List of JAR-files.
	 */
	public final String createClasspath() {
		final StringBuffer sb = new StringBuffer();
		for (int i = 0; i < classpathJarFiles.size(); i++) {
			final SrcFile srcFile = (SrcFile) classpathJarFiles.get(i);
			if (i > 0) {
				sb.append(File.pathSeparatorChar);
			}
			sb.append("\"");
			sb.append(srcFile.getRelativeSlashPathAndFilename());
			sb.append("\"");
		}
		return sb.toString();
	}

}
