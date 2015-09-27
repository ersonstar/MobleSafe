package com.iterson.mobilesafe.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.R.anim;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.iterson.mobilesafe.R;
import com.iterson.mobilesafe.db.dao.AntivirusDao;
import com.iterson.mobilesafe.utils.MD5Utils;
import com.iterson.mobilesafe.utils.ToastUtils;

@SuppressLint("ServiceCast")
public class AntivirusActivity extends Activity {
	private static final int SCANNING = 1;
	private static final int SCANNING_FINSH = 0;
	private ImageView ivScanning;
	private TextView tvStatus;
	private ProgressBar pbBar;
	private PackageManager pm;
	private LinearLayout llScanning;
	private ArrayList<ScanInfo> virusList;
	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			ScanInfo info = (ScanInfo) msg.obj;
			switch (msg.what) {
			case SCANNING://正在扫描
				tvStatus.setText("正在扫描：" + info.name);
				TextView view = new TextView(getApplicationContext());
				int virusSize = 0;
				if (info.isVirus) {
					view.setText("发现病毒:" + info.name);
					view.setTextColor(Color.RED);
					llScanning.addView(view, virusSize);// 把病毒排在前面
					virusSize++;
				} else {
					view.setText("扫描安全:" + info.name);
					view.setTextColor(Color.BLACK);
					if (virusSize == 0) {
						llScanning.addView(view, virusSize);
					} else {
						llScanning.addView(view, virusSize + 1);
					}
				}
				break;
				case SCANNING_FINSH: //扫描完成
				tvStatus.setText("扫描完成");
				ivScanning.clearAnimation();
				ToastUtils.showToast(getApplicationContext(), "扫描完成");
				//判断是否有病毒
				if (virusList.isEmpty()) {//空，表示没有病毒
					ToastUtils.showToast(getApplicationContext(), "你的手机很安全");
				}else {
					showAlertDialog();
				}
				
				break;

			default:
				break;
			}
			

		};
	};
	private ImageView ivLine;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_antivirus);

		ivScanning = (ImageView) findViewById(R.id.iv_scanning);
		tvStatus = (TextView) findViewById(R.id.tv_status);
		pbBar = (ProgressBar) findViewById(R.id.pb_progressBar);
		llScanning = (LinearLayout) findViewById(R.id.ll_scanning);
		
		// 设置扫描雷达动画
		RotateAnimation anim = new RotateAnimation(0, 360,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		anim.setDuration(1000);
		anim.setRepeatCount(Animation.INFINITE);// 无限循环
		anim.setInterpolator(new LinearInterpolator());// 匀速插补器
		ivScanning.startAnimation(anim);
		pm = getPackageManager();

		// 更新进度条 progress 都能在子线程更新
		new Thread() {
			private Message msg;
			public void run() {

				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				// 参1能能返回安装的包的签名信息，参2表示能返回已经卸载了的包的签名信息。/data/data下可能有遗留目录
				List<PackageInfo> installedPackages = pm
						.getInstalledPackages(PackageManager.GET_SIGNATURES
								+ PackageManager.GET_UNINSTALLED_PACKAGES);
				pbBar.setMax(installedPackages.size());// 设置进度条的总大小
				int progress = 0;// 用于进度条更新
				//用于存病毒
				virusList = new ArrayList<ScanInfo>();
				//让用户体验真正在扫描，看的爽
				Random random = new Random();
				// 遍历所有的应用程序
				for (PackageInfo packageInfo : installedPackages) {
					try {
						Thread.sleep(50+random.nextInt(50));
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					ScanInfo info = new ScanInfo();
					info.packagerName = packageInfo.packageName;
					// 对当前遍历的包进行处理
					Signature[] signatures = packageInfo.signatures;// 数组长度为1，扩展未以后预留的数组
					String signInfo = signatures[0].toCharsString();// 得到这个APP签名信息
					String name = packageInfo.applicationInfo.loadLabel(pm)
							.toString();// 得到APP的名字
					info.name = name;
					String MD5 = MD5Utils.encode(signInfo);// 转换签名信息为MD5
					// 在数据找到是否有这个MD5的数据
					String desc = AntivirusDao.findAntivirus(MD5);// 得到病毒描述，无匹配的返回的数null
					info.desc = desc;
					if (desc == null) {
						info.isVirus = false;
					} else {
						info.isVirus = true;
						virusList.add(info);
					}
					pbBar.setProgress(progress++);
					msg = Message.obtain();
					msg.what = SCANNING;
					msg.obj = info;
					mHandler.sendMessage(msg);
				}
				msg.what = SCANNING_FINSH;

			};
		}.start();

	}
	/**
	 * 发现病毒弹窗
	 */
	protected void showAlertDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("发现病毒");
		builder.setMessage("发现"+virusList.size()+"个病毒/n是否立即清理");
		builder.setPositiveButton("立即清理", new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				for (ScanInfo info : virusList) {
					Intent intent = new Intent(Intent.ACTION_DELETE);
					intent.setData(Uri.parse("package:"+info.packagerName));
					intent.addCategory(Intent.CATEGORY_DEFAULT);
					startActivity(intent);
				}
			}
		});

		builder.setNegativeButton("以后再说", null);
		builder.show();
		
	}

	class ScanInfo {
		public String name;
		public String packagerName;
		public String desc;
		public boolean isVirus;
	}

}
