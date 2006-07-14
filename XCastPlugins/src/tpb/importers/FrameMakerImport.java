package tpb.importers;

import java.io.File;
import java.io.PrintStream;
import java.util.ArrayList;

import javax.swing.JPanel;
import javax.swing.filechooser.FileFilter;

import mif.converters.MifConverter;
import mif.converters.MifFileParser;
import utils.DefaultFileFilter;
import utils.StreamGobbler;
import XCCore.XCBridge;
import XCCore.XCDocument;
import XCCore.XCImport;
import XCCore.XCPlugIn;
import XCCore.XCSettings;

public class FrameMakerImport extends XCImport implements XCPlugIn {
	private File program;
	private File xslt;
	private File pre_xslt;
	protected XCSettings settings;
	
	public FrameMakerImport(XCBridge bridge) {
		super(bridge);
		settings = bridge.getSettings();
		// Ändra länkar!
		this.program = new File(settings.getPluginsDir(), "tpb/importers/FMImport/dzbatcher/bin/dzbatcher.exe");
		this.xslt = new File(settings.getPluginsDir(), "tpb/importers/MIFImport/mif2DTBookNSFP.xsl");
		this.pre_xslt = new File(settings.getPluginsDir(), "tpb/importers/MIFImport/mifpre.xsl");
	}
	
	private boolean convertToMIF(File[] files) {
		File dz = new File(settings.getWorkspace(), "dzb_script");
		String[] paths = new String[files.length];
		String[] tmpPaths = new String[files.length];
		String filename;
		try {
			// Skapa skript-fil till DZBatcher
			PrintStream ps = new PrintStream(dz);
			for (int i=0; i<files.length; i++) {
				filename = files[i].getName();
				if (filename.endsWith(".mif")||filename.endsWith(".xml") ||
						filename.endsWith(".jpg")||filename.endsWith(".tif") ||
						filename.endsWith(".txt")||filename.endsWith(".doc")
				) continue;
				System.out.println("Processnig: " + filename);
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
            p.destroy();
			for (int i=0; i<paths.length; i++) {
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
	}
	
	private boolean convertToXML(File source, File target) {
		try {
			MifConverter mc = new MifConverter();
			File tmp = new File(source.getAbsolutePath()+".xml");
			try {
				System.out.println("Converting");
				File[] fs = mc.convert(source, tmp);
				int len = fs.length;
				File[] tmpf = new File[len];
				if (fs!=null) {
					System.out.println("Transforming - Phase I");
					for (int i=0; i<len; i++) {
						tmpf[i] = File.createTempFile("tmp", ".xml");						
						System.out.println("   -> Processing file " + fs[i]);
						utils.Transformer t = new utils.Transformer(fs[i], pre_xslt, tmpf[i]);
						t.transform();
					}
					for (int i=0; i<len; i++) {
						int j=0;
						while (!fs[i].delete()) {
							System.out.println("Unable to delete temp-file. Retrying...");
							j++;
							this.wait(100);
							if (j>100) { (new Exception("Unable to delete temp-file")).printStackTrace(); }
						}
						j=0;
						while (!tmpf[i].renameTo(fs[i])) {
							System.out.println("Unable to rename temp-file. Retrying...");
							j++;
							this.wait(100);
							if (j>100) { (new Exception("Unable to rename temp-file")).printStackTrace(); }
						}
					}
					System.out.println("Transforming - Phase II");
					if (len>1) System.out.println("   -> Compiling...");
					else System.out.println("   -> Processing file " + fs[0]);
					utils.Transformer t = new utils.Transformer(tmp, xslt, target);
					t.transform();
				}
			} catch (Exception e) {e.printStackTrace(); return false;}
			//tmp.delete();
		} catch (Exception e) {e.printStackTrace(); return false;}
		return true;
	}
	
	private boolean convert(File source, File target) {
		try {
			int fileType = MifFileParser.getFileType(source);
			if (fileType==MifFileParser.TYPE_MIFFILE) {
				MifFileParser.parse(source, target);
				return true;
			} else if (fileType==MifFileParser.TYPE_MIFBOOK) {
				File tmp = File.createTempFile("tmp", ".xml");
				MifFileParser.parse(source, tmp);
				File[] links = MifConverter.getLinks(tmp);
				int i = 0;
				ArrayList<File> convert = new ArrayList<File>();
				for (i=0; i<links.length; i++) { 
					if (!links[i].exists()) {
						
					} else if (MifFileParser.getFileType(links[i])==MifFileParser.TYPE_BINARY) {
						convert.add(links[i]);
					}
				}
				if (convert.size()>0) {
					File[] out = new File[convert.size()];
					convertToMIF(convert.toArray(out));
				}
			} else {
				if (convertToMIF(new File[]{source})) {
					File parent = source.getParentFile();
					return convert(new File(parent, source.getName() + ".mif"), target);
				}
				else return false;
			}
		} catch (Exception e) {e.printStackTrace();}
		return false;
	}
	
	public boolean transform(XCDocument doc) { 
		File source = this.getSource(doc);
		File target = this.getTarget(doc);
		return convert(source, target);
	}
	
	public FileFilter getFilter() {
		DefaultFileFilter dff = new DefaultFileFilter() {
			public boolean accept (File f) { return true; }
		};
		dff.addExtension("book");
		dff.addExtension("fm");
		dff.setDescription("FrameMaker files");
        return dff; 
	}
	
	public boolean prepareImport(JPanel panel) { return true; }

	public String getName() { return "FrameMaker document"; }
	public String getDescription() { return "Converts FrameMaker documents into XML"; }
	public String getAuthor() { return "Joel Håkansson, TPB"; }
	public String getIdentifier() { return "www.tpb.se/xcast/importers/framemakerimport/version/1.0"; }
	public int getPlatform() { return XCPlugIn.PLATFORM_WINDOWS; }
	public boolean acceptsApplication(String app) { return true; }
	
}
