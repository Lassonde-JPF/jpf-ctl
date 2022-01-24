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

import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;

import controllers.TransitionSystem;
import formulas.And;
import formulas.AtomicProposition;
import formulas.ExistsAlways;
import formulas.ExistsEventually;
import formulas.ExistsNext;
import formulas.ExistsUntil;
import formulas.False;
import formulas.ForAllAlways;
import formulas.ForAllEventually;
import formulas.ForAllNext;
import formulas.ForAllUntil;
import formulas.Formula;
import formulas.Iff;
import formulas.Implies;
import formulas.Not;
import formulas.Or;
import formulas.True;

/**
 * CTL model checking for partial transition systems.
 * 
 * @author Parssa Khazra
 * @author Anto Nanah Ji
 * @author Matthew Walker
 * @author Hongru Wang
 * @author Franck van Breugel
 */
public class ModelChecker {
	/**
	 * Result of model checking consists of two sets: a lower- and upperbound of the
	 * satisfaction set of the CTL formula. If a state is in the lowerbound then the
	 * CTL formula holds in that state. If a state is not in the upperbound then the
	 * CTL formula does not hold in that state. If a state is not in the lowerbound
	 * but is in the upperbound then the partial transition system has insufficient
	 * information to determine whether the CTL formula holds in that state.
	 */
	public static class Result {
		
		private static enum Status {
			VALID,
			INVALID,
			UNKNOWN;
		}
		
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

		
		/**
		 * Returns whether the corresponding pts for this result is partial
		 * 
		 * @return boolean - whether the corresponding pts is partial
		 */
		public boolean isPartial() {
			return !this.upper.equals(this.lower);
		}
		
		/**
		 * Returns whether this result is valid.
		 * 
		 * @return boolean - whether this result is valid
		 */
		public Status isValid() {
			if (this.isPartial()) {
				if (this.lower.get(0) && this.upper.get(0)) {
					return Status.VALID;
				}
				if (this.lower.get(0) || this.upper.get(0)) {
					return Status.UNKNOWN;
				} 
				return Status.INVALID;
			} else {
				if (this.lower.get(0)) {
					return Status.VALID;
				} else {
					return Status.INVALID;
				}
			}
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
			return "Result: " + this.isValid();
		}

	}

	// partial transition system
	private final TransitionSystem system;

	// alias -> jni mapping
	private final Map<String, String> jniMapping;

	// cache of lower- and upperbounds of the satisfaction set for formulas
	private Map<Formula, Result> cache;

	/**
	 * Initializes this model checker with the given partial transition system.
	 * 
	 * @param system a partial transition system
	 */
	public ModelChecker(TransitionSystem system, Map<String, String> jniMapping) {
		this.system = system;
		this.jniMapping = jniMapping;
		this.cache = new HashMap<Formula, Result>();
	}

	/**
	 * Returns a lower- and upperbound of the satisfaction set of the given CTL
	 * formula.
	 * 
	 * @param formula the CTL formula
	 * @return a lower- and upperbound of the satisfaction set of the given CTL
	 *         formula
	 */
	public Result check(Formula formula) {
		formula = formula.simplify();
		if (this.cache.containsKey(formula)) {
			return this.cache.get(formula);
		} else {
			Result result;
			if (formula instanceof True) {
				BitSet all = new BitSet();
				all.set(0, this.system.getNumberOfStates());
				result = new Result(all, all);
			} else if (formula instanceof False) {
				BitSet none = new BitSet();
				result = new Result(none, none);
			} else if (formula instanceof AtomicProposition) {
				String name = ((AtomicProposition) formula).toString();
				name = jniMapping.get(name); // TODO conversion to jni name
				BitSet labelling = new BitSet();
				if (this.system.getIndices().containsKey(name)) {
					int index = this.system.getIndices().get(name);
					if (this.system.getLabelling().containsKey(index)) {
						labelling = (BitSet) this.system.getLabelling().get(index).clone();
					}
				}
				result = new Result(labelling, (BitSet) labelling.clone());
			} else if (formula instanceof Not) {
				Not not = (Not) formula;
				Formula subformula = not.getFormula(); // TODO changed from 'getSubFormula'
				result = check(subformula);
				BitSet lower = result.getLower();
				BitSet upper = result.getUpper();
				lower.flip(0, this.system.getNumberOfStates());
				upper.flip(0, this.system.getNumberOfStates());
				result = new Result(upper, lower);
			} else if (formula instanceof And) {
				And and = (And) formula;
				Formula left = and.getLeft();
				Formula right = and.getRight();
				Result leftResult = check(left);
				Result rightResult = check(right);
				BitSet leftLower = leftResult.getLower();
				BitSet leftUpper = leftResult.getUpper();
				BitSet rightLower = rightResult.getLower();
				BitSet rightUpper = rightResult.getUpper();
				BitSet lower = leftLower;
				lower.and(rightLower);
				BitSet upper = leftUpper;
				upper.and(rightUpper);
				result = new Result(lower, upper);
			} else if (formula instanceof Or) {
				Or or = (Or) formula;
				Formula left = or.getLeft();
				Formula right = or.getRight();
				Formula equivalent = new Not(new And(new Not(left), new Not(right)));
				result = check(equivalent);
			} else if (formula instanceof Implies) {
				Implies implies = (Implies) formula;
				Formula left = implies.getLeft();
				Formula right = implies.getRight();
				Formula equivalent = new Or(new Not(left), right);
				result = check(equivalent);
			} else if (formula instanceof Iff) {
				Iff iff = (Iff) formula;
				Formula left = iff.getLeft();
				Formula right = iff.getRight();
				Formula equivalent = new And(new Implies(left, right), new Implies(right, left));
				result = check(equivalent);
			} else if (formula instanceof ExistsNext) {
				ExistsNext existsNext = (ExistsNext) formula;
				Formula subFormula = existsNext.getFormula();
				Result subResult = check(subFormula);
				BitSet subLower = subResult.getLower();
				BitSet subUpper = subResult.getUpper();

				BitSet lower = new BitSet();
				BitSet upper = new BitSet();
				if (subFormula instanceof True) {
					for (int state = 0; state < this.system.getNumberOfStates(); state++) {
						if (this.system.getSuccessors().containsKey(state) || this.system.getPartial().get(state)) { // post(state)
																														// is
																														// nonempty
																														// or
																														// state
																														// is
																														// partially
																														// explored
							lower.set(state);
							upper.set(state);
						}
					}
				} else {
					for (int state = 0; state < this.system.getNumberOfStates(); state++) {
						if (this.system.getPartial().get(state)) {
							upper.set(state);
						}
						if (this.system.getSuccessors().containsKey(state)) { // post(state) is nonempty
							BitSet post = this.system.getSuccessors().get(state);
							if (post.intersects(subLower)) {
								lower.set(state);
							}
							if (post.intersects(subUpper)) {
								upper.set(state);
							}
						}
					}
				}

				result = new Result(lower, upper);
			} else if (formula instanceof ForAllNext) {
				ForAllNext alwaysNext = (ForAllNext) formula;
				Formula subformula = alwaysNext.getFormula();
				Result subResult = check(subformula);
				BitSet subLower = subResult.getLower();
				BitSet subUpper = subResult.getUpper();

				BitSet lower = new BitSet();
				BitSet upper = new BitSet();
				for (int state = 0; state < this.system.getNumberOfStates(); state++) {
					if (this.system.getPartial().get(state)) {
						upper.set(state);
					} else {
						if (this.system.getSuccessors().containsKey(state)) { // post(state) is nonempty
							BitSet post = this.system.getSuccessors().get(state);
							if (subset(post, subLower)) {
								lower.set(state);
							}
							if (subset(post, subUpper)) {
								upper.set(state);
							}
						}
					}
				}

				result = new Result(lower, upper);
			} else if (formula instanceof ExistsAlways) {
				ExistsAlways existsAlways = (ExistsAlways) formula;
				Formula subFormula = existsAlways.getFormula();
				Result subResult = check(subFormula);
				BitSet subLower = subResult.getLower();
				BitSet subUpper = subResult.getUpper();

				BitSet lower = new BitSet();
				lower.set(0, this.system.getNumberOfStates());
				BitSet previous;
				do {
					previous = lower;
					lower = new BitSet(this.system.getNumberOfStates());
					for (int state = subLower.nextSetBit(0); state != -1; state = subLower.nextSetBit(state + 1)) { // for
																													// each
																													// state
																													// in
																													// SatLower(subFormula)
						if (this.system.getSuccessors().containsKey(state)) { // post(state) is nonempty
							BitSet post = this.system.getSuccessors().get(state);
							if (post.intersects(previous)) {
								lower.set(state);
							}
						} else { // post(state) is empty
							if (!this.system.getPartial().get(state)) {
								lower.set(state);
							}
						}
					}
				} while (!lower.equals(previous));

				BitSet upper = new BitSet();
				upper.set(0, this.system.getNumberOfStates());
				do {
					previous = upper;
					upper = new BitSet(this.system.getNumberOfStates());
					for (int state = subUpper.nextSetBit(0); state != -1; state = subUpper.nextSetBit(state + 1)) { // for
																													// each
																													// state
																													// in
																													// SatLower(subFormula)
						if (this.system.getSuccessors().containsKey(state)) { // post(state) is nonempty
							BitSet post = this.system.getSuccessors().get(state);
							if (post.intersects(previous)) {
								upper.set(state);
							}
						} else { // post(state) is empty
							upper.set(state);
						}
						if (this.system.getPartial().get(state)) {
							upper.set(state);
						}
					}
				} while (!upper.equals(previous));

				result = new Result(lower, upper);
			} else if (formula instanceof ForAllAlways) {
				ForAllAlways forAllAlways = (ForAllAlways) formula;
				Formula subformula = forAllAlways.getFormula();
				Formula equivalent = new Not(new ExistsUntil(new True(), new Not(subformula)));
				result = check(equivalent);
			} else if (formula instanceof ExistsEventually) {
				ExistsEventually existsEventually = (ExistsEventually) formula;
				Formula subFormula = existsEventually.getFormula();
				Formula equivalent = new ExistsUntil(new True(), subFormula);
				result = check(equivalent);
			} else if (formula instanceof ForAllEventually) {
				ForAllEventually forAllEventually = (ForAllEventually) formula;
				Formula subformula = forAllEventually.getFormula();
				Formula equivalent = new Not(new ExistsAlways(new Not(subformula)));
				result = check(equivalent);
			} else if (formula instanceof ExistsUntil) {
				ExistsUntil existsUntil = (ExistsUntil) formula;
				Formula left = existsUntil.getLeft();
				Formula right = existsUntil.getRight();
				Result leftResult = check(left);
				Result rightResult = check(right);
				BitSet leftLower = leftResult.getLower();
				BitSet leftUpper = leftResult.getUpper();
				BitSet rightLower = rightResult.getLower();
				BitSet rightUpper = rightResult.getUpper();

				BitSet lower = new BitSet();
				BitSet previous;
				do {
					previous = lower;
					lower = new BitSet(this.system.getNumberOfStates());
					for (int state = leftLower.nextSetBit(0); state != -1; state = leftLower.nextSetBit(state + 1)) { // for
																														// each
																														// state
																														// in
																														// Sat(left)
						if (this.system.getSuccessors().containsKey(state)) {
							BitSet post = this.system.getSuccessors().get(state); // post(state)
							if (post.intersects(previous)) {
								lower.set(state);
							}
						}
					}
					lower.or(rightLower);
				} while (!lower.equals(previous));

				BitSet upper = new BitSet();
				do {
					previous = upper;
					upper = new BitSet(this.system.getNumberOfStates());
					for (int state = leftUpper.nextSetBit(0); state != -1; state = leftUpper.nextSetBit(state + 1)) { // for
																														// each
																														// state
																														// in
																														// Sat(left)
						if (this.system.getPartial().get(state)) {
							upper.set(state);
						}
						if (this.system.getSuccessors().containsKey(state)) {
							BitSet post = system.getSuccessors().get(state); // post(state)
							if (post.intersects(previous)) {
								upper.set(state);
							}
						}
					}
					upper.or(rightUpper);
				} while (!upper.equals(previous));

				result = new Result(lower, upper);
			} else if (formula instanceof ForAllUntil) {
				ForAllUntil forAllUntil = (ForAllUntil) formula;
				Formula left = forAllUntil.getLeft();
				Formula right = forAllUntil.getRight();
				Formula equivalent = new And(
						new Not(new ExistsUntil(new Not(right), new And(new Not(left), new Not(right)))),
						new Not(new ExistsAlways(new Not(right))));
				result = check(equivalent);
			} else {
				System.err.println("This formula type is unknown (" + formula.getClass() + ")");
				return null;
			}

			this.cache.put(formula, result);
			return result;
		}
	}

	/**
	 * Tests whether the smaller set is a subset of the larger set.
	 * 
	 * @param smaller a set
	 * @param bigger  a set
	 * @return true if the smaller set is a subset of the larger set, false
	 *         otherwise
	 */
	private static boolean subset(BitSet smaller, BitSet bigger) {
		BitSet copy = (BitSet) bigger.clone();
		copy.and(smaller);
		return smaller.equals(copy);
	}
}