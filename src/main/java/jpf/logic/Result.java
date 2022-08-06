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
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package jpf.logic;

import java.util.BitSet;

/**
 * Result of model checking consists of two sets: a lower- and upperbound of the
 * satisfaction set of a formula.
 * If a state is in the lowerbound then the formula holds in that state.
 * If a state is not in the upperbound then the formula does not hold in that state.
 * If a state is not in the lowerbound but is in the upperbound then the partial
 * transition system has insufficient information to determine whether the
 * formula holds in that state.
 * 
 * @author Franck van Breugel
 */
public class Result {
	private BitSet lower;
	private BitSet upper;

	/**
	 * Initializes this result with the given lower- and upperbound.
	 * 
	 * @param lower the lowerbound of this result
	 * @param upper the upperbound of this result
	 */
	public Result(BitSet lower, BitSet upper) {
		this.lower = lower;
		this.upper = upper;
	}

	/**
	 * Returns the lowerbound of this result.
	 * 
	 * @return the lowerbound of this result
	 */
	public BitSet getLower() {
		return (BitSet) this.lower.clone();
	}

	/**
	 * Returns the upperbound of this result.
	 * 
	 * @return the upperbound of this result
	 */
	public BitSet getUpper() {
		return (BitSet) this.upper.clone();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		return prime * this.lower.hashCode() + this.upper.hashCode();
	}

	@Override
	public boolean equals(Object object) {
		if (object != null && this.getClass() == object.getClass()) {
			Result other = (Result) object;
			return this.lower.equals(other.lower) && this.upper.equals(other.upper);
		} else {
			return false;
		}
	}

	@Override
	public String toString() {
		return "Lowerbound: " + this.lower + "\n" + "Upperbound: " + this.upper;
	}		
}
