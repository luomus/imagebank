package fi.laji.imagebank.models;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

import fi.luomus.commons.json.JSONObject;

public class Preferences implements Serializable {

	private static final long serialVersionUID = 8894315618898407650L;

	private final Map<String, String> preferences = new LinkedHashMap<>();

	public String getJson() {
		JSONObject json = new JSONObject();
		preferences.entrySet().forEach(e->json.setString(e.getKey(), e.getValue()));
		return json.toString();
	}

	public Preferences put(String preference, String value) {
		preferences.put(preference, value);
		return this;
	}

	public boolean isEmpty() {
		return preferences.isEmpty();
	}

}
