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

import config.LabelledPartialTransitionSystem;
import ctl.And;
import ctl.False;
import ctl.Formula;
import ctl.Or;
import ctl.*;
import java.util.ArrayList;
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
		String msg = "Formula: " + formula + "\n\tResult: " + Sat.toString();
		formulaStack.add(msg);
		StateSets result = new StateSets(Sat, unSat);
		this.subset.computeIfAbsent(formula, k -> result);
		return result;
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
				for (Integer s : Pre(sP)) {
					if (T.contains(s)) {
						count.compute(s, (k, v) -> v - 1);
						if (count.get(s).equals(0)) {
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
				Pre(sP).stream()
				.filter(s -> !T.contains(s))
				.forEach(s -> {
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
				Pre(sP).stream().filter(L.getSat()::contains)
				.filter(s -> !T.contains(s))
				.forEach(s -> {
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
				for (Integer s : Pre(sP)) {
					if (G.contains(s)) {
						count.compute(s, (k, v) -> v - 1);
						if (count.get(s) == 0) {
							G.remove(s);
							F.add(s);
						}
					}
				}
			}
			Set<Integer> notG = new HashSet<Integer>(pts.getStates());
			notG.removeAll(G);

			// Piece4: Piece2 && Piece3
			EU.retainAll(notG);

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
		//This should be unreachable
		System.err.println("This formula type is unknown");
		return null;
	}
	
	//Counter Example methods
	/**
	 * This method returns a counter example for the inserted formula starting from the given state
	 * 
	 * @param f - inserted formula
	 * @param s - state to find a counter example
	 * @return a string containing all the details of the counter example
	 */
	public String getCounterExample(Formula f, Integer s )
	{
		
		Set<Integer> counterExStates = new HashSet<>();
		StringBuilder outputMsg = new StringBuilder();
		counterExStates.add(s);
		
		//calling the counter example helper to recursively find a counter examples for the sub-formulas
		outputMsg.append("\nCounter example explanation: ");
		outputMsg.append("\nA counter example to the state " + s + " for the formula (" + f.toString() + ") is: ");
		CounterExampleHelper(f, s, counterExStates, outputMsg);
		
		//constructing LabelledPartialTransitionSystem with the collected states in the counter example 
		outputMsg.append("\n\n");
		outputMsg.append("\nCounter example graph details: \n");		
		Set<Transition> newTSTransitions = this.getRelatedTransitions(counterExStates);
		
		Map<Integer, Set<Integer>> newTSLabelling = this.getRelatedLabellings(counterExStates);
		
		
		LabelledPartialTransitionSystem newTS = new LabelledPartialTransitionSystem(counterExStates,newTSTransitions, newTSLabelling);
		
		outputMsg.append(newTS.toString());
		
		//printing each state in the counter example with the corresponding sat and unsat formula
		outputMsg.append("\n\nStates in the counter example graph with the corresponding formula: \n");
		outputMsg.append(labellingFormulaForEachState.toString());
		
		return outputMsg.toString();
	}
	
	/**
	 * This is a recursive method to find a counter example for each sub-formulas
	 * This method also calls findWitness method is case the formula contains Not operator
	 * 
	 * @param formula - main formula or sub formula
	 * @param state   - initial state or any other state
	 * @param list    - a list to collect the states that are in the counter example
	 * @param msg     - a string buffer to collect the messages
	 */
	private void CounterExampleHelper(Formula formula, Integer state, Set<Integer> list, StringBuilder msg)
	{
    
		
   		/*
		 * Base Case
		 */
		if (formula instanceof True) {
			//No counter example exist
			insetToLabellingFormulaForEachStateMap(state, " does not satisfy : " + formula.toString());
			msg.append("\nNo counter example for the formula (True) ");
			return;
		}
		/*
		 * Base Case
		 */
		else if (formula instanceof False) {	
			//The whole system
			Set<Integer> formulaUnsat = unSatAndSatForEachFormula.get(formula).getUnSat();
			insetToLabellingFormulaForEachStateMap(state, " does not satisfy : " + formula.toString());
			msg.append("\nThe counter example for the formula (False) is the whole system");
			list.addAll(formulaUnsat);
			return;
		}/*
		 * Base Case
		 */
		else if (formula instanceof AtomicProposition) {
			//current state if it does not satisfy the atomic proposition     
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
			//get the left and right sub-formulas with there corresponding unSat state sets
			Formula left = ((And) formula).getLeft();
			Formula right = ((And) formula).getRight();
			
			Set<Integer> subLeftFormulaUnsat = unSatAndSatForEachFormula.get(left).getUnSat();
			Set<Integer> subRightFormulaUnsat = unSatAndSatForEachFormula.get(right).getUnSat();
			
			insetToLabellingFormulaForEachStateMap(state, " does not satisfy : " + formula.toString());
			
			if(subLeftFormulaUnsat.contains(state))
			{
				//if the current state does not satisfy the left sub-formula 
				//then find a counter example for the left sub-formula with the current state
				list.add(state);
				msg.append("\nThe state " + state + " does not satisfy the left subformula");
				msg.append("\nA counter example to the state " + state + " for the left subformula (" + left.toString() + ") is: ");
				CounterExampleHelper(left, state, list, msg);
			}else if(subRightFormulaUnsat.contains(state))
			{
				//if the current state does not satisfy the right sub-formula 
				//then find a counter example for the right sub-formula with the current state
				list.add(state);
				msg.append("\nThe state " + state + " does not satisfy the right subformula");
				msg.append("\nA counter example to the state " + state + " for the right subformula (" + right.toString() + ") is: ");
				CounterExampleHelper(right, state, list, msg);
			}
		} else if (formula instanceof Or) {
			//get the left and right sub-formulas with there corresponding unSat state sets
			Formula left = ((Or) formula).getLeft();
			Formula right = ((Or) formula).getRight();
        
			Set<Integer> subLeftFormulaUnsat = unSatAndSatForEachFormula.get(left).getUnSat();
			Set<Integer> subRightFormulaUnsat = unSatAndSatForEachFormula.get(right).getUnSat();
			
			insetToLabellingFormulaForEachStateMap(state, " does not satisfy : " + formula.toString());
			
			if(subLeftFormulaUnsat.contains(state) && subRightFormulaUnsat.contains(state))
			{
				//if the current state does not satisfy the right and left sub-formulas 
				//then find a counter example for the right and left sub-formulas with the current state
				list.add(state);
				msg.append("\nThe state " + state + " does not satisfy the left and right subformulas");
				msg.append("\nA counter example to the state " + state + " for the left subformula (" + left.toString() + ") is: ");
				CounterExampleHelper(left, state, list, msg);
				msg.append("\nA counter example to the state " + state + " for the right subformula (" + right.toString() + ") is: ");
				CounterExampleHelper(right, state, list, msg);
			}
		} else if (formula instanceof Implies) {
			//get the left and right sub-formulas
			Formula left = ((Implies) formula).getLeft();
			Formula right = ((Implies) formula).getRight();

			insetToLabellingFormulaForEachStateMap(state, " does not satisfy : " + formula.toString());
			
			//find a witness for the left sub-formula with the current state
			list.add(state);
			msg.append("\nThe state " + state + " satisfies the left subformula");
			msg.append("\nA witness to the state " + state + " for the left subformula (" + left.toString() + ") is: ");;
			findWitness(left, state, list, msg);
			
			//find a counter example for the right sub-formula with the current state
			msg.append("\nThe state " + state + " does not satisfy the right subformula");
			msg.append("\nA counter example to the state " + state + " for the right subformula (" + right.toString() + ") is: ");
			CounterExampleHelper(right, state, list, msg);
				
			
		} else if (formula instanceof Iff) {
			//get the left and right sub-formulas with there corresponding unSat and sat state sets
			Formula left = ((Iff) formula).getLeft();
			Formula right = ((Iff) formula).getRight();
			
			Set<Integer> subLeftFormulaUnsat = unSatAndSatForEachFormula.get(left).getUnSat();
			Set<Integer> subRightFormulaUnsat = unSatAndSatForEachFormula.get(right).getUnSat();
			Set<Integer> subLeftFormulaSat = unSatAndSatForEachFormula.get(left).getSat();
			Set<Integer> subRightFormulaSat = unSatAndSatForEachFormula.get(right).getSat();
			
			insetToLabellingFormulaForEachStateMap(state, " does not satisfy : " + formula.toString());
			
			if(subLeftFormulaSat.contains(state) && subRightFormulaUnsat.contains(state))
			{
				//if the current state does satisfy the left sub-formulas but does not satisfy the right sub-formula
				//then find a witness for the left sub-formula with the current state
				list.add(state);
				msg.append("\nThe state " + state + " satisfies the left subformula");
				msg.append("\nA witness to the state " + state + " for the left subformula (" + left.toString() + ") is: ");;
				findWitness(left, state, list, msg);
				
				//and find a counter example for the right sub-formula with the current state
				msg.append("\nThe state " + state + " does not satisfy the right subformula");
				msg.append("\nA counter example to the state " + state + " for the right subformula (" + right.toString() + ") is: ");
				CounterExampleHelper(right, state, list, msg);
			}
			if(subLeftFormulaUnsat.contains(state) && subRightFormulaSat.contains(state))
			{
				//if the current state does not satisfy the left sub-formulas but does satisfy the right sub-formula
				//then find a counter example for the left sub-formula with the current state
				list.add(state);
				msg.append("\nThe state " + state + " does not satisfy the left subformula");
				msg.append("\nA counter example to the state " + state + " for the left subformula (" + left.toString() + ") is: ");
				CounterExampleHelper(left, state, list, msg);
				
				//and find a witness for the right sub-formula with the current state
				msg.append("\nThe state " + state + " satisfies the right subformula");
				msg.append("\nA witness to the state " + state + " for the right subformula (" + right.toString() + ") is: ");;
				findWitness(right, state, list, msg);
			}
					
		} else if (formula instanceof ExistsAlways) {
			//get the sub-formula with the corresponding unSat state set
			Formula subFormula = ((ExistsAlways) formula).getFormula();
			Set<Integer> subformulaUnsat = unSatAndSatForEachFormula.get(subFormula).getUnSat();
			
			insetToLabellingFormulaForEachStateMap(state, " does not satisfy : " + formula.toString());
			
			//if the current state does not satisfy then the current state is the counter example
			if(subformulaUnsat.contains(state))
			{
				msg.append("\nThe state " + state + " is a counter example");
				msg.append("\nA counter example to the state " + state + " for the subformula (" + subFormula.toString() + ") is: ");
				CounterExampleHelper(subFormula, state, list, msg);
			}
			else
			{
				//otherwise for each reachable state of the current state find a counter example for sub-formula
				Set<Integer> path = new HashSet<>();
				//get all reachable states that are in the subformulaUnsat set and store it in path
				getAllReachablePaths(state,subformulaUnsat, path);
				
				if(path.isEmpty())
				{
					msg.append("\nThe state " + state + " has no outgoing edges");
				}else
				{				
					msg.append("\nAll the reachable states from state " + state + ": " + path.toString());
					printSatAndUnSatSets(state,subFormula, path, msg);
				}	
				
				//find a counter example for each reachable state which does not satisfy the sub-formula
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
			//get the sub-formula with the corresponding unSat state set
			Formula subFormula = ((ForAllAlways) formula).getFormula();
			Set<Integer> subformulaUnsat = unSatAndSatForEachFormula.get(subFormula).getUnSat();
			
			insetToLabellingFormulaForEachStateMap(state, " does not satisfy : " + formula.toString());
			
			//if the current state does not satisfy then the current state is the counter example
			if(subformulaUnsat.contains(state))
			{
				msg.append("\nThe state " + state + " is a counter example");
				msg.append("\nA counter example to the state " + state + " for the subformula (" + subFormula.toString() + ") is: ");
				CounterExampleHelper(subFormula, state, list, msg);
			}
			else
			{
				//otherwise find any state that does not satisfy the sub-formula
				//print all reachable states from current state for the user
				Set<Integer> allReachableStates = this.getRechableStates(state);
				if(allReachableStates.isEmpty())
				{
					msg.append("\nThe state " + state + " has no outgoing edges");
				}else
				{				
					msg.append("\nAll the reachable states from state " + state + ": " + allReachableStates.toString());
					printSatAndUnSatSets(state,subFormula, allReachableStates, msg);						
				}
				
				//find the unSatState that is reachable form the current state 
				Map<Integer,Integer> parent = new HashMap<>();							//to store the parent nodes
				Integer unSatState = this.getUnSatOrSatState(state, parent,subformulaUnsat);
				
				//find the parent nodes of the unSatState and add to the list and print the path
				List<Integer> parentList = new LinkedList<>();				
				getParentNodes(parentList,parent,unSatState);
				list.addAll(parentList);				
				parentList.add(state);
				msg.append("\nThe state " + unSatState + " is one of the states that does not satisfy the subformula " + subFormula);
				msg.append("\nThe path from state " + unSatState + " to state " + state + ": ");
				printPath(parentList, msg);
				
				//find a counter example for the unSatState with the sub-formula
				msg.append("\nA counter example to the state " + unSatState + " for the subformula (" + subFormula.toString() + ") is: ");
				CounterExampleHelper(subFormula, unSatState, list, msg);
			}
		}
		else if (formula instanceof ExistsEventually) {
			//get the sub-formula with the corresponding unSat state set
			Formula subFormula = ((ExistsEventually)formula).getFormula();
			Set<Integer> subformulaUnsat = unSatAndSatForEachFormula.get(subFormula).getUnSat();
			
			insetToLabellingFormulaForEachStateMap(state, " does not satisfy : " + formula.toString());
			
			//show all the states on all reachable paths from the current state 
			//entire sub-system that no state satisfies the sub-formula
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
			
			//find a counter example for all the reachable states that does not satisfy the sub-formula
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
			//get the sub-formula with the corresponding unSat state set
			Formula subFormula = ((ForAllEventually) formula).getFormula();
			Set<Integer> subformulaUnsat = unSatAndSatForEachFormula.get(subFormula).getUnSat();
			
			insetToLabellingFormulaForEachStateMap(state, " does not satisfy : " + formula.toString());
			
			//find one reachable path from the current state where no state on that path satisfies sub-formula
			Set<Integer> path = new HashSet<>();
			getOneReachablePath(state,subformulaUnsat, path);
			path.add(state);
			
			if(path.size() == 1)
			{
				msg.append("\nThe state " + state + " has no outgoing edges");
			}else
			{				
				msg.append("\nThe states on the path from state " + state + ": " + path.toString() );
				printSatAndUnSatSets(state,subFormula, path, msg);
			}
			
			//find a counter example for all the states on the path that does not satisfy the sub-formula
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
			//get the sub-formula with the corresponding unSat state set
			Formula f = ((ExistsNext) formula).getFormula();
		    Set<Integer> formulaUnsat = unSatAndSatForEachFormula.get(f).getUnSat();
		    
			insetToLabellingFormulaForEachStateMap(state, " does not satisfy : " + formula.toString());

			// for each successor of the current state find a counter example for sub-formula
			Set<Integer> postStates = Post(state);
			
			if(postStates.isEmpty())
			{
				msg.append("\nThe state " + state + " has no outgoing edges\n");
			}
			else
			{
				 msg.append("\nThe post states of " + state + ": " + postStates.toString() );
				 printSatAndUnSatSets(state,f, postStates, msg);
			}
			
			//find a counter example for each successor that does not satisfy the sub-formula
			for (Iterator<Integer> it = postStates.iterator(); it.hasNext(); ) 
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
			//get the sub-formula with the corresponding unSat state set
			Formula f = ((ForAllNext) formula).getFormula();			
			Set<Integer> formulaUnsat = unSatAndSatForEachFormula.get(f).getUnSat();	
			 
			insetToLabellingFormulaForEachStateMap(state, " does not satisfy : " + formula.toString());	  
			
			//a path contains a state s and a state s' in post(s) such that s' does not satisfy sub-formula
			Set<Integer> postStates = Post(state);		   		
			
		    msg.append("\nThe post states of " + state + ": " + postStates.toString() );
			printSatAndUnSatSets(state,f, postStates, msg);
			
			//find a counter example for one successor that does not satisfy the sub-formula
			for (Iterator<Integer> it = postStates.iterator(); it.hasNext(); ) 
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
			// a AU b
			//get the sub-formula with the corresponding unSat state set
			Formula left = ((ForAllUntil)formula).getLeft();
			Formula right = ((ForAllUntil)formula).getRight();			
			Set<Integer> subLeftFormulaUnsat = unSatAndSatForEachFormula.get(left).getUnSat();
			
			insetToLabellingFormulaForEachStateMap(state, " does not satisfy : " + formula.toString());

			if(subLeftFormulaUnsat.contains(state))
			{
				//does not satisfy a   
				//find a counter example for a with the sub-formula
				list.add(state);
				msg.append("\nThe state " + state + " does not satisfy the left subformula");
				msg.append("\nA counter example to the state " + state + " for the left subformula (" + left.toString() + ") is: ");;
				CounterExampleHelper(left, state, list, msg);
			}else
			{
				//find a path where the contiguous states satisfy a and the last state does not b
				
				Set<Integer> path = new HashSet<>();
				Set<Integer> sat_a = unSatAndSatForEachFormula.get(left).getSat();
				Set<Integer> sat_b = unSatAndSatForEachFormula.get(right).getSat();
				Integer unSatBState = getOnePathWithStatesInANotB(state,sat_a,sat_b,path);
				path.add(state);
			
				if(path.size() == 1)
				{
					msg.append("\nThe state " + state + " has no outgoing edges");
				}else
				{				
					msg.append("\nThe states on the path from state " + state + ": " + path.toString() );
					printSatAndUnSatSets(state, right, path, msg);
				}
			
				//find a counter example for the last state on the path that does not satisfy right sub-formula (b)
				list.addAll(path);
				msg.append("\nA counter example to the state " + unSatBState + " for the subformula (" + right.toString() + ") is: ");
				CounterExampleHelper(right, unSatBState, list, msg);
					
						
			}
			
		}
		else if (formula instanceof ExistsUntil) {
			// a AU b
			//get the sub-formula with the corresponding unSat state set
			Formula left = ((ExistsUntil)formula).getLeft();
			Formula right = ((ExistsUntil)formula).getRight();			
			Set<Integer> subLeftFormulaUnsat = unSatAndSatForEachFormula.get(left).getUnSat();
			
			insetToLabellingFormulaForEachStateMap(state, " does not satisfy : " + formula.toString());
			
			if(subLeftFormulaUnsat.contains(state))
			{
				//does not satisfy a 
				//find a counter example for a with the sub-formula
				list.add(state);
				msg.append("\nThe state " + state + " does not satisfy the left subformula");
				msg.append("\nA counter example to the state " + state + " for the left subformula (" + left.toString() + ") is: ");;
				CounterExampleHelper(left, state, list, msg);
			}else
			{
				//find all paths where the contiguous states satisfy a and the last state does not b
				Set<Integer> path = new HashSet<>();
				Set<Integer> sat_a = unSatAndSatForEachFormula.get(left).getSat();
				Set<Integer> sat_b = unSatAndSatForEachFormula.get(right).getSat();
				getAllPathsWithStatesInANotB(state,sat_a,sat_b,path);
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
				
				//find a counter example for the last state on the all paths that does not satisfy right sub-formula (b)
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
			//get the sub-formula 
			Formula f = ((Not)formula).getFormula();
			
			insetToLabellingFormulaForEachStateMap(state, " does not satisfy : " + formula.toString());

			//find a witness for the current state with the sub-formula
			list.add(state);
			msg.append("\nA witness to the state " + state + " for the subformula (" + f.toString() + ") is: ");			
			findWitness(f, state, list, msg);
			return;
		} 
	}
	
	/**
	 * This is a recursive method to find a witness for each sub-formulas
	 * This method also calls counter example helper method is case the formula contains Not operator
	 * 
	 * @param formula - main formula or sub formula
	 * @param state   - initial state or any other state
	 * @param list    - a list to collect the states that are in the counter example
	 * @param msg     - a string buffer to collect the messages
	 */
	private void findWitness(Formula formula, Integer state, Set<Integer> list, StringBuilder msg)
	{
    
		
   		/*
		 * Base Case
		 */
		if (formula instanceof True) {
			//the witness is the entire system
			Set<Integer> formulaSat = unSatAndSatForEachFormula.get(formula).getSat();
			insetToLabellingFormulaForEachStateMap(state, " satisfies : " + formula.toString());
			msg.append("\nThe witness for the formula (True) is the whole system");
			list.addAll(formulaSat);
			return;
		}
		/*
		 * Base Case
		 */
		else if (formula instanceof False) {	
			//no witness exists
			insetToLabellingFormulaForEachStateMap(state, " satisfies : " + formula.toString());
			msg.append("\nNo witness for the formula (False) ");
			return;
		}/*
		 * Base Case
		 */
		else if (formula instanceof AtomicProposition) {
			//if the current state satisfies the atomic proposition then it is a witness
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
			//get the left and right sub-formulas with there corresponding sat state sets
			Formula left = ((And) formula).getLeft();
			Formula right = ((And) formula).getRight();			
			Set<Integer> subLeftFormulaSat = unSatAndSatForEachFormula.get(left).getSat();
			Set<Integer> subRightFormulaSat = unSatAndSatForEachFormula.get(right).getSat();
			
			insetToLabellingFormulaForEachStateMap(state, " satisfies : " + formula.toString());
			
			if(subLeftFormulaSat.contains(state) && subRightFormulaSat.contains(state))
			{
				//if the current state does satisfy the left and right sub-formulas
				//then find a witness for the left and right sub-formulas with the current state
				list.add(state);
				msg.append("\nThe state " + state + " satisfies the left and right subformulas");
				msg.append("\nA witness to the state " + state + " for the left subformula (" + left.toString() + ") is: ");
				findWitness(left, state, list, msg);
				msg.append("\nA witness to the state " + state + " for the right subformula (" + right.toString() + ") is: ");
				findWitness(right, state, list, msg);
			}
			
		} else if (formula instanceof Or) {
			//get the left and right sub-formulas with there corresponding sat state sets
			Formula left = ((Or) formula).getLeft();
			Formula right = ((Or) formula).getRight();        
			Set<Integer> subLeftFormulaSat = unSatAndSatForEachFormula.get(left).getSat();
			Set<Integer> subRightFormulaSat = unSatAndSatForEachFormula.get(right).getSat();
			
			insetToLabellingFormulaForEachStateMap(state, " satisfies : " + formula.toString());
			
			if(subLeftFormulaSat.contains(state))
			{
				//if the current state does satisfy the left sub-formula
				//then find a witness for the left sub-formula with the current state
				list.add(state);
				msg.append("\nThe state " + state + " does satisfy the left subformula");
				msg.append("\nA witness to the state " + state + " for the left subformula (" + left.toString() + ") is: ");;
				findWitness(left, state, list, msg);
			}else if(subRightFormulaSat.contains(state))
			{
				//if the current state does satisfy the right sub-formula
				//then find a witness for the right sub-formula with the current state
				list.add(state);
				msg.append("\nThe state " + state + " does satisfy the right subformula");
				msg.append("\nA witness to the state " + state + " for the right subformula (" + right.toString() + ") is: ");
				findWitness(right, state, list, msg);
			}	
			
		} else if (formula instanceof Implies) {
			//get the left and right sub-formulas 
			Formula left = ((Implies) formula).getLeft();
			Formula right = ((Implies) formula).getRight();
			
			insetToLabellingFormulaForEachStateMap(state, " satisfies : " + formula.toString());
			
			//find a counter example for the left sub-formula with the current state
			list.add(state);
			msg.append("\nThe state " + state + "does not satisfy the left subformula");
			msg.append("\nA counter example to the state " + state + " for the left subformula (" + left.toString() + ") is: ");;
			CounterExampleHelper(left, state, list, msg);
			
			//find a witness for the right sub-formula with the current state
			msg.append("\nThe state " + state + " satisfies the right subformula");
			msg.append("\nA witness to the state " + state + " for the right subformula (" + right.toString() + ") is: ");;
			findWitness(right, state, list, msg);		
			
		} else if (formula instanceof Iff) {
			//get the left and right sub-formulas with there corresponding sat state sets
			Formula left = ((Iff) formula).getLeft();
			Formula right = ((Iff) formula).getRight();			
			Set<Integer> subLeftFormulaSat = unSatAndSatForEachFormula.get(left).getSat();
			Set<Integer> subRightFormulaSat = unSatAndSatForEachFormula.get(right).getSat();
			
			insetToLabellingFormulaForEachStateMap(state, " satisfies : " + formula.toString());
			
			if(subLeftFormulaSat.contains(state) && subRightFormulaSat.contains(state))
			{
				//if the current state does satisfy the left and right sub-formulas
				//then find a witness for the left and right sub-formulas with the current state
				list.add(state);
				msg.append("\nThe state " + state + " satisfies left and right subformulas");
				msg.append("\nA witness to the state " + state + " for the left subformula (" + left.toString() + ") is: ");
				findWitness(left, state, list, msg);
				msg.append("\nA witness to the state " + state + " for the right subformula (" + right.toString() + ") is: ");
				findWitness(right, state, list, msg);
			}	
			
		} else if (formula instanceof ForAllAlways) {			
			//get the sub-formula with the corresponding sat state set
			Formula subFormula = ((ForAllAlways) formula).getFormula();
			Set<Integer> subformulaSat = unSatAndSatForEachFormula.get(subFormula).getSat();
			
			insetToLabellingFormulaForEachStateMap(state, " satisfies : " + formula.toString());
			
			//show all the states in all reachable paths from the current states satisfies the sub-formulas
			Set<Integer> allReachableStates = this.getRechableStates(state);
			if(allReachableStates.isEmpty())
			{
				msg.append("\nThe state " + state + " has no outgoing edges");
			}else
			{				
				msg.append("\nAll the reachable states from state " + state + ": " + allReachableStates.toString());
				printSatAndUnSatSets(state,subFormula, allReachableStates, msg);						
			}
			
			//find a witness for all states on the all reachable paths from the current state with sub-formula
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
			//get the sub-formula with the corresponding sat state set
			Formula subFormula = ((ExistsAlways) formula).getFormula();
			Set<Integer> subformulaSat = unSatAndSatForEachFormula.get(subFormula).getSat();
			
			insetToLabellingFormulaForEachStateMap(state, " satisfies : " + formula.toString());
			
			//find a reachable path that all the states on that path does satisfy the sub-fomrula
			Set<Integer> path = new HashSet<>();
			getOneReachablePath(state,subformulaSat, path);
			path.add(state);	
			
			if(path.isEmpty())
			{
				msg.append("\nThe state " + state + " has no outgoing edges");
					
			}else
			{				
				msg.append("\nThe states on the path from state " + state + ": " + path.toString() );
				printSatAndUnSatSets(state,subFormula, path, msg);
			}	
				
			//find a witness for all states on the path from the current state with sub-formula
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
			//get the sub-formula with the corresponding sat state set
			Formula subFormula = ((ForAllEventually)formula).getFormula();
			Set<Integer> subformulaSat = unSatAndSatForEachFormula.get(subFormula).getSat();
			
			insetToLabellingFormulaForEachStateMap(state, " satisfies : " + formula.toString());
			
			
			if(subformulaSat.contains(state))
			{
				//if the current state satisfy the sub-formula 
				//find a witness for the current state with the sub-formula
				msg.append("\nThe state " + state + " is a witness");
				msg.append("\nA witness to the state " + state + " for the subformula (" + subFormula.toString() + ") is: ");
				findWitness(subFormula, state, list, msg);
			}
			else
			{
				//show all the states on all reachable paths from the current state 
				//that has a state satisfies the sub-formula
				Set<Integer> path = new HashSet<>();
				getAllReachablePaths(state,subformulaSat, path);
				
				if(path.isEmpty())
				{
					msg.append("\nThe state " + state + " has no outgoing edges");
				}else
				{				
					msg.append("\nAll the reachable states from state " + state + ": " + path.toString());
					printSatAndUnSatSets(state,subFormula, path, msg);
				}	
				
				//find a witness for all states on the all reachable path from the current state with sub-formula 
				//that satisfies the sub-formula
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
			//get the sub-formula with the corresponding sat state set
			Formula subFormula = ((ExistsEventually) formula).getFormula();
			Set<Integer> subformulaSat = unSatAndSatForEachFormula.get(subFormula).getSat();
			
			insetToLabellingFormulaForEachStateMap(state, " satisfies : " + formula.toString());
			
			Set<Integer> path = new HashSet<>();		
			
			if(subformulaSat.contains(state))
			{
				//if the current state satisfy the sub-formula
				//then find a witness for current state with the sub-formula
				msg.append("\nThe state " + state + " is a witness");
				msg.append("\nA witness to the state " + state + " for the subformula (" + subFormula.toString() + ") is: ");
				findWitness(subFormula, state, list, msg);
			}else
			{
				//print all the reachable states from the current state
				Set<Integer> allReachableStates = this.getRechableStates(state);
				if(allReachableStates.isEmpty())
				{
					msg.append("\nThe state " + state + " has no outgoing edges");
				}else
				{				
					msg.append("\nAll the reachable states from state " + state + ": " + allReachableStates.toString());
					printSatAndUnSatSets(state,subFormula, allReachableStates, msg);						
				}
				
				//find a reachable state from the current state that satisfy the sub-formula
				Map<Integer,Integer> parent = new HashMap<>();
				Integer satState = this.getUnSatOrSatState(state, parent,subformulaSat);
				//find the parent nodes of the satState and print the path
				List<Integer> parentList = new LinkedList<>();
				getParentNodes(parentList,parent,satState);
				list.addAll(parentList);
				parentList.add(state);
				msg.append("\nThe state " + satState + " is one of the states that satisfies the subformula " + subFormula);
				msg.append("\nThe path from state " + satState + " to state " + state + ": ");
				printPath(parentList, msg);
				
				//find a witness for the satState with sub-formula
				msg.append("\nA witness to the state " + satState + " for the subformula (" + subFormula.toString() + ") is: ");
				findWitness(subFormula, satState, list, msg);			
			}				
		}
		else if (formula instanceof ForAllNext) 
		{
			//get the sub-formula with the corresponding sat state set
			Formula f = ((ForAllNext) formula).getFormula();
			Set<Integer> formulaSat = unSatAndSatForEachFormula.get(f).getSat();
			
			insetToLabellingFormulaForEachStateMap(state, " satisfies : " + formula.toString());
			
			//show all the successor of the current state satisfy the sub-formula
			Set<Integer> postStates = Post(state);	
		
			if(postStates.isEmpty())
			{
				msg.append("\nThe state " + state + " has no outgoing edges\n");
			}
			else
			{
				 msg.append("\nThe post states of " + state + ": " + postStates.toString() );
				 printSatAndUnSatSets(state,f, postStates, msg);
			}
			
			//find a witness for all the successor of the current state
			for (Iterator<Integer> it = postStates.iterator(); it.hasNext(); ) 
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
			//get the sub-formula with the corresponding sat state set
			Formula f = ((ExistsNext) formula).getFormula();	
			Set<Integer> formulaSat = unSatAndSatForEachFormula.get(f).getSat();	
			
			insetToLabellingFormulaForEachStateMap(state, " satisfies : " + formula.toString());
			
			//show one of the successor of the current state satisfy the sub-formula
			Set<Integer> postStates = Post(state);	    		
			
		    msg.append("\nThe post states of " + state + ": " + postStates.toString() );
			printSatAndUnSatSets(state,f, postStates, msg);
			
			//find a witness for one of the successor of the current state
			for (Iterator<Integer> it = postStates.iterator(); it.hasNext(); ) 
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
			// a EU b
			//get the left and right sub-formulas with there corresponding sat state sets
			Formula left = ((ExistsUntil)formula).getLeft();
			Formula right = ((ExistsUntil)formula).getRight();			
			Set<Integer> subLeftFormulaSat = unSatAndSatForEachFormula.get(left).getSat();
			
			insetToLabellingFormulaForEachStateMap(state, " satisfies : " + formula.toString());
			
			if(subLeftFormulaSat.contains(state))
			{
				//does satisfy a  
				//then find a witness for current state with the left sub-formula
				list.add(state);
				msg.append("\nThe state " + state + " satisfies the left subformula");
				msg.append("\nA witness to the state " + state + " for the left subformula (" + left.toString() + ") is: ");;
				findWitness(left, state, list, msg);
			}else
			{
				//find a path where the contiguous states satisfy a and the last state does satisfy b
				
				Set<Integer> path = new HashSet<>();
				Set<Integer> sat_a = unSatAndSatForEachFormula.get(left).getSat();
				Set<Integer> sat_b = unSatAndSatForEachFormula.get(right).getSat();
				Integer satBState = getOnePathWithStatesInANotB(state,sat_a,sat_b,path);
				path.add(state);
			
				if(path.size() == 1)
				{
					msg.append("\nThe state " + state + " has no outgoing edges");
				}else
				{				
					msg.append("\nThe states on the path from state " + state + ": " + path.toString() );
					printSatAndUnSatSets(state, right, path, msg);
				}
			
				//find a witness for the last state on the path with the right sub-formula
				list.addAll(path);
				msg.append("\nA witness to the state " + satBState + " for the subformula (" + right.toString() + ") is: ");
				CounterExampleHelper(right, satBState, list, msg);						
			}
			
		}
		else if (formula instanceof ForAllUntil) {
			// a AU b
			//get the left and right sub-formulas with there corresponding sat state sets
			Formula left = ((ForAllUntil)formula).getLeft();
			Formula right = ((ForAllUntil)formula).getRight();			
			Set<Integer> subLeftFormulaSat = unSatAndSatForEachFormula.get(left).getSat();
			
			insetToLabellingFormulaForEachStateMap(state, " satisfies : " + formula.toString());
			
			if(subLeftFormulaSat.contains(state))
			{
				//does satisfy a   
				//then find a witness for current state with the left sub-formula
				list.add(state);
				msg.append("\nThe state " + state + " satisfies the left subformula");
				msg.append("\nA witness to the state " + state + " for the left subformula (" + left.toString() + ") is: ");;
				CounterExampleHelper(left, state, list, msg);
			}else
			{
				//find all paths where the contiguous states satisfy a and the last state does satisfy b
				
				Set<Integer> path = new HashSet<>();
				Set<Integer> sat_a = unSatAndSatForEachFormula.get(left).getSat();
				Set<Integer> sat_b = unSatAndSatForEachFormula.get(right).getSat();
				getAllPathsWithStatesInANotB(state,sat_a,sat_b,path);
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
				
				//find a witness for the last state on all the paths with the right sub-formula
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
			//get the sub-formula 
			Formula f = ((Not)formula).getFormula();
			
			insetToLabellingFormulaForEachStateMap(state, " satisfies : " + formula.toString());

			//find a counter example for the current state with the sub-formula
			list.add(state);
			msg.append("\nA counter example to the state " + state + " for the subformula (" + f.toString() + ") is: ");
			
			CounterExampleHelper(f, state, list, msg);
			return;
		} 
	}

	/**
	 * This method recursively inserts the parent nodes of the state s into the list
	 * 
	 * @param list	  - a list to collect the parent nodes of the state s
	 * @param parent  - a map the get the parent node name of each state
	 * @param s       - given state
	 */
	private void getParentNodes(List<Integer> list, Map<Integer,Integer> parent, Integer s)
	{
		Integer parentNode = parent.get(s);
		//Base case
		if(parentNode == null)
		{
			return;
		}
		//add the parent state name to the list
		list.add(s);
		getParentNodes(list,parent,parentNode);
	}
		
	/**
	 * This method recursively adds one reachable path from the state where all the states on that path are in unsatOrSatFormulaSet
	 * 
	 * @param state     			- state name	
	 * @param unsatOrSatFormulaSet	- unSat(Sat) formula set
	 * @param path 					- a set containing all the states on the path
	 */
	private void getOneReachablePath(Integer state, Set<Integer> unsatOrSatFormulaSet,Set<Integer> path)
	{
		//Base case
		if(Post(state).isEmpty()) {
			return;
		}
		else {
			int i = 0;
			
			for (Iterator<Integer> it = Post(state).iterator(); it.hasNext(); ) 
			{
				Integer n = it.next();
				if(unsatOrSatFormulaSet.contains(n) && !path.contains(n)) {
					path.add(n);
					getOneReachablePath(n,unsatOrSatFormulaSet,path);
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
	
	/**
	 * 
	 * This method recursively finds all reachable states which are in the unsatOrSatFormulaSet
	 * It also adds all the state to the set path 
	 * 
	 * @param state					- state name
	 * @param unsatOrSatFormulaSet  - unSat(Sat) formula set
	 * @param path					- a set containing all the states of the path
	 */
	private void getAllReachablePaths(Integer state, Set<Integer> unsatOrSatFormulaSet,Set<Integer> path)
	{
		//Base case
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
					if(!unsatOrSatFormulaSet.contains(n)) 
					{
						getAllReachablePaths(n,unsatOrSatFormulaSet,path);
					}
				}
			}			
		}		
	}
	
	/**
	 * This method recursively finds a path where the contiguous states are in the satA and the last state is not in satB and satA
	 *
	 * @param state		- state name
	 * @param satA		- sat formula A
	 * @param satB		- sat formula B
	 * @param path		- a set containing all the state on the path
	 * @return	the last state that is not in satB and satA
	 */
	private Integer getOnePathWithStatesInANotB(Integer state, Set<Integer> satA ,Set<Integer> satB ,Set<Integer> path)
	{
		//Base case
		if(Post(state).isEmpty()) {
			return state;
		}
		else {
			int i = 0;
			
			for (Iterator<Integer> it = Post(state).iterator(); it.hasNext(); ) 
			{
				
				Integer n = it.next();
				if(satA.contains(n) && !path.contains(n)) {
					path.add(n);
					getOnePathWithStatesInANotB(n,satA,satB,path);					
				}
				else if(satB.contains(n)) {
					i++;
					
				}else if(!satA.contains(n)&&!satB.contains(n)) {
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
	
	
	/**
	 * This method recursively finds all path where the contiguous states are in the satA and the last state is not in satB and satA
	 * 
	 * @param state		- state name
	 * @param satA		- sat formula A
	 * @param satB		- sat formula B
	 * @param path		- a set containing all the state on the all paths
	 */
	private void getAllPathsWithStatesInANotB(Integer state, Set<Integer> satA ,Set<Integer> satB ,Set<Integer> path)
	{
		//Base case
		if(Post(state).isEmpty()) {
			return;
		}
		else {
			int i = 0;
			
			for (Iterator<Integer> it = Post(state).iterator(); it.hasNext(); ) 
			{
				
				Integer n = it.next();
				if(satA.contains(n) && !path.contains(n)) {
					path.add(n);
					getAllPathsWithStatesInANotB(n,satA,satB,path);					
				}
				else if(satB.contains(n)) {
					i++;
					
				}else if(!satA.contains(n)&&!satB.contains(n)) {
					path.add(n);				
				}
				
			}
			if( i == Post(state).size())
			{
				path.remove(state);
				
			}	
				
		}
	}
	
	
	/**
	 * 	This method returns the only transitions from the initial partial transition system 
	 *  where for each transition the source and the target states does exist in the counter example state list
	 *  
	 * @param counterExStates - a set containing counter example states
	 * @return a transition set 
	 */
	private Set<Transition> getRelatedTransitions( Set<Integer> counterExStates)
	{
		Set<Transition> result = new HashSet<Transition>();
		for (Iterator<Transition> it = this.pts.getTransitions().iterator(); it.hasNext(); ) 
		{
			Transition t = it.next();
			if(counterExStates.contains(t.getSource()) && counterExStates.contains(t.getTarget()))
			{
				result.add(t);
			}
			
		}
		
		return result;
	}
	
	/**
	 * 	This method returns the labellings from the initial partial transition system 
	 *  for the states does exist in the counter example state list
	 * 
	 * @param counterExStates - a set containing counter example states
	 * @return a labelling map
	 */
	private Map<Integer, Set<Integer>> getRelatedLabellings( Set<Integer> counterExStates)
	{
		Map<Integer, Set<Integer>> result = new HashMap<>();
		for (Iterator<Integer> it = this.pts.getLabelling().keySet().iterator(); it.hasNext(); ) 
		{
			Integer s = it.next();
			if(counterExStates.contains(s))
			{
				result.put(s, this.pts.getLabelling().get(s));
			}
			
		}
		
		return result;
	}
	
	
	/**
	 * 	This method returns the post states of the given state s
	 * 
	 * @param s - state name
	 * @return a set of post states
	 */
	public Set<Integer> getPostStates(Integer s)
	{
		return this.Post(s);
	}
	
	/**
	 * This method finds the fist unSat(sat) state that is reachable from the given state s
	 * This method is based on BFS graph search algorithm
	 * 
	 * @param s						- state name
	 * @param parent				- a map to store the parent names of each state
	 * @param unSatOrSatFormulaSet	- unSat(Sat) formula set
	 * @return	first unSat(Sat) state that is in the unSatOrSatFormulaSet 
	 */
	private Integer getUnSatOrSatState(Integer s, Map<Integer,Integer> parent, Set<Integer> unSatOrSatFormulaSet)
    {
        // Mark all the vertices as not visited        
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
            
            if(unSatOrSatFormulaSet.contains(s))
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

	/**
	 * This method returns all the reachable states from the given state s
	 * This method is based on BFS graph search algorithm
	 * 
	 * @param s	- state name
	 * @return  a set containing all the reachable state form s
	 */
	private Set<Integer> getRechableStates(Integer s)
    {
        // Mark all the vertices as not visited        
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
	
	/**
	 *  This method insets to LabellingFormulaForEachStateMap
	 *  
	 * @param state		- state name
	 * @param formula	- formula 
	 */
	private void insetToLabellingFormulaForEachStateMap (Integer state, String formula)
	{
		if(labellingFormulaForEachState.containsKey(state))
		{
			String oldFormula = labellingFormulaForEachState.get(state);
			labellingFormulaForEachState.put(state, oldFormula + " ,and" + formula.toString());
		}else
		{
			labellingFormulaForEachState.put(state, formula.toString());
		}
	}
	
	/**
	 * 	This method appends the unSat and sat sets to the string builder msg
	 * 
	 * @param state					- state name
	 * @param formula				- formula or sub-formula
	 * @param allReachableStates	- all reachable states from the state
	 * @param msg					- string builder
	 */
	private void printSatAndUnSatSets(Integer state, Formula formula, Set<Integer> allReachableStates, StringBuilder msg)
	{
		Set<Integer> sat = new HashSet<>();
		Set<Integer> unSat = new HashSet<>();
		Set<Integer> subformulaUnsat = unSatAndSatForEachFormula.get(formula).getUnSat();
		
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
	
		msg.append("\nThe states that satisfy the formula (" + formula + ") : ");
		msg.append(sat.toString());
		msg.append("\nThe states that do not satisfy the formula (" + formula + ") : ");
		msg.append(unSat.toString());	
	}
	
	/**
	 * This method appends the given path to the string builder msg
	 * 
	 * @param path - any path
	 * @param msg  - string builder
	 */
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