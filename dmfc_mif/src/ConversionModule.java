import java.util.Collection;

/*
 * Created on 2005-feb-18
 */

/**
 * @author LINUSE
 */
public abstract class ConversionModule {

	/**
	 * Constructor.
	 */
	public ConversionModule(Collection listeners, boolean interactive) {
		// Do something interesting here
	}
	
	/**
	 * Get user input. This implementation only returns the default value.
	 * @param messageId the ID of the message (in an external language file)
	 * @param deafultValue the default value
	 * @param parameters parameters to the message
	 * @return the input from the user, or the default value if the module is in non-interactive mode
	 */
	final protected String getUserInput(String messageId, String deafultValue, Object[] parameters) {
		System.err.println("User input: " + messageId + "?");
		return deafultValue;
	}
	
	/**
	 * Fire a progress event. All listeners will be notified of the event.
	 * @param message a message
	 */
	final protected void fireProgressEvent(String message) {
		System.err.println("Progress: " + message);
	}
	
	/**
	 * Fire a progress event. All listeners will be notified of the event.
	 * @param message a message
	 * @param percentage a percentage value
	 */
	final protected void fireProgressEvent(String message, float percentage) {
		System.err.println("Progress: " + percentage + "% done. " +  message);
	}

}
