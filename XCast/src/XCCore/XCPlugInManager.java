
package XCCore;

import java.io.File;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;


/**
 * This class is responsible for loading and distributing the plugins, as 
 * requested by the application. 
 * 
 * @author  Joel Hakansson, TPB
 * @version 2005-08-23
 * @since 1.0
 */
public class XCPlugInManager {
	private File root;
	private XCBridge bridge;
	private ArrayList<Class> plugins;
	
	/**
	 * Default constructor
	 * @param settings
	 */
	public XCPlugInManager(XCBridge bridge) {
		this.bridge = bridge;
		this.root = bridge.getSettings().getPluginsDir();
		init();
		loadPlugins();
	}
	
	private void init() {
		plugins = new ArrayList<Class>();
	}
	
	private boolean addPlugin(Class classObj) {
		Class[] interfaces = classObj.getInterfaces();
		for (int i=0; i<interfaces.length; i++) {
			if (interfaces[i] == XCPlugIn.class) {
				plugins.add(classObj);
				return true;
			}
		}
		return false;
	}
	
    private void loadPlugins() {
    	System.out.println("Loading PlugIns...");
    	try {
        	XCClassLoader cl = new XCClassLoader(root);
        	Class classObj;
        	File f;
        	String key;
        	Hashtable<String, File> ht = cl.getClassCatalog();
        	Enumeration<String> en = ht.keys();
        	while (en.hasMoreElements()) {
        		key = en.nextElement();
        		f = ht.get(key);
        		if (f.getName().endsWith(".class")) {
        			classObj = cl.loadClass(key);
        			if (addPlugin(classObj)) { System.out.println("- This is a plugin."); }
        		}
        	}
    	} catch (Exception e) {e.printStackTrace();}
    }
    /*
    private void loadPlugins() {
    	System.out.println("Loading PlugIns...");
    	try {
        	ClassLoader cl = new XCClassLoader(root);
        	FilenameFilter ff = new FilenameFilter(){
        		public boolean accept(File dir, String name) {
        			return name.endsWith(".class");
        		}
        	};
    		ArrayList files = XCUtils.recursiveFileList(root, ff);
    		if (files!=null) {
    			for (int i=0; i<files.size(); i++ ) {
    				File f = (File)(files.get(i));
    				String fn = "";
    				File p = f;
    				while (p!=null && !p.getName().equals(root.getName())) {
    					if (fn=="") fn = p.getName();
    					else fn = p.getName() + '.' + fn;
    					p = p.getParentFile();
    				}
    				fn = fn.substring(0, fn.lastIndexOf(".class"));
        			Class classObj = cl.loadClass(fn);
        			
    				// verify that the plugins interface is implemented
    				if (addPlugin(classObj)) { System.out.println("- This is a plugin.");
    				} else System.out.println("- This is NOT a plugin.");
    			}
    		}
    	} catch (Exception e) {e.printStackTrace();}
    }*/

    /**
     * Get a new instance of this class
     * @param classObj
     * @return a new instance of this plugin type.
     */
    public Object newInstance(Class classObj) {
    	try {
    		Constructor c = classObj.getConstructor(new Class[]{XCBridge.class});
    		return c.newInstance(new Object[]{bridge});
    	} catch (Exception e) { e.printStackTrace(); }
    	return null;
    }
    
    /**
     * Get all plug-ins of a certain type
     * @param type
     * @return returns a list of all matching plug-ins
     */
    public ArrayList<Class> getPluginsByType(Class type) {
    	ArrayList<Class> result = new ArrayList<Class>();
    	for (int i=0; i<plugins.size(); i++) {
    		Class plugin = plugins.get(i);
    		if (type.isAssignableFrom(plugin)) result.add(plugin);
    	}
    	return result;
    }
    
    /**
     * Get the plug-in by identifier.
     * @param ident the unique string identifying the requested plug-in
     * @return Returns the plug-in that is identified by ident, or null if none is found.
     */
    public Object getPluginByName(String ident) {
    	for (int i=0; i<plugins.size(); i++) {
    		Object plugin = newInstance(plugins.get(i));
    		if (((XCPlugIn)(plugin)).getIdentifier().equals(ident)) return plugin;
    	}
    	return null;
    }
    
    /**
     * Get the descriptions of all plugin in a two dimensional Object array
     * for use with JTable.
     * @return the descriptions
     */
    public Object[][] getPluginInfo() {
    	Object[][] result = new Object[plugins.size()][3];
    	//String platform;
    	for (int i=0; i<plugins.size(); i++) {
    		XCPlugIn plugin = (XCPlugIn)(newInstance(plugins.get(i)));
    		result[i][0] = plugin.getName();
    		result[i][1] = plugin.getAuthor();    		
    		result[i][2] = plugin.getDescription();
    		/**if (plugin.getPlatform()&XCPlugIn.PLATFORM_ALL==XCPlugIn.PLATFORM_ALL) {
    			platform
    		} */
    	}
    	return result;
    }
}
