/*
 * Created on 2005-jul-18
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package XCGui;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Wrapper for the org.w3c.dom.Node interface. The purpose of this class is
 * to control the toString method.
 * 
 * @author  Joel Hakansson, TPB
 * @version 2005-08-23
 * @since 1.0
 */
public class XCNode {
	Node node;
	
	public XCNode(Node node) throws NullPointerException {
		if (node==null) throw new NullPointerException();
		this.node = node;
	}
	
	public Node getNode() {
		return node;
	}
	
	public XCNode getChild(int index) {
		//try { 
			return new XCNode(node.getChildNodes().item(index));
		/*
		} catch (NullPointerException e) {
			System.out.println("HÄR"+node.getNodeType()+","+node.getNodeName());
			throw e;
		}*/
	}
	
	public XCNode getChild(Node child) {
		if (child==null) return null;
		NodeList nl = node.getChildNodes();
		for (int i=0; i<nl.getLength(); i++) 
			if (nl.item(i)==child) return getChild(i);
		return null;
	}
	
	public String toString() {
		StringBuffer str = new StringBuffer();
		switch (node.getNodeType()) {
			case Node.ELEMENT_NODE :
				str.append(node.getNodeName());
				NamedNodeMap attrs = node.getAttributes();
				for (int i=0; i<attrs.getLength(); i++) {
					Node attr = attrs.item(i);
					str.append(" " + attr.getNodeName() + "=\"" + attr.getNodeValue()+'"');
				}
				break;
			case Node.TEXT_NODE : 
				str.append(node.getNodeValue());
				break;
			case Node.COMMENT_NODE : 
				str.append("<!-- " + node.getNodeValue() + "-->");
		}
		return str.toString();
	}
	

	
}
