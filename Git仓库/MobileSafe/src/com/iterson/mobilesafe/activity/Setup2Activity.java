package com.iterson.mobilesafe.activity;

import com.iterson.mobilesafe.R;
import com.iterson.mobilesafe.utils.PrefUtils;
import com.iterson.mobilesafe.utils.ToastUtils;
import com.iterson.mobilesafe.view.SettingItemView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class Setup2Activity extends BaseSetupActivity {
	
	private SettingItemView sivBind;
	private String simSerialNumber;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup2);
		
		sivBind = (SettingItemView) findViewById(R.id.siv_bind);
		
		//选择绑定
		checkSivBind();
		//获取SIM卡信息
		TelephonyManager tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		simSerialNumber = tm.getSimSerialNumber();
		System.out.println(simSerialNumber);
		
	}
	
	//设置绑定SIM卡点击事件
	private void checkSivBind() {
		// 
		boolean simBind = PrefUtils.getBoolean(Setup2Activity.this, "SIMBind", false);
		if (simBind) {
			sivBind.setChecked(true);
			PrefUtils.setString(Setup2Activity.this, "SimNumber", simSerialNumber);
		}else {
			sivBind.setChecked(false);
			PrefUtils.remove(Setup2Activity.this, "SimNumber");
		}
		
		sivBind.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (sivBind.isChecked()) {
					sivBind.setChecked(false);
					PrefUtils.setBoolean(Setup2Activity.this, "SIMBind", false);
				}else {
					sivBind.setChecked(true);
					PrefUtils.setBoolean(Setup2Activity.this, "SIMBind", true);
				}
				
				
			}
		});
		
	}
	
	public void showNext(){
		if (PrefUtils.getBoolean(Setup2Activity.this, "SIMBind", false)) {
			startActivity(new Intent(Setup2Activity.this,Setup3Activity.class));
			finish();
			//activity之前的切换动画
			overridePendingTransition(R.anim.anim_enter, R.anim.anim_exit);
		}else {
			ToastUtils.showToast(Setup2Activity.this, "必须先绑定SIM卡");
		}
		
	}
	
	public void showPrevious(){
		startActivity(new Intent(Setup2Activity.this,Setup1Activity.class));
		finish();
		overridePendingTransition( R.anim.anim_previ_enter,R.anim.anim_previous_exit);
	}
	
	
	
	
}
