/**
 * 
 */
package fabiano.santos.restjs.generator.exception;

/**
 * Exception to identify empty or null URLs to be scanned.
 * 
 * @author fabiano.santos
 * 
 */
public class RestJSNullURL extends RestJSException {

	private static final long serialVersionUID = -8687835486323606500L;

	/**
	 * 
	 * @param message
	 */
	public RestJSNullURL(String message) {
		super(message);
	}

	/**
	 * 
	 * @param exception
	 * @param message
	 */
	public RestJSNullURL(Throwable exception, String message) {
		super(message, exception);
	}

}
