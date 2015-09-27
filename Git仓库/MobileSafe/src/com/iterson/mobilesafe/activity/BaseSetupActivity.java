package com.iterson.mobilesafe.activity;

import com.iterson.mobilesafe.utils.ToastUtils;

import android.app.Activity;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

/**
 * 一个基类，用于跳转页面触摸滑动
 * 
 * @author Yang
 * 
 */
public abstract class BaseSetupActivity extends Activity {
	private GestureDetector mDetector;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mDetector = new GestureDetector(this,
				new GestureDetector.SimpleOnGestureListener() {
					@Override
					public boolean onFling(MotionEvent e1, MotionEvent e2,
							float velocityX, float velocityY) {

						/**
						 * e1开始点，e2结束点 velocityX 横向速度 velocityY 竖直速度
						 */
						float x = e2.getRawX() - e1.getRawX();
						float y = Math.abs(e1.getRawY() - e2.getRawY());
						if (y > 200) {
							ToastUtils.showToast(BaseSetupActivity.this,
									"斜向滑动什么gui");
							return true;
						}
						if (Math.abs(velocityX) < 200) {
							ToastUtils.showToast(BaseSetupActivity.this,
									"滑动太慢了");
							return true;
						}
						if (x >= 100) {// 向上滑动
							showPrevious();
							return true;
						}
						if (x <= -100) {// 向下滑
							showNext();
							return true;
						}

						return super.onFling(e1, e2, velocityX, velocityY);
					}

				});

	}

	public abstract void showNext();

	public abstract void showPrevious();

	/**
	 * 下一页
	 */
	public void next(View v) {
		showNext();
	}

	/**
	 * 上一页
	 */
	public void previous(View v) {
		showPrevious();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		mDetector.onTouchEvent(event);
		return super.onTouchEvent(event);
	}

}
