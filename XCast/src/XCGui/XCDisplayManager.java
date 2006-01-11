/*
 * Created on 2005-aug-18
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package XCGui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;

import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.w3c.dom.Document;

import XCCore.XCBridge;
import XCCore.XCDocument;
import XCCore.XCPlugInManager;
import XCCore.XCSettings;

/**
 * This class handles a group of displays that views the same data. The 
 * application has two display managers, one for the input view and one
 * for the output view. It is responsible for adding, configuring and
 * removing displays.
 * 
 * @author  Joel Hakansson, TPB
 * @version 2005-08-22
 * @since 1.0
 */
public class XCDisplayManager extends JPanel {
	private static final long serialVersionUID=200;
	/**
	 * Signifies that this display manager is of type source.
	 */
	public static final int SOURCE = 0;
	/**
	 * Signifies that this display manager is of type target.
	 */
	public static final int TARGET = 1;
	private XCSettings settings;
	private JTabbedPane jtp;
	private int type;
	private XCDisplay activeDisplay;
	private XCDocument doc;
	private String previous;
	private int instance;
	private JFrame frame;
	private ArrayList<Class> plugins;
	private XCPlugInManager plugsManager;
	private JPanel optionsPanel = null;
	private XCBridge bridge;
	
	/**
	 * Default constructor.
	 * @param bridge the bridge used for this execution.
	 * @param plugsManager the plugin manager used for this execution.
	 * @param type the type of this display manager (source/target)
	 * @param frame the parent frame
	 */
	public XCDisplayManager(XCBridge bridge, XCPlugInManager plugsManager, int type, JFrame frame) {
		super(new BorderLayout());
		this.bridge = bridge;
		this.settings = bridge.getSettings();
		this.plugins = new ArrayList<Class>();
		this.plugsManager = plugsManager;
		this.type = type;
		this.frame = frame;
		jtp = new JTabbedPane();
		XCTreeDisplay disp = new XCTreeDisplay(bridge);
		try {
			this.plugins.add(XCTreeDisplay.class); //Default type
			//this.plugins.add(XCSWTDisplay.class); //Temporary
		} catch (Exception e) { e.printStackTrace(); }
		if (plugins!=null) this.plugins.addAll(plugsManager.getPluginsByType(XCDisplay.class));
		disp.setDisplayManager(this);
		
		activeDisplay = disp;
		jtp.add("View 1", disp);
		jtp.add("(new)", null);
		jtp.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				activeDisplay = (XCDisplay)jtp.getSelectedComponent();
				if (jtp.getSelectedIndex()==jtp.getTabCount()-1) {
					JPanel options = new JPanel(new BorderLayout());
					JOptionPane pane = new JOptionPane(options, 
							JOptionPane.PLAIN_MESSAGE, JOptionPane.OK_CANCEL_OPTION);
					final JDialog dialog = pane.createDialog(getFrame(), "Options");
					final JComboBox plugs = new JComboBox(getPlugIns().toArray());
					final JPanel wrap = new JPanel();
					
					plugs.addActionListener(new ActionListener(){
						public void actionPerformed(ActionEvent e) {
							Object source = e.getSource();
							if (source==plugs) {
								wrap.removeAll();
								try {
									Method m = ((Class)plugs.getSelectedItem()).getMethod("getOptionsPanel", new Class[]{getSettings().getClass()});
									setOptionsPanel((JPanel)m.invoke(null, new Object[]{getSettings()})); //static method, one argument
									if (optionsPanel!=null) wrap.add(optionsPanel);
									dialog.pack();
								} catch (Exception e2) {e2.printStackTrace();}
							}
						}
					});
					options.add(plugs, BorderLayout.NORTH);
					options.add(wrap, BorderLayout.CENTER);
					dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
					dialog.setVisible(true);
					Object selectedValue = pane.getValue();
					if (selectedValue.equals(new Integer(JOptionPane.OK_OPTION))) {
						addDisplay((Class)plugs.getSelectedItem(), optionsPanel);
					}
					dialog.dispose();
					jtp.setSelectedIndex(jtp.getTabCount()-2);
				}
			}
		});
		doc = null;
		previous = "";
		instance = 1;
		add(jtp);
	}

	private void setOptionsPanel(JPanel options) {
		optionsPanel = options;
	}
	
	/**
	 * Called when a document is opened
	 * @param doc the document to display
	 */
	public void loadDocument(XCDocument doc) {
		this.doc = doc;
		for (int i=0; i<jtp.getTabCount()-1; i++) {
			((XCDisplay)jtp.getComponentAt(i)).loadDocument();
		}
	}

	private ArrayList getPlugIns() {
		return plugins;
	}
	
	private JFrame getFrame() {
		return frame;
	}
	
	private XCSettings getSettings() {
		return settings;
	}
	/**
	 * Called when a document is closed.
	 *
	 */
	public void unloadDocument() {
		for (int i=0; i<jtp.getTabCount()-1; i++) {
			((XCDisplay)jtp.getComponentAt(i)).unloadDocument();
		}
		//jtp.removeChangeListener(jtp.getChangeListeners()[0]);
		//jtp.removeAll();
	}
	
	private void addDisplay(Class displayType, JPanel optionsPanel) {
		int count = jtp.getTabCount();
		ChangeListener cl = jtp.getChangeListeners()[0];
		jtp.removeChangeListener(cl);
		XCDisplay disp = (XCDisplay)plugsManager.newInstance(displayType);
		disp.setOptions(optionsPanel);
		disp.setDisplayManager(this);
		jtp.insertTab("View " + count, null, disp, null, (count-1));
		jtp.addChangeListener(cl);
		jtp.validate();
		disp.loadDocument();
	}
	
	
	private void removeDisplay(int index) {
		
	}
	
	/**
	 * Asks all displays to reload.
	 *
	 */
	public void reloadAll() {
		for (int i=0; i<jtp.getTabCount()-1; i++) {
			((XCDisplay)jtp.getComponent(i)).reloadDocument();
		}
	}

	/**
	 * Get the DOM that should be used when displaying.
	 * @return a document
	 */
	public Document getDisplayDOM() {
		switch (type) {
			case SOURCE : return doc.getSourceDOM();
			case TARGET : return doc.getWorkingDOM();
		}
		return null;
	}
	
	/**
	 * Get the file that should be used when displaying.
	 * @return a file
	 */
	public File getDisplayDocument() {
		switch (type) {
			case SOURCE : return doc.getSourceDocument();
			case TARGET : return doc.getWorkingDocument();
		}
		return null;
	}
	
	/** 
	 * Called when the displays should scroll to the next instance of an element
	 * @param elementName
	 */
	public void selectNextInstance(String elementName) {
		if (elementName.equals(previous)) {
			instance++;
		} else {
			previous = elementName;
			instance = 1;
		}
		activeDisplay.setDocumentPosition(elementName, instance);
	}

	/**
	 * Get the currently viewable display in this group.
	 * @return the active display
	 */
	public XCDisplay getActiveDisplay() {
		return activeDisplay;
	}

	/**
	 * Determine if displays within this display manager should allow editing.
	 * @return true if the display should allow editing, false otherwise.
	 */
	public boolean allowsEditing() {
		if (type==SOURCE) return true;
		else return false;
	}

}
