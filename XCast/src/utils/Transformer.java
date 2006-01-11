/*
 * Created on 2005-jan-18
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package utils;

import java.io.File;
import java.util.Properties;

import javax.xml.transform.Templates;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.util.Hashtable;

/**
 * ... beskrivning ...
 * 
 * @author  Joel Håkansson, TPB
 * @version 2005-jan-18
 * @see 
 * @since 1.0
 */
public class Transformer {
	//Transform input XSLT [output [encoding]]
	String input;
	String xsltFile;
	String output;
	Hashtable settings;
	Properties transformerSettings;

	public Transformer(String input, String xsltFile, String output, Hashtable settings) {
		this.input = input;
		this.xsltFile = xsltFile;		
		this.output = output;
		this.settings = settings;
	}

	public Transformer(String input, String xsltFile, String output) {
		this.input = input;
		this.xsltFile = xsltFile;		
		this.output = output;
		this.settings = new Hashtable();
		this.settings.put("encoding", "UTF-8");
	}
	
	public Transformer(File input, File xsltFile, File output) {
		this.input = input.getAbsolutePath();
		this.xsltFile = xsltFile.getAbsolutePath();		
		this.output = output.getAbsolutePath();
		this.settings = new Hashtable();
		this.settings.put("encoding", "UTF-8");
	}

	public Transformer(String input, String xsltFile) {
		this.input = input;
		this.xsltFile = xsltFile;		
		this.output = input +  ".xml";
		this.settings = new Hashtable();
		this.settings.put("encoding", "UTF-8");
	}
	
	public Transformer(String input, String xsltFile, Hashtable settings) {
		this.input = input;
		this.xsltFile = xsltFile;		
		this.output = input +  ".xml";
		this.settings = settings;
	}
	
	public void setUp() throws Exception {
		
	}
	
	private void setProperty(String key) {
		if (settings.containsKey(key)) transformerSettings.setProperty(key, (String)(settings.get(key)));
	}
	
	private void setProperties() {
		int len = settings.size();
		java.util.Enumeration e = settings.keys();
		while (e.hasMoreElements()) {
			String key = (String)e.nextElement();
		  transformerSettings.setProperty(key, (String)settings.get(key));
		}
	}
	
	public void transform() throws Exception {
		try {
			StreamSource xmlSource = new StreamSource(new File(input));
			StreamSource xsltSource = new StreamSource(new File(xsltFile));			
			StreamResult xmlResult = new StreamResult(new File(output));
            Templates pss = TransformerFactory.newInstance().newTemplates(xsltSource);
            javax.xml.transform.Transformer xslt = pss.newTransformer();
            
			//javax.xml.transform.Transformer xslt = TransformerFactory.newInstance().newTransformer(xsltSource);
			transformerSettings = xslt.getOutputProperties();
			setProperties();
			//setProperty("encoding");
			//setProperty("omit-xml-declaration");
			xslt.setOutputProperties(transformerSettings);
            //xslt.getOutputProperties().list(System.out);
			xslt.transform(xmlSource, xmlResult);
		} finally {
			
		}
	}
}
