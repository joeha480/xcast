package tpb.tools;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Set;

import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import XCCore.XCBridge;
import XCCore.XCPlugIn;
import XCCore.XCTool;

public class Validator extends ValidatorBase implements XCPlugIn {

	public Validator(XCBridge bridge) {
		super(bridge, bridge.getSettings().getSchema(), XMLConstants.W3C_XML_SCHEMA_NS_URI);
	}

	public String getName() { return "Validator"; }
	public String getIdentifier() { return "www.tpb.se/xcast/tools/validator/version/1.1"; }
	public String getDescription() { return "Generates validation report using the active schema"; }
	public String getAuthor() { return "Joel Håkansson, TPB"; }
	public int getPlatform() { return XCPlugIn.PLATFORM_ALL; }
	public boolean acceptsApplication(String app) { return true; }

}
