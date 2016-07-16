package com.everis.lucmihai.hangaround;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.identity.intents.Address;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import java.util.List;

import butterknife.OnClick;
import stanford.androidlib.SimpleActivity;

public class MapsActivity extends SimpleActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    SupportMapFragment mapFragment;
    Geocoder geocoder;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
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
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
*/
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

        final LocationManager locationManager = (LocationManager)
                getSystemService(Context.LOCATION_SERVICE);


        Criteria criteria = new Criteria();
        try {
            final Location location = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, false));
            final double latitude = location.getLatitude();
            final double longitude = location.getLongitude();
            String stringll = String.valueOf(latitude);
            stringll += "  ";
            stringll += String.valueOf(longitude);
            toast(stringll);

            goThere(latitude,longitude, "you are here");

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
