package com.everis.lucmihai.hangaround.dokimos;

import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by John on 7/19/2016.
 */
public class IntegrationPointImpl implements IntegrationPoint {
    public IntegrationPointImpl() {}
    private static final String TAG = "Dokimos";

    public void setxPlaces(JSONArray xPlaces) {
        this.xPlaces = xPlaces;
    }

    public JSONArray getxPlaces() {
        return xPlaces;
    }

    private JSONArray xPlaces = null;
    @Override
    public JSONArray getXPlacesAroundLocation(Location location, XPlaces x, Timeout timeout) {
        long millis = System.currentTimeMillis() % 1000;
        // parameters needed ?ll=lat,lon and limit is = x

        URL url = null;
        try {
            url = new URL("https://movibit.herokuapp.com/4square/search?ll=42,2&limit=5");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        DownloadPlaces dp = new DownloadPlaces();
        AsyncTask asyncTask =null;
        asyncTask = dp.execute(url);
        // waiting for the end... xd
        while (asyncTask == null);
        return xPlaces;

    }
    private class DownloadPlaces extends AsyncTask<URL, Integer, JSONArray>{

        @Override
        protected JSONArray doInBackground(URL... params) {
            Log.d(TAG,"I'm in background");

            try {

                HttpURLConnection conn = (HttpURLConnection) params[0].openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Accept", "application/json");
                // five seconds time out for this connection
                conn.setConnectTimeout(5000);
                conn.setReadTimeout(5000);
                if (conn.getResponseCode() != 200) {
                    Log.d(TAG, (String)"Errores:" + conn.getResponseCode());

                } else {
                    // responseCode = 200!
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
                    JSONArray json = new JSONArray(sb.toString());
                    conn.disconnect();
                    return json;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(JSONArray result) {
            //do the job after the request
            if(result != null) {
                /*Log.d(TAG,"hurray dude-- downloaded succsesfully");
                Log.d(TAG,String.valueOf(result.length()));*/
                setxPlaces(result);
            }
            else Log.d(TAG,"vacio");
        }
    }


}
