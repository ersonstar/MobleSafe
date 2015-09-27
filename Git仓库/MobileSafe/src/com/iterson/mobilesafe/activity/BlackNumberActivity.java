package com.iterson.mobilesafe.activity;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.iterson.mobilesafe.R;
import com.iterson.mobilesafe.db.dao.BlackNumberDao;
import com.iterson.mobilesafe.db.dao.BlackNumberDao.BlackNumberInfo;
import com.iterson.mobilesafe.utils.ToastUtils;

/**
 * 通讯卫士页面 黑名单管理
 * 
 * @author Yang
 * 
 */
public class BlackNumberActivity extends Activity {
	private ListView lvList;
	private ArrayList<BlackNumberInfo> mLists;
	private ProgressBar pbLoading;
	private BlackNumberAdapte mAdapte;
	private boolean isLoading;// 表示是否在加载数据
	private int startIndex;

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (mAdapte == null) {// 第一页数据，
				mAdapte = new BlackNumberAdapte();// 重新给listview设置适配器，listview
													// 会从0开始显示
				lvList.setAdapter(mAdapte);
			} else {// 刷新listview,位置不会发生跳动，会基于原来的listview显示
				mAdapte.notifyDataSetChanged();
			}

			pbLoading.setVisibility(View.GONE);
			startIndex = mLists.size();
			isLoading = false;
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_blacknumber);

		lvList = (ListView) findViewById(R.id.lv_list);
		pbLoading = (ProgressBar) findViewById(R.id.pb_loading);

		// 初始化数据
		initData();

		// 给listview设置一个滑动监听事件
		lvList.setOnScrollListener(new OnScrollListener() {
			// 当滑动状态发生变化时
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {

				if (OnScrollListener.SCROLL_STATE_IDLE == scrollState) {// 当到底部的时候
					if (lvList.getLastVisiblePosition() >= mLists.size() - 1
							&& !isLoading) {// 当lvlist最底部，等于当前显示的最下面一个条目时。注意减去1
						int total = BlackNumberDao.getInstance(
								BlackNumberActivity.this).getTotalCount();
						if (mLists.size() >= total) {// 如果发现当前集合量大于等于数据库总共的量
							ToastUtils.showToast(BlackNumberActivity.this,
									"已经到底了！亲");
						}
						initData();
					}

				}

			}

			// 当正在滑动的时候
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				// TODO Auto-generated method stub

			}
		});
	}

	/**
	 * 初始化数据
	 * 
	 */
	private void initData() {
		isLoading = true;
		pbLoading.setVisibility(View.VISIBLE);
		new Thread() {
			@Override
			public void run() {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				if (mLists == null) {// 如果是第一页数据
					mLists = BlackNumberDao.getInstance(
							BlackNumberActivity.this).findPart(startIndex);
				} else {// 基于原来的List增加集合
					mLists.addAll(BlackNumberDao.getInstance(
							BlackNumberActivity.this).findPart(startIndex));
				}

				handler.sendEmptyMessage(0);
			}
		}.start();

	}

	/**
	 * 添加黑名单按钮点击事件
	 */
	public void addBlackNumber(View v) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		final AlertDialog dialog = builder.create();
		LayoutInflater inflater = this.getLayoutInflater();
		View view = v.inflate(this, R.layout.dialog_blacknumber_input, null);
		dialog.setView(view, 0, 0, 0, 0);

		final EditText etBlackNumber = (EditText) view
				.findViewById(R.id.et_blacknumber);
		Button btnOk = (Button) view.findViewById(R.id.btn_ok);
		Button btnNO = (Button) view.findViewById(R.id.btn_no);
		RadioButton rbPhone = (RadioButton) view.findViewById(R.id.rb_phone);
		RadioButton rbSms = (RadioButton) view.findViewById(R.id.rb_sms);
		RadioButton rbAll = (RadioButton) view.findViewById(R.id.rb_all);
		final RadioGroup rgMode = (RadioGroup) view.findViewById(R.id.rg_mode);

		// 设置确定按钮时间
		btnOk.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String number = etBlackNumber.getText().toString().trim();
				if (TextUtils.isEmpty(number)) {
					ToastUtils.showToast(BlackNumberActivity.this, "输入号码为空！");
					return;
				}
				int mode = 1;
				int id = rgMode.getCheckedRadioButtonId();// 获取当前被选中rb的id
				switch (id) {
				case R.id.rb_phone:
					mode =1;
					break;
				case R.id.rb_sms:
					mode =2;
					break;
				case R.id.rb_all:
					mode =3;
					break;
				}	
				BlackNumberDao.getInstance(BlackNumberActivity.this).add(number, mode);
				dialog.dismiss();
				BlackNumberInfo addInfo = new BlackNumberInfo(number, mode);
				mLists.add(0,addInfo);//参1位角标，表示给第一个加一条数据
				mAdapte.notifyDataSetChanged();
			}
		});

		// 设置取消按钮时间
		btnNO.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		});

		dialog.show();

	}

	/**
	 * 给他添加一个 adapter
	 * 
	 * @author Yang
	 * 
	 */
	class BlackNumberAdapte extends BaseAdapter {

		@Override
		public int getCount() {
			return mLists.size();
		}

		@Override
		public BlackNumberInfo getItem(int position) {
			return mLists.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			// listview的优化 优化过程。先 让list条目缓存起来，重用convertView，减少inflate调用次数，节省内存
			// 使用ViewHolder 减少findviewbyid调用次数，
			// ViewHolder设置为静态的 也是一直优化
			View view = null;
			ViewHolder holder;
			if (convertView == null) {
				view = View.inflate(BlackNumberActivity.this,
						R.layout.list_blacknumber_item, null);
				holder = new ViewHolder();
				holder.tvNumber = (TextView) view.findViewById(R.id.tv_number);
				holder.tvMode = (TextView) view.findViewById(R.id.tv_mode);
				holder.ivDelete=(ImageView) view.findViewById(R.id.iv_delete);
				view.setTag(holder); // tag为object类型 什么数据都能携带
			} else {
				view = convertView;
				holder = (ViewHolder) view.getTag();
			}

			final BlackNumberInfo info = getItem(position);
			holder.tvNumber.setText(info.number);

			switch (info.mode) {
			case 1:
				holder.tvMode.setText("拦截电话");
				break;
			case 2:
				holder.tvMode.setText("拦截短信");
				break;
			case 3:
				holder.tvMode.setText("拦截电话 + 短信");
				break;

			default:
				break;
			}
			
			//设置删除点击事件
			holder.ivDelete.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					
					BlackNumberDao.getInstance(BlackNumberActivity.this).delete(info.number);
					mLists.remove(position);
					mAdapte.notifyDataSetChanged();
				}
			});
				
			return view;
			
		}
		
		
		
	}

	/**
	 * 这个名字 ViewHolder google标准命名的就是这个
	 * 
	 * @author Yang
	 * 
	 */
	static class ViewHolder {
		public TextView tvNumber;
		public TextView tvMode;
		public ImageView ivDelete;
	}

}
