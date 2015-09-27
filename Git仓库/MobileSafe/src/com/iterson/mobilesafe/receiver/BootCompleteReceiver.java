package com.iterson.mobilesafe.receiver;

import com.iterson.mobilesafe.utils.PrefUtils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.telephony.gsm.SmsManager;
import android.text.TextUtils;

/**
 * 开机重启的广播
 * 加权限android.permission.RECEIVE_BOOT_COMPLETED
 * @author Yang
 *
 */
public class BootCompleteReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		String savedSim = PrefUtils.getString(context, "SimNumber", null);
		boolean isProtected = PrefUtils.getBoolean(context, "protected", false);
		String phone = PrefUtils.getString(context, "SafePhone", null);
		if (!isProtected) {//没有开启短信保护
			return ;
		}else {
			if (!TextUtils.isEmpty(savedSim)) {
				TelephonyManager tm = (TelephonyManager) context.getSystemService(context.TELECOM_SERVICE);
				String currentSim = tm.getSimSerialNumber();
				if (currentSim.equals(savedSim)) {
					System.out.println("手机安全");
				}else {
					System.out.println("SIM卡发生变化，将发送报警短信");
					SmsManager sm = SmsManager.getDefault();
					sm.sendTextMessage(phone, null, "sim card changed", null, null);
					
				}
				
			} 
		}
		
		
	}

}
