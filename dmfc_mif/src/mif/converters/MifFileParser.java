
/**
 * @author JOELH
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package mif.converters;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Vector;

import mif.utils.Node;

public class MifFileParser {
	final static int BUFFSIZE = 2048;
	final static int NO_MODE = 0;
	final static int COMMENT_MODE = 1;
	final static int ELEMENT_MODE = 2;
	final static int ATTRIBUTE_MODE = 3;	
	final static int STRING_MODE = 4;
	
	private String fileName;
	private InputStreamReader input;
	private Node miffTree;
	
	public MifFileParser(String fileName) throws Exception {
		this.fileName = fileName;
		input = new InputStreamReader(new FileInputStream(fileName));
		System.out.println("Original encoding: " + input.getEncoding());
	}
	
	public MifFileParser(File input) throws Exception {
		this.fileName = input.getName();
		this.input = new InputStreamReader(new FileInputStream(input));
		System.out.println("Original encoding: " + this.input.getEncoding());
	}
	
	/*public Node load_old() throws Exception {
	 input = new FileReader(fileName);
	 int read;
	 char token;
	 int mode = NO_MODE;
	 boolean data = false;
	 String current = "";
	 while ((read = input.read()) > -1) {
	 token = (char)(read);
	 if ((!data)&(mode != COMMENT_MODE)) {
	 if (token == '#') mode = COMMENT_MODE;
	 else if (token == '`') data = true;
	 else if (token == '<') mode = ELEMENT_MODE;
	 
	 // terminations				
	  else if ((mode != STRING_MODE)&(token == ' ')) {
	  // set to attribute mode if prev was element
	   if (mode == ELEMENT_MODE) {
	   mode = ATTRIBUTE_MODE;
	   }
	   } else if (token == '>') {
	   
	   } else if ((mode == STRING_MODE)&(token == '\'')) {
	   } else if ((mode == COMMENT_MODE)&(token == '\n')) {
	   } else current += token;
	   }
	   
	   // string mode (`), (overrides all, terminations: ')
	    // comment mode (#), (overrides all, terminations \n)
	     // element mode (<), (terminations: Ox32, >)
	      // attribute mode ( ), (terminations: Ox32, >)
	       }
	       return null;
	       }
	       */
	public Vector parse() throws Exception {
		Vector output = new Vector();
		Vector attribs = new Vector();		
		int read;
		int mode = NO_MODE;		
		char token;
		String current = "";
		boolean cData = false;
		while ((read = input.read()) > -1) {
			token = (char)(read);
			if (cData) {
				if (token == '\'') {
					cData = false;
					output.add(current);
					current = "";
				} else {
					current += token;
				}
			} else {
				if (token == '\n') mode = NO_MODE; // för inset
				
				if (mode == NO_MODE & token == '&') mode = COMMENT_MODE; // sortera bort inset
				else if (mode == NO_MODE & token == '=') mode = COMMENT_MODE; // sortera bort inset
				
				else if (token == '\u0023') mode = COMMENT_MODE; //#
				else if (mode == COMMENT_MODE) {
					if (token == '\n') mode = NO_MODE;
				} else if (token == '`') cData = true;
				else if (token == '<') {
					mode = ELEMENT_MODE;
					Vector tmp = parse();
					output.add(new Node(tmp));
				}
				
				// terminations
				else if ((token == ' ')|(token == '>')) {
					current = current.replace('\n', ' ');
					String tmp = current.trim();
					if (!tmp.equals("")) {
						output.add(tmp);
					}
					current = "";
					if (token == '>') {
						if (mode == ELEMENT_MODE) {
							//System.out.println(output.toString());
							return output;
						}
						else {
							//System.out.println(output.toString());
							return output;
						}
					}
				} else {
					current += token;
				}
				///if (mode == COMMENT_MODE) System.out.print(token);
			}
			// string mode (`), (overrides all, terminations: ')
			// comment mode (#), (overrides all, terminations \n)
			// element mode (<), (terminations: Ox32, >)
			// attribute mode ( ), (terminations: Ox32, >)
		}
		//System.out.println("Return!!!");
		//System.out.println(output.toString());
		return output;
	}
	
	public Node parse(String part) throws Exception {
		Vector tmp = parse();
		//tmp.insertElementAt(((Node)(tmp.elementAt(0))).getName(), 0);
		tmp.insertElementAt(part, 0);
		return (new Node(tmp));
	}
	
}

