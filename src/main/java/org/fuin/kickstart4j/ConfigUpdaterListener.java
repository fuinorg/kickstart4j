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

/**
 * Listens to <code>ConfigUpdater</code> progress events.
 */
public interface ConfigUpdaterListener {

	/**
	 * A file is being copied.
	 * 
	 * @param remoteFile
	 *            Remote file.
	 * @param file
	 *            Local file.
	 * @param nr
	 *            Current file number.
	 * @param max
	 *            Max number of files.
	 */
	public void onCopy(final RemoteFile remoteFile, final File file,
			final int nr, final int max);

	/**
	 * A remote file was not found.
	 * 
	 * @param remoteFile
	 *            Remote file.
	 * @param file
	 *            Local file.
	 * @param nr
	 *            Current file number.
	 * @param max
	 *            Max number of files.
	 */
	public void onNotFound(final RemoteFile remoteFile, final File file,
			final int nr, final int max);

}
