package partialtransitionsystemlistener;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Scanner;
import java.util.Set;

/**
 * 
 * A class which represents a partial transition system.
 * 
 * @author Franck van Breugel
 */
public class PartialTransitionSystem {

	/**
	 * A class which represents a transition between two states.
	 */
	private class Transition {
		private int source;
		private int target;

		/**
		 * 
		 * Initializes this transition with the given source and target nodes.
		 * 
		 * @param source - the source node of this transition
		 * @param target - the target node of this transition
		 */
		public Transition(int source, int target) {
			this.source = source;
			this.target = target;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			return prime * this.source + this.target;
		}

		@Override
		public boolean equals(Object object) {
			if (object != null && this.getClass() == object.getClass()) {
				Transition other = (Transition) object;
				return this.source == other.source && this.target == other.target;
			} else {
				return false;
			}
		}

		@Override
		public String toString() {
			return this.source + " -> " + this.target;
		}
	}

	private Set<Transition> transitions;
	private Set<Integer> processed;
	private int max;

	/**
	 * 
	 * Initializes this PartialTransitionSystem using a file built from the
	 * PartialTransitionSystemListener.
	 * 
	 * @param name - the name of the file to build this PartialTransitionSystem
	 *             with.
	 * @throws FileNotFoundException
	 */
	public PartialTransitionSystem(String name) throws FileNotFoundException {
		this.transitions = new LinkedHashSet<Transition>();
		this.processed = new LinkedHashSet<Integer>();

		String regex = "-?\\d\\s->\\s\\d";
		BufferedReader br = new BufferedReader(new FileReader(new File(name)));
		br.lines().filter(line -> line.matches(regex)).forEach(line -> {
			String[] part = line.split("\\s->\\s"); // changed from " "
			int source = Integer.parseInt(part[0]);
			int target = Integer.parseInt(part[1]);
			this.transitions.add(new Transition(source, target));
			this.max = Math.max(this.max, Math.max(source, target));
		});
		br.lines().filter(line -> !line.matches(regex)).forEach(line -> {
			for (String part : line.split(" ")) {
				int state = Integer.parseInt(part);
				this.processed.add(state);
			}
		});
	}

	/**
	 * 
	 * Checks that `this` PartialTransitionSystem extends some `other`
	 * PartialTransitionSystem object.
	 * 
	 * @implNote A PartialTransitionSystem (A) extends another (B) iff, </br>
	 *           1. All transitions of B are in A </br>
	 *           2. All states that are fully explored in A are also fully explored
	 *           in B </br>
	 *           3. All states that are not fully explored in B are also not fully
	 *           explored in A </br>
	 *           4. All states that are fully explored in the A are not the source
	 *           of a new transition in B
	 * 
	 * 
	 * @param other - the other PartialTransitionSystem object to check against
	 * @throws PartialTransitionSystemException
	 */
	public void extend(PartialTransitionSystem other) throws PartialTransitionSystemException {

		for (Transition transition : other.transitions) {
			if (!this.transitions.contains(transition)) {
				throw new PartialTransitionSystemException(
						"Transition " + transition + " is part of the original system but not its extension");
			}
		}

		for (Integer state : other.processed) {
			if (!this.processed.contains(state)) {
				throw new PartialTransitionSystemException(
						"State " + state + " is fully explored in the original system but not its extension");
			}
		}

		for (int state = -1; state < this.max; state++) {
			if (!this.processed.contains(state) && other.processed.contains(state)) {
				throw new PartialTransitionSystemException("State " + state
						+ " is not fully explored in the extension but fully explored in the original system");
			}
		}

		for (Integer state : other.processed) {
			for (Transition transition : this.transitions) {
				if (!other.transitions.contains(transition) && transition.source == state) {
					throw new PartialTransitionSystemException("State " + state
							+ " is fully explored in the original system and the source of a new transition in the extension");
				}
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
		return toString.toString();
	}
}
