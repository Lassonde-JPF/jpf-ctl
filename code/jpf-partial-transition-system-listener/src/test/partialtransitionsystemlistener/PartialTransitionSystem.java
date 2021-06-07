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
 * @author Franck van Breugel
 */
public class PartialTransitionSystem {

	/**
	 * 
	 */
	private class Transition {
		private int source;
		private int target;

		/**
		 * @param source
		 * @param target
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
	 * @param name
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
	 * @param other
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
