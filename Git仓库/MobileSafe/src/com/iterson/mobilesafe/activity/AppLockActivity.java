package com.iterson.mobilesafe.activity;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.iterson.mobilesafe.R;
import com.iterson.mobilesafe.db.dao.AppLockDao;
import com.iterson.mobilesafe.domain.AppInfo;
import com.iterson.mobilesafe.engine.AppInfoProvider;

/**
 * 软件锁
 * 
 * @author Yang
 * 
 */
public class AppLockActivity extends Activity implements OnClickListener {
	private LinearLayout llLock;
	private LinearLayout llUnlock;
	private TextView tvTabLock;
	private TextView tvTabUnlock;
	private ListView lvLock;
	private ListView lvUnlock;
	private ArrayList<AppInfo> mList;
	private ArrayList<AppInfo> mLockList;
	private ArrayList<AppInfo> mUnlockList;
	private TextView tvLockNum;
	private TextView tvUnLockNum;
	private AppLockDao mDao;
	private AppLockAdapter mUnLockAdapter;
	private AppLockAdapter mLockAdapter;

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {

			mUnLockAdapter = new AppLockAdapter(false);
			lvUnlock.setAdapter(mUnLockAdapter);

			mLockAdapter = new AppLockAdapter(true);
			lvLock.setAdapter(mLockAdapter);
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_applock);
		llLock = (LinearLayout) findViewById(R.id.ll_lock);
		llUnlock = (LinearLayout) findViewById(R.id.ll_unlock);
		tvTabLock = (TextView) findViewById(R.id.tv_tab_lock);
		tvTabUnlock = (TextView) findViewById(R.id.tv_tab_unlock);
		lvLock = (ListView) findViewById(R.id.lv_lock);
		lvUnlock = (ListView) findViewById(R.id.lv_unlock);
		tvLockNum = (TextView) findViewById(R.id.tv_lock_num);
		tvUnLockNum = (TextView) findViewById(R.id.tv_unlock_num);
		// 初始化Dao
		mDao = AppLockDao.getInstance(this);

		tvTabLock.setOnClickListener(this);
		tvTabUnlock.setOnClickListener(this);

		// 初始化界面时的2个锁按键
		llLock.setVisibility(View.GONE);
		llUnlock.setVisibility(View.VISIBLE);
		tvTabUnlock.setBackgroundResource(R.drawable.tab_left_pressed);
		tvTabLock.setBackgroundResource(R.drawable.tab_right_default);

		initData();// 初始化数据

	}

	/**
	 * 初始化数据
	 */
	private void initData() {

		new Thread() {
			public void run() {
				mList = AppInfoProvider.getAppInfos(getApplicationContext());
				// 创建2个集合加锁的和未加锁的
				mLockList = new ArrayList<AppInfo>();
				mUnlockList = new ArrayList<AppInfo>();
				for (AppInfo info : mList) {
					if (mDao.find(info.packgeName)) {// 返回真表示在已加锁的数据库
						mLockList.add(info);
					} else {
						mUnlockList.add(info);

					}
				}

				mHandler.sendEmptyMessage(0);
			};
		}.start();

	}

	/**
	 * mAdpter
	 */
	class AppLockAdapter extends BaseAdapter {
		private boolean isLock;
		private TranslateAnimation animLeft;
		private TranslateAnimation animRight;

		public AppLockAdapter(boolean isLock) {
			this.isLock = isLock;
			// 动画 点击item的移动动画
			animLeft = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0,
					Animation.RELATIVE_TO_SELF, 1, Animation.RELATIVE_TO_SELF,
					0, Animation.RELATIVE_TO_SELF, 0);
			animLeft.setDuration(500);
			// 动画
			animRight = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0,
					Animation.RELATIVE_TO_SELF, -1, Animation.RELATIVE_TO_SELF,
					0, Animation.RELATIVE_TO_SELF, 0);
			animRight.setDuration(500);

		}

		@Override
		public int getCount() {
			if (isLock) {
				return mLockList.size();
			} else {
				return mUnlockList.size();
			}
		}

		@Override
		public AppInfo getItem(int position) {
			if (isLock) {
				return mLockList.get(position);
			} else {
				return mUnlockList.get(position);
			}

		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			AppLockHolder holder = new AppLockHolder();
			if (convertView == null) {
				convertView = View.inflate(getApplicationContext(),
						R.layout.list_applock_item, null);
				holder.tvName = (TextView) convertView
						.findViewById(R.id.tv_title);
				holder.ivIcon = (ImageView) convertView
						.findViewById(R.id.iv_icon);
				holder.ivLock = (ImageView) convertView
						.findViewById(R.id.iv_lock);

				convertView.setTag(holder);

			} else {
				holder = (AppLockHolder) convertView.getTag();
			}

			final AppInfo info = getItem(position);
			holder.tvName.setText(info.name);
			holder.ivIcon.setImageDrawable(info.icon);

			if (isLock) {
				tvLockNum.setText(String.format("已加锁软件%d个", mLockList.size()));
				holder.ivLock.setImageResource(R.drawable.lock);
			} else {
				tvUnLockNum.setText(String.format("未加锁软件%d个",
						mUnlockList.size()));
				holder.ivLock.setImageResource(R.drawable.unlock);
			}

			final View view = convertView;// 下面不能用convertView ,用view
			holder.ivLock.setOnClickListener(new OnClickListener() {// 点击锁图标
						@Override
						public void onClick(View v) {
							if (isLock) {// 如果是已锁，
								view.startAnimation(animRight);
								// 动画和后面的代码是异步执行的，， 所以监听一下animation
								animRight
										.setAnimationListener(new AnimationListener() {
											@Override
											public void onAnimationStart(
													Animation animation) {

											}

											@Override
											public void onAnimationRepeat(
													Animation animation) {

											}

											@Override
											public void onAnimationEnd(
													Animation animation) {
												mUnlockList.add(0, info);// 给未锁的加这一行
												mUnLockAdapter
														.notifyDataSetChanged();
												mLockList.remove(info);// 已锁移除
												mLockAdapter
														.notifyDataSetChanged();
												mDao.delete(info.packgeName);// 数据库删除

											}
										});

							} else {// 未锁界面
								// 开启一个移动的动画 加锁的向右移
								view.startAnimation(animLeft);
								// 同理监听
								animLeft.setAnimationListener(new AnimationListener() {
									@Override
									public void onAnimationStart(
											Animation animation) {
									}

									@Override
									public void onAnimationRepeat(
											Animation animation) {
									}

									@Override
									public void onAnimationEnd(
											Animation animation) {
										mUnlockList.remove(info);
										mUnLockAdapter.notifyDataSetChanged();
										mLockList.add(0,info);
										mLockAdapter.notifyDataSetChanged();
										mDao.add(info.packgeName);
									}
								});
							}

						}
					});

			return convertView;

		}
	}

	class AppLockHolder {
		private TextView tvName;
		private ImageView ivLock;
		private ImageView ivIcon;

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {

		case R.id.tv_tab_unlock:
			llLock.setVisibility(View.GONE);
			llUnlock.setVisibility(View.VISIBLE);
			tvTabUnlock.setBackgroundResource(R.drawable.tab_left_pressed);
			tvTabLock.setBackgroundResource(R.drawable.tab_right_default);
			break;

		case R.id.tv_tab_lock:
			llLock.setVisibility(View.VISIBLE);
			llUnlock.setVisibility(View.GONE);
			tvTabUnlock.setBackgroundResource(R.drawable.tab_right_default);
			tvTabLock.setBackgroundResource(R.drawable.tab_left_pressed);
			break;

		default:
			break;
		}

	}

}
