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
 * Created by lucmihai on 11/10/2016.
 * This is a get connection unit,
 * args[0]  = url
 * args[1 ...]  = url's parameters, 1 so far!
 *
 */

public class GetAdaptationConnection extends AsyncTask<String, Process, String> {

	private static final String TAG = "GetAdaptationConnection";

	private AsyncTaskCompleteListener<String> callback;


	public GetAdaptationConnection(AsyncTaskCompleteListener<String> cb) {
		this.callback = cb;
	}

	protected void onPreExecute() {}

	protected String doInBackground(String... args) {
		OkHttpClient client = new OkHttpClient();
		String result = "nothing found in the result string";
		String index = "";
		String url = args[0];
		if(args.length > 2){
			index = args[2];
			try{
				JSONObject place = new JSONObject(args[1]);
				url += place.getString("four_id");
			}
			catch (Exception e){
				e.printStackTrace();
			}

		}
		else{
			Log.d(TAG, "args has less then 3 param!");
		}
		Request request = new Request.Builder()
				.url(url)
				.build();
		Response response = null;
		try {
			response = client.newCall(request).execute();
			result = response.body().string();
			result += ","+args[2];
		} catch (Exception e) {
			Log.d(TAG, "Error connection: ");
			e.printStackTrace();
		}
		return result;
	}

	protected void onPostExecute(String adaptation) {
		// TODO: check this number!
		// adaptation format: level,four_id
		//Log.d(TAG, "burla numero 2"+adaptation);
		int number = 12;
		if (callback != null) {
			callback.onGetAdaptationComplete(adaptation,number);

		}
	}
}