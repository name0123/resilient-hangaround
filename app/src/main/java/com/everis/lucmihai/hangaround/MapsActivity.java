package com.everis.lucmihai.hangaround;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
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
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    SupportMapFragment mapFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment= (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.search_bar);
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

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        /*// Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        */
        Context context = getApplicationContext();

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
            int duration = Toast.LENGTH_LONG;
            Toast toast = Toast.makeText(context, stringll, duration);
            toast.show();

            final LatLng myLoc = new LatLng(latitude,longitude);
            mMap.addMarker(new MarkerOptions().position(myLoc).title("You are here"));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLoc,15f));
            /*mMap.setMyLocationEnabled(true);
            mMap.animateCamera( CameraUpdateFactory.zoomTo( 57.0f ) );
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
*/
        }
        catch (SecurityException e){
            e.printStackTrace();
        }
    }
}
