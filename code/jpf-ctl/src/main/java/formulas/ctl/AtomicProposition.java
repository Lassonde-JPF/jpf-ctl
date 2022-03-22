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

package formulas.ctl;

import java.util.HashSet;
import java.util.Set;

/**
 * This class represents the CTL state formula consisting of an atomic proposition.
 * 
 * @author Neena Govindhan
 * @author Jonas Laya
 * @author Jessie Leung
 * @author Paul Sison
 * @author Franck van Breugel
 * @author Matthew Walker
 */
public class AtomicProposition extends CTLFormula {
	private String alias;

	/**
	 * Initializes this CTL formula as an atomic proposition with the given name.
	 * 
	 * @param name the name of this atomic proposition
	 */
	public AtomicProposition(String alias) {
		this.alias = alias;
	}
	
	@Override
	public int hashCode() {
		return this.alias.hashCode();
	}

	@Override
	public boolean equals(Object object) {
		if (object != null && this.getClass() == object.getClass()) {
			AtomicProposition other = (AtomicProposition) object;
			return this.alias.equals(other.alias);
		} else {
			return false;
		}
	}

	@Override
	public String toString() {
		return this.alias;
	}
	
	@Override
	public Set<String> getAtomicPropositions() {
		Set<String> set = new HashSet<String>();
		set.add(this.toString());
		return set;
	}
	
	@Override
	public CTLFormula simplify() {
		return this;
	}
}