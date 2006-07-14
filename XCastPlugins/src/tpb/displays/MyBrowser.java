package tpb.displays;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Rectangle;
import java.io.File;

import javax.swing.JPanel;

import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

public class MyBrowser extends JPanel {
	private Canvas canvas;
	private Browser browser;
	private DisplayThread displayThread;
	
	public MyBrowser() {
		displayThread = new DisplayThread();
		displayThread.start();
		canvas = new Canvas();
		setLayout(new BorderLayout());
		add(canvas, BorderLayout.CENTER);
	}
	
	public void addNotify() {
		super.addNotify();
		initBrowser();
	}
	
	public void initBrowser() {
		while(displayThread.getDisplay()==null) {}
		displayThread.getDisplay().syncExec( new Runnable() {
			public void run() {
				final Shell shell = SWT_AWT.new_Shell(displayThread.getDisplay(), canvas);
				shell.setLayout( new FillLayout() );
				shell.addListener(SWT.Resize, new Listener() {
					public void handleEvent(Event e) {
						repaint();
					}
				});
				shell.addListener(SWT.Paint, new Listener () {
					public void handleEvent(Event e) {
						Rectangle pbounds = canvas.getParent().getParent().getBounds();
						canvas.setBounds(pbounds.x,pbounds.y,pbounds.width,pbounds.height);
					}
				});
				browser = new Browser(shell, SWT.NONE);
			}
		} );
	}

	public boolean setURL(File url) {
		final String localUrl;
		try {
			localUrl = url.toURL().toString();
			System.out.println(localUrl);
		} catch (Exception e) {
			return false;
		}
		displayThread.getDisplay().syncExec(new Runnable() {
			public void run() {
				browser.setUrl(localUrl);
			}
		});
		return true;
	}
}