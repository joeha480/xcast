
package utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * ... beskrivning ...
 * 
 * @author  Joel Håkansson, TPB
 * @version 2005-apr-01
 * @see 
 * @since 1.0
 */
public class StreamGobbler extends Thread
{
    InputStream is;
    String type;
    
    public StreamGobbler(InputStream is, String type)
    {
        this.is = is;
        this.type = type;
    }
    
    public void run()
    {
        try
        {
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String line=null;
            while ( (line = br.readLine()) != null)
                //System.out.println(type + ">" + line);
            	System.out.println(line);
            } catch (IOException ioe)
              {
                ioe.printStackTrace();  
              }
    }
}
