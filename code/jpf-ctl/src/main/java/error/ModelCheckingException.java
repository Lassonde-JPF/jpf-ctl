package error;

/**
 * ModelCheckingException - typically used when JPF encounters an error and does
 * not complete.
 * 
 * @author Matthew Walker
 * @author Franck van Breugel
 */
public class ModelCheckingException extends Exception {
	/**
	 * Static (Generated) Serial Version
	 */
	private static final long serialVersionUID = 6699400691308369397L;

	/**
	 * Initializes this ModelCheckingException with a message
	 * 
	 * @param errorMessage
	 */
	public ModelCheckingException(String errorMessage) {
		super(errorMessage);
	}
}
