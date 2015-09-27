package com.iterson.mobilesafe.db.dao;

import java.util.ArrayList;
import java.util.jar.Attributes.Name;

import com.iterson.mobilesafe.R.layout;

import android.R.array;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * 常用号码的数据库封装
 * 
 * @author Yang
 * 
 */
public class CommonNumberDao {

	private static final String PATH = "data/data/com.iterson.mobilesafe/files/commonnum.db";

	/**
	 * 根据电话号码查询归属地
	 * 
	 * @param phone
	 * @return
	 */

	public static ArrayList<CommonNumberGroup> getCommonNmberGroups() {
		SQLiteDatabase db = SQLiteDatabase.openDatabase(PATH, null,
				SQLiteDatabase.OPEN_READONLY);// 注意：数据必须在data/data目录下否则无法读取

		ArrayList<CommonNumberGroup> list = new ArrayList<CommonNumberGroup>();
		Cursor cursor = db.rawQuery("select name,idx from classlist", null);
		while (cursor.moveToNext()) {
			String name = cursor.getString(0);
			String idx = cursor.getString(1);
			CommonNumberGroup group = new CommonNumberGroup();
			group.idx = idx;
			group.name = name;
			group.child = getCommonNumberChilds(db, idx);//调用查询子数据库表
			
			list.add(group);
		}
		cursor.close();
		db.close();
		return list;
	}
	/**
	 * 查询子数据库表，
	 */
	private static ArrayList<CommonNumberChild> getCommonNumberChilds(SQLiteDatabase db,String idx){
		Cursor cursor = db.rawQuery("select number,name from table" + idx, null);
		ArrayList<CommonNumberChild> list = new ArrayList<CommonNumberChild>() ;
		while(cursor.moveToNext()){
			String number = cursor.getString(0);
			String name = cursor.getString(1);
			CommonNumberChild child = new CommonNumberChild();
			child.name =name;
			child.number = number;
			
			list.add(child);
		}
		cursor.close();
//		db.close();此处不关闭，调用此方法后，调用者关闭
		return list;
		
	}

	/**
	  * 对孩子表信息的封装
	 */
	public static class CommonNumberChild {
		public String name;
		public String number;
	}
	/**
	 * * 对组信息的封装
	 */
	public static class CommonNumberGroup{
		public String name;
		public String idx;
		public ArrayList<CommonNumberChild> child;
	}
	
	

}
