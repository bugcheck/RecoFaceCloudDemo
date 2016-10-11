package com.reconova.facecloud.model;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Recognition {
	private Person_Rect person_rect;
	private List<Matching> matching_list;
	
	public Recognition() {
		matching_list = new ArrayList<Matching>();
		person_rect = new Person_Rect();
	}
	
	public void parseJson(String json) throws JSONException {
		JSONObject obj = new JSONObject(json);
		JSONObject person_rect_obj =obj.getJSONObject("person_rect");
		person_rect.parseJson(person_rect_obj.toString());
		
		matching_list.clear();
		if (obj.has("matching_list")) {
			JSONArray arr = obj.getJSONArray("matching_list");
			for (int i = 0; i < arr.length(); i++) {
				JSONObject matching = arr.getJSONObject(i);
				Matching match = new Matching();
				match.parseJson(matching.toString());
				matching_list.add(match);
			}
		}
	}

	public Person_Rect getPerson_rect() {
		return person_rect;
	}

	public void setPerson_rect(Person_Rect person_rect) {
		this.person_rect = person_rect;
	}

	public List<Matching> getMatching_list() {
		return matching_list;
	}

	public void setMatching_list(List<Matching> matching_list) {
		this.matching_list = matching_list;
	}

}
