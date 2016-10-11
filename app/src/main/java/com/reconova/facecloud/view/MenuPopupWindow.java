package com.reconova.facecloud.view;

import android.app.ActionBar.LayoutParams;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.reconova.facecloud.CompareSettingsActivity;
import com.reconova.facecloud.R;
import com.reconova.facecloud.RemoteDBSettingsActivity;

public class MenuPopupWindow {

	private PopupWindow progressWindow;
	private Context context;

	public MenuPopupWindow(Context context) {
		this.context = context;
	}

	public void dismiss() {
		if (progressWindow != null) {
			progressWindow.dismiss();
		}
	}

	/**
	 * 弹出设置菜单
	 */
	public void showWindow(View locationView) {
		View view = LayoutInflater.from(context).inflate(R.layout.menu_pop,
				null);
		LinearLayout ll_group = (LinearLayout) view.findViewById(R.id.ll_group);
		TextView setting_data = (TextView) view.findViewById(R.id.setting_data);
		TextView setting_contrast = (TextView) view
				.findViewById(R.id.setting_contrast);
		ll_group.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dismiss();
			}
		});

		setting_data.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				context.startActivity(new Intent(context,
						RemoteDBSettingsActivity.class));
				dismiss();
			}
		});

		setting_contrast.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				context.startActivity(new Intent(context,
						CompareSettingsActivity.class));
				dismiss();
			}
		});

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
		// progressWindow.showAtLocation(locationView, Gravity.START, 0, -150);
		progressWindow.showAsDropDown(locationView, 0, 0);
		progressWindow.setAnimationStyle(R.style.menu_popup_anim_style);
		progressWindow.update();
	}

}
