package com.iterson.mobilesafe.activity;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.TextView;

import com.iterson.mobilesafe.R;
import com.iterson.mobilesafe.db.dao.CommonNumberDao;
import com.iterson.mobilesafe.db.dao.CommonNumberDao.CommonNumberChild;
import com.iterson.mobilesafe.db.dao.CommonNumberDao.CommonNumberGroup;
/**
 * 常用号码查询
 * @author Yang
 *
 */
public class CommonNumberActivity extends Activity {
	private ExpandableListView elvList;
	private ArrayList<CommonNumberGroup> mList;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_commom_number);
		
		elvList = (ExpandableListView) findViewById(R.id.elv_list);	
		
		mList = CommonNumberDao.getCommonNmberGroups();
		
		final CommonNumberAdapter mAdapter = new CommonNumberAdapter();
		elvList.setAdapter(mAdapter);
		
		//孩子被点击事件监听
		elvList.setOnChildClickListener(new OnChildClickListener() {
			
			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) {
				CommonNumberChild child = mAdapter.getChild(groupPosition, childPosition);
				//条状到系统打电话页面
				Intent intent = new Intent();
				intent.setAction(Intent.ACTION_DIAL);
				intent.setData(Uri.parse("tel:"+child.number));
				startActivity(intent);
				
				
				return false;
			}
		});
		
		
	}
	class CommonNumberAdapter extends BaseExpandableListAdapter{
		/**
		 * 获取group数量
		 */
		@Override
		public int getGroupCount() {
			return mList.size();
		}
		/**
		 * 获取组下孩子数量
		 */
		@Override
		public int getChildrenCount(int groupPosition) {
			return mList.get(groupPosition).child.size();
		}
		/**
		 * 类似listview的getItem
		 * 获取组信息
		 */
		@Override
		public CommonNumberGroup getGroup(int groupPosition) {
			return mList.get(groupPosition);
		}
		/*
		 * 返回孩子
		 * 获取孩子
		 */
		@Override
		public CommonNumberChild getChild(int groupPosition, int childPosition) {
			return mList.get(groupPosition).child.get(childPosition);
		}
		/*
		 * 
		 */
		@Override
		public long getGroupId(int groupPosition) {
			return groupPosition;
		}

		@Override
		public long getChildId(int groupPosition, int childPosition) {
			return childPosition;
		} 
		/*
		 *是否有稳定的id
		 */
		@Override
		public boolean hasStableIds() {
			return false;
		}

		@Override
		public View getGroupView(int groupPosition, boolean isExpanded,
				View convertView, ViewGroup parent) {
			TextView tvGoup = new TextView(CommonNumberActivity.this);
			String name = mList.get(groupPosition).name;
			tvGoup.setText("             "+name);
			tvGoup.setTextColor(Color.RED);
			tvGoup.setTextSize(20);
			return tvGoup;
		}

		@Override
		public View getChildView(int groupPosition, int childPosition,
				boolean isLastChild, View convertView, ViewGroup parent) {
			TextView tvChild = new TextView(CommonNumberActivity.this);
			CommonNumberChild child = mList.get(groupPosition).child.get(childPosition);
			tvChild.setText(child.name+"\n"+child.number);
			tvChild.setTextColor(Color.BLACK);
			tvChild.setTextSize(16);
			return tvChild;
		}
		/**
		 * 孩子能否被点击
		 */
		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition) {
			// TODO Auto-generated method stub
			return true;
		}
		
	}
	
	
}	
