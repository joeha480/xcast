import java.io.File;
import java.io.FileInputStream;

import javax.xml.XMLConstants;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.sax.SAXSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.xml.sax.InputSource;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

public class Test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Test t = new Test();
		System.out.println("Begin");
		File input = new File("test.xml");
		File schemaFile = new File("schema.xsd");
		try {

			javax.xml.parsers.SAXParserFactory spf = SAXParserFactory.newInstance();
			SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI );
			Schema schema = sf.newSchema(schemaFile);
			Validator v = schema.newValidator();
			v.setErrorHandler(t.new MyHandler());
			v.validate(new SAXSource(new InputSource(new FileInputStream(input))));
			/*System.out.println(schema);
			spf.setSchema(schema);
			SAXParser sp = spf.newSAXParser();
			sp.parse(input, t.new MyHandler());*/
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
		}
		System.out.println("End");

	}
	
	public class MyHandler extends DefaultHandler {
		
		public void error(SAXParseException exception) {
			printException(exception);
		}
		public void fatalError(SAXParseException exception) throws SAXParseException {
			printException(exception);
			throw exception;
		}
		public void warning(SAXParseException exception) {
			printException(exception);
		}
		
		private void printException(SAXParseException ex) {
			System.out.println("Validation error: " + ex.getLineNumber()+": "+ex.getMessage());
		}
	}

}
