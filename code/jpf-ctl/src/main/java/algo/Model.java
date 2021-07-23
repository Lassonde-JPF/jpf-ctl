package algo;

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

import java.util.Set;
import java.util.stream.Collectors;
import ctl.And;
import ctl.False;
import ctl.Formula;
import ctl.Or;
import ctl.*;
import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.List;

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
public class Model {

	/**
	 * This class represents a pair of sets of states:
	 * 
	 * TODO suggestion - why not have the model class either be static OR have it
	 * contain an instance of a LabelledPartialTransitionSystem?
	 * 
	 * TODO forAllAlways, ForAllUntil need to be fixed. ForAllUntil is dependent on
	 * forAllAlways and forAllAlways is dependent on ExistsUntil so those are all
	 * possible classes where the bug may exist.
	 */
	public static class StateSets {
		private Set<Integer> sat;
		private Set<Integer> unsat;

		/**
		 * @param sat
		 * @param unsat
		 */
		public StateSets(Set<Integer> sat, Set<Integer> unsat) {
			this.sat = sat;
			this.unsat = unsat;
		}

		public Set<Integer> getSat() {
			return this.sat;
		}

		public Set<Integer> getUnSat() {
			return this.unsat;
		}

		@Override
		public boolean equals(Object o) {
			if (o == this) {
				return true;
			}
			if (!(o instanceof StateSets)) {
				return false;
			}

			StateSets ss = (StateSets) o;

			return this.sat.equals(ss.sat) && this.unsat.equals(ss.unsat);
		}

		@Override
		public String toString() {
			return "sat = " + sat + "\nunsat = " + unsat;
		}
	}

	/**
	 * Returns the
	 * 
	 * @param system
	 * @param formula
	 * @return
	 * 
	 */
	public StateSets check(LabelledPartialTransitionSystem system, Formula formula) {
		/*
		 * Base Case
		 */
		if (formula instanceof True) {
			return new StateSets(system.getStates(), new HashSet<Integer>());
		}
		/*
		 * Base Case
		 */
		else if (formula instanceof False) {
			return new StateSets(new HashSet<Integer>(), system.getStates());
		} else if (formula instanceof And) {
			And f = (And) formula;
			StateSets L = check(system, f.getLeft());
			StateSets R = check(system, f.getRight());
			Set<Integer> Sat = new HashSet<Integer>(L.sat);
			Sat.retainAll(R.sat);
			Set<Integer> unSat = new HashSet<Integer>(system.getStates());
			unSat.removeAll(Sat);
			return new StateSets(Sat, unSat);
		} else if (formula instanceof Or) {
			Or f = (Or) formula;
			StateSets L = check(system, f.getLeft());
			StateSets R = check(system, f.getRight());
			Set<Integer> Sat = new HashSet<Integer>(L.sat);
			Sat.addAll(R.sat);
			Set<Integer> unSat = new HashSet<Integer>(system.getStates());
			unSat.removeAll(Sat);
			return new StateSets(Sat, unSat);
		} else if (formula instanceof Implies) {
			// !a or b
			Implies f = (Implies) formula;
			StateSets L = check(system, f.getLeft());
			StateSets R = check(system, f.getRight());
			Set<Integer> Sat = new HashSet<Integer>(L.unsat);
			Sat.addAll(R.sat);
			Set<Integer> unSat = new HashSet<Integer>(system.getStates());
			unSat.removeAll(Sat);
			return new StateSets(Sat, unSat);
		} else if (formula instanceof Iff) {
			// (a && b) || (!a && !b)
			Iff f = (Iff) formula;
			StateSets L = check(system, f.getLeft());
			StateSets R = check(system, f.getRight());
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
			Set<Integer> unSat = new HashSet<Integer>(system.getStates());
			unSat.removeAll(Sat);
			return new StateSets(Sat, unSat);
		} else if (formula instanceof ExistsAlways) { // Page 352 in textbook
			ExistsAlways f = (ExistsAlways) formula;
			Set<Integer> Sat = check(system, f.getFormula()).sat;
			List<Integer> E = system.getStates().stream().filter(s -> !Sat.contains(s)).collect(Collectors.toList());
			Set<Integer> T = Sat;

			Integer[] count = new Integer[Sat.size()];
			for (Integer s : Sat) {
				Integer succ = (int) system.getTransitions().stream().filter(t -> t.source == s).count();
				count[s] = succ;
			}
			while (!E.isEmpty()) {
				Integer sP = E.remove(0);
				Set<Integer> preS = system.getTransitions().stream().filter(t -> t.target == sP).map(t -> t.source)
						.collect(Collectors.toSet());
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
			Set<Integer> unSat = new HashSet<Integer>(system.getStates());
			unSat.removeAll(T);
			return new StateSets(T, unSat);
		}
		/*
		 * This case is (EF p1) case. On page 333 of the textbook there is an alternate
		 * definition for this formula. The alternate definition is: (true EU p1)
		 */
		else if (formula instanceof ExistsEventually) {
			ExistsEventually eE = (ExistsEventually) formula;
			StateSets S = check(system, eE.getFormula());

			List<Integer> E = S.sat.stream().collect(Collectors.toList());
			Set<Integer> T = new HashSet<Integer>(E);
			while (!E.isEmpty()) {
				Integer sP = E.remove(0);
				Set<Integer> preS = system.getTransitions().stream().filter(t -> t.target == sP).map(t -> t.source)
						.collect(Collectors.toSet());
				for (Integer s : preS) {
					Set<Integer> lSatLessT = system.getStates(); // "true" set
					lSatLessT.removeAll(T);
					if (lSatLessT.contains(s)) {
						E.add(s);
						T.add(s);
					}
				}
			}
			Set<Integer> unSat = new HashSet<Integer>(system.getStates());
			unSat.removeAll(T);
			return new StateSets(T, unSat);
		} else if (formula instanceof ExistsNext) {
			ExistsNext eN = (ExistsNext) formula;
			StateSets S = check(system, eN.getFormula()); // recursive part
			Set<Integer> Sat = system.getTransitions().stream().filter(t -> S.sat.contains(t.target)).map(t -> t.source)
					.collect(Collectors.toSet());
			Set<Integer> unSat = new HashSet<Integer>(system.getStates());
			unSat.removeAll(Sat);
			return new StateSets(Sat, unSat);
		} else if (formula instanceof ExistsUntil) {
			ExistsUntil eU = (ExistsUntil) formula;
			StateSets R = check(system, eU.getRight());
			StateSets L = check(system, eU.getLeft());

			List<Integer> E = R.sat.stream().collect(Collectors.toList());
			Set<Integer> T = new HashSet<Integer>(E);
			while (!E.isEmpty()) {
				Integer sP = E.remove(0);
				Set<Integer> preS = system.getTransitions().stream().filter(t -> t.target == sP).map(t -> t.source)
						.collect(Collectors.toSet());
				for (Integer s : preS) {
					Set<Integer> lSatLessT = new HashSet<Integer>(L.sat);
					lSatLessT.removeAll(T);
					if (lSatLessT.contains(s)) {
						E.add(s);
						T.add(s);
					}
				}
			}
			Set<Integer> unSat = new HashSet<Integer>(system.getStates());
			unSat.removeAll(T);
			return new StateSets(T, unSat);
		}
		/*
		 * This case is (AG p1) case. On page 333 of the textbook there is an alternate
		 * definition for this formula. The alternate definition is: !(true EU p1)
		 */
		else if (formula instanceof ForAllAlways) {
			ForAllAlways fA = (ForAllAlways) formula;
			StateSets S = check(system, fA.getFormula()); // p1

			List<Integer> E = S.sat.stream().collect(Collectors.toList());
			Set<Integer> T = new HashSet<Integer>(E);
			while (!E.isEmpty()) {
				Integer sP = E.remove(0);
				Set<Integer> preS = system.getTransitions().stream().filter(t -> t.target == sP).map(t -> t.source)
						.collect(Collectors.toSet());
				for (Integer s : preS) {
					Set<Integer> lSatLessT = system.getStates(); // "true"
					lSatLessT.removeAll(T);
					if (lSatLessT.contains(s)) {
						E.add(s);
						T.add(s);
					}
				}
			}
			Set<Integer> Sat = new HashSet<Integer>(system.getStates());
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
			Set<Integer> S = check(system, fAF.getFormula()).unsat;
			List<Integer> E = system.getStates().stream().filter(s -> !S.contains(s)).collect(Collectors.toList());
			Set<Integer> T = S;

			Integer[] count = new Integer[S.size()];
			for (Integer s : S) {
				Integer succ = (int) system.getTransitions().stream().filter(t -> t.source == s).count();
				count[s] = succ;
			}
			while (!E.isEmpty()) {
				Integer sP = E.remove(0);
				Set<Integer> preS = system.getTransitions().stream().filter(t -> t.target == sP).map(t -> t.source)
						.collect(Collectors.toSet());
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
			Set<Integer> Sat = new HashSet<Integer>(system.getStates());
			Sat.removeAll(T);
			return new StateSets(Sat, T);

		}
		/*
		 * this case is the (AX p1) case
		 */
		else if (formula instanceof ForAllNext) {
			ForAllNext fN = (ForAllNext) formula;
			StateSets S = check(system, fN.getFormula()); // recursive part

			// States that DO NOT satisfy this formula CONTAIN a transition in which the
			// TARGET is NOT CONTAINED in Sat(p1)
			Set<Integer> unSat = system.getTransitions().stream().filter(t -> !S.sat.contains(t.target))
					.map(t -> t.source).collect(Collectors.toSet());

			Set<Integer> Sat = new HashSet<Integer>(system.getStates());
			Sat.removeAll(unSat);
			return new StateSets(Sat, unSat);
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
			StateSets L = check(system, fAU.getLeft());
			StateSets R = check(system, fAU.getRight());

			// Piece1: (!p1 && !p2)
			Set<Integer> AND = new HashSet<Integer>(L.unsat);
			L.unsat.retainAll(R.unsat);

			// Piece2: !(!p2 EU Piece1)
			List<Integer> E = AND.stream().collect(Collectors.toList());
			Set<Integer> T = new HashSet<Integer>(E);
			while (!E.isEmpty()) {
				Integer sP = E.remove(0);
				Set<Integer> preS = system.getTransitions().stream().filter(t -> t.target == sP).map(t -> t.source)
						.collect(Collectors.toSet());
				for (Integer s : preS) {
					Set<Integer> lSatLessT = new HashSet<Integer>(L.unsat);
					lSatLessT.removeAll(T);
					if (lSatLessT.contains(s)) {
						E.add(s);
						T.add(s);
					}
				}
			}
			Set<Integer> EU = system.getStates().stream().filter(s -> !T.contains(s)).collect(Collectors.toSet());

			// Piece3: !EG!p2
			List<Integer> F = system.getStates().stream().filter(s -> !R.unsat.contains(s))
					.collect(Collectors.toList());
			Set<Integer> G = R.unsat;

			Integer[] count = new Integer[R.unsat.size()];
			for (Integer s : R.unsat) {
				Integer succ = (int) system.getTransitions().stream().filter(t -> t.source == s).count();
				count[s] = succ;
			}
			while (!F.isEmpty()) {
				Integer sP = F.remove(0);
				Set<Integer> preS = system.getTransitions().stream().filter(t -> t.target == sP).map(t -> t.source)
						.collect(Collectors.toSet());
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

			// Piece4: Piece2 && Piece3
			EU.retainAll(G);

			// Final cleanup
			Set<Integer> unSat = new HashSet<Integer>(system.getStates());
			unSat.removeAll(EU);
			return new StateSets(EU, unSat);
		} else if (formula instanceof Not) {
			Not n = (Not) formula;
			StateSets S = check(system, n.getFormula());
			return new StateSets(S.unsat, S.sat);
		} else if (formula instanceof AtomicProposition) {
			AtomicProposition aP = (AtomicProposition) formula;

			int indexOfLastDot = aP.toString().lastIndexOf(".");
			String className = aP.toString().substring(0, indexOfLastDot);
			String fieldName = aP.toString().substring(indexOfLastDot + 1);

			Set<Integer> Sat = new HashSet<Integer>();
			Set<Integer> unSat = new HashSet<Integer>();

			try { // reflection part
				Class<?> obj = Class.forName(className);
				Field f = obj.getField(fieldName);
				Object val = f.get(obj); // This is the "value" of the AP's field

				// now we filter all states by those whose label contains this value
				Sat = system.getStates().stream().filter(s -> system.getLabelling().get(s).contains(val))
						.collect(Collectors.toSet());

				unSat = new HashSet<Integer>(system.getStates());
				unSat.removeAll(Sat);

				// This catch should never be triggered as we filter for these possibilities in
				// the FieldExists class
			} catch (ClassNotFoundException | NoSuchFieldException | SecurityException | IllegalArgumentException
					| IllegalAccessException e) {
				// e.printStackTrace();
				System.err.println(e.getMessage());
			}

			return new StateSets(Sat, unSat);
		} else {
			System.err.println("This formula type is unknown");
			return null;
		}
	}
}