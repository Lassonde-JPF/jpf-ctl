package error;

/**
 * LabelReflectionException - typically used when a property of a label such as
 * a fully qualified pathname does not exist on a particular classpath and
 * cannot be reflected.
 * 
 * @author Matthew Walker
 * @author Franck van Breugel
 */
public class LabelReflectionException extends Error {

	/**
	 * Static (Generated) Serial Version
	 */
	private static final long serialVersionUID = 598159982138885175L;

	/**
	 * Initializes this LabelReflectionException with a message
	 * 
	 * @param errorMessage
	 */
	public LabelReflectionException(String errorMessage) {
		super(errorMessage);
	}
}
