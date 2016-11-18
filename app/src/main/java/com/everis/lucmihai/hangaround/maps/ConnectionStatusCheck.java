package com.everis.lucmihai.hangaround.maps;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;

import okhttp3.OkHttpClient;

/**
 * Created by lucmihai on 18/11/2016.
 */

public class ConnectionStatusCheck extends AsyncTask<String, Process, String> {
	private JSONArray places;

	private static final String TAG = "CheckStatusCheck";

	private AsyncTaskCompleteListener<String> callback;


	public ConnectionStatusCheck(AsyncTaskCompleteListener<String> cb) {
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
	public String doInBackground(String... args) {
		OkHttpClient client = new OkHttpClient();

		return"status okj";
	}
	@Override
	protected void onPostExecute(String status) {
		// TODO: check this number!
		if(places != null) Log.d("OnPostExecute: ", "places' length :"+places.length());
		int number = 11;
		if (callback != null) {
			callback.onConnectionStatusCheck("HOLA");

		}
	}
}
