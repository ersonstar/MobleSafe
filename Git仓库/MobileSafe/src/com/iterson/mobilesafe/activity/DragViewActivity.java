package com.iterson.mobilesafe.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.iterson.mobilesafe.R;
import com.iterson.mobilesafe.utils.PrefUtils;

public class DragViewActivity extends Activity {
	private ImageView ivDrag;
	private WindowManager mWM;
	private int mWidth;
	private int mHeight;
	private TextView mTop;
	private TextView mBotton;
	private long[] mHits = new long[2];

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_drag_view);
		
		ivDrag = (ImageView) findViewById(R.id.iv_drag);
		mTop=(TextView) findViewById(R.id.tv_top);
		mBotton = (TextView) findViewById(R.id.tv_botton);
		
		
		mWM = (WindowManager) getSystemService(WINDOW_SERVICE);
		mWidth = mWM.getDefaultDisplay().getWidth();
		mHeight = mWM.getDefaultDisplay().getHeight();
		
		/*
		 * ivDrag不见了强行调用这里
		 * PrefUtils.setInt(DragViewActivity.this, "PosX", 50);
		 *	PrefUtils.getInt(DragViewActivity.this, "PosY", 50);
		 */
		
		//下次进来读取保存的prefUtils
		
		int PosX = PrefUtils.getInt(DragViewActivity.this, "PosX", 0);
		int PosY = PrefUtils.getInt(DragViewActivity.this, "PosY", 0);
		//因为布局还没画好，所以不能用layout方法，用下面方法，意图是直接在画布局的时候，直接设置好ivDrag的位置
		//得到他的layoutParams  需要强转为布局的类型
		RelativeLayout.LayoutParams Params = (RelativeLayout.LayoutParams) ivDrag.getLayoutParams();
		Params.leftMargin = PosX;
		Params.topMargin =PosY;
		
		if (PosY<mHeight/2) {
			//根据显示位置，上下textview 的变化
				mBotton.setVisibility(View.VISIBLE);
				mTop.setVisibility(View.INVISIBLE);
			}else {
				mBotton.setVisibility(View.INVISIBLE);
				mTop.setVisibility(View.VISIBLE);
			}
		
		//ivDrag设置双击事件
		ivDrag.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//参1为数组，参2位数组的第2位，参3为目标数组，参4为目标数组的第0位开始拷贝，参5表示要拷贝数组1到数组2的长度;
				//意思是把数组1里面第二位开始到最后一位拷贝给数组2的第一位到倒数第二位
				System.arraycopy(mHits, 1, mHits, 0, mHits.length-1);
				mHits[mHits.length-1] =SystemClock.uptimeMillis();//把现在系统已经开机的时间给数组最后一位
				if (mHits[0]>=(SystemClock.uptimeMillis()-500)) {//点击2此间隔500毫秒
					ivDrag.layout(mWidth/2-ivDrag.getWidth()/2, mHeight/2 -ivDrag.getHeight()/2,mWidth/2+ivDrag.getWidth()/2 , mHeight/2+ivDrag.getHeight()/2);
					
				}
				PrefUtils.setInt(DragViewActivity.this, "PosX", ivDrag.getLeft());
				PrefUtils.getInt(DragViewActivity.this, "PosY", ivDrag.getTop());
				
				
				
			}
		});
		
		
		
		//ivDrag设置点击事件
		ivDrag.setOnTouchListener(new OnTouchListener() {
			
			private int startX;
			private int startY;

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					startX = (int) event.getRawX();
					startY = (int) event.getRawY();
					System.out.println(startX+";"+startY);
					break;
				case MotionEvent.ACTION_MOVE:
					int endX = (int) event.getRawX();
					int endY = (int) event.getRawY();
					
					int dX = endX - startX;
					int dY = endY - startY;
					
					int l = dX + ivDrag.getLeft();
					int t = dY +ivDrag.getTop();
					int r =dX + ivDrag.getRight();
					int b =dY +ivDrag.getBottom();
						
					//不让textview出屏幕
					if (l<0 || r>mWidth) {
						return true;
					}
					
					if (t<0 ||b >mHeight-35) {
						return true;
					}
					
					//根据显示位置，上下textview 的变化
					if (t<mHeight/2) {
						mBotton.setVisibility(View.VISIBLE);
						mTop.setVisibility(View.INVISIBLE);
					}else {
						mBotton.setVisibility(View.INVISIBLE);
						mTop.setVisibility(View.VISIBLE);
					}
					
					
					ivDrag.layout(l, t, r, b);
					
					startX = endX;
					startY = endY;
					break;
				case MotionEvent.ACTION_UP:
					PrefUtils.setInt(DragViewActivity.this, "PosX", ivDrag.getLeft());
					PrefUtils.setInt(DragViewActivity.this, "PosY", ivDrag.getTop());
					
					break;
					
				default:
					break;
				}
				
				
				
				return false;//必须返回fasle 返回true的话时间会被触摸消耗掉，其他无法接收
			}
		});
		
		
		
		
	}
}
