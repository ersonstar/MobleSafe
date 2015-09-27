package com.iterson.mobilesafe.db.dao;

import com.iterson.mobilesafe.R.layout;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * 归属地查询的数据库封装
 * 
 * @author Yang
 * 
 */
public class AddressDao {

	private static final String PATH = "data/data/com.iterson.mobilesafe/files/address.db";

	/**
	 * 根据电话号码查询归属地
	 * 
	 * @param phone
	 */

	public static String getAddress(String phone) {
		String location = "未知号码";
		SQLiteDatabase database = SQLiteDatabase.openDatabase(PATH, null,
				SQLiteDatabase.OPEN_READONLY);// 注意：数据必须在data/data目录下否则无法读取

		if (phone.length() == 11) {
			Cursor cursor = database
					.rawQuery(
							"select location from data2 where id=(select outkey from data1 where id=?)",
							new String[] { phone.substring(0, 7) });
			System.out.println(new String[] { phone.substring(0, 7)});
			if (cursor.moveToFirst()) {
				location = cursor.getString(0);
			}
			cursor.close();
		}

		return location;
	}

}
