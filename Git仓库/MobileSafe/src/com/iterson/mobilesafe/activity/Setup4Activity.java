package com.iterson.mobilesafe.activity;

import com.iterson.mobilesafe.R;
import com.iterson.mobilesafe.utils.PrefUtils;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class Setup4Activity extends BaseSetupActivity {

	private CheckBox cbCheck;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup4);
		cbCheck = (CheckBox) findViewById(R.id.cb_find);
		boolean isProtected = PrefUtils.getBoolean(Setup4Activity.this, "protected", false);
		cbCheck.setChecked(isProtected);
		if (isProtected) {
			cbCheck.setText("防盗保护已经开启");
		}else {
			cbCheck.setText("防盗保护已经关闭");
		}
		
		//防盗保护的监听
		cbCheck.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					cbCheck.setText("防盗保护已经开启");
					PrefUtils.setBoolean(Setup4Activity.this, "protected", true);
				}else {
					cbCheck.setText("防盗保护已经关闭");
					PrefUtils.setBoolean(Setup4Activity.this, "protected", false);
				}
			}
		});

	}

	/**
	 * 下一页
	 */
	@Override
	public void showNext() {
		startActivity(new Intent(Setup4Activity.this, LostAndFindActivity.class));
		PrefUtils.setBoolean(this, "configed", true);
		finish();
		overridePendingTransition(R.anim.anim_enter, R.anim.anim_exit);

	}

	/**
	 * 下一页
	 */
	@Override
	public void showPrevious() {
		startActivity(new Intent(Setup4Activity.this, Setup3Activity.class));
		finish();
		overridePendingTransition(R.anim.anim_previ_enter,
				R.anim.anim_previous_exit);

	}

}
