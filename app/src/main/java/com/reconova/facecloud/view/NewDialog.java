package com.reconova.facecloud.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.sax.StartElementListener;

import com.reconova.facecloud.CompareResultListActivity;
import com.reconova.facecloud.MainActivity;
import com.reconova.facecloud.R;
import com.reconova.facecloud.RetrievalActivity;
import com.reconova.facecloud.SplashActivity;

public class NewDialog {

	public static interface ExitListener {
		public void onExit();
	}

	Dialog mDialog;
	Activity mActivity;
	ExitListener mExitListener;

	public void setExitListener(ExitListener exitListener) {
		mExitListener = exitListener;
	}

	public NewDialog(Activity activity) {
		mActivity = activity;

		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		builder.setCancelable(true)
				.setPositiveButton(R.string.comeback, new CancelEvent())
				.setNegativeButton(R.string.sure,new ExistEvent() )
				.setTitle(R.string.title_for_exist)
				.setMessage("未找到匹配结果，是否通过主网调取此人信息？");
		mDialog = builder.create();
	}

	public void show() {
		if (!mDialog.isShowing()) {
			mDialog.show();
		}
	}

	public void dismiss() {
		mDialog.dismiss();
	}

	private class ExistEvent implements OnClickListener {

		@Override
		public void onClick(DialogInterface dialog, int which) {

			Intent intent = new Intent(mActivity,RetrievalActivity.class);
			mActivity.startActivity(intent);

		}

	}

	private class CancelEvent implements OnClickListener {

		@Override
		public void onClick(DialogInterface dialog, int which) {
			dialog.dismiss();
		}

	}

}
