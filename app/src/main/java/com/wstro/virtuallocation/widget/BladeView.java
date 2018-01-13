package com.wstro.virtuallocation.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.wstro.app.common.utils.DeviceUtils;

public class BladeView extends View {
	private OnItemClickListener mOnItemClickListener;
	String[] chars = { "#", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K",
			"L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X",
			"Y", "Z" };
	int choose = -1;
	Paint paint = new Paint();
	boolean showBkg = false;
	private PopupWindow mPopupWindow;
	private TextView mPopupText;

	private Handler handler = new Handler();

	public BladeView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public BladeView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public BladeView(Context context) {
		super(context);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (showBkg) {
			canvas.drawColor(Color.parseColor("#00000000"));
		}
		int height = getHeight();
		int width = getWidth();
		int singleHeight = height / chars.length;
		for (int i = 0; i < chars.length; i++) {
			paint.setColor(Color.parseColor("#444444"));
			//paint.setTypeface(Typeface.DEFAULT_BOLD);
			paint.setTextSize(DeviceUtils.dp2px(getContext(),12));
			paint.setFakeBoldText(true);
			paint.setAntiAlias(true);
			if (i == choose) {
				paint.setColor(Color.parseColor("#3F51B5"));
			}
			float xPos = width / 2 - paint.measureText(chars[i]) / 2;
			float yPos = singleHeight * i + singleHeight;
			canvas.drawText(chars[i], xPos, yPos, paint);
			paint.reset();
		}
	}

	public void setLetters(String[] letters){
		this.chars = letters;
		invalidate();
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		final int action = event.getAction();
		final float y = event.getY();
		final int oldChoose = choose;
		final int c = (int) (y / getHeight() * chars.length);
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			showBkg = true;
			if (oldChoose != c) {
				if (c > 0 && c < chars.length) {
					performItemClicked(c);
					choose = c;
					invalidate();
				}
			}
			break;
		case MotionEvent.ACTION_MOVE:
			if (oldChoose != c) {
				if (c > 0 && c < chars.length) {
					performItemClicked(c);
					choose = c;
					invalidate();
				}
			}
			break;
		case MotionEvent.ACTION_UP:
			showBkg = false;
			choose = -1;
			dismissPopup();
			invalidate();
			break;
		}
		return true;
	}

	private void showPopup(int item) {
		if (mPopupWindow == null) {
			handler.removeCallbacks(dismissRunnable);
			mPopupText = new TextView(getContext());
			mPopupText.setBackgroundColor(Color.GRAY);
			mPopupText.setTextColor(Color.parseColor("#3F51B5"));
			mPopupText.setTextSize(40);
			mPopupText.setGravity(Gravity.CENTER_HORIZONTAL
					| Gravity.CENTER_VERTICAL);
			mPopupWindow = new PopupWindow(mPopupText, DeviceUtils.dp2px(getContext(),80), DeviceUtils.dp2px(getContext(),80));
		}
		String text = "";
		if (item == 0) {
			text = "#";
		} else {
			text = Character.toString((char) ('A' + item - 1));
		}
		mPopupText.setText(text);
		if (mPopupWindow.isShowing()) {
//			mPopupWindow.update();
		} else {
			mPopupWindow.showAtLocation(getRootView(),
					Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);
		}
	}

	private void dismissPopup() {
		handler.postDelayed(dismissRunnable, 400);
	}

	Runnable dismissRunnable = new Runnable() {
		@Override
		public void run() {
			if (mPopupWindow != null) {
				mPopupWindow.dismiss();
			}
		}
	};

	public boolean onTouchEvent(MotionEvent event) {
		return super.onTouchEvent(event);
	}

	public void setOnItemClickListener(OnItemClickListener listener) {
		mOnItemClickListener = listener;
	}

	private void performItemClicked(int item) {
		if (mOnItemClickListener != null) {
			mOnItemClickListener.onItemClick(chars[item]);
			showPopup(item);
		}
	}

	public interface OnItemClickListener {
		void onItemClick(String s);
	}

}