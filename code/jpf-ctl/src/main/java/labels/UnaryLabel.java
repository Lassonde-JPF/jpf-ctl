package labels;

import java.util.Random;

/**
 * BinaryLabel - A label with only a class
 * 
 * @author Matthew Walker
 * @author Franck van Breugel
 */
public abstract class UnaryLabel implements Label {

	// Attributes
	private String name;

	/**
	 * Initializes this UnaryLabel with a given name (type)
	 * 
	 * @param name - Name of this label (Initial, End, etc.)
	 */
	public UnaryLabel(String name) {
		this.name = name;
	}

	@Override
	public String classDef() {
		return UnaryLabel.label_prefix + this.name;
	}
	
	public String getName() {
		return this.name;
	}

	@Override
	public String toString() {
		return this.name + this.classDef();
	}

	@Override
	public boolean equals(Object object) {
		if (object != null && this.getClass() == object.getClass()) {
			UnaryLabel other = (UnaryLabel) object;
			return this.name.equals(other.name) && this.classDef().equals(other.classDef());
		} else {
			return false;
		}
	}

	static UnaryLabel random() {
		return new Random().nextBoolean() ? new End() : new Initial();
	}
}
