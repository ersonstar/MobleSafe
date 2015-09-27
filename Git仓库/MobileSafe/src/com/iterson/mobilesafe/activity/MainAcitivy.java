package com.iterson.mobilesafe.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.iterson.mobilesafe.R;
import com.iterson.mobilesafe.utils.MD5Utils;
import com.iterson.mobilesafe.utils.PrefUtils;
import com.iterson.mobilesafe.utils.ToastUtils;
import com.startapp.android.publish.StartAppAd;
import com.startapp.android.publish.StartAppSDK;

public class MainAcitivy extends Activity {

	private String[] mItems = new String[] { "手机防盗", "通信卫士", "软件管理", "进程管理",
			"流量统计", "手机杀毒", "缓存清理", "高级工具", "设置中心" };
	private Integer[] mPic = new Integer[] { R.drawable.home_safe,
			R.drawable.home_callmsgsafe, R.drawable.home_apps,
			R.drawable.home_taskmanager, R.drawable.home_netmanager,
			R.drawable.home_trojan, R.drawable.home_sysoptimize,
			R.drawable.home_tools, R.drawable.home_settings

	};
	//广告提供
	private StartAppAd startAppAd = new StartAppAd(this);
	@Override
	//广告提供
	public void onResume() {
	    super.onResume();
	    startAppAd.onResume();
	}
	//广告提供
	@Override
	public void onPause() {
	    super.onPause();
	    startAppAd.onPause();
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		//startAPP广告
		StartAppSDK.init(this,"208409082", true);
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		GridView gvHome = (GridView) findViewById(R.id.gv_home);
		// 初始化一个Adapter
		HomeAdper adper = new HomeAdper();
		// 给gridview设置adapter
		gvHome.setAdapter(adper);

		// 九宫格点击事件
		gvHome.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				switch (position) {
				case 0:
					// 弹窗
					showMobileSafeDialog();
					break;
				case 1:
					startActivity(new Intent(MainAcitivy.this,
							BlackNumberActivity.class));
					break;
				case 2:
					startActivity(new Intent(MainAcitivy.this,
							AppManagerActivity.class));
					break;
				case 3:
					startActivity(new Intent(MainAcitivy.this,
							ProcessManagerActivity.class));
					break;
				case 4:
					startActivity(new Intent(MainAcitivy.this,
							TrafficActivity.class));
					break;
				case 5:
					startActivity(new Intent(MainAcitivy.this,
							AntivirusActivity.class));
					break;
				case 6:
					startActivity(new Intent(MainAcitivy.this,
							ClearCacheActivity.class));
					break;
				case 7:
					startActivity(new Intent(MainAcitivy.this,
							AtoolsActivity.class));
					break;
				case 8:
					Intent intent = new Intent(MainAcitivy.this,
							SettingActivity.class);
					startActivity(intent);
					break;

				default:
					break;
				}

			}
		});

	}

	/**
	 * 手机防盗弹窗
	 */
	protected void showMobileSafeDialog() {
		// 判断是否为第一次登陆，如果是设置密码，如果不是输入密码
		String savePassword = PrefUtils.getString(MainAcitivy.this, "password",
				null);
		if (!TextUtils.isEmpty(savePassword)) {
			showPasswordInputDialog();
		} else {// 密码不存在，
			showPassWordSetDialog();
		}

	}

	/**
	 * 输入密码
	 */
	private void showPasswordInputDialog() {

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		final AlertDialog dialog = builder.create();
		View view = View.inflate(this, R.layout.dialog_password_input, null);
		dialog.setView(view, 0, 0, 0, 0);// 去掉上下左右边距,向下兼容
		dialog.show();
		Button btnOK = (Button) view.findViewById(R.id.btn_ok);
		Button btnNo = (Button) view.findViewById(R.id.btn_no);
		final EditText etPassword = (EditText) view
				.findViewById(R.id.et_password);

		// 设置点击事件
		btnOK.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String password = etPassword.getText().toString();
				// 登陆是把密码加密和保存的加密密码比较
				String encode = MD5Utils.encode(password);
				if (encode.equals(PrefUtils.getString(MainAcitivy.this,
						"password", null))) {
					ToastUtils.showToast(MainAcitivy.this, "登陆成功");
					// 跳转页面
					startActivity(new Intent(MainAcitivy.this,
							LostAndFindActivity.class));
					dialog.dismiss();
				} else {
					ToastUtils.showToast(MainAcitivy.this, "密码错误");
				}
			}
		});
		btnNo.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
	}

	/**
	 * 第一次设置密码弹窗
	 */
	private void showPassWordSetDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		final AlertDialog dialog = builder.create();
		// 设置一个自己定义的dialog
		View view = View.inflate(this, R.layout.dialog_password_set, null);
		// dialog.setView(view);
		dialog.setView(view, 0, 0, 0, 0);// 去掉上下左右边距,向下兼容
		Button btnOK = (Button) view.findViewById(R.id.btn_ok);
		Button btnNo = (Button) view.findViewById(R.id.btn_no);
		final EditText etPassword = (EditText) view
				.findViewById(R.id.et_password);
		final EditText etPassswordConfirm = (EditText) view
				.findViewById(R.id.et_password_confirm);

		// 设置点击事件
		btnOK.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 检验数据合法性
				String passsword = etPassword.getText().toString();
				String passwordConfirm = etPassswordConfirm.getText()
						.toString();

				if (!TextUtils.isEmpty(passsword)
						&& !TextUtils.isEmpty(passwordConfirm)) {
					if (passsword.equals(passwordConfirm)) {
						// 将密码保存到本地 封装的sharedPrenfences 并且用MD5加密
						PrefUtils.setString(MainAcitivy.this, "password",
								MD5Utils.encode(passsword));
						ToastUtils.showToast(MainAcitivy.this, "密码设置成功");
						startActivity(new Intent(MainAcitivy.this,
								Setup1Activity.class));
						dialog.dismiss();
					} else {
						ToastUtils.showToast(MainAcitivy.this, "两次密码不一致");
					}

				} else {
					ToastUtils.showToast(MainAcitivy.this, "输入不能为空");

				}

			}
		});

		btnNo.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});

		dialog.show();

	}

	/**
	 * GridView 适配器
	 * 
	 * @author Yang
	 * 
	 */
	class HomeAdper extends BaseAdapter {

		@Override
		public int getCount() {
			return mItems.length;
		}

		@Override
		public Object getItem(int position) {
			return mItems[position];
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// 依次设置 9个图标和字
			View view = View.inflate(MainAcitivy.this, R.layout.list_item_home,
					null);
			TextView tvTitle = (TextView) view.findViewById(R.id.tv_title);
			ImageView ivIcon = (ImageView) view.findViewById(R.id.iv_icon);
			tvTitle.setText(mItems[position]);
			ivIcon.setImageResource(mPic[position]);
			return view;
		}
	}
}