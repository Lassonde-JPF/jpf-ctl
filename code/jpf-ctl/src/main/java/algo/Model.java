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

package algo;

import java.util.Set;
import java.util.stream.Collectors;
import ctl.And;
import ctl.False;
import ctl.Formula;
import ctl.Or;
import ctl.*;
import java.lang.reflect.Field;
import java.util.BitSet;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * 
 * 
 * @author Parssa Khazra
 * @author Anto Nanah Ji
 * @author Franck van Breugel
 * @author Matthew Walker
 * @author Hongru Wang
 * 
 */

/**
 * 
 * TODO suggestion - why not have the model class either be static OR have it
 * contain an instance of a LabelledPartialTransitionpts?
 */
public class Model {

	// Post and Pre hashtables
	private Map<Integer, Set<Integer>> post;
	private Map<Integer, Set<Integer>> pre;

	// Target Transition System
	private LabelledPartialTransitionSystem pts;

	public Model(LabelledPartialTransitionSystem pts) {
		this.post = new HashMap<Integer, Set<Integer>>();
		this.pre = new HashMap<Integer, Set<Integer>>();

		this.pts = pts;
	}

	/*
	 * Returns the set of states that are successors to `state` and if not computed
	 * before, adds the entry to a hashtable, post
	 */
	private Set<Integer> Post(Integer state) {
		post.computeIfAbsent(state, k -> pts.getTransitions().stream().filter(t -> t.source == state).map(t -> t.target)
				.distinct().collect(Collectors.toSet()));
		return post.get(state);
	}

	/*
	 * Returns the set of states that are predecessors to `state` and if not
	 * computed before, adds the entry to a hashtable, pre
	 */
	private Set<Integer> Pre(Integer state) {
		pre.computeIfAbsent(state, k -> pts.getTransitions().stream().filter(t -> t.target == state).map(t -> t.source)
				.distinct().collect(Collectors.toSet()));
		return pre.get(state);
	}

	/**
	 * Returns the
	 * 
	 * @param pts
	 * @param formula
	 * @return
	 * 
	 */
	public StateSets check(Formula formula) {
		/*
		 * Base case
		 */
		if (formula instanceof True) {
			BitSet sat = new BitSet();
			sat.set(0, pts.getNumberOfStates());
			return new StateSets(sat, new BitSet());
		}
		/*
		 * Base case
		 */
		else if (formula instanceof False) {
			BitSet unsat = new BitSet();
			unsat.set(0, pts.getNumberOfStates());
			return new StateSets(new BitSet(), unsat);
		} 
		/*
		 * Base case
		 */
		else if (formula instanceof AtomicProposition) {
			String label = formula.toString();
			
			BitSet sat = new BitSet();
			BitSet unsat = new BitSet();
			for (int state = 0; state < pts.getNumberOfStates; state++) {
				if (pts.getLabelling.get(state).contains(label)) {
					sat.set(state);
				} else {
					unsat.set(state);
				}
			}
			
			//AtomicProposition aP = (AtomicProposition) formula;
			//int indexOfLastDot = aP.toString().lastIndexOf(".");
			//String className = aP.toString().substring(0, indexOfLastDot);
			//String fieldName = aP.toString().substring(indexOfLastDot + 1);

			//Set<Integer> Sat = new HashSet<Integer>();
			//Set<Integer> unSat = new HashSet<Integer>();

			//try { // reflection part
				//Class<?> obj = Class.forName(className);
				//Field f = obj.getField(fieldName);
				//Object val = f.get(obj); // This is the "value" of the AP's field

				// now we filter all states by those whose label contains this value
				//Sat = pts.getStates().stream().filter(s -> pts.getLabelling().get(s).contains(val))
						.collect(Collectors.toSet());

				//unSat = new HashSet<Integer>(pts.getStates());
				//unSat.removeAll(Sat);

				// This catch should never be triggered as we filter for these possibilities in
				// the FieldExists class
			//} catch (ClassNotFoundException | NoSuchFieldException | SecurityException | IllegalArgumentException
			//		| IllegalAccessException e) {
				// e.printStackTrace();
				//System.err.println(e.getMessage());
			//}

			return new StateSets(sat, unsat);
		} 
		/*
		 * Other cases
		 */
		else if (formula instanceof And) {
			And and = (And) formula;
			StateSets left = check(and.getLeft());
			StateSets right = check(and.getRight());
			BitSet sat = (BitSet) left.getSat().clone();
			sat.and(right.getSat());
			BitSet unsat = (BitSet) left.getUnsat().clone();
			sat.or(right.getUnsat());
			//Set<Integer> Sat = new HashSet<Integer>(L.sat);
			//Sat.retainAll(R.sat);
			//Set<Integer> unSat = new HashSet<Integer>(pts.getStates());
			//unSat.removeAll(Sat);
			return new StateSets(sat, unsat);
		} else if (formula instanceof Or) {
			// deal with similarly as And
			Or f = (Or) formula;
			StateSets L = check(f.getLeft());
			StateSets R = check(f.getRight());
			Set<Integer> Sat = new HashSet<Integer>(L.sat);
			Sat.addAll(R.sat);
			Set<Integer> unSat = new HashSet<Integer>(pts.getStates());
			unSat.removeAll(Sat);
			return new StateSets(Sat, unSat);
		} else if (formula instanceof Implies) {
			// !a or b
			Implies f = (Implies) formula;
			StateSets L = check(f.getLeft());
			StateSets R = check(f.getRight());
			Set<Integer> Sat = new HashSet<Integer>(L.unsat);
			Sat.addAll(R.sat);
			Set<Integer> unSat = new HashSet<Integer>(pts.getStates());
			unSat.removeAll(Sat);
			return new StateSets(Sat, unSat);
		} else if (formula instanceof Iff) {
			// (a && b) || (!a && !b)
			Iff f = (Iff) formula;
			StateSets L = check(f.getLeft());
			StateSets R = check(f.getRight());
			// (a && b)
			Set<Integer> LSat = new HashSet<Integer>(L.sat);
			LSat.retainAll(R.sat);
			// (!a && !b)
			Set<Integer> RSat = new HashSet<Integer>(L.unsat);
			RSat.retainAll(R.unsat);
			// (a && b) || (!a && !b)
			Set<Integer> Sat = new HashSet<Integer>(LSat);
			Sat.addAll(RSat);
			// !( (a && b) || (!a && !b) )
			Set<Integer> unSat = new HashSet<Integer>(pts.getStates());
			unSat.removeAll(Sat);
			return new StateSets(Sat, unSat);
		} else if (formula instanceof ExistsAlways) {
			ExistsAlways subFormula = ((ExistsAlways) formula).getFormula();
			
			BitSet sat = new BitSet();
			sat.set(0, pts.getNumberOfStates());
			if (!subFormula instanceof True) {
				BitSet subResult = check(subFormula).getSat();
				BitSet previous;
				do {
					previous = sat;
					sat = new BitSet();
					// Matt: I am converted, streams are convenient (but seem not to interact well with BitSets)
					for (int state = subResult.nextSetBit(0); state != -1; state = subResult.nextSetBit(state + 1)) { // for each state in Sat(subFormula)
						if (post.get(state).isEmpty() || !post.get(state).and(previous).isEmpty()) { // post(state) is empty or intersection of post(state) and previous is nonempty
							sat.set(state);
						}
					}
				} while (!sat.equals(previous));
			}
			
			// still need to compute unsat
			BitSet unsat = new BitSet();
			
//			Set<Integer> Sat = check(f.getFormula()).sat;
//			List<Integer> E = pts.getStates().stream().filter(s -> !Sat.contains(s)).collect(Collectors.toList());
//			Set<Integer> T = Sat;
//
//			int max = 0;
//			if (!Sat.isEmpty())
//				max = Collections.max(Sat);
//			Integer[] count = new Integer[max + 1];
//			for (Integer s : Sat) {
//				count[s] = Post(s).size();
//			}
//			while (!E.isEmpty()) {
//				Integer sP = E.remove(0);
//				Set<Integer> preS = Pre(sP);
//				for (Integer s : preS) {
//					if (T.contains(s)) {
//						count[s] = count[s - 1];
//						if (count[s] == 0) {
//							T.remove(s);
//							E.add(s);
//						}
//					}
//				}
//			}
//			Set<Integer> unSat = new HashSet<Integer>(pts.getStates());
//			unSat.removeAll(T);
			return new StateSets(sat, unsat);
		}
		/*
		 * This case is (EF p1) case. On page 333 of the textbook there is an alternate
		 * definition for this formula. The alternate definition is: (true EU p1)
		 */
		else if (formula instanceof ExistsEventually) {
			ExistsEventually eE = (ExistsEventually) formula;
			StateSets S = check(eE.getFormula());

			List<Integer> E = S.sat.stream().collect(Collectors.toList());
			Set<Integer> T = new HashSet<Integer>(E);
			while (!E.isEmpty()) {
				Integer sP = E.remove(0);
				Set<Integer> preS = Pre(sP);
				for (Integer s : preS) {
					Set<Integer> lSatLessT = pts.getStates(); // "true" set
					lSatLessT.removeAll(T);
					if (lSatLessT.contains(s)) {
						E.add(s);
						T.add(s);
					}
				}
			}
			Set<Integer> unSat = new HashSet<Integer>(pts.getStates());
			unSat.removeAll(T);
			return new StateSets(T, unSat);
		} else if (formula instanceof ExistsNext) {
			Formula subFormula = ((ExistsNext) formula).getFormula();
			StateSets sub = check(subFormula);
			
			BitSet sat = new BitSet();
			if (subFormula instanceof True) {
				for (int state = 0; state < pts.getNumberOfStates(); state++) {
					if (pts.getProcessed().contains(state)) { // state is fully explored
						if (!post.get(state).isEmpty()) { // post(state) is nonempty
							sat.set(state);
						}
					} else { // state is not fully explored
						sat.set(state); 
					}
				}
			} else {
				for (int state = 0; state < pts.getNumberOfStates(); state++) {
					if (!post.get(state).and(sub.getSat()).isEmpty()) { // intersection of post(state) and Sat of subformula is nonempty
						sat.nextSetBit(state);
					}
				}
			}
			
			BitSet unsat = new BitSet();
			if (subFormula instanceof False) {
				unsat.set(0, pts.getNumberOfStates()); // all states
			} else {
				for (Integer state : pts.getProcessed()) {
					if (post.get(state).andNot(sub.getUnsat()).isEmpty()) { // post(state) is subset of or equal to Unsat of subformula
						unsat.set(state);
					}
				}
			}
			
			//ExistsNext eN = (ExistsNext) formula;
			//StateSets S = check(eN.getFormula()); // recursive part
			//Set<Integer> Sat = pts.getTransitions().stream().filter(t -> S.sat.contains(t.target)).map(t -> t.source)
			//		.collect(Collectors.toSet());
			//Set<Integer> unSat = new HashSet<Integer>(pts.getStates());
			//unSat.removeAll(Sat);
			return new StateSets(sat, unsat);
		} else if (formula instanceof ExistsUntil) {
			ExistsUntil eU = (ExistsUntil) formula;
			StateSets R = check(eU.getRight());
			StateSets L = check(eU.getLeft());

			List<Integer> E = R.sat.stream().collect(Collectors.toList());
			Set<Integer> T = new HashSet<Integer>(E);
			while (!E.isEmpty()) {
				Integer sP = E.remove(0);
				Set<Integer> preS = Pre(sP);
				for (Integer s : preS) {
					Set<Integer> lSatLessT = new HashSet<Integer>(L.sat);
					lSatLessT.removeAll(T);
					if (lSatLessT.contains(s)) {
						E.add(s);
						T.add(s);
					}
				}
			}
			Set<Integer> unSat = new HashSet<Integer>(pts.getStates());
			unSat.removeAll(T);
			return new StateSets(T, unSat);
		}
		/*
		 * This case is (AG p1) case. On page 333 of the textbook there is an alternate
		 * definition for this formula. The alternate definition is: !(true EU !p1)
		 */
		else if (formula instanceof ForAllAlways) {
			ForAllAlways fA = (ForAllAlways) formula;
			StateSets S = check(fA.getFormula()); // p1

			List<Integer> E = S.unsat.stream().collect(Collectors.toList());
			Set<Integer> T = new HashSet<Integer>(E);
			while (!E.isEmpty()) {
				Integer sP = E.remove(0);
				Set<Integer> preS = Pre(sP);
				for (Integer s : preS) {
					Set<Integer> lSatLessT = pts.getStates(); // "true"
					lSatLessT.removeAll(T);
					if (lSatLessT.contains(s)) {
						E.add(s);
						T.add(s);
					}
				}
			}
			Set<Integer> Sat = new HashSet<Integer>(pts.getStates());
			Sat.removeAll(T);
			return new StateSets(Sat, T);
		}
		/*
		 * This case is (AF p1) case. On page 333 of the textbook there is an alternate
		 * definition for this formula. The alternate definition is: !EG!p1 .
		 */
		else if (formula instanceof ForAllEventually) {
			ForAllEventually fAF = (ForAllEventually) formula;
			// In this case we want the !p1 or the unsat states
			Set<Integer> S = check(fAF.getFormula()).unsat;
			List<Integer> E = pts.getStates().stream().filter(s -> !S.contains(s)).collect(Collectors.toList());
			Set<Integer> T = S;

			int max = 0;
			if (!S.isEmpty())
				max = Collections.max(S);
			Integer[] count = new Integer[max + 1];
			for (Integer s : S) {
				count[s] = Post(s).size();
			}
			while (!E.isEmpty()) {
				Integer sP = E.remove(0);
				Set<Integer> preS = Pre(sP);
				for (Integer s : preS) {
					if (T.contains(s)) {
						count[s] = count[s - 1];
						if (count[s] == 0) {
							T.remove(s);
							E.add(s);
						}
					}
				}
			}
			Set<Integer> Sat = new HashSet<Integer>(pts.getStates());
			Sat.removeAll(T);
			return new StateSets(Sat, T);

		}
		/*
		 * this case is the (AX p1) case
		 */
		else if (formula instanceof ForAllNext) {
			Formula subFormula = ((ForAllNext) formula).getFormula();
			Formula equivalent = new Not(new ExistsNext(new Not(subFormula))); // ForAllNext(subFormula) = !ExistsNext(!subFormula)
			return check(equivalent);
			
			//ForAllNext fN = (ForAllNext) formula;
			//StateSets S = check(fN.getFormula()); // recursive part

			// States that DO NOT satisfy this formula CONTAIN a transition in which the
			// TARGET is NOT CONTAINED in Sat(p1)
			//Set<Integer> unSat = pts.getTransitions().stream().filter(t -> !S.sat.contains(t.target)).map(t -> t.source)
			//		.collect(Collectors.toSet());

			//Set<Integer> Sat = new HashSet<Integer>(pts.getStates());
			//Sat.removeAll(unSat);
			//return new StateSets(Sat, unSat);
		}
		/*
		 * This case is the (p1 AU p2) case. On page 333 of the textbook there is an
		 * alternate definition for this formula. The alternate definition is: !(!p2 EU
		 * (!p1 && !p2)) && !EG!p2 In order to do this, we break down the formula
		 * ourselves and combine the separate pieces Piece1: (!p1 && !p2) Piece2: !(!p2
		 * EU Piece1) Piece3: !EG!p2 Piece4: Piece2 && Piece3
		 */
		else if (formula instanceof ForAllUntil) {
			ForAllUntil fAU = (ForAllUntil) formula;
			StateSets L = check(fAU.getLeft());
			StateSets R = check(fAU.getRight());

			// Piece1: (!p1 && !p2)
			Set<Integer> AND = new HashSet<Integer>(L.unsat);
			AND.retainAll(R.unsat);

			// Piece2: !(!p2 EU Piece1)
			List<Integer> E = AND.stream().collect(Collectors.toList());
			Set<Integer> T = new HashSet<Integer>(E);
			while (!E.isEmpty()) {
				Integer sP = E.remove(0);
				Set<Integer> preS = Pre(sP);
				for (Integer s : preS) {
					Set<Integer> lSatLessT = new HashSet<Integer>(L.unsat);
					lSatLessT.removeAll(T);
					if (lSatLessT.contains(s)) {
						E.add(s);
						T.add(s);
					}
				}
			}
			Set<Integer> EU = pts.getStates().stream().filter(s -> !T.contains(s)).collect(Collectors.toSet());

			// Piece3: !EG!p2
			List<Integer> F = pts.getStates().stream().filter(s -> !R.unsat.contains(s)).collect(Collectors.toList());
			Set<Integer> G = R.unsat;

			int max = 0;
			if (!R.unsat.isEmpty())
				max = Collections.max(R.unsat);
			Integer[] count = new Integer[max + 1];
			for (Integer s : R.unsat) {
				count[s] = Post(s).size();
			}
			while (!F.isEmpty()) {
				Integer sP = F.remove(0);
				Set<Integer> preS = Pre(sP);
				for (Integer s : preS) {
					if (G.contains(s)) {
						count[s] = count[s - 1];
						if (count[s] == 0) {
							G.remove(s);
							F.add(s);
						}
					}
				}
			}
			Set<Integer> notF = new HashSet<Integer>(pts.getStates());
			notF.removeAll(F);

			// Piece4: Piece2 && Piece3
			EU.retainAll(notF);

			// Final cleanup
			Set<Integer> unSat = new HashSet<Integer>(pts.getStates());
			unSat.removeAll(EU);
			return new StateSets(EU, unSat);
		} else if (formula instanceof Not) {
			Not n = (Not) formula;
			StateSets S = check(n.getFormula());
			return new StateSets(S.unsat, S.sat);
		} else {
			System.err.println("This formula type is unknown");
			return null;
		}
	}
}