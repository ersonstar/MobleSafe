package com.iterson.mobilesafe.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
/**
 * 黑名单管理数据库
 * @author Yang
 *
 */
public class BlackNumberOpenHelper extends SQLiteOpenHelper {

	public BlackNumberOpenHelper(Context context) {
		super(context, "blacknumber.db", null, 1);
	}

	/**
	 * 数据库第一次创建的时候调
	 * 
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
		// 创建黑名单表:_id ,number(电话号码),mode（拦截模式，1代表电话，2代表短信，3代表电话+短信）
		db.execSQL("create table blacknumber (_id integer primary key autoincrement, number varchar(20), mode integer)");
		
	}

	/**
	 * 数据库更新时调用，就是版本发生变化时候
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}

}
