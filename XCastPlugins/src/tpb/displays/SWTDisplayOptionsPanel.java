/*
 * Created on 2005-aug-19
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package tpb.displays;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;

import javax.swing.DefaultListModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JPanel;

import XCCore.XCSettings;

/**
 * ... beskrivning ...
 * 
 * @author  Joel Håkansson, TPB
 * @version 2005-aug-19
 * @see 
 * @since 1.0
 */
public class SWTDisplayOptionsPanel extends JPanel {
	JComboBox tf;
	JCheckBox cb;
	File path;
	//XCSettings settings;
	/**
	 * 
	 */
	public SWTDisplayOptionsPanel(File path) {
		super();
		this.path = path;
		cb = new JCheckBox("Use transform");
		tf = loadXSLTs();
		add(cb);
		add(tf);
	}

    private JComboBox loadXSLTs() {
    	FilenameFilter ff = new FilenameFilter(){
    		public boolean accept(File dir, String name) {
    			return name.endsWith(".xsl")|name.endsWith(".xslt");
    		}
    	};
        File[] files = path.listFiles(ff);
        JComboBox jl = new JComboBox();
        for (int i=0;i<files.length;i++) {
        	jl.addItem(files[i].getName());
        }
        return jl;
    }
    
	public File getXSLT() {
		return new File(path, tf.getSelectedItem().toString());
	}
	
	public boolean transform() {
		return cb.isSelected();
	}
}
