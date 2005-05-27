/*
 * Created on 2005-feb-16
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package mif.converters;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.text.MessageFormat;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;

import mif.utils.Node;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;


/**
 * ... beskrivning ...
 * 
 * @author  Joel Håkansson, TPB
 * @version 2005-feb-16
 * @see 
 * @since 1.0
 */
public class MifConverter {
	private static String DEFAULT_ENCODING = "UTF-8";
	private static boolean SILENT_MODE = false;
	private static MessageFormat MESS_DONE_PARSING = new MessageFormat("-> Done parsing {0} \"{1}\"");
	private static MessageFormat MESS_PROCESSING = new MessageFormat("-> Processing {0}");
	
	private String encoding;
	private javax.xml.transform.Transformer xslt;

	public MifConverter(Transformer xslt, String encoding) {
		this.encoding = encoding;
		this.xslt = xslt;
	}

	public MifConverter(Transformer xslt) {
		this(xslt, DEFAULT_ENCODING);
	}

	public MifConverter(String encoding) {
		this(null, encoding);
	}

	public MifConverter() {
		this(null, DEFAULT_ENCODING);
	}

	private boolean convertFile(Node mifTree, File output) {
		try {
			System.out.println("   Saving as XML...");
			PrintStream ps = new PrintStream(output, encoding);
			ps.println("<?xml version=\"1.0\" encoding=\""+encoding+"\"?>");
			ps.println("<!-- Generated with MIF2XML Version 2005-03-31 14:02 -->");
			mifTree.printAllToXML(ps);
			ps.close();
			System.out.println("   Transforming XML...");
			//Transform
		} catch (Exception e) { e.printStackTrace(); }
		return true;
	}

	
	private String getPath(File input) throws Exception {
		String path = input.getCanonicalPath();
		path = path.replace("\\", "/");
		if (path.lastIndexOf('/')!=-1) {
			path = path.substring(0, path.lastIndexOf('/'));
		} else {
			path = "";
		}
		return path;
	}
	
	public void convertF2N(File input, File output) throws Exception {
		MifFileParser mifFile = new MifFileParser(input);
		Node mifTree = mifFile.parse("MIFXML");
		String miftype = mifTree.getFirstChild().getName();
		if (miftype.equals("MIFFile")) {
			convertFile(mifTree, output);
			if (!SILENT_MODE) System.out.println(MESS_DONE_PARSING.format(new Object[]{miftype, input.getName()}));
		} else if (miftype.equals("Book")) {
			if (!SILENT_MODE) System.out.println("Checking links...");
			Node tmp2 = mifTree.getNextChild("BookComponent");
			boolean broken=false;
			while (tmp2!=null) {
				String tmpfn = tmp2.getFirstChild("FileName").getFirstAttribute();
				tmpfn = MifUnescape.unescape(MifUnescape.unescapeFileName(tmpfn),MifUnescape.WINDOWS_STANDARD);
				//String tmpnext = new String(getPath(input)+tmpfn).replace(" ", "_")+".mif";
				String tmpnext = getPath(input)+tmpfn+".mif";
				if (!(new File(tmpnext).exists())) {
					broken=true;
					if (!SILENT_MODE) System.out.println("  * Missing file: " + tmpnext);
				}
				tmp2 = mifTree.getNextChild("BookComponent");
			}
			if (broken) {
				throw new FileNotFoundException("Missing files");
			} else {
				if (!SILENT_MODE) System.out.println("-> Links OK!");
			}
			mifTree.reset();
			convertFile(mifTree, output); // spara bok-xml också...
			MifConverter mc = new MifConverter(xslt, encoding);
			Node tmp = mifTree.getNextChild("BookComponent");
			if (!SILENT_MODE) System.out.println(MESS_DONE_PARSING.format(new Object[]{miftype, input.getName()}));
			while (tmp!=null) {
				String filename = tmp.getFirstChild("FileName").getFirstAttribute();
				filename = MifUnescape.unescape(MifUnescape.unescapeFileName(filename),MifUnescape.WINDOWS_STANDARD);
				//String next = new String(getPath(input)+filename).replace(" ", "_")+".mif";
				String next = getPath(input)+filename+".mif";
				if (!SILENT_MODE) System.out.println(MESS_PROCESSING.format(new Object[]{next}));
				//mc.convert(new File(next), new File(next+".xml"));
				MifFileParser.parse(new File(next), new File(next+".xml"));
				tmp = mifTree.getNextChild("BookComponent");
			}
		}
	}
	
	// Ny version, ska vara snabbare och bättre! ;)
	public void convert(File input, File output) throws Exception {
		if (!SILENT_MODE) System.out.println(MESS_PROCESSING.format(new Object[]{input}));
		MifFileParser.parse(input, output);
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document doc = db.parse(output);
		Element docRoot = doc.getDocumentElement();
		String rootName = docRoot.getFirstChild().getNodeName();
		if (rootName.equals("MIFFile")) { // we're done
			//if (!SILENT_MODE) System.out.println(MESS_DONE_PARSING.format(new Object[]{rootName, input.getName()}));
		} else if (rootName.equals("Book")) {
			if (!SILENT_MODE) System.out.println("*  Checking links...");
			NodeList components = docRoot.getElementsByTagName("BookComponent");
			File[] nextFile = new File[components.getLength()];
			boolean broken=false;
			for (int i=0;i<components.getLength();i++) {
				String tmpfn = components.item(i).getFirstChild().getAttributes().item(0).getNodeValue(); 
				tmpfn = MifUnescape.unescapeFileNameFTF(tmpfn);
				nextFile[i] = new File(getPath(input), tmpfn+".mif");
				if (!(nextFile[i].exists())) {
					broken=true;
					if (!SILENT_MODE) System.out.println("  * Missing file: " + nextFile[i]);
				}
			}
			if (broken) { throw new FileNotFoundException("Missing files");
			} else { if (!SILENT_MODE) System.out.println("*  Links OK!"); }	
			//if (!SILENT_MODE) System.out.println(MESS_DONE_PARSING.format(new Object[]{rootName, input.getName()}));
			for (int i=0;i<components.getLength();i++) {
				if (!SILENT_MODE) System.out.println(MESS_PROCESSING.format(new Object[]{nextFile[i]}));
				MifFileParser.parse(nextFile[i], new File(nextFile[i]+".xml"));
			}
		}
	}
}
