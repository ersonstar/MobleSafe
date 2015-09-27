package com.iterson.mobilesafe.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.iterson.mobilesafe.R;
import com.iterson.mobilesafe.utils.PrefUtils;
import com.iterson.mobilesafe.utils.ToastUtils;
/**
 	输入密码页面
 * @author Yang
 *
 */
public class EnterPWDActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_enter_pwd);
		
		TextView tvPackageName = (TextView) findViewById(R.id.tv_package_name);
		ImageView ivPackageIcon = (ImageView) findViewById(R.id.iv_package_icon);
		final EditText etLockPWD = (EditText) findViewById(R.id.et_lock_pwd);
		Button btnEnter = (Button) findViewById(R.id.btn_enter);
		
		final String packageName= getIntent().getStringExtra("packageName");
		tvPackageName.setText(packageName);
		PackageManager pm = getPackageManager();
		try {
			ApplicationInfo info = pm.getApplicationInfo(packageName, 0);//通过包名得到app的详细信息
			Drawable loadIcon = info.loadIcon(pm);//得到图标
			String loadLabel = info.loadLabel(pm).toString();//得到app名字
			tvPackageName.setText(loadLabel);//设置名字
			ivPackageIcon.setImageDrawable(loadIcon);//设置图标
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			tvPackageName.setText(packageName);
			ivPackageIcon.setImageResource(R.drawable.icon_android);
		}
		//监听按键
		btnEnter.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String pwd = etLockPWD.getText().toString().trim();
				String lockPwd = PrefUtils.getString(getApplicationContext(), "lock_pwd", null);
				lockPwd = "123";
				if (TextUtils.equals(pwd, lockPwd)) {
					//通知后台服务跳过对当前包的检测
					Intent intent = new Intent();
					intent.putExtra("packageName", packageName);
					intent.setAction("com.iterson.mobilesafe.SKIP_CHECK");
					sendBroadcast(intent);
					finish();
				}else {
					ToastUtils.showToast(getApplicationContext(), "输入密码错误请重新输入");
				}
				
			}
		});
	}
	
	/**
	 * 监听返回，用户点返回直接跳转到主页面
	 */
	@Override
	public void onBackPressed() {
		//跳转主页面
		//finish
		Intent intent = new Intent();
		intent.setAction(Intent.ACTION_MAIN);
		intent.addCategory(Intent.CATEGORY_HOME);
		startActivity(intent);
		finish();
		
	}
	/**
	 * 所有的物理键
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		
		
		return super.onKeyDown(keyCode, event);
	}
	
}
