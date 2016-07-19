package com.everis.lucmihai.hangaround;

import android.content.Context;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;

import com.everis.lucmihai.hangaround.dokimos.IntegrationPoint;
import com.everis.lucmihai.hangaround.dokimos.IntegrationPointImpl;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

import butterknife.OnClick;
import stanford.androidlib.SimpleActivity;

public class MapsActivity extends SimpleActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    SupportMapFragment mapFragment;
    Geocoder geocoder;
    Context context;
    private static final String TAG = "MapActivity";

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
    public void showPlaces(Location location, IntegrationPoint.XPlaces xplaces, IntegrationPoint.Timeout timeout){
        // IP - integration point: arq needed
        IntegrationPointImpl ip = new IntegrationPointImpl();
        ip.getXPlacesAroundLocation(location, xplaces, timeout);

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
                showPlaces(location, xPlaces, timeout);
            }
            else{
                toast("temp: location is null");
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
           //Address address = (Address) geocoder.getFromLocationName(searchedLocation,1);
            // toast here searchedLocation

            toast(searchedLocation);

            geocoder = new Geocoder(this);
            List<android.location.Address> addresses = geocoder.getFromLocationName(searchedLocation,1);
            if(addresses.size() > 0) {
                double latitude= addresses.get(0).getLatitude();
                double longitude= addresses.get(0).getLongitude();
                String s = String.valueOf(latitude);
                // toast here string s
                //toast(s);
                toast("hello");
                goThere(latitude,longitude, searchedLocation);
            }
            else{
                toast("bye hello");            }


        }
        catch(Exception e){
            e.printStackTrace();
        }

    }
}
