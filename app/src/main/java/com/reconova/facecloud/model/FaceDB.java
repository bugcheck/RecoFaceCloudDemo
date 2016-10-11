package com.reconova.facecloud.model;

import org.json.JSONException;
import org.json.JSONObject;

public class FaceDB {

	private String db_id;
	private String name;
	private String url;

	public void parseJson(String json) throws JSONException {
		JSONObject obj = new JSONObject(json);
		db_id = obj.getString("db_id");
		name = obj.getString("name");
		url = obj.getString("url");
	}

	public String getDb_id() {
		return db_id;
	}

	public void setDb_id(String db_id) {
		this.db_id = db_id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

}
