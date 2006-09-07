import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Properties;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.ToolTipManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLFrameHyperlinkEvent;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import utils.DefaultFileFilter;
import utils.ListCollection;
import utils.SortableKeyValuePair;
import utils.parsers.ElementCounter;
import XCCore.XCBridge;
import XCCore.XCDocument;
import XCCore.XCExport;
import XCCore.XCImport;
import XCCore.XCMessages;
import XCCore.XCPlugIn;
import XCCore.XCPlugInManager;
import XCCore.XCSettings;
import XCCore.XCTool;
import XCCore.XCUtils;
import XCGui.XCCellRenderer;
import XCGui.XCDisplayManager;
import XCGui.XCLogArea;
import XCGui.XCNode;
import XCGui.XCXPathBox;
import XCGui.AutoComplete.AutoCompleteTextField;
import XCGui.AutoComplete.DefaultDictionary;
import filters.XSLTImport;

/**
 * <p>XCast is an interactive XML-remapper.</p>
 * 
 * <p>Resolved since 3.0:
 * 		<ol>
 * 			<li>Treeview now scrolls to center of frame (actually, 1/3 from the top).</li>
 * 			<li>FM-import now outputs to a subdirectory.</li>
 * 			<li>Finding selected text or element i treeview.</li>
 * 			<li>FileChooser now returns to latest used directory 
 * (the filechooser is always the same one, only hidden when not in use.)</li>
 * 			<li>Items in xpath-result can be transformed to a rule selecting a single instance.</li>
 * 			<li>Validation error count added.</li>
 * 			<li>"Application" support</li>
 * 			<li>Added copy to clipboard functionality</li>
 *		</ol>
 * </p>
 * 
 * <p>Resolved in 3.0:
 * 		<ol>
 * 			<li>The XPath expression generated when pressing a button was identical
 *      	when pressed several times (now different by [nrOfTimesPressed])</li>
 *          <li>Both input and output DOM-view</li>
 *     		<li>Plugin support for displays</li>
 *     		<li>Logging detail setting</li>
 *     		<li>Discard open document-dialog</li>
 *     		<li>Improved code readability</li>
 *     		<li>XSLT tooltips</li>
 *     		<li>XPathBox</li>
 * 		    <li>Plugins can depend on jar-files</li>
 *          <li>(Display manager) plugin support, "standard" XCTreeDisplay + custom plugs</li>
 *          <li>(Display manager) "add"-options panel</li>
 *          <li>Wrapping of textlines in Tree view</li>
 *          <li>Searching is now context dependent. If the selected view is changed,
 *          so is the search result</li>
 *    		<li>The settings re-use function includes input/output mappings, 
 *      	XPath-exceptions and attributes</li>
 *      </ol>
 *  </p>
 *  
 * <p>Known issues:
 *   <ol>
 *   <li>NullPointerException when expanding empty nodes</li>
 *   <li>Only one help file can be used, i.e. no language support</li>
 *   <li>addPrefs of ListCollection not portable to other languages</li>
 *   <li>Plugin system dont support libraries</li>
 *   <li>Many, many literals remain</li>
 *   <li>When loading saved settings: if the same settings are loaded several times, conditions
 *   appear several times as well.</li>
 *   <li>Finding selected text or elements only works with built in JTree, not with other plugins.</li>
 *   </ol></p>
 * 
 * <p>Planed updates:
 *   <ol>
 *   <li>Editable input-tree</li>
 *   <li>(Buttons respond differently depending on whether input or output view is 
 *      currently in use)</li>
 *   <li>Updating documentation</li>
 *   <li>Save as should have a "reopen" option</li>
 *   <li>Validating possibility</li>
 *   <li>add support for customizable displays</li>
 *   <li>(Display manager) remove function</li>
 *   <li>(Display manager) It should be possible to set the title of tabs</li>
 *   <li>Plugins should specify platform compatibility</li>
 *   <li>XPath box could pop up when pressing a button</li>
 *   <li>Conditional mappings should have priority equal to [length] - [place in list]</li>
 *   <li>D'n'd-support (ability to ask import-filters for assistance when opening).</li>
 *   <li>Ability to place removed contents in a comment.</li>
 *   <li>Plugins should be loaded when needed.</li>
 *   <li>Plugins could be registered through an xml-file.</li>
 *   </ol></p>
 *  
 * @author  Joel Hakansson, TPB
 * @version 2005-08-30
 */
public class XCast implements ActionListener, XCBridge {
	private final static boolean USE_INTERNAL_LOG = true;
	private static File SETTINGS_FILE;
	private XCMessages xcm;
	private XCSettings settings;
	private XCDocument doc;
    private XCDisplayManager inputDisplay;
    private XCDisplayManager outputDisplay;
    private XCPlugInManager plugsManager;
	private JFrame frame;
	private Document utilityDOM;
	private XCDisplayManager activeDisplayManager;
	private JFileChooser chooser;
	private DefaultFileFilter xmlFileFilter;
	boolean documentOpen = false;

	// Layout components
    JButton openButton;
    JButton openClipboard;
    JButton copyToClipboard;
    JButton saveAsButton;
    JButton findButton;
    JTextField findThisText;
    JComboBox jc;
    JList xsltList;
	JPanel newContentPane;
    JPanel buttonPanel;
    JPanel elementListWrapper;
    JPanel xpathListWrapper;
    JEditorPane helpView;
    JProgressBar progress;
    JScrollPane elementListScrollPane;
    JScrollPane xpathListScrollPane;
    JScrollPane xsltListScrollPane;
    JScrollPane logViewScrollPane;
    JScrollPane helpScrollPane;
    JSplitPane splitAll;
    JTabbedPane viewTabs;
    JTabbedPane utilityTabs;
    XCLogArea logView;
    XCXPathBox xpathbox;
    
    utils.ListCollection[] lc;
    DefaultListModel listModel;
    String[] doctype;
    DefaultDictionary dictionary;
    //protected JPopupMenu popup = null;   
    //protected CopyAction copyAction = null;
	//private int counter = 1;
	//private Object previous = null;
	//public ArrayList displayMethod;
	//public static String anchor;
	//private boolean toggle=false;
    //JPanel wrapper3;
    //JPanel wrapper4;
 
    /** Default constructor. Called when the application is started. */
    public XCast() { 
        //Make sure we have nice window decorations.
        JFrame.setDefaultLookAndFeelDecorated(true);
        //Create and set up the window.
        frame = new JFrame(XCSettings.APPLICATION_NAME);
        // Om något går fel vid initieringen så kan man iaf stänga fönstret...
    	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		settings = new XCSettings(SETTINGS_FILE);
		plugsManager = new XCPlugInManager(this);
		xcm = new XCMessages(settings.getLanguage());
		chooser = new JFileChooser();
        xmlFileFilter = new DefaultFileFilter();
        xmlFileFilter.addExtension("xml");
        xmlFileFilter.setDescription(xcm.getProperty(XCMessages.DIALOG_XML_FILTER_DESCRIPTION));
        xmlFileFilter.setExtensionListInDescription(true);
        //Create and set up the content pane.
        newContentPane = new JPanel();
        newContentPane.setOpaque(true); //content panes must be opaque
        newContentPane.setLayout(new BorderLayout());
        frame.setContentPane(newContentPane);
        //Display the window.

        frame.setBounds(getCenteredBounds(0.9));

        ToolTipManager ttm = ToolTipManager.sharedInstance();
        ttm.setDismissDelay(50000); //4000
        ttm.setInitialDelay(300); //750
        ttm.setReshowDelay(300); //500
        if (USE_INTERNAL_LOG) logView = new XCLogArea();
        doctype = XCUtils.readDoc(settings);
        
        frame.setJMenuBar(createMenuBar());
        buttonPanel = setUpButtonPanel();

        elementListWrapper = new JPanel();
        xpathListWrapper = new JPanel();

        FlowLayout fl = new FlowLayout();
        fl.setAlignment(FlowLayout.LEFT);

        xsltList = loadXSLTs(); 
        xsltList.setLayoutOrientation(JList.VERTICAL);
        xsltList.setVisibleRowCount(10);
        xsltList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        elementListScrollPane = XCUtils.setIncrement(settings.getScrollIncrement(), new JScrollPane(elementListWrapper));
        xpathListScrollPane = new JScrollPane(xpathListWrapper);
        helpView = new JEditorPane();
        helpView.setEditable(false);
        helpView.addHyperlinkListener(new Hyperactive());
        helpScrollPane = new JScrollPane(helpView);
        setPage(helpView, XCSettings.HELP_FILE);

        xsltListScrollPane = new JScrollPane(xsltList);
        if (USE_INTERNAL_LOG) logViewScrollPane = new JScrollPane(logView);
        viewTabs = new JTabbedPane();
        utilityTabs = new JTabbedPane();
        utilityDOM = null;

        elementListScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        elementListScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);

        inputDisplay = new XCDisplayManager(this, plugsManager, XCDisplayManager.SOURCE, frame);
        outputDisplay = new XCDisplayManager(this, plugsManager, XCDisplayManager.TARGET, frame);
        
        viewTabs.addTab(xcm.getProperty(XCMessages.TAB_ORIGINAL_VIEW), inputDisplay);
        viewTabs.addTab(xcm.getProperty(XCMessages.TAB_MODIFIED_VIEW), outputDisplay);
        viewTabs.addTab(xcm.getProperty(XCMessages.TAB_HELP_VIEW), helpScrollPane);
        viewTabs.addTab(xcm.getProperty(XCMessages.TAB_LOG_VIEW), logViewScrollPane);
        viewTabs.setSelectedIndex(2);

		viewTabs.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				//try {
					if (xpathbox!=null) {
						xpathbox.setDisplayManager(getActiveDisplayManager());
						xpathbox.updateExp();
					}
				//} catch (XCException e2) {}
			}
		});
		
        utilityTabs.addTab(xcm.getProperty(XCMessages.TAB_MAPPING), elementListScrollPane);
        utilityTabs.addTab(xcm.getProperty(XCMessages.TAB_POSTPROCESSING), xsltListScrollPane);

        splitAll = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, utilityTabs, viewTabs);
        splitAll.setDividerLocation(200);

        newContentPane.add(buttonPanel, BorderLayout.PAGE_START);
        newContentPane.add(splitAll, BorderLayout.CENTER);
		progress = new JProgressBar(0,100);
		progress.setValue(0);
		progress.setStringPainted(true);
		progress.setString("Ready");
		newContentPane.add(progress, BorderLayout.SOUTH);
		newContentPane.setBorder(BorderFactory.createEmptyBorder(20,20,20,20)); 
        frame.addWindowListener(new WindowAdapter() { 
        	public void windowClosing(WindowEvent e) { 
        		tryShutDown();
        	} 
        });
        // read implicit settings
        restoreState();
        if (settings.getLoggingLevel()>=XCSettings.DEBUG_LOGGING) System.out.println("Max memory: "+Runtime.getRuntime().maxMemory()/1000000+"MB");
        frame.setVisible(true);
        frame.validate();
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE); //Init was ok, set to do nothing, istead use window listener definde above
    }
    
    private Rectangle getCenteredBounds(double percent) {
        Rectangle b = frame.getGraphicsConfiguration().getBounds();
        int height = b.height;
        int width = b.width;
        b.x = (int)((width-(width*percent))/2);
        b.y = (int)((height-(height*percent))/2);
        b.height = (int)(height*percent);
        b.width = (int)(width*percent);
        return b;
    }
    
    private void setPage(JEditorPane pane, File page) {
        try {
        	pane.setPage(page.toURL());
        } catch (Exception e) {
	    	if (settings.getLoggingLevel()>=XCSettings.ERROR_LOGGING) 
		    	XCUtils.handleError(e, frame);
        }
    }
    
    private void setButtons(JComponent cmp, SortableKeyValuePair[] data, String[] options) {
        cmp.removeAll();
		cmp.setLayout(new BoxLayout(cmp, BoxLayout.Y_AXIS));
		lc = new utils.ListCollection[data.length];
		for (int i=0;i<data.length;i++) {
			lc[i] = new utils.ListCollection(data[i].getKey(), data[i].getValue(), this, options, dictionary);
			cmp.add(lc[i]);
		}
    }
    
    /*
    private void setXPathButtons(JComponent cmp, SortableKeyValuePair[] data, String[] options) {
    	GridBagLayout gridbag = new GridBagLayout();
    	GridBagConstraints c = new GridBagConstraints();
    	c.fill = GridBagConstraints.BOTH;
    	cmp.removeAll();
    	cmp.setLayout(gridbag);
    	String[] keys = new String[data.length+1];
    	keys[0] = "-add rule";
    	for (int i=0;i<data.length;i++) {
    		keys[i+1] = data[i].getKey();
    	}
    	JComboBox f1 = new JComboBox(keys);
    	c.weightx = 4.0;
    	c.gridwidth = 4;
    	c.gridwidth = GridBagConstraints.CENTER;
    	gridbag.setConstraints(f1, c);
    	f1.addActionListener(this);
    	cmp.add(f1);
    	JTextField f3 = new JTextField("XPath");
    	c.gridwidth = 1;
    	cmp.add(f3);
    	JComboBox f2 = new JComboBox(options);
    	f2.addActionListener(this);
    	c.gridwidth = 1;
    	c.weightx = 1.0;
    	c.gridwidth = GridBagConstraints.REMAINDER;
    	gridbag.setConstraints(f2, c);
    	cmp.add(f2);
    }*/
    
    private String readComment(File f) {
    	if (f.isDirectory()) return "";
    	StringBuffer result = new StringBuffer();
    	try {
    		BufferedReader lnr = new BufferedReader(new InputStreamReader(new FileInputStream(f), "UTF-8"));
    		String line = ""; 
    		while ((line = lnr.readLine()).indexOf("<!--")==-1) { }
    		result.append(line.substring(line.indexOf("<!--")+4).trim());
    		while ((line = lnr.readLine()).indexOf("-->")==-1) {
    			result.append(" " + line.trim());
    		}
    		result.append(line.substring(0, line.indexOf("-->")).trim());
    	} catch (Exception e) { e.printStackTrace(); }
    	return result.toString();
    }
    
    private JList loadXSLTs() {
        DefaultListModel m = new DefaultListModel();
        ArrayList al = new ArrayList();
    	FilenameFilter ff = new FilenameFilter(){
    		public boolean accept(File dir, String name) {
    			return !(new File(dir, name)).isDirectory();
    		}
    	};
        String[] files = settings.getXsltDir().list(ff);
        for (int i=0; i<files.length; i++ ) {
        	m.addElement(files[i]);
        	al.add(readComment(new File(settings.getXsltDir(), files[i])));
        }
        JList jl = new JList(m);
        jl.setCellRenderer(new XCCellRenderer(al));
        return jl;
    }
    
    private Object[] getConfigList() {
    	if (settings.getLoggingLevel()>=XCSettings.DEBUG_LOGGING) System.out.println(settings.getConfigurations());
    	return XCUtils.arrayMerge(new String[]{"", "Spara ny..."}, settings.getConfigurations().list());
    }

    private JPanel setUpButtonPanel() {
    	JPanel bp = new JPanel();
        jc = new JComboBox(getConfigList());
        openButton = new JButton(xcm.getProperty(XCMessages.MENU_FILE_ITEM_OPEN));
        openClipboard = new JButton("Open clipboard");
        copyToClipboard = new JButton("Copy to clipboard");
        saveAsButton = new JButton(xcm.getProperty(XCMessages.MENU_FILE_ITEM_SAVEAS));
        findButton = new JButton("Find text");
        findThisText = new JTextField(10);
        findThisText.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                int charCodePressed = e.getKeyCode();
                if (charCodePressed == KeyEvent.VK_ENTER) {
                      openXPathBox();
                }
            }
        });
        jc.addActionListener(this);
        openButton.addActionListener(this);
        openClipboard.addActionListener(this);
        copyToClipboard.addActionListener(this);
        saveAsButton.addActionListener(this);
        findButton.addActionListener(this);
        bp.setLayout(new FlowLayout(FlowLayout.LEFT));
        bp.add(jc);
        bp.add(openButton);
        bp.add(saveAsButton);
        bp.add(new JLabel("Sök text:"));
        bp.add(findThisText);
        bp.add(openClipboard);
        bp.add(copyToClipboard);
        //bp.add(findButton);
        return bp;
    }

    class Hyperactive implements HyperlinkListener {
    	 
        public void hyperlinkUpdate(HyperlinkEvent e) {
            if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                JEditorPane pane = (JEditorPane) e.getSource();
                if (e instanceof HTMLFrameHyperlinkEvent) {
                    HTMLFrameHyperlinkEvent  evt = (HTMLFrameHyperlinkEvent)e;
                    HTMLDocument doc = (HTMLDocument)pane.getDocument();
                    doc.processHTMLFrameHyperlinkEvent(evt);
                } else {
                    try {
                        pane.setPage(e.getURL());
                    } catch (Throwable t) {
                        t.printStackTrace();
                    }
                }
            }
        }
    }

    /** 
     * Open a document
     * @param doc the document to open
     */
    public void openDocument(XCDocument doc) {
    	this.doc = doc;
        dictionary = XCUtils.getXSLTDictionary();
		loadElements(doc);
		XCImport importMethod = doc.getImportMethod();
		if (importMethod!=null) frame.setTitle(XCSettings.APPLICATION_NAME + " | (" + doc.getOriginalFile() + " # " + importMethod.getName() + ")");
		else frame.setTitle(XCSettings.APPLICATION_NAME + " | (" + doc.getOriginalFile() + ")");
		xpathbox = new XCXPathBox(this, frame, outputDisplay, dictionary);
		xpathbox.addMouseListener(new MouseAdapter() {
			long t = 0;
			long t1;
			public void mouseClicked(MouseEvent e) {
				t1 = new Date().getTime();
				if (t1-t<200) {
					pushBack(xpathbox.getQuery(), xpathbox.getSelectedIndex());
				}
				t = t1;
			};
		});
		xpathbox.addKeyListener(new KeyAdapter(){
			public void keyReleased(KeyEvent e) {
		        //char charPressed = e.getKeyChar();
		        int charCodePressed = e.getKeyCode();
		        if (charCodePressed == KeyEvent.VK_ENTER) {
		        	pushBack(xpathbox.getQuery(), xpathbox.getSelectedIndex());
		        }
			}
		});
		viewTabs.setSelectedIndex(1);
		inputDisplay.loadDocument(doc);
		outputDisplay.loadDocument(doc);
		documentOpen = true;
	}
    
	private void pushBack(String exp, int index) {
		String button;
		exp = exp.trim();
		int ine = 0;
		int inb = 0;
		if (((inb = 2 + exp.indexOf("//")) == 2) || ((inb = 28 + exp.indexOf("/descendant-or-self::node()/")) == 28)) {
			if ((ine = exp.indexOf('[')) == -1) ine = exp.length();
			button = exp.substring(inb, ine);
			String str = "generate-id(.)=generate-id((" + exp + ")[" + (index + 1) +"])";
			for (int i=0; i<lc.length; i++) {
				if (lc[i].getButtonText().equals(button)) {
					lc[i].addXPathBox(str, lc[i].getComboBoxSelectedItem(), "", false);
					break;
				}
			}
			System.out.println(str);
		} else {}
	}
    
    private void setUpFileChooser(String title, FileFilter filter, JComponent accessory) {
    	chooser.setDialogTitle(title);
    	chooser.setAccessory(accessory);
    	chooser.resetChoosableFileFilters();
    	if (filter!=null) chooser.setFileFilter(filter);
    }
    /**
     * Bring up the file chooser and open the file selected by the user
     */
    public void performOpenAction() {
    	if (!(documentOpen&&
    			!(JOptionPane.OK_OPTION==JOptionPane.showConfirmDialog(frame, "Discard open document?", "Discard?", JOptionPane.OK_CANCEL_OPTION)))) {
        	JPanel jp = new JPanel(new BorderLayout());
            JList openAction = getOpenOptions();
            JScrollPane jsp = new JScrollPane(openAction);
            jp.add(new JLabel("   "), BorderLayout.WEST); //Snabbfix
        	jp.add(jsp, BorderLayout.CENTER);
            setUpFileChooser(null, xmlFileFilter, jp);
            //if (doc != null) chooser.setCurrentDirectory(doc.getOriginalFile());
            int returnVal = chooser.showOpenDialog(newContentPane);
            if(returnVal == JFileChooser.APPROVE_OPTION) {
            	closeOpenDocument();
            	if (settings.getLoggingLevel()>=XCSettings.STANDARD_LOGGING) {
            		System.out.println("You chose to open this file: " + chooser.getSelectedFile().getName());
            		System.out.println(chooser.getSelectedFile().getAbsolutePath());
            	}
                File sf = chooser.getSelectedFile();
                if (settings.getLoggingLevel()>=XCSettings.STANDARD_LOGGING) {
                	System.out.println(sf.getParent());
                	System.out.println(sf.getName());
                }
            	if (openAction.getSelectedIndex()>0) { //index noll betyder <none>
            		String openTransform = openAction.getSelectedValue().toString();
        			XCImport importMethod = new XSLTImport(this, new File(settings.getOnOpenXsltDir(), openTransform));
      				try { doc = new XCDocument(sf, importMethod, settings); }
      				catch (Exception e) {
  			    		if (settings.getLoggingLevel()>=XCSettings.ERROR_LOGGING) 
  			    			XCUtils.handleError(e, frame);
      				}
                }
                else try { doc = new XCDocument(sf, settings); } catch (Exception e) {
			    	if (settings.getLoggingLevel()>=XCSettings.ERROR_LOGGING) 
  			    		XCUtils.handleError(e, frame);
                }
                openDocument(doc);
            }    		
    	}
    }
    
    /**
     * Bring up the file chooser and import the file selected by the user
     * @param importMethod
     */
    public void performImportAction(final XCImport importMethod) {
    	if (!(documentOpen&&
    			!(JOptionPane.OK_OPTION==JOptionPane.showConfirmDialog(frame, "Discard open document?", "Discard?", JOptionPane.OK_CANCEL_OPTION)))) {    	
    	if (importMethod.prepareImport(newContentPane)) {
    		setUpFileChooser("Import " + importMethod.getName(), importMethod.getFilter(), null);
            //if (doc != null) chooser.setCurrentDirectory(doc.getOriginalFile());
    		int returnVal = chooser.showOpenDialog(newContentPane);
    		if(returnVal == JFileChooser.APPROVE_OPTION) {
    			closeOpenDocument();
    			final File sf = chooser.getSelectedFile();
    			//final XCImport importMethod2 = importMethod;
    			final utils.SwingWorker worker = new utils.SwingWorker() {
  				public Object construct() {
  					progress.setIndeterminate(true);
  					progress.setString("Importing");
  					try { doc = new XCDocument(sf, importMethod, settings); } 
  					catch (Exception e) {
  			    		if (settings.getLoggingLevel()>=XCSettings.ERROR_LOGGING) 
  			    			XCUtils.handleError(e, frame);
  					}
  					return doc;
  				}
  				public void finished() {
					progress.setIndeterminate(false);
					progress.setValue(0);  					
  					if (doc != null) {
  						progress.setString("Done!");
  						openDocument(doc);
  					} else {
  						progress.setString("Failed.");
  					}
  				  }
    			};
    			worker.start();
    		}
    	}
    	}
    }
    
    /**
     * Open the contents of the clipboard
     */
    public void performOpenClipboard() {
    	if (!(documentOpen&&
    			!(JOptionPane.OK_OPTION==JOptionPane.showConfirmDialog(frame, "Discard open document?", "Discard?", JOptionPane.OK_CANCEL_OPTION)))) {
    		Clipboard cb = Toolkit.getDefaultToolkit().getSystemClipboard();
    		File sf = null;
    		try {
    			String clip = cb.getData(DataFlavor.stringFlavor).toString();
    			System.out.println(clip);
    			sf = File.createTempFile("tmp", ".xml");
    			PrintWriter os = new PrintWriter(sf, "UTF-8");
    			os.print(clip);
    			os.flush();
    			os.close();
    			closeOpenDocument();
    			doc = new XCDocument(sf, settings);
    			openDocument(doc);
    		} catch (Exception e2) {
		    	if (settings.getLoggingLevel()>=XCSettings.ERROR_LOGGING) 
			    		XCUtils.handleError(e2, frame);
    		}
    		sf.delete();
    	}
    }
    
    public void performCopyToClipboard() {
    	if (documentOpen) {
    		try {
    			  InputStreamReader is = new InputStreamReader(new FileInputStream(doc.getWorkingDocument()), "UTF-8");
    			  int len = (int)doc.getWorkingDocument().length();
    			  final char[] buf = new char[len];
    			  is.read(buf, 0, len);
    			  is.close();
    			  Clipboard cb = Toolkit.getDefaultToolkit().getSystemClipboard();
    			  cb.setContents(new Transferable(){

					public DataFlavor[] getTransferDataFlavors() {
						return new DataFlavor[]{DataFlavor.stringFlavor};
					}

					public boolean isDataFlavorSupported(DataFlavor flavor) {
						if (flavor.equals(DataFlavor.stringFlavor)) {
							return true;
						}
						return false;
					}

					public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
						if (flavor.equals(DataFlavor.stringFlavor)) {
							return new String(buf).replaceAll("<\\?.*\\?>", "");
						}
						else throw new UnsupportedFlavorException(flavor);
					}
    				  
    			  }, null);
    			} catch (Exception e) {e.printStackTrace(); }
    	}
    }
    
    public void performExportAction(final XCExport exportMethod) {
    	if (documentOpen) {
    		setUpFileChooser("Export " + exportMethod.getName(), exportMethod.getFilter(), null);
    		boolean desided = false;
            int returnVal;
    		do {
    			returnVal = chooser.showSaveDialog(newContentPane);
    			if (returnVal == JFileChooser.APPROVE_OPTION) {
    				File tmp = chooser.getSelectedFile();
    				int retVal = JOptionPane.YES_OPTION; 
    				if (tmp.exists()) { 
    					retVal = JOptionPane.showConfirmDialog(newContentPane, "Skriv över "+tmp.getAbsoluteFile()+"?", "Varning!", JOptionPane.YES_NO_CANCEL_OPTION);
    				}
    				if (retVal == JOptionPane.YES_OPTION) {
    					desided = true;
    	    			final utils.SwingWorker worker = new utils.SwingWorker() {
    	      				public Object construct() {
    	      					progress.setIndeterminate(true);
    	      					progress.setString("Exporting");
    	      					doc.export(exportMethod, chooser.getSelectedFile());
    	      					return null;
    	      				}
    	      				public void finished() {
    	    					progress.setIndeterminate(false);
    	    					progress.setValue(0);
    	    					progress.setString("Done!");
   	      				  	}
    	        		};
    	        		worker.start();
    				} else if (retVal == JOptionPane.CANCEL_OPTION) desided = true;
    			} else desided = true;
    		} while (!desided);
    	}
    }

    /** 
     * Save the open document. If the document hasn't been saved before, performSaveAsAction() will
     * be called.
     */
    public void performSaveAction() {
    	if (documentOpen) {
    		if (!doc.save()) performSaveAsAction();
    		else if (settings.getLoggingLevel()>=XCSettings.STANDARD_LOGGING) System.out.println(xcm.getProperty(XCMessages.MSG_DOCUMENT_SAVED));
    	}
    }
    
    /** 
     * Bring up the file chooser dialog and save the file using the name specified by the user
     */
    public void performSaveAsAction() {
    	if (documentOpen) {
    		boolean desided = false;
            setUpFileChooser(null, xmlFileFilter, null);
    		/*if (doc != null) {
    			if (doc.getSaveDocument()!=null) chooser.setCurrentDirectory(doc.getSaveDocument());
    			else chooser.setCurrentDirectory(doc.getOriginalFile());
    		}*/
            int returnVal;
    		do {
    			returnVal = chooser.showSaveDialog(newContentPane);
    			if (returnVal == JFileChooser.APPROVE_OPTION) {
    				File tmp = chooser.getSelectedFile();
    				int retVal = JOptionPane.YES_OPTION; 
    				if (tmp.exists()) { 
    					retVal = JOptionPane.showConfirmDialog(newContentPane, "Skriv över "+tmp.getAbsoluteFile()+"?", "Varning!", JOptionPane.YES_NO_CANCEL_OPTION);
    				}
    				if (retVal == JOptionPane.YES_OPTION) {
    					desided = true;
    					doc.saveAs(tmp);
    					frame.setTitle(XCSettings.APPLICATION_NAME + " | " + doc.getSaveDocument());
    					if (settings.getLoggingLevel()>=XCSettings.STANDARD_LOGGING) System.out.println(xcm.getProperty(XCMessages.MSG_DOCUMENT_SAVED));
    				} else if (retVal == JOptionPane.CANCEL_OPTION) desided = true;
    			} else desided = true;
    		} while (!desided);
    	}
    }
    
    /** 
     * Closes the open document
     */
    public void closeOpenDocument() {
		if (documentOpen) {
			doc.close();
			documentOpen = false;
			frame.setTitle(XCSettings.APPLICATION_NAME);
		}
    }
    
    private Properties getList(boolean getAttribs) {
    	Properties pl = new Properties();
    	for (int i=0;i<lc.length;i++) {
    		pl = lc[i].addProperties(pl, getAttribs);
    	}
    	pl.list(System.out);
    	return pl;
    }
    
    private void updateUtilityDOM() {
    	utilityDOM = null;
    	try {
    		DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
    		utilityDOM = db.newDocument();
    		Element root = utilityDOM.createElement("XCast");
    		Element created = utilityDOM.createElement("created");
    		Element data = utilityDOM.createElement("data");
    		root.setAttribute("version", "1.0");
    		created.setAttribute("at", new Date().toString());
    		created.setAttribute("using", doc.getOriginalFile().getAbsolutePath());
    		root.appendChild(created);
    		root.appendChild(data);
    		utilityDOM.appendChild(root);
    		
    		for (int i=0;i<lc.length;i++) {
    			lc[i].addPrefs(doc, data);
    		}
    	} catch (Exception e) {
    		if (settings.getLoggingLevel()>=XCSettings.ERROR_LOGGING) 
    			XCUtils.handleError(e, frame);
    	}
    }
    
    private void saveUtilityDOM(File file) {
    	if (documentOpen) {
    		updateUtilityDOM();
    		try {
    			Transformer tr = TransformerFactory.newInstance().newTransformer();
    			tr.transform(new DOMSource(utilityDOM), new StreamResult(file));
    		} catch (Exception e) {
    			if (settings.getLoggingLevel()>=XCSettings.ERROR_LOGGING) 
    				XCUtils.handleError(e, frame);    		
    		}
    	}
    }

    private void loadUtilityDOM(File file) {
    	if (!documentOpen) return; 
    	int noMatch=0;
    	try {
    		DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
    		utilityDOM = db.newDocument();
    		DOMResult dr = new DOMResult(utilityDOM);
    		Transformer tr = TransformerFactory.newInstance().newTransformer();
    		tr.transform(new StreamSource(file), dr);
    		NodeList e_element = utilityDOM.getElementsByTagName("element");
    		XPath xp = XPathFactory.newInstance().newXPath();
    		String value;
    		Node e_current;
    		Node e_defaultAction;
    		Node e_defaultAttribs;
    		Node e_condition;
    		NodeList nl;
    		NodeList nl2;
    		HashMap hm;
    		int j;
    		int k;
    		Object attrib;
    		Object match;
    		for (int i=0; i<lc.length; i++) {
    			e_current = (Node)xp.evaluate("//element[@name=\""+lc[i].getButtonText()+"\"]", utilityDOM, XPathConstants.NODE);
    			if (e_current == null) {
   					noMatch++;
   					lc[i].highlight(true);
    				continue;
    			}
    			nl = e_current.getChildNodes();
    			for (j=0; j<nl.getLength(); j++) {
    				if (nl.item(j).getNodeName().equals("default")) {
    					hm = getSettings(nl.item(j).getChildNodes());
    					attrib = hm.get("attribs");
    					lc[i].setComboBoxSelectedItem(hm.get("action"));
    					if (attrib != null) lc[i].setAttribute(attrib.toString(), (Boolean)hm.get("selected"));
    				} else if (nl.item(j).getNodeName().equals("condition")) {
    					hm = getSettings(nl.item(j).getChildNodes());
    					attrib = hm.get("attribs");
    					match = hm.get("match");
    					if (attrib == null) attrib = new String();
    					if (match == null) match = new String();
    					lc[i].addXPathBox(match.toString(), hm.get("action"), attrib.toString(), (Boolean)hm.get("selected"));
    				}
    			}
   			}
   		} catch (Exception e) {
   			if (settings.getLoggingLevel()>=XCSettings.ERROR_LOGGING) 
   				XCUtils.handleError(e, frame);
   		}
   		if (noMatch == lc.length) {
   			JOptionPane.showMessageDialog(frame, "Inga inställningar har ändrats.", xcm.getProperty(XCMessages.MSG_WARNING), JOptionPane.WARNING_MESSAGE);
   		} else if (noMatch>0) {
   			Object[] mfo = new Object[] {new Integer(noMatch), new Integer(lc.length)};
   			String msg = MessageFormat.format(xcm.getProperty(XCMessages.DIALOG_LOAD_LIST_WARNING), mfo);
   			JOptionPane.showMessageDialog(frame, msg, xcm.getProperty(XCMessages.MSG_WARNING), JOptionPane.WARNING_MESSAGE);
   		}
   	}

    private HashMap getSettings(NodeList nl2) {
		String value;
		HashMap hm = new HashMap(10);
		int k;
		for (k=0; k<nl2.getLength(); k++) {
			if (nl2.item(k).getNodeName().equals("action")) {
    			if (nl2.item(k) != null) {
    				value = nl2.item(k).getAttributes().getNamedItem("value").getNodeValue();
    				if (value.equals("keep")) hm.put("action", "-bevara"); 
   					else if (value.equals("flatten")) hm.put("action", "-platta");
   					else if (value.equals("delete")) hm.put("action", "-radera"); 
   					else if (value.equals("map")) hm.put("action", nl2.item(k).getTextContent());    				
   				}
			}
			else if (nl2.item(k).getNodeName().equals("attribs")) {
    			if (nl2.item(k) != null) {
    				value = nl2.item(k).getAttributes().getNamedItem("selected").getNodeValue();
    				hm.put("attribs", nl2.item(k).getTextContent());
    				if (value.equals("true")) hm.put("selected", true);
   					else if (value.equals("false")) hm.put("selected", false);   				
   				}
			}
			else if (nl2.item(k).getNodeName().equals("match")) {
				hm.put("match", nl2.item(k).getTextContent());
			}
		}
		return hm;
    }
    
    private File[] getXSLTList() {
    	Object[] or = xsltList.getSelectedValues();
    	if (or.length==0) return null;
    	File[] ret = new File[or.length];
    	for (int i=0; i<ret.length; i++) {
    		//ret[i] = new File((String)(settings.getXsltDir().getAbsolutePath()+"\\"+or[i])).getAbsolutePath();
    		ret[i] = new File(settings.getXsltDir(), or[i].toString());
    		if (settings.getLoggingLevel()>=XCSettings.DEBUG_LOGGING) System.out.println(ret[i]);
    	}
    	return ret;
    }
    
    private void reloadList() {
		jc.removeAllItems();
		Object[] or = getConfigList();
		for (int i=0;i<or.length;i++) {
			jc.addItem(or[i]);
		}
    }
    
    /**
     * Bring up a dialog that allows the user to select a name for the settings
     * and save the settings to that file using saveUtilityDOM(name);
     */
    public void saveUtilitySettings() {
    	//Save
    	boolean desided = false;
    	String fileName = "Set";
    	while (!desided) {
    		fileName = JOptionPane.showInputDialog(newContentPane, "Title", fileName);
    		if (fileName != null) {
    			File tmp = new File(settings.getConfigurations(), fileName);
    			if (tmp.exists()) {
    				desided = false;
    				JOptionPane.showMessageDialog(newContentPane, "Namnet är upptaget, välj ett annat namn.", "Information", JOptionPane.INFORMATION_MESSAGE);
    			} else {
    				desided = true;
    				//performSaveList(tmp);
    				saveUtilityDOM(tmp);
    				reloadList();
    			}
    		} else desided = true;
    	}
    }
    
    /**
     * @deprecated
     * @param tmp
     */
    private void performSaveList(File tmp) {
		Properties pa = getList(false);
		try {
			PrintStream ps = new PrintStream(tmp);
			pa.storeToXML(ps, "Settings for file " + doc.getOriginalFile());
			ps.close();
		} 
		catch (Exception e2) {
    		if (settings.getLoggingLevel()>=XCSettings.ERROR_LOGGING) 
    			XCUtils.handleError(e2, frame);    					
		}
    }
    
    /**
     * Should be removed, but kept for a version allowing users to convert to
     * the new format.
     * @deprecated
     * @param list
     */
    public void loadList(File list) {
    	if (documentOpen) {
    		Properties pa = new Properties();
    		try {
    			FileInputStream is = new FileInputStream(list);
    			pa.loadFromXML(is);
    			int noMatch=0;
    			int total = lc.length;
    			for (int i=0; i<total; i++) {
    				String jbs = pa.getProperty(lc[i].getButtonText());
    				if (jbs!=null) {
    					lc[i].setComboBoxSelectedItem(jbs);
    				} else {
    					noMatch++;
    					lc[i].highlight(true);
    				}
    			}
    			if (noMatch == total) {
    				JOptionPane.showMessageDialog(frame, "Inga inställningar har ändrats.", xcm.getProperty(XCMessages.MSG_WARNING), JOptionPane.WARNING_MESSAGE);
    			} else if (noMatch>0) {
    				Object[] mfo = new Object[] {new Integer(noMatch), new Integer(lc.length)};
    				String msg = MessageFormat.format(xcm.getProperty(XCMessages.DIALOG_LOAD_LIST_WARNING), mfo);
    				JOptionPane.showMessageDialog(frame, msg, xcm.getProperty(XCMessages.MSG_WARNING), JOptionPane.WARNING_MESSAGE);
    			}
    			//JOptionPane.showMessageDialog(frame, "Inställningarna är sparade i ett format som inte kommer stödjas i nästa uppdatering.", xcm.getProperty(XCMessages.MSG_WARNING), JOptionPane.WARNING_MESSAGE);
    		} catch (Exception e) {
    			loadUtilityDOM(list);
    			/*if (settings.getLoggingLevel()>=XCSettings.ERROR_LOGGING) 
    			 XCUtils.handleError(e, frame);*/
    		}
    	}
    }
    
    private void deleteListItem(File tmp) {
    	tmp.delete();
    	reloadList();
    }
	
	private int transformer(XCDocument doc, Properties props, File[] xslts) throws Exception {
		if (documentOpen) {
			File styleSheetDocument = File.createTempFile("XCDocStyle", ".tmp", settings.getWorkspace());
			File tempFile = File.createTempFile("XCDoc", ".tmp", settings.getWorkspace());
			int count = 0;
			utils.Transformer t;
			try {
				int totalLen = 1;
				if (xslts != null) totalLen += xslts.length;
				progress.setMaximum(totalLen);
				progress.setValue(0);
				if (props != null) {
					PrintStream ps = new PrintStream(styleSheetDocument, "UTF-8");
					XCUtils.printStyleSheet(props, ps);
					if (settings.getLoggingLevel()>=XCSettings.DEBUG_LOGGING) System.out.println();
					if (settings.getLoggingLevel()>=XCSettings.DEBUG_LOGGING) XCUtils.printStyleSheet(props, System.out);
					ps.close();
					t = new utils.Transformer(doc.getSourceDocument(), styleSheetDocument, doc.getWorkingDocument());
					progress.setString("Remapping...");
					t.transform();
				} else {
					progress.setString("Restoring...");
					XCUtils.copyFile(doc.getSourceDocument(), doc.getWorkingDocument());
				}
		        if (xslts != null) {
		        	for (int i=0; i<xslts.length; i++) {
		        		progress.setValue(i+1);
		        		progress.setString(xslts[i].toString());
		        		XCUtils.copyFile(doc.getWorkingDocument(), tempFile);
		        		t = new utils.Transformer(tempFile, xslts[i], doc.getWorkingDocument());
		        		t.transform();
		        	}
		        }
		        progress.setValue(totalLen);
		        count = doc.update();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
		        outputDisplay.reloadAll();
				//try {
					if (getActiveDisplayManager() == inputDisplay) {
						inputDisplay.reloadAll();
					}
				//} catch (XCException e) {}
				styleSheetDocument.delete();
				tempFile.delete();
				progress.setMaximum(100);
			}
			return count;
		}
		return -1;
	}
	
	/**
	 * Bring up the settings dialog. In this version, this window is not editable 
	 * in any way.
	 */
	public void openSettingsDialog() {
		String[] pluginsHeader = {"Name", "Author", "Description"};
		JTable table = new JTable(plugsManager.getPluginInfo(), pluginsHeader);
		JScrollPane pluginsScroll = new JScrollPane(table);
		String[] settingsHeader = {"Key", "Value"};
		JTable settingsTable = new JTable(settings.getProperties(), settingsHeader);
		JScrollPane settingsScroll = new JScrollPane(settingsTable);
		JPanel settingsPanel = new JPanel();
		JTabbedPane settingsTab = new JTabbedPane();
		settingsTab.addTab("Settings", settingsScroll);
		settingsTab.addTab("Plugins", pluginsScroll);
		settingsPanel.add(settingsTab);
		JOptionPane pane = 
			new JOptionPane(settingsPanel, JOptionPane.PLAIN_MESSAGE, JOptionPane.OK_CANCEL_OPTION);
		JDialog dialog = pane.createDialog(frame, "Settings");
		dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		dialog.setVisible(true);
		//Object selectedValue = pane.getValue();
		dialog.dispose();
	}
	/*
	private void openHelpWindow() {
		JFrame helpWindow = new JFrame("Help");
		JPanel contentPane = new JPanel(new BorderLayout());
		contentPane.setOpaque(true);
		try {
			JEditorPane ep = new JEditorPane(XCSettings.HELP_FILE.toURL());
			JScrollPane sp = new JScrollPane(ep);
			contentPane.add(sp, BorderLayout.CENTER);
		} catch (Exception e) {XCLogic.handleError(e, frame);}
		helpWindow.add(contentPane);
		helpWindow.setSize(300,300);
		helpWindow.setVisible(true);
	}
	*/
    
	/**
	 * The applications event listener
	 * @inheritDoc
	 */
    public void actionPerformed(ActionEvent e) {
    	Object source = e.getSource();
    	//String className = XCLogic.getClassName(source);
    	if (source.equals(openButton)) { performOpenAction();
    	} else if (source.equals(openClipboard)) { performOpenClipboard();
    	} else if (source.equals(copyToClipboard)) { performCopyToClipboard();
    	} else if (source.equals(saveAsButton)) { performSaveAsAction();
    	} else if (source.equals(findButton)) {
    		openXPathBox("//text()[contains(., '"+ findThisText.getText() +"')]");
    	} else if (source.equals(jc)) {
    	    if (jc.getSelectedIndex()==1) {
    	    	if (documentOpen) saveUtilitySettings();
    	    } else if (jc.getSelectedIndex()>1) {
    	    	if ((e.getModifiers()&ActionEvent.CTRL_MASK)==ActionEvent.CTRL_MASK) {
    	    		if (JOptionPane.showConfirmDialog(newContentPane, "Vill du verkligen ta bort filen?", 
    	    				"Ta bort?",	JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION) 
    	    				deleteListItem(new File(settings.getConfigurations(), jc.getSelectedItem().toString()));
    	    	} else {
    	    		//the old function loadList will call the new function loadUtilityDOM
    	    		//if it cannot handle the file, this is a temporary step and should be removed
    	    		loadList(new File(settings.getConfigurations(), jc.getSelectedItem().toString()));
    	    		//loadUtilityDOM(new File(settings.getConfigurations(), jc.getSelectedItem().toString()));
    	    	}
    	    }
    	    jc.setSelectedIndex(0);
    	} else if (source instanceof JMenuItem) {
    		JMenuItem menuItem = (JMenuItem)(source);
    		String menuItemText = menuItem.getText();
    		if (settings.getLoggingLevel()>=XCSettings.EXCESSIVE_LOGGING) System.out.println(menuItemText);
    		if (menuItemText.equals(xcm.getProperty(XCMessages.MENU_FILE_ITEM_OPEN))) {
    			performOpenAction();
    		} /*else if (menuItemText.equals(xcm.getProperty(XCMessages.MENU_FILE_SUBMENU_IMPORT_ITEM_FM_KEY))) {
    			performImportAction(new FMImport(settings));
    		} else if (menuItemText.equals(xcm.getProperty(XCMessages.MENU_FILE_SUBMENU_IMPORT_ITEM_MIF_KEY))) {
    			performImportAction(new MIFImport(settings));
    		}*/ else if (menuItemText.equals(xcm.getProperty(XCMessages.MENU_FILE_ITEM_SAVE))) {
    			performSaveAction();
    		} else if (menuItemText.equals(xcm.getProperty(XCMessages.MENU_FILE_ITEM_SAVEAS))) {
    			performSaveAsAction();
    		} else if (menuItemText.equals(xcm.getProperty(XCMessages.MENU_VIEW_ITEM_ORIGINAL))) {
    			viewTabs.setSelectedIndex(0);
    		} else if (menuItemText.equals(xcm.getProperty(XCMessages.MENU_VIEW_ITEM_MODIFIED))) { /*String tmpstr = settings.getBrowser() +  " " + doc.getWorkingDocument().getAbsolutePath();System.out.println(tmpstr);XCLogic.execProcess(tmpstr);*/
    			viewTabs.setSelectedIndex(1);
    		} else if (menuItemText.equals(xcm.getProperty(XCMessages.MENU_VIEW_ITEM_HELP))) {
    			viewTabs.setSelectedIndex(2);
    		} else if (menuItemText.equals(xcm.getProperty(XCMessages.MENU_VIEW_ITEM_LOG))) { 
    			viewTabs.setSelectedIndex(3);
    		} else if (menuItemText.equals(xcm.getProperty(XCMessages.MENU_EDIT_ITEM_FIND))) { 
    			openXPathBox();
    		} else if (menuItemText.equals(xcm.getProperty(XCMessages.MENU_EDIT_ITEM_CHECK))) {
    			/*
    		} else if (menuItemText.equals(xcm.getProperty(XCMessages.MENU_EDIT_SUBMENU_RENDER_ITEM_ORIGINAL))) {
    			if (documentOpen) {
    				try {
    					transformer(doc, null, null); 
      					progress.setValue(0);
      					progress.setString("Done!");
      				} 
    				catch (Exception e2) {
    		    		if (settings.getLoggingLevel()>=XCSettings.ERROR_LOGGING) 
    		    			XCUtils.handleError(e2, frame);
    				}
    			}*/
    		} else if (menuItemText.equals(xcm.getProperty(XCMessages.MENU_EDIT_ITEM_UPDATE))) {
    			if (documentOpen) {
        	       	final Properties pa = getList(true);
        	       	updateUtilityDOM();
        	       	final File[] xl = getXSLTList();
        	       	
    	    		final utils.SwingWorker worker = new utils.SwingWorker() {
    	    			private int count = 0;
    	    			
    	    			public Object construct() {
    	    				try { 
    	    					count = transformer(doc, pa, xl);
    	    				} catch (Exception e2) {}
    	      				return count;
    	      			}
    	    			
    	      			public void finished() {
    	      				progress.setValue(0);
    	      				if (count>0) progress.setString("Done! Found " + count + " validation problems.");
    	      				else if (count==1) progress.setString("Done! Found " + count + " validation problem.");
    	      				else if (count==0) progress.setString("Done! Found no validation problems.");
    	      				else progress.setString("Done! Validation failed.");
    	      				if (xpathbox.isVisible()) xpathbox.updateExp();
    	      			  }
    	        	};
    	        	worker.start();
    			}
    		} else if (menuItemText.equals(xcm.getProperty(XCMessages.MENU_EDIT_ITEM_SETTINGS))) {
    			openSettingsDialog();
    		} else if (menuItemText.equals(xcm.getProperty(XCMessages.MENU_HELP_ITEM_CONTENTS))) {
    			viewTabs.setSelectedIndex(2);
    			setPage(helpView, new File("help/index.html"));
    		} else if (menuItemText.equals(xcm.getProperty(XCMessages.MENU_HELP_ITEM_API_REFERENCE))) {
    			viewTabs.setSelectedIndex(2);
    			setPage(helpView, new File("help/api-doc/overview-summary.html"));    			
    		} else if (menuItemText.equals(xcm.getProperty(XCMessages.MENU_HELP_ITEM_ABOUT))) {
    			//JOptionPane.showMessageDialog(newContentPane, XCSettings.APPLICATION_NAME + "\n" + XCSettings.APPLICATION_AUTHOR + "\n" + XCSettings.APPLICATION_VERSION, xcm.getProperty(XCMessages.MENU_HELP_ITEM_ABOUT), JOptionPane.INFORMATION_MESSAGE);
    			JOptionPane.showMessageDialog(newContentPane, "<html><body><p>"+XCSettings.APPLICATION_NAME + " " + XCSettings.APPLICATION_VERSION + "</p><p>Build " + XCSettings.APPLICATION_BUILD + "</p><p>" + XCSettings.APPLICATION_AUTHOR + "</p><p><a href=\"www.tpb.se\">www.tpb.se</a></p></body></html>", xcm.getProperty(XCMessages.MENU_HELP_ITEM_ABOUT), JOptionPane.INFORMATION_MESSAGE);
    		} else if (menuItemText.equals(xcm.getProperty(XCMessages.MENU_FILE_ITEM_EXIT))) {
    			tryShutDown();
    		} else {
    			boolean found = false;
    			ArrayList<Class> plgs = plugsManager.getPluginsByType(XCImport.class);
    			for (int i=0; i<plgs.size(); i++) {
    				XCPlugIn pl = (XCPlugIn)(plugsManager.newInstance((Class)plgs.get(i)));
    				if (menuItemText.equals(pl.getName())) {
    					performImportAction((XCImport)(pl));
    					found = true;
    					break;
    				}
    			}
    			if (!found && documentOpen) {
        			plgs = plugsManager.getPluginsByType(XCExport.class);
        			for (int i=0; i<plgs.size(); i++) {
        				XCPlugIn pl = (XCPlugIn)(plugsManager.newInstance((Class)plgs.get(i)));
        				if (menuItemText.equals(pl.getName())) {
        					performExportAction((XCExport)(pl));
        					found = true;
        					break;
        				}
        			}    				
    			}
    			if (!found && documentOpen) {
    				plgs = plugsManager.getPluginsByType(XCTool.class);
    				for (int i=0; i<plgs.size(); i++) {
    					XCPlugIn pl = (XCPlugIn)(plugsManager.newInstance((Class)plgs.get(i)));
    					if (menuItemText.equals(pl.getName())) {
    						final XCPlugIn pl2 = pl;
    	    	    		final utils.SwingWorker worker = new utils.SwingWorker() {
    	    	    			public Object construct() {
    	    	    				try {
    	        						progress.setIndeterminate(true);
    	        						progress.setString("Executing " + pl2.getName());
    	        						boolean success = ((XCTool)pl2).execute();
    	        						if (success) {
    	        							getDocument().update();
    	        							inputDisplay.reloadAll();
    	        							outputDisplay.reloadAll();
    	        						}
    	    	    				} catch (Exception e2) {}
    	    	      				return null;
    	    	      			}
    	    	      			public void finished() {
    	    						progress.setIndeterminate(false);
    	    						progress.setString("Done!");
    	    						
    	    					}
    	    	        	};
    	    	        	worker.start();
    						break;
    					}
    				}
    			}
    		}
    	} else if (source instanceof JButton) {
    		String text = ((JButton)(e.getSource())).getText();
    		//try {
    			if (getActiveDisplayManager()!=null) 
    				getActiveDisplayManager().selectNextInstance(text);
    		//} catch (XCException e2) {}
    	} else { 
    		if (settings.getLoggingLevel()>=XCSettings.STANDARD_LOGGING) {
    			System.out.println("Unhandled event: " + e.getSource().getClass().getCanonicalName());
    		}
    	}
    }
    
    private class XCException extends Exception {

		public XCException() {
			super();
		}

		public XCException(String message, Throwable cause) {
			super(message, cause);
		}

		public XCException(String message) {
			super(message);
		}

		public XCException(Throwable cause) {
			super(cause);
		}
    }
    
    private void openXPathBox() {
    	if (documentOpen) {
    		try {xpathbox.setDisplayManager(getActiveDisplayManager()); } catch (Exception e) {}
    		Component c = frame.getFocusOwner();
    		if (c instanceof AutoCompleteTextField) {
    			AutoCompleteTextField f = (AutoCompleteTextField)c;
    			Component c1 = f.getParent().getParent().getParent();
    			if (c1 instanceof ListCollection) {
    				ListCollection f1 = (ListCollection)c1;
    				String text = "//"+f1.getButtonText();
    				if (!f.getText().equals("")) text += '['+f.getText()+']';
    				xpathbox.setQuery(text);
    			}
    		}
    		else if (c instanceof JButton && c.getParent().getParent() == elementListWrapper) {
    			xpathbox.setQuery("//"+((JButton)c).getText()); //= new XCXPathBox(frame, outputDisplay, dd);
    		} else if (c == findThisText) {
    			xpathbox.setQuery("//text()[contains(., '"+ findThisText.getText() +"')]");
    		} // Fungerar inte med generella plugins
    		else if (c instanceof JTree) {
    			JTree tr = (JTree)c;
    			Node n = ((XCNode)tr.getLastSelectedPathComponent()).getNode();
    			switch (n.getNodeType()) {
    				case Node.ELEMENT_NODE :
    					xpathbox.setQuery("//"+n.getNodeName());
    					break;
    				case Node.TEXT_NODE :
    					xpathbox.setQuery("//text()[.='"+ n.getNodeValue() +"']");
    					break;
    			}
    		}
    		//
    		xpathbox.setVisible(true);
    	}
    }
    
    private void openXPathBox(String query) {
    	if (documentOpen) {
    		try {xpathbox.setDisplayManager(getActiveDisplayManager()); } catch (Exception e) {}
    		xpathbox.setQuery(query);
    		xpathbox.setVisible(true);
    	}
    }
        /*
	
	if (c instanceof AutoCompleteTextField) {
		AutoCompleteTextField f = (AutoCompleteTextField)c;
		Component c1 = f.getParent().getParent().getParent();
		if (c1 instanceof ListCollection) {
			ListCollection f1 = (ListCollection)c1;
			String text = "//"+f1.getButtonText();
			if (!f.getText().equals("")) text += '['+f.getText()+']';
			inputText.setText(text);
			getResult();
		}
	} else if (c instanceof JButton) {
		inputText.setText();
		getResult();
	}
	*/
    private void tryShutDown() {
		int n = JOptionPane.showConfirmDialog(newContentPane, xcm.getProperty(XCMessages.DIALOG_EXIT), xcm.getProperty(XCMessages.DIALOG_EXIT), JOptionPane.YES_NO_OPTION);
		if (n==0) {
			if (documentOpen) {
				inputDisplay.unloadDocument();
				outputDisplay.unloadDocument();
				doc.close();
			}
			saveState();
			//try { Thread.sleep(1000); } catch (Exception e) { e.printStackTrace(); }
			System.exit(0);
		}
    }

    private void saveState() {
    	try {
    		FileOutputStream fos = new FileOutputStream(settings.getPersistentFile());
    		ObjectOutputStream oos = new ObjectOutputStream(fos);
    		oos.writeObject(frame.getBounds());
    		oos.writeInt(splitAll.getDividerLocation());
    		oos.writeObject(chooser.getCurrentDirectory());
    		oos.close();
    	} catch (Exception e) {}
    }

    private void restoreState() {
    	if (settings.getPersistentFile().exists()) {
    		try {
    			FileInputStream fis = new FileInputStream(settings.getPersistentFile());
    			ObjectInputStream ois = new ObjectInputStream(fis);
    			frame.setBounds((Rectangle)(ois.readObject()));
    			splitAll.setDividerLocation(ois.readInt());
    			chooser.setCurrentDirectory((File)ois.readObject());
    			ois.close();
    		} catch (Exception e) {e.printStackTrace();}
    	}
    }

    private void loadElements(XCDocument doc) {
    	ElementCounter h = null;
    	try {
    		h = new ElementCounter(doc.getSourceDocument()); 
    		//Hashtable ht = h.getTable();
    		SortableKeyValuePair[] skvp = h.getSortable();
    		Arrays.sort(skvp);
    		for (int i=0; i<skvp.length; i++) {
    			if (settings.getLoggingLevel()>=XCSettings.DEBUG_LOGGING) System.out.println(skvp[i].getKey() + " " + skvp[i].getValue());
    			dictionary.addEntry(skvp[i].getKey());
    		}
        	elementListWrapper.removeAll();
        	setButtons(elementListWrapper, skvp, doctype);
    	} catch (Exception e) { 
    		if (settings.getLoggingLevel()>=XCSettings.ERROR_LOGGING) 
    			XCUtils.handleError(e, frame);
    	}
    }

    private JList getOpenOptions() {
    	FilenameFilter ff = new FilenameFilter(){
    		public boolean accept(File dir, String name) {
    			return name.endsWith(".xsl")|name.endsWith(".xslt");
    		}
    	};
    	String[] oo = settings.getOnOpenXsltDir().list(ff);
    	JList njcb = new JList(XCUtils.arrayMerge(new String[]{"<Ingen transformering>"}, oo));
    	njcb.setSelectedIndex(0);
    	njcb.setLayoutOrientation(JList.VERTICAL);
    	njcb.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    	return njcb;
    }
    
    private JMenuBar createMenuBar() {
         JMenuBar menuBar;
         JMenu menu, submenu;
         JMenuItem menuItem;

         menuBar = new JMenuBar();

         // File meny
         menu = new JMenu(xcm.getProperty(XCMessages.MENU_FILE));
         menu.setMnemonic(KeyEvent.VK_A);
         menuBar.add(menu);

         menuItem = new JMenuItem(xcm.getProperty(XCMessages.MENU_FILE_ITEM_OPEN), KeyEvent.VK_P);
         menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));
         menuItem.addActionListener(this);
         menu.add(menuItem);

         ArrayList impPlugs = new ArrayList();
       	 impPlugs = plugsManager.getPluginsByType(XCImport.class);//ClassLoader.getSystemClassLoader().loadClass("XCCore.XCImport"));
         if (impPlugs.size()>0) {
         	submenu = new JMenu(xcm.getProperty(XCMessages.MENU_FILE_SUBMENU_IMPORT));
         	submenu.setMnemonic(KeyEvent.VK_I);
         	for (int i=0; i<impPlugs.size(); i++) {
         		XCPlugIn pli = (XCPlugIn)plugsManager.newInstance((Class)impPlugs.get(i));
         		menuItem = new JMenuItem(pli.getName());
         		menuItem.setAccelerator(KeyStroke.getKeyStroke(49+i, ActionEvent.CTRL_MASK));
         		menuItem.addActionListener(this);
         		submenu.add(menuItem);
         	}
         	menu.add(submenu);
         }
         
         ArrayList expPlugs = new ArrayList();
       	 expPlugs = plugsManager.getPluginsByType(XCExport.class);//ClassLoader.getSystemClassLoader().loadClass("XCCore.XCImport"));
         if (expPlugs.size()>0) {
         	submenu = new JMenu(xcm.getProperty(XCMessages.MENU_FILE_SUBMENU_EXPORT));
         	submenu.setMnemonic(KeyEvent.VK_E);
         	for (int i=0; i<expPlugs.size(); i++) {
         		XCPlugIn pli = (XCPlugIn)plugsManager.newInstance((Class)expPlugs.get(i));
         		menuItem = new JMenuItem(pli.getName());
         		menuItem.addActionListener(this);
         		submenu.add(menuItem);
         	}
         	menu.add(submenu);
         }
         
         menuItem = new JMenuItem(xcm.getProperty(XCMessages.MENU_FILE_ITEM_SAVE), KeyEvent.VK_S);
         menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
         menuItem.addActionListener(this);
         menu.add(menuItem);
         
         menuItem = new JMenuItem(xcm.getProperty(XCMessages.MENU_FILE_ITEM_SAVEAS), KeyEvent.VK_A);
         menuItem.addActionListener(this);
         menu.add(menuItem);
         
         menu.addSeparator();
         
         menuItem = new JMenuItem(xcm.getProperty(XCMessages.MENU_FILE_ITEM_EXIT), KeyEvent.VK_T);
         menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F4, ActionEvent.ALT_MASK));
         menuItem.addActionListener(this);
         menu.add(menuItem);

         // Edit menu
         menu = new JMenu(xcm.getProperty(XCMessages.MENU_EDIT));
         menu.setMnemonic(KeyEvent.VK_R);
         menuBar.add(menu);
/*
         submenu = new JMenu(xcm.getProperty(XCMessages.MENU_EDIT_SUBMENU_RENDER));
         submenu.setMnemonic(KeyEvent.VK_R);
                  
         menuItem = new JMenuItem(xcm.getProperty(XCMessages.MENU_EDIT_SUBMENU_RENDER_ITEM_TRANSFORMED), KeyEvent.VK_G);
         menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0));
         menuItem.addActionListener(this);
         submenu.add(menuItem);
         
         menuItem = new JMenuItem(xcm.getProperty(XCMessages.MENU_EDIT_SUBMENU_RENDER_ITEM_ORIGINAL));
         menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F5, ActionEvent.SHIFT_MASK));
         menuItem.addActionListener(this);
         submenu.add(menuItem);
         
         menu.add(submenu);*/
         
         menuItem = new JMenuItem(xcm.getProperty(XCMessages.MENU_EDIT_ITEM_UPDATE), KeyEvent.VK_G);
         menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0));
         menuItem.addActionListener(this);
         menu.add(menuItem);
         
       /*  menuItem = new JMenuItem(xcm.getProperty(XCMessages.MENU_EDIT_ITEM_CHECK_KEY), KeyEvent.VK_V);
         menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F11, 0));
         menuItem.addActionListener(this);
         menu.add(menuItem);*/
         
         menu.addSeparator();
         
         menuItem = new JMenuItem(xcm.getProperty(XCMessages.MENU_EDIT_ITEM_FIND), KeyEvent.VK_P);
         menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, ActionEvent.CTRL_MASK));
         menuItem.addActionListener(this);
         menu.add(menuItem);
         
         menu.addSeparator();
         
         menuItem = new JMenuItem(xcm.getProperty(XCMessages.MENU_EDIT_ITEM_SETTINGS));
         menuItem.addActionListener(this);
         menu.add(menuItem);
         
         ArrayList toolPlugs = new ArrayList();
       	 toolPlugs = plugsManager.getPluginsByType(XCTool.class);//ClassLoader.getSystemClassLoader().loadClass("XCCore.XCImport"));
         if (toolPlugs.size()>0) {
         	menu = new JMenu(xcm.getProperty(XCMessages.MENU_TOOLS));
         	menu.setMnemonic(KeyEvent.VK_T);
         	menuBar.add(menu);
         	for (int i=0; i<toolPlugs.size(); i++) {
         		XCPlugIn pli = (XCPlugIn)plugsManager.newInstance((Class)toolPlugs.get(i));
         		menuItem = new JMenuItem(pli.getName());
         		menuItem.addActionListener(this);
         		menu.add(menuItem);
         	}
         }

         // View menu
         menu = new JMenu(xcm.getProperty(XCMessages.MENU_VIEW));
         menu.setMnemonic(KeyEvent.VK_V);
         menuBar.add(menu);

         menuItem = new JMenuItem(xcm.getProperty(XCMessages.MENU_VIEW_ITEM_ORIGINAL), KeyEvent.VK_X);
         menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_1, ActionEvent.ALT_MASK));
         menuItem.addActionListener(this);
         menu.add(menuItem);

         menuItem = new JMenuItem(xcm.getProperty(XCMessages.MENU_VIEW_ITEM_MODIFIED), KeyEvent.VK_M);
         menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_2, ActionEvent.ALT_MASK));
         menuItem.addActionListener(this);
         menu.add(menuItem);

         menuItem = new JMenuItem(xcm.getProperty(XCMessages.MENU_VIEW_ITEM_HELP), KeyEvent.VK_T);
         menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_3, ActionEvent.ALT_MASK));
         menuItem.addActionListener(this);
         menu.add(menuItem);

         menuItem = new JMenuItem(xcm.getProperty(XCMessages.MENU_VIEW_ITEM_LOG), KeyEvent.VK_L);
         menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_4, ActionEvent.ALT_MASK));
         menuItem.addActionListener(this);
         menu.add(menuItem);
         
         // Help menu
         menu = new JMenu(xcm.getProperty(XCMessages.MENU_HELP));
         menu.setMnemonic(KeyEvent.VK_H);
         menuBar.add(menu);

         menuItem = new JMenuItem(xcm.getProperty(XCMessages.MENU_HELP_ITEM_CONTENTS), KeyEvent.VK_T);
         menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0));
         menuItem.addActionListener(this);
         menu.add(menuItem);
         
         menuItem = new JMenuItem(xcm.getProperty(XCMessages.MENU_HELP_ITEM_API_REFERENCE), KeyEvent.VK_T);
         menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F1, ActionEvent.ALT_MASK));
         menuItem.addActionListener(this);
         menu.add(menuItem);
         
         menuItem = new JMenuItem(xcm.getProperty(XCMessages.MENU_HELP_ITEM_ABOUT), KeyEvent.VK_T);
         menuItem.addActionListener(this);
         menu.add(menuItem);
         
         return menuBar;
    }
    
    /**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event-dispatching thread.
     */
    private static void createAndShowGUI() {
    	/*XCast xc = */new XCast();
    }
   
    /**
     * Application main. One optional argument; should point to a settings-file 
     * having identical structure as the settings-file generated when the
     * application is started for the first time.
     * @param args
     */
    public static void main(String[] args) {
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
    	if (args.length>0) {
    		SETTINGS_FILE = new File(args[0]);
    	} 
    	if (args.length==0||!SETTINGS_FILE.exists()) {
    		SETTINGS_FILE = new File("settings.xml");
    	}
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }
    
    // Interface XCBridge
    public XCDocument getDocument() {
    	return doc;
    }
    public XCDisplayManager getActiveDisplayManager() {
    	if (viewTabs.getSelectedComponent() instanceof XCDisplayManager) 
    		return (XCDisplayManager)viewTabs.getSelectedComponent();
    	else return null; //throw new XCException("No display is active");
    }
    public XCSettings getSettings() {
    	return settings;
    }
    public XCMessages getMessages() {
    	return xcm;
    }
    
    public JFrame getFrame() {
    	return frame;
    }
    
    public void handleError(Exception e) {
    	XCUtils.handleError(e, frame);
    }
    
}

