package com.iterson.mobilesafe.activity;

import com.iterson.mobilesafe.R;

import android.app.Activity;
import android.net.TrafficStats;
import android.os.Bundle;

/**
 * 流量统计页面
 * 
 * @author Yang
 * 
 */
public class TrafficActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_traffic);

		long mobileRxBytes = TrafficStats.getMobileRxBytes();// 2G 3G下载流量
		long mobileTxBytes = TrafficStats.getMobileTxBytes();// 2G 3G上传流量
		long totalRxBytes = TrafficStats.getTotalRxBytes();// 移动流量+wifi 一共下载流量
		long totalTxBytes = TrafficStats.getTotalTxBytes();// 移动+wifi一共上传流量
		// TrafficStats.getUidRxBytes(uid);
		// TrafficStats.getUidTxBytes(uid);
		System.out.println(mobileRxBytes + "-----" + mobileTxBytes);

	}

}
