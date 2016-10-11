package com.reconova.facecloud.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Base64;
import android.util.Log;

public class HttpUtil {
	public final static String CONTENT_TYPE = "application/json; charset=utf-8";
	public final static String ACCEPT = "application/json; version=1.0";
	public final static int CONNECT_TIMEOUT = 5;
	public final static int READ_TIMEOUT = 5;
	public static String sessionid = null;

	public static String postLogin(String requrl, Map<String, String> map)
			throws SocketTimeoutException, SocketException,ConnectException, IOException,
			JSONException {
		URL url = new URL(requrl);
		HttpURLConnection connection = getHttpURLConnection(url, "POST");
		//connection.setDoOutput(true);
		writeJsonRequestBody(map, connection);
		// 取得sessionid.
		sessionid = connection.getHeaderField("set-cookie");
		Log.i("test", "sessionid======" + sessionid);
		int code = connection.getResponseCode();
		if (code == 200) {
			return changeInputStream(connection.getInputStream());
		}
		return "";
	}

	public static String postMethod(String requrl, Map<String, String> map)
			throws SocketTimeoutException, SocketException,ConnectException, IOException,
			JSONException {
		URL url = new URL(requrl);
		HttpURLConnection connection = getHttpURLConnection(url, "POST");
		if (sessionid != null) {
			connection.setRequestProperty("cookie", sessionid);
		}
		writeJsonRequestBody(map, connection);
		int code = connection.getResponseCode();
		if (code == 200) {
			return changeInputStream(connection.getInputStream());
		}
		return "";
	}

	public static String getMethod(String requrl)
			throws SocketTimeoutException,ConnectException, IOException, JSONException {
		URL url = new URL(requrl);
		HttpURLConnection connection = getHttpURLConnection(url, "GET");
		if (sessionid != null) {
			connection.setRequestProperty("cookie", sessionid);
		}
		int code = connection.getResponseCode();
		if (code == 200) {
			return changeInputStream(connection.getInputStream());
		}
		return "";
	}

	public static String putMethod(String requrl, Map<String, String> params)
			throws SocketTimeoutException, ConnectException,IOException, JSONException {
		// StringBuilder是用来组拼请求地址和参数
		StringBuilder sb = new StringBuilder();
		sb.append(requrl).append("?");
		if (params != null && params.size() != 0) {
			for (Map.Entry<String, String> entry : params.entrySet()) {
				// 如果请求参数中有中文，需要进行URLEncoder编码
				sb.append(entry.getKey()).append("=")
						.append(URLEncoder.encode(entry.getValue(), "utf-8"));
				sb.append("&");
			}
			sb.deleteCharAt(sb.length() - 1);
		}
		URL url = new URL(sb.toString());
		HttpURLConnection connection = getHttpURLConnection(url, "PUT");
		if (sessionid != null) {
			connection.setRequestProperty("cookie", sessionid);
		}
		int code = connection.getResponseCode();
		if (code == 200) {
			return changeInputStream(connection.getInputStream());
		}
		return "";
	}

	public static String recognize(String requrl, byte[] image,
								   Map<String, String> params, String method)
			throws SocketTimeoutException, ConnectException,IOException, JSONException {
		// StringBuilder是用来组拼请求地址和参数
		StringBuilder sb = new StringBuilder();
		sb.append(requrl).append("?");
		// Log.i("test", "params===="+params.toString());
		if (params != null && params.size() != 0) {
			for (Map.Entry<String, String> entry : params.entrySet()) {
				// 如果请求参数中有中文，需要进行URLEncoder编码
				sb.append(entry.getKey()).append("=").append(entry.getValue());
				// .append(URLEncoder.encode(entry.getValue(), "utf-8"));
				sb.append("&");
			}
			sb.deleteCharAt(sb.length() - 1);
		}
		URL url = new URL(sb.toString());
		HttpURLConnection connection = getHttpURLConnection(url, method);
		if (sessionid != null) {
			connection.setRequestProperty("cookie", sessionid);
		}
		String imageFrame = Base64.encodeToString(image, Base64.NO_WRAP);
		Log.i("test", "iamgeData=========" + imageFrame);
		JSONObject ClientKey = new JSONObject();
		JSONObject image_data = new JSONObject();
		image_data.put("type", "jpg");
		image_data.put("content", imageFrame);
		ClientKey.put("image_data", image_data);
		String json = String.valueOf(ClientKey);

		OutputStream os = connection.getOutputStream();
		os.write(json.getBytes());
		os.flush();
		os.close();

		int code = connection.getResponseCode();
		if (code == 200) {
			return changeInputStream(connection.getInputStream());
		}
		return "";
	}

	private static HttpURLConnection getHttpURLConnection(URL url, String method)
			throws IOException {
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setConnectTimeout(CONNECT_TIMEOUT * 1000);
		connection.setReadTimeout(READ_TIMEOUT * 1000);
		connection.setRequestMethod(method);
		connection.setRequestProperty("Content-Type", CONTENT_TYPE);
		connection.setRequestProperty("Accept", ACCEPT);
		connection.setDoInput(true);
		// connection.setDoOutput(true);
		return connection;
	}

	private static void writeJsonRequestBody(Map<String, String> map,
											 HttpURLConnection connection) throws JSONException,
			SocketException,ConnectException, IOException {
		if (map != null) {
			JSONObject ClientKey = new JSONObject();
			for (String key : map.keySet()) {
				ClientKey.put(key, map.get(key));
			}
			String json = String.valueOf(ClientKey);
			OutputStream os = connection.getOutputStream();
			os.write(json.getBytes());
			os.flush();
			os.close();
		}
	}

	private static String changeInputStream(InputStream inputStream)
			throws IOException {
		String jsonString = "";
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		int len = 0;
		byte[] data = new byte[1024];
		while ((len = inputStream.read(data)) != -1) {
			outputStream.write(data, 0, len);
		}
		jsonString = new String(outputStream.toByteArray());
		System.out.println("test  ==========="+jsonString);
		return jsonString;
	}

}
