package com.iterson.mobilesafe.activity;

import java.io.File;

import com.iterson.mobilesafe.R;
import com.iterson.mobilesafe.utils.SmsUtils;
import com.iterson.mobilesafe.utils.SmsUtils.SmsCallback;
import com.iterson.mobilesafe.utils.ToastUtils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;

public class AtoolsActivity extends Activity {
	private ProgressDialog mProgrss;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_atools);

	}
	/**
	 * 软件锁
	 */	
	public void appLock(View v){
		startActivity(new Intent(this,AppLockActivity.class));
		
	}
	/**
	 * 查询地址
	 * 
	 * @param v
	 */
	public void addressQuery(View v) {
		startActivity(new Intent(AtoolsActivity.this,
				AddressQueryActivity.class));
	}
	/**
	 * 常用号码查询
	 * 
	 * @param v
	 */
	public void  commonNumberQuery(View v){
		
		startActivity(new Intent(this , CommonNumberActivity.class));
		
	}
	/**
	 * 短信备份
	 * 
	 * @param v
	 */
	public void smsBackup(View v) {
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {// 如果sd卡被挂载
			mProgrss = new ProgressDialog(this);//可以在子线程更新
			mProgrss.setMessage("正在备份短信");
			mProgrss.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);// 水平方向进度条，有进度显示
			mProgrss.show();
			
			//备份比较耗时，建一个子线程
			new Thread() {
				public void run() {
					// 备份文件的本地路径
					String path = Environment.getExternalStorageDirectory()
							.getAbsoluteFile() + "/smsBackup.xml";
					File file = new File(path);
					//创建一个mSmsCallback 并且实现 SmsCallback接口为实现的方法
					mSmsCallback mSmsCallback = new mSmsCallback();
					SmsUtils.smsBackup(AtoolsActivity.this, file,mSmsCallback);
					mProgrss.dismiss();
					
				};
			}.start();
			
			
			
		} else {// 如果没有Sd卡，将备份存在内存中
			ToastUtils.showToast(this, "SD卡无");

		}

	}
	/**
	 * 内部类，用于实现方法
	 * @author Yang
	 *
	 */
	class mSmsCallback implements SmsCallback{

		@Override
		public void preSmsBackup(int total) {
			mProgrss.setMax(total);
			
		}

		@Override
		public void onSmsBackup(int progress) {
			mProgrss.setProgress(progress);
		}
		
	}
}
