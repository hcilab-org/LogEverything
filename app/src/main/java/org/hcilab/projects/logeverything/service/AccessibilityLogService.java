package org.hcilab.projects.logeverything.service;


import org.hcilab.projects.logeverything.R;
import org.hcilab.projects.logeverything.activity.CONST;
import org.hcilab.projects.logeverything.activity.MainActivity;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.PowerManager;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;

import androidx.core.app.NotificationCompat;

public class AccessibilityLogService extends AccessibilityService {

	public static final String TAG = AccessibilityLogService.class.getSimpleName();
	
	public static final String SERVICE = "org.hcilab.projects.logeverything/org.hcilab.projects.logeverything.service.AccessibilityLogService";
	
	private PowerManager.WakeLock m_wakeLock;	
	
	private final AccessibilityServiceInfo info = new AccessibilityServiceInfo();
	
	@Override
    public void onCreate() {
		super.onCreate();
		Log.d(TAG, "onCreate");
		
		/*Intent notificationIntent = new Intent(this, MainActivity.class);
		notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

		NotificationCompat.Builder notification = new NotificationCompat.Builder(
				getApplicationContext()).setSmallIcon(R.drawable.ic_launcher)
				.setTicker(getText(R.string.notif_ticker))
				.setContentTitle(getText(R.string.notif_title))
				.setContentText(getText(R.string.notif_text))
				.setContentIntent(pendingIntent).setAutoCancel(true)
				.setOngoing(true).setContentInfo("");

		startForeground(42, notification.build());*/

		PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
		m_wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG);
	}
	
	
	@Override
    public void onInterrupt() {
        Log.v(TAG, "onInterrupt");
    }
 
    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        Log.v(TAG, "onServiceConnected");
        
        
        // Set the type of events that this service wants to listen to.  Others
        // won't be passed to this service.
        info.eventTypes = AccessibilityEvent.TYPES_ALL_MASK;

        // If you only want this service to work with specific applications, set their
        // package names here.  Otherwise, when the service is activated, it will listen
        // to events from all applications.
        //info.packageNames = new String[]{"com.example.android.myFirstApp", "com.example.android.mySecondApp"};

        // Set the type of feedback your service will provide.
        info.feedbackType = AccessibilityServiceInfo.FEEDBACK_ALL_MASK;

        // Default services are invoked only if no package-specific ones are present
        // for the type of AccessibilityEvent generated.  This service *is*
        // application-specific, so the flag isn't necessary.  If this was a
        // general-purpose service, it would be worth considering setting the
        // DEFAULT flag.

        // info.flags = AccessibilityServiceInfo.DEFAULT;
        info.flags = AccessibilityServiceInfo.DEFAULT;

        info.notificationTimeout = 100;

        this.setServiceInfo(info);
    }

	
	
 
    private String getEventText(AccessibilityEvent event) {
        StringBuilder sb = new StringBuilder();
        for (CharSequence s : event.getText()) {
            sb.append(s);
        }
        return sb.toString();
    }
    
    @Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		super.onStartCommand(intent, flags, startId);
		Log.d(TAG, "onStartCommand() was called");
				
		if(!m_wakeLock.isHeld()) {
			m_wakeLock.acquire();
		}		
		new Handler().postDelayed(new Runnable() {
			@Override
			    public void run() {
					if(m_wakeLock.isHeld()) {
						m_wakeLock.release();			
					}
			    }
			},
			10000);
		
		return Service.START_STICKY;
	}
	    
    @Override
	public void onDestroy() {
		Log.d(TAG, "service stopped");
		
		if(m_wakeLock.isHeld()) {
			m_wakeLock.release();			
		}
		
		stopForeground(true);
		super.onDestroy();
	}
    
    
    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
	    Log.d(TAG,"onAccessibilityEvent: "+getEventType(event));
    	
    	if (AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED == event.getEventType())
    		return;
    	
    	String s = String.format("%s,%s,%s,%s,%s,%s\n",
    			CONST.dateFormat.format(System.currentTimeMillis()), event.getEventTime(), getEventType(event),
                event.getClassName(), event.getPackageName(),
                 getEventText(event));
		
		Intent message = new Intent(TAG);
		message.putExtra(android.content.Intent.EXTRA_TEXT, s);
		sendBroadcast(message);   
    }
    
    private String getEventType(AccessibilityEvent event) {
        switch (event.getEventType()) {
        	case AccessibilityEvent.TYPE_ANNOUNCEMENT:
        		return "TYPE_ANNOUNCEMENT";
        	case AccessibilityEvent.TYPE_GESTURE_DETECTION_END:
            	return "TYPE_GESTURE_DETECTION_END";
        	case AccessibilityEvent.TYPE_GESTURE_DETECTION_START:
            	return "TYPE_GESTURE_DETECTION_START";
        	case AccessibilityEvent.TYPE_TOUCH_EXPLORATION_GESTURE_END:
            	return "TYPE_TOUCH_EXPLORATION_GESTURE_END";
        	case AccessibilityEvent.TYPE_TOUCH_EXPLORATION_GESTURE_START:
            	return "TYPE_TOUCH_EXPLORATION_GESTURE_START";
            case AccessibilityEvent.TYPE_TOUCH_INTERACTION_END:
                return "TYPE_TOUCH_INTERACTION_END";
            case AccessibilityEvent.TYPE_TOUCH_INTERACTION_START:
                return "TYPE_TOUCH_INTERACTION_START";
            case AccessibilityEvent.TYPE_VIEW_ACCESSIBILITY_FOCUS_CLEARED:
                return "TYPE_VIEW_ACCESSIBILITY_FOCUS_CLEARED";
            case AccessibilityEvent.TYPE_VIEW_ACCESSIBILITY_FOCUSED:
                return "TYPE_VIEW_ACCESSIBILITY_FOCUS_CLEARED";
            case AccessibilityEvent.TYPE_VIEW_CLICKED:
                return "TYPE_VIEW_CLICKED";
            case AccessibilityEvent.TYPE_VIEW_FOCUSED:
                return "TYPE_VIEW_FOCUSED";
            case AccessibilityEvent.TYPE_VIEW_HOVER_ENTER:
                return "TYPE_VIEW_HOVER_ENTER";
            case AccessibilityEvent.TYPE_VIEW_HOVER_EXIT:
            	return "TYPE_VIEW_HOVER_EXIT";
            case AccessibilityEvent.TYPE_VIEW_LONG_CLICKED:
            	return "TYPE_VIEW_LONG_CLICKED";
            case AccessibilityEvent.TYPE_VIEW_SCROLLED:
                return "TYPE_VIEW_SCROLLED";
            case AccessibilityEvent.TYPE_VIEW_SELECTED:
                return "TYPE_VIEW_SELECTED";
            case AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED:
                return "TYPE_VIEW_TEXT_CHANGED";
            case AccessibilityEvent.TYPE_VIEW_TEXT_SELECTION_CHANGED:
                return "TYPE_VIEW_TEXT_SELECTION_CHANGED";
            case AccessibilityEvent.TYPE_VIEW_TEXT_TRAVERSED_AT_MOVEMENT_GRANULARITY:
                return "TYPE_VIEW_TEXT_TRAVERSED_AT_MOVEMENT_GRANULARITY";
            case AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED:
                return "TYPE_WINDOW_CONTENT_CHANGED";
            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
                return "TYPE_WINDOW_STATE_CHANGED";      	
            
        }
        return "default";
    }
      
}
