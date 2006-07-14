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

/**
 * ... beskrivning ...
 * 
 * @author  Joel Håkansson, TPB
 * @version 2005-apr-07
 * @see 
 * @since 1.0
 */
public class FMImportOld extends MIFImport implements XCPlugIn {
	private File program;
	
	public FMImportOld(XCBridge bridge) {
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
			try {
				// Skapa skript-fil till DZBatcher
				PrintStream ps = new PrintStream(dz);
				for (int i=0; i<files.length; i++) {
					paths[i] = files[i].getAbsolutePath();
					// Döp om filer för att passa DZBatcher
					File tmp = File.createTempFile("FMImp", ".tmp", settings.getWorkspace());
					tmpPaths[i] = tmp.getAbsolutePath();
					tmp.delete();
					files[i].renameTo(new File(tmpPaths[i]));
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
	            
				for (int i=0; i<files.length; i++) {
					String p1 = tmpPaths[i]+".mif";
					String p2 = paths[i]+".mif";
					String p3 = tmpPaths[i];
					String p4 = paths[i];
					new File(p2).delete();
					new File(p4).delete();
					(new File(p1)).renameTo(new File(p2));
					(new File(p3)).renameTo(new File(p4));
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
	public String getIdentifier() { return "www.tpb.se/xcast/importers/fmimport/version/1.0"; }
	public int getPlatform() { return XCPlugIn.PLATFORM_WINDOWS; }

}
