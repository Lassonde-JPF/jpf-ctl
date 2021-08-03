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

	// states that are not fully explored
	private Set<Integer> partial;
	// transitions
	private Set<Transition> transitions;
	// labelling of the states
	private Map<Integer, Set<Object>> labelling;
	// all states
	private Set<Integer> stateSet;

	// maximum number of states
	private static final int MAX_STATES = 15;

	// probability that a state is not explored
	private static final double PARTIAL = 0.1;

	// maximum number of states
	private static final int MAX_LABELS_PER_STATE = 5;

	// probability that a state is labeled
	private static final double LABELLED = 0.9;

	// number of states in the system
	private int states;

	// sink state
	private static final int SINK_STATE = -2;

	public static final Object[] javaFields = new Object[] { java.lang.Integer.MAX_VALUE, java.lang.Integer.MIN_VALUE,
			java.lang.Double.MAX_VALUE, java.lang.Double.MIN_VALUE, java.lang.Float.MAX_VALUE,
			java.lang.Float.MIN_VALUE };

	/**
	 * Initializes this labeled partial transition system randomly.
	 */
	public LabelledPartialTransitionSystem() {
		Random random = new Random(System.currentTimeMillis());

		// The number of states that will be in this transition system
		states = 1 + random.nextInt(MAX_STATES);
		stateSet = IntStream.range(0, states).boxed().collect(Collectors.toSet());

		/*
		 * Randomly generates a set of states that will be considered 'not fully
		 * explored'
		 */
		this.partial = new HashSet<Integer>();
		for (int state = 0; state < states; state++) {
			if (random.nextDouble() < PARTIAL) {
				this.partial.add(state);
			}
		}

		/*
		 * Randomly generates transitions between states (explored and not explored)
		 */
		final double TRANSITIONS = 2 * Math.log(states) / states;
		this.transitions = new HashSet<Transition>();
		for (int source = 0; source < states; source++) {
			for (int target = 0; target < states; target++) {
				if (random.nextDouble() < TRANSITIONS) {
					this.transitions.add(new Transition(source, target));
				}
			}
			if (this.partial.contains(source)) {
				this.transitions.add(new Transition(source, SINK_STATE));
			}
		}

		// TODO So I need to label -2 (sink state) as true once and false another time
		// but not at the same time..?
		this.labelling = new HashMap<Integer, Set<Object>>();
		for (int state = 0; state < states; state++) {
			// Do we give this state a labeling?
			if (random.nextDouble() < LABELLED) {
				// How many labels should this state have (roughly since it's a set and may have
				// duplicates)
				int labels = 1 + random.nextInt(MAX_LABELS_PER_STATE);
				Set<Object> labelSet = new HashSet<Object>();
				this.labelling.put(state, labelSet);
				for (int label = 0; label < labels; label++) {
					labelSet.add(javaFields[random.nextInt(javaFields.length)]);
				}
			}
		}
	}

	
	public LabelledPartialTransitionSystem(Set<Integer> states, Set<Transition> t, Map<Integer, Set<Object>> l) {
		this.partial = states;
		this.transitions = t;
		this.labelling = l;
	}
	
	
	@Override
	public String toString() {
		
		StringBuffer toString = new StringBuffer();
		for (Transition transition : this.transitions) {
			toString.append(transition);
			toString.append("\n");
		}
		for (Integer state : this.partial) {
			toString.append(state);
			toString.append(" ");
		}
		toString.append("\n");
		for (Integer state : labelling.keySet()) {
			toString.append(state + ":");
			for (Object label : labelling.get(state)) {
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

			// if this state is not fully explored
			if (this.partial.contains(state)) {
				toDot.append("shape=box ");
			}

			int labels = labelling.get(state).size();

			// Is there more than one label?
			if (labels == 1) {
				toDot.append("style=filled fillcolor=");
			} else {
				toDot.append("fillcolor=\"");
			}

			// Append colors by label
			for (int i = 0; i < javaFields.length; i++) {
				if (labelling.get(state).contains(javaFields[i])) {
					toDot.append((i + 2) + ":");
				}
			}

			// remove last :
			toDot.setLength(toDot.length() - 1);
			if (labels != 1) {
				toDot.append("\"");
			}

			// add actual label (val of AP)
			if (labels > 0) {
				toDot.append(",label=\"" + state + ": ");
				for (Object label : labelling.get(state)) {
					toDot.append(label + ", ");
				}
				// remove last ,
				toDot.setLength(toDot.length() - 2);
				toDot.append("\"");
			}
			toDot.append("]\n");
		}

		toDot.append("}\n");

		return toDot.toString();
	}

	public Set<Integer> getStates() {
		return this.stateSet;
	}

	public Set<Transition> getTransitions() {
		return this.transitions;
	}

	public Map<Integer, Set<Object>> getLabelling() {
		return this.labelling;
	}

}
