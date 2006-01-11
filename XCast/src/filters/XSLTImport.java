
package filters;

import java.io.File;

import javax.swing.JPanel;
import javax.swing.filechooser.FileFilter;

import utils.DefaultFileFilter;
import XCCore.XCBridge;
import XCCore.XCDocument;
import XCCore.XCImport;
import XCCore.XCSettings;



/**
 * This class transforms the document using the specified xslt.
 * 
 * @author  Joel Hakansson, TPB
 * @version 2005-08-26
 * @since 1.0
 */
public class XSLTImport extends XCImport {
	private File xslt;
	private XCSettings settings;
	/**
	 * Default constructor
	 * @param settings the global settings file
	 * @param xslt the xslt to use
	 */
	public XSLTImport(XCBridge bridge, File xslt) {
		super(bridge);
		settings = bridge.getSettings();
		this.xslt = xslt;
	}
	
	public boolean transform(XCDocument doc) {
		File source = this.getSource(doc);
		File target = this.getTarget(doc);
		try {
			utils.Transformer t = new utils.Transformer(source, xslt, target);
		    t.transform();
		} catch (Exception e) {e.printStackTrace(); return false;}
		return true;
	}
	
	public boolean prepareImport(JPanel panel) {
		return true;
	}
	
	public String getName() {
		if (xslt!=null) return "xslt:" + xslt.getName();
		return "XSLT import";
	}
	
	public FileFilter getFilter() {
        DefaultFileFilter dff = new DefaultFileFilter();
        dff.addExtension("xml");
        dff.setDescription("XML-file");
        dff.setExtensionListInDescription(true);
        return dff;
	}

}
