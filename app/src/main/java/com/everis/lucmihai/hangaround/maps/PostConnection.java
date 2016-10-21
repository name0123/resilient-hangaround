package com.everis.lucmihai.hangaround.maps;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import com.everis.lucmihai.hangaround.maps.AsyncTaskCompleteListener;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by lucmihai on 11/10/2016.
 *      This connection manages the posting to mobserv API
 */

public  class PostConnection extends AsyncTask<String, Process, String> {
	private static final String TAG = "KarambaPostConnection";

	private AsyncTaskCompleteListener<String> callback;

	public PostConnection(AsyncTaskCompleteListener<String> cb) {
		this.callback = cb;
	}

	protected void onPreExecute() {}

	private class PostOk {

	}
	@JsonPropertyOrder({"uid","lat","lng","ac","wc","el"})
	private class ValorFourId {
		String uid;
		String four_id;
		Boolean ac;
		Boolean wc;
		Elev el;
	}
	private enum Elev{
		HAS,HAS_NOT,NO_NEED
	}
	protected String doInBackground(String... args) {
		final MediaType JSON
				= MediaType.parse("application/json; charset=utf-8");

		OkHttpClient client = new OkHttpClient();

		String url = args[0];
		String json = args[1];
		Log.d(TAG, ' ' + url + ' ' + json.toString());
		RequestBody body = RequestBody.create(JSON, json);
		Request request = new Request.Builder()
				.url(url)
				.post(body)
				.build();
		Log.d(TAG, request.toString());
		try (Response response = client.newCall(request).execute()) {
			response.body().string();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return "Post Connection null response back";
	}


	protected void onPostExecute(JSONArray content) {
		// TODO: check this number!
		int number = 2;
		if (callback != null)
			callback.onVotedPlace(content,number);
	}
}