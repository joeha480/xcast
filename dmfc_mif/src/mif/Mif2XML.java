
package mif;
import java.io.File;

import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import mif.converters.MifConverter;

/**
 * @author JOELH
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class Mif2XML {
	
	public static void main(String[] args) {
		String infile = "";
		String outfile = "";
		String encoding = "UTF-8";
		String xslt = "./xslt/mif2xhtml.xsl";
		if (args.length >= 1 & args.length <= 3) {
			infile = args[0];
		}
		if (args.length == 1) {
			System.out.println("Warning: Overwriting input.");
			outfile = args[0] + ".xml";
		} else if (args.length == 2) {
			outfile = args[1];
		} else if (args.length == 3) {
			outfile = args[1];
			encoding = args[2];
		} else {
			System.out.println();
			System.out.println("Mif2XML input [output [encoding]]");
			System.out.println("   input - path to the MIF-file");
			System.out.println("   output - path to the resulting XML-file");
			System.out.println("   encoding - E.g., UTF-8 or UNICODE");
			System.out.println();
			System.out.println("   Note! If \"output\" is omitted, the input file");
			System.out.println("         is overwritten.");
			System.out.println();
			System.out.println("   Joel Hakansson, TPB");
			System.out.println("   Version 2005-03-31");
			System.exit(1);
		}
		MifConverter mc = new MifConverter();
		try {
			mc.convert(new File(infile), new File(outfile));
		/*	StreamSource xmlSource = new StreamSource(new File(outfile));
			StreamSource xsltSource = new StreamSource(new File(xslt));			
			StreamResult xmlResult = new StreamResult(new File(outfile+".html"));
			javax.xml.transform.Transformer xsltT = TransformerFactory.newInstance().newTransformer(xsltSource);
            xsltT.getOutputProperties().list(System.out);
			xsltT.transform(xmlSource, xmlResult);*/
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
		/*
		try {
			MifFileParser mifFile = new MifFileParser(args[0]);
			Node mifTree = mifFile.parse("MIFXML");
			String miftype = mifTree.getFirstChild().getName();
			System.out.println("Done reading file: " + miftype);
			if (miftype.equals("MIFFile")) {
				convertFile(mifTree, outfile, encoding, xslt);
			} else if (miftype.equals("Book")) {
				Node tmp = mifTree.getNextChild("BookComponent");
				String path = args[0];
				path = path.replace("\\", "/");
				if (path.lastIndexOf('/')!=-1) {
					path = path.substring(0, path.lastIndexOf('/'));
				} else {
					path = "";
				}
				System.out.println(path);
				String part1 = "call c:\\program\\dzbatcher\\bin\\savemif";
				String part2 = "";
				String part3 = "call c:\\cUtils\\run join c:\\cUtils\\header.txt %1\r\n";
				while (tmp!=null) {
					String filename = tmp.getFirstChild("FileName").getFirstAttribute();
					filename = filename.replace("<r\\>", "");
					filename = filename.replace("<v\\>", "");
					filename = filename.replace("<h\\>", "");
					filename = filename.replace("<c\\>", "/");
					filename = filename.replace("<u\\>", "../");
					filename = path + filename;
					String newfilename = filename.replace(" ", "_");
					
					(new File(filename)).renameTo(new File(newfilename));
					//if (filename.indexOf("/")==0) filename = filename.substring(1);
					part1 += " " + newfilename;
					part2 += "call c:\\cUtils\\run mif " + newfilename + ".mif\r\n";
					part3 += "call c:\\cUtils\\run join " + newfilename + ".mif.xml.xml %1\r\n";
					tmp = mifTree.getNextChild("BookComponent");
				}
				part3 += "call c:\\cUtils\\run join c:\\cUtils\\footer.txt %1\r\n";
				PrintStream ps = new PrintStream(outfile);
				ps.println(part1);
				ps.println(part2);
				part3 = part3.replace("/", "\\");
				ps.println(part3);
				ps.close();
			}
		} catch (Exception e) {
			System.out.println("An error occured.");
			e.printStackTrace();
			System.exit(1);
		} finally {
			/*if (args.length == 1) {
				(new File(args[0])).delete();
				(new File(outfile)).renameTo(new File(args[0]));
			}*/
			//System.out.println("Operation completed.");
		//}
	}
}
