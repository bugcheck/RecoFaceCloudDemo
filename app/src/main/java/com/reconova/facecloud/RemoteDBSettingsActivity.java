package com.reconova.facecloud;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.ConnectException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.reconova.facecloud.action.CompareAction;
import com.reconova.facecloud.model.FaceDB;
import com.reconova.facecloud.model.FaceDB_Result;
import com.reconova.facecloud.util.HttpUtil;
import com.reconova.facecloud.util.JsonUtil;
import com.reconova.facecloud.util.MD5Util;
import com.reconova.facecloud.util.MyApplication;
import com.reconova.facecloud.util.SettingsHelper;
import com.reconova.facecloud.util.ToastUtil;

public class RemoteDBSettingsActivity extends ActionBarActivity {
	private TextView tv_server_setting;
	private ListView listview_facedb;
	private List<FaceDB> facedb_list;
	private SettingsHelper settingsHelper;
	private FaceDB_Result facedb_result;
	private StringBuilder sbFaceDB;
	private StringBuilder sbFaceDBName;
	private MyApplication myApplication;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_remote_db_settings);

		ActionBar actionBar = getSupportActionBar();
		ActionBar.LayoutParams lp = new ActionBar.LayoutParams(
				ActionBar.LayoutParams.MATCH_PARENT,
				ActionBar.LayoutParams.MATCH_PARENT, Gravity.CENTER);
		View viewTitleBar = getLayoutInflater().inflate(
				R.layout.actionbar_customer, null);
		TextView title = (TextView) viewTitleBar.findViewById(R.id.title);
		title.setText("远程数据库");
		actionBar.setCustomView(viewTitleBar, lp);
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setDisplayShowCustomEnabled(true);
		ImageView btn_back = (ImageView) actionBar.getCustomView()
				.findViewById(R.id.btn_back);
		btn_back.setVisibility(View.VISIBLE);
		btn_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});

		tv_server_setting = (TextView) findViewById(R.id.tv_server_setting);
		tv_server_setting.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				startActivity(new Intent(RemoteDBSettingsActivity.this,
						RemoteServerSettingsActivity.class));

			}
		});

		settingsHelper = new SettingsHelper(this);
		listview_facedb = (ListView) findViewById(R.id.listview_facedb);
		myApplication = (MyApplication) this.getApplication();
	}

	/**
	 * 请求人脸数据库
	 */
	private void requestFaceDB() {
		facedb_result = new FaceDB_Result();
		facedb_list = new ArrayList<FaceDB>();
		sbFaceDB = new StringBuilder();
		sbFaceDBName= new StringBuilder();
		myApplication.faceDB.clear();
		AsyncTask<Void, Void, String> getdbTask = new AsyncTask<Void, Void, String>() {

			@Override
			protected String doInBackground(Void... params) {
				try {
					facedb_list.clear();
					String url = CompareAction.wrapFaceDbUrl(settingsHelper
							.getHttpServer());
					Log.i("test", "url===" + url);
					CompareAction.getFaceDB(url, facedb_result);
					for (FaceDB db : facedb_result.getFacedb_list()) {
						//获取所有的数据库ID用于远程比对的时候设置比对的数据库ID
						sbFaceDB.append(db.getDb_id()).append("+");
						//获取所有数据库名称用于注册的时候显示数据库列表的名称
						sbFaceDBName.append(db.getName()).append("+");
						facedb_list.add(db);
						myApplication.faceDB.put(db.getDb_id(), db.getName());
					}
					if (sbFaceDB.length() == 0) {
						return "empty";
					}
					sbFaceDB.deleteCharAt(sbFaceDB.length() - 1);
					Log.i("test", "sbFaceDB=====" + sbFaceDB.toString());
					return "success";
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
					return getString(R.string.base64_encode_failed_tip);
				} catch (JSONException e) {
					e.printStackTrace();
					return getString(R.string.json_decode_failed_tip);
				} catch (SocketTimeoutException e) {
					e.printStackTrace();
					return getString(R.string.scoket_timeout_failed_tip);
				} catch (IOException e) {
					e.printStackTrace();
					return getString(R.string.io_failed_tip);
				}
			}

			@Override
			protected void onPostExecute(String result) {
				if (result.equals("success")) {
					settingsHelper.SetFaceDB(sbFaceDB.toString());
					settingsHelper.SetFaceDBName(sbFaceDBName.toString());
					Adapter adapter = new Adapter();
					listview_facedb.setAdapter(adapter);
				} else if (result.equals("empty")) {
					ToastUtil.showToast(RemoteDBSettingsActivity.this, "数据库为空");
				} else {
					ToastUtil.showToast(RemoteDBSettingsActivity.this, result);
				}
			}

		};
		getdbTask.execute();
	}

	/**
	 * 远程登陆
	 */
	private void login() {
		settingsHelper.removeKey("facedb_key");
		AsyncTask<Void, Void, String> loginTask = new AsyncTask<Void, Void, String>() {

			@Override
			protected String doInBackground(Void... params) {
				Map<String, String> map = new HashMap<String, String>();
				map.put("user_name", settingsHelper.getUsername());
				map.put("user_pwd",
						MD5Util.getMD5(settingsHelper.getPassword()));
				map.put("mode", "force_login");

				try {
					String url = CompareAction.wrapLoginUrl(settingsHelper
							.getHttpServer());
					Log.i("test", "url===" + url);
					String json = HttpUtil.postLogin(url, map);
					Log.i("test", "result====" + json);
					if (json.equals("")) {
						return "JsonNull";
					}
					String ret_mes = JsonUtil.parseLoginResult(json);
					return ret_mes;
				} catch (ConnectException e) {
					e.printStackTrace();
					return getString(R.string.connect_failed_tip);
				} catch (SocketException e) {
					e.printStackTrace();
					return getString(R.string.socket_failed_tip);
				} catch (SocketTimeoutException e) {
					e.printStackTrace();
					return getString(R.string.scoket_timeout_failed_tip);
				} catch (JSONException e) {
					e.printStackTrace();
					return getString(R.string.json_decode_failed_tip);
				} catch (IOException e) {
					e.printStackTrace();
					return getString(R.string.io_failed_tip);
				}

			}

			@Override
			protected void onPostExecute(String result) {
				if (result.equals("成功")) {
					requestFaceDB();
				} else if(result.equals("JsonNull")){
					ToastUtil.showToast(RemoteDBSettingsActivity.this, "连接服务器失败！");
				} else {
					ToastUtil.showToast(RemoteDBSettingsActivity.this, result);
				}
			}
		};

		loginTask.execute();
	}

	/**
	 * 人脸数据库的列表适配器
	 */
	class Adapter extends BaseAdapter {

		@Override
		public int getCount() {
			if (facedb_list == null)
				return 0;
			return facedb_list.size();
		}

		@Override
		public FaceDB getItem(int position) {
			return facedb_list.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = View.inflate(getApplicationContext(),
						R.layout.item_facedb_listview, null);
				new ViewHolder(convertView);
			}
			ViewHolder holder = (ViewHolder) convertView.getTag();
			FaceDB item = new FaceDB();
			item = getItem(position);

			holder.id.setText("ID:" + item.getDb_id());
			holder.name.setText("名称:" + item.getName());
			return convertView;
		}

		class ViewHolder {
			TextView id;
			TextView name;

			public ViewHolder(View view) {
				id = (TextView) view.findViewById(R.id.tv_facedb_id);
				name = (TextView) view.findViewById(R.id.tv_facedb_name);
				view.setTag(this);
			}
		}
	}

	@Override
	protected void onResume() {
		Log.i("test", "onResume--------------------------");
		listview_facedb.setAdapter(null);
		super.onResume();
		login();
	}

}
