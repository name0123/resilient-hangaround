package com.everis.lucmihai.hangaround.maps;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by lucmihai on 11/10/2016.
 */

public class Connection extends AsyncTask<String, Process, String> {
	private static final String TAG = "KarambaConnection";

	private AsyncTaskCompleteListener<String> callback;

	public Connection(AsyncTaskCompleteListener<String> cb) {
		this.callback = cb;
	}

	protected void onPreExecute() {}

	protected String doInBackground(String... args) {
		String content = "hola";
		// args[0] - this is
		// args[1] - this is
		try {
			URL url = new URL(args[0]);
			try {
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				conn.setRequestMethod("GET");
				conn.setRequestProperty("Accept", "application/json");
				int respCode = conn.getResponseCode();

				if (respCode != 200) {
					Log.d(TAG, (String)"Errores:" + respCode);

				} else {
					// responseCode = 200! - this should be only a line of  answer
					String line;
					StringBuilder sb = new StringBuilder();
					BufferedReader rd = new BufferedReader(new InputStreamReader(
							(conn.getInputStream())));
					try {
						while ((line = rd.readLine()) != null) {
							sb.append(line);
						}
						rd.close();


					}catch (Exception e) {
						return null;
					}
					return sb.toString();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return content;
	}

	protected void onPostExecute(String content) {
		// TODO: check this number!
		int number = 2;
		if (callback != null)
			callback.onTaskComplete(content,number);
	}
}