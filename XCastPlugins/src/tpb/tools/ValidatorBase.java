package tpb.tools;

import java.awt.Rectangle;
import java.io.File;
import java.util.ArrayList;

import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXParseException;

import XCCore.XCBridge;
import XCCore.XCTool;

public class ValidatorBase extends XCTool {
	private DocumentBuilder validatingDocumentBuilder;
	private MyErrorHandler errorHandler;

	
	public ValidatorBase(XCBridge bridge, File schemaFile, String schemaNSUri) {
		super(bridge, XCTool.TOOL_TYPE_ANALYZER);
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		errorHandler = new MyErrorHandler();
		try {
			SchemaFactory sf = SchemaFactory.newInstance(schemaNSUri);
			Schema schema = sf.newSchema(schemaFile);
			dbf.setSchema(schema);
			validatingDocumentBuilder = dbf.newDocumentBuilder();
			validatingDocumentBuilder.setErrorHandler(errorHandler);
		} catch (Exception e) {
			e.printStackTrace();
			bridge.handleError(new Exception("Error when creating validator."));
		}
	}

	private class MyErrorHandler implements ErrorHandler {
		private ArrayList<String> messages = new ArrayList<String>();
		private StringBuffer mess;
		private int errors;
		private int fatalErrors;
		private int warnings;
		private String prev;
		private String cur;
		
		public MyErrorHandler() {
			reset();
		}
		
		public void reset() {
			errors = 0;
			fatalErrors = 0;
			warnings = 0;
			mess = new StringBuffer();
			prev = "";
		}
		
		public void error(SAXParseException exception)  {
			cur = parseException(exception);
			if (prev==null||!prev.equals(cur)) {
				prev=cur;	
				errors++;
				mess.append(cur+"\n");
			}
		}
		public void fatalError(SAXParseException exception)  {
			cur = parseException(exception);
			if (prev==null||!prev.equals(cur)) {
				prev=cur;		
				fatalErrors++;
				mess.append(cur+"\n");
			}
		}
		public void warning(SAXParseException exception) {
			cur = parseException(exception);
			if (prev==null||!prev.equals(cur)) {
				prev=cur;
				warnings++;
				mess.append(cur+"\n");
			}
		}
		private String parseException(SAXParseException ex) {
			String msg = ex.getMessage();
			int bi = msg.indexOf('?');
			int ei = msg.indexOf('&');
			if (bi<0) bi = 0;
			if (ei<0) ei = msg.length();
			msg = msg.substring(bi+1, ei);
			return "Line " + ex.getLineNumber()+", column:" + ex.getColumnNumber() + ": "+ msg;
		}
		
		public int getErrorCount() { return errors; }
		public int getWarningCount() { return warnings; }
		public int getFatalErrorCount() { return fatalErrors; }
		public ArrayList getMessages() { return messages; }
		public StringBuffer getMessageString() { return mess; }
	}
	
	public boolean execute() {
		try {
			errorHandler.reset();
			//System.out.println("*** Validation report ***");
			validatingDocumentBuilder.parse(bridge.getDocument().getWorkingDocument());
			//System.out.println(errorHandler.getMessages());
			//System.out.println(" - Summary -");
			//System.out.println("   Fatal errors: " + errorHandler.getFatalErrorCount());
			//System.out.println("   Errors: " + errorHandler.getErrorCount());
			//System.out.println("   Warnings: " + errorHandler.getWarningCount());
			//System.out.println("*** End of report ***");
			JDialog d = new JDialog(bridge.getFrame(), "Validation report");
			d.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			StringBuffer buf = errorHandler.getMessageString();
			
			buf.append("*** Summary ***\n");
			buf.append("   Fatal errors: " + errorHandler.getFatalErrorCount()+ '\n');
			buf.append("   Errors: " + errorHandler.getErrorCount()+'\n');
			buf.append("   Warnings: " + errorHandler.getWarningCount()+'\n');
			JTextArea ta = new JTextArea(buf.toString());
			d.add(new JScrollPane(ta));
			d.setSize(400,300);
			d.setLocationRelativeTo(null);
			d.setVisible(true);
	        Rectangle place = ta.modelToView(errorHandler.getMessageString().length());
	        if(place != null) ta.scrollRectToVisible(place);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			bridge.handleError(new Exception("Error while validating."));
			return false;
		}
	}

}
