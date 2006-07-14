/*
 * Created on 2005-apr-04
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package tpb.importers;

import java.io.File;

import javax.swing.JPanel;
import javax.swing.filechooser.FileFilter;

import mif.converters.MifConverter;
import utils.DefaultFileFilter;
import XCCore.XCBridge;
import XCCore.XCDocument;
import XCCore.XCImport;
import XCCore.XCPlugIn;
import XCCore.XCSettings;



/**
 * ... beskrivning ...
 * 
 * @author  Joel Håkansson, TPB
 * @version 2005-apr-04
 * @see 
 * @since 1.0
 */
public class MIFImport extends XCImport implements XCPlugIn {
	public File xslt;
	public File pre_xslt;
	protected XCSettings settings;
	
	public MIFImport(XCBridge bridge) {
		super(bridge);
		settings = bridge.getSettings();
		xslt = new File(settings.getPluginsDir(), "tpb/importers/MIFImport/mif2DTBookNSFP.xsl");
		pre_xslt = new File(settings.getPluginsDir(), "tpb/importers/MIFImport/mifpre.xsl");
	}
	
	public boolean transform(XCDocument doc) {
		File source = this.getSource(doc);
		File target = this.getTarget(doc);
		//Runtime rt = Runtime.getRuntime();
		try {
			MifConverter mc = new MifConverter();
			File tmp = new File(source.getAbsolutePath()+".xml");
			try {
				System.out.println("Converting");
				File[] fs = mc.convert(source, tmp);
				int len = fs.length;
				File[] tmpf = new File[len];
				if (fs!=null) {
					System.out.println("Transforming - Phase I");
					for (int i=0; i<len; i++) {
						tmpf[i] = File.createTempFile("tmp", ".xml");						
						System.out.println("   -> Processing file " + fs[i]);
						utils.Transformer t = new utils.Transformer(fs[i], pre_xslt, tmpf[i]);
						t.transform();
					}
					for (int i=0; i<len; i++) {
						int j=0;
						while (!fs[i].delete()) {
							System.out.println("Unable to delete temp-file. Retrying...");
							j++;
							this.wait(100);
							if (j>100) { (new Exception("Unable to delete temp-file")).printStackTrace(); }
						}
						j=0;
						while (!tmpf[i].renameTo(fs[i])) {
							System.out.println("Unable to rename temp-file. Retrying...");
							j++;
							this.wait(100);
							if (j>100) { (new Exception("Unable to rename temp-file")).printStackTrace(); }
						}
					}
					System.out.println("Transforming - Phase II");
					if (len>1) System.out.println("   -> Compiling...");
					else System.out.println("   -> Processing file " + fs[0]);
					utils.Transformer t = new utils.Transformer(tmp, xslt, target);
					t.transform();
				}
			} catch (Exception e) {e.printStackTrace(); return false;}
			//tmp.delete();
		} catch (Exception e) {e.printStackTrace(); return false;}
		return true;
	}
	
	/**
	 * @inheritDoc
	 */
	public boolean prepareImport(JPanel panel) {
		return true;
	}
	
	/**
	 * @inheritDoc
	 */
	public String getName() { return "Maker Interchange Format"; }
	public String getDescription() { return "Converts FrameMaker (MIF) documents into XML"; }
	public String getAuthor() { return "Joel Håkansson, TPB"; }
	public String getIdentifier() { return "www.tpb.se/xcast/importers/mifimport/version/1.0"; }
	public int getPlatform() { return XCPlugIn.PLATFORM_ALL; }
	public boolean acceptsApplication(String app) { return true; }
	
	/**
	 * @inheritDoc
	 */
	public FileFilter getFilter() {
        DefaultFileFilter dff = new DefaultFileFilter();
        dff.addExtension("mif");
        dff.setDescription("Maker Interchange Format");
        dff.setExtensionListInDescription(true);
        return dff;
	}

}
