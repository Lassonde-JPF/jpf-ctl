package labels;

/**
 * End - A label representing the end of the JVM
 * 
 * @author Matthew Walker
 * @author Franck van Breugel
 */
public class End extends UnaryLabel {

	/**
	 * Initializes this End object
	 */
	public End() {
		super(End.class.getSimpleName());
	}

	@Override
	public String toString() {
		return this.getName();
	}
}
