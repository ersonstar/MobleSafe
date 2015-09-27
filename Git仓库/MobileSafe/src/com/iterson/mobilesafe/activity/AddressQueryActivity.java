package com.iterson.mobilesafe.activity;

import com.iterson.mobilesafe.R;
import com.iterson.mobilesafe.db.dao.AddressDao;
import com.iterson.mobilesafe.utils.ToastUtils;

import android.app.Activity;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.widget.EditText;
import android.widget.TextView;

public class AddressQueryActivity extends Activity {
	private EditText etPhoneNumber;
	private TextView tvResulte;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_addressquery);
		etPhoneNumber = (EditText) findViewById(R.id.et_phoneNumber);
		tvResulte = (TextView) findViewById(R.id.tv_result);
		
		//设置edittext监听器
		etPhoneNumber.addTextChangedListener(new TextWatcher() {
			//监听文本发生变化
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				
			}
			//监听发生变化之前
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				
			}
			//监听变化之后
			@Override
			public void afterTextChanged(Editable s) {
				String address = AddressDao.getAddress(s.toString().trim());
				tvResulte.setText(address);
			}
		});
		
	}
	/**
	 * 查询按钮
	 */
	public void startQuery(View v){
		
		String phoneNumber = etPhoneNumber.getText().toString().trim();
		//电话号码 1开始 （345678）第二位后面全数字的话
		//正则表达式为^1[3-8]\d{9}$
		if (phoneNumber.matches("^1[3-8]\\d{9}$")) {//匹配手机号码
			String address = AddressDao.getAddress(phoneNumber);
			tvResulte.setText(address);
			
		}else {
			Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
//			shake.setInterpolator(new Interpolator() {
//				
//				@Override
//				public float getInterpolation(float x) {
//					/**
//					 * 一个 x y 的方程式，决定他的路径，移动
//					 * x代表时间
//					 * y代表路程
//					* 自定义 插补器
//					 */
//					return 0;
//				}
//			});
			
			etPhoneNumber.startAnimation(shake);
			vibrate();
			ToastUtils.showToast(AddressQueryActivity.this,"请输入11位手机号码");
		}
	}
	
	/**
	 * 手机震动
	 * 需要权限android.permission.VIBRATE
	 */
	public void vibrate(){
		Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
//		vibrator.vibrate(1000);
		vibrator.vibrate(new long[]{500,500,300,500}, -1);//有节奏震动，long表示先停留，在震动，在停留依次类推，参2表示-1表示不重复，只震动一次,0表示第一个时间重复震动，1表示第二个参数开始重复
		
		
	}
	
	
}
