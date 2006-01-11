
package XCGui;

import java.awt.BorderLayout;

import javax.swing.JPanel;

import XCCore.XCBridge;
import XCCore.XCSettings;

/**
 * <p>Plugins intended for displaying purposes must extend the this class.
 * To be recognized as a plugin, the class must also implement the 
 * plugin interface.</p>
 * 
 * <p>Note that the constructor defined below is the one used by the application
 * when creating a new instance. Additional constructors will not be noticed 
 * by the application.</p>
 * 
 * @author  Joel Hakansson, TPB
 * @version 2005-08-23
 * @see XCCore.XCPlugIn
 * @since 1.0
 */
public abstract class XCDisplay extends JPanel {
	protected XCBridge bridge;
	protected XCDisplayManager manager;
	
	/**
	 * Default constructor
	 * @param bridge
	 */
	public XCDisplay(XCBridge bridge) {
		super(new BorderLayout());
		this.bridge = bridge;
	}
	
	/**
	 * Sets the displays displayManger.
	 * @param manager
	 */
	public void setDisplayManager(XCDisplayManager manager) {
		this.manager = manager;
	}
	
	/**
	 * Called when a document is opened.
	 */
	public abstract void loadDocument();
	
	/**
	 * Called when one of the files within this document has changed.
	 */
	public abstract void reloadDocument();

	/** 
	 * Called when the displays should scroll to a certain instance of an element
	 * @param elementName
	 * @param instanceNo
	 */
	public abstract void setDocumentPosition(String elementName, int instanceNo);
	
	/**
	 * The displays should select the nodes of the xpath expression
	 * @param exp
	 */
	public abstract void selectXPath(String exp);
		
	/**
	 * Called when a document is closed. The display should release all resources 
	 * at this point, since there is no additional call when the program exits.
	 *
	 */
	public abstract void unloadDocument();
	
	/** 
	 * A panel containing additional options. This method will be called when initializing 
	 * the display. By default, this method returns null.
	 * @param settings 
	 * @return the options panel
	 */
	public static JPanel getOptionsPanel(XCSettings settings) {
		return null;
	}

	/**
	 * Called when the user has set up the panel given in getOptionsPanel.
	 * @param options
	 */
	public void setOptions(JPanel options) {}

}
