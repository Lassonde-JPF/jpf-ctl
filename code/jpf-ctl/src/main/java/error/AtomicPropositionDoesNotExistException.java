package error;

public class AtomicPropositionDoesNotExistException extends Error {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1980095989914878314L;

	public AtomicPropositionDoesNotExistException(String errorMessage) {
		super(errorMessage);
	}
}
