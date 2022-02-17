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

package model.ctl;

import java.util.BitSet;
import formulas.Formula;
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
import model.ModelChecker;
import model.Result;
import model.TransitionSystem;

/**
 * CTL model checking for partial transition systems.
 * 
 * @author Parssa Khazra
 * @author Anto Nanah Ji
 * @author Matthew Walker
 * @author Hongru Wang
 * @author Franck van Breugel
 */
public class CTLModelChecker extends ModelChecker {

	/**
	 * Initializes this model checker with the given partial transition system.
	 * 
	 * @param system a partial transition system
	 */
	public CTLModelChecker(TransitionSystem system) {
		super(system);
	}

	/**
	 * Returns a lower- and upperbound of the satisfaction set of the given CTL
	 * formula.
	 * 
	 * @param formula the CTL formula
	 * @return a lower- and upperbound of the satisfaction set of the given CTL
	 *         formula
	 */
	@Override
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

}