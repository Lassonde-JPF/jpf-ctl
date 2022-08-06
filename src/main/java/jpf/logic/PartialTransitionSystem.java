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

package jpf.logic;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;

/**
 * A class which represents a partial transition system.  The states of the system
 * are assumed to be numbered 0, 1, ...
 * 
 * @author Franck van Breugel
 * @author Matt Walker
 */
public class PartialTransitionSystem {

	// for each state, its successors
	private final Map<Integer, BitSet> successors;
	// for each label, its states
	private final Map<String, BitSet> labelling;
	// states that are partially explored
	private BitSet partial;
	// number of states
	private int numberOfStates;
	// number of states
	private int numberOfTransitions;

	// separates the source and target of a transition
	public static final String TRANSITION_SEPARATOR = " -> ";
	// separates the state and its labels
	public static final String LABEL_SEPARATOR = ": ";

	// introduces randomness
	private static final Random random = new Random();
	// maximum number of states of a random system
	private static final int MAX_STATES = 10;
	// probability that a state is fully explored in a random system
	private static final double FULLY_EXPLORED = 0.8;
	// maximum number of labels of a random system
	private static final int MAX_LABELS = 3;
	// probability that a label is used in any state in a random system
	private static final double LABELLED = 0.8;

	/**
	 * Initializes this transition system randomly.
	 */
	public PartialTransitionSystem() {
		this.numberOfStates = 1 + random.nextInt(MAX_STATES);	
		this.partial = new BitSet(numberOfStates);

		final double TRANSITIONS = 2 * Math.log(this.numberOfStates) / this.numberOfStates;
		this.successors = new HashMap<Integer, BitSet>();
		for (int source = 0; source < this.numberOfStates; source++) {
			BitSet post = new BitSet(this.numberOfStates);
			for (int target = 0; target < this.numberOfStates; target++) {
				if (random.nextDouble() < TRANSITIONS) {
					post.set(target);
					this.numberOfTransitions++;
				}
			}
			if (!post.isEmpty()) {
				this.successors.put(source, post);
			}
			if (random.nextDouble() > FULLY_EXPLORED) {
				this.partial.set(source);
			}
		}

		int labels = 1 + random.nextInt(MAX_LABELS);
		this.labelling = new HashMap<String, BitSet>();
		for (int index = 0; index < labels; index++) {
			BitSet stateSet = new BitSet(this.numberOfStates);
			this.labelling.put("label" + index, stateSet);
			if (random.nextDouble() < LABELLED) {
				do {
					for (int state = 0; state < this.numberOfStates; state++) {
						if (random.nextDouble() < LABELLED / this.numberOfStates) {
							stateSet.set(state);
						}
					}
				} while (stateSet.isEmpty());
			}
		}
	}

	/**
	 * Initializes this partial transition system randomly with the given set of labels.
	 * 
	 * @param labels a set of labels
	 */
	public PartialTransitionSystem(Set<String> labels) {
		this.numberOfStates = 1 + random.nextInt(MAX_STATES);	
		this.partial = new BitSet();

		final double TRANSITIONS = 2 * Math.log(this.numberOfStates) / this.numberOfStates;
		this.successors = new HashMap<Integer, BitSet>();
		for (int source = 0; source < this.numberOfStates; source++) {
			BitSet post = new BitSet(this.numberOfStates);
			for (int target = 0; target < this.numberOfStates; target++) {
				if (random.nextDouble() < TRANSITIONS) {
					post.set(target);
					this.numberOfTransitions++;
				}
			}
			if (!post.isEmpty()) {
				this.successors.put(source, post);
			}
			if (random.nextDouble() > FULLY_EXPLORED) {
				this.partial.nextSetBit(source);
			}
		}

		this.labelling = new HashMap<String, BitSet>();
		for (String label : labels) {
			BitSet stateSet = new BitSet(this.numberOfStates);
			this.labelling.put(label, stateSet);
			if (random.nextDouble() < LABELLED) {
				do {
					for (int state = 0; state < this.numberOfStates; state++) {
						if (random.nextDouble() < LABELLED / this.numberOfStates) {
							stateSet.set(state);
						}
					}
				} while (stateSet.isEmpty());
			}
		}
	}

	/**
	 * Initializes this partial transition system from the file with the given name.
	 * The transitions are extracted from a file named fileName.tra and the
	 * labelling of the states is extracted from a file named fileName.lab.
	 * 
	 * @param fileName the base name of the file containing the description of
	 * this transition system and its labelling
	 * @throws IOException if something goes wrong with reading the files
	 */
	public PartialTransitionSystem(String fileName) throws IOException {
		final String TRANSITION = "\\d+" + TRANSITION_SEPARATOR + "\\d+"; 
		final String PARTIAL = "\\d+( \\d+)*";
		final String STATES_AND_TRANSTIONS = "\\d+ \\d+";

		Scanner input = new Scanner(new File(fileName + ".tra"));

		// first line contains the number of states and the number of transitions
		String line = null;
		try {
			line = input.nextLine().trim();
			if (line.matches(STATES_AND_TRANSTIONS)) {
				this.parseNumberOfStatesAndTransitions(line);
			} else {
				throw new IOException("File " + fileName + ".tra not in the correct format");
			}
		} catch (NoSuchElementException e) {
			throw new IOException("File " + fileName + ".tra not in the correct format");
		}

		this.successors = new HashMap<Integer, BitSet>();
		try {
			line = input.nextLine().trim();
			while (line.matches(TRANSITION)) { // line represents a transition
				this.parseTransition(line);
				line = input.nextLine().trim();
			}
		} catch (NoSuchElementException e) {
			throw new IOException("File " + fileName + ".tra not in the correct format");
		}

		// last line contains the partially explored states
		this.partial = new BitSet();
		if (line.length() > 0) {
			if (line.matches(PARTIAL)) {
				this.parsePartial(line);
			} else {
				throw new IOException("File " + fileName + ".tra not in the correct format");	
			}
		}
		input.close();

		input = new Scanner(new File(fileName + ".lab"));
		Map<Integer, String> indices = new HashMap<Integer, String>();
		try {
			line = input.nextLine(); // first line containing the labels and their indices
			this.parseIndices(line, indices);
		} catch (NoSuchElementException e) {
			throw new IOException("File " + fileName + ".lab not in the correct format");
		}

		this.labelling = new HashMap<String, BitSet>();
		for (String label : indices.values()) { 
			this.labelling.put(label, new BitSet(this.numberOfStates));
		}
		while (input.hasNextLine()) { 
			line = input.nextLine(); // line represents a state labelling
			this.parseLabelling(line, indices);
		}
		input.close();
	}

	/**
	 * Parses the given line. 
	 * 
	 * @param line a line representing a transition
	 */
	private void parseTransition(String line) {
		String[] part = line.split(TRANSITION_SEPARATOR);
		int source = Integer.parseInt(part[0]);
		int target = Integer.parseInt(part[1]);

		BitSet post;
		if (this.successors.containsKey(source)) {
			post = this.successors.get(source);
		} else {
			post = new BitSet();
			this.successors.put(source, post);
		}
		post.set(target);
	}

	/**
	 * Parses the given line.
	 * 
	 * @param line a line representing the states that are partially explored
	 * @pre. line.length() > 0
	 */
	private void parsePartial(String line) {
		for (String state : line.split(" ")) {
			int source = Integer.parseInt(state);
			this.partial.set(source);
		}
	}

	/**
	 * Parses the given line.
	 * 
	 * @param line a line representing names of the labels and their indices
	 */
	private void parseIndices(String line, Map<Integer, String> indices) {
		for (String item : line.split(" ")) {
			String[] pair = item.split("=");
			int index = Integer.parseInt(pair[0]);
			String label = pair[1];
			indices.put(index, label);
		}
	}

	/**
	 * Parses the given line.
	 * 
	 * @param line a line representing a state and its labels
	 */
	private void parseLabelling(String line, Map<Integer, String> indices) {
		String[] part = line.split(LABEL_SEPARATOR);
		int state = Integer.parseInt(part[0]);
		for (String label : part[1].split(" ")) {
			int index = Integer.parseInt(label);
			if (indices.containsKey(index)) {
				this.labelling.get(indices.get(index)).set(state);
			}
		}
	}

	/**
	 * Parses the given line. 
	 * 
	 * @param line a line representing the number of states
	 */
	private void parseNumberOfStatesAndTransitions(String line) {
		String[] pair = line.split(" ");
		this.numberOfStates = Integer.parseInt(pair[0]);
		this.numberOfTransitions = Integer.parseInt(pair[1]);
	}

	@Override
	public String toString() {
		StringBuffer toString = new StringBuffer();

		toString.append(this.numberOfStates + " " + this.numberOfTransitions + "\n");

		for (Integer source : this.successors.keySet()) {
			BitSet post = this.successors.get(source);
			for (int target = post.nextSetBit(0); target != -1; target = post.nextSetBit(target + 1)) {
				toString.append(source + TRANSITION_SEPARATOR + target);
				toString.append("\n");
			}
		}

		for (int state = this.partial.nextSetBit(0); state != -1; state = this.partial.nextSetBit(state + 1)) {
			toString.append(state + " ");
		}
		toString.append("\n");

		List<String> labels = new ArrayList<String>(this.labelling.keySet());
		for (int index = 0; index < labels.size(); index++) {
			toString.append(index + "=" + labels.get(index) + " ");
		}
		toString.append("\n");

		for (int state = 0; state < this.numberOfStates; state++) {
			Set<Integer> labelSet = new HashSet<Integer>();
			for (int index = 0; index < labels.size(); index++) {
				String label = labels.get(index);
				if (this.labelling.get(label).get(state)) {
					labelSet.add(index);
				}
			}
			if (!labelSet.isEmpty()) {
				toString.append(state + LABEL_SEPARATOR);
				for (Integer label : labelSet) {
					toString.append(label + " ");
				}
				toString.append("\n");
			}
		}

		return toString.toString();
	}

	@Override
	public boolean equals(Object object) {
		if (object != null && this.getClass() == object.getClass()) {
			PartialTransitionSystem other = (PartialTransitionSystem) object;
			return this.numberOfStates == other.numberOfStates 
					&& this.numberOfTransitions == other.numberOfTransitions
					&& this.labelling.equals(other.labelling)
					&& this.partial.equals(other.partial)
					&& this.successors.equals(other.successors);
		} else {

			return false;
		}
	}

	/**
	 * Returns the number of states of this partial transition system.
	 * 
	 * @return the number of states of this partial transition system
	 */
	public int getNumberOfStates() {
		return this.numberOfStates;
	}

	/**
	 * Returns the number of transitions of this partial transition system.
	 * 
	 * @return the number of transitions of this partial transition system
	 */
	public int getNumberOfTransitions() {
		return this.numberOfTransitions;
	}

	/**
	 * Returns the set of successors of this partial transition system.
	 * 
	 * @return the set of successors of this partial transition system
	 */
	public Map<Integer, BitSet> getSuccessors() {
		return this.successors;
	}

	/**
	 * Returns the labelling of this partial transition system.
	 * 
	 * @return the labelling of this partial transition system
	 */
	public Map<String, BitSet> getLabelling() {
		return this.labelling;
	}

	/**
	 * Returns the set of states that are partially explored.
	 * 
	 * @return the set of states that are partially explored
	 */
	public BitSet getPartial() {
		return this.partial;
	}
}
