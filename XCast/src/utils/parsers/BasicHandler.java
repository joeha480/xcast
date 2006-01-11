
package utils.parsers;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.SAXParseException;

/**
 * Default base class for SAX2 event handlers including error messages.
 * 
 * @author  Joel Håkansson, TPB
 * @version 2005-jan-27
 * @since 1.0
 */
public class BasicHandler extends DefaultHandler {
    
    /**
     * Creates a new BasicHandler.
     */
    public BasicHandler() {
    	super();
    }

	/**
	 * Receive notification of a parser warning.
	 * A message is printed to System.out.
	 * @inheritDoc
	 */
	public void warning(SAXParseException e) {
		System.out.println("Warning:" + e.getMessage());
		System.out.println("Line:  " + e.getLineNumber());
		System.out.println("Column:" + e.getColumnNumber());		
	}
	
	/**
	 * Receive notification of a parser error.
	 * A message is printed to System.err.
	 * @inheritDoc
	 */	
	public void error(SAXParseException e) {
		System.err.println("Error: " + e.getMessage());
		System.err.println("Line:  " + e.getLineNumber());
		System.err.println("Column:" + e.getColumnNumber());
	}
	
	/**
	 * Receive notification of a fatal parser error.
	 * A message is printed to System.err.
	 * @inheritDoc
	 */
	public void fatalError(SAXParseException e) {
		System.err.println("Fatal error:" + e.getMessage());
		System.err.println("Line:  " + e.getLineNumber());
		System.err.println("Column:" + e.getColumnNumber());		
	}
	
	/**
	 * Receive notification of a notation declaration.
	 * A message is printed to System.out.
	 * @inheritDoc
	 */
	public void notationDecl(String name, String publicId, String systemId) {
		System.out.println("DTD:"+name+publicId+systemId);
	}
    
	/**
	 * Receive notification of an unparsed entity declaration.
	 * A message is printed to System.out.
	 * @inheritDoc
	 */
	public void unparsedEntityDecl(String name, String publicId, String systemId, String notationName) {
		System.out.println("DTD:"+name+publicId+systemId+notationName);
	} 
}

