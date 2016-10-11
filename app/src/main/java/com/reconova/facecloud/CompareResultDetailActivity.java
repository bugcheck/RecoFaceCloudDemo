package com.reconova.facecloud;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.reconova.facecloud.model.Matching;
import com.reconova.facecloud.util.CameraInterface;
import com.reconova.facecloud.util.MyApplication;

public class CompareResultDetailActivity extends ActionBarActivity {
	private ImageView image_original;
	private ImageView image_compare;
	private TextView tv_detail;
	private TextView tv_similarity;
	private Matching matching;
	private Bitmap bitmap_compare;
	private Bitmap bitmap_source;
	private MyApplication myApplication;
	private Button liandong;
	private Button baojing;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayShowCustomEnabled(true);
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setDisplayShowHomeEnabled(true);

		actionBar.setCustomView(R.layout.actionbar_customer);
		TextView titleTextView = (TextView) actionBar.getCustomView()
				.findViewById(R.id.title);
		titleTextView.setText("比对结果");
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

		setContentView(R.layout.activity_compareresultdetail);

		Log.i("test", "CompareResultDetailActivity  oncreate");
		myApplication = (MyApplication) this.getApplication();

		image_original = (ImageView) findViewById(R.id.image_original);
		image_compare = (ImageView) findViewById(R.id.image_compare);
		tv_similarity = (TextView) findViewById(R.id.tv_similarity);
		tv_detail = (TextView) findViewById(R.id.tv_detail);

		matching = (Matching) getIntent().getSerializableExtra("matching");
		bitmap_compare = getIntent().getParcelableExtra("bitmap_compare");
		// bitmap_source = getIntent().getParcelableExtra("bitmap_source");
		Log.i("test", "aaaaa=====" + CameraInterface.getInstance().getBitmap());
		if (CameraInterface.getInstance().getBitmap() != null)
			image_original.setImageBitmap(CameraInterface.getInstance().getBitmap());
		image_compare.setImageBitmap(bitmap_compare);
		tv_similarity.setText(matching.getSimilarity() + "%");
		tv_detail
				.setText("和" + myApplication.faceDB.get(matching.getDb_id())
						+ "中的" + matching.getName() + "匹配度为"
						+ matching.getSimilarity()+",身份证号码是"+matching.getId_card());

		baojing = (Button) findViewById(R.id.btn_upload);
		liandong = (Button)findViewById(R.id.btn_liandong);

		baojing.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				//finish();
				Intent intent = new Intent(CompareResultDetailActivity.this,
						UploadActivity.class);

				startActivity(intent);
			}
		});

		liandong.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				//finish();
				Intent intent = new Intent(CompareResultDetailActivity.this,
						LinkageActivity.class);

				startActivity(intent);
			}
		});

	}
}
