package com.everis.lucmihai.hangaround.maps;

import android.os.AsyncTask;
import android.util.Log;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by lucmihai on 11/10/2016.
 *      This connection manages the posting to mobserv API
 */

public  class PostConnection extends AsyncTask<String, Process, JSONObject> {
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
	protected JSONObject doInBackground(String... args) {
		final MediaType JSON
				= MediaType.parse("application/json; charset=utf-8");


		OkHttpClient client = new OkHttpClient.Builder()
				.readTimeout(1500, TimeUnit.MILLISECONDS)
				.build();
		JSONObject result = null;
		String url = args[0];
		String json = args[1];
		Log.d(TAG, ' ' + url + ' ' + json.toString());
		RequestBody body = RequestBody.create(JSON, json);
		Response response = null;
		Request request = new Request.Builder()
				.url(url)
				.post(body)
				.build();
		Log.d(TAG, request.toString());
		try  {
			response = client.newCall(request).execute();
			String res = response.body().string();
			if(response.code() == 200){
				result = new JSONObject(res);
				Log.e(TAG, "This is result: "+result);
			}
			else{
				Log.e(TAG, "This no connection case, result is set null ");
				result = null;
			}
		} catch (Exception e) {
			Log.d(TAG, "response tag: "+response.code());
			e.printStackTrace();

			if (callback != null)
				callback.onVotedPlace(result);
		}
		return result;
	}

	@Override
	protected void onPostExecute(JSONObject content) {
		// TODO: check this number!
		int number = 2;
		if (callback != null)
			callback.onVotedPlace(content);
	}
}