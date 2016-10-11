package com.reconova.facecloud.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SettingsHelper {
	public static String TAG = "SettingsHelper";
	private Context mContext;

	public SettingsHelper(Context context) {
		mContext = context;
	}

	public String getHttpIp() {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(mContext);
		return sp.getString("http_ip_key", "10.10.25.199");
	}

	public void SetHttpIp(String ip) {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(mContext);
		SharedPreferences.Editor editor = sp.edit();
		editor.putString("http_ip_key", ip);
		editor.commit();
	}

	public String getHttpHost() {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(mContext);
		return sp.getString("http_host_key", "8000");
	}

	public void SetHttpHost(String host) {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(mContext);
		SharedPreferences.Editor editor = sp.edit();
		editor.putString("http_host_key", host);
		editor.commit();
	}

	public String getServer() {
		return getHttpIp() + ":" + getHttpHost();
	}

	public String getHttpServer() {
		return "http://" + getHttpIp() + ":" + getHttpHost() + "/";
	}

	public String getUsername() {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(mContext);
		return sp.getString("username_key", "admin");
	}

	public void SetUsername(String username) {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(mContext);
		SharedPreferences.Editor editor = sp.edit();
		editor.putString("username_key", username);
		editor.commit();
	}

	public String getPassword() {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(mContext);
		return sp.getString("password_key", "123456");
	}

	public void SetPassword(String password) {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(mContext);
		SharedPreferences.Editor editor = sp.edit();
		editor.putString("password_key", password);
		editor.commit();
	}

	public void SetCompareNum(int compareNum) {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(mContext);
		SharedPreferences.Editor editor = sp.edit();
		editor.putInt("comparenum_key", compareNum);
		editor.commit();
	}

	public int getCompareNum() {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(mContext);
		return sp.getInt("comparenum_key", 3);
	}

	public void SetFaceDB(String facedb) {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(mContext);
		SharedPreferences.Editor editor = sp.edit();
		editor.putString("facedb_key", facedb);
		editor.commit();
	}

	public String getFaceDB() {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(mContext);
		return sp.getString("facedb_key", "");
	}
	
	public void SetFaceDBName(String facedb) {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(mContext);
		SharedPreferences.Editor editor = sp.edit();
		editor.putString("facedb_name_key", facedb);
		editor.commit();
	}

	public String getFaceDBName() {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(mContext);
		return sp.getString("facedb_name_key", "");
	}
	
	public void removeKey(String key) {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(mContext);
		SharedPreferences.Editor editor = sp.edit();
		editor.remove(key);
		editor.commit();
	}

}
