package com.reconova.facecloud.model;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Recognition_Result {
	// "ret": 0,
	// "ret_mes": "xxxx",
	// "list_size": 2,
	// " recognition_list": [],
	// "blur": 1

	private int ret;
	private String ret_mes;
	private int list_size;
	private List<Recognition> recognition_list;
	private String blur = null;

	public Recognition_Result() {
		recognition_list = new ArrayList<Recognition>();
	}

	public void parseJson(String json) throws JSONException {
		JSONObject obj = new JSONObject(json);
		// if (obj.has("ret_msg")) {
		// // exception
		// }
		if (!obj.has("blur")) {
			return;
		}
		ret = obj.getInt("ret");
		ret_mes = obj.getString("ret_mes");
		blur = obj.getString("blur");
		recognition_list.clear();
		if (obj.has("recognition_list")) {
			JSONArray arr = obj.getJSONArray("recognition_list");
			for (int i = 0; i < arr.length(); i++) {
				JSONObject recognition_obj = arr.getJSONObject(i);
				Recognition recognition = new Recognition();
				recognition.parseJson(recognition_obj.toString());
				recognition_list.add(recognition);
			}
		}
	}

	public int getRet() {
		return ret;
	}

	public void setRet(int ret) {
		this.ret = ret;
	}

	public String getRet_mes() {
		return ret_mes;
	}

	public void setRet_mes(String ret_mes) {
		this.ret_mes = ret_mes;
	}

	public int getList_size() {
		return list_size;
	}

	public void setList_size(int list_size) {
		this.list_size = list_size;
	}

	public List<Recognition> getRecognition_list() {
		return recognition_list;
	}

	public void setRecognition_list(List<Recognition> recognition_list) {
		this.recognition_list = recognition_list;
	}

	public String getBlur() {
		return blur;
	}

	public void setBlur(String blur) {
		this.blur = blur;
	}

}
