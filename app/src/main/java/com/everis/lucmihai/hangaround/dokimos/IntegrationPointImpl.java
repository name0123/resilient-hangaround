package com.everis.lucmihai.hangaround.dokimos;

import android.content.Context;
import android.location.Location;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import stanford.androidlib.SimpleActivity;

/**
 * Created by John on 7/19/2016.
 */
public class IntegrationPointImpl extends SimpleActivity implements IntegrationPoint {
    public IntegrationPointImpl() { }

    @Override
    public void getXPlacesAroundLocation(Context context, Location location, XPlaces x, Timeout timeout) {
        long millis = System.currentTimeMillis() % 1000;
        // parameters needed ?ll=lat,lon and limit is = x
        toast("hola");

        try {
            URL url = new URL("https://movibit.herokuapp.com/4square/search?ll=42,2&limit=10");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");

            if(conn.getResponseCode() != 200){
                toast(conn.getResponseCode());

            }else{
                toast("Ok dude");
                BufferedReader br = new BufferedReader(new InputStreamReader(
                        (conn.getInputStream())));

                String output;

                while ((output = br.readLine()) != null) {
                    System.out.println("mishoo" + output);
                }
                conn.disconnect();

            }


        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
