package mif.converters;
import java.util.Vector;

/*
 * Created on 2004-dec-02
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

/**
 * @author JOELH
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class MifEquationConverter {
	private String exp;
	private static final String NS="m:";
	
	public MifEquationConverter(String exp) {
		this.exp = exp;
	}

	public String bulkReplace(String in, String[][] map)
	{
		for (int i=0; i<map.length; i++){
			in = in.replace(map[i][0], map[i][1]);
			//in = in.replaceAll(find[i],replace[i]);
		}
		int ind = in.indexOf("AAA");
		if (ind > 0) { System.out.println(in.codePointBefore(ind) + ' ' + in.codePointBefore(ind)); }
		return in;
	}
	
	private String parseElement(String opname, Vector param)
	{
		String[][] map = new String[][] {
				{"&", "&amp;"},
				{"\\>", "&gt;"},
				{"\\x81 ", "�"},
				{"\\x80 ", "�"},
				{"\\x85 ", "�"},
				{"\\x8c ", "�"},
				{"\\x8a ", "�"},
				{"\\x9a ", "�"},
				{"\\xd3 ", "&quot;"},
				{">", "&gt;"},
				{"<", "&lt;"},
				{"\"", "&quot;"}
				};
		String tmp = "";
		String op = "";
		String cs = "";
		String ent = "";
		String p1 = (String)(param.elementAt(0));
		String fo = "";
		int fp = p1.indexOf("*)");
		if (fp > 0) {
			    System.out.println("P1P:"+p1);
				fo = p1.substring(0,fp+2);
				p1 = p1.substring(fp+2);
				param.setElementAt(p1,0);
				System.out.println("FO:"+fo);
				System.out.println("P1A:"+p1);
				System.out.println("Param:"+(String)(param.elementAt(0)));
		}
		String fo2 = "";
		int fp2 = opname.indexOf("*)");
		if (fp2 > 0) {
			    System.out.println("2P1P:"+opname);
				fo2 = opname.substring(0,fp2+2);
				opname = opname.substring(fp2+2);
				System.out.println("2FO:"+fo2);
				System.out.println("2P1A:"+opname);
		}
		
		// String p2 = (String)(param.elementAt(1));
		int type=0;
		
		// Unary operators
		if (opname.equals("abs")) { type=1; op="abs"; } //1:1
		else if (opname.equals("acos")) { type=1; op="arccos"; } //1:1
		else if (opname.equals("acosh")) { type=1; op="arccosh"; } //1:1
		else if (opname.equals("acot")) { type=1; op="arccot"; } //1:1
		else if (opname.equals("acoth")) { type=1; op="arccoth"; } //1:1
		else if (opname.equals("acsc")) { type=1; op="arccsc"; } //1:1
		else if (opname.equals("acsch")) { type=1; op="arccsch"; } //1:1
		//else if (opname.equals("angle")) { type=1; op="?"; }
		//else if (opname.equals("arg")) { type=1; op="?"; }
		else if (opname.equals("asec")) { type=1; op="arcsec"; } //1:1
		else if (opname.equals("asech")) { type=1; op="arcsech"; } //1:1
		else if (opname.equals("asin")) { type=1; op="arcsin"; } //1:1
		else if (opname.equals("asinh")) { type=1; op="arcsinh"; } //1:1
		//else if (opname.equals("ast")) { type=1; op="?"; }
		else if (opname.equals("atan")) { type=1; op="arctan"; } //1:1
		else if (opname.equals("atanh")) { type=1; op="arctanh"; } //1:1
		//else if (opname.equals("box")) { type=1; op="?"; }
//		else if (opname.equals("box2")) { type=1; op="?"; }
//		else if (opname.equals("boxdot")) { type=1; op="?"; }
//		else if (opname.equals("bra")) { type=1; op="?"; }
		else if (opname.equals("ceil")) { type=1; op="ceiling"; } //1:1
//		else if (opname.equals("change")) { type=1; op="?"; }
		else if (opname.equals("cos")) { type=1; op="cos"; } //1:1
		else if (opname.equals("cosh")) { type=1; op="cosh"; } //1:1
		else if (opname.equals("cot")) { type=1; op="cot"; } //1:1
		else if (opname.equals("coth")) { type=1; op="coth"; } //1:1
		else if (opname.equals("csc")) { type=1; op="csc"; } //1:1
		else if (opname.equals("csch")) { type=1; op="csch"; } //1:1
		else if (opname.equals("curl")) { type=1; op="curl"; } //1:1
//		else if (opname.equals("dagger")) { type=1; op="?"; }
//		else if (opname.equals("dangle")) { type=1; op="?"; }		
//		else if (opname.equals("diff")) { type=1; op="?"; } //1:N
		
		else if (opname.equals("id")) return "<"+NS+"mfenced>" + p1 + "</"+NS+"mfenced>";
		else if (opname.equals("uequal")) return "<"+NS+"apply><"+NS+"eq/><"+NS+"none/>" + p1 + "</"+NS+"apply>";
		// misc
		else if (opname.equals("prompt")) return "<none/>";
		else if (opname.equals("num")) {
			// Infinity & NAN ska till <notanumber/> <infinity/>
			String p2 = (String)(param.elementAt(1));
			p2 = p2.substring(1,p2.length()-1);
			if (p2.equals("Infinity")) return "<infinity/>";
			else if (p2.equals("NaN")) return "<notanumber/>";
			else return "<"+NS+"cn>" + p2 + "</"+NS+"cn>";
		}
		else if (opname.equals("char")) return "<"+NS+"ci>" + p1 + "</"+NS+"ci>";
		else if (opname.equals("string")) return "<"+NS+"mi>" + p1.substring(1,p1.length()-1) + "</"+NS+"mi>";
		else if (opname.equals("times")) { type=1; op="times";   }
		else if (opname.equals("cdot")) { type=1; op="times"; cs="cdot";  }
		else if (opname.equals("power")) { type=1; op="power";   }
		else if (opname.equals("equal")) { type=1; op="eq"; }
		else if (opname.equals("plus")) { type=1; op="plus"; }
		else if (opname.equals("minus")) { type=1; op="minus"; }
		else if (opname.equals("comma")) { type=10; op=","; }
		else if (opname.equals("pm")) { type=2; op="csymbol"; ent="&PlusMinus;"; }
		else if (opname.equals("over")) { type=1; op="divide"; }
		else if (opname.equals("Rightarrow")) {
			if (param.size()<=2) {
				type=1;
				op="implies";
			}
		}
		else if (opname.equals("rightarrow")) {
			if (param.size()<=2) {
				type=1;
				op="tendsto";
			}
		}		
		else if (opname.equals("approx")) {
			if (param.size()<=2) {
				type=1;
				op="approx";
			}
		}
		//return "<"+NS+"mfrac>" + (String)(param.elementAt(0)) + " " + (String)(param.elementAt(1)) + "</"+NS+"mfrac>";
		
		// optional operand
		else if (opname.equals("sqrt")) {
			op = "root";
			tmp = "<"+NS+"apply>";
			tmp += "<"+NS+op+"/>";
			if (param.size()>1) {
				tmp += "<"+NS+"degree>"+(String)(param.elementAt(1))+"</"+NS+"degree>";
			}
			tmp += p1;
			tmp += "</"+NS+"apply>";
			return tmp;
		}
		// Index, chem & tensor
		else if (opname.equals("indexes")) {
			// Alla exempel i Miff reference �r testade.
			tmp = "<"+NS+"mmultiscripts>";
			int sup = Integer.parseInt((String)(param.elementAt(0)));
			int sub = Integer.parseInt((String)(param.elementAt(1)));
			int supI=0;
			int subI=0;
			tmp += (String)(param.elementAt(2));
			for (int i=0;i<Math.max(sup,sub);i++) {
				if (subI<sub) {
					tmp += (String)(param.elementAt(3+subI));
					subI++;
				} else tmp += "<"+NS+"none/>";					
				if (supI<sup) {
					tmp += (String)(param.elementAt(3+sub+supI));
					supI++;
				} else tmp += "<"+NS+"none/>";
			}
			tmp += "</"+NS+"mmultiscripts>";
			return tmp;			
		}
		else if (opname.equals("chem")) {
			// Alla exempel i Miff reference �r testade.
			tmp = "<"+NS+"mmultiscripts>";
			int psub = Integer.parseInt((String)(param.elementAt(0)));
			int sub = Integer.parseInt((String)(param.elementAt(1)));
			int psup = Integer.parseInt((String)(param.elementAt(2)));
			int sup = Integer.parseInt((String)(param.elementAt(3)));
			tmp += (String)(param.elementAt(4));
			if (sub==1) tmp += (String)(param.elementAt(5+psup+sup+psub));
			else tmp += "<"+NS+"none/>";
			if (sup==1) tmp += (String)(param.elementAt(5+psup));
			else tmp += "<"+NS+"none/>";
			tmp += "<"+NS+"mprescripts/>";
			if (psub==1) tmp += (String)(param.elementAt(5+sup+psup));
			else tmp += "<"+NS+"none/>";
			if (psup==1) tmp += (String)(param.elementAt(5));
			else tmp += "<"+NS+"none/>";			
			tmp += "</"+NS+"mmultiscripts>";
			return tmp;				
		}
		else if (opname.equals("tensor")) {
			// Alla exempel i Miff reference �r testade.
			tmp = "<"+NS+"mmultiscripts>";
			int pos = Integer.parseInt((String)(param.elementAt(0)));
			int len = param.size()-2;
			tmp += (String)(param.elementAt(1));
			for (int i=0;i<len;i++) {
			  if (((pos)&(int)(Math.pow(2,i)))>0) {
			  	tmp += "<"+NS+"none/>";
			  	tmp += (String)(param.elementAt(i+2));
			  } else {
			  	tmp += (String)(param.elementAt(i+2));
			  	tmp += "<"+NS+"none/>";
			  }
			}
			tmp += "</"+NS+"mmultiscripts>";
			return tmp;
		}
		else type=100;
		if (type>=0&type<10) {
			if (type<=2) tmp = "<"+NS+"apply>";
			if (type==1) { 
				tmp += "<"+NS+op;
			    if (!cs.equals("")) {tmp += " class=\""+cs+"\"";
			    System.out.println(cs);}
			    tmp += "/>";
			}
			else if (type==2) tmp += "<"+NS+op+">"+ent+"</"+NS+op+">";
			for (int i=0;i<param.size();i++) {
				tmp += (String)(param.elementAt(i));
			}
			if (type<=2) tmp += "</"+NS+"apply>";
		} else if (type<20) {
			tmp += "<"+NS+"mrow>";
			for (int i=0;i<param.size();i++) {
				tmp += (String)(param.elementAt(i));
				if (i<param.size()-1) tmp += "<"+NS+"mo>"+op+"</"+NS+"mo>";
			}
			tmp += "</"+NS+"mrow>";
		} else {
			for (int i=0;i<param.size();i++) {
				tmp += "<NOTMAPPED attr1=\"" + opname + "\">" + bulkReplace((String)(param.elementAt(i)),map)+"</NOTMAPPED>";
			}
		}
		return tmp;	
	}
	
	public String toMathML(String inp)
	{
		int opIndex = inp.indexOf('[');
		String opname = inp.substring(0,opIndex);
		
		
		int open = 0;
		int last = opIndex+1;
		Vector param = new Vector();
		if (opIndex < 0) return "Syntax ERROR! (ParseMath)";
		for (int i=opIndex+1;i<inp.length();i++)
		{
			switch (inp.charAt(i)) {
			    case ']':
			    	open--;
			    	if (open!=-1) break;
				case ',':
					if (open<1) {
						String tmp = inp.substring(last,i);
						if (tmp.indexOf('[')!=-1) { //icke-atom�rt argument
							tmp = toMathML(tmp);
						}
						param.add(tmp);
						last = i+1;
					}
					break;
				case '[':open++; break;
			}
		}
		//System.out.println(opname);
		return parseElement(opname, param);	
	}
	
	public String toMathML() {
		exp = exp.replace("\n","");
		exp = exp.replace("\r","");
		return "<"+NS+"math>" + toMathML(exp) + "</"+NS+"math>";
	}
}
