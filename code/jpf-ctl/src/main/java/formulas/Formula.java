/*
 * Copyright (C)  2021
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
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package formulas;

import java.util.Set;

/**
 * This class represents the CTL formula.
 * 
 * @author Neena Govindhan
 * @author Jonas Laya
 * @author Jessie Leung
 * @author Paul Sison
 * @author Franck van Breugel
 */
public abstract class Formula {

	/**
	 * Returns the hashcode of this formula.
	 * 
	 * @return the hashcode of this formula
	 */
	public abstract int hashCode();
	
	/**
	 * Tests whether this formula is equal to the given object.
	 * 
	 * @param object an object
	 * @return true if this formula is equal to the given object, false otherwise
	 */
	public abstract boolean equals(Object object);
	
	/**
	 * Returns a string representation of this formula.
	 * 
	 * @return a string representation of this formula
	 */
	public abstract String toString();
	
	/**
	 * Returns the set of names of the atomic propositions of this formula.
	 * 
	 * @return the set of names of the atomic propositions of this formula
	 */
	public abstract Set<String> getAtomicPropositions();
	
	/**
	 * Returns a simplified formula that is equivalent to this formula.
	 * 
	 * @return a simplified formula that is equivalent to this formula
	 */
	public abstract Formula simplify();
}
