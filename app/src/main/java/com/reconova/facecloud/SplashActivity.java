package com.reconova.facecloud;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.ConnectException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.DisconnectedBufferOptions;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONException;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.reconova.facecloud.action.CompareAction;
import com.reconova.facecloud.model.FaceDB;
import com.reconova.facecloud.model.FaceDB_Result;
import com.reconova.facecloud.mqtt.ServiceMqttClient;
import com.reconova.facecloud.util.HttpUtil;
import com.reconova.facecloud.util.JsonUtil;
import com.reconova.facecloud.util.MD5Util;
import com.reconova.facecloud.util.MyApplication;
import com.reconova.facecloud.util.SettingsHelper;
import com.reconova.facecloud.util.ToastUtil;

public class SplashActivity extends Activity {

	protected static final String TAG = "SplashActvity";
	private SettingsHelper settingsHelper;
	private FaceDB_Result facedb_result;
	private StringBuilder sbFaceDB;
	private StringBuilder sbFaceDBName;
	private MyApplication myApplication;
	private MqttAndroidClient mqttAndroidClient;


	final String serverUri = "tcp://192.168.1.198:1883";
	final String subscriptionTopic = "exampleAndroidTopic";
	final String clientId = "ExampleAndroidClient";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		myApplication = (MyApplication) this.getApplication();
		settingsHelper = new SettingsHelper(this);
		login();
		Mqtt_PushInit();
	}

	private void addToHistory(String mainText){
		System.out.println("LOG: " + mainText);

		ToastUtil.showToast(this,mainText);

//		mAdapter.add(mainText);
//		Snackbar.make(findViewById(android.R.id.content), mainText, Snackbar.LENGTH_LONG)
//				.setAction("Action", null).show();

	}

	private void Mqtt_PushInit() {

		startService(new Intent(SplashActivity.this, ServiceMqttClient.class));
		

//		mqttAndroidClient = new MqttAndroidClient(getApplicationContext(), serverUri, clientId);
//		mqttAndroidClient.setCallback(new MqttCallbackExtended() {
//			@Override
//			public void connectComplete(boolean reconnect, String serverURI) {
//
//				if (reconnect) {
//					addToHistory("Reconnected to : " + serverURI);
//					// Because Clean Session is true, we need to re-subscribe
//					subscribeToTopic();
//				} else {
//					addToHistory("Connected to: " + serverURI);
//				}
//			}
//
//			@Override
//			public void connectionLost(Throwable cause) {
//				addToHistory("The Connection was lost.");
//			}
//
//			@Override
//			public void messageArrived(String topic, MqttMessage message) throws Exception {
//				addToHistory("Incoming message: " + new String(message.getPayload()));
//			}
//
//			@Override
//			public void deliveryComplete(IMqttDeliveryToken token) {
//
//			}
//		});
//
//		MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
//		mqttConnectOptions.setAutomaticReconnect(true);
//		mqttConnectOptions.setCleanSession(false);
//
//
//
//
//
//
//
//		try {
//			//addToHistory("Connecting to " + serverUri);
//			mqttAndroidClient.connect(mqttConnectOptions, null, new IMqttActionListener() {
//				@Override
//				public void onSuccess(IMqttToken asyncActionToken) {
//					DisconnectedBufferOptions disconnectedBufferOptions = new DisconnectedBufferOptions();
//					disconnectedBufferOptions.setBufferEnabled(true);
//					disconnectedBufferOptions.setBufferSize(100);
//					disconnectedBufferOptions.setPersistBuffer(false);
//					disconnectedBufferOptions.setDeleteOldestMessages(false);
//					mqttAndroidClient.setBufferOpts(disconnectedBufferOptions);
//					subscribeToTopic();
//				}
//
//				@Override
//				public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
//					addToHistory("Failed to connect to: " + serverUri);
//				}
//			});
//
//
//		} catch (MqttException ex){
//			ex.printStackTrace();
//		}
	}

	public void subscribeToTopic(){
		try {
			mqttAndroidClient.subscribe(subscriptionTopic, 0, null, new IMqttActionListener() {
				@Override
				public void onSuccess(IMqttToken asyncActionToken) {
//					addToHistory("Subscribed!");
				}

				@Override
				public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
//					addToHistory("Failed to subscribe");
				}
			});

			// THIS DOES NOT WORK!
			mqttAndroidClient.subscribe(subscriptionTopic, 0, new IMqttMessageListener() {
				@Override
				public void messageArrived(String topic, MqttMessage message) throws Exception {
					// message Arrived!
					System.out.println("Message: " + topic + " : " + new String(message.getPayload()));
				}
			});

		} catch (MqttException ex){
			System.err.println("Exception whilst subscribing");
			ex.printStackTrace();
		}
	}

	/**
	 * 请求人脸数据库
	 */
	private void requestFaceDB() {
		facedb_result = new FaceDB_Result();
		sbFaceDB = new StringBuilder();
		sbFaceDBName = new StringBuilder();
		myApplication.faceDB.clear();
		AsyncTask<Void, Void, String> getdbTask = new AsyncTask<Void, Void, String>() {

			@Override
			protected String doInBackground(Void... params) {
				try {
					String url = CompareAction.wrapFaceDbUrl(settingsHelper
							.getHttpServer());
					CompareAction.getFaceDB(url, facedb_result);
					for (FaceDB db : facedb_result.getFacedb_list()) {
						//获取所有的数据库ID用于远程比对的时候设置比对的数据库ID
						sbFaceDB.append(db.getDb_id()).append("+");
						//获取所有数据库名称用于注册的时候显示数据库列表的名称
						sbFaceDBName.append(db.getName()).append("+");
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
				} else if (result.equals("empty")) {
					ToastUtil.showToast(SplashActivity.this, "数据库为空");
				} else {
					ToastUtil.showToast(SplashActivity.this, result);
				}
				startActivity(new Intent(SplashActivity.this,
						MainActivity.class));
				finish();
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
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				Map<String, String> map = new HashMap<String, String>();
				map.put("user_name", settingsHelper.getUsername());
				map.put("user_pwd",
						MD5Util.getMD5(settingsHelper.getPassword()));
				map.put("mode", "force_login");

				try {
					String url = CompareAction.wrapLoginUrl(settingsHelper
							.getHttpServer());
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
				} else if (result.equals("JsonNull")) {
					ToastUtil.showToast(SplashActivity.this, "连接服务器失败！");
					startActivity(new Intent(SplashActivity.this,
							MainActivity.class));
					finish();
				} else {
					ToastUtil.showToast(SplashActivity.this, result);
					startActivity(new Intent(SplashActivity.this,
							MainActivity.class));
					finish();
				}
			}
		};

		loginTask.execute();
	}

}
