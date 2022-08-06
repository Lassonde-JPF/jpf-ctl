/*
 * Copyright (C)  2022
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see https://www.gnu.org/licenses/.
 */

package jpf.logic;

import java.util.Set;

/**
 * A temporal logic formula.
 * 
 * @author Neena Govindhan
 * @author Jonas Laya
 * @author Jessie Leung
 * @author Paul Sison
 * @author Franck van Breugel
 */
public interface Formula {
	
	/**
	 * Returns the hashcode of this formula.
	 * 
	 * @return the hashcode of this formula
	 */
	public int hashCode();
	
	/**
	 * Tests whether this formula is equal to the given object.
	 * 
	 * @param object an object
	 * @return true if this formula is equal to the given object, false otherwise
	 */
	public boolean equals(Object object);
	
	/**
	 * Returns a string representation of this formula.
	 * 
	 * @return a string representation of this formula
	 */
	public String toString();
	
	/**
	 * Returns the set of aliases of this formula.
	 * 
	 * @return the set of aliases of this formula
	 */
	public Set<String> getAliases();
	
	/**
	 * Returns a simplified formula that is equivalent to this formula.
	 * 
	 * @return a simplified formula that is equivalent to this formula
	 */
	public Formula simplify();
}
