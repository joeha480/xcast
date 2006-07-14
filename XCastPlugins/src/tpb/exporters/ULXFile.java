package tpb.exporters;

import java.io.File;

import javax.swing.filechooser.FileFilter;

import utils.DefaultFileFilter;
import utils.StreamGobbler;
import XCCore.XCBridge;
import XCCore.XCDocument;
import XCCore.XCExport;
import XCCore.XCPlugIn;

public class ULXFile extends XCExport implements XCPlugIn {
	private File program;
	
	public ULXFile(XCBridge bridge) {
		super(bridge);
		program = new File(bridge.getSettings().getPluginsDir(), "tpb/exporters/ULXFile/run.bat");
	}

	protected boolean transform(XCDocument doc) {
		try {
			Runtime rt = Runtime.getRuntime();
			Process p = rt.exec(program + " \"" + doc.getWorkingDocument().getAbsolutePath() + "\" \"" + exportFile.getAbsolutePath() + "\"");
			StreamGobbler errorGobbler = new StreamGobbler(p.getErrorStream(), "ERROR");            
			StreamGobbler outputGobbler = new StreamGobbler(p.getInputStream(), "OUTPUT");
			errorGobbler.start();
			outputGobbler.start();
			int ret = p.waitFor();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public FileFilter getFilter() {
        DefaultFileFilter dff = new DefaultFileFilter();
        dff.addExtension("ulx");
        dff.setDescription("ULX-file");
        dff.setExtensionListInDescription(true);
        return dff;
	}
	
	public String getName() { return "ULX-File"; }
	public String getIdentifier() { return "www.tpb.se/xcast/exporters/ULXFile/version/1.0"; }
	public String getDescription() { return "Compiles an ULX-file from the current document."; }
	public String getAuthor() { return "Kåre Sjölander, TPB"; }
	public int getPlatform() { return XCPlugIn.PLATFORM_WINDOWS; }
	public boolean acceptsApplication(String app) { return true; }

}
