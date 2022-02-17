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

import java.util.Random;
import java.util.Set;

import formulas.ctl.And;
import formulas.ctl.AtomicProposition;
import formulas.ctl.ExistsAlways;
import formulas.ctl.ExistsEventually;
import formulas.ctl.ExistsNext;
import formulas.ctl.ExistsUntil;
import formulas.ctl.False;
import formulas.ctl.ForAllAlways;
import formulas.ctl.ForAllEventually;
import formulas.ctl.ForAllNext;
import formulas.ctl.ForAllUntil;
import formulas.ctl.Iff;
import formulas.ctl.Implies;
import formulas.ctl.Not;
import formulas.ctl.Or;
import formulas.ctl.True;

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
	private static final Random RANDOM = new Random();
	
	/**
	 * Returns a random formula of at most the given depth.
	 * 
	 * @param the maximum depth of the formula
	 * @return a random formula of at most the given depth
	 */
	public static Formula random(int depth) {
		final int BASE_CASES = 3;
		final int INDUCTIVE_CASES = 13;
		final int MAX_INDEX = 4;
		
		if (depth == 0) {
			switch (RANDOM.nextInt(BASE_CASES)) {
			case 0 :
				return new True();
			case 1 :
				return new False();
			case 2 :
				String alias = "a" + RANDOM.nextInt(MAX_INDEX+1);
				return new AtomicProposition(alias);
			default :
				throw new IllegalArgumentException("Illegal argument for switch in base case");
			}
		} else {
			switch (RANDOM.nextInt(BASE_CASES + INDUCTIVE_CASES)) {
			case 0 :
				return new True();
			case 1 :
				return new False();
			case 2 :
				String alias = "b" + RANDOM.nextInt(MAX_INDEX+1);
				return new AtomicProposition(alias);
			case 3 :
				return new Not(Formula.random(depth - 1));
			case 4: 
				return new And(Formula.random(depth - 1), Formula.random(depth - 1));
			case 5 :
				return new Or(Formula.random(depth - 1), Formula.random(depth - 1));
			case 6 :
				return new Implies(Formula.random(depth - 1), Formula.random(depth - 1));
			case 7 :
				return new Iff(Formula.random(depth - 1), Formula.random(depth - 1));
			case 8 :
				return new ExistsAlways(Formula.random(depth - 1));
			case 9 :
				return new ForAllAlways(Formula.random(depth - 1));
			case 10 :
				return new ExistsEventually(Formula.random(depth - 1));
			case 11 :
				return new ForAllEventually(Formula.random(depth - 1));
			case 12 :
				return new ExistsNext(Formula.random(depth - 1));
			case 13 :
				return new ForAllNext(Formula.random(depth - 1));
			case 14 :
				return new ExistsUntil(Formula.random(depth - 1), Formula.random(depth - 1));
			case 15 :
				return new ForAllUntil(Formula.random(depth - 1), Formula.random(depth - 1));
			default :
				throw new IllegalArgumentException("Illegal argument for switch in inductive case");
			}
		}
	}
	
	private static final int DEFAULT_DEPTH = 5;
	
	/**
	 * Returns a random formula.
	 * 
	 * @return a random formula 
	 */
	public static Formula random() {
		return Formula.random(Formula.DEFAULT_DEPTH);
	}	

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
