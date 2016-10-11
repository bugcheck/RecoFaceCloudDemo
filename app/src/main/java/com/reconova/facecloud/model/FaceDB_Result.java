package com.reconova.facecloud.model;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class FaceDB_Result {

	private int ret;
	private String ret_mes;
	private int total_count;
	private int list_size;
	private List<FaceDB> facedb_list;

	public FaceDB_Result() {
		facedb_list = new ArrayList<FaceDB>();
	}

	public void parseJson(String json) throws JSONException {
		JSONObject obj = new JSONObject(json);
		ret = obj.getInt("ret");
		ret_mes = obj.getString("ret_mes");
		total_count = obj.getInt("total_count");
		list_size = obj.getInt("list_size");
		facedb_list.clear();
		if (obj.has("facedb_list")) {
			JSONArray arr = obj.getJSONArray("facedb_list");
			for (int i = 0; i < arr.length(); i++) {
				JSONObject face_db_obj = arr.getJSONObject(i);
				FaceDB faceDB = new FaceDB();
				faceDB.parseJson(face_db_obj.toString());
				facedb_list.add(faceDB);
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

	public int getTotal_count() {
		return total_count;
	}

	public void setTotal_count(int total_count) {
		this.total_count = total_count;
	}

	public int getList_size() {
		return list_size;
	}

	public void setList_size(int list_size) {
		this.list_size = list_size;
	}

	public List<FaceDB> getFacedb_list() {
		return facedb_list;
	}

	public void setFacedb_list(List<FaceDB> facedb_list) {
		this.facedb_list = facedb_list;
	}

}
