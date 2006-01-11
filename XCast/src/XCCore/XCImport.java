
package XCCore;

import java.io.File;

import javax.swing.JPanel;
import javax.swing.filechooser.FileFilter;

/**
 * <p>Plugins intended for importing purposes must extend the this class.
 * To be recognized as a plugin, the class must also implement the 
 * plugin interface.</p>
 * 
 * <p>Note that the constructor defined below is the one used by the application
 * when creating a new instance. Additional constructors will not be noticed 
 * by the application.</p>
 * 
 * @author  Joel Hakansson, TPB
 * @version 2005-apr-04
 * @see XCCore.XCPlugIn
 * @since 1.0
 */
public abstract class XCImport extends XCDocumentTransformation {

	/**
	 * @inheritDoc
	 */
	public XCImport(XCBridge bridge) {
		super(bridge);
	}
	
	/**
	 * @inheritDoc
	 */
	public final File getTarget(XCDocument doc) {
		return doc.getSourceDocument();
	}
	
	/**
	 * @inheritDoc
	 */
	public final File getSource(XCDocument doc) {
		return doc.getOriginalFile();
	}
	
	public final boolean importer(XCDocument doc) {
		return transform(doc);
	}
	
	public abstract boolean prepareImport(JPanel panel);


}
