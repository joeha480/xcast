/*
 * Created on 2005-jul-11
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package XCGui;

import java.awt.Component;
import java.util.ArrayList;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

/**
 * ... beskrivning ...
 * 
 * @author  Joel Hakansson, TPB
 * @version 2005-08-23
 * @since 1.0
 */
public class XCCellRenderer extends DefaultListCellRenderer {
	private static final long serialVersionUID=400;
	private ArrayList toolTips;
	
	public XCCellRenderer(ArrayList toolTips) {
		super();
		this.toolTips = toolTips;
	}
	
	public ArrayList getToolTips() {
		return toolTips;
	}
	
	public void setToolTips(ArrayList toolTips) {
		this.toolTips = toolTips;
	}
	
	public Component getListCellRendererComponent(
	      JList list,
			Object value,   // value to display
			int index,      // cell index
			boolean iss,    // is the cell selected
			boolean chf)    // the list and the cell have the focus
	    	{
	        /* The DefaultListCellRenderer class will take care of
	         * the JLabels text property, it's foreground and background
	         * colors, and so on.
	         */
	        super.getListCellRendererComponent(list, value, index, iss, chf);

	        /* We additionally set the JLabels toolTip property here.
	         */
	        setToolTipText(index < toolTips.size() ? toolTips.get(index).toString() : "");
	        return this;
	    }
	}
