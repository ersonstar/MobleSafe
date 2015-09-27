package com.iterson.mobilesafe.activity;

import com.iterson.mobilesafe.R;
import com.iterson.mobilesafe.utils.PrefUtils;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

public class LostAndFindActivity extends Activity {
	private TextView tvReset;
	private TextView tvSafePhone;
	private ImageView ivLock;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		
		
		boolean configed = PrefUtils.getBoolean(this, "configed", false);
		if (!configed) {
			startActivity(new Intent(this,Setup1Activity.class));
			finish();
		}else {
			//主页面
			setContentView(R.layout.activity_lostandfind);
			
			tvSafePhone = (TextView) findViewById(R.id.tv_safePhone);
			ivLock = (ImageView) findViewById(R.id.iv_lock);
			tvReset = (TextView) findViewById(R.id.tv_reset);
			//设置页面
			SetPage();
		}
		
		
		//重置向导
		ResetGuide();
		
		
		
	}
	/**
	 * 设置页面
	 */
	private void SetPage() {
		String mPafePhone = PrefUtils.getString(LostAndFindActivity.this, "SafePhone", null);
		boolean mProtected = PrefUtils.getBoolean(LostAndFindActivity.this, "protected", false);
		tvSafePhone.setText(mPafePhone);
		if (mProtected) {
			ivLock.setImageResource(R.drawable.lock);
		}else {
			ivLock.setImageResource(R.drawable.unlock);
		}
		
		
	}

	private void ResetGuide() {
		tvReset.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				PrefUtils.setBoolean(LostAndFindActivity.this, "configed", false);
				startActivity(new Intent(LostAndFindActivity.this,Setup1Activity.class));
				finish();
			}
		});
		
	}
	
	
	
	
}
