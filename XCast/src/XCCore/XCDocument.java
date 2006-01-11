
package XCCore;

import java.io.File;
import java.util.ArrayList;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import XCGui.XCDisplay;


/**
 * XCDocument manages the document 
 * 
 * @author  Joel Hakansson, TPB
 * @version 2005-08-26
 * @since 2005-mar-23
 */
public final class XCDocument {
	private final File originalFile;
	private final File sourceDocument;
	private final File workingDocument;
	private final File displayDocument;
	private File saveDocument;
	private Document sourceDOM;
	private Document workingDOM;
	private XCImport importMethod;
	private boolean isOpen;
	private DocumentBuilder documentBuilder;
	private DocumentBuilder validatingDocumentBuilder;
	private MyErrorHandler errorHandler;
	
	//private XCSettings settings;	
	//private XCDisplay displayMethod;
	//private boolean updateNeeded;
	//private boolean isUpdating;
	//private boolean quenedUpdate;
	//private String anchor;
	//private XCMonitor monitor;
	//private Thread thread;

	/**
	 * Same as calling XCDocument(originalFile, displayMethod, null, settings)
	 * @param originalFile the file selected by the user
	 * @param settings settings
	 * @throws Exception
	 */
	public XCDocument(File originalFile, XCSettings settings) throws Exception {
		this(originalFile, null, settings);
/*		this.settings = settings;
		load(originalFile, displayMethod);
		XCLogic.copyFile(originalFile, sourceDocument);
		XCLogic.copyFile(sourceDocument, workingDocument);
		updateNeeded = true;*/
	}

	/**
	 * @param originalFile the file selected by the user
	 * @param importMethod the import method, or null
	 * @param settings settings
	 * @throws Exception 
	 */
	public XCDocument(File originalFile, XCImport importMethod, XCSettings settings) throws Exception {
		//this.settings = settings;
		this.originalFile = originalFile;
		saveDocument = null;
		sourceDocument = File.createTempFile("XCDoc", ".source.xml", settings.getWorkspace());
		workingDocument = File.createTempFile("XCDoc", ".working.xml", settings.getWorkspace());
		displayDocument = File.createTempFile("XCDoc", ".display.xml", settings.getWorkspace());
		this.importMethod = importMethod;
		if (importMethod!=null) {
			if (importMethod.importer(this)==false) throw new Exception("Failed to import document.");
		}
		else XCUtils.copyFile(originalFile, sourceDocument);
		XCUtils.copyFile(sourceDocument, workingDocument);
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		documentBuilder = dbf.newDocumentBuilder();
/*
		errorHandler = new MyErrorHandler();
		try {
			SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI );
			sf.setErrorHandler(new MyErrorHandler());
			Schema schema = sf.newSchema(settings.getSchema());
			dbf.setSchema(schema);
		} catch (Exception e) {System.out.println("Error when creating validator."); }

		validatingDocumentBuilder = dbf.newDocumentBuilder();
		validatingDocumentBuilder.setErrorHandler(errorHandler);
*/
		sourceDOM = documentBuilder.parse(sourceDocument);
		workingDOM = documentBuilder.parse(sourceDocument); //=sourceDOM?
		isOpen = true;
	}

	/*private void load(File originalFile, XCDisplay displayMethod) {
		isOpen = true;
		updateNeeded = false;
		isUpdating = false;
		quenedUpdate = false;
		monitor = null;
		thread = null;
		this.originalFile = originalFile;
		saveDocument = null;
		try {
			sourceDocument = File.createTempFile("XCDoc", ".source.xml", settings.getWorkspace());
			workingDocument = File.createTempFile("XCDoc", ".working.xml", settings.getWorkspace());
			displayDocument = File.createTempFile("XCDoc", ".display.xml", settings.getWorkspace());
		} catch (Exception e) { e.printStackTrace(); }
		
		this.displayMethod = displayMethod;
	}*/

	private class MyErrorHandler implements ErrorHandler {
		private ArrayList messages = new ArrayList();
		private int errors;
		private int fatalErrors;
		private int warnings;
		
		public MyErrorHandler() {
			reset();
		}
		
		public void reset() {
			errors = 0;
			fatalErrors = 0;
			warnings = 0;			
		}
		
		public void error(SAXParseException exception) throws SAXException {
			errors++;
			//printException(exception);
			//throw exception;
		}
		public void fatalError(SAXParseException exception) throws SAXException {
			fatalErrors++;
			//printException(exception);
			//throw exception;
		}
		public void warning(SAXParseException exception) throws SAXException {
			warnings++;
			//printException(exception);
			//throw exception;
		}
		private void printException(SAXParseException ex) {
			messages.add("Validation error: " + ex.getLineNumber()+": "+ex.getMessage());
		}
		
		public int getErrorCount() { return errors; }
		public int getWarningCount() { return warnings; }
		public int getFatalErrorCount() { return fatalErrors; }
		public int getProblemsCount() { return errors + warnings + fatalErrors; }
	}
	
	/**
	 * The file selected by the user when opening/importing
	 * @return returns the file selected by the user when opening/importing
	 */
	public File getOriginalFile() {
		return originalFile;
	}

	/**
	 * When opening, the source document is a copy of the original file.
	 * When importing, the source document is the result of transforming the orignal file 
	 * using the specified import filter. 
	 * @return returns a copy or transformation of the original file.
	 */
	public File getSourceDocument() {
		return sourceDocument;
	}

	/**
	 * The result of user interaction
	 * @return returns the document that contains the result of user interaction
	 */
	public File getWorkingDocument() {
		return workingDocument;
	}

	/**
	 * The preview document 
	 * @return returns a transformation of the working document more suited for display
	 * @see XCDisplay
	 * @deprecated How the update of the document is handled is now up to the XCDisplay
	 */
	public File getDisplayDocument() {
		return displayDocument;
	}

	/**
	 * Get the import method used when opening the document.
	 * @return the method used when importing
	 */
	public XCImport getImportMethod() {
		return importMethod;
	}
	
	/**
	 * The document used when saving the working document
	 * @return returns the document used when saving
	 */
	public File getSaveDocument() {
		return saveDocument;
	}

	/**
	 * Closes the document, i.e. deletes all created files. The document cannot not be
	 * used after closing.
	 */
	public void close() {
		if (isOpen) {
		  sourceDocument.delete();
		  workingDocument.delete();
		  displayDocument.delete();
		  isOpen = false;
		}
	}

	//obsolete
	/*
	private File display() {
		if (isOpen) {
			while (isUpdating) {}
			return displayDocument;
		} else return null;
	}

	public boolean isUpdating() {
		return updateNeeded|isUpdating;
	}
	
	public void addMonitor(XCMonitor monitor) {
		this.monitor = monitor;
	}*/
/*
	private void start() {
		thread = new Thread(this);
		thread.start();
	}

	public void run() {
		while (isOpen) {
			if (updateNeeded) {
				if (monitor!=null) monitor.onStart("Updating preview");
				isUpdating = true;
				System.out.println("Display needs to be updated. Updating...");
				displayMethod.display();
				updateNeeded = false;
				System.out.println("Display updated.");
				isUpdating = false;
				if (monitor!=null) monitor.onDone("Done!");
				if (quenedUpdate) {
					quenedUpdate = false;
					updateNeeded = true;
				}
			}
			try { Thread.sleep(100); } catch (Exception e) {}
		}
	}*/

	/** 
	 * @param exp
	 * @return the string
	 * @throws Exception
	 */
	public String evaluateXPath(String exp) throws Exception {
		String result = "";		
		//InputSource xmlSource = new InputSource(new FileInputStream(sourceDocument));
		XPath xp = XPathFactory.newInstance().newXPath();
		result = xp.evaluate(exp, sourceDOM);
		return result;
	}
	
	/**
	 * 
	 * @param exp
	 * @return the numeric value of the xpath expression
	 * @throws Exception
	 */
	public int evaluateNumericXPath(String exp) throws Exception {
		//InputSource xmlSource = new InputSource(new FileInputStream(sourceDocument));
		XPath xp = XPathFactory.newInstance().newXPath();
		Double result = (Double)xp.evaluate(exp, sourceDOM, XPathConstants.NUMBER);
		return result.intValue();
	}
	
	/**
	 * 
	 *
	 */
	public int update() throws SAXParseException {
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			workingDOM = documentBuilder.parse(workingDocument);
			/*errorHandler.reset();
			workingDOM = validatingDocumentBuilder.parse(workingDocument);
			return errorHandler.getProblemsCount();*/
			return -1;
		} catch (SAXParseException e) { throw e; 
		} catch (Exception e) { 
			e.printStackTrace();
			return -1;
		}
	}
	
	/**
	 * 
	 * @return returns the working DOM
	 */
	public Document getWorkingDOM() {
		return workingDOM;
	}

	/**
	 * 
	 * @return returns the source DOM
	 */
	public Document getSourceDOM() {
		return sourceDOM;
	}
	
	/*
	public void requestUpdate() {
        if (isUpdating) quenedUpdate = true;
        else updateNeeded = true;
	}

	private class UpdatingException extends Exception {
		UpdatingException(String msg) {
			super(msg);
		}
	}
	*/
	/**
	 * @param exportMethod the method used when exporting (@see XCExport)
	 * @param saveDocument sets the file returned by getSaveDocument()
	 * @return returns true if exporting was successful, false otherwise
	 */
	public boolean export(XCExport exportMethod, File saveFile) {
		if (isOpen) {
			return exportMethod.export(this, saveFile);
		} else return false;
	}

	/**
	 * Calls save() after setting the destination of the operation
	 * @param saveDocument sets the file returned by getSaveDocument()
	 * @return true if saving was successful, false otherwise
	 */
	public boolean saveAs(File saveDocument) {
		if (isOpen) {
			this.saveDocument = saveDocument;
			return save();
		} else return false;
	}

	/**
	 * Saves the document
	 * @return true if saving was successful, false otherwise
	 */
	public boolean save() {
		if (saveDocument!=null && isOpen) {
			XCUtils.copyFile(workingDocument, saveDocument);
			return true;
		} else return false;
	}

}
