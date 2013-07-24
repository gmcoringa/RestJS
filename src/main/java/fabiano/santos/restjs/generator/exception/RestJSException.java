/**
 * 
 */
package fabiano.santos.restjs.generator.exception;

/**
 * Default RestJS exception.
 * 
 * @author fabiano.santos
 * 
 */
public class RestJSException extends Exception {

	private static final long serialVersionUID = 6127707821322547396L;

	/**
	 * Default constructor.
	 */
	public RestJSException() {
		super();
	}

	/**
	 * @param message
	 * @param cause
	 */
	public RestJSException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 */
	public RestJSException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public RestJSException(Throwable cause) {
		super(cause);
	}

}
