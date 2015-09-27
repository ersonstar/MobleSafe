package com.iterson.mobilesafe.activity;

import java.util.ArrayList;

import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ClipData.Item;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StatFs;
import android.text.format.Formatter;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.iterson.mobilesafe.R;
import com.iterson.mobilesafe.domain.AppInfo;
import com.iterson.mobilesafe.engine.AppInfoProvider;
import com.iterson.mobilesafe.utils.ToastUtils;

/**
 * 软件管理
 * 
 * @author Yang
 * 
 */

public class AppManagerActivity extends Activity implements OnClickListener {
	private TextView tvMemAvail;
	private TextView tVSdcardAvail;
	private ListView lvList;
	private TextView tvHeader;
	private ArrayList<AppInfo> mList;// 应用列表集合
	private ArrayList<AppInfo> mUserList;// 用户应用列表集合
	private ArrayList<AppInfo> mSystemList;// 系统应用列表集合
	private AppInfoAdapter mAdapter;
	private LinearLayout llLoading;// 加载
	private AppInfo mCurrentInfo;
	private Handler mHandler = new Handler() {

		public void handleMessage(android.os.Message msg) {

			mAdapter = new AppInfoAdapter();
			lvList.setAdapter(mAdapter);
			llLoading.setVisibility(View.GONE);// 隐藏加载界面
			tvHeader.setText("用户应用:" + mUserList.size());// 帧布局，

		};
	};
	private PopupWindow mPopupWindow;
	private AnimationSet mSet;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_appmanager);

		tvMemAvail = (TextView) findViewById(R.id.tv_mem_avail);
		tVSdcardAvail = (TextView) findViewById(R.id.tv_sdcard_avail);
		lvList = (ListView) findViewById(R.id.lv_list);
		llLoading = (LinearLayout) findViewById(R.id.ll_loading);
		tvHeader = (TextView) findViewById(R.id.tv_header);

		tvMemAvail.setText("内存可用："
				+ getAvailSpace(Environment.getDataDirectory()
						.getAbsolutePath()));// 把内存(手机自带的存储空间)根路径传递进去
		tVSdcardAvail.setText("SD卡可用："
				+ getAvailSpace(Environment.getExternalStorageDirectory()
						.getAbsolutePath()));// 把sd卡路径传递进去
		
				// 给lvList 设置监听
					lvList.setOnScrollListener(new OnScrollListener() {

						@Override
						public void onScrollStateChanged(AbsListView view,
								int scrollState) {

						}

						// 监听滑动事件
						@Override
						public void onScroll(AbsListView view, int firstVisibleItem,
								int visibleItemCount, int totalItemCount) {
							if (mUserList!=null && mSystemList != null) {
								if (firstVisibleItem >= mUserList.size() + 1) {
								tvHeader.setText("系统应用:" + mSystemList.size());
							} else {
								tvHeader.setText("用户应用:" + mUserList.size());
							}
							}
						}
					});

		//lvList的点击监听
		lvList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				AppInfo item = mAdapter.getItem(position);
				if (item != null) {
					showPopupWindow(view);
					mCurrentInfo = item;
				}
				
				
			
			}

		});

		initAppInfo();

	}

	/**
	 * 弹出popupwindow
	 */
	protected void showPopupWindow(View itemView) {
		View view = View.inflate(this, R.layout.popup_appinfo, null);
		if (mPopupWindow == null) {// 判断为空 可以节约性能
			mPopupWindow = new PopupWindow(view, LayoutParams.WRAP_CONTENT,
					LayoutParams.WRAP_CONTENT, true);
			mPopupWindow.setBackgroundDrawable(new ColorDrawable());// 必须设置一个背景，这样的话点击返回才可以消失
			// 初始化textview
			TextView tvUninstall = (TextView) view
					.findViewById(R.id.tv_uninstall);
			TextView tvShare = (TextView) view.findViewById(R.id.tv_share);
			TextView tvLaunch = (TextView) view.findViewById(R.id.tv_launch);
			tvLaunch.setOnClickListener(this);
			tvShare.setOnClickListener(this);
			tvUninstall.setOnClickListener(this);
			// 初始化popupwindow
			mPopupWindow = new PopupWindow(view, LayoutParams.WRAP_CONTENT,
					LayoutParams.WRAP_CONTENT, true);
			mPopupWindow.setBackgroundDrawable(new ColorDrawable());// 必须设置一个背景，这样的话点击返回才可以消失
			ScaleAnimation animScale = new ScaleAnimation(0, 1, 0, 1,
					Animation.RELATIVE_TO_SELF, 0.5f,
					Animation.RELATIVE_TO_SELF, 0.5f);
			animScale.setDuration(200);
			AlphaAnimation animAlph = new AlphaAnimation(0, 1);
			animAlph.setDuration(200);
			mSet = new AnimationSet(false);
			mSet.addAnimation(animAlph);
			mSet.addAnimation(animScale);

		}

		mPopupWindow.showAsDropDown(itemView, 100, -itemView.getHeight());
		view.startAnimation(mSet);

	}

	/**
	 * Adapter
	 * 
	 * @author Yang
	 * 
	 */
	class AppInfoAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return mUserList.size() + mSystemList.size() + 2;// 加2表示有一个用户和系统的头列表
		}

		@Override
		public AppInfo getItem(int position) {
			if (position == 0 || position == mUserList.size() + 1) {
				return null;
			} else if (position <= mUserList.size()) {
				return mUserList.get(position - 1);// 减去一个头的
			} else {
				return mSystemList.get(position - mUserList.size() - 2);
			}
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		/*
		 * 根据位置返回当前item的类型
		 */
		@Override
		public int getItemViewType(int position) {
			if (position == 0 || position == mUserList.size() + 1) {
				return 0;// 头布局
			} else {
				return 1;// 普通布局
			}
		}

		/**
		 * 返回总共有多少种类型, 该方法必须实现, 系统会根据该方法判断需要缓存几个convertView
		 */
		@Override
		public int getViewTypeCount() {
			return 2;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			int type = getItemViewType(position);
			// 系统会根据几种类型，缓存几个convertview

			switch (type) {
			case 0: {
				HeaderViewHolder headerHolder = null;
				if (convertView == null) {
					convertView = View.inflate(AppManagerActivity.this,
							R.layout.list_appinfo_header, null);
					headerHolder = new HeaderViewHolder();
					headerHolder.tvHearder = (TextView) convertView
							.findViewById(R.id.tv_header);
					convertView.setTag(headerHolder);

				} else {
					headerHolder = (HeaderViewHolder) convertView.getTag();
				}

				if (position == 0) {
					headerHolder.tvHearder.setText("用户应用:" + mUserList.size());
				} else {
					headerHolder.tvHearder
							.setText("系统应用:" + mSystemList.size());
				}
				break;
			}

			case 1: {
				ViewHolder holder = null;
				if (convertView == null) {
					convertView = View.inflate(AppManagerActivity.this,
							R.layout.list_appinfo_item, null);
					holder = new ViewHolder();
					holder.ivIcon = (ImageView) convertView
							.findViewById(R.id.iv_appicon);
					holder.tvLocation = (TextView) convertView
							.findViewById(R.id.tv_location);
					holder.tvTitle = (TextView) convertView
							.findViewById(R.id.tv_title);

					convertView.setTag(holder);// 设置一个标记，能携带任何数据，
				} else {
					holder = (ViewHolder) convertView.getTag();
				}
				AppInfo info = getItem(position);
				holder.tvTitle.setText(info.name);
				holder.ivIcon.setImageDrawable(info.icon);
				if (info.isRom) {
					holder.tvLocation.setText("手机内存");
				} else {
					holder.tvLocation.setText("SD卡中");
				}

			}

				break;
			}

			return convertView;
		}
	}

	static class ViewHolder {
		public ImageView ivIcon;
		public TextView tvTitle;
		public TextView tvLocation;
	}

	static class HeaderViewHolder {
		public TextView tvHearder;
	}

	/**
	 * 初始化应用列表
	 */
	private void initAppInfo() {
		new Thread() {
			public void run() {
				mList = AppInfoProvider.getAppInfos(AppManagerActivity.this);
				mUserList = new ArrayList<AppInfo>();
				mSystemList = new ArrayList<AppInfo>();
				for (AppInfo info : mList) {// 把list分为两个
					if (info.isUserApp) {
						mUserList.add(info);
					} else {
						mSystemList.add(info);
					}
				}

				mHandler.sendEmptyMessage(0);
			};
		}.start();

	}

	/**
	 * 获取某个路径下的可用空间
	 */
	@SuppressWarnings("deprecation")
	private String getAvailSpace(String path) {
		StatFs stat = new StatFs(path);//
		long availableBlocks = stat.getAvailableBlocks();// 获取可用储存块的数量
		long blockSize = stat.getBlockSize();// 获取每个储存块的大小
		long size = availableBlocks * blockSize;// 计算出可用的大小，int最多能储存的数量级转为出来只有1GB，所以用long
		String fileSize = Formatter.formatFileSize(this, size);// 转换为文件大小

		return fileSize;
	}

	/**
	 * activity 实现 点击事件
	 */
	@Override
	public void onClick(View v) {
		if (mPopupWindow != null) {
			mPopupWindow.dismiss();

		}

		switch (v.getId()) {
		case R.id.tv_uninstall:
			if (mCurrentInfo.isUserApp) {
				uninstall(mCurrentInfo.packgeName);
			}else {
				ToastUtils.showToast(this, "必须有root权限才能卸载系统应用");
			}
			

			break;
		case R.id.tv_launch:
			launch(mCurrentInfo.packgeName);
			break;
		case R.id.tv_share:
			shareApp(mCurrentInfo.packgeName);
			break;

		default:
			break;
		}

	}
	/**
	 * 分享应用
	 * @param packgeName
	 */
	private void shareApp(String packgeName) {
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("text/plain");// 发送的是文本
		intent.putExtra(Intent.EXTRA_TEXT,
				"这个app很好用,赶快下载吧!下载地址:https://play.google.com/store/apps/details?id="
						+ packgeName);
		startActivity(intent);
		// 跳相册
		// Intent picture = new
		// Intent(Intent.ACTION_PI	CK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		// startActivityForResult(picture, 0);
	}

	/*
	 * 启动应用
	 */
	private void launch(String packgeName) {
		PackageManager pm = getPackageManager();
		Intent intent = pm.getLaunchIntentForPackage(packgeName);
		startActivity(intent);
	}

	/*
	 * 卸载页面
	 */
	private void uninstall(String packgeName) {
		//跳转到卸载页面
		Intent intent = new Intent(Intent.ACTION_DELETE);//使用ACTION_VIEW 4.2以后可能崩溃
		intent.addCategory(Intent.CATEGORY_DEFAULT);
		intent.setData(Uri.parse("package:" + packgeName));
		startActivityForResult(intent, 0);
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		//卸载完成，刷新listview数据
		initAppInfo();
		super.onActivityResult(requestCode, resultCode, data);
	}

}
