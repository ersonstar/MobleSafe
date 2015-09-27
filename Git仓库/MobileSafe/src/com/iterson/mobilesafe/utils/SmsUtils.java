package com.iterson.mobilesafe.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.xmlpull.v1.XmlSerializer;

import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Xml;
import android.widget.ProgressBar;

/***
 * 操作短信的工具类
 * 
 * @author Yang
 * 
 */
public class SmsUtils {

	public static void smsBackup(Context context ,File file, SmsCallback smsCallback) {
		
		//获取短信数据库
		Cursor cursor = context.getContentResolver().query(Uri.parse("content://sms"),
				new String[] { "address", "date", "type", "body" }, null, null,
				null);
		smsCallback.preSmsBackup(cursor.getCount());//回传短信总数
		int progress = 0;
		
		XmlSerializer serializer = Xml.newSerializer();
		
		try {
			serializer.setOutput(new FileOutputStream(file), "utf-8");
			serializer.startDocument("utf-8", null);//参2传null代表xml文件头不带 standalone标签
			serializer.startTag(null, "smss");//起始标签
			//开始遍历短信库
			while(cursor.moveToNext()){
				serializer.startTag(null, "sms");
				
				//短信号码
				serializer.startTag(null, "address");
				String address = cursor.getString(cursor.getColumnIndex("address"));
				serializer.text(address);
				serializer.endTag(null, "address");
				//短信时间
				serializer.startTag(null, "date");
				String date = cursor.getString(cursor.getColumnIndex("date"));
				serializer.text(date);
				serializer.endTag(null, "date");
				//短信类型
				serializer.startTag(null, "type");
				String type = cursor.getString(cursor.getColumnIndex("type"));
				serializer.text(type);
				serializer.endTag(null, "type");
				//短信内容
				serializer.startTag(null, "body");
				String body = cursor.getString(cursor.getColumnIndex("body"));
				serializer.text(body);
				serializer.endTag(null, "body");
				
				serializer.endTag(null, "sms");
				Thread.sleep(300);
				progress++;
				smsCallback.onSmsBackup(progress);//当前进度数量
				
			}
			
			serializer.endTag(null, "smss");//结束标签
			serializer.endDocument();
			cursor.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 短信回调接口
	 * @author Yang
	 *
	 */
	public interface SmsCallback{
		/*
		 * 短信总数
		 */
		public void preSmsBackup(int total);
		
		/*
		 * 备份进度
		 */
		public void onSmsBackup(int progress);
		
	}
	
}
