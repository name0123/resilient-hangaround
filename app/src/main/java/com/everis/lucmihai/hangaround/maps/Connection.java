package com.everis.lucmihai.hangaround.maps;

import android.os.AsyncTask;
import android.util.Log;

import com.everis.lucmihai.hangaround.MapsActivity;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by lucmihai on 11/10/2016.
 * This is a get connection unit,
 * args[0]  = url
 * args[1 ...]  = url's parameters, 1 so far!
 *
 */

public class Connection extends AsyncTask<String, Process, JSONArray> {
	private JSONArray places;
	private static final String TAG = "KarambaConnection";

	private AsyncTaskCompleteListener<String> callback;


	public Connection(AsyncTaskCompleteListener<String> cb) {
		this.callback = cb;
	}

	protected void onPreExecute() {}

	protected JSONArray doInBackground(String... args) {
		OkHttpClient client = new OkHttpClient();
		JSONArray result = new JSONArray();
		String url = args[0];
		if(args.length > 1){
			url += args[1];
		}
		Log.d(TAG, " before error: "+url);
		Request request = new Request.Builder()
				.url(url)
				.build();
		Response response = null;
		try {
			response = client.newCall(request).execute();
			result = new JSONArray(response.body().string());
		} catch (Exception e) {
			Log.d(TAG, "Error connection: ");
			e.printStackTrace();
		}
		return result;
	}

	protected void onPostExecute(JSONArray places) {
		// TODO: check this number!
//		Log.d(TAG, "burla numero 2"+places.length());
		int number = 11;
		if (callback != null) {
			if(places != null) callback.onGetPlacesComplete(places,number);

		}
	}
}