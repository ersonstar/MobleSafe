package com.iterson.mobilesafe.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
/**
 * 上传Error 日志文件
 * @author Yang
 *
 */
public class UnloadErrFileService extends Service {

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	@Override
	public void onCreate() {
		
		
		
		super.onCreate();
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
	}

}
