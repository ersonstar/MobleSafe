package com.iterson.mobilesafe.service;

import com.iterson.mobilesafe.R;
import com.iterson.mobilesafe.db.dao.AddressDao;
import com.iterson.mobilesafe.utils.PrefUtils;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.TextView;

/**
 * 来电提醒的服务
 * 
 * @author Kevin
 * 
 */
public class AddressService extends Service {

	private TelephonyManager mTM;
	private MyPhoneStateListener mListener;
	private OutCallReceiver mReceiver;
	private WindowManager mWM;
	private View mView;

	int startX;
	int startY;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();

		mTM = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);

		mListener = new MyPhoneStateListener();
		mTM.listen(mListener, PhoneStateListener.LISTEN_CALL_STATE);// 监听来电状态

		// 注册监听去电的广播
		mReceiver = new OutCallReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(Intent.ACTION_NEW_OUTGOING_CALL);
		registerReceiver(mReceiver, filter);
	}

	class MyPhoneStateListener extends PhoneStateListener {
		// 来电状态发生变化
		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			switch (state) {
			case TelephonyManager.CALL_STATE_RINGING:// 电话玲响
				String address = AddressDao.getAddress(incomingNumber);
				// Toast.makeText(AddressService.this, address,
				// Toast.LENGTH_LONG)
				// .show();
				showToast(address);
				break;

			case TelephonyManager.CALL_STATE_IDLE:// 通话结束
				if (mWM != null && mView != null) {
					mWM.removeView(mView);// 当通话结束后,从window上移除布局
				}
				break;

			default:
				break;
			}

			super.onCallStateChanged(state, incomingNumber);
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		
		mTM.listen(mListener, PhoneStateListener.LISTEN_NONE);// 停止来电监听

		// 注销监听去电的广播
		unregisterReceiver(mReceiver);
		mReceiver = null;
	}

	/**
	 * 监听去电广播 需要权限:android.permission.PROCESS_OUTGOING_CALLS
	 * 
	 * @author Kevin
	 * 
	 */
	public class OutCallReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			String phone = getResultData();// 获取去电电话号码
			System.out.println("去电号码:" + phone);

			String address = AddressDao.getAddress(phone);
			// Toast.makeText(context, address, Toast.LENGTH_LONG).show();
			showToast(address);
		}
	}

	/**
	 * 在电话界面上展示归属地界面 需要权限:android.permission.SYSTEM_ALERT_WINDOW
	 */
	private void showToast(String address) {
		// 获取windowManager对象, 获取窗口管理器, 窗口承载系统所有界面和布局,包括状态栏,activity等
		mWM = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);

		final int width = mWM.getDefaultDisplay().getWidth();
		final int height = mWM.getDefaultDisplay().getHeight();

		// 初始化window的参数
		final WindowManager.LayoutParams params = new WindowManager.LayoutParams();
		params.height = WindowManager.LayoutParams.WRAP_CONTENT;
		params.width = WindowManager.LayoutParams.WRAP_CONTENT;
		params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
				| WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
		params.format = PixelFormat.TRANSLUCENT;
		params.type = WindowManager.LayoutParams.TYPE_PHONE;
		params.setTitle("Toast");
		params.gravity = Gravity.LEFT + Gravity.TOP;// 将重心设置为左上方,
													// 这样的话,0,0点坐标就从左上方开始计算

		// 获取本地保存的位置信息
		int posX = PrefUtils.getInt(this, "PosX", 0);
		int posY = PrefUtils.getInt(this, "PosY", 0);

		params.x = posX;
		params.y = posY;

		// 初始化要展示的界面
		// mView = new TextView(this);
		// mView.setTextColor(Color.RED);
		// mView.setText(address);
		mView = View.inflate(this, R.layout.custom_toast, null);
		TextView tvAddress = (TextView) mView.findViewById(R.id.tv_addresss);
		tvAddress.setText(address);

		Integer[] picIds = new Integer[] { R.drawable.call_locate_white,
				R.drawable.call_locate_orange, R.drawable.call_locate_blue,
				R.drawable.call_locate_gray, R.drawable.call_locate_green };
		int style = PrefUtils.getInt(this, "address_style", 0);
		tvAddress.setBackgroundResource(picIds[style]);// 根据本地设置,更新背景图片

		// 设置触摸监听
		mView.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				System.out.println("onTouch....");

				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:// 按下事件
					startX = (int) event.getRawX();
					startY = (int) event.getRawY();
					break;

				case MotionEvent.ACTION_MOVE:// 移动事件
					int endX = (int) event.getRawX();
					int endY = (int) event.getRawY();

					// 计算移动偏移量
					int dx = endX - startX;
					int dy = endY - startY;

					// 根据偏移量,更新显示位置
					int x = params.x + dx;
					int y = params.y + dy;

					// 控制坐标点不要离开屏幕范围
					if (x < 0) {
						x = 0;
					}

					if (x > width - mView.getWidth()) {
						x = width - mView.getWidth();
					}

					// 控制坐标点不要离开屏幕范围
					if (y < 0) {
						y = 0;
					}

					if (y > height - 25 - mView.getHeight()) {
						y = height - 25 - mView.getHeight();
					}

					params.x = x;
					params.y = y;

					System.out.println("x:" + params.x + ";y:" + params.y);

					mWM.updateViewLayout(mView, params);// 更新窗口布局

					// 重新初始化起点坐标
					startX = (int) event.getRawX();
					startY = (int) event.getRawY();
					break;

				case MotionEvent.ACTION_UP:// 抬起事件
					// 保存当时的显示位置
					PrefUtils.setInt(AddressService.this, "PosX", params.x);
					PrefUtils.setInt(AddressService.this, "PosY", params.y);
					break;
				default:
					break;
				}

				return true;
			}
		});

		// 给window添加布局
		mWM.addView(mView, params);
	}

}
