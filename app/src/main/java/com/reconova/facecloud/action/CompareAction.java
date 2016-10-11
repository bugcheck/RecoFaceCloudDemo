package com.reconova.facecloud.action;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.Map;

import org.json.JSONException;

import android.util.Log;

import com.reconova.facecloud.model.FaceDB_Result;
import com.reconova.facecloud.model.Recognition_Result;
import com.reconova.facecloud.util.HttpUtil;

public class CompareAction {
	public static final String LOGIN_REQUEST_URL = "base/auth/login";
	public static final String GET_FACEDB_REQUEST_URL = "facedb";
	public static final String RECOGNIZE_REQUEST_URL = "faceops/image_recognition";
	public static final String ADD_PERSON_REQUEST_URL = "facedb/";

	public static String wrapLoginUrl(String server) {
		return server + LOGIN_REQUEST_URL;
	}

	public static String wrapFaceDbUrl(String server) {
		return server + GET_FACEDB_REQUEST_URL;
	}

	public static String wrapRecognizeUrl(String server) {
		return server + RECOGNIZE_REQUEST_URL;
	}

	public static String wrapAddPersonUrl(String server,String facedb_id) {
		return server + ADD_PERSON_REQUEST_URL+facedb_id+"/persons";
	}

	public static void getRecognition_Result(String url,
			Recognition_Result recognition_result, byte[] image,
			Map<String, String> params, String method)
			throws SocketTimeoutException, IOException, JSONException {
		String json = HttpUtil.recognize(url, image, params, method);
		Log.i("test", "json=========================" + json);
		recognition_result.parseJson(json);
	}

	public static void getFaceDB(String url, FaceDB_Result facedb_result)
			throws SocketTimeoutException, IOException, JSONException {
		String json = HttpUtil.getMethod(url);
		Log.i("test", "json=========================" + json);
		facedb_result.parseJson(json);
	}

}
