package error;

public class LabelDoesNotExistException extends Error {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8992054757257677415L;

	public LabelDoesNotExistException(String errorMessage) {
		super(errorMessage);
	}
}
