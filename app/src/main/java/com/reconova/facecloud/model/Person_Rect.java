package com.reconova.facecloud.model;

import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;

public class Person_Rect implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// "left": 20,
	// "top": 80,
	// "right": 100,
	// "bottom": 160,
	// "blur": 0.78
	private int left;
	private int top;
	private int right;
	private int bottom;
	private double blur;

	public void parseJson(String json) throws JSONException {
		JSONObject obj = new JSONObject(json);
		left = obj.getInt("left");
		top = obj.getInt("top");
		right = obj.getInt("right");
		bottom = obj.getInt("bottom");
		blur = obj.getDouble("blur");
	}

	public int getLeft() {
		return left;
	}

	public void setLeft(int left) {
		this.left = left;
	}

	public int getTop() {
		return top;
	}

	public void setTop(int top) {
		this.top = top;
	}

	public int getRight() {
		return right;
	}

	public void setRight(int right) {
		this.right = right;
	}

	public int getBottom() {
		return bottom;
	}

	public void setBottom(int bottom) {
		this.bottom = bottom;
	}

	public double getBlur() {
		return blur;
	}

	public void setBlur(double blur) {
		this.blur = blur;
	}

}
