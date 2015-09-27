package com.iterson.mobilesafe.receiver;

import com.iterson.mobilesafe.R;
import com.iterson.mobilesafe.service.LocationService;
import com.iterson.mobilesafe.utils.PrefUtils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
/**
 * 短信拦截
 * android.permission.RECEIVE_SMS 权限
 * @author Yang
 *
 */
public class SmsReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Object[] objs = (Object[]) intent.getExtras().get("pdus");

		for(Object object : objs){
			SmsMessage message = SmsMessage.createFromPdu((byte[]) object);
			String phoneNumber = message.getOriginatingAddress();
			String messageBody = message.getMessageBody();
			
			if ("#*alarm*#".equals(message)) {
				//播放报警音乐
				MediaPlayer player = MediaPlayer.create(context, R.raw.ylzs);
				player.setVolume(1, 1); 
				player.setLooping(true);
				player.start();
				abortBroadcast();//中断广播传递，不会进入短信列表，此方法4.4以上版本无效
			}else if ("#*location*#".equals(messageBody)) {
				//获取手机经纬度
				//开启了服务,sp里面已经保存了位置信息，
				context.startService(new Intent(context,LocationService.class));
				String location = PrefUtils.getString(context, "location", null);
				//发送位置信息短信
				SmsManager sm = SmsManager.getDefault();
				sm.sendTextMessage(phoneNumber, null, location, null, null);
				
				abortBroadcast();//中断广播传递，不会进入短信列表，此方法4.4以上版本无效
				
				
				
			}
			
		}
		
		
	}
	
	
	

}
