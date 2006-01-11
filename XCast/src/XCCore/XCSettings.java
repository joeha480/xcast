
package XCCore;

import java.io.File;
import java.util.Properties;

/**
 * This class handles the global settings of the application. The main task is to
 * resolve the different directories used by the application.
 * 
 * @author  Joel Hakansson, TPB
 * @version 2005-08-25
 * @since 1.0
 */
public final class XCSettings extends XCProperties {
	/** The name of the application */ 
	public static final String APPLICATION_NAME = "XCast";
	/** The author of the application */ 
	public static final String APPLICATION_AUTHOR = "Joel Håkansson, TPB";
	/** The version of the application */ 
	public static final String APPLICATION_VERSION = "3.1";
	/** The build */
	public static final String APPLICATION_BUILD = "2005-10-13 13:28";
	/** File prefix to use when creating temporary files */ 
	public static final String FILE_PREFIX = "XCDoc";
	/** File postfix to use when creating temporary files */ 
	public static final String FILE_POSTFIX = ".tmp";
	/** The location of the help file */ 
	public static final File HELP_FILE = new File("help/index.html");
	/** Logging level 1 */ public static final int NO_LOGGING = 1;
	/** Logging level 2 */ public static final int ERROR_LOGGING = 2;
	/** Logging level 3 */ public static final int STANDARD_LOGGING = 3;
	/** Logging level 4 */ public static final int DEBUG_LOGGING = 4;
	/** Logging level 5 */ public static final int EXCESSIVE_LOGGING = 5;
	
	// resources
	private static final String XSLT_DIR = "xslt/";
	private static final String ON_OPEN_XSLT_DIR = "onOpen/";

	private static final String WORKSPACE_KEY = "workspace";
	private static final String RESOURCES_KEY = "resources";
	private static final String PLUGINS_KEY = "plugins";
	private static final String APPLICATIONS_KEY = "applications";
	private static final String ACTIVE_APPLICATION_KEY = "active_application";
	
	private static final String CONFIG_DIR_KEY = "configDir";
	private static final String PERSISTENT_FILE_KEY= "persistent";
	
	private static final String BROWSER_KEY = "browser";
	private static final String EDITOR_KEY = "editor";
	private static final String SCHEMA_KEY = "schema";
	private static final String LANGUAGE_KEY = "language";
	private static final String SCROLL_KEY = "scroll";
	private static final String LOGGING_LEVEL = "logging_detail";
	
	/**
	 * Default constructor
	 * @param settingsFile
	 */
	public XCSettings(File settingsFile) {
		super(settingsFile, "Default settings");
	}
	
	protected Properties getDefaults() {
		Properties defaults;
		defaults = new Properties();
		defaults.setProperty(WORKSPACE_KEY, "workspace/");
		defaults.setProperty(RESOURCES_KEY, "resources/");
		defaults.setProperty(PLUGINS_KEY, "plugins/");

		// relativt resources
		defaults.setProperty(SCHEMA_KEY, "dtbook121.xsd");
		defaults.setProperty(LANGUAGE_KEY, "languages/default.xml");
		defaults.setProperty(APPLICATIONS_KEY, "applications/");
		defaults.setProperty(ACTIVE_APPLICATION_KEY, "dtbook/");

		// relativt workspace
		defaults.setProperty(CONFIG_DIR_KEY, "config/");
		defaults.setProperty(PERSISTENT_FILE_KEY, "keep.xc");
		
		defaults.setProperty(BROWSER_KEY, "c:/Program/Internet Explorer/iexplore.exe");
		defaults.setProperty(EDITOR_KEY, "");
		defaults.setProperty(SCROLL_KEY, "20");
		defaults.setProperty(LOGGING_LEVEL, "5");
		//defaults.setProperty(THRESHOLD_KEY, "500000");
		return defaults;
	}
	
	protected Properties verify(Properties props) {
		verifyDir(getConfigurations());
		return props;
	}
	
	protected void verifyDir(File path) {
		if (!path.exists()) try { 
			path.mkdirs(); 
		} catch (Exception e) { e.printStackTrace(); }		
	}
	
	protected File makeAbsolute(File path, String item) {
		File itemFile = new File(item);
		if (itemFile.isAbsolute()) return itemFile;
		else return new File(path, item);		
	}
	
	/**
	 * Get the location of the workspace. This must return a location where the user
	 * is granted both read and write access.
	 * @return returns the location of the workspace.
	 */
	public File getWorkspace() { return new File(getProperty(WORKSPACE_KEY)); }
	
	/**
	 * Get the location of the resources directory. Components using this location
	 * can't assume write access to this directory. It is prefered if it is
	 * regarded as read only.
	 * @return returns the location of the resources directory
	 */
	public File getResources() { return new File(getProperty(RESOURCES_KEY)); }
	
	/**
	 * Get the location of the plugins directory. Components using this location
	 * can't assume write access to this directory. It is prefered if it is
	 * regarded as read only.
	 * @return returns the location of the plugins directory
	 */
	public File getPluginsDir() { return new File(getProperty(PLUGINS_KEY)); }
	
	/**
	 * Get the location of the applications directory. If the property holding this 
	 * value is relative, it will be relative the current resources directory. Components 
	 * using this location can't assume write access to this directory. It is prefered 
	 * if it is regarded as read only.
	 * @return returns the location of the applications directory
	 */
	public File getApplicationsDir() { return makeAbsolute(getResources(), getProperty(APPLICATIONS_KEY)); }
	

	/**
	 * Get the location of the active application directory. The property holding this 
	 * value must be relative to the applications directory. Components 
	 * using this location can't assume write access to this directory. It is prefered 
	 * if it is regarded as read only.
	 * @return returns the location of the active application directory
	 */
	public File getActiveApplication() { return new File(getApplicationsDir(), getProperty(ACTIVE_APPLICATION_KEY)); }
	
	/**
	 * Get the location of the configurations directory. If the property holding this 
	 * value is relative, it will be relative the current workspace. It is recommended
	 * that this location stay relative, since it ensures both read and write access.
	 * @return returns the location of the configurations directory
	 */
	public File getConfigurations() { return makeAbsolute(getWorkspace(), getProperty(CONFIG_DIR_KEY)); }
	
	/**
	 * Get the location of the persistent file. If the property holding this 
	 * value is relative, it will be relative the current workspace. It is recommended
	 * that this location stay relative, since it ensures both read and write access.
	 * @return returns the location of the persistent file
	 */
	public File getPersistentFile() { return makeAbsolute(getWorkspace(), getProperty(PERSISTENT_FILE_KEY)); }
	
	/**
	 * Get the location of the schema file. The property holding this 
	 * value is relative to the active application directory. Assume
	 * read only access.
	 * @return returns the location of the schema file
	 */
	public File getSchema() { return new File(getActiveApplication(), getProperty(SCHEMA_KEY)); }
	
	/**
	 * Get the location of the language file. If the property holding this 
	 * value is relative, it will be relative the current resources directory. Assume 
	 * read only access.
	 * @return returns the location of the language file
	 */
	public File getLanguage() { return makeAbsolute(getResources(), getProperty(LANGUAGE_KEY)); }
	
	/**
	 * Get the location of the xslt directory. This is a subdirectory of the
	 * active application directory. Assume read only access.
	 * @return returns the location of the xslt directory
	 */
	public File getXsltDir() { return new File(getActiveApplication(), XSLT_DIR); }
	
	/**
	 * Get the location of the on-open xslt directory. This is a subdirectory of the
	 * active application directory. Assume read only accses.
	 * @return returns the location of the on-open xslt directory
	 */	
	public File getOnOpenXsltDir() { return new File(getActiveApplication(), ON_OPEN_XSLT_DIR); }
	
	/** 
	 * Get the location of an external browser.
	 * @return returns the location of the external browser
	 */
	public File getBrowser() { return new File(getProperty(BROWSER_KEY)); }
	
	/**
	 * Get the location of an external xml-editor.
	 * @return returns the location of the external editor.
	 */
	public File getEditor() { return new File(getProperty(EDITOR_KEY)); }
	
	/**
	 * Get the scroll increment used for JScrollPanes within the application.
	 * @return returns the scroll increment
	 */
	public int getScrollIncrement() {
		return getIntValue(SCROLL_KEY);
	}
	
	/**
	 * Get the logging level value. Valid values range from 1-5 where:
	 * <ul>
	 * <li>1 = no logging (not recommended)</li>
	 * <li>2 = error logging</li>
	 * <li>3 = standard logging</li>
	 * <li>4 = debug messages</li>
	 * <li>5 = excessive debug messages</li></ul>
	 * 
	 * @return returns the logging level
	 */
	public int getLoggingLevel() {
		return getIntValue(LOGGING_LEVEL);
	}
	
	private int getIntValue(String key) {
		try { 
			return Integer.parseInt(getProperty(key));
		} catch (Exception e) { 
			return Integer.parseInt(getDefaults().getProperty(key));
		}	
	}
	
}
