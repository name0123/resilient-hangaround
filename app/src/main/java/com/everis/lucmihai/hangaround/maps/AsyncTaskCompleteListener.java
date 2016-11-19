package com.everis.lucmihai.hangaround.maps;

/**
 * Created by lucmihai on 11/10/2016.
 * This is frome here: http://stackoverflow.com/questions/28854875/java-asynctask-passing-variable-to-main-thread/28855151#28855151
 *
 *
 */

public interface AsyncTaskCompleteListener<JSONArray> {
	void onGetPlacesComplete(org.json.JSONArray result, int number);
	void onGetAdaptationComplete(String result, int number);
	void onVotedPlace(org.json.JSONObject result);
	void onConnectionStatusCheck(String[] s);
	//void onKeepChecking(String s); inutil, repetida!
}
