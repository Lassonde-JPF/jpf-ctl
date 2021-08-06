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
import ctl.And;
import ctl.False;
import ctl.Formula;
import ctl.Or;
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
	private final Map<Formula,Set<Integer>> unSatForEachFormula; 
	private final Map<Integer,Formula> labellingFormulaForEachState;

	// Subset tables
	private final Map<Formula, StateSets> subset;

	// Target Transition System
	private final LabelledPartialTransitionSystem pts;

	private List<String> formulaStack;

	// Constructor
	public Model(LabelledPartialTransitionSystem pts) {
		this.post = new HashMap<Integer, Set<Integer>>();
		this.pre = new HashMap<Integer, Set<Integer>>();
		this.unSatForEachFormula = new HashMap<>();
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
			unSatForEachFormula.put(formula,new HashSet<Integer>());
			return buildResult(formula, Sat, unSat);
		}
		/*
		 * Base Case
		 */
		else if (formula instanceof False) {
			Set<Integer> Sat = new HashSet<Integer>();
			Set<Integer> unSat = pts.getStates();
			unSatForEachFormula.put(formula,pts.getStates());
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
			unSatForEachFormula.put(formula,pts.getStates());
			return buildResult(formula, Sat, unSat);
			
		} else if (formula instanceof And) {
			And f = (And) formula;
			StateSets L = check(f.getLeft());
			StateSets R = check(f.getRight());
			Set<Integer> Sat = new HashSet<Integer>(L.getSat());
			Sat.retainAll(R.getSat());
			Set<Integer> unSat = new HashSet<Integer>(pts.getStates());
			unSat.removeAll(Sat);
			unSatForEachFormula.put(f,unSat);
			return buildResult(formula, Sat, unSat);
		} else if (formula instanceof Or) {
			Or f = (Or) formula;
			StateSets L = check(f.getLeft());
			StateSets R = check(f.getRight());
			Set<Integer> Sat = new HashSet<Integer>(L.getSat());
			Sat.addAll(R.getSat());
			Set<Integer> unSat = new HashSet<Integer>(pts.getStates());
			unSat.removeAll(Sat);
			unSatForEachFormula.put(f,unSat);
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
			unSatForEachFormula.put(f,unSat);
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
			unSatForEachFormula.put(f,unSat);
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
			unSatForEachFormula.put(f,unSat);
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
			unSatForEachFormula.put(eE,unSat);
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
			unSatForEachFormula.put(eN,unSat);
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
			unSatForEachFormula.put(eU,unSat);
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
			unSatForEachFormula.put(fA,T);
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
			unSatForEachFormula.put(fAF,T);
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
			unSatForEachFormula.put(fN,unSat);
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
			unSatForEachFormula.put(fAU,unSat);
			return buildResult(formula, EU, unSat);
		} else if (formula instanceof Not) {
			Not n = (Not) formula;
			StateSets S = check(n.getFormula());
			unSatForEachFormula.put(n,S.getSat());
			return buildResult(formula, S.getUnSat(), S.getSat());
		}
		System.err.println("This formula type is unknown");
		return null;
	}
	
	public LabelledPartialTransitionSystem getCounterExample(Formula f, Integer s )
	{
		Set<Integer> counterExStates = new HashSet<>();
		counterExStates.add(s);
        Set<Integer> states = new HashSet<>();
		states.add(s);
		CounterExampleHelper(f, s, counterExStates);
        
		Set<Transition> newTSTransitions = this.getRelatedTransitions(counterExStates);
		
		Map<Integer, Set<Integer>> newTSLabelling = this.getRelatedLabellings(counterExStates);
		
		
		LabelledPartialTransitionSystem newTS = new LabelledPartialTransitionSystem(counterExStates,newTSTransitions, newTSLabelling);
		
		 
		return newTS;
	}

	private void CounterExampleHelper(Formula formula, Integer state, Set<Integer> list)
	{
    
		
   		/*
		 * Base Case
		 */
		if (formula instanceof True) {
			labellingFormulaForEachState.put(state, formula);
			return;
		}
		/*
		 * Base Case
		 */
		else if (formula instanceof False) {	
			Set<Integer> formulaUnsat = unSatForEachFormula.get(formula);
			labellingFormulaForEachState.put(state, formula);
			list.addAll(formulaUnsat);
			return;
		}/*
		 * Base Case
		 */
		else if (formula instanceof AtomicProposition) {
			Set<Integer> formulaUnsat = unSatForEachFormula.get(formula);
			if(formulaUnsat.contains(state))
			{
				//add to the list and break;
				list.add(state);
				labellingFormulaForEachState.put(state, formula);
				return;
			}
						
		}
		else if (formula instanceof And) {
			Formula left = ((And) formula).getLeft();
			Formula right = ((And) formula).getRight();
			
			Set<Integer> subLeftFormulaUnsat = unSatForEachFormula.get(left);
			Set<Integer> subRightFormulaUnsat = unSatForEachFormula.get(right);
			labellingFormulaForEachState.put(state, formula);
			//for (Iterator<Integer> it = states.iterator(); it.hasNext(); ) 
		    {
		       	//Integer n = it.next();
				if(subLeftFormulaUnsat.contains(state))
				{
					list.add(state);
					
					CounterExampleHelper(left, state, list);
				}else if(subRightFormulaUnsat.contains(state))
				{
					list.add(state);
					
					CounterExampleHelper(right, state, list);
				}
			}	

		} else if (formula instanceof Or) {
			Formula left = ((Or) formula).getLeft();
			Formula right = ((Or) formula).getRight();
           

			Set<Integer> subLeftFormulaUnsat = unSatForEachFormula.get(left);
			Set<Integer> subRightFormulaUnsat = unSatForEachFormula.get(right);
			labellingFormulaForEachState.put(state, formula);
			if(subLeftFormulaUnsat.contains(state) && subRightFormulaUnsat.contains(state))
			{
				list.add(state);
				CounterExampleHelper(left, state, list);
			}
				
			
		} else if (formula instanceof Implies) {
			Formula left = ((Implies) formula).getLeft();
			Formula right = ((Implies) formula).getRight();
			
			Set<Integer> subLeftFormulaUnsat = unSatForEachFormula.get(left);
			Set<Integer> subRightFormulaUnsat = unSatForEachFormula.get(right);
			labellingFormulaForEachState.put(state, formula);
			//for (Iterator<Integer> it = states.iterator(); it.hasNext(); ) 
		    {
		       	//Integer n = it.next();
				if(subLeftFormulaUnsat.contains(state))
				{
					list.add(state);
					CounterExampleHelper(left, state, list);
				}else if(subRightFormulaUnsat.contains(state))
				{
					list.add(state);
					CounterExampleHelper(right, state, list);
				}
			}	
			
		} else if (formula instanceof Iff) {
			Formula left = ((Iff) formula).getLeft();
			Formula right = ((Iff) formula).getRight();
			Set<Integer> subLeftFormulaUnsat = unSatForEachFormula.get(left);
			Set<Integer> subRightFormulaUnsat = unSatForEachFormula.get(right);
			labellingFormulaForEachState.put(state, formula);
			//for (Iterator<Integer> it = states.iterator(); it.hasNext(); ) 
		    {
		       	//Integer n = it.next();
				if(subLeftFormulaUnsat.contains(state))
				{
					list.add(state);
					CounterExampleHelper(left, state, list);
				}else if(subRightFormulaUnsat.contains(state))
				{
					list.add(state);
					CounterExampleHelper(right, state, list);
				}
			}	
			
		} else if (formula instanceof ExistsAlways) {
			
			//if the current state does not satisfy then the current state is the counter ex
			//otherwise show all the states in all paths 
			labellingFormulaForEachState.put(state, formula);
		}
		else if (formula instanceof ExistsEventually) {
			//show all the states on all paths
			labellingFormulaForEachState.put(state, formula);
			
		} else if (formula instanceof ExistsNext) 
		{
			Formula f = ((ExistsNext) formula).getFormula();
			
			labellingFormulaForEachState.put(state, formula);
			Set<Integer> subPostStates = Post(state);
		     
		    Set<Integer> formulaUnsat = unSatForEachFormula.get(f);
		
			for (Iterator<Integer> it = subPostStates.iterator(); it.hasNext(); ) 
			{
		       	Integer s = it.next();
				if(formulaUnsat.contains(s))
				{
					list.add(s);
					CounterExampleHelper(f, s, list);
				}					
		    	
			}
		} else if (formula instanceof ExistsUntil) {

		}
		else if (formula instanceof ForAllAlways) {
			//do this first
			//one path one state in unsat sub formula 
			Formula subFormula = ((ForAllAlways) formula).getFormula();
			Set<Integer> subformulaUnsat = unSatForEachFormula.get(subFormula);
			Map<Integer,Integer> parent = new HashMap<>();
			Set<Integer> allReachableStates = this.getPath(state, parent);
			for (Iterator<Integer> it = allReachableStates.iterator(); it.hasNext(); )
			{
				Integer n = it.next();
				//find the parent nodes of the n and add to the list
				CounterExampleHelper(subFormula, n, list);
				return;
			}
			
			
		}
		else if (formula instanceof ForAllEventually) {
			Formula subFormula = ((ForAllEventually) formula).getFormula();
			Set<Integer> subformulaUnsat = unSatForEachFormula.get(subFormula);
			
			//for (Iterator<Integer> it = states.iterator(); it.hasNext(); ) 
		    {
		       	//Integer n = it.next();
				//find the path from state
			}
		    //go through the states of the path that non of the states on that path sat sub formula
		 	//send one by one to the next recursive call
			
		}
		else if (formula instanceof ForAllNext) {
			Formula f = ((ForAllNext) formula).getFormula();
			
			Set<Integer> subPostStates = Post(state);
			
			labellingFormulaForEachState.put(state, formula);	    
		    Set<Integer> formulaUnsat = unSatForEachFormula.get(f);

			for (Iterator<Integer> it = subPostStates.iterator(); it.hasNext(); ) 
			{
		       	Integer s = it.next();
				if(formulaUnsat.contains(s))
				{
					list.add(s);
					CounterExampleHelper(f, s, list);
					return;
				}
		    	
			}		
		}
		else if (formula instanceof ForAllUntil) {
			
		} else if (formula instanceof Not) {
			// ! AX = EX !
		
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
	
	
	public Map<Integer,Formula> getLabellingFormulaForEachState()
	{
		return this.labellingFormulaForEachState;
	}
	
	public Set<Integer> getPostStates(Integer s)
	{
		return this.Post(s);
	}
	
	private Set<Integer> getPath(Integer s, Map<Integer,Integer> parent)
    {
        // Mark all the vertices as not visited(By default
        // set as false)
        boolean visited[] = new boolean[pts.getStates().size()];
        // Create a queue for BFS
        LinkedList<Integer> queue = new LinkedList<Integer>();
        Set<Integer> result = new HashSet<>();
        
        // Mark the current node as visited and enqueue it
        visited[s]=true;
        queue.add(s);
 
        while (queue.size() != 0)
        {
            // Dequeue a vertex from queue and print it
            s = queue.poll();
            result.add(s);

            // Get all adjacent vertices of the dequeued vertex s
            // If a adjacent has not been visited, then mark it
            // visited and enqueue it
            Iterator<Integer> i = Post(s).iterator();
            while (i.hasNext())
            {
                int n = i.next();
                if (!visited[n])
                {
                	parent.put(n, s);
                    visited[n] = true;
                    queue.add(n);
                }
            }
        }
        return result;
    }
}