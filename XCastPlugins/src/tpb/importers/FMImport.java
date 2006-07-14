/*
 * Created on 2005-apr-07
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package tpb.importers;

import java.io.File;
import java.io.PrintStream;

import javax.swing.JFileChooser;
import javax.swing.JPanel;

import utils.StreamGobbler;
import XCCore.XCBridge;
import XCCore.XCPlugIn;
import XCCore.XCSettings;
import XCCore.XCUtils;

/**
 * ... beskrivning ...
 * 
 * @author  Joel Håkansson, TPB
 * @version 2005-apr-07
 * @see 
 * @since 1.0
 */
public class FMImport extends MIFImport implements XCPlugIn {
	private File program;
	
	public FMImport(XCBridge bridge) {
		super(bridge);
		this.program = new File(settings.getPluginsDir(), "tpb/importers/FMImport/dzbatcher/bin/dzbatcher.exe");
	}
	
	public boolean prepareImport(JPanel panel) {
		JFileChooser chooser = new JFileChooser();
		chooser.setMultiSelectionEnabled(true);
		chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		chooser.setDialogTitle("Convert FrameMaker documents");
		int returnVal = chooser.showOpenDialog(panel);
		if(returnVal == JFileChooser.APPROVE_OPTION) {
			File dz = new File(settings.getWorkspace(), "dzb_script");
			File[] files = chooser.getSelectedFiles();
			String[] paths = new String[files.length];
			String[] tmpPaths = new String[files.length];
			File[] tmpFiles = new File[files.length];
			try {
				// Skapa skript-fil till DZBatcher
				PrintStream ps = new PrintStream(dz);
				for (int i=0; i<files.length; i++) {
					paths[i] = files[i].getAbsolutePath();
					// Kopiera filer för att passa DZBatcher
					tmpFiles[i] = File.createTempFile("FMImp", ".tmp", settings.getWorkspace());
					tmpPaths[i] = tmpFiles[i].getAbsolutePath();
					XCUtils.copyFile(files[i], tmpFiles[i]);
					ps.println("Open \"" + tmpPaths[i] + "\"");
					ps.println("SaveAs -m \"" + tmpPaths[i] + "\" \"" + tmpPaths[i] + ".mif\"");
					ps.println("Quit \"" + tmpPaths[i] + "\"");
				}
				ps.close();
				// Kör DZBatcher
				Runtime rt = Runtime.getRuntime();
				Process p = rt.exec(program + " -v "+ settings.getWorkspace() + "/dzb_script");
	            StreamGobbler errorGobbler = new StreamGobbler(p.getErrorStream(), "ERROR");            
	            StreamGobbler outputGobbler = new StreamGobbler(p.getInputStream(), "OUTPUT");
	            errorGobbler.start();
	            outputGobbler.start();
	            int ret = p.waitFor();
	            File dir = new File(files[0].getParent(), "/output/");
	            if (!dir.exists()) dir.mkdirs();
				for (int i=0; i<files.length; i++) {
					File p1 = (new File(tmpPaths[i]+".mif"));
					File p2 = new File(dir, files[i].getName()+".mif");
					tmpFiles[i].delete();
					if (p2.exists()) p2.delete();
					p1.renameTo(p2);
				}
				return true;
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			} finally {
				dz.delete();
			}
		} else {
			System.out.println("Aborted!");
			return false;
		}
	}

	public String getName() { return "FrameMaker FM Format"; }
	public String getDescription() { return "Converts FrameMaker (FM) documents into XML"; }
	public String getAuthor() { return "Joel Håkansson, TPB"; }
	public String getIdentifier() { return "www.tpb.se/xcast/importers/fmimport/version/1.1"; }
	public int getPlatform() { return XCPlugIn.PLATFORM_WINDOWS; }

}
