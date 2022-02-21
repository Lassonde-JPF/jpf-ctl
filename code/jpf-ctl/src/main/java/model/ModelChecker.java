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

package model;

import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;

import formulas.Formula;

/**
 * A model checker for a partial transition system.
 * 
 * @author Matt Walker
 * @author Franck van Breugel
 */
public abstract class ModelChecker {

	/**
	 * A partial transition system.
	 */
	protected final TransitionSystem system;

	/**
	 * A cache of lower- and upperbounds of the satisfaction set for formulas.
	 */
	protected Map<Formula, Result> cache;

	/**
	 * Initializes this model checker with the given partial transition system.
	 * 
	 * @param system a partial transition system
	 */
	public ModelChecker(TransitionSystem system) {
		this.system = system;
		this.cache = new HashMap<Formula, Result>();
	}

	/**
	 * Checks whether the given formula holds for partial transition system
	 * of this model checker
	 * 
	 * @param formula a formula
	 * @return the lower- and upperbounds of the satisfaction set for the
	 * given formula
	 */
	public abstract Result check(Formula formula);

	/**
	 * Tests whether the smaller set is a subset of the larger set.
	 * 
	 * @param smaller a set
	 * @param bigger  a set
	 * @return true if the smaller set is a subset of the larger set, false
	 *         otherwise
	 */
	protected static boolean subset(BitSet smaller, BitSet bigger) {
		BitSet copy = (BitSet) bigger.clone();
		copy.and(smaller);
		return smaller.equals(copy);
	}
}
