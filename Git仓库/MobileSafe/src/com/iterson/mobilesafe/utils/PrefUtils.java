package com.iterson.mobilesafe.utils;

import android.R.integer;
import android.R.string;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * 封装sharedPreference
 * @author Yang
 *
 *
 */

public class PrefUtils {
	public static final String PREF_NAME ="config";
	//优化只创建一个sp对象
	public static SharedPreferences mPrefs;
	
	public static void setBoolean(Context ctx, String key , boolean value){
		if(mPrefs == null){
		 mPrefs= ctx.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
		}
		mPrefs.edit().putBoolean(key, value).commit();
	}
	public static boolean getBoolean(Context ctx, String key , boolean defValue) {
		if(mPrefs == null){
		 mPrefs= ctx.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
		}
		return mPrefs.getBoolean(key, defValue);
	}
	
	public static void setString(Context ctx, String key , String value){
		if(mPrefs == null){
		SharedPreferences mPrefs= ctx.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
		}
		mPrefs.edit().putString(key, value).commit();
	}
	public static String getString(Context ctx, String key , String defValue) {
		if(mPrefs == null){
		SharedPreferences mPrefs= ctx.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
		}
		return mPrefs.getString(key, defValue);
	}
	
	public static void remove(Context ctx, String key){
		if (mPrefs == null) {
			SharedPreferences mPrefs = ctx.getSharedPreferences(key, Context.MODE_PRIVATE);
		}
		mPrefs.edit().remove(key).commit();
		
	}
	
	public static void setInt(Context ctx,String key , int value){
		if (mPrefs == null) {
			SharedPreferences mPrefs = ctx.getSharedPreferences(key, Context.MODE_PRIVATE);
		}
		mPrefs.edit().putInt(key, value).commit();
		
	}
	public static int getInt(Context ctx ,String key ,int defValue){
		if (mPrefs == null) {
			SharedPreferences mPrefs= ctx.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
		}
		return mPrefs.getInt(key, defValue);
	}
	
	
}
