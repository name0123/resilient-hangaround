package com.everis.lucmihai.hangaround.maps;

import android.app.Activity;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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

public class ConnectionStatusCheck extends AsyncTask<Object, Process, String[]> {

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

	public boolean isOnline(Activity a) {
		ConnectivityManager cm =
				(ConnectivityManager) a.getSystemService(a.getApplicationContext().CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		return netInfo != null && netInfo.isConnectedOrConnecting();
	}

	@Override
	public String[] doInBackground(Object... args) {
		String[] result = new String[3];
		result[0] = "OFFLINE"; // internet
		Activity a = (Activity) args[1];
		result[1] = "OFFLINE"; // backend
		result[2] = String.valueOf(args[0]);    // first run or sleep?
		OkHttpClient client = new OkHttpClient();
		Request requestB = new Request.Builder().url("http://mobserv.herokuapp.com/places/getall").build();
		Response response = null;
		Call call = client.newCall(requestB);
		Log.e(TAG, "Check connection to google code: "+isOnline(a));

		if(isOnline(a)) {
			result[0] = "ONLINE";
			try {
				response = call.execute();
				int bcode = response.code();
				Log.e(TAG, "Check connection to backend code: "+bcode);
				if(bcode >= 200 && bcode < 400) {
					result[1] = "ONLINE";
					result[2] = "NO NEED TO SLEEP";
				}
				response.body().close();
				call.cancel(); // ya volveremos, si hace falta
			}catch (IOException e) {
				Log.e(TAG, "error de call");
				e.printStackTrace();
			}
		}
		if("SLEEP".equals(result[2])) { // should move it in finally
			Log.e(TAG, "Before you sleep!");
			try {
				Log.e(TAG, "Went to sleep");
				TimeUnit.SECONDS.sleep(10);
				Log.e(TAG, "Come from sleep");
			} catch (InterruptedException ex) {
				Log.e(TAG, "error de sleep");
				ex.printStackTrace();
				Thread.currentThread().interrupt();
			}

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
