package com.iterson.mobilesafe.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.iterson.mobilesafe.R;
import com.iterson.mobilesafe.service.AddressService;
import com.iterson.mobilesafe.service.BlackNumberService;
import com.iterson.mobilesafe.service.WatchDogService;
import com.iterson.mobilesafe.utils.PrefUtils;
import com.iterson.mobilesafe.utils.ServiceStatusUtils;
import com.iterson.mobilesafe.view.SettingClickView;
import com.iterson.mobilesafe.view.SettingItemView;

public class SettingActivity extends Activity {
	private SettingItemView sivUpdate;//自动更新组合控件
	private SettingItemView sivAddress;//归宿地显示组合控件
	private SettingClickView sivAddresssStyle;//归宿地风格组合控件
	private final String[] mItems = new String[]{"半透明","活力橙","卫士蓝","金属灰","苹果绿"};
	private SettingClickView scvAddressPositon;//归属地位置组合控件
	private SettingItemView sivBlackNumber;//黑名单组合控件
	private SettingItemView sivAppLock;//程序锁组合控件
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);
		
		sivUpdate = (SettingItemView) findViewById(R.id.siv_update);
		sivAddress = (SettingItemView) findViewById(R.id.siv_address);
		sivBlackNumber = (SettingItemView) findViewById(R.id.siv_black_number);
		sivAppLock = (SettingItemView) findViewById(R.id.siv_app_lock);
		
		initAutoUpdata();
		initAddress();
		initAddressStyle();
		initAddressPosition();
		initBlackNumber();
		initAppLock();
	}
	/**
	 * 程序锁
	 */
	private void initAppLock() {
		boolean serviceRunning = ServiceStatusUtils.isServiceRunning(SettingActivity.this, "com.iterson.mobilesafe.service.WatchDogService");
		if (serviceRunning) {
			sivAppLock.setChecked(true);
			startService(new Intent(SettingActivity.this,WatchDogService.class));//开启服务
		}else {
			sivAppLock.setChecked(false);
			stopService(new Intent(SettingActivity.this,WatchDogService.class));
		}
		
		 //点击事件
		sivAppLock.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) { 
				if (sivAppLock.isChecked()) {
					sivAppLock.setChecked(false);
					stopService(new Intent(SettingActivity.this,WatchDogService.class));
				}else {
					sivAppLock.setChecked(true);
					startService(new Intent(SettingActivity.this,WatchDogService.class));
				}
			}
		});
	}
	/**
	 * 黑名单拦截
	 */
	private void initBlackNumber() {
		boolean serviceRunning = ServiceStatusUtils.isServiceRunning(SettingActivity.this, "com.iterson.mobilesafe.service.BlackNumberService");
		if (serviceRunning) {
			sivBlackNumber.setChecked(true);
			startService(new Intent(SettingActivity.this,BlackNumberService.class));//开启服务
		}else {
			sivBlackNumber.setChecked(false);
			stopService(new Intent(SettingActivity.this,BlackNumberService.class));
		}
		
		 //点击事件
		sivBlackNumber.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) { 
				if (sivBlackNumber.isChecked()) {
					sivBlackNumber.setChecked(false);
					stopService(new Intent(SettingActivity.this,BlackNumberService.class));
				}else {
					sivBlackNumber.setChecked(true);
					startService(new Intent(SettingActivity.this,BlackNumberService.class));
				}
			}
		});
		
		
	}

	/**
	 * 归属地弹窗  提示位置
	 * 
	 * 
	 */
	private void initAddressPosition() {
		scvAddressPositon = (SettingClickView) findViewById(R.id.scv_addres_position);
		scvAddressPositon.setTitle("归属地提示框位置");
		scvAddressPositon.setDesc("设置归属地提示框位置");
		
		scvAddressPositon.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startActivity(new Intent(SettingActivity.this,DragViewActivity.class));
				
				
				
			}
		});
		
		
		
		
	}

	/*
	 * 初始化来电归属显示
	 */
	private void initAddress() {
		boolean serviceRunning = ServiceStatusUtils.isServiceRunning(SettingActivity.this, "com.iterson.mobilesafe.service.AddressService");
		if (serviceRunning) {
			sivAddress.setChecked(true);
			startService(new Intent(SettingActivity.this,AddressService.class));//开启服务
		}else {
			sivAddress.setChecked(false);
			stopService(new Intent(SettingActivity.this,AddressService.class));
		}
		 //电话归属地显示点击事件
		sivAddress.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (sivAddress.isChecked()) {
					sivAddress.setChecked(false);
					stopService(new Intent(SettingActivity.this,AddressService.class));
				}else {
					sivAddress.setChecked(true);
					startService(new Intent(SettingActivity.this,AddressService.class));
					
				}
			}
		});
	}
	
	/**
	 * 
	 * 自动更新
	 */
	private void initAutoUpdata() {
		//直接调用封装了的 sharePrenfenrences  PrefUtils
		boolean auto_update = PrefUtils.getBoolean(this,"auto_update" , false);
		if (auto_update) {//根据本地记录的sharedPreferences来设置checkbox时的状态
			sivUpdate.setChecked(true);
		}else {
			sivUpdate.setChecked(false);
		}
		//设置点击事件
		sivUpdate.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(sivUpdate.isChecked()){
					sivUpdate.setChecked(false);
					PrefUtils.setBoolean(SettingActivity.this,"auto_update" , false);
				}else {
					sivUpdate.setChecked(true);
					PrefUtils.setBoolean(SettingActivity.this,"auto_update" , true);
				}
			}
		});
	}
	
	/**
	 * 初始化归属地提示
	 */
	private void initAddressStyle(){
		sivAddresssStyle = (SettingClickView) findViewById(R.id.scv_address_style);
		sivAddresssStyle.setTitle("归属地提示框显示");
		int sytle = PrefUtils.getInt(SettingActivity.this, "address_style", 0);
		sivAddresssStyle.setDesc(mItems[sytle]);
		
		//给 他设置点击事件
		sivAddresssStyle.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				//选择弹窗
				showChooseDialog();
				
			}
		});
	}

	/**
	 * 修改归属地提示框
	 */
	protected void showChooseDialog() {
		AlertDialog.Builder buider = new AlertDialog.Builder(this);
		buider.setTitle("归属地提示框风格");
		int styleVlaue = PrefUtils.getInt(SettingActivity.this,"address_style", 0);
		buider.setIcon(R.drawable.ic_launcher);
		
		
		buider.setSingleChoiceItems(mItems,styleVlaue, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				PrefUtils.setInt(SettingActivity.this, "address_style", which);
				sivAddresssStyle.setDesc(mItems[which]);
				dialog.dismiss();
				
			}
		});
		buider.setNegativeButton("取消", null);
		buider.show();
	}

}
