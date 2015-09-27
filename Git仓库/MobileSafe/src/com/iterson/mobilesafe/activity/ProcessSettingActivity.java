package com.iterson.mobilesafe.activity;

import com.iterson.mobilesafe.R;
import com.iterson.mobilesafe.service.AppLockService;
import com.iterson.mobilesafe.utils.PrefUtils;
import com.iterson.mobilesafe.utils.ServiceStatusUtils;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;

/**
 * 系统进程设置页面
 * 
 * @author Yang
 * 
 */
public class ProcessSettingActivity extends Activity {
	private boolean process;
	private CheckBox cbProcessCheck;
	private CheckBox cbLockClean;
	private boolean serviceRunning;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_process_setting);

		 cbProcessCheck = (CheckBox) findViewById(R.id.cb_show_system);
		cbLockClean = (CheckBox) findViewById(R.id.cb_lock_clean);

		process = PrefUtils.getBoolean(this, "system_prcocess_ischeck",
				false);
		if (process) {
			cbProcessCheck.setChecked(process);
			cbProcessCheck.setText("当前状态:显示系统进程已开启");
		}
		
		
		cbProcessCheck.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (process) {
					PrefUtils.setBoolean(ProcessSettingActivity.this,
							"system_prcocess_ischeck", false);
					cbProcessCheck.setText("当前状态:显示系统进程已关闭");
				} else {
					PrefUtils.setBoolean(ProcessSettingActivity.this,
							"system_prcocess_ischeck", true);
					cbProcessCheck.setText("当前状态:显示系统进程已开启");
				}
			}
		});
		//判断service是否在运行
		serviceRunning = ServiceStatusUtils.isServiceRunning(ProcessSettingActivity.this, "AppLockService");
		if (serviceRunning) {
			cbLockClean.setChecked(serviceRunning);
			cbProcessCheck.setText("当前状态:锁屏清理已开启");
		}
		cbLockClean.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(ProcessSettingActivity.this,AppLockService.class);
				if (serviceRunning) {
					cbLockClean.setText("当前状态:锁屏清理已关闭");
					stopService(intent);
					} else {
					cbLockClean.setText("当前状态:锁屏清理已开启");
					startService(intent);
				}
			}
		});
		
		

	}

}
