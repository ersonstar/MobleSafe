package com.iterson.mobilesafe.service;

import com.iterson.mobilesafe.domain.ProcessInfo;
import com.iterson.mobilesafe.engine.ProcessInfoProvider;
import com.iterson.mobilesafe.utils.PrefUtils;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
/**
 * 锁屏清理服务
 * @author Yang
 *
 */
public class AppLockService extends Service {

	private InnerScreenOffReceiver mReceiver;

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void onCreate() {
		//监听屏幕关闭的广播
		mReceiver = new InnerScreenOffReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(Intent.ACTION_SCREEN_OFF);
		registerReceiver(mReceiver, filter);
		
		
		
		super.onCreate();
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		unregisterReceiver(mReceiver);
		mReceiver = null;
	}
	
	class InnerScreenOffReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			//杀死后台所有进程
			ProcessInfoProvider.killAll(context);
		}
	}

}
