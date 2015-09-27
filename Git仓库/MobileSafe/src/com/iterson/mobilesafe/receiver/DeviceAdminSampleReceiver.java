package com.iterson.mobilesafe.receiver;

import android.app.admin.DeviceAdminReceiver;
import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.iterson.mobilesafe.R;

public class DeviceAdminSampleReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO 未完成的超级管理员广播
		
	}
//	
//	public void lockScreen(){
//		
//		Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
//        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, mDeviceAdminSample);
//        intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,
//                mActivity.getString(R.string.add_admin_extra_app_text));
//        startActivityForResult(intent, REQUEST_CODE_ENABLE_ADMIN);	
//		
//	}

}
