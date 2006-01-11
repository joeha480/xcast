/*
 * Created on 2005-apr-21
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package XCCore;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;
import java.util.Set;

/**
 * ... beskrivning ...
 * 
 * @author  Joel Håkansson, TPB
 * @version 2005-apr-21
 * @see 
 * @since 1.0
 */
public abstract class XCProperties {
	private Properties props;
	private String comment;

	protected abstract Properties getDefaults();
	protected abstract Properties verify(Properties props);
	
	public XCProperties(File propertiesFile, String comment) {
		this.comment = comment;
		props = new Properties(getDefaults());
		if (propertiesFile.exists()) {
			try {
				FileInputStream is = new FileInputStream(propertiesFile);
				props.loadFromXML(is);
				props = verify(props);
			} catch (Exception e) { e.printStackTrace();}
		} else {
			props = getDefaults();
			props = verify(props);
			saveProperties(propertiesFile);
		}
	}
	
	public void saveProperties(File propertiesFile) {
		try {
			FileOutputStream os = new FileOutputStream(propertiesFile);
			props.storeToXML(os, comment);
		} catch (Exception e) { e.printStackTrace();}		
	}

	public String getProperty(String key) {
		return props.getProperty(key);
	}
	
	public String[][] getProperties() {
		Set ks = props.keySet();
		Object[] keys = ks.toArray();
		String[][] result = new String[keys.length][2];
		for (int i=0; i<keys.length; i++) {
			String key = (String)(keys[i]);
			result[i][0] = key;
			result[i][1] = props.getProperty(key);
		}
		return result;
	}
}
