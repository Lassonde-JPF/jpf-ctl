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

package jpf.logic;

/**
 * Model checking for partial transition systems.
 * 
 * @author Parssa Khazra
 * @author Anto Nanah Ji
 * @author Matthew Walker
 * @author Hongru Wang
 * @author Franck van Breugel
 */
public abstract class ModelChecker {

	// partial transition system
	protected PartialTransitionSystem system;

	/**
	 * Initializes this model checker with the given partial transition system.
	 * 
	 * @param system a partial transition system
	 */	
	public ModelChecker() { }

	public void setPartialTransitionSystem(PartialTransitionSystem system) {
		this.system = system;
	}
	
	/**
	 * Returns a lower- and upperbound of the satisfaction set of the given formula.
	 * 
	 * @param formula the CTL formula
	 * @return a lower- and upperbound of the satisfaction set of the given formula
	 */
	public abstract Result check(Formula formula);
}