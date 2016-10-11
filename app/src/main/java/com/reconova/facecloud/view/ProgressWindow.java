package com.reconova.facecloud.view;

import android.app.ActionBar.LayoutParams;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.reconova.facecloud.R;


public class ProgressWindow {

	private PopupWindow progressWindow;
	private Context context;

	public ProgressWindow(Context context) {
		this.context = context;
	}

	public void dismiss() {
		if (progressWindow != null) {
			progressWindow.dismiss();
		}
	}

	/**
	 * 弹出加载进度条
	 */
	public void showProgressWindows(String title, String msg, View locationView) {
		View view = LayoutInflater.from(context).inflate(
				R.layout.progress_layout, null);
		TextView tv_title = (TextView) view.findViewById(R.id.title_tv);
		TextView tv_msg = (TextView) view.findViewById(R.id.msg_tv);
		tv_title.setText(title);
		tv_msg.setText(msg);
		if (progressWindow == null) {
			progressWindow = new PopupWindow(context);
		}
		progressWindow.setFocusable(true); // 设置PopupWindow可获得焦点
		progressWindow.setTouchable(true); // 设置PopupWindow可触摸
		progressWindow.setOutsideTouchable(true); // 设置非PopupWindow区域可触摸
		progressWindow.setContentView(view);
		progressWindow.setWidth(LayoutParams.MATCH_PARENT); // 设置SelectPicPopupWindow弹出窗体的宽
		progressWindow.setHeight(LayoutParams.MATCH_PARENT); // 设置SelectPicPopupWindow弹出窗体的高

		ColorDrawable dw = new ColorDrawable(0xb0000000); // 实例化一个ColorDrawable颜色为半透明
		progressWindow.setBackgroundDrawable(dw); // 设置SelectPicPopupWindow弹出窗体的背景
		progressWindow.showAtLocation(locationView, Gravity.CENTER, 0, 0);
		progressWindow.update();
	}

}
