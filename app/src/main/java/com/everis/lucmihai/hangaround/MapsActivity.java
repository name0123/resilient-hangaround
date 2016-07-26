package com.everis.lucmihai.hangaround;

import android.content.Context;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.everis.lucmihai.hangaround.dokimos.IntegrationPoint;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import butterknife.OnClick;
import stanford.androidlib.SimpleActivity;

public class MapsActivity extends SimpleActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private GoogleMap mMap;
    SupportMapFragment mapFragment;
    Geocoder geocoder;
    Context context;
    private static final String TAG = "MapActivity";
    private final Float CTOTAL = BitmapDescriptorFactory.HUE_GREEN;
    private final Float CPARTIAL = BitmapDescriptorFactory.HUE_YELLOW;
    private final Float CUNADAPTED = BitmapDescriptorFactory.HUE_AZURE;
    private final Float CUNKNOWN = BitmapDescriptorFactory.HUE_CYAN;

    private final String SUNKNOWN = "UNKNOWN";
    private final String SUNADAPTED = "UNADAPTED";
    private final String SPARTIAL = "PARTIAL";
    private final String STOTAL = "TOTAL";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        context = getApplicationContext();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment= (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.search_bar);
        toolbar.setTitle("");
        toolbar.setLogo(R.drawable.ic_action_action_search);
        setSupportActionBar(toolbar);

    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    private void goThere(double latitude, double longitude, String markerMessage){
        final LatLng myLoc = new LatLng(latitude,longitude);
        mMap.addMarker(new MarkerOptions().position(myLoc).title(markerMessage));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLoc,15f));
            /*mMap.setMyLocationEnabled(true);
            mMap.animateCamera( CameraUpdateFactory.zoomTo( 57.0f ) );

*/
    }
    public void getPlaces(Location location, IntegrationPoint.XPlaces xplaces, IntegrationPoint.Timeout timeout){
        URL url1 = null;
        try {
            //url1 = new URL("https://movibit.herokuapp.com/4square/search?ll=41.3830878006894,2.04654693603516&limit=50");
            //url1 = new URL("https://movibit.herokuapp.com/places/get?ll=41.3830878006894,2.04654693603516");
            url1 = new URL("https://movibit.herokuapp.com/places/getall");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        new getPlacesBackgound().execute(url1);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {

        return false;
    }

    private class getPlacesBackgound extends AsyncTask<URL, Integer, JSONArray> {
        protected JSONArray doInBackground(URL... urls) {
            try {
                HttpURLConnection conn = (HttpURLConnection) urls[0].openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Accept", "application/json");
                // five seconds time out for this connection
                //conn.setConnectTimeout(5000);
                //conn.setReadTimeout(5000);
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
                            Log.d(TAG,line);
                            sb.append(line);
                        }
                        rd.close();

                    }catch (Exception e) {
                        return null;
                    }
                    JSONArray json = new JSONArray(sb.toString());
                    publishProgress();
                    return json;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }


        protected void onPostExecute(JSONArray result) {
            if(result != null) {
               showPlaces(result);
            }
            else{
                // no places found
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        /*// Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        */
        context = getApplicationContext();
        mMap.getUiSettings().setMyLocationButtonEnabled(true); // no work!
        final LocationManager locationManager = (LocationManager)
                getSystemService(Context.LOCATION_SERVICE);
        mMap.setOnMarkerClickListener(this);


        Criteria criteria = new Criteria();
        try {
            final Location location = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, false));
            if(location != null){
                final Double latitude = location.getLatitude();
                final Double longitude = location.getLongitude();

                String stringll = String.valueOf(latitude);
                stringll += "  ";
                stringll += String.valueOf(longitude);
                toast(stringll);
                goThere(latitude, longitude, "you are here");
                IntegrationPoint.XPlaces xPlaces = new IntegrationPoint.XPlaces(10);
                IntegrationPoint.Timeout timeout = new IntegrationPoint.Timeout(100);
                getPlaces(location, xPlaces, timeout);
            }
            else{
                toast("No current location, turn on GPS");
                //showAlertDialog(@StringRes)
            }
        }
        catch (SecurityException e){
            e.printStackTrace();
        }
    }
    @OnClick(R.id.bsearch)
    public void onClickSearch(View view){
        // get the txtsearch
        // locate it on the map - and move camera
        // display places arround
        EditText searchedText = (EditText) findViewById(R.id.txtsearch);
        String searchedLocation = searchedText.getText().toString();
        try {
           //Address address = (Address) geocoder.getFromLocationName(searchedLocation,1); cool
            geocoder = new Geocoder(this);
            List<android.location.Address> addresses = geocoder.getFromLocationName(searchedLocation,1);
            if(addresses.size() > 0) {
                double latitude= addresses.get(0).getLatitude();
                double longitude= addresses.get(0).getLongitude();
                goThere(latitude,longitude, searchedLocation);
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }

    }
    public void showPlaces(JSONArray places){
        // returned the
        toast(places.length());
        try {
            JSONObject place = (JSONObject) places.get(0);
            //Log.d(TAG,place.toString());
            double lat = ((JSONObject) places.get(0)).getDouble("latitude");
            double lng = ((JSONObject) places.get(0)).getDouble("longitude");
            String mark = ((JSONObject) places.get(0)).getString("name");
            // place the camera on [0] - might need zoom
            goThere(lat,lng,mark);
            showMarker(place);
            // display
            for(int i = 1; i < places.length(); ++i){
                place = (JSONObject)places.get(i);
                showMarker(place);
                if(place.getString("category").equals("Food") ||
                        place.getString("category").equals("Bar") ||
                        place.getString("category").equals("Coffee Shop") ||
                        place.getString("category").equals("Restaurant")){
                }

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void showMarker(JSONObject place) {
        double lat = 0.0;
        double lng = 0.0;
        String adaptationLevel ="";
        String placeName="";
        try {
            lat = place.getDouble("latitude");
            lng = place.getDouble("longitude");
            adaptationLevel = place.getString("adaptedLevel");
            placeName = place.getString("name");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        /*enum AdaptedLevel {
            UNKNOWN, UNADAPTED, PARTIAL, TOTAL
        }*/

        LatLng placeLocation = new LatLng(lat, lng);

        if(adaptationLevel.equals(SUNKNOWN)){
            Marker placeMarker = mMap.addMarker(new MarkerOptions()
                    .position(placeLocation)
                    .icon(BitmapDescriptorFactory.defaultMarker(CUNKNOWN))
                    .title(placeName)
            );
        }

        else if(adaptationLevel.equals(STOTAL)) {
            Marker placeMarker = mMap.addMarker(new MarkerOptions()
                    .position(placeLocation)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                    .title(placeName)
            );
        }
        else if(adaptationLevel.equals(SPARTIAL)) {
            Marker placeMarker = mMap.addMarker(new MarkerOptions()
                    .position(placeLocation)
                    .icon(BitmapDescriptorFactory.defaultMarker(CPARTIAL))
                    .title(placeName)
            );
        }
        else if(adaptationLevel.equals(SUNADAPTED)) {
            Marker placeMarker = mMap.addMarker(new MarkerOptions()
                    .position(placeLocation)
                    .icon(BitmapDescriptorFactory.defaultMarker(CUNADAPTED))
                    .title(placeName)
            );
        }
    }
}
