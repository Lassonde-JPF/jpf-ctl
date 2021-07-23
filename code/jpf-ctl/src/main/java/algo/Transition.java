package algo;

/**
 * A class which represents a transition between two states.
 */
public class Transition {
	public int source;
	public int target;

	/**
	 * 
	 * Initializes this transition with the given source and target nodes.
	 * 
	 * @param source the source node of this transition
	 * @param target the target node of this transition
	 */
	public Transition(int source, int target) {
		this.source = source;
		this.target = target;
	}

	public int getSource() {
		return this.source;
	}

	public int getTarget() {
		return this.target;
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