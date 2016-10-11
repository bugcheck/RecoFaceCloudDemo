package com.reconova.facecloud.util;

import java.util.HashMap;

import android.app.Application;

public class MyApplication extends Application {

	private static MyApplication myApplication;
	public HashMap<String, Object> dataHolder = new HashMap<String, Object>();
	public HashMap<String, Object> faceDB = new HashMap<String, Object>();

	@Override
	public void onCreate() {
		super.onCreate();
		myApplication = this;
		//CrashHandler crashHandler = CrashHandler.getInstance();
		//crashHandler.init(getApplicationContext());
	}

	public static MyApplication getInstance() {
		return myApplication;
	}

}
