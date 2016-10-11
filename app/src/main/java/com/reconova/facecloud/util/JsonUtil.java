package com.reconova.facecloud.util;

import org.json.JSONException;
import org.json.JSONObject;

public class JsonUtil {

	public static String getBase64Data(String json) throws JSONException {
		JSONObject obj = new JSONObject(json);
		if (obj.has("image_data")) {
			JSONObject image = obj.getJSONObject("image_data");
			return image.getString("content");
		}
		return null;
	}

	public static String parseLoginResult(String json) throws JSONException {
		JSONObject obj = new JSONObject(json);
		String ret_mes = obj.getString("ret_mes");
		return ret_mes;
	}

}
