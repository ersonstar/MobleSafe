package com.iterson.mobilesafe.db.dao;

import java.util.ArrayList;

import android.R.integer;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.iterson.mobilesafe.db.BlackNumberOpenHelper;

/**
 * 黑名单表增删改查
 * 
 * @author Yang
 * 
 */
public class BlackNumberDao {
	/**
	 * 单例模式 逼格更高的一种做法 private BlackNumberDao(){} private static BlackNumber
	 * getInstance(){ return InnerInstance.sInstance;} private static class
	 * InnerInstance(){ private static BlackNumberDao sInstance = new
	 * BlackNumberDao(); }
	 * 
	 */
	private static BlackNumberDao sInstance = null;
	private BlackNumberOpenHelper mHelper;
	private int mode;

	private BlackNumberDao(Context context) {
		mHelper = new BlackNumberOpenHelper(context);
		mHelper.getReadableDatabase();
	};

	public static BlackNumberDao getInstance(Context context) {
		if (sInstance == null) {
			synchronized (BlackNumberDao.class) {
				if (sInstance == null) {
					sInstance = new BlackNumberDao(context);
				}
			}
		}
		return sInstance;
	}

	public void add(String number, int mode) {
		SQLiteDatabase db = mHelper.getWritableDatabase();// 得到数据库
		ContentValues values = new ContentValues();
		values.put("mode", mode);
		values.put("number", number);
		db.insert("blacknumber", null, values);// 第三个值为ContentValues，上面new出来
		db.close();

	}

	public void delete(String number) {
		SQLiteDatabase db = mHelper.getWritableDatabase();
		db.delete("blacknumber", "number = ?", new String[] { number });
		db.close();
	}

	public void update(String number, int mode) {
		SQLiteDatabase db = mHelper.getWritableDatabase();// 得到数据库
		ContentValues values = new ContentValues();
		values.put("mode", mode);
		db.update("blacknumber", values, "number=?", new String[] { number });
		db.close();
	}

	public boolean find(String number) {
		SQLiteDatabase db = mHelper.getWritableDatabase();// 得到数据库
		Cursor cursor = db.query("blacknumber",
				new String[] { "number", "mode" }, "number=?",
				new String[] { number }, null, null, null);
		boolean exist = false;
		if (cursor.moveToFirst()) {
			exist = true;
		}
		cursor.close();
		db.close();
		return exist;

	}

	/**
	 * 返回电话号码拦截模式
	 * 
	 * @param number
	 * @return
	 */
	public int findMode(String number) {
		SQLiteDatabase db = mHelper.getWritableDatabase();// 得到数据库
		Cursor cursor = db.query("blacknumber", new String[] { "mode" },
				"number=?", new String[] { number }, null, null, null);
		int mode = -1;
		if (cursor.moveToFirst()) {
			mode = cursor.getInt(0);
		}
		cursor.close();
		db.close();
		return mode;

	}

	/**
	 * 查找所有
	 * 
	 * @return
	 */
	public ArrayList<BlackNumberInfo> findAll() {
		SQLiteDatabase db = mHelper.getWritableDatabase();
		Cursor cursor = db
				.query("blacknumber", new String[] { "number", "mode" }, null,
						null, null, null, null);
		ArrayList<BlackNumberInfo> info = new ArrayList<BlackNumberInfo>();
		while (cursor.moveToNext()) {
			String number = cursor.getString(0);
			int mode = cursor.getInt(1);
			BlackNumberInfo blackNumberInfo = new BlackNumberInfo(number, mode);
			info.add(blackNumberInfo);
		}
		cursor.close();
		db.close();
		return info;
	}

	/**
	 * 查找一页
	 * 
	 * @return
	 */
	public ArrayList<BlackNumberInfo> findPart(int startIndex) {
		SQLiteDatabase db = mHelper.getWritableDatabase();
		Cursor cursor = db.rawQuery(
				"select number, mode from blacknumber order by _id desc limit ? ,20",
				new String[] { startIndex + "" });//按照id逆序排列
		ArrayList<BlackNumberInfo> info = new ArrayList<BlackNumberInfo>();
		while (cursor.moveToNext()) {
			String number = cursor.getString(0);
			int mode = cursor.getInt(1);
			BlackNumberInfo blackNumberInfo = new BlackNumberInfo(number, mode);
			info.add(blackNumberInfo);
		}
		cursor.close();
		db.close();
		return info;
	}
	/**
	 * 得到数据库总大小
	 * @return
	 */
	public int getTotalCount(){
		int total = 0;
		SQLiteDatabase db = mHelper.getWritableDatabase();
		Cursor cursor = db.rawQuery(
				"select count(*) from blacknumber",
				null);
		if (cursor.moveToFirst()) {
			total =cursor.getInt(0);
		}
		cursor.close();
		db.close();
		return total;
	}

	public static class BlackNumberInfo {
		public String number;
		public int mode;

		public BlackNumberInfo(String number, int mode) {
			this.number = number;
			this.mode = mode;
		}
	}
}
