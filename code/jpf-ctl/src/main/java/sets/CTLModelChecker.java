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

package sets;

import java.util.HashSet;
import formulas.Formula;
import formulas.ctl.And;
import formulas.ctl.AtomicProposition;
import formulas.ctl.CTLFormula;
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
	@SuppressWarnings("unchecked")
	@Override
	public Result check(Formula formula) {
		// formula = formula.simplify();
		if (false) {// (this.cache.containsKey(formula)) {
			return this.cache.get(formula);
		} else {
			Result result;
			if (formula instanceof True) {
				HashSet<Integer> upper = new HashSet<Integer>();
				for (int state = 0; state < this.system.getNumberOfStates(); state++) {
					upper.add(state);
				}
				HashSet<Integer> lower = new HashSet<Integer>();
				for (int state = 0; state < this.system.getNumberOfStates(); state++) {
					lower.add(state);
				}
				result = new Result(upper, lower);
			} else if (formula instanceof False) {
				result = new Result(new HashSet<Integer>(), new HashSet<Integer>());
			} else if (formula instanceof AtomicProposition) {
				String name = ((AtomicProposition) formula).toString();
				HashSet<Integer> labelling = new HashSet<Integer>();
				if (this.system.getIndices().containsKey(name)) {
					int index = this.system.getIndices().get(name);
							if (this.system.getLabelling().containsKey(index)) {
								labelling = new HashSet<Integer>(this.system.getLabelling().get(index));
							}
				}
				result = new Result(labelling, new HashSet<Integer>(labelling));
			} else if (formula instanceof Not) {
				Not not = (Not) formula;
				CTLFormula subformula = not.getFormula();
				result = check(subformula);
				HashSet<Integer> resultLower = result.getLower();
				HashSet<Integer> resultUpper = result.getUpper();
				HashSet<Integer> lower = new HashSet<Integer>();
				HashSet<Integer> upper = new HashSet<Integer>();
				for (int state = 0; state < this.system.getNumberOfStates(); state++) {
					if (!resultLower.contains(state)) {
						lower.add(state);
					}
					if (!resultUpper.contains(state)) {
						upper.add(state);
					}
				}
				result = new Result(upper, lower);
			} else if (formula instanceof And) {
				And and = (And) formula;
				CTLFormula left = and.getLeft();
				CTLFormula right = and.getRight();
				Result leftResult = check(left);
				Result rightResult = check(right);
				HashSet<Integer> leftLower = leftResult.getLower();
				HashSet<Integer> leftUpper = leftResult.getUpper();
				HashSet<Integer> rightLower = rightResult.getLower();
				HashSet<Integer> rightUpper = rightResult.getUpper();
				leftLower.retainAll(rightLower);
				leftUpper.retainAll(rightUpper);
				result = new Result(leftLower, leftUpper);
			} else if (formula instanceof Or) {
				Or or = (Or) formula;
				CTLFormula left = or.getLeft();
				CTLFormula right = or.getRight();
				Result leftResult = check(left);
				Result rightResult = check(right);
				HashSet<Integer> leftLower = leftResult.getLower();
				HashSet<Integer> leftUpper = leftResult.getUpper();
				HashSet<Integer> rightLower = rightResult.getLower();
				HashSet<Integer> rightUpper = rightResult.getUpper();
				leftLower.addAll(rightLower);
				leftUpper.addAll(rightUpper);
				result = new Result(leftLower, leftUpper);
			} else if (formula instanceof Implies) {
				Implies implies = (Implies) formula;
				CTLFormula left = implies.getLeft();
				CTLFormula right = implies.getRight();
				CTLFormula equivalent = new Or(new Not(left), right);
				result = check(equivalent);
			} else if (formula instanceof Iff) {
				Iff iff = (Iff) formula;
				CTLFormula left = iff.getLeft();
				CTLFormula right = iff.getRight();
				CTLFormula equivalent = new And(new Implies(left, right), new Implies(right, left));
				result = check(equivalent);
			} else if (formula instanceof ExistsNext) {
				ExistsNext existsNext = (ExistsNext) formula;
				CTLFormula subFormula = existsNext.getFormula();
				Result subResult = check(subFormula);
				HashSet<Integer> subLower = subResult.getLower();
				HashSet<Integer> subUpper = subResult.getUpper();
				HashSet<Integer> lower = new HashSet<Integer>(this.system.getNumberOfStates());
				HashSet<Integer> upper = new HashSet<Integer>(this.system.getNumberOfStates());
				for (int state = 0; state < this.system.getNumberOfStates(); state++) {
					if (this.system.getPartial().contains(state)) {
						upper.add(state);
					}
					if (this.system.getSuccessors().containsKey(state)) { // post(state) is nonempty
						HashSet<Integer> post = this.system.getSuccessors().get(state);
						HashSet<Integer> p1 = new HashSet<Integer>(post);
						p1.retainAll(subLower);
						if (!p1.isEmpty()) {
							lower.add(state);
						}
						HashSet<Integer> p2 = new HashSet<Integer>(post);
						p2.retainAll(subUpper);
						if (!p2.isEmpty()) {
							upper.add(state);
						}
					}
				}
				result = new Result(lower, upper);
			} else if (formula instanceof ForAllNext) {
				ForAllNext alwaysNext = (ForAllNext) formula;
				CTLFormula subformula = alwaysNext.getFormula();
				Result subResult = check(subformula);
				HashSet<Integer> subLower = subResult.getLower();
				HashSet<Integer> subUpper = subResult.getUpper();

				HashSet<Integer> lower = new HashSet<Integer>();
				HashSet<Integer> upper = new HashSet<Integer>();
				for (int state = 0; state < this.system.getNumberOfStates(); state++) {
					if (this.system.getPartial().contains(state)) {
						upper.add(state);
					} else {
						if (this.system.getSuccessors().containsKey(state)) { // post(state) is nonempty
							HashSet<Integer> post = this.system.getSuccessors().get(state);
							if (subLower.containsAll(post)) {
								lower.add(state);
							}
							if (subUpper.containsAll(post)) {
								upper.add(state);
							}
						}
					}
				}
				result = new Result(lower, upper);
			} else if (formula instanceof ExistsAlways) {
				ExistsAlways existsAlways = (ExistsAlways) formula;
				CTLFormula subFormula = existsAlways.getFormula();
				Result subResult = check(subFormula);
				HashSet<Integer> subLower = subResult.getLower();
				HashSet<Integer> subUpper = subResult.getUpper();

				HashSet<Integer> lower = new HashSet<Integer>(this.system.getNumberOfStates());
				for (int state = 0; state < this.system.getNumberOfStates(); state++) {
					lower.add(state);
				}
				HashSet<Integer> previous;
				do {
					previous = lower;
					lower = new HashSet<Integer>(this.system.getNumberOfStates());
					for (Integer state : subLower) {
						if (this.system.getSuccessors().containsKey(state)) { // post(state) is nonempty
							HashSet<Integer> post = this.system.getSuccessors().get(state);
							post.retainAll(previous);
							if (!post.isEmpty()) {
								lower.add(state);
							}
						} else { // post(state) is empty
							if (!this.system.getPartial().contains(state)) {
								lower.add(state);
							}
						}
					}
				} while (!lower.equals(previous));

				HashSet<Integer> upper = new HashSet<Integer>(this.system.getNumberOfStates());
				for (int state = 0; state < this.system.getNumberOfStates(); state++) {
					upper.add(state);
				}
				do {
					previous = upper;
					upper = new HashSet<Integer>(this.system.getNumberOfStates());
					for (Integer state : subUpper) {
						if (this.system.getSuccessors().containsKey(state)) { // post(state) is nonempty
							HashSet<Integer> post = this.system.getSuccessors().get(state);
							post.retainAll(previous);
							if (!post.isEmpty()) {
								upper.add(state);
							}
						} else { // post(state) is empty
							upper.add(state);
						}
						if (this.system.getPartial().contains(state)) {
							upper.add(state);
						}
					}
				} while (!upper.equals(previous));

				result = new Result(lower, upper);
			} else if (formula instanceof ForAllAlways) {
				ForAllAlways forAllAlways = (ForAllAlways) formula;
				CTLFormula subformula = forAllAlways.getFormula();
				CTLFormula equivalent = new Not(new ExistsUntil(new True(), new Not(subformula)));
				result = check(equivalent);
			} else if (formula instanceof ExistsEventually) {
				ExistsEventually existsEventually = (ExistsEventually) formula;
				CTLFormula subFormula = existsEventually.getFormula();
				CTLFormula equivalent = new ExistsUntil(new True(), subFormula);
				result = check(equivalent);
			} else if (formula instanceof ForAllEventually) {
				ForAllEventually forAllEventually = (ForAllEventually) formula;
				CTLFormula subformula = forAllEventually.getFormula();
				CTLFormula equivalent = new Not(new ExistsAlways(new Not(subformula)));
				result = check(equivalent);
			} else if (formula instanceof ExistsUntil) {
				ExistsUntil existsUntil = (ExistsUntil) formula;
				CTLFormula left = existsUntil.getLeft();
				CTLFormula right = existsUntil.getRight();
				Result leftResult = check(left);
				Result rightResult = check(right);
				HashSet<Integer> leftLower = leftResult.getLower();
				HashSet<Integer> leftUpper = leftResult.getUpper();
				HashSet<Integer> rightLower = rightResult.getLower();
				HashSet<Integer> rightUpper = rightResult.getUpper();

				HashSet<Integer> lower = new HashSet<Integer>(system.getNumberOfStates());
				HashSet<Integer> previous;
				do {
					previous = lower;
					lower = new HashSet<Integer>(this.system.getNumberOfStates());
					for (Integer state : leftLower) {
						if (this.system.getSuccessors().containsKey(state)) {
							HashSet<Integer> post = this.system.getSuccessors().get(state); // post(state)
							post.retainAll(previous);
							if (!post.isEmpty()) {
								lower.add(state);
							}
						}
					}
					lower.addAll(rightLower);
				} while (!lower.equals(previous));

				HashSet<Integer> upper = new HashSet<Integer>(system.getNumberOfStates());
				do {
					previous = upper;
					upper = new HashSet<Integer>(this.system.getNumberOfStates());
					for (Integer state : leftUpper) {
						if (this.system.getPartial().contains(state)) {
							upper.add(state);
						}
						if (this.system.getSuccessors().containsKey(state)) {
							HashSet<Integer> post = system.getSuccessors().get(state); // post(state)
							post.retainAll(previous);
							if (!post.isEmpty()) {
								upper.add(state);
							}
						}
					}
					upper.addAll(rightUpper);
				} while (!upper.equals(previous));

				result = new Result(lower, upper);
			} else if (formula instanceof ForAllUntil) {
				ForAllUntil forAllUntil = (ForAllUntil) formula;
				CTLFormula left = forAllUntil.getLeft();
				CTLFormula right = forAllUntil.getRight();
				CTLFormula equivalent = new And(
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