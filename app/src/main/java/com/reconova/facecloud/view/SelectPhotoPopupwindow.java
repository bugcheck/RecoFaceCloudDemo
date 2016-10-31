package com.reconova.facecloud.view;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.reconova.facecloud.R;

public class SelectPhotoPopupwindow {

	/** PopupWindow for select image or take a photos.*/
	PopupWindow popupWindow;
	/** Used for take real photo. */
	SelectPhotoAction selectPhotoAction;
	/** Activity where the popupWindow belong to.*/
	Activity activity;

	public SelectPhotoPopupwindow(Activity activity, SelectPhotoAction action) {
		this.activity = activity;
		selectPhotoAction = action;
	}

	/**
	 * 弹出选择图片来源的对话框
	 */
	public void showPopupWindow(View location) {
		View view = (RelativeLayout) LayoutInflater.from(activity)
				.inflate(R.layout.camera_popmenu_layout, null);
		Button camera = (Button) view.findViewById(R.id.tv_take_photo);
		Button photos = (Button) view.findViewById(R.id.tv_choose_photo);
		View outsideView = view.findViewById(R.id.outside);
		outsideView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				popupWindow.dismiss();
			}

		});
		camera.setOnClickListener(onClickListener);
		photos.setOnClickListener(onClickListener);
		if (popupWindow == null) {
			popupWindow = new PopupWindow(location);
		}
		popupWindow.setFocusable(true); // 设置PopupWindow可获得焦点
		popupWindow.setTouchable(true); // 设置PopupWindow可触摸
		popupWindow.setOutsideTouchable(true); // 设置非PopupWindow区域可触摸
		popupWindow.setContentView(view);
		popupWindow.setWidth(LayoutParams.MATCH_PARENT); // 设置SelectPicPopupWindow弹出窗体的宽
		popupWindow.setHeight(LayoutParams.MATCH_PARENT); // 设置SelectPicPopupWindow弹出窗体的高

		ColorDrawable dw = new ColorDrawable(0xb0000000); // 实例化一个ColorDrawable颜色为半透明
		popupWindow.setBackgroundDrawable(dw); // 设置SelectPicPopupWindow弹出窗体的背景
		popupWindow.showAtLocation(location,
				Gravity.CENTER, 0, 0);
		popupWindow.update();
	}

	public void dismiss() {
		if (popupWindow != null) {
			popupWindow.dismiss();
		}
	}

	OnClickListener onClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
				// 拍照
				case R.id.tv_take_photo:
					dismiss();
					if (selectPhotoAction != null) {
						selectPhotoAction.takePhoto();
					}
					break;
				// 相册
				case R.id.tv_choose_photo:
					dismiss();
					if (selectPhotoAction != null) {
						selectPhotoAction.choosePhoto();
					}
					break;
				default:
					dismiss();
					break;
			}
		}
	};

	public static interface SelectPhotoAction {
		public void takePhoto();
		public void choosePhoto();
	}

}
