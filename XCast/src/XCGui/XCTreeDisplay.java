
package XCGui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.util.ArrayList;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTree;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.text.Position;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreePath;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Node;

import XCCore.XCBridge;
import XCCore.XCSettings;
import XCCore.XCUtils;

/**
 * This is the display that is used by default within the application. It uses
 * a JTree to display the information.
 * 
 * @author  Joel Hakansson, TPB
 * @version 2005-08-23
 * @since 1.0
 */
public class XCTreeDisplay extends XCDisplay implements TreeExpansionListener {
	private static final long serialVersionUID=300;
    private JTree tree;
    private JPanel treeWrapper;
    private JScrollPane treeScrollPane;
    private ArrayList expandedTreePathList;
    private XCSettings settings;

    /**
     * Default constructor.
     * @param settings
     */
	public XCTreeDisplay(XCBridge bridge) {
		super(bridge);
		this.settings = bridge.getSettings();
        treeWrapper = new JPanel();
        FlowLayout fl = new FlowLayout();
        fl.setAlignment(FlowLayout.LEFT);
        treeWrapper.setLayout(fl);
        treeScrollPane = XCUtils.setIncrement(settings.getScrollIncrement(), new JScrollPane(treeWrapper));
        treeScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        expandedTreePathList = new ArrayList();
        add(treeScrollPane, BorderLayout.CENTER);
	}
	
	public void loadDocument() {
		unloadDocument();
		resetWorkingTree();
	}

	public void unloadDocument() {
	}

	public void reloadDocument() {
		resetWorkingTree();
	}
	
	public void selectXPath(String exp) {
		// remove the treelistener here, because when selecting from xpath, the list can get very
		// long and heavy.
		tree.removeTreeExpansionListener(this);
		try {
			XPath xp = XPathFactory.newInstance().newXPath();
			Node result = (Node)(xp.evaluate(exp, ((XCNode)tree.getModel().getRoot()).getNode(), XPathConstants.NODE));
			if (result!=null) {
				ArrayList path = new ArrayList();
				Node curr = result;
				do {
					path.add(curr);
				} while ((curr = curr.getParentNode())!= null);
				ArrayList reversePath = new ArrayList();
				XCNode nd = (XCNode)(tree.getModel().getRoot());
				reversePath.add(nd);
				for (int i=path.size()-3; i>=0; i--) { 
					nd = nd.getChild((Node)(path.get(i)));
					reversePath.add(nd);
				}
				TreePath tp = new TreePath(reversePath.toArray());
				tree.setSelectionPath(tp);
				tree.expandPath(tp);
				tree.scrollPathToVisible(tp);
				center(tree, tree.getPathBounds(tp), true);
			}
		} catch (Exception e) { e.printStackTrace(); }
		tree.addTreeExpansionListener(this);
	}
	
	private static void center(JComponent c, Rectangle r, boolean withInsets)
	{
	    Rectangle visible = c.getVisibleRect();

	    visible.x = r.x - (visible.width - r.width) / 3;
	    visible.y = r.y - (visible.height - r.height) / 3;

	    Rectangle bounds = c.getBounds();
	    Insets i = withInsets ? new Insets(0, 0, 0, 0) : c.getInsets();
	    bounds.x = i.left;
	    bounds.y = i.top;
	    bounds.width -= i.left + i.right;
	    bounds.height -= i.top + i.bottom;

	    if (visible.x < bounds.x)
	        visible.x = bounds.x;

	    if (visible.x + visible.width > bounds.x + bounds.width)
	        visible.x = bounds.x + bounds.width - visible.width;

	    if (visible.y < bounds.y)
	        visible.y = bounds.y;

	    if (visible.y + visible.height > bounds.y + bounds.height)
	        visible.y = bounds.y + bounds.height - visible.height;

	    c.scrollRectToVisible(visible);
	}

	
	public void setDocumentPosition(String elementName, int instanceNo) {
		selectXPath("/descendant-or-self::"+elementName+'['+instanceNo+']');
	}
	
    private void setUpWorkingTree() {
    	treeWrapper.removeAll();
		XCTreeModel treeModel = new XCTreeModel(new XCNode(manager.getDisplayDOM().getDocumentElement()));
		tree = new JTree(treeModel);
		tree.setExpandsSelectedPaths(true);
		tree.setEditable(manager.allowsEditing());
		tree.setCellRenderer(new MyRenderer());
    	treeWrapper.add(tree);
    }
    
    private void resetWorkingTree() {
    	try {
    		setUpWorkingTree();
        	ArrayList oldList = expandedTreePathList;
            expandedTreePathList = new ArrayList();
        	tree.addTreeExpansionListener(this);
            for (int i=0; i<oldList.size(); i++) {
            	expandEqualPath((TreePath)(oldList.get(i)), tree);
            }    
        	treeWrapper.revalidate();
    	} catch (Exception e) { e.printStackTrace(); };
    }
    
    private void expandEqualPath(TreePath path, JTree tree) {
    	int startingRow = 0;
    	TreePath result;
    	for (int i=0; i<path.getPathCount(); i++) {
    		String str = path.getPathComponent(i).toString();
    		result = tree.getNextMatch(str, startingRow, Position.Bias.Forward);
    		if (result==null) break;
    		tree.expandPath(result); 
    		startingRow = tree.getRowForPath(result);
    		//System.out.println(result.toString() + " " + startingRow);
    	}
    }
    
    public void treeCollapsed(TreeExpansionEvent e) {
    	expandedTreePathList.remove(e.getPath());
    }

    public void treeExpanded(TreeExpansionEvent e) {
    	expandedTreePathList.add(e.getPath());
    }
    
    class MyRenderer extends DefaultTreeCellRenderer {
    	private JTextArea t;
    	private JPanel p;
    	private Node node;
    	private String nodevalue;
    	
        public MyRenderer() {
    		t = new JTextArea();
    		p = new JPanel();
    		p = new JPanel();
    		t.setColumns(45);
    		t.setLineWrap(true);
    		t.setFocusable(true);
    		t.setWrapStyleWord(true);
    		p.setBorder(new LineBorder(new Color(125, 125, 125)));
    		t.setBorder(new EmptyBorder(5,10,5,10));
    		p.add(t);
        }

        public Component getTreeCellRendererComponent(
                            JTree tree, Object value, boolean sel, boolean expanded,
                            boolean leaf, int row, boolean hasFocus) {
            if (value instanceof XCNode) {
            	node = ((XCNode)value).getNode();
            	if (node.getNodeType()==Node.TEXT_NODE) {
            		nodevalue = node.getNodeValue().trim();
            		if (nodevalue.length()>50) {
                		t.setText(nodevalue);
            			if (sel) t.setBackground(this.getBackgroundSelectionColor());
            			else t.setBackground(this.getBackgroundNonSelectionColor());
            			return p;
            		}
            	}
            }
            super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
            return this;
        }
    }


}
