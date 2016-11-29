package com.everis.lucmihai.hangaround.maps;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;

import okhttp3.Call;
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
		JSONArray result = null;
		Log.d(TAG, " stequer:s ");
		String url = (String) args[0];
		Log.d(TAG, " connection: "+url);
		Log.d(TAG, " before call: "+args[1].toString());
		Request request = new Request.Builder()
				.url(url)
				.build();
		Response response = null;
		final Call call = client.newCall(request);
		try {
			response = call.execute();
			String resp = null;
			if(response.code() == 200) {
				resp = response.body().string();
				Log.e(TAG, "This is resp:"+resp);
				if(!resp.isEmpty() || resp != "[]")result = new JSONArray(resp);
				response.body().close();
			}
		} catch (Exception e) {
			Log.d(TAG, "Canceling call: ");
			call.cancel();
			//e.printStackTrace();
		}

		return result;
	}
	@Override
	protected void onPostExecute(JSONArray places) {
		// TODO: check this number!
		if(places != null) Log.d("Connection: ", "places' length :"+places.length());
		int number = 11;
		if (callback != null) {
			callback.onGetPlacesComplete(places,number);

		}
	}
}