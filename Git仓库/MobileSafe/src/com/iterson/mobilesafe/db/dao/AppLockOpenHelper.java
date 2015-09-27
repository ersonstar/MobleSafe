package com.iterson.mobilesafe.db.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 黑名单管理数据库
 * 
 * @author Kevin
 * 
 */
public class AppLockOpenHelper extends SQLiteOpenHelper {

	public AppLockOpenHelper(Context ctx) {
		super(ctx, "appunlock.db", null, 1);
	}

	/**
	 * 数据库第一次创建时调用
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
		// 创建黑名单表, 字段: _id, package(已加锁的包名)
		db.execSQL("create table appunlock (_id integer primary key autoincrement, package varchar(30))");
	}

	/**
	 * 数据库更新时, 也就是version发生变化时,会调用此方法
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

}
