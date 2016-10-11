package com.reconova.facecloud;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.reconova.facecloud.util.SettingsHelper;
import com.reconova.facecloud.view.TextMoveLayout;
import com.reconova.facecloud.view.WheelView;

public class CompareSettingsActivity extends ActionBarActivity {
	private WheelView compare_num;
	private SettingsHelper settingsHelper;
	private int num=-1;
	public  static int setting_similarity = 70;
	private TextView tv_similarity;
	private SeekBar seekbar_similarity;
	private float moveStep = 0;
	private int screenWidth;
	private ViewGroup.LayoutParams layoutParams;
	private TextMoveLayout textMoveLayout;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_compare_settings);
		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayShowCustomEnabled(true);
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setDisplayShowHomeEnabled(true);

		actionBar.setCustomView(R.layout.actionbar_customer);
		TextView titleTextView = (TextView) actionBar.getCustomView()
				.findViewById(R.id.title);
		titleTextView.setText("比对设置");
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
		seekbar_similarity = (SeekBar) findViewById(R.id.seekbar_similarity);
		textMoveLayout = (TextMoveLayout)findViewById(R.id.textLayout);
		screenWidth = this.getWindowManager().getDefaultDisplay()
				.getWidth();
		tv_similarity = new TextView(this);
		// tv_similarity.setBackgroundColor(Color.rgb(245, 245, 245));
		tv_similarity.setTextColor(Color.rgb(0, 161, 229));
		tv_similarity.setTextSize(16);
		layoutParams = new ViewGroup.LayoutParams(screenWidth, 40);
		textMoveLayout.addView(tv_similarity, layoutParams);
		seekbar_similarity.setProgress(setting_similarity);
		moveStep = (float) (((float) screenWidth / (float) 100) * 0.8);
		tv_similarity.layout((int) ((setting_similarity * moveStep)), 10, screenWidth,
				70);
		tv_similarity.setText(setting_similarity + "%");

		seekbar_similarity
				.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

					@Override
					public void onStopTrackingTouch(SeekBar seekBar) {
						setting_similarity = seekBar.getProgress();
					}

					@Override
					public void onStartTrackingTouch(SeekBar seekBar) {

					}

					@Override
					public void onProgressChanged(SeekBar seekBar,
												  int progress, boolean fromUser) {
						tv_similarity.layout((int) (progress * moveStep), 10,
								screenWidth, 70);
						tv_similarity.setText(progress + "%");
					}
				});




		settingsHelper = new SettingsHelper(this);

		compare_num = (WheelView) findViewById(R.id.compare_num);
		compare_num.setOffset(1);
		List<String> numList = new ArrayList<String>();
		for (int i = 0; i <= 100; i++) {
			numList.add(i + "");
		}
		compare_num.setItems(numList);
		compare_num.setOnWheelViewListener(new WheelView.OnWheelViewListener() {
			@Override
			public void onSelected(int selectedIndex, String item) {
				num = selectedIndex;
			}
		});

	}

	@Override
	protected void onResume() {
		super.onResume();
		compare_num.setSeletion(settingsHelper.getCompareNum());
	}

	@Override
	protected void onPause() {
		super.onPause();
		if(num!=-1){
			//num!=-1表示有选择匹配数量，此时应保存选择的匹配数量
			settingsHelper.SetCompareNum(num - 1);
		}
	}

}
