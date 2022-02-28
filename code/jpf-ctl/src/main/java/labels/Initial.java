package labels;

/**
 * Initial - A label representing the Initial stage of the JVM
 * 
 * @author Matthew Walker
 * @author Franck van Breugel
 */
public class Initial extends UnaryLabel {

	/**
	 * Initializes this Initial label
	 */
	public Initial() {
		super(Initial.class.getSimpleName());
	}

	@Override
	public String toString() {
		return this.getName();
	}

}
