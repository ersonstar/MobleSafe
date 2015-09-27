package com.iterson.mobilesafe.global;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.lang.Thread.UncaughtExceptionHandler;

import android.app.AlertDialog;
import android.app.Application;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Environment;
/**
 * 
 * @author Yang
 *
 */
public class MoblieSafeApplication extends Application {
	@Override
	public void onCreate() {
		super.onCreate();
		//设置未捕获异常的处理器
		Thread.setDefaultUncaughtExceptionHandler(new MyUncaughtExceptionHandler());
		
		
	}
	class MyUncaughtExceptionHandler implements UncaughtExceptionHandler{
		//未捕获的异常会走到该方法中
		//Exception，Error
		@Override
		public void uncaughtException(Thread thread, Throwable ex) {
			System.out.println("遇到一个未捕获的异常");
			//将错误日志写到文件中，然后在后头将文件悄悄上传到服务器，供开发者分析
			File errlog = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/errlog.log");
			PrintWriter err = null;
			try {
				err = new PrintWriter(errlog);
				ex.printStackTrace(err);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			err.close();
			ex.printStackTrace();
			AlertDialog.Builder builder =new AlertDialog.Builder(getApplicationContext());
			builder.setTitle("抱歉，发生未知错误");
			builder.setMessage("非常抱歉！\n APP遇到一个未知的错误，我们记录下了异常，为了更好的体验请上传异常。\n 我们会及时改进，谢谢支持！");
			builder.setNegativeButton("放弃上传", null);
			builder.setPositiveButton("及时解决", new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					
				}
			});
			
			
			
			//遇到异常杀死自己。闪退
			android.os.Process.killProcess(android.os.Process.myPid());
		}
		
	
		
		
	}
	
	
}
