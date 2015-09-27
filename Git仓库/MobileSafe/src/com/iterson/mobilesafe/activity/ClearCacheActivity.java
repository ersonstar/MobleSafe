package com.iterson.mobilesafe.activity;

import java.lang.reflect.Method;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageDataObserver;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.PackageStats;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.text.format.Formatter;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.iterson.mobilesafe.R;
import com.iterson.mobilesafe.utils.ToastUtils;

/**
 * 清楚缓存界面
 * 
 * @author Yang
 * 
 */
public class ClearCacheActivity extends Activity {

	private static final int SCANNING = 1;
	private static final int SCANNING_FINISH = 2;
	private static final int SCANNING_CACHE = 3;
	private static final int REFRECH_LL = 4;
	private TextView tvDesc;
	private PackageManager mPM;
	private ProgressBar pbClear;
	private List<ApplicationInfo> mAppList;
	private LinearLayout llCacheList;
	private Handler mHandler = new Handler() {
		private LinearLayout llCacheBg;

		public void handleMessage(android.os.Message msg) {
			
			switch (msg.what) {
			case SCANNING:
				String name =  (String) msg.obj;
				tvDesc.setText("正在扫描:"+name);
				break;
			case SCANNING_CACHE:
				final CacheInfo info = (CacheInfo) msg.obj;
				View view = View.inflate(getApplicationContext(), R.layout.list_cache_item, null);
				ImageView ivIcon = (ImageView) view.findViewById(R.id.iv_cache_icon);
				ImageView ivDelete = (ImageView) view.findViewById(R.id.iv_delete);
				TextView tvTitle = (TextView) view.findViewById(R.id.tv_cache_name);
				TextView tvCacheSize = (TextView) view.findViewById(R.id.tv_cache_size);
				llCacheBg = (LinearLayout) findViewById(R.id.ll_cache_bg);
				ivIcon.setImageDrawable(info.icon);
				tvTitle.setText(info.name);
				tvCacheSize.setText("缓存大小："+info.cacheSize);
				llCacheList.addView(view, 0);
				
				//单个清理
				ivDelete.setOnClickListener(new OnClickListener() {
				//如果想在应用中清理其他应用的缓存，必须自己是系统应用
					//达不到系统应用，就跳转到这个应用的相信，让用户自己清理
					@Override
					public void onClick(View v) {
						Intent intent = new Intent();
						intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
						intent.setData(Uri.parse("package:"+info.packName));
						intent.addCategory(Intent.CATEGORY_DEFAULT);
						startActivity(intent);
						
					}
				});
				break;
				case SCANNING_FINISH:
				tvDesc.setText("扫描完成");
				llCacheList.setBackgroundResource(R.drawable.rubish_1_green);
				
				break;
			
			default:
				break;
			}

		};
	};
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_clear_cache);
		tvDesc = (TextView) findViewById(R.id.tv_describe);
		pbClear = (ProgressBar) findViewById(R.id.pb_clear_progress);
		llCacheList = (LinearLayout) findViewById(R.id.ll_cache_bg);
		
		
		mPM = getPackageManager();
		
		new Thread() {
			public void run() {
				// 获取已安装和未安装APP信息
				mAppList = mPM
						.getInstalledApplications(PackageManager.GET_UNINSTALLED_PACKAGES);
				pbClear.setMax(mAppList.size());
				int progress = 0;
				
				for (ApplicationInfo info : mAppList) {
					// 使用反射得到方法
					
					try {
						Method method = mPM.getClass().getMethod("getPackageSizeInfo",
								String.class, IPackageStatsObserver.class);
						MyObserver myObserver = new MyObserver();
						method.invoke(mPM, info.packageName, myObserver);

						Message msg = Message.obtain();
						msg.obj = info.loadLabel(mPM).toString();
						msg.what = SCANNING;
						mHandler.sendMessage(msg);

					} catch (Exception e) {
						e.printStackTrace();
					}
					try {
						Thread.sleep(50);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					progress++;
					pbClear.setProgress(progress);
				}
				mHandler.sendEmptyMessage(SCANNING_FINISH);
			};
		}.start();
		
		
		

	}

	/**
	 * 清理按键
	 */
	public void clear(View v) {
		try {//请求系统尽可能多给空间，系统为了请求就会，清空缓存，来得到目的
			Method method = mPM.getClass().getMethod("freeStorageAndNotify", long.class,IPackageDataObserver.class);
			method.invoke(mPM, Long.MAX_VALUE,new IPackageDataObserver.Stub() {
				@Override
				public void onRemoveCompleted(String packageName, boolean succeeded)
						throws RemoteException {
					ToastUtils.showToast(getApplicationContext(), "缓存清理完毕");
				}
			});
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		//移除所有的linealayout里面的view，然后添加一个空view。达到刷新目的
		llCacheList.removeAllViews();
		TextView view = new TextView(getApplicationContext());
		view.setText("  ");
		llCacheList.addView(view);
		
	}

	/**
		权限android.permission.CLEAR_APP_CACHE
	 * @author Yang
	 *
	 */
	class MyObserver extends IPackageStatsObserver.Stub {

		@Override
		public void onGetStatsCompleted(PackageStats pStats, boolean succeeded)
				throws RemoteException {
			long cacheSize = pStats.cacheSize;// 缓存大小
			long codeSize = pStats.codeSize;// 安装包大小
			long dataSize = pStats.dataSize;// 数据大小
			if (cacheSize>0) {
				CacheInfo info = new CacheInfo();
				String packageName = pStats.packageName;
				try {
					ApplicationInfo appInfo = mPM
							.getApplicationInfo(packageName, 0);
					info.name = appInfo.loadLabel(mPM).toString();
					info.icon = appInfo.loadIcon(mPM);
					info.cacheSize = Formatter.formatFileSize(
							getApplicationContext(), cacheSize);
					info.packName = appInfo.packageName;
				} catch (NameNotFoundException e) {
					e.printStackTrace();
				}
				Message msg = Message.obtain();
				msg.obj = info;
				msg.what = SCANNING_CACHE;
				mHandler.sendMessage(msg);
			}
			

		}

	}

	public class CacheInfo {
		public String name;
		public Drawable icon;
		public String cacheSize;
		public String packName;

	}

}
