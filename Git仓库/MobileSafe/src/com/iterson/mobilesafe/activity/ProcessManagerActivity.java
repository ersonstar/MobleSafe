package com.iterson.mobilesafe.activity;

import java.util.ArrayList;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.iterson.mobilesafe.R;
import com.iterson.mobilesafe.domain.ProcessInfo;
import com.iterson.mobilesafe.engine.ProcessInfoProvider;
import com.iterson.mobilesafe.utils.PrefUtils;
import com.iterson.mobilesafe.utils.ToastUtils;

/**
 * 进程管理页面
 * 
 * @author Yang
 * 
 */
public class ProcessManagerActivity extends Activity {
	private TextView tvMemAvail;
	private TextView tvProgress;
	private ListView lvProcess;
	private ArrayList<ProcessInfo> mList;
	private ArrayList<ProcessInfo> mUserProcess;
	private ArrayList<ProcessInfo> mSystemProcess;
	private ProcessAdater mAdapter;
	private LinearLayout llLoading;
	private int mProcessNum;
	private long mAvalilMemory;
	private long mTotalMemory;

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			mAdapter = new ProcessAdater();
			lvProcess.setAdapter(mAdapter);
			llLoading.setVisibility(View.GONE);
		};
	};
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.avtivity_process_manager);

		tvMemAvail = (TextView) findViewById(R.id.tv_meomory_avail);
		tvProgress = (TextView) findViewById(R.id.tv_running_process);
		lvProcess = (ListView) findViewById(R.id.lv_prcess);
		llLoading = (LinearLayout) findViewById(R.id.ll_loading);
		mProcessNum = ProcessInfoProvider.getRunningProcessNum(this);
		tvProgress.setText(String.format("运行进程数:%d个", mProcessNum));

		mAvalilMemory = ProcessInfoProvider.getAvalilMemory(this);
		mTotalMemory = ProcessInfoProvider.getTotalMemory(this);
		tvMemAvail.setText(String.format("剩余/总内存:%s/%s", Formatter.formatFileSize(this, mAvalilMemory),
				Formatter.formatFileSize(this, mTotalMemory)));

		initProcessData();
		
		lvProcess.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				ProcessInfo info = mAdapter.getItem(position);
				CheckBox cbCheak = (CheckBox) view.findViewById(R.id.cb_process_check);
				if (info != null) {//为null时表示为标题item
					if (getPackageName().equals(info.packageName)) {
						return ; //如果是手机卫士，则不做任何逻辑
					}
					if (info.isChecked) {
						info.isChecked = false;
						cbCheak.setChecked(info.isChecked);
					}else {
						info.isChecked = true;
						cbCheak.setChecked(info.isChecked);
					}
					
				}
				
			}
			
		});
	}

	/**
	 * 初始化进程数据
	 */
	private void initProcessData() {
		llLoading.setVisibility(View.VISIBLE);
		new Thread() {
			public void run() {
				mList = ProcessInfoProvider
						.getRunningProcess(ProcessManagerActivity.this);
				// 把mList分成用户进程和系统进程
				mHandler.sendEmptyMessage(0);
				initProcessList();
			};
		}.start();
	}

	class ProcessAdater extends BaseAdapter {

		@Override
		public int getCount() {
			if (PrefUtils.getBoolean(ProcessManagerActivity.this, "system_prcocess_ischeck",
					false)) {//开启只显示用户进程
				return mUserProcess.size()+1;
			}else {
				return mUserProcess.size()+mSystemProcess.size()+2;
			}
			
		}

		@Override
		public int getViewTypeCount() {
			return 2;
		}

		@Override
		public int getItemViewType(int position) {
			if (position == 0 || position == mUserProcess.size() + 1) {
				return 0;
			} else {
				return 1;
			}
		}

		@Override
		public ProcessInfo getItem(int position) {
			if (position == 0 || position == mUserProcess.size() + 1) {
				return null;
			} else {
				if (position <= mUserProcess.size()) {
					return mUserProcess.get(position - 1);
				} else {
					return mSystemProcess.get(position - mUserProcess.size()
							- 2);
				}
			}

		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			int type = getItemViewType(position);

			switch (type) {
			case 0:
				HearderProcessHolder headerHolder;
				if (convertView == null) {
					headerHolder = new HearderProcessHolder();
					convertView = View.inflate(ProcessManagerActivity.this,
							R.layout.list_process_header, null);
					headerHolder.heared = (TextView) convertView
							.findViewById(R.id.tv_process_header);
					convertView.setTag(headerHolder);
				} else {
					headerHolder = (HearderProcessHolder) convertView.getTag();
				}
				if (position == 0) {
					headerHolder.heared
							.setText("用户应用进程:" + mUserProcess.size());
				} else {
					headerHolder.heared.setText("系统应用进程:"
							+ mSystemProcess.size());
				}
				break;
			case 1: {
				ProcessHolder holder;
				if (convertView == null) {
					holder = new ProcessHolder();
					convertView = View.inflate(ProcessManagerActivity.this,
							R.layout.list_process_item, null);
					holder.icon = (ImageView) convertView
							.findViewById(R.id.iv_process_icon);
					holder.name = (TextView) convertView
							.findViewById(R.id.tv_process_name);
					holder.memory = (TextView) convertView
							.findViewById(R.id.tv_process_memory);
					holder.check = (CheckBox) convertView
							.findViewById(R.id.cb_process_check);
					convertView.setTag(holder);

				} else {
					holder = (ProcessHolder) convertView.getTag();
				}
				ProcessInfo info = getItem(position);
				holder.icon.setImageDrawable(info.icon);
				holder.memory.setText(Formatter.formatFileSize(
						getApplicationContext(), info.memory));
				holder.name.setText(info.name);
				
				if (getPackageName().equals(info.packageName)) {//说明这个item是本应用
					holder.check.setVisibility(View.INVISIBLE);//不让他显示可选
				}else {
					holder.check.setChecked(info.isChecked);
				}
				
				break;
			}
			}

			return convertView;
		}

	}

	static class ProcessHolder {
		private ImageView icon;
		private TextView name;
		private TextView memory;
		private CheckBox check;
	}

	static class HearderProcessHolder {
		private TextView heared;
	}

	/**
	 * 分解mlist为用户进程和系统进程
	 */

	private void initProcessList() {
		mUserProcess = new ArrayList<ProcessInfo>();
		mSystemProcess = new ArrayList<ProcessInfo>();
		for (ProcessInfo info : mList) {
			if (info.isUserProcess) {
				mUserProcess.add(info);
			} else {
				mSystemProcess.add(info);
			}
		}
	}

	/**
	 * 按键全选
	 */
	public void selectAll(View v) {
		for (ProcessInfo info : mUserProcess) {
			if (getPackageName().equals(info.packageName)) {
				continue ; //如果是手机卫士，则不做任何逻辑
			}
			info.isChecked = true;
		}
		for (ProcessInfo info : mSystemProcess) {
			info.isChecked = true;
		}
		mAdapter.notifyDataSetChanged();//刷新listview
		
	}

	/**
	 * 按键反选
	 */
	public void selectInvenrt(View v) {
		for (ProcessInfo info : mUserProcess) {
			if (getPackageName().equals(info.packageName)) {
				continue ; //如果是手机卫士，则不做任何逻辑
			}
			info.isChecked = !info.isChecked; //对自己设自己的非值
		}
		for (ProcessInfo info : mSystemProcess) {
			info.isChecked = !info.isChecked;//对自己设自己的非值
		}
		mAdapter.notifyDataSetChanged();//刷新listview
	}

	/**
	 * 按键一键清理
	 * 权限android.permission.KILL_BACKGROUND_PROCESSES
	 * 
	 */
	public void clear(View v) {
		ActivityManager  am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		//需要清理的列表
		ArrayList<ProcessInfo> killedList = new ArrayList<ProcessInfo>();
		
		for (ProcessInfo info : mUserProcess) {
			if (info.isChecked) {
				//am.killBackgroundProcesses(info.packageName);//杀死进程
				//android.os.Process.killProcess(pid) 也可以杀死进程
				killedList.add(info);
			}
		}
		
		for (ProcessInfo info : mSystemProcess) {
			if (info.isChecked) {
				//am.killBackgroundProcesses(info.packageName);//杀死进程
				//android.os.Process.killProcess(pid) 也可以杀死进程
				killedList.add(info);
			}
		}
		long save = 0;
		//遍历要杀死的进程 把它移除
		for (ProcessInfo processInfo : killedList) {
			if (processInfo.isUserProcess) {
				am.killBackgroundProcesses(processInfo.packageName);//杀死进程
				mUserProcess.remove(processInfo);
				save =save + processInfo.memory;
			}else {
				am.killBackgroundProcesses(processInfo.packageName);//杀死进程
				mSystemProcess.remove(processInfo);
				save =save + processInfo.memory;
			}
		}
		
		
		ToastUtils.showToast(ProcessManagerActivity.this, String.format("帮你杀死了%d个进程,共节约了%s空间", killedList.size(),Formatter.formatFileSize(getApplicationContext(),save)));
		//更新内存大小
		tvProgress.setText(String.format("运行进程数:%d个", mProcessNum-killedList.size()));
		tvMemAvail.setText(String.format("剩余/总内存:%s/%s", Formatter.formatFileSize(this, mAvalilMemory+save),
				Formatter.formatFileSize(this, mTotalMemory)));
		
		mAdapter.notifyDataSetChanged();//刷新listview
	}

	/**
	 * 按键
	 */
	public void setting(View v) {
		startActivityForResult(new Intent(ProcessManagerActivity.this,ProcessSettingActivity.class),0);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		mAdapter.notifyDataSetChanged();//当设置页面跳转回来刷新一次
		super.onActivityResult(requestCode, resultCode, data);
		
	}

}
