package project;

public class NullPlayerException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public NullPlayerException() {
		super("Cannot return the instance of Player because it is null.");
	}
}
