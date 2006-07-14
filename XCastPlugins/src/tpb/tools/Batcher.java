package tpb.tools;

import java.io.File;

import utils.StreamGobbler;
import XCCore.XCBridge;
import XCCore.XCDocument;
import XCCore.XCPlugIn;
import XCCore.XCTool;

public class Batcher extends XCTool implements XCPlugIn {
	private File program;

	public Batcher(XCBridge bridge) {
		super(bridge, XCTool.TOOL_TYPE_AUTOMATIC_TRANSFORMER);
		this.program = new File(bridge.getSettings().getPluginsDir(), "tpb/tools/Batcher/run.bat");
	}

	public boolean execute() {
		Runtime rt = Runtime.getRuntime();
		File saveFile = bridge.getDocument().getSaveDocument();
		if (saveFile!=null) {
			String fn = saveFile.getAbsolutePath();
			fn = fn.substring(0,fn.lastIndexOf('.'));
			try {
				Process p = rt.exec(program + " \"" + fn + "\"");
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
		} else {
			bridge.handleError(new Exception("File must be saved. Save and try again."));
			return false;
		}
	}

	public String getName() { return "Batcher"; }
	public String getIdentifier() { return "www.tpb.se/xcast/tools/batcher/version/1.0"; }
	public String getDescription() { return "Runs a bat-file"; }
	public String getAuthor() { return "Joel Håkansson, TPB"; }
	public int getPlatform() { return XCPlugIn.PLATFORM_WINDOWS; }
	public boolean acceptsApplication(String app) { return true; }

}
 