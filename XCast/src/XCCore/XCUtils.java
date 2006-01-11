package XCCore;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;

import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import XCGui.AutoComplete.DefaultDictionary;

import utils.StreamGobbler;
import utils.parsers.SchemaElementCollector;


/*
 * Created on 2005-apr-04
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

/**
 * ... beskrivning ...
 * 
 * @author  Joel Håkansson, TPB
 * @version 2005-apr-04
 * @see 
 * @since 1.0
 */
public class XCUtils {
	private final static String[] XSLT_DICTIONARY = new String[]{
			// Axis specifiers
			"ancestor","ancestor-or-self","attribute","child",
			"descendant","descendant-or-self","following","following-sibling","namespace",
			"parent","preceding","preceding-sibling","self",
			// Node test (ej komplett)
			"node","text","comment","processing-instruction",
			// Node set functions 
			"last","position","count","id","local-name","namespace-uri","name",
			// String functions
			"string","concat","starts-with","contains","substring-before",
			"substring-after","substring","string-length","normalize-space","translate",
			// Boolean functions
			"boolean","not","true","false","lang",
			// Number functions
			"number","sum","floor","ceiling","round",
			// XSLT functions
			"document","key","format-number","current","unparsed-entity-uri",
			"generate-id","system-property","element-available","function-available"};
	
    /*public static void performSaveAction(JPanel panel, Properties pl) {
        JFileChooser chooser = new JFileChooser();
        int returnVal = chooser.showSaveDialog(panel);
        if(returnVal == JFileChooser.APPROVE_OPTION) {
        	System.out.println("You chose to save this file: " + chooser.getSelectedFile().getName());
        	System.out.println("OK to save");
        	DTBookExport method = new DTBookExport(new String[]{""});
        	
        }
    }*/
	
    /**
     * Get a new DefaultDictionary containing the most common xslt key-words.
     */
    public static DefaultDictionary getXSLTDictionary() {
		return new DefaultDictionary((Collection)Arrays.asList(XSLT_DICTIONARY));
    }

    public static void execProcess(String exec) {
		Runtime rt = Runtime.getRuntime();
		try {
		  Process p = rt.exec(exec);
          StreamGobbler errorGobbler = new StreamGobbler(p.getErrorStream(), "ERROR");            
          StreamGobbler outputGobbler = new StreamGobbler(p.getInputStream(), "OUTPUT");
          errorGobbler.start();
          outputGobbler.start();
	   } catch (Exception e2) {e2.printStackTrace(); }
    }

    public static String getClassName(Object o) {
        String classString = o.getClass().getName();
        int dotIndex = classString.lastIndexOf(".");
        return classString.substring(dotIndex+1);
    }
    
    public static Object[] arrayMerge(Object[] begin, Object[] end) {
    	Object[] result = new Object[begin.length + end.length];
    	System.arraycopy(begin, 0, result, 0, begin.length);
    	System.arraycopy(end, 0, result, begin.length, end.length);
    	return result;
    }

    public static File saveAsDialog(JComponent parent) {
		JFileChooser chooser = new JFileChooser();    	
    	while (true) {
   			int returnVal = chooser.showSaveDialog(parent);
   			if (returnVal == JFileChooser.APPROVE_OPTION) {
   				File tmp = chooser.getSelectedFile();
   				int retVal = JOptionPane.YES_OPTION; 
   				if (tmp.exists()) { 
   					retVal = JOptionPane.showConfirmDialog(parent, "Skriv över "+tmp.getAbsoluteFile()+"?", "Varning!", JOptionPane.YES_NO_CANCEL_OPTION);
   				}
   				if (retVal == JOptionPane.YES_OPTION) { return tmp;
   				} else if (retVal == JOptionPane.CANCEL_OPTION) return null;
   			} else return null;
    	}
   	}
    
    public static void handleError(Exception e, JFrame parent) {
    	e.printStackTrace();
    	String msg = null;
    	if (e.getCause()!=null) msg = e.getCause().getLocalizedMessage();  
    	if (msg == null) msg = e.getLocalizedMessage();
    	if (msg == null) msg = e.getMessage();
    	JOptionPane.showMessageDialog(parent, msg , "Error!", JOptionPane.WARNING_MESSAGE);
    }
    
    public static String[] readDoc(XCSettings settings) {
    	String[] tmp = new String[0];
    	try {
    		SchemaElementCollector h = new SchemaElementCollector();
    		XMLReader xr = XMLReaderFactory.createXMLReader();
    		xr.setContentHandler(h);
    		xr.setErrorHandler(h);
    		xr.setDTDHandler(h);
    		xr.parse(settings.getSchema().getAbsolutePath());
    		Hashtable ht = h.getTable();
    		tmp = new String[ht.size()];
    		ht.keySet().toArray(tmp);
    		Arrays.sort(tmp);
    	} catch (Exception e) { e.printStackTrace(); }
    	String[] out = new String[tmp.length+3];
    	out[0] = "-bevara";
    	out[1] = "-platta";
    	out[2] = "-radera";
    	if (tmp.length>0) System.arraycopy(tmp,0,out,3,tmp.length);
      	return out;
    }
    
	public static void copyFile(File from, File to) {
		try {
		  FileInputStream is = new FileInputStream(from);
		  FileOutputStream os = new FileOutputStream(to);
		  byte[] buf = new byte[32000];
		  int i = 0;
		  while ((i=is.read(buf))>0){os.write(buf, 0, i);}
		  is.close();
		  os.close();
		} catch (Exception e) {e.printStackTrace(); }
	}
	
	private static void printDefaultRule(PrintStream out) {
		out.println("<xsl:template match='*'>");
		out.println("	<xsl:element name='{name()}'>");
		out.println("		<xsl:copy-of select='@*'/>");
		out.println("		<xsl:apply-templates/>");
		out.println("	</xsl:element>");
		out.println("</xsl:template>");
	}
	
	private static void printCommentRule(PrintStream out) {
		out.println("<xsl:template match='*' mode='comment'>");
		out.print("	<xsl:value-of select=\"concat('&lt;', name())\" disable-output-escaping='yes'/>");
		out.print("<xsl:for-each select='@*'>");
		out.print("<xsl:value-of select=\"concat(' ', name(), '=&quot;', ., '&quot;')\"/>");
		out.print("</xsl:for-each>>");
		out.print("<xsl:apply-templates mode='comment'/>");
		out.println("<xsl:value-of select=\"concat('&lt;/', name())\" disable-output-escaping='yes'/>>");
		out.println("</xsl:template>");
		out.println("<xsl:template match='text()' mode='comment'>");
		out.println("	<xsl:value-of select='.'/>");
		out.println("</xsl:template>");
	}
	
	public static void printStyleSheet(Properties props, PrintStream out) {
		out.println("<xsl:stylesheet version='1.0' xmlns:xsl='http://www.w3.org/1999/XSL/Transform'>");
		out.println("<xsl:output method='xml' indent='no' encoding='UTF-8'/>");
		out.println("<xsl:template match='comment()'>");
		out.println("	<xsl:copy-of select='.'/>");
		out.println("</xsl:template>");
		for (Enumeration e = props.propertyNames(); e.hasMoreElements();) {
			String key = e.nextElement().toString();
			String value = props.getProperty(key).trim();
			String attribs = "";
			String condition = "";
			int inx = value.indexOf(' ');
			if (inx>-1) {
				attribs = value.substring(inx+1).replace('\'', '"');
				value = value.substring(0,inx);
			}
			inx = key.indexOf('[');
			if (inx>-1) {
				condition = key.substring(inx).replace('\'', '"');
				key = key.substring(0,inx);
			}
			if (value.equals("-bevara")) {
				if (!attribs.equals("")) {
					out.println("<!-- Lägg till attribut "+attribs+" -->");
					out.println("<xsl:template match='"+key+condition+"'>");
					out.println("	<" + key + ' ' + attribs + '>');
					out.println("		<xsl:copy-of select='@*'/>");
					out.println("		<xsl:apply-templates/>");
					out.println("	</"+key+">");
					out.println("</xsl:template>");					
				}
			} else if (value.equals("-platta")) {
				out.println("<!-- Platta "+key+condition+ "-->");
				out.println("<xsl:template match='"+key+condition+"'>");
//				out.println("	<xsl:if test='count(*)=0'>");
//				out.println("		<xsl:value-of select='.'/>");
//				out.println("	</xsl:if>");
				out.println("	<xsl:apply-templates/>");
				out.println("</xsl:template>");
			}
			else if (value.equals("-radera")) {
				out.println("<!-- Radera "+key+condition+"-->");
				original: out.println("<xsl:template match='"+key+condition+"'/>");
			}
			else if (value.equals("-comment")) {
				out.println("<!-- Radera "+key+condition+"-->");
				out.println("<xsl:template match='"+key+condition+"'>");
				out.println("	<xsl:comment>");
				out.println("		<xsl:apply-templates select='.' mode='comment'/>");
				out.println("	</xsl:comment>");
				out.println("</xsl:template>");
			}
			else {
				out.println("<!-- Ersätt "+key+condition+" med "+value+" -->");
				out.println("<xsl:template match='"+key+condition+"'>");
				//out.println("	<xsl:element name='"+value+"'>");
				out.print("	<" + value);
				if (!attribs.equals("")) out.print(" " + attribs);
				out.println(">");
				
				out.println("		<xsl:copy-of select='@*'/>");
				out.println("		<xsl:apply-templates/>");
				out.println("	</"+value+">");
				//out.println("	</xsl:element>");
				out.println("</xsl:template>");
			}
		}
		printDefaultRule(out);
		out.println("</xsl:stylesheet>");		
	}

	public static Element addElement(Element parent, String name) {
		Document doc = parent.getOwnerDocument();
		Element el = doc.createElement(name);
		parent.appendChild(el);
		return el;
	}
	
	public static Text addTextNode(Element parent, String name) {
		Document doc = parent.getOwnerDocument();
		Text txt = doc.createTextNode(name);
		parent.appendChild(txt);
		return txt;
	}
	
	public static Attr addAttribute(Element parent, String name, String value) {
		Document doc = parent.getOwnerDocument();
		Attr attr = doc.createAttribute(name);
		attr.setNodeValue(value);
		
		return attr;
	}
	
	/**
	 * Lists all files within a directory-tree that matches the given FilenameFilter ff
	 * @param dir The root directory of the search
	 * @param ff The FilenameFilter to use, if null is provided, all files are accepted
	 * @return Returns a list of files in an ArrayList
	 */
    public static ArrayList<File> recursiveFileList(File dir, FilenameFilter ff) {
    	FilenameFilter dff = new FilenameFilter(){
    		public boolean accept(File dir, String name) {
    			return (new File(dir, name)).isDirectory();
    		}
    	};
    	File[] dirs = dir.listFiles(dff);
    	File[] files = dir.listFiles(ff);
    	ArrayList<File> result = new ArrayList<File>();
    	for (int i=0; i<files.length; i++ ) {
   			result.add(files[i]); 
    	}
    	for (int i=0; i<dirs.length; i++) {
			ArrayList<File> tmp = recursiveFileList(dirs[i], ff);
			if (!tmp.isEmpty()) result.addAll(tmp);    		
    	}
    	return result;
    }
    
    public static JScrollPane setIncrement(int inc, JScrollPane jsp) {
    	
    	JScrollBar vsb = jsp.getVerticalScrollBar();
    	vsb.setUnitIncrement(inc);
        jsp.setVerticalScrollBar(vsb);
        return jsp;
    }
    
    public static Node getChild(Node node, String name) {
    	NodeList children = node.getChildNodes();
    	for (int i=0; i<children.getLength(); i++) {
    		if (children.item(i).getNodeName().equals(name)) return children.item(i);
    	}
    	return null;
    }
    
    public static ArrayList<Node> getChildren(Node node, String name) {
    	ArrayList<Node> res = new ArrayList<Node>();
    	NodeList children = node.getChildNodes();
    	for (int i=0; i<children.getLength(); i++) {
    		if (children.item(i).getNodeName().equals(name)) res.add(children.item(i));
    	}
    	return res;
    }
}
