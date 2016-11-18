package com.everis.lucmihai.hangaround.maps;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by lucmihai on 18/11/2016.
 */

public class ConnectionStatusCheck extends AsyncTask<String, Process, String> {

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
		String result = "init";
		OkHttpClient client = new OkHttpClient();
		Request requestG = new Request.Builder().url("http://google.com").build();
		Request requestB = new Request.Builder().url("http://mobserv.herokuapp.com/places/getall").build();
		Response response = null;
		try {
			response = client.newCall(requestG).execute();
			int gcode = response.code();
			Log.e(TAG, "Check connection to google code: "+gcode);
			if(gcode >= 200 && gcode < 400) {
				// internet is fine
				response = client.newCall(requestB).execute();
				int bcode = response.code();
				Log.e(TAG, "Check connection to backend code: "+bcode);
				if(bcode >= 200 && bcode < 400) result = "ALL OK HERE:";
				else result = "BACKEND_OFFLINE";
			}
			else result = "INTERNET_OFFLINE";
			return result;
		} catch (IOException e) {
			e.printStackTrace();
		}
		finally {
			response.body().close();
		}

		return result;
	}
	@Override
	protected void onPostExecute(String status) {
		// TODO: check this number!
		int number = 11;
		if (callback != null) {
			callback.onConnectionStatusCheck(status);

		}
	}
}
