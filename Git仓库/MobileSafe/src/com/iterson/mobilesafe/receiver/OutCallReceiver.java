package com.iterson.mobilesafe.receiver;

import com.iterson.mobilesafe.db.dao.AddressDao;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.widget.Toast;
/**
 * 监听去电广播 
 * 权限 android.permission.PROCESS_OUTGOING_CALLS
 * @author Yang
 *
 */
public class OutCallReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		
		String phone = getResultData();//得到去电号码
		String address = AddressDao.getAddress(phone);
		System.out.println(address);
		
		Toast.makeText(context, address, 1).show();
	}

}
