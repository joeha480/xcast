/*
 * Created on 2005-jul-06
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package XCCore;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * This classloader loads class-files and scans jar-files within the specified directory. 
 * If a class requests a class contained within a jar, the data is extracted from the jar file.
 *  
 * @author  Joel Hakansson, TPB
 * @version 2005-08-26
 * @since 1.0
 */
public class XCClassLoader extends ClassLoader {
	private File dir;
	private Hashtable<String, File> fileHash;
	
	public XCClassLoader(File dir) throws Exception {
		super();
		this.dir = dir;
		fileHash = new Hashtable<String, File>();
		//Search through dir and subdirs
    	FilenameFilter ff = new FilenameFilter(){
    		public boolean accept(File dir, String name) {
    			return name.endsWith(".class")|name.endsWith(".jar");
    		}
    	};
    	ArrayList<File> files = XCUtils.recursiveFileList(dir, ff);
    	// we found something
    	if (files!=null) {
    		File file;
    		//File parent;
    		String filename;
    		for (int i=0; i<files.size(); i++ ) {
    			file = (files.get(i));
    			filename = file.getPath();
    			if (filename.endsWith(".class")) {
    				/* parent = file.getParentFile();
    				System.out.println(file.getPath());
    				while (parent!=null && !parent.getName().equals(dir.getName())) {
    					filename = parent.getName() + '/' + filename;
    					parent = parent.getParentFile();
    				}*/
    				fileHash.put(getClassName(file.getPath(), File.separatorChar), file);
    			} else if (filename.endsWith(".jar")) { // It must be a jar, but just in case.. 
    				JarFile jf = new JarFile(file);
    				Enumeration<JarEntry> en = jf.entries();
    				while (en.hasMoreElements()) {
    					JarEntry jen = en.nextElement();
    					filename = jen.getName();
    					if (filename.endsWith(".class")) {
    						fileHash.put(getClassName(filename, '/'), file);
    					}
    				}
    			}
    		}
    	}
		// build list by searching through dir and subdirs
		// class --> parse name, add file
		// jar --> open jar, find class, parse name, add file
	}
	
	public Hashtable getClassCatalog() {
		return fileHash;
	}
	
    public Class findClass(String name) throws ClassNotFoundException {
    	if (!fileHash.containsKey(name)) throw new ClassNotFoundException(name);
    	File cf = fileHash.get(name);
    	String fn = cf.getName();
    	byte[] b;
    	if (fn.endsWith(".jar")) {
    		b = loadJarData(cf, name);
    	} else {
    		b = loadClassData(cf, fn);
    	}
    	if (b==null) throw new ClassNotFoundException(fn);
    	Class c = null;
    	try {
    		c = defineClass(name, b, 0, b.length);
    	} catch (Exception e) {
    		e.printStackTrace();
    		throw new ClassNotFoundException(name);
    	}
        return c;
    }
    
    private byte[] loadClassData(File f, String classname) {
    	int d = (int)(f.length());
    	byte[] res = new byte[d];
    	System.out.println("Loading class: "+classname + " ("+ d + " bytes)");
    	try {
    		FileInputStream in  = new FileInputStream(f);
    		in.read(res);
    		return res;
    	} catch (Exception e) {
    		e.printStackTrace();
    		return null;
    	}
    }
    
    private byte[] loadJarData(File f, String classname) {
    	try {
    		JarFile jf = new JarFile(f);
    		JarEntry jen = jf.getJarEntry(getFileName(classname, '/'));
    		BufferedInputStream is = new BufferedInputStream(jf.getInputStream(jen));
        	int d = (int)(jen.getSize());
        	byte[] res = new byte[d];
        	System.out.println("Loading class: "+classname + " ("+ d + " bytes)");
    		is.read(res);
    		is.close();
    		jf.close();
    		return res;
    	} catch (Exception e) { 
    		e.printStackTrace();
    		return null;
    	} 
    }
    
    private String getClassName(String filename, char separator) {
		String classname = filename.replace(separator, '.');
		int start = 0;
		if (classname.indexOf(dir.getName()+'.')== 0) start = dir.getName().length()+1; 
		classname = classname.substring(start, classname.lastIndexOf(".class"));
		return classname;
    }
    
    private String getFileName(String classname, char separator) {
    	String filename = classname.replace('.', separator) + ".class";
		return filename;
    }

}
