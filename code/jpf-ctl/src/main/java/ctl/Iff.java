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

package ctl;

/**
 * This class represents a CTL formula that is an equivalence (iff) of two formulas.
 * 
 * @author Neena Govindhan
 * @author Jonas Laya
 * @author Jessie Leung
 * @author Paul Sison
 * @author Franck van Breugel
 */
public class Iff extends Formula {
	private Formula left;
	private Formula right;
	
	/**
	 * Initializes this CTL formula as the equivalence (iff) of the given {@code left} and {@code right} subformulas.
	 * 
	 * @param left the left subformula of this iff formula
	 * @param right the right subformula of this iff formula
	 */
	public Iff(Formula left, Formula right) {
		this.left = left;
		this.right = right;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int hashCode = 1;
		hashCode = prime * hashCode + this.left.hashCode();
		hashCode = prime * hashCode + this.right.hashCode();
		return hashCode;
	}

	@Override
	public boolean equals(Object object) {
		if (object != null && this.getClass() == object.getClass()) {
			Iff other = (Iff) object;
			return this.left.equals(other.left) && this.right.equals(other.right);
		} else {
			return false;
		}
	}

	@Override
	public String toString() {
		return "(" + this.left + " <-> " + this.right + ")";
	}
	
	public Formula getLeft()
	{
    return left;
    }

	public Formula getRight()
	{
    return right;
 	}  
}
