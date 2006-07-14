package tpb.displays;

import java.awt.BorderLayout;
import java.io.File;
import java.io.PrintStream;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.ProcessingInstruction;

import XCCore.XCBridge;
import XCCore.XCPlugIn;
import XCCore.XCSettings;
import XCCore.XCUtils;
import XCGui.XCDisplay;

/**
 * XCSWTDisplay uses the SWT package to render the xml-files. This method is 
 * much faster than using standard Java/Swing components. However, the embedding
 * of native components inside the Swing interface has its flaws. For this reason
 * this component is offered as a plugin to - rather than as a part of - the 
 * XCast software.
 * 
 * Known issues: Temporary file not deleted.
 * 
 * @author  Joel Hakansson, TPB
 * @version 2005-08-23
 * @since 1.0
 */
public class SWTDisplay extends XCDisplay implements XCPlugIn {
	private static final long serialVersionUID=100;
	private File dir;
	private File defaultScreen;
	private File path;
	private MyBrowser xmlBrowser;
	
    private JScrollPane xmlScrollPane;
    private String previous;
    private File css;
    private File displayFile;
    private boolean transformed;
    private XCSettings settings;
    

    /**
     * Default constructor. 
     * @param bridge
     */
	public SWTDisplay(XCBridge bridge) {
		super(bridge);
		this.settings = bridge.getSettings();
		dir = new File(settings.getPluginsDir(), "/tpb/displays/SWTDisplay/");
		XCUtils.copyFile(
				new File(dir, "generic.css"), 
				new File(settings.getWorkspace(), "generic.css"));

        displayFile = null;
        previous = "";
        xmlBrowser = new MyBrowser();
        xmlScrollPane = new JScrollPane(xmlBrowser);
        add(xmlScrollPane, BorderLayout.CENTER);
		transformed = false;
		defaultScreen = new File(dir, "default.html");
	}

	public static JPanel getOptionsPanel(XCSettings settings) {
		return new SWTDisplayOptionsPanel(new File(settings.getPluginsDir(), "/tpb/displays/SWTDisplay/xslt/"));
	}

	public void setOptions(JPanel options) {
		SWTDisplayOptionsPanel x = (SWTDisplayOptionsPanel)options;
		transformed = x.transform();
		path = x.getXSLT();
	}
	
	private boolean transform() {
		File source = manager.getDisplayDocument();
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document domDoc = db.parse(source);
			ProcessingInstruction pi = domDoc.createProcessingInstruction("xml-stylesheet", "type=\"text/xsl\" href=\""+path.getAbsolutePath() + '"');
			domDoc.insertBefore(pi, domDoc.getFirstChild());
    		Transformer tr = TransformerFactory.newInstance().newTransformer();
    		tr.setOutputProperty("indent", "no");
    		tr.setOutputProperty("method", "xml");
    		tr.transform(new DOMSource(domDoc), new StreamResult(displayFile));
		} 
		catch (Exception e) { e.printStackTrace(); }
		return false;
	}
	
	public void loadDocument() {
		unloadDocument();
		if (transformed) {
			css = new File(settings.getWorkspace(), "specific.css");
			try {
				displayFile = File.createTempFile("SWTDisplay", ".xml", settings.getWorkspace());
			} catch (Exception e) { e.printStackTrace(); }
			transform();
			setURL(displayFile, xmlBrowser);
		} else {
			setURL(manager.getDisplayDocument(), xmlBrowser);
		}
	}

	public void unloadDocument() {
		previous = "";
		setURL(defaultScreen, xmlBrowser);
		if (transformed) {
			if (css!=null) css.delete();
			if (displayFile!=null) displayFile.delete();
		}
	}

	public void reloadDocument() {
		previous = "";
		if (transformed) { transform(); }
	}

	public void selectXPath(String exp) {
		if (transformed) {
			if (exp.matches("\\(//\\w++\\)\\[\\d++\\]")) {
				String[] split = new String[2];
				split[0] = exp.substring(exp.indexOf("(//")+3, exp.indexOf(")[")); 
				split[1] = exp.substring(exp.indexOf("[")+1, exp.indexOf("]"));
				setPreviewPosition(split[0], Integer.parseInt(split[1]));
			}
		}
	}

	private void setPreviewPosition(String elementName, int instanceNo) {
		if (!elementName.equals(previous)) {
    		try {
    			PrintStream ps = new PrintStream(css);
    			ps.println("."+elementName+"{ font-size: 20px;background-color: #808080; color: #ffffff; border-color: #00ff00;}");
    			ps.close();
    		} catch (Exception e) { e.printStackTrace(); }
			setURL(defaultScreen, xmlBrowser);
			previous = elementName;
		}
		setURL(new File(displayFile+"#"+elementName+instanceNo), xmlBrowser);
	}

	public void setDocumentPosition(String elementName, int instanceNo) {
		if (transformed) {
			// set the position of the preview display
			setPreviewPosition(elementName, instanceNo);
		} // can't set the position of the xml view using SWT, IE
	}
	
    private void setURL(File url, MyBrowser br) {
    	br.setURL(url);
    	//br.getParent().getParent().getParent().validate();
    }

	/**
	 * @inheritDoc
	 */
	public String getName() { return "SWT Display"; }
	
	/**
	 * @inheritDoc
	 */
	public String getIdentifier() { return "www.tpb.se/xcast/displays/swtdisplay/version/1.0"; }
	
	/**
	 * @inheritDoc
	 */
	public String getDescription() { return "Embedded browser didsplay"; }
	
	/**
	 * @inheritDoc
	 */
	public String getAuthor() { return "Joel Håkansson, TPB"; }
	
	/**
	 * @inheritDoc
	 */
	public int getPlatform() {
		return XCPlugIn.PLATFORM_WINDOWS;
	}
	
	/**
	 * @inheritDoc
	 */
	public boolean acceptsApplication(String app) {
		return true;
	}

}
