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

import org.fuin.utils4swing.progress.FileCopyProgressListener;

/**
 * Supports loading files from a source location into a local directory.
 */
public interface FileLoader {

	/**
	 * Delete all files in the given directory.
	 * 
	 * @param path
	 *            Path inside the current directory - Cannot be
	 *            <code>null</code> or empty (it's not allowed to delete the
	 *            base directory!=.
	 * 
	 * @throws DeleteException
	 *             There was a problem deleting the files.
	 */
	public void deletePath(String path) throws DeleteException;

	/**
	 * Delete a file in the given directory.
	 * 
	 * @param path
	 *            Path inside the current directory - Cannot be
	 *            <code>null</code> but may be empty.
	 * @param filename
	 *            Filename (without path) - Cannot be <code>null</code> or empty
	 * 
	 * @throws DeleteException
	 *             There was a problem deleting the file.
	 */
	public void deleteFile(String path, String filename) throws DeleteException;

	/**
	 * Loads a file from a source into a local directory.If the file is already
	 * up to date nothing will be copied.
	 * 
	 * @param path
	 *            Path inside the current directory.
	 * @param filename
	 *            Filename (without path).
	 * 
	 * @return File reference.
	 * 
	 * @throws LoadingFileException
	 *             Loading the file failed for some reason.
	 */
	public File loadFile(String path, String filename) throws LoadingFileException;

	/**
	 * Loads a file from a source into a local directory.If the file is already
	 * up to date nothing will be copied.
	 * 
	 * @param path
	 *            Path inside the current directory.
	 * @param filename
	 *            Filename (without path).
	 * @param listener
	 *            Listener to inform about progress - Can be <code>null</code>
	 *            if no progress information is needed.
	 * 
	 * @return File reference.
	 * 
	 * @throws LoadingFileException
	 *             Loading the file failed for some reason.
	 */
	public File loadFile(String path, String filename, FileCopyProgressListener listener)
			throws LoadingFileException;

}
