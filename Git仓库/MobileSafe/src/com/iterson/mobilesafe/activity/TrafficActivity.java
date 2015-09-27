package com.iterson.mobilesafe.activity;

import java.util.ArrayList;

import com.iterson.mobilesafe.R;
import com.iterson.mobilesafe.domain.AppInfo;
import com.iterson.mobilesafe.engine.AppInfoProvider;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.TrafficStats;
import android.os.Bundle;
import android.os.Handler;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.view.textservice.TextInfo;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

/**
 * 流量统计页面
 * 
 * @author Yang
 * 
 */
public class TrafficActivity extends Activity {
	private ListView lvTraffic;
	private ArrayList<AppInfo> mAppList;
	private TrafficAdapter mAdapter;
	private TrafficHodler mHodler;
	private ArrayList<TrafficInfo> mTrafficList;
	private ArrayList<AppInfo> mUserApp;
	private ArrayList<AppInfo> mSystemApp;
	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			mAdapter = new TrafficAdapter();
			lvTraffic.setAdapter(mAdapter);
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_traffic);
		lvTraffic = (ListView) findViewById(R.id.lv_traffic_pid);

		new Thread() {

			public void run() {
				mTrafficList = new ArrayList<TrafficInfo>();
				mAppList = AppInfoProvider.getAppInfos(TrafficActivity.this);
				mUserApp = new ArrayList<AppInfo>();
				mSystemApp = new ArrayList<AppInfo>();
				for (AppInfo info : mAppList) {
					TrafficInfo trafficInfo = new TrafficInfo();
					trafficInfo.rxBytes = TrafficStats.getUidRxBytes(info.uid);
					trafficInfo.txBytes = TrafficStats.getUidTxBytes(info.uid);
					trafficInfo.name = info.name;
					trafficInfo.icon = info.icon;
					mTrafficList.add(trafficInfo);
					if (info.isUserApp) {
						mUserApp.add(info);
					} else {
						mSystemApp.add(info);
					}
					mHandler.sendEmptyMessage(0);

				}
			};
		}.start();

	}

	class TrafficAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return mUserApp.size() + mSystemApp.size() + 2;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public int getViewTypeCount() {
			return 2;
		}

		@Override
		public int getItemViewType(int position) {
			if (position == 0 || position == mUserApp.size() + 1) {
				return 0;
			} else {
				return 1;
			}
		}

		@Override
		public AppInfo getItem(int position) {
			if (position == 0 || position == mUserApp.size() + 1) {
				return null;
			} else {
				if (position <= mUserApp.size()) {
					return mUserApp.get(position - 1);
				} else {
					return mSystemApp.get(position - mUserApp.size() - 2);
				}
			}
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			int type = getItemViewType(position);
			switch (type) {
			case 0:
				TrafficHodler mUserHodler = new TrafficHodler();
				if (position == 0) {
					convertView = View.inflate(getApplicationContext(),
							R.layout.list_traffic_type_header, null);
					mUserHodler.name = (TextView) convertView
							.findViewById(R.id.tv_traffic_header);
					convertView.setTag(mUserHodler);
				} else {
					mUserHodler = (TrafficHodler) convertView.getTag();
				}

				if (position == 0) {
					mUserHodler.name.setText("用户应用");
				} else {
					mUserHodler.name.setText("系统应用");
				}
				break;

			case 1: {
				mHodler = new TrafficHodler();
				if (convertView == null) {
					convertView = View.inflate(getApplicationContext(),
							R.layout.list_traffic_item, null);
					mHodler.icon = (ImageView) convertView
							.findViewById(R.id.iv_traffic_icon);
					mHodler.name = (TextView) convertView
							.findViewById(R.id.tv_traffic_name);
					mHodler.traffic = (TextView) convertView
							.findViewById(R.id.tv_traffic_traffic);
					convertView.setTag(mHodler);
				} else {
					mHodler = (TrafficHodler) convertView.getTag();
				}
				AppInfo info = getItem(position);
				mHodler.icon.setImageDrawable(info.icon);
				mHodler.name.setText(info.name);
				String rxBytes = Formatter.formatFileSize(
						getApplicationContext(),
						TrafficStats.getUidRxBytes(info.uid));
				String txBytes = Formatter.formatFileSize(
						getApplicationContext(),
						TrafficStats.getUidTxBytes(info.uid));
				mHodler.traffic.setText("消耗移动流量为" + rxBytes + "\nWIFI流量为"
						+ txBytes);

			}

				break;
			}
			return convertView;
		}
	}

	public void trafficAPI() {
		long mobileRxBytes = TrafficStats.getMobileRxBytes();// 2G 3G下载流量
		long mobileTxBytes = TrafficStats.getMobileTxBytes();// 2G 3G上传流量
		long totalRxBytes = TrafficStats.getTotalRxBytes();// 移动流量+wifi
															// 一共下载流量
		long totalTxBytes = TrafficStats.getTotalTxBytes();// 移动+wifi一共上传流量
		// TrafficStats.getUidRxBytes(uid);
		// TrafficStats.getUidTxBytes(uid);
		System.out.println(mobileRxBytes + "-----" + mobileTxBytes);
	}

	class TrafficInfo {
		public long rxBytes;
		public long txBytes;
		public Drawable icon;
		public String name;
	}

	public static class TrafficHodler {
		public ImageView icon;
		public TextView traffic;
		public TextView name;
	}

}
