package com.reconova.facecloud;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

public class UploadActivity extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayShowCustomEnabled(true);
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setDisplayShowHomeEnabled(true);

		actionBar.setCustomView(R.layout.actionbar_customer);
		TextView titleTextView = (TextView) actionBar.getCustomView()
				.findViewById(R.id.title);
		titleTextView.setText("报警上传");
		actionBar.getCustomView().findViewById(R.id.camera_switch)
				.setVisibility(View.INVISIBLE);
		ImageView btn_back = (ImageView) actionBar.getCustomView()
				.findViewById(R.id.btn_back);


		btn_back.setVisibility(View.VISIBLE);
		btn_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
		setContentView(R.layout.activity_upload);
	}

}
