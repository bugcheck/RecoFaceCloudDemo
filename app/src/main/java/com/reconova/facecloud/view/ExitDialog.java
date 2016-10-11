package com.reconova.facecloud.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

import com.reconova.facecloud.R;

public class ExitDialog {

	public static interface ExitListener {
		public void onExit();
	}

	Dialog mDialog;
	Activity mActivity;
	ExitListener mExitListener;

	public void setExitListener(ExitListener exitListener) {
		mExitListener = exitListener;
	}

	public ExitDialog(Activity activity) {
		mActivity = activity;

		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		builder.setCancelable(true)
				.setPositiveButton(R.string.comeback, new CancelEvent())
				.setNegativeButton(R.string.sure,new ExistEvent() )
				.setTitle(R.string.title_for_exist)
				.setMessage(R.string.question_for_exist);
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
			if (mExitListener != null) {
				mExitListener.onExit();
			}
			mActivity.finish();
		}

	}

	private class CancelEvent implements OnClickListener {

		@Override
		public void onClick(DialogInterface dialog, int which) {
			dialog.dismiss();
		}

	}

}
