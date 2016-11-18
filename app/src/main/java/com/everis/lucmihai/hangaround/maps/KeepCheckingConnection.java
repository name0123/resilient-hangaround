package com.everis.lucmihai.hangaround.maps;

import android.os.AsyncTask;
import android.util.Log;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by lucmihai on 18/11/2016.
 */

public class KeepCheckingConnection extends AsyncTask<String, Process, String> {
	private static final String TAG = "CheckStatusCheck";

	private AsyncTaskCompleteListener<String> callback;


	public KeepCheckingConnection(AsyncTaskCompleteListener<String> cb) {
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
		String result = "ok";
		// cada 5 segons tornem a comprovar la connexió
		OkHttpClient client = new OkHttpClient();
		Request requestG = new Request.Builder().url("http://google.com").build();
		Request requestB = new Request.Builder().url("http://mobserv.herokuapp.com/places/getall").build();
		int gcode = 0;
		int bcode = 0;
		while((gcode < 200 && gcode > 400) || (bcode < 200 && bcode > 400)) {
			// aqui es queda el thread fins tenir internet o que la dependència torna
			Response response = null;
			try {
				response = client.newCall(requestG).execute();
				gcode = response.code();
				Log.e(TAG, "Check connection to google code: " + gcode);
				response = client.newCall(requestB).execute();
				bcode = response.code();
				Log.e(TAG, "Check connection to backend code: " + bcode);
				try {
					Log.e(TAG, "Went to sleep");
					TimeUnit.SECONDS.sleep(5);
					Log.e(TAG, "Come from sleep");
				}
				catch (InterruptedException ex){
					Thread.currentThread().interrupt();
				}
			} catch (Exception e) {

				e.printStackTrace();
			}
			finally {
				response.body().close();
			}
		}
		return result;
	}
	@Override
	protected void onPostExecute(String status) {
		// TODO: check this number!
		int number = 11;
		if (callback != null) {
			callback.onKeepChecking(status);

		}
	}
}

