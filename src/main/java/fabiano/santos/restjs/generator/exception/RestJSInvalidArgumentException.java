/**
 * 
 */
package fabiano.santos.restjs.generator.exception;

/**
 * @author fabiano.santos
 * 
 */
public class RestJSInvalidArgumentException extends RestJSException {

	private static final long serialVersionUID = 2419714393452730195L;

	/**
	 * Default constructor.
	 */
	public RestJSInvalidArgumentException() {
		super();
	}

	/**
	 * @param message
	 * @param cause
	 */
	public RestJSInvalidArgumentException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 */
	public RestJSInvalidArgumentException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public RestJSInvalidArgumentException(Throwable cause) {
		super(cause);
	}

}
