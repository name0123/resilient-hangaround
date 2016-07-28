package com.everis.lucmihai.hangaround.dokimos;

import android.location.Location;

import org.json.JSONArray;

import java.io.InputStream;
import java.net.HttpURLConnection;
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
    public void getXPlacesAroundLocation(Location location, XPlaces x, Timeout timeout) {

    }

    @Override
    public int connectWithTimeout(URL url, InputStream inputStream) {
        HttpURLConnection conn = null;
        int resultCode = 0;
        try {
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");
            resultCode = conn.getResponseCode();
            inputStream = conn.getInputStream();

        } catch (Exception e) {
            e.printStackTrace();
        }

        // five seconds time out for this connection
        //conn.setConnectTimeout(5000);
        //conn.setReadTimeout(5000);
        return resultCode;
    }


}
