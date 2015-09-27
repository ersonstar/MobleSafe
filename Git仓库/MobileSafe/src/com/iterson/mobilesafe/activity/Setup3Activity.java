package com.iterson.mobilesafe.activity;


import com.iterson.mobilesafe.R;
import com.iterson.mobilesafe.utils.PrefUtils;
import com.iterson.mobilesafe.utils.ToastUtils;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

public class Setup3Activity extends BaseSetupActivity {

	private EditText etSafePhone;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup3);
		
		etSafePhone = (EditText) findViewById(R.id.et_safePhone);
		String safePhone = PrefUtils.getString(this, "SafePhone", "");
		etSafePhone.setText(safePhone);

	}

	/**
	 * 选择联系人按钮逻辑
	 */
	public void pick(View v) {
		startActivityForResult(new Intent(Setup3Activity.this,SelectContactActivity.class),0);
		
	}
	/**
	 * 接收结果
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode==RESULT_OK) {
			String phone = data.getStringExtra("phone");
			phone = phone.replace("-", "").replace(" ", "");
			etSafePhone.setText(phone);
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	@Override
	public void showNext() {
		// 下一步保存 设置的安全号码
		String SafePhone = etSafePhone.getText().toString().trim();
		if (TextUtils.isEmpty(SafePhone)) {
			ToastUtils.showToast(Setup3Activity.this, "号码不能为空");
			return;
		} else {
			PrefUtils.setString(Setup3Activity.this, "SafePhone", SafePhone);
			startActivity(new Intent(Setup3Activity.this, Setup4Activity.class));
			finish();
			overridePendingTransition(R.anim.anim_enter, R.anim.anim_exit);
		}

	}

	@Override
	public void showPrevious() {
		startActivity(new Intent(Setup3Activity.this, Setup2Activity.class));
		finish();
		overridePendingTransition(R.anim.anim_previ_enter,
				R.anim.anim_previous_exit);
	}

}
