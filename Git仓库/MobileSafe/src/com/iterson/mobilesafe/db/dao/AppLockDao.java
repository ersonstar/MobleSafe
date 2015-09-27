package com.iterson.mobilesafe.db.dao;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.iterson.mobilesafe.db.dao.AppLockOpenHelper;;
/**
 * 程序锁增删改查
 * 
 * @author Yang
 * 
 */
public class AppLockDao {

	private static AppLockDao sInstance = null;
	private AppLockOpenHelper mHelper;

	private Context mContext;

	private AppLockDao(Context ctx) {
		mHelper = new AppLockOpenHelper(ctx);
		mContext = ctx;
	}

	public static AppLockDao getInstance(Context ctx) {
		if (sInstance == null) {
			synchronized (AppLockDao.class) {
				if (sInstance == null) {
					sInstance = new AppLockDao(ctx);
				}
			}
		}

		return sInstance;
	}

	public void add(String packageName) {
		SQLiteDatabase db = mHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("package", packageName);
		db.insert("appunlock", null, values);
		db.close();

		// 通知观察者,数据发生变化了
		mContext.getContentResolver().notifyChange(
				Uri.parse("content://com.iterson.mobilesafe.appunlock.change"),
				null);

	}

	public void delete(String packageName) {
		SQLiteDatabase db = mHelper.getWritableDatabase();
		db.delete("appunlock", "package = ?", new String[] { packageName });
		db.close();

		// 通知观察者,数据发生变化了
		mContext.getContentResolver().notifyChange(
				Uri.parse("content://com.iterson.mobilesafe.appunlock.change"),
				null);
	}

	public boolean find(String packageName) {
		SQLiteDatabase db = mHelper.getWritableDatabase();
		Cursor cursor = db.query("appunlock", new String[] { "package" },
				"package=?", new String[] { packageName }, null, null, null);

		boolean exist = false;

		if (cursor.moveToFirst()) {
			exist = true;
		}

		cursor.close();
		db.close();

		return exist;
	}

	/**
	 * 查询所有程序锁记录
	 * 
	 * @return
	 */
	public ArrayList<String> findAll() {
		SQLiteDatabase db = mHelper.getWritableDatabase();
		Cursor cursor = db.query("appunlock", new String[] { "package" }, null,
				null, null, null, null);

		ArrayList<String> list = new ArrayList<String>();
		while (cursor.moveToNext()) {
			String packageName = cursor.getString(0);
			list.add(packageName);
		}

		cursor.close();
		db.close();

		return list;
	}
}
