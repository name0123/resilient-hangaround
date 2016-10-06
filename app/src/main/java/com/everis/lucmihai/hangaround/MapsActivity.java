package com.everis.lucmihai.hangaround;

import android.app.ProgressDialog;
import android.content.Context;
import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.everis.lucmihai.hangaround.dokimos.IntegrationPoint;
import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
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
	private SupportMapFragment mapFragment;
	private Geocoder geocoder;
    private Context context;
    private boolean loggedIn = false;
    private static final String TAG = "KarambaMaps";
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
        FacebookSdk.sdkInitialize(this);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment= (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.search_bar);
        toolbar.setTitle("");
        toolbar.setLogo(R.drawable.ic_action_action_search);
        setSupportActionBar(toolbar);
        String whoIsThis = "";

        if(getIntent().getExtras() != null) {
            whoIsThis = getIntent().getStringExtra("logged");
            Button blogin = (Button) findViewById(R.id.blogin);
            whoIsThis = Profile.getCurrentProfile().getName();
            blogin.setText("Welcome \n" + whoIsThis);
        }
        else {
            Intent intent = new Intent(MapsActivity.this, LoginActivity.class);
            startActivity(intent);
        }
    }

	@Override
	public void onMapReady(GoogleMap googleMap) {
		mMap = googleMap;
		context = getApplicationContext();
		mMap.getUiSettings().setMyLocationButtonEnabled(true);
		mMap.setOnMarkerClickListener(this);

	}

	@Override
	public boolean onMarkerClick(Marker marker) {
		if(!loggedIn) showUserValorationOptions(marker);
		else showGuestOptions();

		return false;
	}


    public void getPlaces(double latitude, double longitude){
        URL url1 = null;
        try {
            // YOU PROBABLY NEED TO CALL 4SQARE FROM HERE!
            //url1 = new URL("https://mobserv.herokuapp.com/4square/search?ll=41.3830878006894,2.04654693603516&limit=50");
            //url1 = new URL("https://mobserv.herokuapp.com/places/get?ll=41.3830878006894,2.04654693603516");
            //url1 = new URL("https://mobserv.herokuapp.com/places/getall");
            //url1 = new URL("https://mobserv.herokuapp.com/places/getcs?ll=41.3830878006894,2.04654693603516");
            //url1 = new URL("https://mobserv.herokuapp.com/4square/search?near=near"); // you have the location
            String fourSquareSearch = "https://mobserv.herokuapp.com/4square/search?ll=";
            fourSquareSearch += Double.toString(latitude);
            fourSquareSearch += ',';
            fourSquareSearch += Double.toString(longitude);
            //Toast.makeText(getBaseContext(), TAG+fourSquareSearch, Toast.LENGTH_SHORT).show();
            Log.d(TAG,fourSquareSearch);
            url1 = new URL(fourSquareSearch);


        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        new getPlacesBackground().execute(url1);
    }

	/**
	 * Dialogs here
	 *
	 */
	public void showValorationDialog(final Marker marker) {
        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.valoration_dialog, null);
        final Spinner spAdaptLevel = (Spinner) alertLayout.findViewById(R.id.spAdaptLevel);

        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle(marker.getTitle());
        alert.setView(alertLayout);
        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Toast.makeText(getBaseContext(), "Cancel clicked", Toast.LENGTH_SHORT).show();
            }
        });

        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String adaptedLevel = String.valueOf(spAdaptLevel.getSelectedItem());
				LatLng place = marker.getPosition();
				String adl = spAdaptLevel.getSelectedItem().toString();
				Toast.makeText(getBaseContext(), adl, Toast.LENGTH_LONG).show();
				ValorLatLong newValorationPlace = null;

				updatePlaceAdaptedLevel(newValorationPlace);
            }
        });
        AlertDialog dialog = alert.create();
        dialog.show();
    }

	private void updatePlaceAdaptedLevel(ValorLatLong adl) {
		URL url1 = null;
		try {
			String placeUpdate = "https://mobserv.herokuapp.com/places/update?ll=";
			placeUpdate += Double.toString(adl.lat);
			placeUpdate += ',';
			placeUpdate += Double.toString(place.longitude);
			placeUpdate += "&al="+adl;

			Log.d(TAG,placeUpdate);
			url1 = new URL(placeUpdate);


		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		new getPlacesBackground().execute(url1);
	}


	private void gpsRequestDialog(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                this);

        // set title
        alertDialogBuilder.setTitle("GPS request");

        // set dialog message
        alertDialogBuilder
				.setMessage("GPS is not enabled, would you like to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        // if this button is clicked, close
                    isGPSEnable();
                    }
                })
                .setNegativeButton("No",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // if this button is clicked, just close
                        // the dialog box and do nothing
                        dialog.cancel();
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

    }
    private void showUserValorationOptions(Marker marker){
        // this should be different
        showValorationDialog(marker);
    }

    private void showGuestOptions(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                this);

        // set title
        alertDialogBuilder.setTitle("Your Title");

        // set dialog message
        alertDialogBuilder
                .setMessage("Guest: yes to exit!")
                .setCancelable(false)
                .setPositiveButton("Yes",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        // if this button is clicked, close
                        // current activity
                        MapsActivity.this.finish();
                    }
                })
                .setNegativeButton("No",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // if this button is clicked, just close
                        // the dialog box and do nothing
                        dialog.cancel();
                    }
                });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();

    }

private class getPlacesBackground extends AsyncTask<URL, Integer, JSONArray> {
		private ProgressDialog Dialog = new ProgressDialog(MapsActivity.this);

		@Override
		protected void onPreExecute()
		{
			Dialog.setMessage("Loading places ...");
			Dialog.show();
		}

		protected JSONArray doInBackground(URL... urls) {

            try {
                HttpURLConnection conn = (HttpURLConnection) urls[0].openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Accept", "application/json");
                int respCode = conn.getResponseCode();

                if (respCode != 200) {
                    Log.d(TAG, (String)"Errores:" + respCode);

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
                    publishProgress();
                    return json;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }


        protected void onPostExecute(JSONArray result) {
			Dialog.dismiss();
            if(result != null) {
				go(result);
            }
            else{
                // no places found, to be handled
            }
        }
    }
	private class ValorLatLong {
		String uid;
		Double lat;
		Double lng;
		Boolean ac;
		Boolean wc;
		Elev el;
	}
	private enum Elev{
		HAS,HAS_NOT,NO_NEED
	}


	private LatLng minim(JSONArray places){

		double lat = 0;
		double lng = 0;

		try {
			lat = ((JSONObject) places.get(0)).getDouble("latitude");
			lng = ((JSONObject) places.get(0)).getDouble("longitude");
			for (int i = 1; i < places.length(); ++i){
				if(((JSONObject) places.get(i)).getDouble("latitude") < lat){
					lat = ((JSONObject) places.get(i)).getDouble("latitude");
				}
				if(((JSONObject) places.get(i)).getDouble("longitude") < lng){
					lng = ((JSONObject) places.get(i)).getDouble("longitude");
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		LatLng result = new LatLng(lat,lng);
		return result;
	}
	private LatLng maxim(JSONArray places){
		double lat = 0;
		double lng = 0;
		try {
			lat = ((JSONObject) places.get(0)).getDouble("latitude");
			lng = ((JSONObject) places.get(0)).getDouble("longitude");
			for (int i = 1; i < places.length(); ++i){
				if(((JSONObject) places.get(i)).getDouble("latitude") > lat){
					lat = ((JSONObject) places.get(i)).getDouble("latitude");
				}
				if(((JSONObject) places.get(i)).getDouble("longitude") > lng){
					lng = ((JSONObject) places.get(i)).getDouble("longitude");
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		LatLng result = new LatLng(lat,lng);

		return result;
	}

    public void isGPSEnable(){
        LocationManager service = (LocationManager) getSystemService(LOCATION_SERVICE);
        boolean enabled = service
                .isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!enabled) {
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);
        }
		else{
			// we will see what to do here
		}

    }
	private void go(JSONArray places){
		/**
		 * we came here from postExecuteGett places
		 * If lat & long != 0 (somewhere near Guinee Golf)
		 * we need to get location
		 * Else we just try to go there
		 * At least one place in places!
		 */

		Criteria criteria = new Criteria();

		try {
			double lat = ((JSONObject) places.get(0)).getDouble("latitude");
			double lng = ((JSONObject) places.get(0)).getDouble("longitude");
			LatLng firstPlace = new LatLng(lat,lng);
			int j = places.length()-1;
			if(j > 0 ){
				// more than a place
				firstPlace = minim(places);
				LatLng secondPlace = maxim(places);
				LatLngBounds llb = new LatLngBounds(firstPlace,secondPlace);
				Toast.makeText(getBaseContext(), TAG+" more places "+j, Toast.LENGTH_SHORT).show();
				mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(llb,60));
				showPlaces(places);
			}
			else {
				// there is just one place
				Toast.makeText(getBaseContext(), TAG+" just one dude!", Toast.LENGTH_SHORT).show();
				mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(firstPlace,10f));
			}
		}
		catch (Exception e){
			e.printStackTrace();
		}
	}

	private void showPlaces(JSONArray places){
		try {
			JSONObject place = (JSONObject) places.get(0);
			showMarker(place);
			for(int i = 1; i < places.length(); ++i){
				place = (JSONObject)places.get(i);
				showMarker(place);
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


	/**
	 * On clicks  only 2 of them!
	 *
	 * @param view
	 */
	@OnClick(R.id.blogin)
	public void onClickLogin(View view){
		Intent intent = new Intent(MapsActivity.this, LoginActivity.class);
		startActivity(intent);
	}
    @OnClick(R.id.bsearch)
    public void onClickSearch(View view){
        // get the first address in text search - call GetPlaces()
		InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        EditText searchedText = (EditText) findViewById(R.id.txtsearch);
        String searchedLocation = searchedText.getText().toString();
        try {
           //Address address = (Address) geocoder.getFromLocationName(searchedLocation,1); cool
            geocoder = new Geocoder(this);
            List<android.location.Address> addresses = geocoder.getFromLocationName(searchedLocation,1);
            if(addresses.size() > 0) {
                double latitude= addresses.get(0).getLatitude();
                double longitude= addresses.get(0).getLongitude();
                getPlaces(latitude,longitude);
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

}
