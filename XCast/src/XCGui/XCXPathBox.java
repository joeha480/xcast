
package XCGui;

import java.awt.BorderLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import XCCore.XCBridge;
import XCCore.XCUtils;
import XCGui.AutoComplete.AutoCompleteTextField;
import XCGui.AutoComplete.DefaultDictionary;

/**
 * The xpath frame.
 * 
 * @author  Joel Hakansson, TPB
 * @version 2005-08-23
 * @since 1.0
 */
public class XCXPathBox extends JDialog {
	private static final int CELL_WIDTH = 336;
	private static final int CELL_HEIGHT = 20;
	private JFrame frame;
	private XCDisplayManager display;
	private JPanel resPanel;
	//private JDialog xbox;
	private DefaultDictionary dictionary;
	private AutoCompleteTextField inputText;
	private StringBuffer sb;
	private XCBridge bridge;
	private String query;
	private JList jl2;
	private MouseListener ml;
	private KeyListener kl;
	
	/**
	 * Default constructionr.
	 * @param frame
	 * @param display
	 * @param dictionary
	 */
	public XCXPathBox(XCBridge bridge, JFrame frame, XCDisplayManager display, DefaultDictionary dictionary) {
		super(frame, "XPath Box", false); // ;)
		this.bridge = bridge;
		this.frame = frame;
		this.display = display;
		this.dictionary = dictionary;
		//xbox = new JDialog(frame, "XPath Box", false); // ;)
		init();
	}
	
	private void init() {
		try {
    	//xbox.setLayout(new BorderLayout());
		setLayout(new BorderLayout());
    	//xbox.setSize(400, 300);
		setSize(400, 300);
    	//xbox.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
		setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
    	JPanel expPanel = new JPanel();
    	resPanel = new JPanel();
    	inputText = new AutoCompleteTextField(dictionary);
    	inputText.setColumns(32);
    	JList jt = new JList(new String[]{" "});
    	jt.setFixedCellWidth(CELL_WIDTH);
    	//jt.setFixedCellHeight(CELL_HEIGHT);
    	resPanel.add(new JScrollPane(jt), BorderLayout.CENTER);
    	expPanel.add(inputText);
    	inputText.addKeyListener(new KeyAdapter(){
    		public void keyPressed(KeyEvent ke) {
    			if (ke.getKeyCode()==KeyEvent.VK_ENTER) {
    	        	updateExp();
    			}
    		}
    	});
    	query = "";
    	jl2 = null;
    	//xbox.add(expPanel, BorderLayout.NORTH);
    	add(expPanel, BorderLayout.NORTH);
    	//xbox.add(resPanel, BorderLayout.CENTER);
    	add(resPanel, BorderLayout.CENTER);
    	//xbox.pack();
    	pack();
    	//xbox.setLocationRelativeTo(null);
    	setLocationRelativeTo(null);
	} catch (Exception e) { e.printStackTrace(); }
	}
	
	public void setDisplayManager(XCDisplayManager display) {
		if (display!=null) this.display = display; 
	}

	public void setQuery(String text) {    	
		inputText.setText(text);
		updateExp();
	}
	
	public String getQuery() {
		return query;
	}
	
	public int getSelectedIndex() {
		return jl2.getSelectedIndex();
	}
	
	public void addMouseListener(MouseListener ml) {
		this.ml = ml;
	}
	
	public void addKeyListener(KeyListener kl) {
		this.kl = kl;
	}
	
	public void updateExp() {
		final String exp = inputText.getText(); //((JTextField)(ke.getSource())).getText();
		if (exp.equals("")) return;
		query = exp;
		XPath xp = XPathFactory.newInstance().newXPath();
    	try {
    		NodeList res = (NodeList)(xp.evaluate(exp, display.getDisplayDOM(), XPathConstants.NODESET));
    		int len = res.getLength();
    		Object[] tableData = new Object[len];
    		for (int i=0; i<len; i++) {
    			Node current = res.item(i);
    			sb = new StringBuffer((i+1)+":<");
    			sb.append(current.getNodeName());
    			sb.append(">");
    			sb.append(current.getTextContent());
    			if (sb.length()>50) {
    				sb.setLength(50);
    				sb.append("...");
    				
    			}
    			tableData[i] = sb.toString();
    		}
    		resPanel.removeAll();
    		final JList jt = new JList(tableData);
    		jl2 = jt;
    		jt.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    		jt.setFixedCellWidth(CELL_WIDTH);
    		//jt.setFixedCellHeight(CELL_HEIGHT);
    		jt.addMouseListener(ml);
    		jt.addKeyListener(kl);
    		jt.addListSelectionListener(new ListSelectionListener(){
    			public void valueChanged(ListSelectionEvent ev) {
    				int index = jt.getSelectedIndex()+1;
    				display.getActiveDisplay().selectXPath('('+exp+")["+ index +']');
    			}
    		});
    		resPanel.add(new JScrollPane(jt), BorderLayout.CENTER);
    		//xbox.pack();
    		pack();
    	} catch (Exception e) {
    		XCUtils.handleError(e, frame);
    	}
	}
	

	
	/*public void setVisible(boolean visible) {
    	xbox.setVisible(visible);		
	}
	
	public boolean isVisible() {
		return xbox.isVisible();
	}*/
}
