
/**
 * @author JOELH
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package mif.converters;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Stack;

import mif.utils.Node;

public class MifFileParser {
	final static int BUFFSIZE = 2048;
	final static int NO_MODE = 0;
	final static int COMMENT_MODE = 1;
	final static int ELEMENT_MODE = 2;
	final static int ATTRIBUTE_MODE = 3;	
	final static int STRING_MODE = 4;
	
	private String fileName;
	private BufferedReader input;
	private Node miffTree;
	
	public MifFileParser(String fileName) throws Exception {
		this.fileName = fileName;
		//this.input = new BufferedReader(new InputStreamReader(new FileInputStream(fileName)));
		this.input = new BufferedReader(new FileReader(fileName));
		//System.out.println("Original encoding: " + input.getEncoding());
	}
	
	public MifFileParser(File input) throws Exception {
		this.fileName = input.getName();
		//this.input = new BufferedReader(new InputStreamReader(new FileInputStream(input)));
		this.input = new BufferedReader(new FileReader(input));
		//System.out.println("Original encoding: " + this.input.getEncoding());
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

	public ArrayList parse() throws Exception {
		ArrayList output = new ArrayList();
		ArrayList attribs = new ArrayList();		
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
					ArrayList tmp = parse();
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
	
	/**
	 * Parse the file to a node-tree
	 * @param part
	 * @return 
	 * @throws Exception
	 */
	public Node parse(String part) throws Exception {
		ArrayList tmp = parse();
		//tmp.insertElementAt(((Node)(tmp.elementAt(0))).getName(), 0);
		tmp.add(0, part);
		//tmp.insertElementAt(part, 0);
		return (new Node(tmp));
	}

	private static void outputEscaping(int charRead, BufferedReader input, PrintWriter output) throws Exception {
		switch (charRead) {
		  case '\\':
		  	// eventuellt escapesekvens, läs nästa tecken
		  	if ((charRead = input.read()) > -1) {
		  		switch (charRead) {
		  			case '\\': output.write('\\'); break;
		  			case 't': output.write("&#x0009;"); break;
		  			case '>': output.write('>'); break;
		  			case 'q': output.write('\''); break;
		  			case 'Q': output.write('`'); break;
		  			case 'x':
		  				StringBuffer x = new StringBuffer();
		  				x.append((char)(input.read()));
		  				x.append((char)(input.read()));
		  				if (input.read() != ' ') System.err.println("Error in MIF-file!");
		  				output.write(MifUnescape.MAP_WINDOWS_STANDARD[Integer.parseInt(x.toString(), 16)]);
		  				break;
		  			default: 
		  				output.write('\\');
		  				output.write(charRead);
		  		}
		  	}
		  	break;
		  case '<' : output.write("&lt;"); break;
		  case '&': output.write("&amp;"); break;
		  case '"': output.write("&quot;"); break;
		  default : output.write(charRead);
		}
	}
	
	public static void parse(File inputFile, File outputFile) throws Exception {
		//Ny fin metod
		BufferedReader input = new BufferedReader(new FileReader(inputFile));
		OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(outputFile), "UTF8");
		PrintWriter output = new PrintWriter(new BufferedWriter(osw));
		//PrintStream output = System.out;
		boolean openTag = false;
		int attributeCount = 0;
		int charRead;
		Stack openTags = new Stack();
		StringBuffer element;
		output.print("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		output.print("<!-- MIFXML file-to-file-parser -->");
		output.print("<MIFXML version=\"2.0\">");
		//int count = 0;
		loop:while ((charRead = input.read()) > -1) {
			//count++;
			//if (count>1000) break loop;
			switch (charRead) {
				case '#':
					if (openTag) {
						if (attributeCount>0) {
							openTags.pop();
							output.print("/>");
						}
						else output.print(">");	
					}
					openTag = false;
					attributeCount = 0;
					output.write("<!-- ");
					output.write(charRead);
					innerloop:while ((charRead = input.read()) > -1) {
						if (charRead == '\n' || charRead == '\r') break innerloop;
						output.write(charRead);
					}
					output.print(" -->");
					break;
				case '<' :
					if (openTag) {
						if (attributeCount>0) {
							openTags.pop();
							output.print("/>");
						}
						else output.print(">");				
					}
					openTag = true;
					attributeCount = 0;
					output.write('<');
					element = new StringBuffer();
					innerloop:while ((charRead = input.read()) > -1) {
						if (charRead == ' ') {
							openTags.push(element.toString());
							break innerloop;
						}
						if (charRead == '>') {
							output.print("/>");
							openTag = false;
							break innerloop;
						}
						element.append((char)(charRead));
						output.write(charRead);
					}					
					break;
				case '>' :
					String tmp = (String)(openTags.pop());
					if (attributeCount>0) {
						attributeCount = 0;
						output.print("/>");
					}
					else {
						if (openTag) output.write(">");
						output.print("</");
						output.print(tmp);
						output.print(">");
					}
					openTag = false;
					break;
				case '`' :
					attributeCount++;
					output.print(" attr");
					output.print(attributeCount);
					output.print("=\"");
					innerloop:while ((charRead = input.read()) > -1) {
						if (charRead == '\'') break innerloop;
						// output-escaping!!!
						outputEscaping(charRead, input, output);
					}
					output.write('"');
					break;
				case ' ' : case '\t' : case '\n' : case '\r' : break;
				case '=':
					String inset;
					innerloop:while ((inset = input.readLine()) != null) {
						if (inset.contains("=EndInset")) break innerloop;
					}
				default:
					attributeCount++;
					output.print(" attr");
					output.print(attributeCount);
					output.print("=\"");
					output.write(charRead);
					innerloop:while ((charRead = input.read()) > -1) {
						if (charRead == ' ') break innerloop;
						if (charRead == '>') break innerloop;
						outputEscaping(charRead, input, output);
					}
					if (charRead == '>') {
						attributeCount = 0;
						openTag = false;
						openTags.pop();
						output.print("\"/>");
					} else output.write('"');
					break;
			}
		}
		output.print("</MIFXML>");
		output.flush();
	}
	/**
	 * Parse the Mif-file into an XML-file.
	 * @param output
	 * @throws Exception
	 */
	public static void parseToFile(PrintStream output, File inputFile) throws Exception {
		BufferedReader input = new BufferedReader(new FileReader(inputFile));
		String str;
		ArrayList elements = new ArrayList();
		boolean cdata = false;
		int ac = 1;
		int lines = 1;
		output.println("<MIFXML version=\"1.1\" xmlns:m=\"http://www.w3.org/1998/Math/MathML\">");
		all:while ((str = input.readLine()) != null) {
			lines++;
			if (lines>200) break all;
			
			char strBegin = str.charAt(0);
			char strEnd = str.charAt(str.length()-1);
			boolean comment = false;
			
			String[] res = str.split(" ");
			process:for (int i=0; i<res.length; i++) {
				String current = res[i];
				System.out.println("|"+current+"|");
				if (current.equals("")) continue process;
				char begin = current.charAt(0);
				char end = current.charAt(current.length()-1);
				//System.out.println();
				//System.out.println("    "+begin);
				if (cdata) {
					output.print(" " + current);
					if ((new Character('\'').equals(new Character(current.charAt(current.length()-1))))) {
						cdata = false;
					}
					continue process;
				} else if (comment) {
					output.print(" " + current);
					continue process;
				}
				//System.out.println(begin + ":" + end);
				switch (begin) {
					case '<': // begining of element
						ac = 1;
						String value = current.substring(1,current.length());
						if (res.length==1) elements.add(value);
						output.print("<"+value);
						if (res.length == 1) output.println(">");
						break;
					case '>': 
						System.out.println("HÄ");
						int last = elements.size()-1;
						String element = (String)(elements.remove(last));
						output.println("</"+element+">");
						ac = 1;
						break; // end of element
					case '`': // beginging of cdata
						
						if (end=='\'') { // end of cdata?
							output.print(" at2tr"+ac+"=\""+current.substring(0,current.length()-1)+"\"");
							cdata = false;
						} else {
							cdata = false;
							output.print(" at3tr"+ac+"=\""+current.substring(0,current.length()-1));
						}
						break; 
					case '#':
						ac = 1;
						comment = true;
						output.print("<!-- " + current);
						break;
						//break process; // beginning of comment
					default : 
						output.print(" attr"+ac+"=\""+current.substring(0,current.length()-1)+"\"");
						if (end=='>') {
							output.print("/>");
						}
						ac++;
				}
			}
			if (comment) output.println(" -->");
			else output.println();
		}
		output.println("</MIFXML>");
	}
	
	public static void parseToFile(File inputFile, PrintStream output) throws Exception {
		BufferedReader input = new BufferedReader(new FileReader(inputFile));
		String current = "";
		int ac = -1;
		int c;
		int mode = NO_MODE;
		int previousMode = NO_MODE;
		ArrayList elements = new ArrayList();
		boolean cdata = false;
		boolean comment = false;
		boolean terminated = false;
		boolean collect = false;
		String currentElement = null;
		output.println("<MIFXML version=\"1.1\" xmlns:m=\"http://www.w3.org/1998/Math/MathML\">");
		int count = 0;
		loop:while ((c = input.read()) > -1) {
			//count++;
			//if (count>100000) break loop;
			char cc = (char)(c);
			if (cdata) {
				if (cc=='\'') {
					cdata = false;
				}
				else { 
					current += cc;
					continue loop;
				}
			} else if (comment) {
				if (cc=='\n'|cc=='\r') {
					comment = false;
					output.println(" -->");
				} else output.print(cc);
				continue loop;
			}
			switch1:switch (cc) {
				case '`': cdata = true; break;
				case '<':
					if (ac == 0) output.println(">");				
					mode = ELEMENT_MODE; break;
				case '\n': case '\r': case '\t': case ' ': case '>':
					if (mode==ELEMENT_MODE) {
						ac=0;
						elements.add(current);
						output.print("<"+current);
						current = "";
						mode = NO_MODE;
					} else if (mode==ATTRIBUTE_MODE) {
						ac++;
						output.print(" attr"+ac+"=\""+current+"\"");
						if (cc=='>') {
							output.println("/>");
							elements.remove(elements.size()-1);
						}
						current = "";
						mode = NO_MODE;
					} else {
						if (cc=='>') output.println("</"+elements.remove(elements.size()-1)+">");
						mode = NO_MODE;
					}
					break;
				case '#': comment = true; output.print("<!-- "); break;
				default:
					if (mode == NO_MODE) mode = ATTRIBUTE_MODE;
					current += cc;
			}
		}
		output.println("</MIFXML>");
	}	
}

