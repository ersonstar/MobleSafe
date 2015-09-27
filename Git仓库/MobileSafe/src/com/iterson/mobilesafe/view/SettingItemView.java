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
 * @author Yang
 *
 */
public class SettingItemView extends RelativeLayout {

	private TextView tvDesc;
	private TextView tvTitle;
	private CheckBox cbCheck;
	private static final String NAMESPACE = "http://schemas.android.com/apk/res/com.iterson.mobilesafe";
	private String mTitle;
	private String mDesc_On;
	private String mDesc_off;
	public SettingItemView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		initView();
	}

	public SettingItemView(Context context, AttributeSet attrs) {
		super(context, attrs);
		int attributeCount = attrs.getAttributeCount();
		for (int i = 0; i < attributeCount; i++) {
			String attributeName = attrs.getAttributeName(i);
			String attributeValue = attrs.getAttributeValue(i);
			System.out.println(attributeName+"="+attributeValue);
		}
		//得到属性描述
		mTitle = attrs.getAttributeValue(NAMESPACE, "title");
		mDesc_On = attrs.getAttributeValue(NAMESPACE, "desc_on");
		mDesc_off = attrs.getAttributeValue(NAMESPACE, "desc_off");
		
		
		initView();	}

	public SettingItemView(Context context) {
		super(context);
		initView();	}
	/**
	 * 初始化布局
	 */
	private void initView(){
		//将布局文件填充给当前relativelayout
		View.inflate(getContext(), R.layout.view_setting_item, this);
		tvDesc = (TextView) findViewById(R.id.tv_desc);
		tvTitle = (TextView) findViewById(R.id.tv_title);
		cbCheck = (CheckBox) findViewById(R.id.cb_check);
		tvTitle.setText(mTitle);
		
		
		
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
	
	public boolean isChecked(){
		return cbCheck.isChecked();//返回checkbox是否被勾选
	}
	/**
	 * 设置勾选状态
	 */
	public void setChecked(boolean checked){
		//根据选择状态更新描述
		if (checked) {
			tvDesc.setText(mDesc_On);
		}else {
			tvDesc.setText(mDesc_off);
		}
		cbCheck.setChecked(checked);
	
	}
	
	
	

}
