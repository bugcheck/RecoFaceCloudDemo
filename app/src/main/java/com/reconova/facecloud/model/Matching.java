package com.reconova.facecloud.model;

import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;

public class Matching implements Serializable {
	// "person_id": "00001",
	// "name": "zhang01",
	// "birth": "1987-11-11",
	// "sex": "male",
	// "card_type": 1,
	// "id_card": "652646183235",
	// "native_place": "fujianxiamen",
	// "addr": "fujianxiamen",
	// "phone": "864152234",
	// "similarity": 0,
	// "db_id": "d01",
	// "url": "http://.../facedb/00001/persons/00001",
	// "defaut_face_image_url":
	// "http://.../facedb/00001/persons/00001/faces/0001/image"

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String person_id;
	private String name;
	private String birth;
	private String sex;
	private int card_type;
	private String id_card;
	private String native_place;
	private String addr;
	private String phone;
	private double similarity;
	private String db_id;
	private String url;
	private String default_face_image_url;

	public void parseJson(String json) throws JSONException {
		JSONObject obj = new JSONObject(json);
		person_id = obj.getString("person_id");
		name = obj.getString("name");
		birth = obj.getString("birth");
		sex = obj.getString("sex");
		id_card = obj.getString("id_card");
		card_type = obj.getInt("card_type");
		native_place = obj.getString("native_place");
		addr = obj.getString("addr");
		phone = obj.getString("phone");
		similarity = obj.getDouble("similarity");
		db_id = obj.getString("db_id");
		url = obj.getString("url");
		default_face_image_url = obj.getString("default_face_image_url");
	}

	public String getPerson_id() {
		return person_id;
	}

	public String getId_card() {
		return id_card;
	}

	public void setId_card(String id_card) {
		this.id_card = id_card;
	}

	public void setPerson_id(String person_id) {
		this.person_id = person_id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getBirth() {
		return birth;
	}

	public void setBirth(String birth) {
		this.birth = birth;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public int getCard_type() {
		return card_type;
	}

	public void setCard_type(int card_type) {
		this.card_type = card_type;
	}

	public String getNative_place() {
		return native_place;
	}

	public void setNative_place(String native_place) {
		this.native_place = native_place;
	}

	public String getAddr() {
		return addr;
	}

	public void setAddr(String addr) {
		this.addr = addr;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public double getSimilarity() {
		return similarity;
	}

	public void setSimilarity(double similarity) {
		this.similarity = similarity;
	}

	public String getDb_id() {
		return db_id;
	}

	public void setDb_id(String db_id) {
		this.db_id = db_id;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getDefault_face_image_url() {
		return default_face_image_url;
	}

	public void setDefault_face_image_url(String default_face_image_url) {
		this.default_face_image_url = default_face_image_url;
	}

}
