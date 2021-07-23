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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * A class which represents a labelled partial transition system.
 * 
 * @author Franck van Breugel
 * @author Matt Walker
 */
public class LabelledPartialTransitionSystem {

	// states that are fully explored
	private Set<Integer> processed;
	// transitions
	private Set<Transition> transitions;
	// labelling of the states
	private Map<Integer, Set<Integer>> labelling;

	// maximum number of states
	private static final int MAX_STATES = 1000;

	// probability that a state is fully explored
	private static final double PROCESSED = 0.8;

	// maximum number of states
	private static final int MAX_LABELS = 3;

	// probability that a state is labeled
	private static final double LABELLED = 0.8;
	
	//number of states in the system
	private int states;

	/**
	 * Initializes this labeled partial transition system randomly.
	 */
	public LabelledPartialTransitionSystem() {
		Random random = new Random(System.currentTimeMillis());

		states = 1 + random.nextInt(MAX_STATES);

		this.processed = new HashSet<Integer>();
		for (int state = 0; state < states; state++) {
			if (random.nextDouble() < PROCESSED) {
				this.processed.add(state);
			}
		}

		final double TRANSITIONS = 2 * Math.log(states) / states;
		this.transitions = new HashSet<Transition>();
		for (int source = 0; source < states; source++) {
			for (int target = 0; target < states; target++) {
				if (random.nextDouble() < TRANSITIONS) {
					this.transitions.add(new Transition(source, target));
				}
			}
		}

		int labels = 1 + random.nextInt(MAX_LABELS);
		this.labelling = new HashMap<Integer, Set<Integer>>();
		for (int state = 0; state < states; state++) {
			if (random.nextDouble() < LABELLED) {
				Set<Integer> labelSet = new HashSet<Integer>();
				this.labelling.put(state, labelSet);
				do {
					for (int label = 0; label < labels; label++) {
						if (random.nextDouble() < LABELLED / labels) {
							labelSet.add(label);
						}
					}
				} while (labelSet.isEmpty());
			}
		}
	}

	@Override
	public String toString() {
		StringBuffer toString = new StringBuffer();
		for (Transition transition : this.transitions) {
			toString.append(transition);
			toString.append("\n");
		}
		for (Integer state : this.processed) {
			toString.append(state);
			toString.append(" ");
		}
		toString.append("\n");
		for (Integer state : labelling.keySet()) {
			toString.append(state + ":");
			for (Integer label : labelling.get(state)) {
				toString.append(" " + label);
			}
			toString.append("\n");
		}
		return toString.toString();
	}

	/**
	 * Returns the dot representation of this labelled partial transition system as
	 * a string.
	 * 
	 * @return the dot representation of this labelled partial transition system as
	 *         a string.
	 */
	public String toDot() {
		StringBuffer toDot = new StringBuffer();

		toDot.append("digraph system {\n");
		toDot.append("  node [colorscheme=\"set312\" style=wedged]\n");

		for (Transition transition : this.transitions) {
			toDot.append(String.format("  %d -> %d%n", transition.source, transition.target));
		}

		for (Integer state : labelling.keySet()) {
			toDot.append("  " + state + " [");
			if (!this.processed.contains(state)) {
				toDot.append("shape=box ");
			}
			int number = labelling.get(state).size();
			if (number == 1) {
				toDot.append("style=filled fillcolor=");
			} else {
				toDot.append("fillcolor=\"");
			}
			for (Integer label : labelling.get(state)) {
				toDot.append((label + 1) + ":");
			}
			toDot.setLength(toDot.length() - 1); // remove last :
			if (number != 1) {
				toDot.append("\"");
			}
			toDot.append("]\n");
		}

		toDot.append("}\n");

		return toDot.toString();
	}
	
	
	//TODO this is super messy 
	public Set<Integer> getStates() {
		return IntStream.range(0, states).boxed().collect(Collectors.toSet());
	}
	
	public Set<Transition> getTransitions() {
		return this.transitions;
	}

	public Map<Integer, Set<Integer>> getLabelling()
	{
		return this.labelling;
	}

}
