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
import java.util.Stack;
import java.util.stream.Collectors;
import ctl.And;
import ctl.False;
import ctl.Formula;
import ctl.Or;
import ctl.*;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
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
	private final Map<Formula,Set<Integer>> unsatForEachSubformula; 
	//Sink State val
	private static final int SINK_STATE = -2;

	// Target Transition System
	private final LabelledPartialTransitionSystem pts;

	// Constructor 
	public Model(LabelledPartialTransitionSystem pts) {
		this.post = new HashMap<Integer, Set<Integer>>();
		this.pre = new HashMap<Integer, Set<Integer>>();
		unsatForEachSubformula = new HashMap<>();
		this.pts = pts;
	}

	/*
	 * Returns the set of states that are successors to `state` and if not computed
	 * before, adds the entry to a hashtable, post
	 */
	private Set<Integer> Post(Integer state) {
		post.computeIfAbsent(state, k -> pts.getTransitions().stream()
				//.filter(t -> t.target != -2)
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
				//.filter(t -> t.target != -2)
				.filter(t -> t.target == state)
				.map(t -> t.source)
				.collect(Collectors.toSet()));
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
		 * Base Case
		 */
		if (formula instanceof True) {
			unsatForEachSubformula.put(formula,new HashSet<Integer>());
			return new StateSets(pts.getStates(), new HashSet<Integer>());
		}
		/*
		 * Base Case
		 */
		else if (formula instanceof False) {	
			unsatForEachSubformula.put(formula,pts.getStates());
			return new StateSets(new HashSet<Integer>(), pts.getStates());
		}
		/*
		 * Base Case
		 */
		else if (formula instanceof AtomicProposition) {
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
				Sat = pts.getStates().stream()
						.filter(s -> pts.getLabelling().containsKey(s))
						.filter(s -> pts.getLabelling().get(s).contains(val))
						.collect(Collectors.toSet());

				unSat = new HashSet<Integer>(pts.getStates());
				unsatForEachSubformula.put(formula,pts.getStates());
				unSat.removeAll(Sat);
				// This catch should never be triggered as we filter for these possibilities in
				// the FieldExists class
			} catch (ClassNotFoundException | NoSuchFieldException | SecurityException | IllegalArgumentException
					| IllegalAccessException e) {
				System.err.println("This error should never have happened:\n" + e.getMessage());
				return null;
			}
			return new StateSets(Sat, unSat);
		}
		else if (formula instanceof And) {
			And f = (And) formula;
			StateSets L = check(f.getLeft());
			StateSets R = check(f.getRight());

			Set<Integer> Sat = new HashSet<Integer>(L.getSat());
			Sat.retainAll(R.getSat());
			Set<Integer> unSat = new HashSet<Integer>(pts.getStates());
			unSat.removeAll(Sat);
			
			unsatForEachSubformula.put(f,unSat);
			return new StateSets(Sat, unSat);
		} else if (formula instanceof Or) {
			Or f = (Or) formula;
			StateSets L = check(f.getLeft());
			StateSets R = check(f.getRight());

			Set<Integer> Sat = new HashSet<Integer>(L.getSat());
			Sat.addAll(R.getSat());
			Set<Integer> unSat = new HashSet<Integer>(pts.getStates());
			unSat.removeAll(Sat);
			
			unsatForEachSubformula.put(f,unSat);
			return new StateSets(Sat, unSat);
		} else if (formula instanceof Implies) {
			// !a or b
			Implies f = (Implies) formula;
			StateSets L = check(f.getLeft());
			StateSets R = check(f.getRight());
			


			Set<Integer> Sat = new HashSet<Integer>(L.getUnSat());
			Sat.addAll(R.getSat());
			Set<Integer> unSat = new HashSet<Integer>(pts.getStates());
			unSat.removeAll(Sat);
			unsatForEachSubformula.put(f,unSat);
			return new StateSets(Sat, unSat);
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
			unsatForEachSubformula.put(f,unSat);
			return new StateSets(Sat, unSat);
		} else if (formula instanceof ExistsAlways) {
			ExistsAlways f = (ExistsAlways) formula;
			StateSets S  = check(f.getFormula());
			Set<Integer> Sat = S.getSat();

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
			unsatForEachSubformula.put(f,unSat);
			return new StateSets(T, unSat);
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
			unsatForEachSubformula.put(eE,unSat);
			return new StateSets(T, unSat);
		} else if (formula instanceof ExistsNext) {
			ExistsNext eN = (ExistsNext) formula;
			StateSets S = check(eN.getFormula()); // recursive part

			Set<Integer> Sat = pts.getTransitions().stream()
					.filter(t -> S.getSat().contains(t.target) && t.target != SINK_STATE) // TODO added sink state ?
					.map(t -> t.source)
					.collect(Collectors.toSet());
			Set<Integer> unSat = new HashSet<Integer>(pts.getStates());
			unSat.removeAll(Sat);
			unsatForEachSubformula.put(eN,unSat);
			return new StateSets(Sat, unSat);
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
			unsatForEachSubformula.put(eU,unSat);
			return new StateSets(T, unSat);
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
			
			unsatForEachSubformula.put(fA,T);
			return new StateSets(Sat, T);
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
			unsatForEachSubformula.put(fAF,T);
			return new StateSets(Sat, T);

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
					.filter(t -> !S.getSat().contains(t.target) && t.target != SINK_STATE) // TODO added sink state ?
					.map(t -> t.source)
					.collect(Collectors.toSet());

			Set<Integer> Sat = new HashSet<Integer>(pts.getStates());
			Sat.removeAll(unSat);
			unsatForEachSubformula.put(fN,unSat);
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
				Pre(sP).stream().filter(L.getUnSat()::contains).filter(s -> !T.contains(s)).forEach(s -> {
					E.add(s);
					T.add(s);
				});
			}
			Set<Integer> EU = pts.getStates().stream()
					.filter(s -> !T.contains(s))
					.collect(Collectors.toSet());

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
			Set<Integer>  notF = new HashSet<Integer>(pts.getStates());
			notF.removeAll(F);

			// Piece4: Piece2 && Piece3
			EU.retainAll(notF);

			// Final cleanup
			Set<Integer> unSat = new HashSet<Integer>(pts.getStates());
			unSat.removeAll(EU);
			unsatForEachSubformula.put(fAU,unSat);
			return new StateSets(EU, unSat);
		} else if (formula instanceof Not) {
			Not n = (Not) formula;
			StateSets S = check(n.getFormula());
			
			unsatForEachSubformula.put(n,S.getSat());
			return new StateSets(S.getUnSat(), S.getSat());
		} else {
			System.err.println("This formula type is unknown");
			return null;
		}
	}


	public LabelledPartialTransitionSystem getCounterExample(Formula f, Integer s )
	{
		Set<Integer> counterExStates = new HashSet<>();
		
        Set<Integer> states = new HashSet<>();
		states.add(s);
		CounterExampleHelper(f, states, counterExStates);
        
		Set<Transition> newTSTransitions = this.getRelatedTransitions(counterExStates);
		
		Map<Integer, Set<Object>> newTSLabelling = this.getRelatedLabellings(counterExStates);
		
		
		LabelledPartialTransitionSystem newTS = new LabelledPartialTransitionSystem(counterExStates,newTSTransitions, newTSLabelling);
		
		 
		return newTS;
	}

	private void CounterExampleHelper(Formula formula, Set<Integer> states, Set<Integer> list)
	{
    

   		/*
		 * Base Case
		 */
		if (formula instanceof True) {
			return;
		}
		/*
		 * Base Case
		 */
		else if (formula instanceof False) {	
			Set<Integer> formulaUnsat = unsatForEachSubformula.get(formula);
			list.addAll(formulaUnsat);
			return;
		}/*
		 * Base Case
		 */
		else if (formula instanceof AtomicProposition) {
			Set<Integer> formulaUnsat = unsatForEachSubformula.get(formula);
			for (Iterator<Integer> it = states.iterator(); it.hasNext(); ) 
			{
				Integer n = it.next();
				if(formulaUnsat.contains(n))
				{
					//add to the list and break;
					list.add(n);
					break;
				}
			}
			return;
		}
		else if (formula instanceof And) {
			Formula left = ((And) formula).getLeft();
			Formula right = ((And) formula).getRight();
           
			
			CounterExampleHelper(left, states, list);
			CounterExampleHelper(right, states, list);

		} else if (formula instanceof Or) {
			Formula left = ((Or) formula).getLeft();
			Formula right = ((Or) formula).getRight();
           
			
			CounterExampleHelper(left, states, list);
			CounterExampleHelper(right, states, list);
		} else if (formula instanceof Implies) {
			Formula left = ((Implies) formula).getLeft();
			Formula right = ((Implies) formula).getRight();
           
			
			CounterExampleHelper(left, states, list);
			CounterExampleHelper(right, states, list);
		} else if (formula instanceof Iff) {
			Formula left = ((Iff) formula).getLeft();
			Formula right = ((Iff) formula).getRight();
           
			
			CounterExampleHelper(left, states, list);
			CounterExampleHelper(right, states, list);
		} else if (formula instanceof ExistsAlways) {

		}
		else if (formula instanceof ExistsEventually) {

		} else if (formula instanceof ExistsNext) 
		{
			Formula f = ((ExistsNext) formula).getFormula();
			
			Set<Integer> subPostStates = new HashSet<>();
				
		    for (Iterator<Integer> it = states.iterator(); it.hasNext(); ) 
		    {
		       			Integer n = it.next();
						subPostStates.addAll(Post(n));
			}
		    
		    Set<Integer> formulaUnsat = unsatForEachSubformula.get(f);
		    Set<Integer> S = new HashSet<>();
			for (Iterator<Integer> it = subPostStates.iterator(); it.hasNext(); ) 
			{
		       	Integer s = it.next();
				if(formulaUnsat.contains(s))
				{
					S.add(s);
				}					
		    	
			}	
			
			CounterExampleHelper(f, S, list);

		} else if (formula instanceof ExistsUntil) {

		}
		else if (formula instanceof ForAllAlways) {

		}
		else if (formula instanceof ForAllEventually) {

		}
		else if (formula instanceof ForAllNext) {
			Formula f = ((ForAllNext) formula).getFormula();
			
			Set<Integer> subPostStates = new HashSet<>();
				
		    for (Iterator<Integer> it = states.iterator(); it.hasNext(); ) 
		    {
		       			Integer n = it.next();
						subPostStates.addAll(Post(n));
			}
		    
		    Set<Integer> formulaUnsat = unsatForEachSubformula.get(f);
		    Set<Integer> S = new HashSet<>();
			for (Iterator<Integer> it = subPostStates.iterator(); it.hasNext(); ) 
			{
		       	Integer s = it.next();
				if(formulaUnsat.contains(s))
				{
					S.add(s);
					break;
				}					
		    	
			}	
			CounterExampleHelper(f, subPostStates, list);
		}
		else if (formula instanceof ForAllUntil) {

		} else if (formula instanceof Not) {

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
	
	private Map<Integer, Set<Object>> getRelatedLabellings( Set<Integer> list)
	{
		Map<Integer, Set<Object>> result = new HashMap<>();
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
}