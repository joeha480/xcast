package XCCore;

import java.io.File;

import javax.swing.JFrame;

import XCGui.XCDisplayManager;

public interface XCBridge {

	public XCSettings getSettings();
	
	public XCDisplayManager getActiveDisplayManager();
	
	public XCDocument getDocument();
	
	public XCMessages getMessages();
	
	public JFrame getFrame();
	
	public void handleError(Exception e);
	
}
