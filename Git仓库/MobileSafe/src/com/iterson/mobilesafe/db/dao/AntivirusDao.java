package com.iterson.mobilesafe.db.dao;

import java.util.ArrayList;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * 读取病毒数据库
 * 
 * @author Yang
 * 
 */
public class AntivirusDao extends Activity {
	 private static final String PAHT = "data/data/com.iterson.mobilesafe/files/antivirus.db";

	 public static String findAntivirus(String MD5){
		 SQLiteDatabase sb = SQLiteDatabase.openDatabase(PAHT, null, SQLiteDatabase.OPEN_READONLY);
		 Cursor cursor = sb.rawQuery("select desc from datable where md5=?", new String[] { MD5 });
		 String desc = null;
		if (cursor.moveToFirst()) {
			desc = cursor.getString(0);
		} 
		cursor.close();
		sb.close();
		return desc;
	 }
	
	

	 public static ArrayList<AntivirusInfo> findAllAntivirus(){
		 SQLiteDatabase sb = SQLiteDatabase.openDatabase(PAHT, null, SQLiteDatabase.OPEN_READONLY);
		 Cursor cursor = sb.rawQuery("select desc,md5 from datable",null);
		 AntivirusInfo Info = new AntivirusInfo();
		 ArrayList<AntivirusInfo> list = new ArrayList<AntivirusInfo>();
		 while(cursor.moveToNext()){
			 Info.desc = cursor.getString(0);
			 Info.MD5 = cursor.getString(1);
			 list.add(Info);
		 }
		cursor.close();
		sb.close();
		return list;
	 }
	 
	public static class AntivirusInfo{
		public String MD5;
		public String desc;
		 
	 }
}
