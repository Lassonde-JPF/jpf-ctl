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
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import ctl.*;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
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
 * TODO suggestion - why not have the model class either be static OR have it
 * contain an instance of a LabelledPartialTransitionpts?
 */
public class Model {

	// Post and Pre hashtables
	private final Map<Integer, Set<Integer>> post;
	private final Map<Integer, Set<Integer>> pre;
	private final Map<Formula,StateSets> unSatAndSatForEachFormula; 
	private final Map<Integer,String> labellingFormulaForEachState;

	// Subset tables
	private final Map<Formula, StateSets> subset;

	// Target Transition System
	private final LabelledPartialTransitionSystem pts;

	private List<String> formulaStack;

	// Constructor
	public Model(LabelledPartialTransitionSystem pts) {
		this.post = new HashMap<Integer, Set<Integer>>();
		this.pre = new HashMap<Integer, Set<Integer>>();
		this.unSatAndSatForEachFormula = new HashMap<>();
		this.labellingFormulaForEachState = new HashMap<>();
		this.subset = new HashMap<Formula, StateSets>();
		
		this.pts = pts;

		this.formulaStack = new ArrayList<String>();
	}

	/*
	 * Returns the set of states that are successors to `state` and if not computed
	 * before, adds the entry to a hashtable, post
	 */
	private Set<Integer> Post(Integer state) {
		post.computeIfAbsent(state, k -> pts.getTransitions().stream()
				.filter(t -> t.source == state)
				.map(t -> t.target)
				.collect(Collectors.toSet()));
		return post.get(state);
	}

	/*
	 * Returns the set of states that are predecessors to `state` and if not
	 * computed before, adds the entry to a hashtable, pre
	 */
	private Set<Integer> Pre(Integer state) {
		pre.computeIfAbsent(state, k -> pts.getTransitions().stream()
				.filter(t -> t.target == state)
				.map(t -> t.source)
				.collect(Collectors.toSet()));
		return pre.get(state);
	}

	private StateSets buildResult(Formula formula, Set<Integer> Sat, Set<Integer> unSat) {
		String msg = "Formula: " + formula + "\n\tResult: (Sat) " + Sat.toString() + "\n\tResult: (UnSat) " + unSat.toString();
		formulaStack.add(msg);
		StateSets result = new StateSets(Sat, unSat);
		this.subset.computeIfAbsent(formula, k -> result);
		return result;
	}

	public void printSubResult() {
		formulaStack.stream().forEach(System.out::println);
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
		 * Base Case - Lookup Table
		 */
		if (this.subset.containsKey(formula)) {
			
			return this.subset.get(formula);
		}

		/*
		 * Base Case
		 */
		else if (formula instanceof True) {
			Set<Integer> Sat = pts.getStates();
			Set<Integer> unSat = new HashSet<Integer>();
			unSatAndSatForEachFormula.put(formula,new StateSets(Sat,unSat));
			return buildResult(formula, Sat, unSat);
		}
		/*
		 * Base Case
		 */
		else if (formula instanceof False) {
			Set<Integer> Sat = new HashSet<Integer>();
			Set<Integer> unSat = pts.getStates();
			unSatAndSatForEachFormula.put(formula,new StateSets(Sat,unSat));
			return buildResult(formula, Sat, unSat);
		}
		/*
		 * Base Case
		 */
		else if (formula instanceof AtomicProposition) {
			AtomicProposition aP = (AtomicProposition) formula;

			// Get index that represents this aP
			Integer index = pts.getFields().get(aP.toString());

			// Get states which have 'index' in their labeling set
			Set<Integer> Sat = pts.getLabelling().entrySet().stream()
					.filter(e -> e.getValue().contains(index))
					.map(e -> e.getKey())
					.collect(Collectors.toSet());

			// build unsat
			Set<Integer> unSat = new HashSet<Integer>(pts.getStates());
			unSat.removeAll(Sat);
			unSatAndSatForEachFormula.put(formula,new StateSets(Sat,unSat));
			return buildResult(formula, Sat, unSat);
			
		} else if (formula instanceof And) {
			And f = (And) formula;
			StateSets L = check(f.getLeft());
			StateSets R = check(f.getRight());
			Set<Integer> Sat = new HashSet<Integer>(L.getSat());
			Sat.retainAll(R.getSat());
			Set<Integer> unSat = new HashSet<Integer>(pts.getStates());
			unSat.removeAll(Sat);
			unSatAndSatForEachFormula.put(f,new StateSets(Sat,unSat));
			return buildResult(formula, Sat, unSat);
		} else if (formula instanceof Or) {
			Or f = (Or) formula;
			StateSets L = check(f.getLeft());
			StateSets R = check(f.getRight());
			Set<Integer> Sat = new HashSet<Integer>(L.getSat());
			Sat.addAll(R.getSat());
			Set<Integer> unSat = new HashSet<Integer>(pts.getStates());
			unSat.removeAll(Sat);
			unSatAndSatForEachFormula.put(f,new StateSets(Sat,unSat));
			return buildResult(formula, Sat, unSat);
		} else if (formula instanceof Implies) {
			// !a or b
			Implies f = (Implies) formula;
			StateSets L = check(f.getLeft());
			StateSets R = check(f.getRight());
			Set<Integer> Sat = new HashSet<Integer>(L.getUnSat());
			Sat.addAll(R.getSat());
			Set<Integer> unSat = new HashSet<Integer>(pts.getStates());
			unSat.removeAll(Sat);
			unSatAndSatForEachFormula.put(f,new StateSets(Sat,unSat));
			return buildResult(formula, Sat, unSat);
		} else if (formula instanceof Iff) {
			// (a && b) || (!a && !b)
			Iff f = (Iff) formula;
			StateSets L = check(f.getLeft());
			StateSets R = check(f.getRight());
			// (a && b)
			Set<Integer> LSat = new HashSet<Integer>(L.getSat());
			LSat.retainAll(R.getSat());
			// (!a && !b)
			Set<Integer> RSat = new HashSet<Integer>(L.getUnSat());
			RSat.retainAll(R.getUnSat());
			// (a && b) || (!a && !b)
			Set<Integer> Sat = new HashSet<Integer>(LSat);
			Sat.addAll(RSat);
			// !( (a && b) || (!a && !b) )
			Set<Integer> unSat = new HashSet<Integer>(pts.getStates());
			unSat.removeAll(Sat);
			unSatAndSatForEachFormula.put(f,new StateSets(Sat,unSat));
			return buildResult(formula, Sat, unSat);
			
		} else if (formula instanceof ExistsAlways) {
			ExistsAlways f = (ExistsAlways) formula;
			Set<Integer> Sat = check(f.getFormula()).getSat();
			List<Integer> E = pts.getStates().stream()
					.filter(s -> !Sat.contains(s))
					.collect(Collectors.toList());
			Set<Integer> T = Sat;

			Map<Integer, Integer> count = new HashMap<Integer, Integer>();
			T.stream().forEach(s -> count.computeIfAbsent(s, k -> Post(s).size()));
			while (!E.isEmpty()) {
				Integer sP = E.remove(0);
				Set<Integer> preS = Pre(sP);
				for (Integer s : preS) {
					if (T.contains(s)) {
						count.compute(s, (k, v) -> v - 1);
						if (count.get(s) == 0) {
							T.remove(s);
							E.add(s);
						}
					}
				}
			}
			Set<Integer> unSat = new HashSet<Integer>(pts.getStates());
			unSat.removeAll(T);
			unSatAndSatForEachFormula.put(f,new StateSets(Sat,unSat));
			return buildResult(formula, T, unSat);
		}
		/*
		 * This case is (EF p1) case. On page 333 of the textbook there is an alternate
		 * definition for this formula. The alternate definition is: (true EU p1)
		 */
		else if (formula instanceof ExistsEventually) {
			ExistsEventually eE = (ExistsEventually) formula;
			StateSets S = check(eE.getFormula());

			List<Integer> E = S.getSat().stream().collect(Collectors.toList());
			Set<Integer> T = new HashSet<Integer>(E);
			while (!E.isEmpty()) {
				Integer sP = E.remove(0);
				Pre(sP).stream().filter(s -> !T.contains(s)).forEach(s -> {
					E.add(s);
					T.add(s);
				});
			}

			Set<Integer> unSat = new HashSet<Integer>(pts.getStates());
			unSat.removeAll(T);
			unSatAndSatForEachFormula.put(eE,new StateSets(T,unSat));
			return buildResult(formula, T, unSat);
		} else if (formula instanceof ExistsNext) {
			ExistsNext eN = (ExistsNext) formula;
			StateSets S = check(eN.getFormula()); // recursive part
			Set<Integer> Sat = pts.getTransitions().stream()
					.filter(t -> S.getSat().contains(t.target))
					.map(t -> t.source)
					.collect(Collectors.toSet());
			Set<Integer> unSat = new HashSet<Integer>(pts.getStates());
			unSat.removeAll(Sat);
			unSatAndSatForEachFormula.put(eN,new StateSets(Sat,unSat));
			return buildResult(formula, Sat, unSat);
		} else if (formula instanceof ExistsUntil) {
			ExistsUntil eU = (ExistsUntil) formula;
			StateSets R = check(eU.getRight());
			StateSets L = check(eU.getLeft());

			List<Integer> E = R.getSat().stream().collect(Collectors.toList());
			Set<Integer> T = new HashSet<Integer>(E);
			while (!E.isEmpty()) {
				Integer sP = E.remove(0);
				Pre(sP).stream().filter(L.getSat()::contains).filter(s -> !T.contains(s)).forEach(s -> {
					E.add(s);
					T.add(s);
				});
			}
			Set<Integer> unSat = new HashSet<Integer>(pts.getStates());
			unSat.removeAll(T);
			unSatAndSatForEachFormula.put(eU,new StateSets(T,unSat));
			return buildResult(formula, T, unSat);
		}
		/*
		 * This case is (AG p1) case. On page 333 of the textbook there is an alternate
		 * definition for this formula. The alternate definition is: !(true EU !p1)
		 */
		else if (formula instanceof ForAllAlways) {
			ForAllAlways fA = (ForAllAlways) formula;
			StateSets S = check(fA.getFormula()); // p1

			List<Integer> E = S.getUnSat().stream().collect(Collectors.toList());
			Set<Integer> T = new HashSet<Integer>(E);
			while (!E.isEmpty()) {
				Integer sP = E.remove(0);
				Pre(sP).stream().filter(s -> !T.contains(s)).forEach(s -> {
					E.add(s);
					T.add(s);
				});
			}
			Set<Integer> Sat = new HashSet<Integer>(pts.getStates());
			Sat.removeAll(T);
			unSatAndSatForEachFormula.put(fA,new StateSets(Sat,T));
			return buildResult(formula, Sat, T);
		}
		/*
		 * This case is (AF p1) case. On page 333 of the textbook there is an alternate
		 * definition for this formula. The alternate definition is: !EG!p1 .
		 */
		else if (formula instanceof ForAllEventually) {
			ForAllEventually fAF = (ForAllEventually) formula;
			// In this case we want the !p1 or the unsat states
			Set<Integer> S = check(fAF.getFormula()).getUnSat();
			List<Integer> E = pts.getStates().stream()
					.filter(s -> !S.contains(s))
					.collect(Collectors.toList());
			Set<Integer> T = S;

			Map<Integer, Integer> count = new HashMap<Integer, Integer>();
			T.stream().forEach(s -> count.computeIfAbsent(s, k -> Post(s).size()));
			while (!E.isEmpty()) {
				Integer sP = E.remove(0);
				Set<Integer> preS = Pre(sP);
				for (Integer s : preS) {
					if (T.contains(s)) {
						count.compute(s, (k, v) -> v - 1);
						if (count.get(s) == 0) {
							T.remove(s);
							E.add(s);
						}
					}
				}
			}
			Set<Integer> Sat = new HashSet<Integer>(pts.getStates());
			Sat.removeAll(T);
			unSatAndSatForEachFormula.put(fAF,new StateSets(Sat,T));
			return buildResult(formula, Sat, T);
		}
		/*
		 * this case is the (AX p1) case
		 */
		else if (formula instanceof ForAllNext) {
			ForAllNext fN = (ForAllNext) formula;
			StateSets S = check(fN.getFormula()); // recursive part

			// States that DO NOT satisfy this formula CONTAIN a transition in which the
			// TARGET is NOT CONTAINED in Sat(p1)
			Set<Integer> unSat = pts.getTransitions().stream()
					.filter(t -> !S.getSat().contains(t.target))
					.map(t -> t.source)
					.collect(Collectors.toSet());

			Set<Integer> Sat = new HashSet<Integer>(pts.getStates());
			Sat.removeAll(unSat);
			unSatAndSatForEachFormula.put(fN,new StateSets(Sat,unSat));
			return buildResult(formula, Sat, unSat);
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
			Set<Integer> AND = new HashSet<Integer>(L.getUnSat());
			AND.retainAll(R.getUnSat());

			// Piece2: !(!p2 EU Piece1)
			List<Integer> E = AND.stream().collect(Collectors.toList());
			Set<Integer> T = new HashSet<Integer>(E);
			while (!E.isEmpty()) {
				Integer sP = E.remove(0);
				Pre(sP).stream()
				.filter(L.getUnSat()::contains)
				.filter(s -> !T.contains(s))
				.forEach(s -> {
					E.add(s);
					T.add(s);
				});
			}
			Set<Integer> EU = new HashSet<Integer>(pts.getStates());
			EU.removeAll(T);

			// Piece3: !EG!p2
			List<Integer> F = pts.getStates().stream()
					.filter(s -> !R.getUnSat().contains(s))
					.collect(Collectors.toList());
			Set<Integer> G = R.getUnSat();

			Map<Integer, Integer> count = new HashMap<Integer, Integer>();
			G.stream().forEach(s -> count.computeIfAbsent(s, k -> Post(s).size()));
			while (!F.isEmpty()) {
				Integer sP = F.remove(0);
				Set<Integer> preS = Pre(sP);
				for (Integer s : preS) {
					if (G.contains(s)) {
						count.compute(s, (k, v) -> v - 1);
						if (count.get(s) == 0) {
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
			unSatAndSatForEachFormula.put(fAU,new StateSets(EU,unSat));
			return buildResult(formula, EU, unSat);
		} else if (formula instanceof Not) {
			Not n = (Not) formula;
			StateSets S = check(n.getFormula());
			unSatAndSatForEachFormula.put(n,new StateSets(S.getUnSat(),S.getSat()));
			return buildResult(formula, S.getUnSat(), S.getSat());
		}
		System.err.println("This formula type is unknown");
		return null;
	}
	
	public String getCounterExample(Formula f, Integer s )
	{
		Set<Integer> counterExStates = new HashSet<>();
		StringBuilder outputMsg = new StringBuilder();
		counterExStates.add(s);
        Set<Integer> states = new HashSet<>();
		states.add(s);
		
		outputMsg.append("\nCounter example explanation: ");
		outputMsg.append("\nA counter example to the state " + s + " for the formula (" + f.toString() + ") is: ");
		CounterExampleHelper(f, s, counterExStates, outputMsg);
		outputMsg.append("\n\n");
		outputMsg.append("\nCounter example graph details: \n");
		Set<Transition> newTSTransitions = this.getRelatedTransitions(counterExStates);
		
		Map<Integer, Set<Integer>> newTSLabelling = this.getRelatedLabellings(counterExStates);
		
		
		LabelledPartialTransitionSystem newTS = new LabelledPartialTransitionSystem(counterExStates,newTSTransitions, newTSLabelling);
		
		outputMsg.append(newTS.toString());
		
		outputMsg.append("\n\nStates in the counter example graph with the corresponding formula: \n");
		outputMsg.append(labellingFormulaForEachState.toString());
		
		return outputMsg.toString();
	}

	private void CounterExampleHelper(Formula formula, Integer state, Set<Integer> list, StringBuilder msg)
	{
    
		
   		/*
		 * Base Case
		 */
		if (formula instanceof True) {
			insetToLabellingFormulaForEachStateMap(state, " does not satisfy : " + formula.toString());
			msg.append("\nNo counter example for the formula (True) ");
			return;
		}
		/*
		 * Base Case
		 */
		else if (formula instanceof False) {	
			Set<Integer> formulaUnsat = unSatAndSatForEachFormula.get(formula).getUnSat();
			insetToLabellingFormulaForEachStateMap(state, " does not satisfy : " + formula.toString());
			msg.append("\nThe counter example for the formula (False) is the whole system");
			list.addAll(formulaUnsat);
			return;
		}/*
		 * Base Case
		 */
		else if (formula instanceof AtomicProposition) {
			Set<Integer> formulaUnsat = unSatAndSatForEachFormula.get(formula).getUnSat();
			if(formulaUnsat.contains(state))
			{
				//add to the list and break;
				list.add(state);
				msg.append("\nThe state " + state + " does not satisfy the atomic proposition (" + formula.toString() + ")");
				insetToLabellingFormulaForEachStateMap(state, " does not satisfy : " + formula.toString());
				return;
			}
						
		}
		else if (formula instanceof And) {
			Formula left = ((And) formula).getLeft();
			Formula right = ((And) formula).getRight();
			
			Set<Integer> subLeftFormulaUnsat = unSatAndSatForEachFormula.get(left).getUnSat();
			Set<Integer> subRightFormulaUnsat = unSatAndSatForEachFormula.get(right).getUnSat();
			insetToLabellingFormulaForEachStateMap(state, " does not satisfy : " + formula.toString());
			if(subLeftFormulaUnsat.contains(state))
			{
				list.add(state);
				msg.append("\nThe state " + state + " does not satisfy the left subformula");
				msg.append("\nA counter example to the state " + state + " for the left subformula (" + left.toString() + ") is: ");
				CounterExampleHelper(left, state, list, msg);
			}else if(subRightFormulaUnsat.contains(state))
			{
				list.add(state);
				msg.append("\nThe state " + state + " does not satisfy the right subformula");
				msg.append("\nA counter example to the state " + state + " for the right subformula (" + right.toString() + ") is: ");
				CounterExampleHelper(right, state, list, msg);
			}
		} else if (formula instanceof Or) {
			Formula left = ((Or) formula).getLeft();
			Formula right = ((Or) formula).getRight();
        
			Set<Integer> subLeftFormulaUnsat = unSatAndSatForEachFormula.get(left).getUnSat();
			Set<Integer> subRightFormulaUnsat = unSatAndSatForEachFormula.get(right).getUnSat();
			insetToLabellingFormulaForEachStateMap(state, " does not satisfy : " + formula.toString());
			if(subLeftFormulaUnsat.contains(state) && subRightFormulaUnsat.contains(state))
			{
				list.add(state);
				msg.append("\nThe state " + state + " does not satisfy the left and right subformulas");
				msg.append("\nA counter example to the state " + state + " for the left subformula (" + left.toString() + ") is: ");
				CounterExampleHelper(left, state, list, msg);
				msg.append("\nA counter example to the state " + state + " for the right subformula (" + right.toString() + ") is: ");
				CounterExampleHelper(right, state, list, msg);
			}
		} else if (formula instanceof Implies) {
			Formula left = ((Implies) formula).getLeft();
			Formula right = ((Implies) formula).getRight();

			insetToLabellingFormulaForEachStateMap(state, " does not satisfy : " + formula.toString());

			list.add(state);
			msg.append("\nThe state " + state + " satisfies the left subformula");
			msg.append("\nA witness to the state " + state + " for the left subformula (" + left.toString() + ") is: ");;
			findWitness(left, state, list, msg);
			
			msg.append("\nThe state " + state + " does not satisfy the right subformula");
			msg.append("\nA counter example to the state " + state + " for the right subformula (" + right.toString() + ") is: ");
			CounterExampleHelper(right, state, list, msg);
				
			
		} else if (formula instanceof Iff) {
			//a != b
			Formula left = ((Iff) formula).getLeft();
			Formula right = ((Iff) formula).getRight();
			Set<Integer> subLeftFormulaUnsat = unSatAndSatForEachFormula.get(left).getUnSat();
			Set<Integer> subRightFormulaUnsat = unSatAndSatForEachFormula.get(right).getUnSat();
			Set<Integer> subLeftFormulaSat = unSatAndSatForEachFormula.get(left).getSat();
			Set<Integer> subRightFormulaSat = unSatAndSatForEachFormula.get(right).getSat();
			
			insetToLabellingFormulaForEachStateMap(state, " does not satisfy : " + formula.toString());
			
			if(subLeftFormulaSat.contains(state) && subRightFormulaUnsat.contains(state))
			{
				list.add(state);
				msg.append("\nThe state " + state + " satisfies the left subformula");
				msg.append("\nA witness to the state " + state + " for the left subformula (" + left.toString() + ") is: ");;
				findWitness(left, state, list, msg);
				
				msg.append("\nThe state " + state + " does not satisfy the right subformula");
				msg.append("\nA counter example to the state " + state + " for the right subformula (" + right.toString() + ") is: ");
				CounterExampleHelper(right, state, list, msg);
			}
			if(subLeftFormulaUnsat.contains(state) && subRightFormulaSat.contains(state))
			{
				list.add(state);
				msg.append("\nThe state " + state + " does not satisfy the left subformula");
				msg.append("\nA counter example to the state " + state + " for the left subformula (" + left.toString() + ") is: ");
				CounterExampleHelper(left, state, list, msg);
				
				msg.append("\nThe state " + state + " satisfies the right subformula");
				msg.append("\nA witness to the state " + state + " for the right subformula (" + right.toString() + ") is: ");;
				findWitness(right, state, list, msg);
			}
					
		} else if (formula instanceof ExistsAlways) {
			
			//if the current state does not satisfy then the current state is the counter ex
			//otherwise show all the states in all paths 
			Formula subFormula = ((ExistsAlways) formula).getFormula();
			Set<Integer> subformulaUnsat = unSatAndSatForEachFormula.get(subFormula).getUnSat();
			insetToLabellingFormulaForEachStateMap(state, " does not satisfy : " + formula.toString());
			if(subformulaUnsat.contains(state))
			{
				msg.append("\nThe state " + state + " is a counter example");
				msg.append("\nA counter example to the state " + state + " for the subformula (" + subFormula.toString() + ") is: ");
				CounterExampleHelper(subFormula, state, list, msg);
			}
			else
			{
				Set<Integer> path = new HashSet<>();
				getPath2(state,subformulaUnsat, path);
				
				if(path.isEmpty())
				{
					msg.append("\nThe state " + state + " has no outgoing edges");
				}else
				{				
					msg.append("\nAll the reachable states from state " + state + ": " + path.toString());
					printSatAndUnSatSets(state,subFormula, path, msg);
				}	
				
				for (Iterator<Integer> it = path.iterator(); it.hasNext(); ) 
				{
					Integer s = it.next();
					list.add(s);
					if(subformulaUnsat.contains(s))
					{
						msg.append("\nA counter example to the state " + s + " for the subformula (" + subFormula.toString() + ") is: ");
						CounterExampleHelper(subFormula, s, list, msg);
					}					
				}			
			}
		}
		else if (formula instanceof ForAllAlways) {
			Formula subFormula = ((ForAllAlways) formula).getFormula();
			Set<Integer> subformulaUnsat = unSatAndSatForEachFormula.get(subFormula).getUnSat();
			insetToLabellingFormulaForEachStateMap(state, " does not satisfy : " + formula.toString());
			
			if(subformulaUnsat.contains(state))
			{
				msg.append("\nThe state " + state + " is a counter example");
				msg.append("\nA counter example to the state " + state + " for the subformula (" + subFormula.toString() + ") is: ");
				CounterExampleHelper(subFormula, state, list, msg);
			}
			else
			{
				Set<Integer> allReachableStates = this.getRechableStates(state);
				if(allReachableStates.isEmpty())
				{
					msg.append("\nThe state " + state + " has no outgoing edges");
				}else
				{				
					msg.append("\nAll the reachable states from state " + state + ": " + allReachableStates.toString());
					printSatAndUnSatSets(state,subFormula, allReachableStates, msg);						
				}
				
				//find the parent nodes of the unSatState and add to the list
				Map<Integer,Integer> parent = new HashMap<>();
				Integer unSatState = this.getUnSatOrSatState(state, parent,subformulaUnsat);
				List<Integer> parentList = new LinkedList<>();
				getParentNodes(parentList,parent,unSatState);
				list.addAll(parentList);
				parentList.add(state);
				msg.append("\nThe state " + unSatState + " is one of the states that does not satisfy the subformula " + subFormula);
				msg.append("\nThe path from state " + unSatState + " to state " + state + ": ");
				printPath(parentList, msg);
				msg.append("\nA counter example to the state " + unSatState + " for the subformula (" + subFormula.toString() + ") is: ");
				CounterExampleHelper(subFormula, unSatState, list, msg);
			}
		}
		else if (formula instanceof ExistsEventually) {
			//show all the states on all paths
			Formula subFormula = ((ExistsEventually)formula).getFormula();
			Set<Integer> subformulaUnsat = unSatAndSatForEachFormula.get(subFormula).getUnSat();
			insetToLabellingFormulaForEachStateMap(state, " does not satisfy : " + formula.toString());
			Set<Integer> allReachableStates = this.getRechableStates(state);
			allReachableStates.add(state);
			
			if(allReachableStates.size() == 1)
			{
				msg.append("\nThe state " + state + " has no outgoing edges");
			}else
			{				
				msg.append("\nAll the reachable states from state " + state + ": " + allReachableStates.toString());
				printSatAndUnSatSets(state,subFormula, allReachableStates, msg);
				
			}
			
			for (Iterator<Integer> it = allReachableStates.iterator(); it.hasNext(); ) 
			{
				Integer s = it.next();
				list.add(s);
				if(subformulaUnsat.contains(s))
				{
					msg.append("\nA counter example to the state " + s + " for the subformula (" + subFormula.toString() + ") is: ");
					CounterExampleHelper(subFormula, s, list, msg);
				}
			}
			
		}
		else if (formula instanceof ForAllEventually) {
			Formula subFormula = ((ForAllEventually) formula).getFormula();
			Set<Integer> subformulaUnsat = unSatAndSatForEachFormula.get(subFormula).getUnSat();
			Set<Integer> path = new HashSet<>();
			insetToLabellingFormulaForEachStateMap(state, " does not satisfy : " + formula.toString());
			getPath(state,subformulaUnsat, path);
			path.add(state);
			
			if(path.size() == 1)
			{
				msg.append("\nThe state " + state + " has no outgoing edges");
			}else
			{				
				msg.append("\nThe states on the path from state " + state + ": " + path.toString() );
				printSatAndUnSatSets(state,subFormula, path, msg);
			}
			
			for (Iterator<Integer> it = path.iterator(); it.hasNext(); ) 
			{
				Integer s = it.next();
				list.add(s);
				msg.append("\nA counter example to the state " + s + " for the subformula (" + subFormula.toString() + ") is: ");
				CounterExampleHelper(subFormula, s, list, msg);
			}		
		}
		else if (formula instanceof ExistsNext) 
		{
			Formula f = ((ExistsNext) formula).getFormula();
			
			insetToLabellingFormulaForEachStateMap(state, " does not satisfy : " + formula.toString());
			Set<Integer> subPostStates = Post(state);
		     
		    Set<Integer> formulaUnsat = unSatAndSatForEachFormula.get(f).getUnSat();
			
		
			if(subPostStates.isEmpty())
			{
				msg.append("\nThe state " + state + " has no outgoing edges\n");
			}
			else
			{
				 msg.append("\nThe post states of " + state + ": " + subPostStates.toString() );
				 printSatAndUnSatSets(state,f, subPostStates, msg);
			}
			for (Iterator<Integer> it = subPostStates.iterator(); it.hasNext(); ) 
			{
		       	Integer s = it.next();
				if(formulaUnsat.contains(s))
				{
					list.add(s);
					msg.append("\nA counter example to the state " + s + " for the subformula (" + f.toString() + ") is: ");
					CounterExampleHelper(f, s, list, msg);
				}		    	
			}
		} 		
		else if (formula instanceof ForAllNext) {
			Formula f = ((ForAllNext) formula).getFormula();			
			Set<Integer> subPostStates = Post(state);
			
			insetToLabellingFormulaForEachStateMap(state, " does not satisfy : " + formula.toString());	    
		    Set<Integer> formulaUnsat = unSatAndSatForEachFormula.get(f).getUnSat();			
			
		    msg.append("\nThe post states of " + state + ": " + subPostStates.toString() );
			printSatAndUnSatSets(state,f, subPostStates, msg);
			
			for (Iterator<Integer> it = subPostStates.iterator(); it.hasNext(); ) 
			{
		       	Integer s = it.next();
				if(formulaUnsat.contains(s))
				{
					list.add(s);
					msg.append("\nA counter example to the state " + s + " for the subformula (" + f.toString() + ") is: ");
					CounterExampleHelper(f, s, list, msg);
					break;
				}
		    	
			}		
		}
		else if (formula instanceof ForAllUntil) {
			//do this first
			// a AU b
			
			Formula left = ((ForAllUntil)formula).getLeft();
			Formula right = ((ForAllUntil)formula).getRight();
			
			Set<Integer> subLeftFormulaUnsat = unSatAndSatForEachFormula.get(left).getUnSat();
			
			insetToLabellingFormulaForEachStateMap(state, " does not satisfy : " + formula.toString());
			
			if(subLeftFormulaUnsat.contains(state))
			{
				//does not satisfy a   
				list.add(state);
				msg.append("\nThe state " + state + " does not satisfy the left subformula");
				msg.append("\nA counter example to the state " + state + " for the left subformula (" + left.toString() + ") is: ");;
				CounterExampleHelper(left, state, list, msg);
			}else
			{
				//find a path where the contigues states satisfy a and the last state does not b
				
				Set<Integer> path = new HashSet<>();
				Set<Integer> sat_a = unSatAndSatForEachFormula.get(left).getSat();
				Set<Integer> sat_b = unSatAndSatForEachFormula.get(right).getSat();
				Integer unSatBState = getPath3(state,sat_a,sat_b,path);
				path.add(state);
			
				if(path.size() == 1)
				{
					msg.append("\nThe state " + state + " has no outgoing edges");
				}else
				{				
					msg.append("\nThe states on the path from state " + state + ": " + path.toString() );
					printSatAndUnSatSets(state, right, path, msg);
				}
			
			
				list.addAll(path);
				msg.append("\nA counter example to the state " + unSatBState + " for the subformula (" + right.toString() + ") is: ");
				CounterExampleHelper(right, unSatBState, list, msg);
					
						
			}
			
		}
		else if (formula instanceof ExistsUntil) {
			// a AU b
			
			Formula left = ((ExistsUntil)formula).getLeft();
			Formula right = ((ExistsUntil)formula).getRight();
			
			Set<Integer> subLeftFormulaUnsat = unSatAndSatForEachFormula.get(left).getUnSat();
			
			insetToLabellingFormulaForEachStateMap(state, " does not satisfy : " + formula.toString());
			
			if(subLeftFormulaUnsat.contains(state))
			{
				//does not satisfy a   
				list.add(state);
				msg.append("\nThe state " + state + " does not satisfy the left subformula");
				msg.append("\nA counter example to the state " + state + " for the left subformula (" + left.toString() + ") is: ");;
				CounterExampleHelper(left, state, list, msg);
			}else
			{
				//find all paths where the contigues states satisfy a and the last state does not b
				
				Set<Integer> path = new HashSet<>();
				Set<Integer> sat_a = unSatAndSatForEachFormula.get(left).getSat();
				Set<Integer> sat_b = unSatAndSatForEachFormula.get(right).getSat();
				getPath4(state,sat_a,sat_b,path);
				path.add(state);
			
				if(path.size() == 1)
				{
					msg.append("\nThe state " + state + " has no outgoing edges");
				}else
				{				
					msg.append("\nThe states on the path from state " + state + ": " + path.toString() );
					printSatAndUnSatSets(state, right, path, msg);
				}
				list.addAll(path);
				for (Iterator<Integer> it = path.iterator(); it.hasNext(); ) 
				{
			       	Integer s = it.next();
					if(!sat_a.contains(s))
					{
						msg.append("\nA counter example to the state " + s + " for the subformula (" + right.toString() + ") is: ");
						CounterExampleHelper(right, s, list, msg);
					}
					
				}						
			}
		}
		else if (formula instanceof Not) {
			// ! AX EX Red = EX !(EX Red) = EX AX !Red
			Formula f = ((Not)formula).getFormula();
			insetToLabellingFormulaForEachStateMap(state, " does not satisfy : " + formula.toString());

			list.add(state);
			msg.append("\nA witness to the state " + state + " for the subformula (" + f.toString() + ") is: ");
			
			findWitness(f, state, list, msg);
			return;
		} 
	}
	
	private void findWitness(Formula formula, Integer state, Set<Integer> list, StringBuilder msg)
	{
    
		
   		/*
		 * Base Case
		 */
		if (formula instanceof True) {
			insetToLabellingFormulaForEachStateMap(state, " satisfies : " + formula.toString());
			msg.append("\nThe witness for the formula (True) is the whole system");
			return;
		}
		/*
		 * Base Case
		 */
		else if (formula instanceof False) {	
			Set<Integer> formulaUnsat = unSatAndSatForEachFormula.get(formula).getUnSat();
			insetToLabellingFormulaForEachStateMap(state, " satisfies : " + formula.toString());
			msg.append("\nNo witness for the formula (False) ");
			list.addAll(formulaUnsat);
			return;
		}/*
		 * Base Case
		 */
		else if (formula instanceof AtomicProposition) {
			Set<Integer> formulaSat = unSatAndSatForEachFormula.get(formula).getSat();
			if(formulaSat.contains(state))
			{
				//add to the list and break;
				list.add(state);
				msg.append("\nThe state " + state + " does satisfy the atomic proposition (" + formula.toString() + ")");
				insetToLabellingFormulaForEachStateMap(state, " satisfies : " + formula.toString());
				return;
			}
						
		}
		else if (formula instanceof And) {
			Formula left = ((And) formula).getLeft();
			Formula right = ((And) formula).getRight();
			
			Set<Integer> subLeftFormulaSat = unSatAndSatForEachFormula.get(left).getSat();
			Set<Integer> subRightFormulaSat = unSatAndSatForEachFormula.get(right).getSat();
			insetToLabellingFormulaForEachStateMap(state, " satisfies : " + formula.toString());
			if(subLeftFormulaSat.contains(state) && subRightFormulaSat.contains(state))
			{
				list.add(state);
				msg.append("\nThe state " + state + " satisfies the left and right subformulas");
				msg.append("\nA witness to the state " + state + " for the left subformula (" + left.toString() + ") is: ");
				findWitness(left, state, list, msg);
				msg.append("\nA witness to the state " + state + " for the right subformula (" + right.toString() + ") is: ");
				findWitness(right, state, list, msg);
			}
			
		} else if (formula instanceof Or) {
			Formula left = ((Or) formula).getLeft();
			Formula right = ((Or) formula).getRight();
        
			Set<Integer> subLeftFormulaSat = unSatAndSatForEachFormula.get(left).getSat();
			Set<Integer> subRightFormulaSat = unSatAndSatForEachFormula.get(right).getSat();
			insetToLabellingFormulaForEachStateMap(state, " satisfies : " + formula.toString());
			if(subLeftFormulaSat.contains(state))
			{
				list.add(state);
				msg.append("\nThe state " + state + " does satisfy the left subformula");
				msg.append("\nA witness to the state " + state + " for the left subformula (" + left.toString() + ") is: ");;
				findWitness(left, state, list, msg);
			}else if(subRightFormulaSat.contains(state))
			{
				list.add(state);
				msg.append("\nThe state " + state + " does satisfy the right subformula");
				msg.append("\nA witness to the state " + state + " for the right subformula (" + right.toString() + ") is: ");
				findWitness(right, state, list, msg);
			}	
			
		} else if (formula instanceof Implies) {
			//!(!a or b) = a and !b

			Formula left = ((Implies) formula).getLeft();
			Formula right = ((Implies) formula).getRight();
			insetToLabellingFormulaForEachStateMap(state, " satisfies : " + formula.toString());
			
			list.add(state);
			msg.append("\nThe state " + state + "does not satisfy the left subformula");
			msg.append("\nA counter example to the state " + state + " for the left subformula (" + left.toString() + ") is: ");;
			CounterExampleHelper(left, state, list, msg);
			
			msg.append("\nThe state " + state + " satisfies the right subformula");
			msg.append("\nA witness to the state " + state + " for the right subformula (" + right.toString() + ") is: ");;
			findWitness(right, state, list, msg);
		
			
		} else if (formula instanceof Iff) {
			Formula left = ((Iff) formula).getLeft();
			Formula right = ((Iff) formula).getRight();
			
			Set<Integer> subLeftFormulaSat = unSatAndSatForEachFormula.get(left).getSat();
			Set<Integer> subRightFormulaSat = unSatAndSatForEachFormula.get(right).getSat();
			insetToLabellingFormulaForEachStateMap(state, " satisfies : " + formula.toString());
			
			if(subLeftFormulaSat.contains(state) && subRightFormulaSat.contains(state))
			{
				list.add(state);
				msg.append("\nThe state " + state + " satisfies left and right subformulas");
				msg.append("\nA witness to the state " + state + " for the left subformula (" + left.toString() + ") is: ");
				findWitness(left, state, list, msg);
				msg.append("\nA witness to the state " + state + " for the right subformula (" + right.toString() + ") is: ");
				findWitness(right, state, list, msg);
			}	
			
		} else if (formula instanceof ForAllAlways) {
			
			//show all the states in all paths satisfies the subformula
			Formula subFormula = ((ForAllAlways) formula).getFormula();
			Set<Integer> subformulaSat = unSatAndSatForEachFormula.get(subFormula).getSat();
			insetToLabellingFormulaForEachStateMap(state, " satisfies : " + formula.toString());

			Set<Integer> allReachableStates = this.getRechableStates(state);
			if(allReachableStates.isEmpty())
			{
				msg.append("\nThe state " + state + " has no outgoing edges");
			}else
			{				
				msg.append("\nAll the reachable states from state " + state + ": " + allReachableStates.toString());
				printSatAndUnSatSets(state,subFormula, allReachableStates, msg);						
			}
			
			
			for (Iterator<Integer> it = allReachableStates.iterator(); it.hasNext(); ) 
			{
				Integer s = it.next();
				list.add(s);
				if(subformulaSat.contains(s))
				{
					msg.append("\nA witness to the state " + s + " for the subformula (" + subFormula.toString() + ") is: ");
					findWitness(subFormula, s, list, msg);									
				}			
			}
		}
		else if (formula instanceof  ExistsAlways) {
			Formula subFormula = ((ExistsAlways) formula).getFormula();
			Set<Integer> subformulaSat = unSatAndSatForEachFormula.get(subFormula).getSat();
			insetToLabellingFormulaForEachStateMap(state, " satisfies : " + formula.toString());
			
			Set<Integer> path = new HashSet<>();
			getPath2(state,subformulaSat, path);
			path.add(state);	
			
			if(path.isEmpty())
			{
				msg.append("\nThe state " + state + " has no outgoing edges");
					
			}else
			{				
				msg.append("\nAll the reachable states from state " + state + ": " + path.toString());
				printSatAndUnSatSets(state,subFormula, path, msg);
			}	
				
			for (Iterator<Integer> it = path.iterator(); it.hasNext(); ) 
			{
				Integer s = it.next();
				list.add(s);
				if(subformulaSat.contains(s))
				{
					msg.append("\nA witness to the state " + s + " for the subformula (" + subFormula.toString() + ") is: ");
					findWitness(subFormula, s, list, msg);									
				}			
			}				
		}
		else if (formula instanceof ForAllEventually) {
			//show all the states on all paths
			Formula subFormula = ((ForAllEventually)formula).getFormula();
			Set<Integer> subformulaSat = unSatAndSatForEachFormula.get(subFormula).getSat();
			insetToLabellingFormulaForEachStateMap(state, " satisfies : " + formula.toString());
			
			
			if(subformulaSat.contains(state))
			{
				msg.append("\nThe state " + state + " is a witness");
				msg.append("\nA witness to the state " + state + " for the subformula (" + subFormula.toString() + ") is: ");
				findWitness(subFormula, state, list, msg);
			}
			else
			{
				Set<Integer> path = new HashSet<>();
				getPath2(state,subformulaSat, path);
				
				if(path.isEmpty())
				{
					msg.append("\nThe state " + state + " has no outgoing edges");
				}else
				{				
					msg.append("\nAll the reachable states from state " + state + ": " + path.toString());
					printSatAndUnSatSets(state,subFormula, path, msg);
				}	
				
				for (Iterator<Integer> it = path.iterator(); it.hasNext(); ) 
				{
					Integer s = it.next();
					list.add(s);
					if(subformulaSat.contains(s))
					{
						msg.append("\nA witness to the state " + s + " for the subformula (" + subFormula.toString() + ") is: ");
						findWitness(subFormula, s, list, msg);
					}					
				}			
			}
			
		}
		else if (formula instanceof  ExistsEventually) {
			Formula subFormula = ((ExistsEventually) formula).getFormula();
			Set<Integer> subformulaSat = unSatAndSatForEachFormula.get(subFormula).getSat();
			Set<Integer> path = new HashSet<>();
			insetToLabellingFormulaForEachStateMap(state, " satisfies : " + formula.toString());
			
			if(subformulaSat.contains(state))
			{
				
				msg.append("\nThe state " + state + " is a witness");
				msg.append("\nA witness to the state " + state + " for the subformula (" + subFormula.toString() + ") is: ");
				findWitness(subFormula, state, list, msg);
			}else
			{
				Set<Integer> allReachableStates = this.getRechableStates(state);
				if(allReachableStates.isEmpty())
				{
					msg.append("\nThe state " + state + " has no outgoing edges");
				}else
				{				
					msg.append("\nAll the reachable states from state " + state + ": " + allReachableStates.toString());
					printSatAndUnSatSets(state,subFormula, allReachableStates, msg);						
				}
				
				//find the parent nodes of the satState and add to the list
				Map<Integer,Integer> parent = new HashMap<>();
				Integer satState = this.getUnSatOrSatState(state, parent,subformulaSat);
				List<Integer> parentList = new LinkedList<>();
				getParentNodes(parentList,parent,satState);
				list.addAll(parentList);
				parentList.add(state);
				msg.append("\nThe state " + satState + " is one of the states that satisfies the subformula " + subFormula);
				msg.append("\nThe path from state " + satState + " to state " + state + ": ");
				printPath(parentList, msg);
				msg.append("\nA witness to the state " + satState + " for the subformula (" + subFormula.toString() + ") is: ");
				findWitness(subFormula, satState, list, msg);
			
				
			}
				
		}
		else if (formula instanceof ForAllNext) 
		{
			Formula f = ((ForAllNext) formula).getFormula();
			
			insetToLabellingFormulaForEachStateMap(state, " satisfies : " + formula.toString());
			Set<Integer> subPostStates = Post(state);
		     
		    Set<Integer> formulaSat = unSatAndSatForEachFormula.get(f).getSat();
			
		
			if(subPostStates.isEmpty())
			{
				msg.append("\nThe state " + state + " has no outgoing edges\n");
			}
			else
			{
				 msg.append("\nThe post states of " + state + ": " + subPostStates.toString() );
				 printSatAndUnSatSets(state,f, subPostStates, msg);
			}
			for (Iterator<Integer> it = subPostStates.iterator(); it.hasNext(); ) 
			{
		       	Integer s = it.next();
				if(formulaSat.contains(s))
				{
					list.add(s);
					msg.append("\nA witness to the state " + s + " for the subformula (" + f.toString() + ") is: ");
					findWitness(f, s, list, msg);
				}		    	
			}
		} 		
		else if (formula instanceof ExistsNext) {
			Formula f = ((ExistsNext) formula).getFormula();			
			Set<Integer> subPostStates = Post(state);
			
			insetToLabellingFormulaForEachStateMap(state, " satisfies : " + formula.toString());	    
		    Set<Integer> formulaSat = unSatAndSatForEachFormula.get(f).getSat();			
			
		    msg.append("\nThe post states of " + state + ": " + subPostStates.toString() );
			printSatAndUnSatSets(state,f, subPostStates, msg);
			
			for (Iterator<Integer> it = subPostStates.iterator(); it.hasNext(); ) 
			{
		       	Integer s = it.next();
				if(formulaSat.contains(s))
				{
					list.add(s);
					msg.append("\nA witness to the state " + s + " for the subformula (" + f.toString() + ") is: ");
					findWitness(f, s, list, msg);
					break;
				}
		    	
			}		
		}
	else if (formula instanceof ExistsUntil) {
			// a AU b
			
			Formula left = ((ExistsUntil)formula).getLeft();
			Formula right = ((ExistsUntil)formula).getRight();
			
			Set<Integer> subLeftFormulaSat = unSatAndSatForEachFormula.get(left).getSat();
			
			insetToLabellingFormulaForEachStateMap(state, " satisfies : " + formula.toString());
			
			if(subLeftFormulaSat.contains(state))
			{
				//does not satisfy a   
				list.add(state);
				msg.append("\nThe state " + state + " satisfies the left subformula");
				msg.append("\nA witness to the state " + state + " for the left subformula (" + left.toString() + ") is: ");;
				findWitness(left, state, list, msg);
			}else
			{
				//find a path where the contigues states satisfy a and the last state does not b
				
				Set<Integer> path = new HashSet<>();
				Set<Integer> sat_a = unSatAndSatForEachFormula.get(left).getSat();
				Set<Integer> sat_b = unSatAndSatForEachFormula.get(right).getSat();
				Integer satBState = getPath3(state,sat_a,sat_b,path);
				path.add(state);
			
				if(path.size() == 1)
				{
					msg.append("\nThe state " + state + " has no outgoing edges");
				}else
				{				
					msg.append("\nThe states on the path from state " + state + ": " + path.toString() );
					printSatAndUnSatSets(state, right, path, msg);
				}
			
			
				list.addAll(path);
				msg.append("\nA witness to the state " + satBState + " for the subformula (" + right.toString() + ") is: ");
				CounterExampleHelper(right, satBState, list, msg);
					
						
			}
			
		}
		else if (formula instanceof ForAllUntil) {
			// a AU b
			
			Formula left = ((ForAllUntil)formula).getLeft();
			Formula right = ((ForAllUntil)formula).getRight();
			
			Set<Integer> subLeftFormulaSat = unSatAndSatForEachFormula.get(left).getSat();
			
			insetToLabellingFormulaForEachStateMap(state, " satisfies : " + formula.toString());
			
			if(subLeftFormulaSat.contains(state))
			{
				//does not satisfy a   
				list.add(state);
				msg.append("\nThe state " + state + " satisfies the left subformula");
				msg.append("\nA witness to the state " + state + " for the left subformula (" + left.toString() + ") is: ");;
				CounterExampleHelper(left, state, list, msg);
			}else
			{
				//find all paths where the contigues states satisfy a and the last state does not b
				
				Set<Integer> path = new HashSet<>();
				Set<Integer> sat_a = unSatAndSatForEachFormula.get(left).getSat();
				Set<Integer> sat_b = unSatAndSatForEachFormula.get(right).getSat();
				getPath4(state,sat_a,sat_b,path);
				path.add(state);
			
				if(path.size() == 1)
				{
					msg.append("\nThe state " + state + " has no outgoing edges");
				}else
				{				
					msg.append("\nThe states on the path from state " + state + ": " + path.toString() );
					printSatAndUnSatSets(state, right, path, msg);
				}
				list.addAll(path);
				for (Iterator<Integer> it = path.iterator(); it.hasNext(); ) 
				{
			       	Integer s = it.next();
					if(!sat_a.contains(s))
					{
						msg.append("\nA witness to the state " + s + " for the subformula (" + right.toString() + ") is: ");
						CounterExampleHelper(right, s, list, msg);
					}
					
				}						
			}
		}
		else if (formula instanceof Not) {
			// ! AX EX Red = EX !(EX Red) = EX AX !Red
			Formula f = ((Not)formula).getFormula();
			insetToLabellingFormulaForEachStateMap(state, " satisfies : " + formula.toString());

			
			list.add(state);
			msg.append("\nA counter example to the state " + state + " for the subformula (" + f.toString() + ") is: ");
			
			CounterExampleHelper(f, state, list, msg);
			return;
		} 
	}

	private void getParentNodes(List<Integer> list, Map<Integer,Integer> parent, Integer s)
	{
		Integer parentNode = parent.get(s);
		if(parentNode == null)
		{
			return;
		}
		list.add(s);
		getParentNodes(list,parent,parentNode);
	}
	
	
	
	private void getPath(Integer state, Set<Integer> subformulaUnsat,Set<Integer> path)
	{
		if(Post(state).isEmpty()) {
			return;
		}
		else {
			int i = 0;
			
			for (Iterator<Integer> it = Post(state).iterator(); it.hasNext(); ) 
			{
				Integer n = it.next();
				if(subformulaUnsat.contains(n) && !path.contains(n)) {
					path.add(n);
					getPath(n,subformulaUnsat,path);
					return;
				}
				else {
					i++;
				}
				
			}
			if( i == Post(state).size())
			{
				path.remove(state);
				
			}	
		}
		
	}
	
	private void getPath2(Integer state, Set<Integer> subformulaUnsat,Set<Integer> path)
	{
		if(Post(state).isEmpty()) {
			return;
		}
		else 
		{			
			for (Iterator<Integer> it = Post(state).iterator(); it.hasNext(); ) 
			{
				Integer n = it.next();
				if(!path.contains(n))
				{
					path.add(n);
					if(!subformulaUnsat.contains(n)) 
					{
						getPath2(n,subformulaUnsat,path);
					}
				}
			}			
		}		
	}
	
	private Integer getPath3(Integer state, Set<Integer> Sata,Set<Integer> Satb,Set<Integer> path)
	{
		if(Post(state).isEmpty()) {
			return state;
		}
		else {
			int i = 0;
			
			for (Iterator<Integer> it = Post(state).iterator(); it.hasNext(); ) 
			{
				
				Integer n = it.next();
				if(Sata.contains(n) && !path.contains(n)) {
					path.add(n);
					getPath3(n,Sata,Satb,path);					
				}
				else if(Satb.contains(n)) {
					i++;
					
				}else if(!Sata.contains(n)&&!Satb.contains(n)) {
					path.add(n);
					return n;
				}
				
			}
			if( i == Post(state).size())
			{
				path.remove(state);
				
			}	
				
		}
		return state;
		
	}
	
	private void getPath4(Integer state, Set<Integer> Sata,Set<Integer> Satb,Set<Integer> path)
	{
		if(Post(state).isEmpty()) {
			return;
		}
		else {
			int i = 0;
			
			for (Iterator<Integer> it = Post(state).iterator(); it.hasNext(); ) 
			{
				
				Integer n = it.next();
				if(Sata.contains(n) && !path.contains(n)) {
					path.add(n);
					getPath4(n,Sata,Satb,path);					
				}
				else if(Satb.contains(n)) {
					i++;
					
				}else if(!Sata.contains(n)&&!Satb.contains(n)) {
					path.add(n);				
				}
				
			}
			if( i == Post(state).size())
			{
				path.remove(state);
				
			}	
				
		}
	}
	
	
	
	private Set<Transition> getRelatedTransitions( Set<Integer> list)
	{
		Set<Transition> result = new HashSet<Transition>();
		for (Iterator<Transition> it = this.pts.getTransitions().iterator(); it.hasNext(); ) 
		{
			Transition t = it.next();
			if(list.contains(t.getSource()) && list.contains(t.getTarget()))
			{
				result.add(t);
			}
			
		}
		
		return result;
	}
	
	private Map<Integer, Set<Integer>> getRelatedLabellings( Set<Integer> list)
	{
		Map<Integer, Set<Integer>> result = new HashMap<>();
		for (Iterator<Integer> it = this.pts.getLabelling().keySet().iterator(); it.hasNext(); ) 
		{
			Integer s = it.next();
			if(list.contains(s))
			{
				result.put(s, this.pts.getLabelling().get(s));
			}
			
		}
		
		return result;
	}
	
	
	public Set<Integer> getPostStates(Integer s)
	{
		return this.Post(s);
	}
	
	private Integer getUnSatOrSatState(Integer s, Map<Integer,Integer> parent, Set<Integer> unSatSubFormula)
    {
        // Mark all the vertices as not visited(By default
        // set as false)
		
        
        Set<Integer> visited = new HashSet<>();
        // Create a queue for BFS
        LinkedList<Integer> queue = new LinkedList<Integer>();
       
        
        // Mark the current node as visited and enqueue it
        queue.add(s);
        visited.add(s);

        while (queue.size() != 0)
        {
            // Dequeue a vertex from queue and print it
            s = queue.poll();
            
            if(unSatSubFormula.contains(s))
            {
            	return s;
            }
            // Get all adjacent vertices of the dequeued vertex s
            // If a adjacent has not been visited, then mark it
            // visited and enqueue it
            Iterator<Integer> i = Post(s).iterator();
            while (i.hasNext())
            {
                int n = i.next();
                if (!visited.contains(n))
                {
                	parent.put(n, s);
                	visited.add(n);
                    queue.add(n);
                }
            }
        }
        return s;
    }

	private Set<Integer> getRechableStates(Integer s)
    {
        // Mark all the vertices as not visited(By default
        // set as false)
		
        
        Set<Integer> visited = new HashSet<>();
        // Create a queue for BFS
        LinkedList<Integer> queue = new LinkedList<Integer>();
        Set<Integer> result = new HashSet<>();
        
        // Mark the current node as visited and enqueue it
        queue.add(s);
        visited.add(s);

        while (queue.size() != 0)
        {
            // Dequeue a vertex from queue 
            s = queue.poll();
    		result.add(s);
			
			
            
            // Get all adjacent vertices of the dequeued vertex s
            // If a adjacent has not been visited, then mark it
            // visited and enqueue it
            Iterator<Integer> i = Post(s).iterator();
            while (i.hasNext())
            {
                int n = i.next();
                if (!visited.contains(n))
                {
                   	visited.add(n);
                    queue.add(n);
                }
            }
        }
        return result;
    }
	
	private void insetToLabellingFormulaForEachStateMap (Integer state, String SatUnSatFormula)
	{
		if(labellingFormulaForEachState.containsKey(state))
		{
			String oldFormula = labellingFormulaForEachState.get(state);
			labellingFormulaForEachState.put(state, oldFormula + " ,and" + SatUnSatFormula.toString());
		}else
		{
			labellingFormulaForEachState.put(state, SatUnSatFormula.toString());
		}
	}
	
	private void printSatAndUnSatSets(Integer state, Formula subFormula, Set<Integer> allReachableStates, StringBuilder msg)
	{
		Set<Integer> sat = new HashSet<>();
		Set<Integer> unSat = new HashSet<>();
		Set<Integer> subformulaUnsat = unSatAndSatForEachFormula.get(subFormula).getUnSat();
		
		for (Iterator<Integer> it = allReachableStates.iterator(); it.hasNext(); ) 
		{
			Integer s = it.next();
			if(subformulaUnsat.contains(s))
			{
				unSat.add(s);
			}else
			{
				sat.add(s);
			}
		}
	
		msg.append("\nThe states that satisfy the formula (" + subFormula + ") : ");
		msg.append(sat.toString());
		msg.append("\nThe states that do not satisfy the formula (" + subFormula + ") : ");
		msg.append(unSat.toString());	
	}
	private void printPath(List<Integer> path, StringBuilder msg)
	{
		int size = path.size();
		for(int i=0 ; i < size ; i++)
		{
			msg.append(path.get(i));
			if(i != size - 1)
			{
				msg.append(" -> ");
			}
		}
	}
}