package com.everis.lucmihai.hangaround.maps;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by lucmihai on 18/11/2016.
 */

public class ConnectionStatusCheck extends AsyncTask<String, Process, String[]> {

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
	public String[] doInBackground(String... args) {
		String[] result = new String[3];
		result[0] = "OFFLINE"; // internet
		result[1] = "OFFLINE"; // backend
		result[2] = args[0];    // first run or sleep?
 		OkHttpClient client = new OkHttpClient();

		Request requestG = new Request.Builder().url("http://google.com").build();
		Request requestB = new Request.Builder().url("http://mobserv.herokuapp.com/places/getall").build();
		Response response = null;
		Response response1 = null;
		Call call = client.newCall(requestG);
		Call call1 = client.newCall(requestB);
		try {
			response = call.execute();
			int gcode = response.code();
			Log.e(TAG, "Check connection to google code: "+gcode);
			if(gcode >= 200 && gcode < 400) {
				// internet is fine
				response.body().close();
				OkHttpClient client1 = new OkHttpClient();
				result[0] = "ONLINE";
				response1 = call1.execute();
				int bcode = response1.code();
				Log.e(TAG, "Check connection to backend code: "+bcode);
				if(bcode >= 200 && bcode < 400) result[1] = "ONLINE:";
				response1.body().close();
				if("SLEEP".equals(args[0])) {
					Log.e(TAG, "Before you sleep!");
					try {
						call.cancel(); // ya volveremos, si hace falta
						call1.cancel();
						Log.e(TAG, "Went to sleep");
						TimeUnit.SECONDS.sleep(10);
						Log.e(TAG, "Come from sleep");

					} catch (InterruptedException ex) {
						ex.printStackTrace();
						Thread.currentThread().interrupt();
					}
				}
			}
			return result;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}
	@Override
	protected void onPostExecute(String[] status) {
		// TODO: check this number!
		int number = 11;
		if (callback != null) {
			callback.onConnectionStatusCheck(status);

		}
	}
}
