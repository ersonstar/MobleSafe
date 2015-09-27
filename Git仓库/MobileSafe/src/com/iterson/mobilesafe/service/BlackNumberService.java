package com.iterson.mobilesafe.service;

import java.lang.reflect.Method;

import com.android.internal.telephony.ITelephony;
import com.iterson.mobilesafe.R;
import com.iterson.mobilesafe.db.dao.BlackNumberDao;
import com.iterson.mobilesafe.service.AddressService.MyPhoneStateListener;
import com.iterson.mobilesafe.utils.PrefUtils;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;

public class BlackNumberService extends Service {

	private InnerSmsReceiver mReceiver;
	private TelephonyManager mTM;
	private MyPhoneStateListener mListener;
	private CallLogContentObserver mObserver;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		mTM = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		mListener = new MyPhoneStateListener();
		mTM.listen(mListener, PhoneStateListener.LISTEN_CALL_STATE);

		// 注册短信广播
		mReceiver = new InnerSmsReceiver();
		IntentFilter filter = new IntentFilter(
				"android.provider.Telephony.SMS_RECEIVED");
		filter.setPriority(Integer.MAX_VALUE);// 设置优先级
		registerReceiver(mReceiver, filter);

	}

	class MyPhoneStateListener extends PhoneStateListener {
		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			super.onCallStateChanged(state, incomingNumber);
			switch (state) {
			case TelephonyManager.CALL_STATE_RINGING:
				// 判断电话号码是否为黑名单
				boolean exist = BlackNumberDao.getInstance(
						BlackNumberService.this).find(incomingNumber);
				if (exist) {
					int mode = BlackNumberDao.getInstance(
							BlackNumberService.this).findMode(incomingNumber);
					if (mode == 1 || mode == 3) {// 1 和3 位拦截电话 和拦截所有
						// 拦截电话 endCall
						endCall();
						// //系统添加通讯录是异步执行的，要等一段时间，挂断就删除了，但是挂断系统才添加
						// deleteCalllog(incomingNumber);

						// 监听通讯录的变化,一旦变化再删除.
						mObserver = new CallLogContentObserver(new Handler(),
								incomingNumber);
						getContentResolver().registerContentObserver(
								Uri.parse("content://call_log/calls"), true,
								mObserver);//注册内容观察者
					}
				}
				break;

			default:
				break;
			}
		}
	}

	class CallLogContentObserver extends ContentObserver {
		private String incomingNumber;

		public CallLogContentObserver(Handler handler, String incomingNumber) {
			super(handler);
			this.incomingNumber = incomingNumber;
		}

		// 监听数据发生变化
		@Override
		public void onChange(boolean selfChange) {
			// TODO Auto-generated method stub
			super.onChange(selfChange);
			// 删除通话记录
			deleteCalllog(incomingNumber);

			// 注销mObserver;
			getContentResolver().unregisterContentObserver(mObserver);

		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		unregisterReceiver(mReceiver);
		mReceiver = null;

		mTM.listen(mListener, PhoneStateListener.LISTEN_NONE);
		mListener = null;

	}

	/**
	 * 删除通话记录 <uses-permission android:name="android.permission.READ_CALL_LOG"/>
	 * <uses-permission android:name="android.permission.WRITE_CALL_LOG"/>
	 * <uses-permission android:name="android.permission.WRITE_CONTACTS"/> 2.x版本需要此权限
	 */
	public void deleteCalllog(String Number) {
		getContentResolver().delete(Uri.parse("content://call_log/calls"),
				"number=?", new String[] { Number });

	}

	/**
	 * 挂电话
	 */
	public void endCall() {
		// android.os.ServiceManager
		// ITelephony.Stub.asInterface(ServiceManager.getService(Context.TELEPHONY_SERVICE));
		try {
			Class clazz = BlackNumberService.class.getClassLoader().loadClass(
					"android.os.ServiceManager");// 获取ServiceManager的字节码
			Method method = clazz.getDeclaredMethod("getService", String.class);// 获取方法
			IBinder binder = (IBinder) method.invoke(null,
					Context.TELEPHONY_SERVICE);// 参1
												// 静态方法可以传null。通过反射调用方法，获取IBinder代理对象
			ITelephony telephony = ITelephony.Stub.asInterface(binder);// 需要2个aidl
			telephony.endCall();
		} catch (Exception e) {
			// 如果不存在的异常
			e.printStackTrace();
		}

	}

	/**
	 * 动态广播比静态广播先接受到拦截短信
	 * 
	 * @author Yang
	 * 
	 */
	class InnerSmsReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {

			Object[] objs = (Object[]) intent.getExtras().get("pdus");

			for (Object object : objs) {
				SmsMessage message = SmsMessage.createFromPdu((byte[]) object);
				String phoneNumber = message.getOriginatingAddress();
				String messageBody = message.getMessageBody();
				// 从数据库得到拦截的名单
				boolean result = BlackNumberDao.getInstance(
						BlackNumberService.this).find(phoneNumber);
				if (result) {// 如果广播在黑名单之内
					// 并且它的拦截类型为短信
					int mode = BlackNumberDao.getInstance(
							BlackNumberService.this).findMode(phoneNumber);
					if (mode > 1) {
						abortBroadcast();// 终止广播传递
					}
				}
				System.out.println("动态设置拦截service");
			}
		}
	}
}
