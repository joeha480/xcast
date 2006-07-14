package tpb.displays;

import org.eclipse.swt.widgets.Display;

public class DisplayThread extends Thread {
	private Display display;
	
	public void run() {
		display = Display.getDefault();
		while( true ) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}
	
	public Display getDisplay() {
		return display;
	}
}