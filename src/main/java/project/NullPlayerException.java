package project;

/**
 * This class is used for throwing runtime exceptions in the event a 
 * process attempts to get the {@code Player} instance before it has been set.
 */
public class NullPlayerException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public NullPlayerException() {
		super("Cannot return the instance of Player because it is null.");
	}
}
