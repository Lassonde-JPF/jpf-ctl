package labels;

import java.util.Random;

public interface Label {
	
	/** 
	 * The static prefix associated with labels
	 */
	static final String label_prefix = "label.";
	
	/**
	 * Returns the class definition of this label.
	 * 
	 * @return the class definition of this label
	 */
	public String classDef();
	
	/**
	 * Returns the hashcode of this label.
	 * 
	 * @return the hashcode of this label
	 */
	public abstract int hashCode();
	
	/**
	 * Tests whether this label is equal to the given object.
	 * 
	 * @param object an object
	 * @return true if this formula is equal to the given object, false otherwise
	 */
	public abstract boolean equals(Object object);
	
	/**
	 * Generates a random label
	 * 
	 * @return a random label object
	 */
	static Label random() {
		return new Random().nextBoolean() ? BinaryLabel.random() : UnaryLabel.random();
	}
}
