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

package model;

/**
 * A class which represents a transition between two states.
 * 
 * @author Franck van Breugel
 * @author Matt Walker
 */
public class Transition {
	public final int source;
	public final int target;

	/**
	 * 
	 * Initializes this transition with the given source and target nodes.
	 * 
	 * @param source the source node of this transition
	 * @param target the target node of this transition
	 */
	public Transition(int source, int target) {
		this.source = source;
		this.target = target;
	}

	/**
	 * Returns the source of this transition.
	 * 
	 * @return the source of this transition
	 */
	public int getSource() {
		return this.source;
	}

	/**
	 * Returns the target of this transition.
	 * 
	 * @return the target of this transition
	 */
	public int getTarget() {
		return this.target;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		return prime * this.source + this.target;
	}

	@Override
	public boolean equals(Object object) {
		if (object != null && this.getClass() == object.getClass()) {
			Transition other = (Transition) object;
			return this.source == other.source && this.target == other.target;
		} else {
			return false;
		}
	}

	@Override
	public String toString() {
		return this.source + " -> " + this.target;
	}
}