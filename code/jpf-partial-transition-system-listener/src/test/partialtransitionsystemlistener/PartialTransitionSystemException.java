package partialtransitionsystemlistener;

/**
 * An exception representing an issue with a given PartialTransitionSystem
 * object.
 * 
 * @author Matthew Walker, Franck van Breugel
 */
public class PartialTransitionSystemException extends Exception {

	private static final long serialVersionUID = -3352006110889061504L;

	public PartialTransitionSystemException(String message) {
		super(message);
	}
}