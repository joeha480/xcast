import java.awt.Rectangle;
import java.io.File;
import java.util.ArrayList;

import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXParseException;

public class XCValidator {
	private DocumentBuilder validatingDocumentBuilder;
	private MyErrorHandler errorHandler;
	private Schema schema;
	private File input;

	public XCValidator(File input, File schemaFile) {
		super();
		this.input = input;
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		errorHandler = new MyErrorHandler();
		try {
			SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
			schema = sf.newSchema(schemaFile);
			dbf.setSchema(schema);
			validatingDocumentBuilder = dbf.newDocumentBuilder();
			validatingDocumentBuilder.setErrorHandler(errorHandler);
		} catch (Exception e) {
			System.err.println("Error when creating validator.");
			System.exit(1);
		}
	}

	private class MyErrorHandler implements ErrorHandler {
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
		public StringBuffer getMessageString() { return mess; }
	}
	
	public boolean execute() {
		try {
			errorHandler.reset();
			System.out.println("*** Validation report ***");
			validatingDocumentBuilder.parse(input);
			System.out.println(errorHandler.getMessageString());
			System.out.println(" - Summary -");
			System.out.println("   Fatal errors: " + errorHandler.getFatalErrorCount());
			System.out.println("   Errors: " + errorHandler.getErrorCount());
			System.out.println("   Warnings: " + errorHandler.getWarningCount());
			System.out.println("*** End of report ***");
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Error while validating.");
			return false;
		}
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		if (args.length!=2) {
			System.err.println("Wrong number of argumets.");
			System.exit(1);
		}
		XCValidator xcv = new XCValidator(new File(args[0]), new File(args[1]));
		xcv.execute();
	}

}
