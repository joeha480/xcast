
package mif.utils;
import java.io.PrintStream;
import java.util.Vector;
import mif.converters.MifUnescape;

/**
 * beskrivning
 * 
 * @author  Joel Hakansson, TPB
 * @version 2005-03-31
 * @since 1.0
 */
public class Node {
	public final static int TYPE_NODE = 0;
	public final static int TYPE_LEAF = 1;
	public final static String[][] XML_ESCAPES = new String[][] {
			// XML Escape
			{"&", "&amp;"},
			{">", "&gt;"},
			{"<", "&lt;"},
			{"\"", "&quot;"}
	};
	
	private Vector children;
	private String name;
	private int type;
	private int position = -1;
	
	public Node(String name, Node[] children) {
		initNode(name, children);
	}
	
	public Node(String name, String[] children) {
		initNode(name, children);
	}
	
	public Node(Vector v) {
		if (v.size()<2) {
			//System.out.println("The vector should have at least two elements:");
			//System.out.println("  A name and a value.");
			if (v.size()==1) {
				//System.out.println("Adding a default value.");
				v.add("*** default ***");
			}
			else System.exit(1); 
		}
		String name = (String)(v.remove(0));
		//System.out.println(name);
		//System.out.println(((Node[])(v.toArray())).getClass());
		try {
			Object[] tmp = v.toArray();
			boolean isNode = v.elementAt(0).getClass()==Class.forName("mif.utils.Node");
			if (isNode) {
				Node[] nodeArray = new Node[tmp.length];
				//System.out.println("begin");
				for (int i=0;i<tmp.length;i++) {
					try {
					  nodeArray[i] = (Node)(tmp[i]);
					  //System.out.println("Node:" + nodeArray[i].getName());
					} catch (Exception e) {
					  //System.err.println("Error: " + (String)(tmp[i]));
					}
				}
				//System.out.println("end");
				initNode(name, nodeArray);
			} else {
				String[] stringArray = new String[tmp.length];
				for (int i=0;i<tmp.length;i++) {
					stringArray[i] = (String)(tmp[i]);
					//System.out.println("Attrib:" + stringArray[i]); 
				}				
				initNode(name, stringArray);
			}
		} catch (Exception e1) {
			System.err.println("*** Error ***");
			e1.printStackTrace();
			System.exit(1);
		}
	}
	
	private void initNode(String name, Node[] children) {
		this.type = TYPE_NODE;
		this.name = name;
		this.children = new Vector(children.length);
		for (int i = 0; i < children.length; i++) {
			this.children.add(children[i]);
		}
	}
	
	public void initNode(String name, String[] children) {
		this.type = TYPE_LEAF;
		this.name = name;
		this.children = new Vector(children.length);
		for (int i = 0; i < children.length; i++) {
			this.children.add(children[i]);
		}
	}
	
	public int getType() {
		return type;
	}
	
	public String getName() {
		return name;
	}
	
	public Node getFirstChild() {
		if (getType() == TYPE_NODE) return ((Node)(children.elementAt(0)));
		else return null;
	}
	
	public String getFirstAttribute() {
		if (getType() == TYPE_LEAF) return ((String)(children.elementAt(0)));
		else return null;
	}
	public boolean hasAttribute(String attrib) {
		if (type == TYPE_LEAF) {
		  for (int i = 0; i < children.size(); i++) {
			String test = ((String)(children.elementAt(i)));
			//System.out.println(test);
			if (attrib.equals(test)) return true;
		  }
		}
		return false;
	}
	
	public void reset() {
		position = -1;
	}
	
	public Node getNextChild() {
		position ++;
		if (position >= children.size()) return null;
		if (getType() == TYPE_NODE) {
			return ((Node)(children.elementAt(position)));		
		}
		return null;
	}
	
	public Node getAllChildren(String name) {
		Vector output = new Vector();
		output.add(name + 's');
		Object[] tmp = children.toArray();
		for (int i=0;i<tmp.length;i++) {
			Node test = ((Node)(tmp[i]));
			if (test.getName().equals(name)) {
				//System.out.println(test.getName());
				output.add(test);
				//System.out.println(test.getName());
			}
		}
		if (output.size()==0) return null;
		else return (new Node(output));
	}
	
	public Node getFirstChild(String name) {
		Object[] tmp = children.toArray();
		for (int i = 0; i < tmp.length; i++) {
			if (((Node)(tmp[i])).getName().equals(name)) {
				return ((Node)(tmp[i]));
			}
		}
		return null;
	}
	
	public Node getNextChild(String name) {
		Object[] tmp = children.toArray();
		while (position < tmp.length - 1) {
			position++;
			if (((Node)(tmp[position])).getName().equals(name)) {
				return ((Node)(tmp[position]));
			}
		}
		return null;
	}
	
	private Node[] vectorToNode(Vector input)
	{
		Object[] tmp = input.toArray();
		Node[] output = new Node[tmp.length]; 
		for (int i=0;i<tmp.length;i++) {
			output[i] = (Node)(tmp[i]); 
		}
		return output;
	}

	/*
	public Node[] getAllChildren(String element) {
		Object[] tmp = children.toArray();
		Vector output = new Vector();
		for (int i = 0; i < tmp.length; i++) {
			Node test = ((Node)(tmp[i])); 
			if (test.getType() == TYPE_NODE) {
				if ((test.getName()).equals(element)) {
					System.out.println("name: " + test.getName());
					output.add(test);
				} else {
					output.add(test.getAllChildren(element));
				}
			}
		}
		System.out.println("here: "+output.toString());
		if (output.size()==0) return (new Node[]{new Node("def", new String[]{"def"})});
		return (vectorToNode(output));
	}
	*/
	/*
	private int findFirst(String element, String attribute, int rlength, int pid) {
		int res = 0;
		int eid = 0;
		rlength *= children.size();
		for (int i = 0; i < children.size(); i++) {
			Node test = ((Node)(children.elementAt(i)));
			eid = rlength * (i + 1) + pid;
			System.out.println(eid);
			if (test.getName().equals(element) & test.getType() == TYPE_LEAF) {
				// och attribut!!!
				System.out.println("here");
				return eid;
			} else if (test.getType() == TYPE_NODE){
				res = test.findFirst(element, attribute, rlength, eid);
				if (res!=0) return res;
			}
		}
		return res;
	}
	
	public int findFirst(String element, String attribute) {
		return findFirst(element, attribute, 1, 0);
	}
	*/
	
	public Vector findFirst(String element, String attribute) {
		Vector res = new Vector();
		for (int i = 0; i < children.size(); i++) {
			Node test = ((Node)(children.elementAt(i)));
			//System.out.println("TESTAR:");
			//test.printAll();
			if (test.getType() == TYPE_LEAF) {
				if (test.getName().equals(element) & test.hasAttribute(attribute)) {
					res.add(new Integer(i+1));
					//System.out.println(i+1);
					return res;
				}
			} else if (test.getType() == TYPE_NODE){
				Vector tmp = test.findFirst(element, attribute);
				res.addAll(tmp);
				//System.out.println("LE:"+res.lastElement());
				if (((Integer)(res.lastElement())).intValue()>0) {
					res.add(new Integer(i+1));
					return res;
				}
				else res.removeElementAt(res.size()-1);
			}
		}
		res.add(new Integer(-children.size()));
		return res;
	}
	
	public Node getSubset(Vector subset) {
		if (subset.size()>0) {
			int childIndex = ((Integer)(subset.remove(subset.size()-1))).intValue() - 1;
			Node tmp = (Node)(children.elementAt(childIndex));
			return tmp.getSubset(subset);
		}
		// else
		return this;
	}
	/*
	public Node getSubsetRange(Vector from, Vector to) {
		Vector tmp = new Vector();
		tmp.add("Subset");
		
		return (new Node(tmp));
	}
	*/
	public void setNode(Vector pos, String name, String[] attributes) {
		initNode(name, attributes);
	}
	
	private void printAll(int level) {
		Object[] tmp = children.toArray(); 
		level++;
		System.out.print("<" + getName());
		if (getType() == TYPE_NODE) {
			System.out.println();
			for (int i = 0; i < tmp.length; i++) {
				for (int j = 0; j < level; j++) System.out.print(" ");
				((Node)(tmp[i])).printAll(level);
			}
		} else if (getType() == TYPE_LEAF) {
			for (int i = 0; i < tmp.length; i++) {
				if (((String)(tmp[i])).indexOf(' ')>-1)
					System.out.print(" `" + ((String)(tmp[i])) + '\'');
				else System.out.print(" " + ((String)(tmp[i])));
			}		
		}
		level--;
		if (getType() == TYPE_NODE) {
			for (int j = 0; j < level; j++) System.out.print(" ");				
			System.out.println("> # end of " + getName());
		} else {
			System.out.println(">");
		}
	}
	
	private String escapeXML(String input) {
		String output = input;
		for (int i=0; i<XML_ESCAPES.length; i++){
			output = output.replace(XML_ESCAPES[i][0], XML_ESCAPES[i][1]);
		}
		return output;
	}
	
	public void printAll() {
		printAll(0);
	}
	
	private void printAllToXML(int level, PrintStream ps) {
		Object[] tmp = children.toArray(); 
		level++;
		
		ps.print('<' + getName());
		if (getType() == TYPE_NODE) {
			if (getName()=="MIFXML") ps.print(" version=\"1.0\" xmlns:m=\"http://www.w3.org/1998/Math/MathML\"");
			ps.print('>');
			for (int i = 0; i < tmp.length; i++) {
				((Node)(tmp[i])).printAllToXML(level, ps);
			}
		} else if (getType() == TYPE_LEAF) {
			for (int i = 0; i < tmp.length; i++) {
				if (getName().equals("MathFullForm")) {
					// tolka!!! 
					//ps.print(" attr" + (i+1) + "=\"" + ((String)(tmp[i])).replaceAll("&","&amp;").replaceAll(">","&gt;").replaceAll("<","&lt;").replaceAll("\"", "&quot;") + '"');
					//->System.out.println("MATH: " + parseMath((String)(tmp[i])));
					//ps.print("><math xmlns=\"http://www.w3.org/1998/Math/MathML\">");
					//->ps.print(bulkReplace(parseMath((String)(tmp[i])), map));
					//ps.print("</math>");
					
					ps.print(">");
			//		ps.print(new MifEquationConverter((String)(tmp[i]))).toMathML());
					ps.print("</MathFullForm>");
				} else {
					//ps.print(" attr" + (i+1) + "=\"" + ((String)(tmp[i])).replaceAll("&","&amp;").replaceAll(">","&gt;").replaceAll("<","&lt;").replaceAll("\"", "&quot;") + '"');
					ps.print(" attr" + (i+1) + "=\"" + 
							escapeXML(MifUnescape.unescape((String)(tmp[i]), 
									MifUnescape.WINDOWS_STANDARD)) + '"');
				}

			}
		}
		level--;
		if (getType() == TYPE_NODE) {				
			ps.print("</" + getName() + '>');
		} else {
			if (!getName().equals("MathFullForm")) ps.print("/>");
		}
		ps.flush();
	}
	
	/**
	 * Converts the contents to XML and prints it to the specified 
	 * PrintStream.
	 * 
	 * @param ps  the PrintStream to which the output is written
	 */	
	public void printAllToXML(PrintStream ps) {
		printAllToXML(0, ps);
	}

}
