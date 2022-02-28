package error;

/**
 * LabelDoesNotExistException - typically used when a label defined in a formula
 * is not expected by the client.
 * 
 * @author Matthew Walker
 * @author Franck van Breugel
 */
public class LabelDoesNotExistException extends Error {

	/**
	 * Static (Generated) Serial Version
	 */
	private static final long serialVersionUID = 8992054757257677415L;

	/**
	 * Initializes this LabelDoesNotExistException with a message
	 * 
	 * @param errorMessage
	 */
	public LabelDoesNotExistException(String errorMessage) {
		super(errorMessage);
	}
}
