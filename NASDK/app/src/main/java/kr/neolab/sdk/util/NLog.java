package kr.neolab.sdk.util;

import android.util.Log;

public class NLog
{
	private static final String TAG = "nasdk";
	private static final boolean D = true;
	private static final boolean I = true;
	private static final boolean E = true;
	private static final boolean W = true;
	
	public static boolean UseExternalLogger = false;
	
	public static void d(String text)
	{
		if (D)
		{
			if ( UseExternalLogger ) 
			{
				//Microlog4android.debug(text);
			}
			Log.d(TAG, text);
		}
	}
	
	public static void d(String tag, String text)
	{
		if (D)
		{
			if ( UseExternalLogger ) 
			{
				//Microlog4android.debug( "["+tag+"]   " +text);
			}
			Log.d(TAG, text);
		}
	}

	public static void i(String text)
	{
		if (I)
		{
			if ( UseExternalLogger ) 
			{
				//Microlog4android.info(text);
			}
			Log.i(TAG, text);
			
		}
	}
	
	public static void i(String tag, String text)
	{
		if (I)
		{
			if ( UseExternalLogger ) 
			{
				//Microlog4android.info( "["+tag+"]   " +text);
			}
			Log.i(TAG, text);
			
		}
	}
	
	public static void w(String text)
	{
		if (W)
		{
			if ( UseExternalLogger ) 
			{
				//Microlog4android.info(text);
			}
			Log.w(TAG, text);
			
		}
	}	

	public static void w(String tag,  String text)
	{
		if (W)
		{
			if ( UseExternalLogger ) 
			{
				//Microlog4android.info( "["+tag+"]   " +text);
			}
			Log.w(TAG, text);
			
		}
	}	

	public static void e(String text)
	{
		if (E)
		{
			if ( UseExternalLogger ) 
			{
				//Microlog4android.error(text);
			}
			Log.e(TAG, text);
		}
	}	
	
	public static void e(String text, Exception e)
	{
		if (E)
		{
			if ( UseExternalLogger ) 
			{
				//Microlog4android.error(text, e);
			}
			Log.e(TAG, text, e);
			
		}
	}	
}
