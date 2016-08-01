package com.everis.lucmihai.hangaround.dokimos;

import android.location.Location;

import org.json.JSONArray;

import java.io.InputStream;
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


        return 0;
    }


}
