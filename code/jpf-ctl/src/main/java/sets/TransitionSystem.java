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

package sets;

import java.util.BitSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;

/**
 * A class which represents a partial transition system. The states of the
 * system are assumed to be numbered 0, 1, ...
 * 
 * @author Franck van Breugel
 * @author Matt Walker
 */

public class TransitionSystem {

	// separates the source and target of a transition
	public static final String TRANSITION_SEPARATOR = " -> ";
	// separates the state and its labels
	private static final String LABEL_SEPARATOR = ": ";
	
	// for each state, its successors
	private final Map<Integer, HashSet<Integer>> successors;
	// for each label, its index
	private final Map<String, Integer> indices;
	// for each label index, its states
	private final Map<Integer, HashSet<Integer>> labelling;
	// states that are partially explored
	private HashSet<Integer> partial;
	// number of states
	private int numberOfStates;

	// introduces randomness
	private static final Random random = new Random(System.currentTimeMillis());
	// maximum number of states of a random system
	private static final int MAX_STATES = 2;
	// probability that a state is fully explored in a random system
	private static final double FULLY_EXPLORED = 0.8;
	// maximum number of labels of a random system
	private static final int MAX_LABELS = 3;
	// probability that a label is used in any state in a random system
	private static final double LABELLED = 0.8;

	/**
	 * Initializes this transition system randomly.
	 */
	public TransitionSystem() {
		this.numberOfStates = 1 + random.nextInt(MAX_STATES);
		this.partial = new HashSet<Integer>();

		final double TRANSITIONS = 2 * Math.log(this.numberOfStates) / this.numberOfStates;
		this.successors = new HashMap<Integer, HashSet<Integer>>();
		for (int source = 0; source < this.numberOfStates; source++) {
			HashSet<Integer> post = new HashSet<Integer>(this.numberOfStates);
			for (int target = 0; target < this.numberOfStates; target++) {
				if (random.nextDouble() < TRANSITIONS) {
					post.add(target);
				}
			}
			if (!post.isEmpty()) {
				this.successors.put(source, post);
			}
			if (random.nextDouble() > FULLY_EXPLORED) {
				this.partial.add(source);
			}
		}

		int labels = 1 + random.nextInt(MAX_LABELS);
		this.indices = new HashMap<String, Integer>();
		for (int index = 0; index < labels; index++) {
			this.indices.put("C.f" + index, index);
		}

		this.labelling = new HashMap<Integer, HashSet<Integer>>();
		for (int index = 0; index < labels; index++) {
			HashSet<Integer> stateSet = new HashSet<Integer>(this.numberOfStates);
			this.labelling.put(index, stateSet);
			if (random.nextDouble() < LABELLED) {
				do {
					for (int state = 0; state < this.numberOfStates; state++) {
						if (random.nextDouble() < LABELLED / this.numberOfStates) {
							stateSet.add(state);
						}
					}
				} while (stateSet.isEmpty());
			}
		}
	}

	/**
	 * Initializes this partial transition system randomly with the given set of
	 * atomic proposition names.
	 * 
	 * @param atomicPropositions a set of atomic proposition names
	 */
	public TransitionSystem(Set<String> atomicPropositions) {
		this.numberOfStates = 1 + random.nextInt(MAX_STATES);
		this.partial = new HashSet<Integer>(this.numberOfStates);

		final double TRANSITIONS = 2 * Math.log(this.numberOfStates) / this.numberOfStates;
		this.successors = new HashMap<Integer, HashSet<Integer>>();
		for (int source = 0; source < this.numberOfStates; source++) {
			HashSet<Integer> post = new HashSet<Integer>(this.numberOfStates);
			for (int target = 0; target < this.numberOfStates; target++) {
				if (random.nextDouble() < TRANSITIONS) {
					post.add(target);
				}
			}
			if (!post.isEmpty()) {
				this.successors.put(source, post);
			}
			if (random.nextDouble() > FULLY_EXPLORED) {
				this.partial.add(source);
			}
		}

		this.indices = new HashMap<String, Integer>();
		int labels = 0;
		for (String name : atomicPropositions) {
			this.indices.put(name, labels);
			labels++;
		}

		this.labelling = new HashMap<Integer, HashSet<Integer>>();
		for (int index = 0; index < labels; index++) {
			HashSet<Integer> stateSet = new HashSet<Integer>(this.numberOfStates);
			this.labelling.put(index, stateSet);
			if (random.nextDouble() < LABELLED) {
				do {
					for (int state = 0; state < this.numberOfStates; state++) {
						if (random.nextDouble() < LABELLED / this.numberOfStates) {
							stateSet.add(state);
						}
					}
				} while (stateSet.isEmpty());
			}
		}
	}

	public TransitionSystem(Set<String> atomicPropositions, boolean fullyExplored) {
		this.numberOfStates = 1 + random.nextInt(MAX_STATES);
		this.partial = new HashSet<Integer>(this.numberOfStates);

		final double TRANSITIONS = 2 * Math.log(this.numberOfStates) / this.numberOfStates;
		this.successors = new HashMap<Integer, HashSet<Integer>>();
		for (int source = 0; source < this.numberOfStates; source++) {
			HashSet<Integer> post = new HashSet<Integer>(this.numberOfStates);
			for (int target = 0; target < this.numberOfStates; target++) {
				if (random.nextDouble() < TRANSITIONS) {
					post.add(target);
				}
			}
			if (!post.isEmpty()) {
				this.successors.put(source, post);
			}
			if (!fullyExplored) {
				this.partial.add(source);
			}
		}

		this.indices = new HashMap<String, Integer>();
		int labels = 0;
		for (String name : atomicPropositions) {
			this.indices.put(name, labels);
			labels++;
		}

		this.labelling = new HashMap<Integer, HashSet<Integer>>();
		for (int index = 0; index < labels; index++) {
			HashSet<Integer> stateSet = new HashSet<Integer>(this.numberOfStates);
			this.labelling.put(index, stateSet);
			if (random.nextDouble() < LABELLED) {
				do {
					for (int state = 0; state < this.numberOfStates; state++) {
						if (random.nextDouble() < LABELLED / this.numberOfStates) {
							stateSet.add(state);
						}
					}
				} while (stateSet.isEmpty());
			}
		}
	}

	public TransitionSystem(Map<Integer, HashSet<Integer>> successors, Map<String, Integer> indices, Map<Integer, HashSet<Integer>> labelling, HashSet<Integer> partial, int numberOfStates) {
		this.successors = successors;
		this.indices = indices;
		this.labelling = labelling;
		this.partial = partial;
		this.numberOfStates = numberOfStates;
	}

	@Override
	public String toString() {
		StringBuffer toString = new StringBuffer();

		for (Integer source : this.successors.keySet()) {
			HashSet<Integer> post = this.successors.get(source);
			for (Integer target : post) {
				toString.append(source + TRANSITION_SEPARATOR + target);
				toString.append("\n");
			}
		}
		for (Integer state : this.partial) {
			toString.append(state + " ");
		}
		toString.append("\n");

		toString.append(this.numberOfStates + "\n");

		for (String label : this.indices.keySet()) {
			int index = this.indices.get(label);
			toString.append(index + "=" + "\"true__" + label + "\" ");
		}
		toString.append("\n");

		for (int state = 0; state < this.numberOfStates; state++) {
			Set<Integer> labelSet = new HashSet<Integer>();
			for (Integer index : this.labelling.keySet()) {
				if (this.labelling.get(index).contains(state)) {
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
			TransitionSystem other = (TransitionSystem) object;
			return this.numberOfStates == other.numberOfStates && this.indices.equals(other.indices)
					&& this.labelling.equals(other.labelling) && this.partial.equals(other.partial)
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
	 * Returns the set of successors of this partial transition system.
	 * 
	 * @return the set of successors of this partial transition system
	 */
	public Map<Integer, HashSet<Integer>> getSuccessors() {
		return this.successors;
	}

	/**
	 * Returns the labelling of this partial transition system.
	 * 
	 * @return the labelling of this partial transition system
	 */
	public Map<Integer, HashSet<Integer>> getLabelling() {
		return this.labelling;
	}

	/**
	 * Returns the indices of the labels of this partial transition system.
	 * 
	 * @return the indices of the labels of this partial transition system
	 */
	public Map<String, Integer> getIndices() {
		return this.indices;
	}

	/**
	 * Returns the set of states that are partially explored.
	 * 
	 * @return the set of states that are partially explored
	 */
	@SuppressWarnings("unchecked")
	public HashSet<Integer> getPartial() {
		return (HashSet<Integer>) this.partial.clone();
	}
}
