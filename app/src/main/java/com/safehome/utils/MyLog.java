package com.safehome.utils;

import android.util.Log;

public class MyLog {
	
	private static final boolean DEBUG=true;
	
	public static void error(String tag,String msg){
		if(DEBUG)
			Log.e(tag, msg);
	}
	
	public static void info(String tag,String msg){
		if(DEBUG)
			Log.i(tag, msg);
	}
}
