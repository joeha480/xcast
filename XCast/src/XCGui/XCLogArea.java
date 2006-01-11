
package XCGui;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import javax.swing.AbstractAction;
import javax.swing.JPopupMenu;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;

public class XCLogArea extends JTextPane {
	  
	    /** Field needed in inner classes*/
	    protected XCLogArea thisLogArea = null;
	  
	    /** The popup menu with various actions*/
	    protected JPopupMenu popup = null;
	  
	    /** The original printstream on System.out */
	    protected PrintStream originalOut;
	  
	    /** The original printstream on System.err */
	    protected PrintStream originalErr;
	    /** This fields defines the Select all behaviour*/
	    protected SelectAllAction selectAllAction = null;
	  
	    /** This fields defines the copy  behaviour*/
	    protected CopyAction copyAction = null;
	  
	    /** This fields defines the clear all  behaviour*/
	    protected ClearAllAction clearAllAction = null;
	  
	    /** Constructs a LogArea object and captures the output from Err and Out. The
	      * output from System.out & System.err is not captured.
	      */
	    public XCLogArea(){
	      thisLogArea = this;
	      this.setEditable(false);
	  
	      LogAreaOutputStream err = new LogAreaOutputStream(true);
	      LogAreaOutputStream out = new LogAreaOutputStream(false);
	  
	      // Redirecting Err
/*	      try{
	        System.err.setPrintWriter(new UTF8PrintWriter(err,true));
	      }catch(UnsupportedEncodingException uee){
	        uee.printStackTrace();
	      }
	      // Redirecting Out
	      try{
	        Out.setPrintWriter(new UTF8PrintWriter(out,true));
	      }catch(UnsupportedEncodingException uee){
	        uee.printStackTrace();
	      }
	*/  
	      // Redirecting System.out
	      originalOut = System.out;
	      try{
	        System.setOut(new UTF8PrintStream(out, true));
	      }catch(UnsupportedEncodingException uee){
	        uee.printStackTrace();
	      }
	  
	      // Redirecting System.err
	      originalErr = System.err;
	      try{
	        System.setErr(new UTF8PrintStream(err, true));
	      }catch(UnsupportedEncodingException uee){
	        uee.printStackTrace();
	      }
	      popup = new JPopupMenu();
	      selectAllAction = new SelectAllAction();
	     copyAction = new CopyAction();
	     clearAllAction = new ClearAllAction();
	 
	     popup.add(selectAllAction);
	     popup.add(copyAction);
	     popup.addSeparator();
	     popup.add(clearAllAction);
	     initListeners();
	   }// LogArea
	 
	   /** Init all listeners for this object*/
	   public void initListeners(){
	    this.addMouseListener(new MouseAdapter(){
	       public void mouseClicked(MouseEvent e){
	         if(SwingUtilities.isRightMouseButton(e)){
	           popup.show(thisLogArea, e.getPoint().x, e.getPoint().y);
	         }//End if
	       }// end mouseClicked()
	     });// End addMouseListener();
	   }
	 
	   /** Returns the original printstream on System.err */
	   public PrintStream getOriginalErr() {
	     return originalErr;
	   }
	 
	   /** Returns the original printstream on System.out */
	   public PrintStream getOriginalOut() {
	     return originalOut;
	   }// initListeners();
	 
	   /** Inner class that defines the behaviour of SelectAll action.*/
	   protected class SelectAllAction extends AbstractAction{
	     public SelectAllAction(){
	       super("Select all");
	     }// SelectAll
	     public void actionPerformed(ActionEvent e){
	       thisLogArea.selectAll();
	     }// actionPerformed();
	   }// End class SelectAllAction
	 
	   /** Inner class that defines the behaviour of copy action.*/
	   protected class CopyAction extends AbstractAction{
	     public CopyAction(){
	       super("Copy");
	     }// CopyAction
	     public void actionPerformed(ActionEvent e){
	       thisLogArea.copy();
	     }// actionPerformed();
	   }// End class CopyAction
	 
	   /**
	    * A runnable that adds a bit of text to the area; needed so we can write
	    * from the Swing thread.
	    */
	   protected class SwingWriter implements Runnable{
	     SwingWriter(String text, Style style){
	       this.text = text;
	       this.style = style;
	     }
	 
	     public void run(){
	       try{
	         if(getDocument().getLength() > 0){
	           Rectangle place = modelToView(getDocument().getLength() - 1);
	           if(place != null) scrollRectToVisible(place);
	         }
	         getDocument().insertString(getDocument().getLength(), text, style);
	       } catch(BadLocationException e){
	           e.printStackTrace(System.err);
	       }// End try
	     }
	     String text;
	     Style style;
	   }
	 
	   /**
	    * A print writer that uses UTF-8 to convert from char[] to byte[]
	    */
	   public static class UTF8PrintWriter extends PrintWriter{
	     public UTF8PrintWriter(OutputStream out)
	            throws UnsupportedEncodingException{
	       this(out, true);
	     }
	 
	     public UTF8PrintWriter(OutputStream out, boolean autoFlush)
	            throws UnsupportedEncodingException{
	       super(new BufferedWriter(new OutputStreamWriter(out, "UTF-8")),
	             autoFlush);
	     }
	   }
	 
	   /**
	    * A print writer that uses UTF-8 to convert from char[] to byte[]
	    */
	   public static class UTF8PrintStream extends PrintStream{
	     public UTF8PrintStream(OutputStream out)
	            throws UnsupportedEncodingException{
	       this(out, true);
	     }
	 
	     public UTF8PrintStream(OutputStream out, boolean autoFlush)
	            throws UnsupportedEncodingException{
	       super(out, autoFlush);
	     }
	 
	     /**
	      * Overriden so it uses UTF-8 when converting a string to byte[]
	      * @param s the string to be printed
	      */
	     public void print(String s) {
	       try{
	         write(s.getBytes("UTF-8"));
	       }catch(UnsupportedEncodingException uee){
	         //support for UTF-8 is guaranteed by the JVM specification
	       }catch(IOException ioe){
	         //print streams don't throw exceptions
	         setError();
	       }
	     }
	 
	     /**
	      * Overriden so it uses UTF-8 when converting a char[] to byte[]
	      * @param s the string to be printed
	      */
	     public void print(char s[]) {
	       print(String.valueOf(s));
	     }
	   }
	 
	   /** Inner class that defines the behaviour of clear all action.*/
	   protected class ClearAllAction extends AbstractAction{
	     public ClearAllAction(){
	       super("Clear all");
	     }// ClearAllAction
	     public void actionPerformed(ActionEvent e){
	       try{
	         thisLogArea.getDocument().remove(0,thisLogArea.getDocument().getLength());
	       } catch (BadLocationException e1){
	         e1.printStackTrace(System.err);
	       }// End try
	     }// actionPerformed();
	   }// End class ClearAllAction
	 
	   /** Inner class that defines the behaviour of an OutputStream that writes to
	    *  the LogArea.
	    */
	   class LogAreaOutputStream extends OutputStream{
	     /** This field dictates the style on how to write */
	     private boolean isErr = false;
	     /** Char style*/
	     private Style style = null;
	 
	     /** Constructs an Out or Err LogAreaOutputStream*/
	     public LogAreaOutputStream(boolean anIsErr){
	       isErr = anIsErr;
	       if (isErr){
	         style = addStyle("error", getStyle("default"));
	         StyleConstants.setForeground(style, Color.red);
	       }else {
	         style = addStyle("out",getStyle("default"));
	         StyleConstants.setForeground(style, Color.black);
	       }// End if
	     }// LogAreaOutputStream
	 
	     /** Writes an int which must be a the code of a char, into the LogArea,
	      *  using the style specified in constructor. The int is downcast to a byte.
	      */
	     public void write(int charCode){
	       // charCode int must be a char. Let us be sure of that
	       charCode &= 0x000000FF;
	       // Convert the byte to a char before put it into the log area
	       char c = (char)charCode;
	       // Insert it in the log Area
	       SwingUtilities.invokeLater(new SwingWriter(String.valueOf(c), style));
	     }// write(int charCode)
	 
	     /** Writes an array of bytes into the LogArea,
	      *  using the style specified in constructor.
	      */
	     public void write(byte[] data, int offset, int length){
	       // Insert the string to the log area
	       try{
	         SwingUtilities.invokeLater(new SwingWriter(new String(data,offset,
	                                                               length, "UTF-8"),
	                                                    style));
	       }catch(UnsupportedEncodingException uee){
	         uee.printStackTrace();
	       }
	     }// write(byte[] data, int offset, int length)
	   }////End class LogAreaOutputStream
	 }//End class LogArea
