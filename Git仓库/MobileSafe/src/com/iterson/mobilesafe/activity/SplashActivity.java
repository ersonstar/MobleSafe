package com.iterson.mobilesafe.activity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import com.iterson.mobilesafe.R;
import com.iterson.mobilesafe.utils.PrefUtils;
import com.iterson.mobilesafe.utils.StreamUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;

import android.R.integer;
import android.R.string;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class SplashActivity extends Activity {

	private static final int CODE_UPDATE_DIALOG = 0;
	private static final int CODE_URL_ERROR = 1;
	private static final int CODE_NETWORK_ERROR = 2;
	private static final int CODE_JSON_ERROR = 3;
	private static final int CODE_ENTER_HOME = 4;
	private static final int CODE_UPDATE_DILOG = 5;
	private static final int CODE_UPDATE_ALOG = 7;

	private TextView tvVersion;
	private HttpURLConnection conn;
	private String mVersionName;
	private int mVersionCode;
	private String mDescription;
	private String mDownloadUrl;

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 0:
				showUpdataDialog();
				break;
			case 1:
				Toast.makeText(getApplicationContext(), "网络连接连接错误", 0).show();
				enterHome();
				break;

			case 2:
				Toast.makeText(getApplicationContext(), "网络错误", 0).show();
				enterHome();
				break;

			case 3:
				Toast.makeText(getApplicationContext(), "数据解析错误", 0).show();
				enterHome();
				break;
			case 4:
				enterHome();
				break;

			}
		};
	};
	private TextView tvProgress;
	private FileOutputStream out;
	private InputStream in;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		tvVersion = (TextView) findViewById(R.id.tv_version);
		tvProgress = (TextView) findViewById(R.id.tv_progress);
		// 得到版本名
		getVersionName();
		// 动态设置版本名
		tvVersion.setText("版本名：" + getVersionName());

		// 调拷贝归属地查询数据
		copyDb("address.db");
		// 拷贝常用号码拷贝数据库
		copyDb("commonnum.db");
		//拷贝病毒数据库
		copyDb("antivirus.db");
		
		// 开启渐变动画
		RelativeLayout rlRoot = (RelativeLayout) findViewById(R.id.rl_root);
		AlphaAnimation anim = new AlphaAnimation(0.3f, 1);
		anim.setDuration(2000);
		rlRoot.startAnimation(anim);

		

		// 检查版本,
		boolean autoUpdate = PrefUtils.getBoolean(this, "auto_update", false);
		if (autoUpdate) {// 判断设置选项是否为需要自动更新
			checkVersion();
		} else {
			mHandler.sendEmptyMessageDelayed(CODE_ENTER_HOME, 2000);

		}
		
		//创建快捷方式
		createShortcut();

	}

	/**
	 * 检查版本
	 */
	private void checkVersion() {

		new Thread() {
			public void run() {
				Message msg = Message.obtain();
				long startTime = System.currentTimeMillis();// 得到当前时间
				try {

					URL url = new URL("http://121.42.203.145/update.json");
					conn = (HttpURLConnection) url.openConnection();
					conn.setRequestMethod("GET");// 设置请求方法
					conn.setConnectTimeout(2000);// 连接超时
					conn.setReadTimeout(2000);// 读取超时
					int responseCode = conn.getResponseCode();
					if (responseCode == 200) {// 正常
						System.out.println("连接成功");
						InputStream inputStream = conn.getInputStream();
						String result = StreamUtils.streamToString(inputStream);// 用转换工具把流转换为字符串
						System.out.println(result);
						// 解析JSON
						try {
							JSONObject joson = new JSONObject(result);
							mVersionName = joson.getString("versionName");
							mVersionCode = joson.getInt("versionCode");
							mDescription = joson.getString("description");
							mDownloadUrl = joson.getString("downloadUrl");
							System.out.println(mDescription);

							// 判断有没有版本更新
							if (mVersionCode > getVersionCode()) {// 有更新
								// 弹出升级对话框
								msg.what = CODE_UPDATE_DIALOG;
							} else {
								// 跳转主页面
								msg.what = CODE_ENTER_HOME;
							}
						} catch (JSONException e) {
							// JSON解析异常
							e.printStackTrace();
							msg.what = CODE_JSON_ERROR;
						}
					}

				} catch (MalformedURLException e) {
					// url错误
					System.out.println("url error！！！");
					msg.what = CODE_URL_ERROR;

					e.printStackTrace();
				} catch (IOException e) {
					System.out.println("网络 error！！！");
					// 网络异常
					mHandler.sendEmptyMessage(CODE_NETWORK_ERROR);
					e.printStackTrace();
				} finally {
					if (conn != null) {
						conn.disconnect();// 关闭网络连接
					}
					long endTime = System.currentTimeMillis();// 结束时间
					long timeUsed = endTime - startTime;// 调用网络用的时间

					// 强制让闪屏页面停留2秒
					try {
						Thread.sleep(2000 - timeUsed); // 让闪屏停留2秒钟
					} catch (Exception e) {
						e.printStackTrace();
					}
					mHandler.sendMessage(msg);// 发送消息
				}
			}
		}.start();

	}

	/**
	 * 获取版本名称
	 * 
	 * @return 当前版本
	 */
	private String getVersionName() {
		PackageManager pm = this.getPackageManager();// 获取包管理器
		try {
			PackageInfo packageInfo = pm.getPackageInfo(getPackageName(), 0);// 获取当前应用信息
			int versionCode = packageInfo.versionCode;// 版本号
			String versionName = packageInfo.versionName;// 当前版本名
			System.out.println("版本号:" + versionCode + "版本名" + versionName);
			return versionName;

		} catch (NameNotFoundException e) {
			// 没有找到包名的异常
			e.printStackTrace();
		}

		return "";
	}

	/**
	 * 获取版本号
	 * 
	 * @return 版本号
	 */
	private int getVersionCode() {
		PackageManager pm = this.getPackageManager();// 获取包管理器
		try {
			PackageInfo packageInfo = pm.getPackageInfo(getPackageName(), 0);// 获取当前应用信息
			int versionCode = packageInfo.versionCode;// 版本号
			String versionName = packageInfo.versionName;// 当前版本名
			System.out.println("版本号:" + versionCode + "版本名" + versionName);
			return versionCode;

		} catch (NameNotFoundException e) {
			// 没有找到包名的异常
			e.printStackTrace();
		}
		return -1;
	}

	/**
	 * 弹出升级对话框
	 */
	private void showUpdataDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("最新版本:" + mVersionName);
		builder.setMessage(mDescription);
		builder.setPositiveButton("立即更新",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						System.out.println("开始更新");
						downloadAPK();

					}

				});
		builder.setNegativeButton("以后再更新",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						System.out.println("跳转主页面");
						enterHome();

					}
				});

		/**
		 * 
		 * 监听返回键，直接跳转主页面
		 */
		builder.setOnCancelListener(new OnCancelListener() {

			@Override
			public void onCancel(DialogInterface dialog) {
				enterHome();
			}
		});
		builder.show();

	}

	/**
	 * 跳转主页面
	 */
	private void enterHome() {
		Intent intent = new Intent(this, MainAcitivy.class);
		startActivity(intent);
		finish();
	}

	/**
	 * 下载APK
	 */
	private void downloadAPK() {
		HttpUtils utils = new HttpUtils();

		// if 判断是否存在sdCard
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			// 文件路径
			tvProgress.setVisibility(View.VISIBLE);// 显示下载进度
			String path = Environment.getExternalStorageDirectory()
					.getAbsolutePath() + "/mobilesafe.apk";
			utils.download(mDownloadUrl, path, new RequestCallBack<File>() {

				/**
				 * 下载进度 total总文件大小 current 已下载大小 isuploading上传
				 */
				@Override
				public void onLoading(long total, long current,
						boolean isUploading) {
					super.onLoading(total, current, isUploading);
					int percent = (int) (current * 100 / total);
					System.out.println("下载进度" + percent + "%");
					tvProgress.setText("下载进度：" + percent + "%");

				}

				/**
				 * 文件下载成功
				 */
				@Override
				public void onSuccess(ResponseInfo<File> responseInfo) {
					String filePath = responseInfo.result.getPath();
					System.out.println("下载成功" + filePath);
					// 安装APK
					installApk(responseInfo.result);
				}

				/**
				 * 文件下载失败
				 */
				@Override
				public void onFailure(HttpException error, String msg) {
					error.printStackTrace();
					Toast.makeText(getApplicationContext(), msg, 1).show();
				}
			});
		} else {
			Toast.makeText(this, "sd卡不存在", 1).show();
			return;
		}
	}

	/**
	 * 安装APK
	 */
	private void installApk(File file) {
		Intent intent = new Intent();
		intent.setAction(Intent.ACTION_VIEW);
		intent.addCategory(Intent.CATEGORY_DEFAULT);
		intent.setDataAndType(Uri.fromFile(file),
				"application/vnd.android.package-archive");
		startActivityForResult(intent, 0);

	}

	/**
	 * 监听安装界面 取消键
	 * 
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		enterHome();
	}

	/**
	 * 拷贝数据库， 因为数据库必须在data/data下才能读取 拷贝电话查询的数据库
	 */
	private void copyDb(String dbName) {
		File file = new File(getFilesDir(), dbName);
		System.out.println(getFilesDir());// 拿的是data/data路径

		if (file.exists()) {
			System.out.println("数据库" + dbName + "已经存在");
			return;
		} else {
			try {
				in = getAssets().open(dbName);
				out = new FileOutputStream(file);

				int len = 0;
				byte[] buffer = new byte[1024];
				while ((len = in.read(buffer)) != -1) {
					out.write(buffer, 0, len);
				}

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				try {
					in.close();
					out.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				System.out.println("拷贝dbname成功");
			}
		}
	}
	/**
	 * 创建快捷方式
	 */
	public void createShortcut() {//com.android.launcher.permission.INSTALL_SHORTCUT  权限
		boolean isCreated = PrefUtils.getBoolean(this, "is_Shortcut_created", false);
		
		if (!isCreated) {
			Intent intent = new Intent(
					"com.android.launcher.action.INSTALL_SHORTCUT");
			// 设置快捷方式图标
			intent.putExtra(Intent.EXTRA_SHORTCUT_ICON, BitmapFactory
					.decodeResource(getResources(), R.drawable.ic_launcher));
			// 设置快捷方式名称
			intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, "手机卫士");

			Intent actionIntent = new Intent();
			actionIntent.setAction("com.iterson.moblesafe.MAIN");
			actionIntent.addCategory(Intent.CATEGORY_DEFAULT);// 必须配一个Intent.CATEGORY_DEFAULT
																// 清单文件<category
																// android:name="android.intent.category.DEFAULT"
																// />
			// 给它添加自己的intent动作
			intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, actionIntent);
			sendBroadcast(intent);
			
			PrefUtils.setBoolean(this, "is_Shortcut_created", true);
		}

	}
}
