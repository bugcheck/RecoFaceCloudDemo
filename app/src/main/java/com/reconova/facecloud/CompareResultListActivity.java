package com.reconova.facecloud;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.reconova.facecloud.model.Matching;
import com.reconova.facecloud.util.CameraInterface;
import com.reconova.facecloud.util.ImageUtil;
import com.reconova.facecloud.util.MyApplication;
import com.reconova.facecloud.util.ToastUtil;

public class CompareResultListActivity extends ActionBarActivity {
	private Bitmap bitmap_source;
	private ImageView image_source;
	private ListView compareResultListView;
	private List<Matching> matchings = null;
	private List<Bitmap> bitmaps = null;
	private List<byte[]> byteList = null;
	private MyApplication myApplication;

	@SuppressWarnings("unchecked")
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
		setContentView(R.layout.activity_compareresultlist);
		myApplication = (MyApplication) this.getApplication();
		image_source = (ImageView) findViewById(R.id.image_source);
		compareResultListView = (ListView) findViewById(R.id.listview_compareresult);

		matchings = (List<Matching>) myApplication.dataHolder
				.get("matchingList");
		byteList = (List<byte[]>) myApplication.dataHolder
				.get("byte_compareList");
		if (matchings == null || byteList == null) {
			ToastUtil.showToast(this, "请重新比对");
			return;
		}

		myApplication.dataHolder.remove("matchingList");
		myApplication.dataHolder.remove("byte_compareList");

		bitmap_source = CameraInterface.getInstance().getBitmap();
		image_source.setImageBitmap(bitmap_source);

		bitmaps = new ArrayList<Bitmap>();
		for (byte[] bytes : byteList) {
			Bitmap bitmap = ImageUtil.Bytes2Bimap(bytes);
			bitmaps.add(bitmap);
		}

		Adapter adapter = new Adapter();
		compareResultListView.setAdapter(adapter);

		compareResultListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
									int position, long id) {
				Intent intent = new Intent(CompareResultListActivity.this,
						CompareResultDetailActivity.class);
				intent.putExtra("bitmap_compare", bitmaps.get(position));
				// 传递bitmap在华为u9508上导致activity跳转失败,原因不晓得，暂时不传递bitmap
				// intent.putExtra("bitmap_source", bitmap_source);
				intent.putExtra("matching", matchings.get(position));
				startActivity(intent);
			}
		});
	}

	/**
	 * 比对结果列表的适配器
	 */
	class Adapter extends BaseAdapter {

		@Override
		public int getCount() {
			return matchings.size();
		}

		@Override
		public Matching getItem(int position) {
			return matchings.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			boolean  flag = false;
			if (convertView == null) {
				flag = true;
				convertView = View.inflate(getApplicationContext(),
						R.layout.item_compareresult_listview, null);
				new ViewHolder(convertView);
			}
			ViewHolder holder = (ViewHolder) convertView.getTag();
			Matching item = getItem(position);



			holder.destination.setText("来源:"
					+ myApplication.faceDB.get(item.getDb_id()));

			holder.name.setText("姓名:" + item.getName());
			holder.similarity.setText(item.getSimilarity() + "%");
			System.out.println("id card is"+Float.parseFloat(String.valueOf(item.getId_card())));
			if(flag == true){
				if(Float.parseFloat(String.valueOf(item.getSimilarity()))>85.00){
					if(holder.destination.getText().toString().contains("在逃人员")){
						holder.name.setTextColor(Color.RED);
						holder.destination.setTextColor(Color.RED);
						holder.similarity.setTextColor(Color.RED);
						System.out.println("测试"+holder.destination.getText().toString());
					}else if(holder.destination.getText().toString().contains("重点人员")){
						holder.name.setTextColor(Color.RED);
						holder.destination.setTextColor(Color.RED);
						holder.similarity.setTextColor(Color.RED);
						System.out.println("测试"+holder.destination.getText().toString());
					}

				}
			}
			holder.avatar.setImageBitmap(bitmaps.get(position));
			return convertView;
		}

		class ViewHolder {
			ImageView avatar;
			TextView destination;
			TextView name;
			TextView similarity;
			TextView idcard;

			public ViewHolder(View view) {
				avatar = (ImageView) view.findViewById(R.id.image_result);
				destination = (TextView) view.findViewById(R.id.tv_destination);
				name = (TextView) view.findViewById(R.id.tv_name);
				similarity = (TextView) view.findViewById(R.id.tv_similarity);
				view.setTag(this);
			}
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		CameraInterface.getInstance().clearBitmap();
	}

}
