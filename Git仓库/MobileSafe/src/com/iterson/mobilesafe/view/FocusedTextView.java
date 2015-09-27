package com.iterson.mobilesafe.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 *	获取焦点的textview 
 * @author Yang
 *
 */
public class FocusedTextView extends TextView {

	//如果要设置样式，会走此方法
	public FocusedTextView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		// TODO Auto-generated constructor stub
	}
	
	//如果要设置属性，会走此方法
	public FocusedTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}
	
	//直接代码中new对象，走此方法
	public FocusedTextView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	
	
	/**
	 * 重写isFocused（）方法
	 * 强制返回true，有焦点，跑马灯才会有效果
	 * 让系统误以为此textview一直有焦点
	 * 不管其他，都返回true
	 * 
	 */
	@Override
	public boolean isFocused() {
		return true;
	}
	
}
