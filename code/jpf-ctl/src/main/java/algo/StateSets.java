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

package algo;

import java.util.Set;

/**
 * A class which represents a pair of sets of states.  The states are 
 * represented by integers.  The first set is used to capture those
 * states that satisfy a given CTL formula, whereas the second set is used 
 * to capture those states that do not satisfy the CTL formula.
 * 
 * @author Franck van Breugel
 * @author Matt Walker
 */
public class StateSets {
	private final BitSet sat;
	private final BitSet unsat;

	/**
	 * Initializes this object with the given sets of states.
	 * 
	 * @param sat the set of states that satisfy a given CTL formula
	 * @param unsat the set of states that do not satisfy the CTL formula
	 */
	public StateSets(BitSet sat, BitSet unsat) {
		this.sat = sat;
		this.unsat = unsat;
	}

	/**
	 * Returns the set of states that satisfy a given CTL formula.
	 * 
	 * @return set of states that satisfy a given CTL formula
	 */
	public BitSet getSat() {
		return this.sat;
	}

	/**
	 * Returns the set of states that do not satisfy a given CTL formula.
	 * 
	 * @return set of states that do not satisfy a given CTL formula
	 */
	public BitSet getUnsat() {
		return this.unsat;
	}

	@Override
	public boolean equals(Object object) {
		if (object != null && this.getClass() == object.getClass()) {
			StateSets other = (StateSets) object;
			return this.sat.equals(other.sat) && this.unsat.equals(other.unsat);
		} else {
			return false;
		}
	}

	@Override
	public String toString() {
		return "sat = " + sat + "\nunsat = " + unsat;
	}
}
