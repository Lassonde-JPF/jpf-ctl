package algo;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

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
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

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
	private Map<Integer, Set<Integer>> labelling; // stateID -> indicies
	private Map<String, Integer> fields; // qualifiedFieldNames -> indicies

	// all states
	private Set<Integer> stateSet;

	// maximum number of states
	private static final int MAX_STATES = 50;

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

	/**
	 * Initializes this labeled partial transition system randomly.
	 */
	public LabelledPartialTransitionSystem() {
		Random random = new Random(System.currentTimeMillis());

		// The number of states that will be in this transition system
		states = 1 + random.nextInt(MAX_STATES);
		stateSet = IntStream.range(0, states).boxed().collect(Collectors.toSet());
		stateSet.add(SINK_STATE);

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
		final double TRANSITIONS = 2 * Math.log(states) / Math.pow(states, 1.5);// 2 * Math.log(states) / states;
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

		// Field Setup
		this.fields = new HashMap<String, Integer>();
		String[] fieldNames = new String[] { "algo.JavaFields.p1", "algo.JavaFields.p2", "algo.JavaFields.p3",
				"algo.JavaFields.p4" };
		for (int i = 0; i < fieldNames.length; i++) {
			fields.put(fieldNames[i], i);
		}

		// TODO So I need to label -2 (sink state) as true once and false another time
		// but not at the same time..?
		this.labelling = new HashMap<Integer, Set<Integer>>();
		for (int state = 0; state < states; state++) {
			Set<Integer> labelSet = new HashSet<Integer>();
			this.labelling.put(state, labelSet);
			// Do we give this state a labeling?
			if (random.nextDouble() < LABELLED) {
				// How many labels should this state have (roughly since it's a set and may have
				// duplicates)
				int labels = 1 + random.nextInt(MAX_LABELS_PER_STATE);
				for (int label = 0; label < labels; label++) {
					labelSet.add(random.nextInt(fields.size())); // next int is exclusive
				}
			}
		}
	}

	// Constructor for debugging with specific transition system
	public LabelledPartialTransitionSystem(int states, Set<Transition> transitions, Set<Integer> partial,
			Map<Integer, Set<Integer>> labelling, Map<String, Integer> fields) {
		this.states = states;
		stateSet = IntStream.range(0, states).boxed().collect(Collectors.toSet());
		stateSet.add(SINK_STATE);
		this.transitions = transitions;
		this.partial = partial;
		this.labelling = labelling;
		this.fields = fields;
	}
	
	public LabelledPartialTransitionSystem(Set<Integer> s, Set<Transition> t, Map<Integer, Set<Integer>> l)
	{
		this.partial = s;
		this.transitions = t;
		this.labelling = l;
		this.fields = new HashMap<>();
	}
	
	// Actual Constructor for production
	public LabelledPartialTransitionSystem(String jpfLabelFile, String listenerFile) throws IOException {
		// Wrap path string with Path object
		Path pathToListenerFile = Paths.get(listenerFile);
		Path pathToJpfLabelFile = Paths.get(jpfLabelFile);

		// Get files as stream (of lines)
		Stream<String> listenerFileLines = Files.lines(pathToListenerFile);
		Stream<String> jpfLabelFileLines = Files.lines(pathToJpfLabelFile);

		// regex for different line types
		final String TRANSITION = "-?\\d+\\s->\\s\\d+"; // 3 -> 4
		final String TRANSITION_DELIMETER = "\\s->\\s";
		final String PARTIAL = "(\\d+\\s?)+"; // 3 4 5
		final String PARTIAL_DELIMETER = "\\s";
		final String MAPPING = "(\\d+=\"(([a-zA-Z_$][a-zA-Z\\d_$]*\\.)*[a-zA-Z_$][a-zA-Z\\d_$]*)\"\\s?)+"; // 2="something"
																											// 3="anotherthing"
		final String MAPPING_DELIMETER = "\\s";
		final String LABELLING = "\\d+:\\s(\\d+\\s?)+"; // 2: 3 4
		final String LABELLING_DELIMETER = ":\\s";

		this.stateSet = new HashSet<Integer>();
		this.transitions = new HashSet<Transition>();
		this.partial = new HashSet<Integer>();
		// Listener File
		listenerFileLines.forEach(line -> {
			if (line.matches(TRANSITION)) {
				String[] t = line.split(TRANSITION_DELIMETER);
				int source = Integer.parseInt(t[0]);
				int target = Integer.parseInt(t[1]);
				this.stateSet.add(source);
				this.stateSet.add(target);
				this.transitions.add(new Transition(source, target));
			}
			if (line.matches(PARTIAL)) {
				this.partial.addAll(Pattern.compile(PARTIAL_DELIMETER).splitAsStream(line)
						.map(e -> Integer.parseInt(e))
						.collect(Collectors.toSet()));
				this.stateSet.addAll(this.partial);
			}
		});
		listenerFileLines.close();

		this.stateSet.add(SINK_STATE);
		this.states = this.stateSet.size();

		// jpf-label File
		this.labelling = new HashMap<Integer, Set<Integer>>();
		this.fields = new HashMap<String, Integer>();
		jpfLabelFileLines.forEach(line -> {
			if (line.matches(LABELLING)) {
				String[] lr = line.split(LABELLING_DELIMETER);
				Set<Integer> labels = new HashSet<Integer>();
				labels = Pattern.compile(MAPPING_DELIMETER).splitAsStream(lr[1])
						.map(Integer::parseInt)
						.filter(fields::containsValue)
						.collect(Collectors.toSet());
				if (!labels.isEmpty()) {
					this.labelling.put(Integer.parseInt(lr[0]), labels);
				}
			}
			if (line.matches(MAPPING)) {
				Pattern.compile(MAPPING_DELIMETER).splitAsStream(line).forEach(e -> {
					String l = e.replace("\"", "");
					int index = Integer.parseInt(l.split("=")[0]);
					String AP = l.split("=")[1];
					String[] LR = AP.split("__");
					if (LR[0].equals("true")) {
						this.fields.put(LR[1].replace("_", "."), index);
					}
				});
			}
		});
		jpfLabelFileLines.close();
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
		this.fields.entrySet().stream().forEach(e -> {
			toString.append(e.getKey() + " -> " + e.getValue());
			toString.append("\n");
		});
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
			for (Integer i : labelling.get(state)) {
				toDot.append((i + 2) + ":");
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

	public Map<Integer, Set<Integer>> getLabelling() {
		return this.labelling;
	}

	public Map<String, Integer> getFields() {
		return this.fields;
	}

	public Set<Integer> getPartial() {
		return this.partial;
	}

}
