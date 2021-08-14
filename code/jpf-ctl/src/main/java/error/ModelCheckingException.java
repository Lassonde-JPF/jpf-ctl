package error;

public class ModelCheckingException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6699400691308369397L;

	public ModelCheckingException(String errorMessage) {
		super(errorMessage);
	}
}
