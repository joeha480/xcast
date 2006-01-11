
package utils.parsers;
import java.io.File;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.tree.DefaultMutableTreeNode;

import org.xml.sax.Attributes;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import utils.SortableKeyValuePair;

/**
 * beskrivning
 * 
 * @author  Joel Hakansson, TPB
 * @version 2004-dec-16
 * @since 1.0
 */
public class ElementCounter extends BasicHandler {
    private Hashtable elements;
    private String element;
    private int level;
    private DefaultMutableTreeNode tn = new DefaultMutableTreeNode("root");
    private Vector cn = new Vector();
    
    public ElementCounter(File file) throws Exception {
    	super();
    	elements=new Hashtable();
    	level=0;
    	element="";
   		XMLReader xr = XMLReaderFactory.createXMLReader();
   		xr.setContentHandler(this);
   		xr.setErrorHandler(this);
   		xr.setDTDHandler(this);
   		xr.parse(file.getAbsolutePath());
    }

  /*  public ElementCounter(String[] required) {
    	super();
    	elements=new Hashtable();
    	level=0;
    	element="";
    }*/
        
	public void endDocument() {
			System.out.println();
			System.out.println("Done processing file. Found " + elements.size() + " elements.");
			//System.out.println(elements.toString());
	}

	public void startElement(String uri, String localName, String qName, Attributes attributes) {
		int val = 0;
		int len = attributes.getLength();
		String attrs = "";
		level++;
		if (len>0) {
			for (int i=0;i<len;i++) {
				attrs += " " + attributes.getQName(i)+"=\""+attributes.getValue(i)+"\"";
			}
		}
		if (elements.containsKey(qName)) {
			val = ((Integer)(elements.get(qName))).intValue();				
		}
		elements.put(qName, (new Integer(val+1)));
		//System.out.println(qName);
		cn.add(new DefaultMutableTreeNode(qName+attrs));
	}
	
	public void endElement(String uri, String localName, String qName) {
		level--;
		if (level>=1) {
			DefaultMutableTreeNode tmp = ((DefaultMutableTreeNode)(cn.elementAt(level-1)));
			tmp.add((DefaultMutableTreeNode)(cn.remove(level)));
			cn.setElementAt(tmp, level-1);
		}
		else tn.add((DefaultMutableTreeNode)(cn.elementAt(level)));
	}
	
	public void characters(char[] ch, int start, int length) {
		//System.out.println("Textblock:" + start + ", " + length);
		//System.out.println(new String(ch,start,length));
		String str = (new String(ch,start,length)).trim();
		if (!str.equals("")) { 
		DefaultMutableTreeNode tmp = ((DefaultMutableTreeNode)(cn.elementAt(level-1)));
		tmp.add(new DefaultMutableTreeNode(str));
		cn.setElementAt(tmp, level-1);
		}
	}
	
	/**
	 * Checks if the setting <tt>name</tt> has a defined value.
	 * @param name  the name of the value.
	 * @return true if a value has been assigned, false otherwise.
	 */
	public boolean isDefined(String name) {
		return elements.containsKey(name);
	}
	
	public Hashtable getTable() {
	  return elements;	
	}
	
	public SortableKeyValuePair[] getSortable() {
		int len = elements.size();
		SortableKeyValuePair[] ret = new SortableKeyValuePair[len];
		int i = 0;
	    for (Enumeration e = elements.keys() ; e.hasMoreElements() ;) {
	    	Object next = e.nextElement();
	         ret[i] = new SortableKeyValuePair(next.toString(), 
	         		Integer.parseInt(elements.get(next).toString()));
	         i++;
	    }
	    return ret;
	}
	
	public DefaultMutableTreeNode getTree() {
		//return (ExtendedMutableTreeNode)(tn.getFirstChild());
		return (DefaultMutableTreeNode)(tn.getFirstChild());
	}
	
	/**
	 * Returns the value associated with <tt>name</tt>.
	 * 
	 * @param name  the name of the value to get.
	 * @return the value associated with <tt>name</tt>.  
	 */
	public int get(String name) {
		return ((Integer)(elements.get(name))).intValue();
	}
}
