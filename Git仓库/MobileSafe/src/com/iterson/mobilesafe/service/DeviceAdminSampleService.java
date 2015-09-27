package com.iterson.mobilesafe.service;

import com.iterson.mobilesafe.R;

import android.app.Service;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
/**
 * 超级管理员权限
 * @author Yang
 *
 */
public class DeviceAdminSampleService extends Service {

	private DevicePolicyManager mDPM;

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		
		mDPM = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
		mDPM.lockNow();
		// TODO 未完成的服务，用于开启超级管理员权限，和锁屏，清理数据等 
		Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
//        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, mDeviceAdminSample);
        intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,
               "超级管理员权限，很强大");
        startActivity(intent);	
		
	}
	
	

}
