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

package jpf.logic.ltl;

import java.util.HashSet;
import java.util.Set;

/**
 * This class represents an LTL formula that is an until of two formulas.
 * 
 * @author Neena Govindhan
 * @author Jonas Laya
 * @author Jessie Leung
 * @author Paul Sison
 * @author Franck van Breugel
 * @author Anto Nanah Ji
 */
public class Until extends Formula {
	private Formula left;
	private Formula right;

	/**
	 * Initializes this LTL formula as the until of the given {@code left} and {@code right} subformulas.
	 * 
	 * @param left the left subformula of this until formula
	 * @param right the right subformula of this until formula
	 */
	public Until(Formula left, Formula right) {
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
			Until other = (Until) object;
			return this.left.equals(other.left) && this.right.equals(other.right);
		} else {
			return false;
		}
	}

	@Override
	public String toString() {
		return "(" + this.left + " U " + this.right + ")";
	}

	/**
	 * Returns the left subformula of this formula.
	 * 
	 * @return the left subformula of this formula
	 */
	public Formula getLeft() {
		return this.left;
	}

	/**
	 * Returns the right subformula of this formula.
	 * 
	 * @return the right subformula of this formula
	 */
	public Formula getRight() {
		return this.right;
	}  
	
	@Override
	public Set<String> getAliases() {
		Set<String> set = new HashSet<String>();
		set.addAll(this.left.getAliases());
		set.addAll(this.right.getAliases());
		return set;
	}
	
	@Override
	public Formula simplify() {
		Formula left = this.left.simplify();
		Formula right = this.right.simplify();
		if (right instanceof True) {
			return new True();
		} else if (right instanceof False) {
			return new False();
		} else {
			return new Until(left, right);
		}
	}
}
