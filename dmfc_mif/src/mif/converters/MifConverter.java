/*
 * Created on 2005-feb-16
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package mif.converters;

import java.io.File;
import java.io.PrintStream;
import java.text.MessageFormat;

import javax.xml.transform.Transformer;

import mif.utils.Node;


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
			ps.println("<!-- Generated with MIF2XML Version 2005-03-09 09:09 -->");
			mifTree.printAllToXML(ps);
			ps.close();
			System.out.println("   Transforming XML...");
			//Transform
		} catch (Exception e) { e.printStackTrace(); }
		return true;
	}
	
	private String unescapeFileName(String filename) {
		filename = filename.replace("<r\\>", "");
		filename = filename.replace("<v\\>", "");
		filename = filename.replace("<h\\>", "");
		filename = filename.replace("<c\\>", "/");
		filename = filename.replace("<u\\>", "../");
		return filename;
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
	
	public void convert(File input, File output) throws Exception {
		MifFileParser mifFile = new MifFileParser(input);
		Node mifTree = mifFile.parse("MIFXML");
		String miftype = mifTree.getFirstChild().getName();
		if (miftype.equals("MIFFile")) {
			convertFile(mifTree, output);
			if (!SILENT_MODE) System.out.println(MESS_DONE_PARSING.format(new Object[]{miftype, input.getName()}));
		} else if (miftype.equals("Book")) {
			convertFile(mifTree, output); // spara bok-xml också...
			MifConverter mc = new MifConverter(xslt, encoding);
			Node tmp = mifTree.getNextChild("BookComponent");
			if (!SILENT_MODE) System.out.println(MESS_DONE_PARSING.format(new Object[]{miftype, input.getName()}));
			while (tmp!=null) {
				String filename = tmp.getFirstChild("FileName").getFirstAttribute();
				filename = MifUnescape.unescape(unescapeFileName(filename),MifUnescape.WINDOWS_STANDARD);
				String next = new String(getPath(input)+filename).replace(" ", "_")+".mif";
				if (!SILENT_MODE) System.out.println(MESS_PROCESSING.format(new Object[]{next}));
				mc.convert(new File(next), new File(next+".xml"));
				tmp = mifTree.getNextChild("BookComponent");
			}
		}
	}
}
