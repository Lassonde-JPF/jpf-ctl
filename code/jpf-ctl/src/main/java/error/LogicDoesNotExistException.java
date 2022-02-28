package error;

/**
 * LogicDoesNotExistException - typically used when a LogicType is undefined.
 * 
 * @author Matthew Walker
 * @author Franck van Breugel
 */
public class LogicDoesNotExistException extends Exception {
	/**
	 * Static (Generated) Serial Version
	 */
	private static final long serialVersionUID = 2074287497195070800L;

	/**
	 * Initializes this LogicDoesNotExistException with a message
	 * 
	 * @param errorMessage
	 */
	public LogicDoesNotExistException(String errorMessage) {
		super(errorMessage);
	}
}
