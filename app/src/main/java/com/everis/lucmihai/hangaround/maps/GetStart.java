package com.everis.lucmihai.hangaround.maps;

import android.os.AsyncTask;
import android.util.Log;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by lucmihai on 8/11/2016.
 * This is a get connection launched each time the application is started,
 * in order for the host of API mobserv to wake up - working from birth!
 * args[1 ...]  = url's parameters,!
 *
 */

public class GetStart extends AsyncTask<String, Process, String> {

	private static final String TAG = "GetStart";

	private AsyncTaskCompleteListener<String> callback;


	public GetStart(AsyncTaskCompleteListener<String> cb) {
		this.callback = cb;
	}

	protected void onPreExecute() {}

	protected String doInBackground(String... args) {
		OkHttpClient client = new OkHttpClient();
		String result = "nothing found in the result string";
		String index = "";
		String url = args[0];
		Request request = new Request.Builder()
				.url(url)
				.build();
		Response response = null;
		try {
			response = client.newCall(request).execute();
		} catch (Exception e) {
			Log.d(TAG, "Error connection: ");
			e.printStackTrace();
		}
		return result;
	}
	// nothing to do here
	protected void onPostExecute(String adaptation) {}
}