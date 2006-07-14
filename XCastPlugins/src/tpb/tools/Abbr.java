package tpb.tools;

import java.awt.event.KeyEvent;

import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import XCCore.XCBridge;
import XCCore.XCPlugIn;
import XCCore.XCTool;

public class Abbr extends XCTool implements XCPlugIn {

	public Abbr(XCBridge bridge) {
		super(bridge, XCTool.TOOL_TYPE_AUTOMATIC_TRANSFORMER);
	}

	public boolean execute() {
		JPanel settingsPanel = new JPanel();
		JLabel label = new JLabel("Select:");
	    JCheckBox cbAbbr = new JCheckBox("Abbr");
	    cbAbbr.setMnemonic(KeyEvent.VK_A); 
	    cbAbbr.setSelected(true);
	    JCheckBox cbSent = new JCheckBox("Sent");
	    cbSent.setMnemonic(KeyEvent.VK_S); 
	    cbSent.setSelected(true);
	    JCheckBox cbWord = new JCheckBox("Word");
	    cbWord.setMnemonic(KeyEvent.VK_W); 
	    cbWord.setSelected(false);
	    settingsPanel.add(label);
	    settingsPanel.add(cbAbbr);
	    settingsPanel.add(cbSent);
		settingsPanel.add(cbWord);
		
		JOptionPane pane = new JOptionPane(settingsPanel, JOptionPane.PLAIN_MESSAGE, JOptionPane.OK_CANCEL_OPTION);
		JDialog dialog = pane.createDialog(bridge.getFrame(), "Select scripts to run");
		dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		dialog.setVisible(true);
		int value = ((Integer)pane.getValue()).intValue();
		dialog.dispose();
		if (value==JOptionPane.OK_OPTION) {
			return true;
		} else
			return false;
	}

	public String getName() { return "Abbr"; }
	public String getIdentifier() { return "www.tpb.se/xcast/tools/Abbr/version/1.0"; }
	public String getDescription() { return "..."; }
	public String getAuthor() { return "Joel Håkansson, TPB"; }
	public int getPlatform() { return XCPlugIn.PLATFORM_ALL; }
	public boolean acceptsApplication(String app) { return true; }

}
