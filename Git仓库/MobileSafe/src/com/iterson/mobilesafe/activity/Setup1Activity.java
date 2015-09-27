package com.iterson.mobilesafe.activity;

import com.iterson.mobilesafe.R;
import com.iterson.mobilesafe.utils.ToastUtils;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class Setup1Activity extends BaseSetupActivity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup1);
		
	}
	/**
	 * 下一页
	 */
	@Override
	public void showNext() {
		startActivity(new Intent(Setup1Activity.this,Setup2Activity.class));
		finish();
		overridePendingTransition(R.anim.anim_enter, R.anim.anim_exit);
		
		
	}
	@Override
	public void showPrevious() {
		// 
		ToastUtils.showToast(Setup1Activity.this, "已经到顶了");
	}
	
	
}
