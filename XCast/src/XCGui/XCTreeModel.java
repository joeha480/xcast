/*
 * Created on 2005-jul-15
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package XCGui;

import java.io.File;
import java.util.Vector;

import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;



/**
 * ... beskrivning ...
 * 
 * @author  Joel Hakansson, TPB
 * @version 2005-08-23
 * @see XCGui.XCTreeDisplay
 * @since 1.0
 */
public class XCTreeModel implements TreeModel {
	private XCNode document;
	private Vector listenerList = new Vector();
	
	public XCTreeModel(XCNode document) {
		this.document = document;
	}

	private void clean(Node node) {
		NodeList nl = node.getChildNodes();
		int i=0;
		int len = nl.getLength();
		while (i<len) {
			Node current = nl.item(i);
				if (current.getNodeType()==Node.TEXT_NODE && (current.getNodeValue()==null || current.getNodeValue().trim().equals(""))) {
					node.removeChild(current);
					len--;
				} else {
					clean(nl.item(i));
					i++;
				}
		}
	}
	
	/**
	 * @inheritDoc
	 */
	public Object getRoot() {
		return document;
	}

	/**
	 * @inheritDoc
	 */
	public Object getChild(Object parent, int index) {
		return ((XCNode)(parent)).getChild(index);
	}

	/**
	 * @inheritDoc
	 */
	public int getChildCount(Object parent) {
		/*NodeList nl = ((XCNode)(parent)).getNode().getChildNodes();
		if (nl.getLength()==1) System.out.println(nl.item(0).getNodeType());*/
		return ((XCNode)(parent)).getNode().getChildNodes().getLength();
	}

	/**
	 * @inheritDoc
	 */
	public boolean isLeaf(Object node) {
		return ((XCNode)(node)).getNode().getNodeType()==Node.TEXT_NODE;
	}


	/**
	 * Not implemented
	 * @inheritDoc
	 */
	public void valueForPathChanged(TreePath path, Object newValue) {
		// TODO Auto-generated method stub

	}

	/**
	 * @inheritDoc
	 */
	public int getIndexOfChild(Object parent, Object child) {
		if (parent==null | child==null) return -1;
		XCNode n = (XCNode)(parent);
		NodeList nl = n.getNode().getChildNodes();
		for (int i=0; i<nl.getLength(); i++) 
			if (nl.item(i)==((XCNode)(child)).getNode()) return i;
		return -1;
	}

	/**
	 * @inheritDoc
	 */
    public void addTreeModelListener(TreeModelListener listener) {
        if ((listener != null) && !listenerList.contains(listener)) {
            listenerList.addElement(listener);
        }
    }

    /**
     * @inheritDoc
     */
    public void removeTreeModelListener(TreeModelListener listener) {
        if (listener != null) {
            listenerList.removeElement(listener);
        }
    }

}
