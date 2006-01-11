
package XCCore;

/**
 * XCPlugIn is an interface that has to be implemented by a XCast plugin.
 * 
 * @author  Joel Hakansson, TPB
 * @version 2005-08-25
 * @see XCGui.XCDisplay
 * @see XCCore.XCImport
 * @since 1.0
 */
public interface XCPlugIn {
	public static final int PLATFORM_WINDOWS      = 0x000F;
	public static final int PLATFORM_MAC_OS       = 0x00F0;
	public static final int PLATFORM_OTHER        = 0x8000;
	public static final int PLATFORM_ALL          = 0xFFFF;
	
	/** 
	 * A name identifying the plug-in to the user.
	 * @return returns the name of the plug-in
	 */
	public String getName();
	
	/**
	 * A unique string identifying this plug-in. Recommended praxis is to use an url under your
	 * control.
	 * @return returns the unique string
	 */
	public String getIdentifier();
	
	/**
	 * A short description of the plug-ins purpose
	 * @return returns a short description of the plug-ins purpose
	 */
	public String getDescription();
	
	/**
	 * The author of the plug-in
	 * @return returns the author of the plug-in
	 */
	public String getAuthor();

	/**
	 * Specifies if the plug-in is designed for a specific platform, i.e.
	 * uses components that are platform specific.
	 * @return returns platform compatiblity
	 */
	public int getPlatform();
	
	/**
	 * Specifies whether the plug-in is useful for the current application. 
	 * @param app
	 * @return returns true if the application is accepted, false otherwise.
	 */
	public boolean acceptsApplication(String app);
}
