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

/**
 * Object that can compare it's attributes to another object of the same type.
 */
public interface SimpleAttributesEqualsCapable {

    /**
     * Determines if all simple attributes ( {@link boolean}, {@link byte},
     * {@link short}, {@link int}, {@link long}, {@link float}, {@link double},
     * {@link char} {@link Boolean}, {@link Byte}, {@link Short},
     * {@link Integer}, {@link Long}, {@link Float}, {@link Double},
     * {@link Character} and {@link String} ) of the object are equal to the
     * fields of the other object (using the {@link Object#equals(Object)}
     * method). No complex types like lists, maps or sub-objects will be
     * compared.
     * 
     * @param theOther
     *            Object to compare with. If <code>null</code> or not an
     *            instance of this class then <code>false</code> will be
     *            returned.
     * 
     * @return If all simple attributes are equal <code>true</code> else
     *         <code>false</code>.
     */
    public boolean simpleAttributesEquals(Object theOther);

}
