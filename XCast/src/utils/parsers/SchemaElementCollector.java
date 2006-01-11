/*
 * Created on 2005-jan-27
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package utils.parsers;

import java.util.Hashtable;

import org.xml.sax.Attributes;

/**
 * ... beskrivning ...
 * 
 * @author  Joel Håkansson, TPB
 * @version 2005-jan-27
 * @since 1.0
 */
public class SchemaElementCollector extends BasicHandler {
    private Hashtable elements;
	/**
	 * @inheritDoc
	 */
	public SchemaElementCollector() {
		super();
    	elements=new Hashtable();
	}
	
	/**
	 * @inheritDoc
	 */
	public void startElement(String uri, String localName, String qName, Attributes attributes) {
		int val = 0;
		if (qName.equals("xs:element")) {
			String name = attributes.getValue("name");
			if (name!=null) elements.put(name, "null");
		}
	}
	
	/**
	 * @return elements
	 */
	public Hashtable getTable() {
		  return elements;	
	}
}
