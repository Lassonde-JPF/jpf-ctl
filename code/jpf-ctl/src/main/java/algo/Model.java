package algo;

import java.lang.reflect.Field;
import java.util.HashSet;

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

import algo.LabelledPartialTransitionSystem.Transition;
import ctl.And;
import ctl.False;
import ctl.Formula;
import ctl.Or;
import ctl.*;

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
		if (formula instanceof True) {
			return new StateSets(system.getStates(), new HashSet<Integer>());
		} else if (formula instanceof False) {
			return new StateSets(new HashSet<Integer>(), system.getStates());
		} else if (formula instanceof And) {
			And f = (And) formula;
			StateSets L = check(system, f.getLeft());
			StateSets R = check(system, f.getRight());
			Set<Integer> Sat = L.sat.stream().filter(R.sat::contains).collect(Collectors.toSet());
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
			Set<Integer> LSat = L.sat.stream().filter(R.sat::contains).collect(Collectors.toSet());
			// (!a && !b)
			Set<Integer> RSat = L.unsat.stream().filter(R.unsat::contains).collect(Collectors.toSet());
			// (a && b) || (!a && !b)
			Set<Integer> Sat = new HashSet<Integer>(LSat);
			Sat.addAll(RSat);
			// !( (a && b) || (!a && !b) )
			Set<Integer> unSat = new HashSet<Integer>(system.getStates());
			unSat.removeAll(Sat);
			return new StateSets(Sat, unSat);
		} else if (formula instanceof ExistsAlways) { //TODO check this probably wrong
			ExistsAlways f = (ExistsAlways) formula;
			StateSets T = check(system, f.getFormula()); //recursive step
			// For each state s subset Sat(phi)
			T.sat.stream().forEach(s -> {
				//Set of transitions for s
				Set<Transition> transitions = system.getTransitions().stream().filter(t -> t.source == s).collect(Collectors.toSet());
				if (transitions.stream().noneMatch(t -> T.sat.contains(t.target))) {
					T.sat.remove(s);
					T.unsat.add(s);
				}
			});
			return T;
		} else if (formula instanceof ExistsEventually) {
			return null;
		} else if (formula instanceof ExistsNext) {
			ExistsNext eN = (ExistsNext) formula;
			StateSets S = check(system, eN.getFormula()); // recursive part //EX phi1 
			Set<Integer> Sat = system.getTransitions().stream().filter(t -> S.sat.contains(t.target)).map(t -> t.source)
					.collect(Collectors.toSet());
			Set<Integer> unSat = new HashSet<Integer>(system.getStates());
			unSat.removeAll(Sat);
			return new StateSets(Sat, unSat);
		} else if (formula instanceof ExistsUntil) {
			return null;
		} else if (formula instanceof ForAllAlways) {
			return null;
		} else if (formula instanceof ForAllEventually) {
			return null;
		} else if (formula instanceof ForAllNext) {
			ForAllNext fN = (ForAllNext) formula;
			StateSets S = check(system, fN.getFormula()); // recursive part
			Set<Integer> unSat = system.getTransitions().stream().filter(t -> !S.sat.contains(t.target)).map(t -> t.source)
					.collect(Collectors.toSet());
			//Pretty sure this next part is wrong because we will be including unknown states :shrugs:
			Set<Integer> Sat = new HashSet<Integer>(system.getStates());
			Sat.removeAll(unSat);
			return new StateSets(Sat, unSat);
		} else if (formula instanceof ForAllUntil) {
			return null;
		} else if (formula instanceof Not) {
			Formula n = (Not) formula;
			StateSets S = check(system, n);
			return new StateSets(S.unsat, S.sat);
		} else if (formula instanceof AtomicProposition) {
			AtomicProposition aP = (AtomicProposition) formula;

			int indexOfLastDot = aP.toString().lastIndexOf(".");
		    String className = aP.toString().substring(0, indexOfLastDot);
		    String fieldName = aP.toString().substring(indexOfLastDot + 1);
			
		    Set<Integer> Sat = new HashSet<Integer>();
			Set<Integer> unSat = new HashSet<Integer>();
		    
			try {
				Class<?> obj = Class.forName(className);
				Field f = obj.getField(fieldName);
				Object val = f.get(obj);

				Sat = system.getStates().stream().filter(s -> system.getLabelling().get(s).equals(val)).collect(Collectors.toSet());
				
				unSat = new HashSet<Integer>(system.getStates());
				unSat.removeAll(Sat);
				
			} catch (ClassNotFoundException | NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
				//e.printStackTrace();
				System.err.println(e.getMessage());
			}
		
			return new StateSets(Sat, unSat);
		} else {
			System.err.println("This formula type is unknown");
			return null;
		}
	}
}