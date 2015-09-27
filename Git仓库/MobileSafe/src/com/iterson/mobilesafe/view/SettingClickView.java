package com.iterson.mobilesafe.view;

import com.iterson.mobilesafe.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 定义一个自定义的组合控件
 * 用于设置 调换皮肤
 * @author Yang
 *
 */
public class SettingClickView extends RelativeLayout {

	private TextView tvDesc;
	private TextView tvTitle;
	private static final String NAMESPACE = "http://schemas.android.com/apk/res/com.iterson.mobilesafe";
	public SettingClickView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		initView();
	}

	public SettingClickView(Context context, AttributeSet attrs) {
		super(context, attrs);
		int attributeCount = attrs.getAttributeCount();
		for (int i = 0; i < attributeCount; i++) {
			String attributeName = attrs.getAttributeName(i);
			String attributeValue = attrs.getAttributeValue(i);
			System.out.println(attributeName+"="+attributeValue);
		}
		initView();	}

	public SettingClickView(Context context) {
		super(context);
		initView();	}
	/**
	 * 初始化布局
	 */
	private void initView(){
		//将布局文件填充给当前relativelayout
		View.inflate(getContext(), R.layout.view_setting_click_item, this);
		tvDesc = (TextView) findViewById(R.id.tv_desc);
		tvTitle = (TextView) findViewById(R.id.tv_title);
	}
	
	/*
	 * 设置标题
	 */
	public void setTitle(String title) {
		tvTitle.setText(title);
	}
	/**
	 * 设置描述
	 */
	public void setDesc(String desc){
		tvDesc.setText(desc);
	}
	
	
	

}
