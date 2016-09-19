package org.hcilab.projects.logeverything.handler;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.hcilab.projects.logeverything.activity.CONST;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

public final class TouchHandler extends Handler {

	@SuppressWarnings("unused")
	private static final String TAG = TouchHandler.class.getSimpleName();
	
	private int x = -1;
	private int y = -1;
	private double prs = -1;
	private int finger = 0;
	private int q = -1;
	private String e = "";
	private final Long maxVal = Long.parseLong("4294967295"); //HEX: FFFFFFFF
	
	private final List<HandlerListener> m_Listeners = new ArrayList<>();
		
	private boolean m_IsRunning = true;
	
	public TouchHandler(Looper looper) {
		super(looper);
	}
	
    public void addListener(HandlerListener toAdd) {
    	m_Listeners.add(toAdd);
    }
	
    private void sendMessage(String msg) {
        // Notify everybody that may be interested.
        for (HandlerListener hl : m_Listeners)
            hl.sendMessage(msg);
    }
    	
	@Override
	public void handleMessage(Message msg) {      
		try {
			Process p = Runtime.getRuntime().exec(new String[]{"su", "-c", "system/bin/sh"});
    		DataOutputStream stdin = new DataOutputStream(p.getOutputStream());

            //Nexus 7 event0
            //HTC ONE m7 event11
			//Amazon Fire Phone event0 synaptics

    		stdin.writeBytes("getevent /dev/input/event11\n");
    		InputStream stdout = p.getInputStream();
    		byte[] buffer = new byte[19];
    		int read;
    		while(m_IsRunning){
    			if (stdout.available() > 19)
    			{
    				read = stdout.read(buffer);
				  
    				String s = new String(buffer, 0, read);
    				String[] parts = s.split(" ");
    				if (parts.length > 2)	
    				{
    					Long code0 = Long.parseLong(parts[0], 16);
    					Long code1 = Long.parseLong(parts[1], 16);
    					while (parts[2].startsWith("0"))
    						parts[2] = parts[2].substring(1);
    					Long code2 = Long.decode("#" + parts[2].replaceAll("\\s", ""));
				  
    					if (code0 == 0 && code1 == 0 && code2 == 0)
    					{
    						sendMessage(CONST.dateFormat.format(System.currentTimeMillis()) + ","+ finger +"," + e + "," + x + "," + y + "," + prs  + "," + q);
    					}
    					else
    					{
    						if (code0 == 3 && code1 == 53) //HEX: 35
    							x = code2.intValue(); //max 1200	
    						else if (code0 == 3 && code1 == 54) //HEX :36
    							y = code2.intValue(); //max 1900
    						else if (code0 == 3 && code1 == 48) //HEX: 30
    							q = code2.intValue(); //max ?
    						else if (code0 == 3 && code1 == 58) //HEX: 3a Press/2 *0.01
    							prs = code2.intValue() / 2.0 * 0.01; //max ?
    						else if (code0 == 3 && code1 == 47) //HEX: 3f
    							finger = code2.intValue(); //finger Count
    						else if (code0 == 3 && code1 == 57) //HEX: 39 
    						{
    							if (code2.equals(maxVal)) //HEX: FFFFFFFF
    								sendMessage(CONST.dateFormat.format(System.currentTimeMillis()) + ","+ finger +"," + "ende" + "," + x + "," + y + "," + prs  + "," + q);
    							else
    								e = "start";
    						}
    					}
    				}
    			}
    			else
    			{
    				synchronized (this) { 
    					wait((System.currentTimeMillis() + 1000) - System.currentTimeMillis()); 	
    				}
    			}
    		}
		} catch (IOException | InterruptedException e) {
			Log.e(TAG, e.toString());
		}
    }
	
	public void stop(){
		m_IsRunning = false;		
	} 
}
