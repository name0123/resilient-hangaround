package com.everis.lucmihai.hangaround.maps;

import android.app.Activity;
import android.content.Context;
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

public class Connection extends AsyncTask<Object, Process, JSONArray> {
	private JSONArray places;

	private static final String TAG = "KarambaConnection";

	private AsyncTaskCompleteListener<String> callback;


	public Connection(AsyncTaskCompleteListener<String> cb) {
		this.callback = cb;
	}

	/**
	 * Override this method to perform a computation on a background thread. The
	 * specified parameters are the parameters passed to {@link #execute}
	 * by the caller of this task.
	 * <p>
	 * This method can call {@link #publishProgress} to publish updates
	 * on the UI thread.
	 *

	 * @return A result, defined by the subclass of this task.
	 * @see #onPreExecute()
	 * @see #onPostExecute
	 * @see #publishProgress
	 */


	@Override
	protected void onPreExecute() {}

	@Override
	public JSONArray doInBackground(Object... args) {
		OkHttpClient client = new OkHttpClient();
		JSONArray result = new JSONArray();
		String url = (String) args[0];
		Log.d(TAG, " before error: "+url);
		Log.d(TAG, " before: "+args[1].toString());
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
	@Override
	protected void onPostExecute(JSONArray places) {
		// TODO: check this number!
		Log.d("Other new tag: ", "places should have no elements:"+places.length());
		int number = 11;
		if (callback != null) {
			if(places != null) callback.onGetPlacesComplete(places,number);

		}
	}
}