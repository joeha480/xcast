
package utils;

import java.awt.Color;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Properties;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import XCCore.XCDocument;
import XCCore.XCUtils;
import XCGui.AutoComplete.AutoCompleteTextField;
import XCGui.AutoComplete.DefaultDictionary;

/**
 * ... beskrivning ...
 * 
 * @author  Joel Hakansson, TPB
 * @version 2005-08-26
 * @since 1.0
 */
public class ListCollection extends JPanel implements ActionListener {

	private JButton elementButton;
	private JComboBox elementSelect;
	private JCheckBox elementCheck;
	private JPanel xpathPanel;
	
	private String element;
	private String[] select;
	private int counter;
	private DefaultDictionary dd; 
	
	public void addXPathBox(String msg, Object obj, String attribs, Boolean selected) {
		counter++;
		JPanel sub = new JPanel();

		AutoCompleteTextField tf = new AutoCompleteTextField(dd);
		KeyAdapter ka = new KeyAdapter(){
			public void keyPressed(KeyEvent ke) {
				if (ke.getKeyCode()==KeyEvent.VK_BACK_SPACE) {
					boolean doremove = false;
					Object source = ke.getSource();
					if (source instanceof JTextField) {
						JTextField tf = (JTextField)(ke.getSource());
						if (tf.getText().equals("")) {
							doremove = true;
						}
					} else if (source instanceof JComboBox) {
						doremove = true;
					}
					if (doremove) {
						counter--;
						Container jp = ((JComponent)(source)).getParent();
						Container jpp = jp.getParent();
						jpp.remove(jp);
						jpp.getParent().getParent().validate();
					}
				}
				else if (ke.getKeyCode()==KeyEvent.VK_ENTER) {
					
					addXPathBox(null, null, null, false);
				}
			}
		};
		if (msg!=null) tf.setText(msg);
		tf.addKeyListener(ka);
		sub.setLayout(new BoxLayout(sub, BoxLayout.X_AXIS));
		sub.add(tf);
		JComboBox jcb = new JComboBox(select);
		jcb.addKeyListener(ka);
		sub.add(jcb);
		if (obj!=null && !obj.equals("")) jcb.setSelectedItem(obj);
		JCheckBox jch = new JCheckBox();
		if (selected!=null) jch.setSelected(selected);
		else jch.setSelected(false);
		jch.addActionListener(this);
		if (attribs!=null) jch.setToolTipText(attribs);
		else jch.setToolTipText("");
		sub.add(jch);
		
		xpathPanel.add(sub);
		tf.requestFocus();
		xpathPanel.getParent().getParent().validate();
	}
	
	public ListCollection(String text, int count, ActionListener alist, final String[] select, DefaultDictionary dd) {
		super();
		GridBagLayout gridbag = new GridBagLayout();
        setLayout(gridbag);
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        
		elementButton = new JButton();
		elementSelect = new JComboBox(select);
		elementCheck = new JCheckBox();
		this.select = select;
		counter = 0;
		setElement(text);
		xpathPanel = new JPanel();
		xpathPanel.setLayout(new BoxLayout(xpathPanel, BoxLayout.Y_AXIS));
		xpathPanel.setBorder(new EmptyBorder(2,20,6,0));
		//xpathPanel.setBackground(new Color(0, 80, 140));
		xpathPanel.addMouseListener(new MouseAdapter(){
			public void mouseClicked(MouseEvent me) {
				addXPathBox(null, null, null, false);
			}
		});
		
		elementButton.setToolTipText(Integer.toString(count));
		c.weightx = 1.0; c.gridwidth = GridBagConstraints.BOTH; gridbag.setConstraints(elementButton, c);
		elementButton.addActionListener(alist);
		add(elementButton);
		
		elementSelect.setToolTipText(Integer.toString(count));
		c.gridwidth = GridBagConstraints.RELATIVE; c.weightx = 0; gridbag.setConstraints(elementSelect, c);
		elementSelect.addActionListener(this);
		add(elementSelect);

		elementCheck.setToolTipText("");
		c.gridwidth = GridBagConstraints.REMAINDER; c.weightx = 0; gridbag.setConstraints(elementCheck, c);
		elementCheck.addActionListener(this);
		add(elementCheck);
		
		c.weightx = 1.0; c.gridwidth = GridBagConstraints.REMAINDER; gridbag.setConstraints(xpathPanel, c);
		add(xpathPanel);
		this.dd = dd;
	}
	
	private void editAttributes(JCheckBox box) {
		String msg = JOptionPane.showInputDialog(null, "Attribut", box.getToolTipText());
		if (msg!=null) {
			box.setToolTipText(msg);
			if (msg.equals("")) box.setSelected(false);
		}
		else elementCheck.setSelected(false);
	}
	
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		if (source instanceof JComboBox) {
			highlight(false);
		} else if (source instanceof JCheckBox) {
			JCheckBox cSource = (JCheckBox)(source); 
			if (cSource.isSelected()) editAttributes(cSource);
			else if ((e.getModifiers()&ActionEvent.CTRL_MASK)==ActionEvent.CTRL_MASK) {
				cSource.setSelected(true);
				editAttributes(cSource);
			}
		}
	}
	
	public String getButtonText() {
		return elementButton.getText();
	}
	
	public Object getComboBoxSelectedItem() {
		return elementSelect.getSelectedItem();
	}
	
	public void setComboBoxSelectedItem(Object obj) {
		elementSelect.setSelectedItem(obj);
	}
	
	public void setAttribute(String msg, Boolean selected) {
		elementCheck.setToolTipText(msg);
		elementCheck.setSelected(selected);		
	}
	
	/*
	public JCheckBox getCheckBox() {
		return elementCheck;
	}
	
	public JPanel getXPathPanel() {
		return xpathPanel;
	}*/
	
	public void highlight(boolean yes) {
		if (yes) { elementButton.setBackground(new Color(255,40,40)); }
		else { elementButton.setBackground(null); }
	}
	
	/**
	 * Adds the properties of this object to the list of properties pl
	 * @param pl
	 * @return returns the new properties list
	 */
	public Properties addProperties(Properties pl, boolean getAttrib) {
		int len = xpathPanel.getComponentCount();
		String tmp = getComboBoxSelectedItem().toString();
		if (getAttrib & elementCheck.isSelected()) tmp += ' ' + elementCheck.getToolTipText();
		pl.setProperty(getButtonText(), tmp);
		for (int i=0; i<len; i++) {
			JPanel jp = (JPanel)(xpathPanel.getComponent(i));
			JTextField tf = (JTextField)(jp.getComponent(0));
			JComboBox jcb = (JComboBox)(jp.getComponent(1));
			JCheckBox jch = (JCheckBox)(jp.getComponent(2));
			tmp = jcb.getSelectedItem().toString();
			if (getAttrib & jch.isSelected()) tmp += ' ' + jch.getToolTipText();
			if (!tf.getText().equals("")) {
				pl.setProperty(element+'['+tf.getText()+']', tmp);
			}
		}
		return pl;
	}
	

	
	/**
	 * Adds the preferences of this object to the list of preferences pl
	 * @param e_root
	 */
	public void addPrefs(XCDocument doc, Element e_root) {
		int len = xpathPanel.getComponentCount();
		Element e_element = XCUtils.addElement(e_root, "element");
		e_element.setAttribute("name", getButtonText());
		Element e_default = XCUtils.addElement(e_element, "default");
		setAction(getComboBoxSelectedItem().toString(), e_default, elementCheck);

		for (int i=0; i<len; i++) {
			JPanel jp = (JPanel)(xpathPanel.getComponent(i));
			JTextField tf = (JTextField)(jp.getComponent(0));
			JComboBox jcb = (JComboBox)(jp.getComponent(1));
			JCheckBox jch = (JCheckBox)(jp.getComponent(2));
			tf.setBackground((new JTextField()).getBackground());				
			if (!tf.getText().equals("")) {
				try {
					String xpathExp = "count(//"+getButtonText()+"["+tf.getText()+"])";
					int res = doc.evaluateNumericXPath(xpathExp);
					tf.setToolTipText(res+"");
					jcb.setToolTipText(res+"");
					System.out.println("XPATH " + xpathExp + ": " + res);
					Element e_condition = XCUtils.addElement(e_element, "condition");
					XCUtils.addTextNode(XCUtils.addElement(e_condition, "match"), tf.getText());
					setAction(jcb.getSelectedItem().toString(), e_condition, jch);
					if (res==0) tf.setBackground(new Color(255, 255, 0));
				} catch (Exception e) {
						tf.setBackground(new Color(255,0,0));
						e.printStackTrace(); 
				}
			} else {
				tf.setBackground(new Color(255,0,0));
			}
		}
		e_root.appendChild(e_element);
	}

	private void setAction(String action, Element el, JCheckBox attribs) {
		Element el2 = XCUtils.addElement(el, "action");
		if (action.equals("-bevara")) el2.setAttribute("value", "keep");
		else if (action.equals("-platta")) el2.setAttribute("value", "flatten");
		else if (action.endsWith("-radera")) el2.setAttribute("value", "delete");
		else {
			el2.setAttribute("value", "map");
			XCUtils.addTextNode(el2, action);
		}
		String attribText = attribs.getToolTipText();
		if (attribText!=""){
			Element e_attribs = XCUtils.addElement(el, "attribs");
			e_attribs.setAttribute("selected", attribs.isSelected()+"");
			XCUtils.addTextNode(e_attribs, attribText);
		}
	}
	
	public void setPrefs(Document utilityDOM) {

	}
	
	public void setElement(String text) {
		element = text;
		elementButton.setText(text);
	}
	
}
