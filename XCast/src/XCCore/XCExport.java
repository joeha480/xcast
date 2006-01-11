package XCCore;

import java.io.File;

/**
 * <p>Plugins intended for exporting purposes must extend the this class.
 * To be recognized as a plugin, the class must also implement the 
 * plugin interface.</p>
 * 
 * <p>Note that the constructor defined below is the one used by the application
 * when creating a new instance. Additional constructors will not be noticed 
 * by the application.</p>
 * 
 * <p><i>Note! The export class is not recognized by the application, and therefor
 * serves no purpose.</i> </p>
 * 
 * @author  Joel Hakansson, TPB
 * @version 2005-mar-23
 * @see XCCore.XCPlugIn
 * @since 1.0
 */
public abstract class XCExport extends XCDocumentTransformation {
	protected File exportFile;
	
	public XCExport(XCBridge bridge) {
		super(bridge);
	}
	
	public final File getTarget(XCDocument doc) {
		return doc.getSaveDocument();
	}
	
	public final File getSource(XCDocument doc) {
		return doc.getWorkingDocument();
	}
	
	public final boolean export(XCDocument doc, File exportFile) {
		this.exportFile = exportFile;
		return transform(doc);
	}
}
