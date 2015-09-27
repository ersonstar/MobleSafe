package com.iterson.mobilesafe.service;

import java.util.ArrayList;
import java.util.List;

import com.iterson.mobilesafe.activity.EnterPWDActivity;
import com.iterson.mobilesafe.db.dao.AppLockDao;

import android.app.ActivityManager;
import android.app.ActivityManager.AppTask;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.ActivityManager.TaskDescription;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;

/**
 * 权限android.permission.GET_TASKS
 * 
 * @author Yang
 * 
 */
public class WatchDogService extends Service {

	private boolean isStart = true;
	private ActivityManager am;
	private AppLockDao mDao;
	private InnerReceiver mReceiver;
	private String mSkipPackageName;
	private ArrayList<String> mLockList;
	private InnerContentObserver mObserver;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		isStart = true;
		am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		mDao = AppLockDao.getInstance(getApplicationContext());
		mLockList = mDao.findAll();//调出所有数据库
		
			new Thread() {
				@SuppressWarnings("deprecation")
				public void run() {
					while (isStart) {
					List<RunningTaskInfo> runningTasks = am.getRunningTasks(1);
					ComponentName componentName = runningTasks.get(0).topActivity;// 得到栈顶的activity
					String packageName = componentName.getPackageName();
					if (mLockList.contains(packageName)) {//判断mlocklist是否包含packagename
						if (!packageName.equals(mSkipPackageName)) {//如果这个包名不是当前已经输入正确密码的包
							Intent intent = new Intent();
							intent.putExtra("packageName", packageName);// 把包名传过去
							intent.setClass(getApplicationContext(),
									EnterPWDActivity.class);
							intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);//从service启动activity需要加这个flag
							startActivity(intent);
						}
							
						}
					}try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
			};
		}.start();
		//注册广播
		mReceiver = new InnerReceiver();
		IntentFilter filter =new IntentFilter();
		filter.addAction("com.iterson.mobilesafe.SKIP_CHECK");
		registerReceiver(mReceiver, filter);
		//内容观察者
		mObserver = new InnerContentObserver(new Handler());
		getContentResolver().registerContentObserver(Uri.parse("content://com.iterson.mobilesafe.appunlock.change"), true, mObserver);
		
		super.onCreate();
	}
	
	class InnerReceiver extends BroadcastReceiver{
		@Override
		public void onReceive(Context context, Intent intent) {
			mSkipPackageName = intent.getStringExtra("packageName");
		}
	}
	
	class InnerContentObserver extends ContentObserver{

		public InnerContentObserver(Handler handler) {
			super(handler);
		}
		@Override
		public void onChange(boolean selfChange) {
			super.onChange(selfChange);
			//当数据发生改变时，刷新一次findAll的数据
			mLockList=mDao.findAll();
			System.out.println("数据发生变化了");
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		isStart =false;
		unregisterReceiver(mReceiver);
		mReceiver=null;
		//反注册观察者
		getContentResolver().unregisterContentObserver(mObserver);
		mReceiver= null;
	}
	

}
