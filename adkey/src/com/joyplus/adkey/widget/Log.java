package com.joyplus.adkey.widget;

import com.joyplus.adkey.Util;

/**
 * @author yyc
 * 
 */

public final class Log {
	
	private static boolean mbLoggable = Util.DEBUE_MODE; 
	
	public static void setLoggable(boolean bLoggable)
	{
		mbLoggable = bLoggable;
	}
	
	public static boolean isLoggable()
	{
		return mbLoggable;
	}
	
	public static boolean isDebuggable()
	{
		return mbLoggable;
	}
	
    public static int i(String tag, String msg) {
    	if (!mbLoggable)
    		return -1;
        return android.util.Log.i(tag, msg);
    }
    
    public static int i(String tag, String msg, Throwable tr) {
    	if (!mbLoggable)
    		return -1;
        return android.util.Log.i(tag, msg, tr);
    }
    
    public static int d(String tag, String msg) {
    	if (!mbLoggable)
    		return -1;
    	return android.util.Log.d(tag, msg);
    }
    public static int d(String msg) {
    	if (!mbLoggable)
    		return -1;
    	return android.util.Log.d("Jas", msg);
    }
    public static int d(String tag, String msg, Throwable tr) {
    	if (!mbLoggable)
    		return -1;
    	return android.util.Log.d(tag, msg, tr);
    }
    
    public static int e(String tag, String msg) {
    	if (!mbLoggable)
    		return -1;
    	return android.util.Log.e(tag, msg);
    }
	
    public static int e(String tag, String msg, Throwable tr) {
    	if (!mbLoggable)
    		return -1;
    	return android.util.Log.e(tag, msg, tr);
    }
    
    public static int w(String tag, String msg, Throwable tr) {
    	if (!mbLoggable)
    		return -1;
    	return android.util.Log.w(tag, msg, tr);
    }
    
    public static int w(String tag, String msg) {
    	if (!mbLoggable)
    		return -1;
    	return android.util.Log.w(tag, msg);
    }
}