package com.iterson.mobilesafe.service;

import com.iterson.mobilesafe.utils.PrefUtils;

import android.app.Service;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.text.TextUtils;
/**
 * 手机定位的服务
 * @author Yang
 *
 */
public class LocationService extends Service {

	private LocationManager mLM;
	private MyLocationListener mListener;


	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		mLM = (LocationManager) getSystemService(LOCATION_SERVICE);
		
		
		Criteria criteria = new Criteria();//标准
		criteria.setCostAllowed(true);//是否允许花费，如耗费流量
		criteria.setAccuracy(criteria.ACCURACY_FINE);//设置精度。精度越高越耗电
		String bestProvider = mLM.getBestProvider(criteria, true);//获取当前环境下最好的提供者
		if (!TextUtils.isEmpty(bestProvider)) {//
			mListener = new MyLocationListener();
			mLM.requestLocationUpdates(bestProvider, 0, 0, mListener);
		
		}
		
	}
	/**
	 * 获取经纬度的内部类
	 * 位置监听器
	 * @author Yang
	 *
	 */
	
	class MyLocationListener implements  LocationListener{
		
		//当位置发生变化时调用
		@Override
		public void onLocationChanged(Location location) {
			String latitude ="j:" + location.getLatitude();
			String longitude	="w:" +location.getLongitude();
			String altitude ="海拔" +location.getAltitude();
			String accuray ="精确度" + location.getAccuracy();
			PrefUtils.setString(LocationService.this, "location", latitude+";"+longitude);
			
			stopSelf();//自己停掉自己
		}
		//当状态发生变化时调用
		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			
		}
		//当打开GPS时调用
		@Override
		public void onProviderEnabled(String provider) {
			
		}
		//关闭GPS时调用
		@Override
		public void onProviderDisabled(String provider) {
			
		}
		
	}
	
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		mLM.removeUpdates(mListener);//通知系统，停止监听
		
	}

}
