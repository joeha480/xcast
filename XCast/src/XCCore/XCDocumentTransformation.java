/*
 * Created on 2005-apr-01
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package XCCore;

import java.io.File;

import javax.swing.filechooser.FileFilter;

/**
 * XCDocumentTransformation
 * 
 * @author  Joel Håkansson, TPB
 * @version 2005-apr-01
 * @since 1.0
 */
public abstract class XCDocumentTransformation {
	protected XCBridge bridge;
	
	/**
	 * The default constructor
	 * @param bridge
	 */
	public XCDocumentTransformation(XCBridge bridge) {
		this.bridge = bridge;
	}
	/**
	 * Subclasses must override this method
	 * @param doc
	 * @return returns true if the transformation was successful, false otherwise
	 */
	protected abstract boolean transform(XCDocument doc);
	
	/**
	 * Get the document target
	 * @param doc
	 * @return returns the target file associated with this document
	 */
	public abstract File getTarget(XCDocument doc);
	
	/**
	 * Get the document souce
	 * @param doc  the document whos source 
	 * @return returns the source file associated with this document
	 */
	public abstract File getSource(XCDocument doc);
	
	/**
	 * Get the name of the transformation
	 * @return returns the name of the transformation
	 */
	public abstract String getName();
	public abstract FileFilter getFilter();
}
