package com.everis.lucmihai.hangaround;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.everis.lucmihai.hangaround.maps.AsyncTaskCompleteListener;
import com.everis.lucmihai.hangaround.maps.Connection;
import com.everis.lucmihai.hangaround.maps.ConnectionStatusCheck;
import com.everis.lucmihai.hangaround.maps.GetAdaptationConnection;
import com.everis.lucmihai.hangaround.maps.GetStart;
import com.everis.lucmihai.hangaround.maps.KeepCheckingConnection;
import com.everis.lucmihai.hangaround.maps.PostConnection;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
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

import java.util.Map;

import butterknife.OnClick;
import stanford.androidlib.SimpleActivity;


public class MapsActivity extends SimpleActivity implements AsyncTaskCompleteListener, OnMapReadyCallback,
		GoogleApiClient.ConnectionCallbacks,
		GoogleApiClient.OnConnectionFailedListener,
		GoogleMap.OnMarkerClickListener,
		GoogleMap.OnMyLocationButtonClickListener,
		LocationListener {

	private GoogleMap mGoogleMap;
	private SupportMapFragment mapFragment;
	private Geocoder geocoder;
	private Location location;
	private Context context;
	private boolean loggedIn = false; // too hard coded!
	private static final String TAG = "KarambaMaps";
	private final Float CTOTAL = BitmapDescriptorFactory.HUE_GREEN;
	private final Float CPARTIAL = BitmapDescriptorFactory.HUE_YELLOW;
	private final Float CUNADAPTED = BitmapDescriptorFactory.HUE_RED;
	private final Float CUNKNOWN = BitmapDescriptorFactory.HUE_CYAN;
	private final String SUNKNOWN = "UNKNOWN";
	private final String SUNADAPTED = "UNADAPTED";
	private final String SPARTIAL = "PARTIAL";
	private final String STOTAL = "TOTAL";
	public static final String PREFS_NAME = "SearchCache";
	private int count = 0;
	private JSONArray places = null;
	private String BACKEND_MODE = "ONLINE";

	SupportMapFragment mapFrag;
	LocationRequest mLocationRequest;
	GoogleApiClient mGoogleApiClient;
	Location mLastLocation;
	Marker mCurrLocationMarker;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_maps);
		context = getApplicationContext();
		context.getSharedPreferences("SearchCache",Context.MODE_ENABLE_WRITE_AHEAD_LOGGING);
		context.getSharedPreferences("DirtyVoteCache",Context.MODE_ENABLE_WRITE_AHEAD_LOGGING);
		context.getSharedPreferences("DirtySearchCache",Context.MODE_ENABLE_WRITE_AHEAD_LOGGING);
		mapFrag = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
		mapFrag.getMapAsync(this);
		//getStart(); // make the first call to wake up  API host, random call, no result taken
		String userLog="guest";
		if (savedInstanceState == null) {
			Bundle extras = getIntent().getExtras();
			if(extras != null) {
				userLog= extras.getString("logged");
			}
		} else {
			userLog= (String) savedInstanceState.getSerializable("logged");
		}
		if(!userLog.equals("guest")) {
			loggedIn = true;
			Button lgnButton = (Button) findById(R.id.blogin);
			lgnButton.setText("Welcome \n"+userLog);
		}

	}

	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public void onMapReady(GoogleMap googleMap) {
		mGoogleMap = googleMap;
		mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
		mGoogleMap.setOnMarkerClickListener(this);
		//Initialize Google Play Services
		buildGoogleApiClient();
		if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			checkLocationPermission();
			// TODO: show progress dialog enabling gps , searching for your possition and getPlaces
			if (ContextCompat.checkSelfPermission(this,
					Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
				mGoogleMap.setMyLocationEnabled(true);
				mGoogleMap.setOnMyLocationButtonClickListener(this);
			}
		} else mGoogleMap.setMyLocationEnabled(false);

	}

	protected synchronized void buildGoogleApiClient() {
		mGoogleApiClient = new GoogleApiClient.Builder(this)
				.addConnectionCallbacks(this)
				.addOnConnectionFailedListener(this)
				.addApi(LocationServices.API)
				.build();
		mGoogleApiClient.connect();
	}

	@Override
	public void onConnected(Bundle bundle) {
		final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE );
		mLocationRequest = new LocationRequest();
		mLocationRequest.setInterval(1000);
		mLocationRequest.setFastestInterval(1000);
		mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
		if (ContextCompat.checkSelfPermission(this,
				Manifest.permission.ACCESS_FINE_LOCATION)
				== PackageManager.PERMISSION_GRANTED) {
			LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
			if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER )) {
				showGpsEnable();

			}
		}
	}

	@Override
	public void onConnectionSuspended(int i) {
	}

	@Override
	public void onConnectionFailed(ConnectionResult connectionResult) {
	}

	@Override
	public void onLocationChanged(Location location) {
		mLastLocation = location;
		if (mCurrLocationMarker != null) {
			mCurrLocationMarker.remove();
		}

		//Place current location marker
		LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
		/*MarkerOptions markerOptions = new MarkerOptions();
		markerOptions.position(latLng);
		markerOptions.title("Current Position");
		markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
		mCurrLocationMarker = mGoogleMap.addMarker(markerOptions);
*/
		//move map camera
		mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
		mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(11));
		//definetly not moving around!
		getPlaces(latLng.latitude, latLng.longitude, this);

		//stop location updates
		if (mGoogleApiClient != null) {
			LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, (com.google.android.gms.location.LocationListener) this);
		}
	}

	public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

	public boolean checkLocationPermission() {
		if (ContextCompat.checkSelfPermission(this,
				Manifest.permission.ACCESS_FINE_LOCATION)
				!= PackageManager.PERMISSION_GRANTED) {

			// Should we show an explanation?
			if (ActivityCompat.shouldShowRequestPermissionRationale(this,
					Manifest.permission.ACCESS_FINE_LOCATION)) {

				//TODO:
				// Show an expanation to the user *asynchronously* -- don't block
				// this thread waiting for the user's response! After the user
				// sees the explanation, try again to request the permission.

				//Prompt the user once explanation has been shown
				//(just doing it here for now, note that with this code, no explanation is shown)
				ActivityCompat.requestPermissions(this,
						new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
						MY_PERMISSIONS_REQUEST_LOCATION);


			} else {
				// No explanation needed, we can request the permission.
				ActivityCompat.requestPermissions(this,
						new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
						MY_PERMISSIONS_REQUEST_LOCATION);
			}
			return false;
		} else {
			return true;
		}
	}

	@Override
	public void onRequestPermissionsResult(int requestCode,
	                                       String permissions[], int[] grantResults) {
		switch (requestCode) {
			case MY_PERMISSIONS_REQUEST_LOCATION: {
				// If request is cancelled, the result arrays are empty.
				if (grantResults.length > 0
						&& grantResults[0] == PackageManager.PERMISSION_GRANTED) {

					// permission was granted, yay! Do the
					// location-related task you need to do.
					if (ContextCompat.checkSelfPermission(this,
							Manifest.permission.ACCESS_FINE_LOCATION)
							== PackageManager.PERMISSION_GRANTED) {

						if (mGoogleApiClient == null) {
							buildGoogleApiClient();
						}
						mGoogleMap.setMyLocationEnabled(true);
					}
					if(location != null) getPlaces(location.getLatitude(), location.getLongitude(),this);
					// this is not going to be used... to test
					else Log.e(TAG,"location is null!");

				} else {

					// permission denied, boo! Disable the
					// functionality that depends on this permission.
					Toast.makeText(this, "Permission denied: location features disabled!", Toast.LENGTH_LONG).show();
				}
				return;
			}

			// other 'case' lines to check for other
			// permissions this app might request
		}
	}

	@Override
	public boolean onMarkerClick(Marker marker) {
		//TODO: figure this out: you show the vote button if user
		// on simple click just show title
		// on long(dragstart) showUserValorationOptions!
		//http://stackoverflow.com/questions/15391665/setting-a-longclicklistener-on-a-map-marker
		showUserValorationOptions(marker);
		//else showGuestOptions();
		return false;
	}


	public void getPlaces(double latitude, double longitude, Activity activity) {

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
		fourSquareSearch += "&radius=150";
		fourSquareSearch += "&limit=25";
//		Log.d(TAG, fourSquareSearch);
		new Connection(this).execute(fourSquareSearch, this);
	}

	// this is public doInBackground() - async (save name, if in cache we )
	public JSONArray getPlacesName(String searchedName, Activity a) {
		// YOU PROBABLY NEED TO CALL 4SQARE FROM HERE!
		//url1 = new URL("https://mobserv.herokuapp.com/4square/search?near=near"); // you have the location
		String fourSquareSearch = "https://mobserv.herokuapp.com/4square/search?near=";
		fourSquareSearch += searchedName;
		fourSquareSearch += "&radius=150";
		fourSquareSearch += "&limit=25";
		new Connection(this).execute(fourSquareSearch,searchedName, this);
		return null;
	}

	@Override
	public boolean onMyLocationButtonClick() {
		Log.d(TAG, "Is this just real life?");
		checkLocationPermission();
		return false;
	}

	private class ValorFourId {
		String uid;
		String four_id;
		Boolean ac;
		Boolean wc;
		Elev el;

	}
	private enum Elev{
		HAS,HAS_NOT,NO_NEED
	}

	/**
	 * Dialogs here
	 *
	 */
	public void showValorationDialog(final Marker marker, final int index) {
        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.valoration_dialog, null);
		final Spinner spGenAccess = (Spinner) alertLayout.findViewById(R.id.spgena);
		final Spinner spWcAccess = (Spinner) alertLayout.findViewById(R.id.spwca);
		final Spinner spElev = (Spinner) alertLayout.findViewById(R.id.spElev);
		String placeCategory = "";
/*		Log.d(TAG, " This is the marker: "+marker.toString());
		Log.d(TAG, " This is the index: "+index);
		Log.d(TAG, " This is places's length: "+places.length());*/
		try{
			JSONObject place = places.getJSONObject(index);
			placeCategory = place.get("category").toString();
		} catch (Exception  e){
			e.printStackTrace();
		}

        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle(placeCategory + ": " + marker.getTitle());
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
	            ValorFourId newValPlace = new ValorFourId();

	            // break here; print places, and print marker
	            try{
		            JSONObject place = places.getJSONObject(index);
		            newValPlace.uid = "misho0stequer2@gmail.com"; // user_id: hardcoded provisional
		            newValPlace.four_id = place.getString("four_id"); // or venue_id
		            newValPlace.ac = Boolean.valueOf(spGenAccess.getSelectedItem().toString());
		            newValPlace.wc = Boolean.valueOf(spWcAccess.getSelectedItem().toString());
		            newValPlace.el = Elev.valueOf(spElev.getSelectedItem().toString());

		            //Log.i(TAG,newValPlace.el.toString());
		            ObjectMapper mapper = new ObjectMapper();
		            String newValPlaceJson = mapper.writeValueAsString(newValPlace);
		            Log.e(TAG, "Json stuff: "+newValPlaceJson.toString());
		            votePlace(newValPlaceJson,place.getString("four_id"),index,MapsActivity.this);

	            }catch (Exception e){
		            // TODO: handling needed!
		            e.printStackTrace();
		            Log.d(TAG," ahora hay un problema grodo!");
	            }
            }
        });
        AlertDialog dialog = alert.create();
        dialog.show();
    }

	public void showPlaceValorationDialog(final Marker marker, final int index) {
		LayoutInflater inflater = getLayoutInflater();
		View alertLayout = inflater.inflate(R.layout.place_valoration_dialog, null);
		TextView adaptedLevel = (TextView) alertLayout.findViewById(R.id.tvga);
		String placeCategory = "";
		if (places.length() > 0) {
			try {
				JSONObject place = places.getJSONObject(index);
				placeCategory = place.get("category").toString();
				adaptedLevel.setText(place.getString("adaptedLevel"));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setTitle(placeCategory + ": " + marker.getTitle());
		alert.setView(alertLayout);

		alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				//Toast.makeText(getBaseContext(), "Cancel clicked", Toast.LENGTH_SHORT).show();
			}
		});

		alert.setPositiveButton("Vote", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// wanna vote the place!
				if(loggedIn){
					showValorationDialog(marker, index);
				}
				else{
					Toast.makeText(getBaseContext(), "Only registered users can vote!", Toast.LENGTH_LONG).show();
				}
			}
		});

		AlertDialog dialog = alert.create();
		dialog.show();
		dialog.getWindow().setBackgroundDrawableResource(android.R.color.holo_blue_dark);

	}
	private void setLevelForPlace(Integer index, String nvl){
		// TODO: debugg NO_NEED - complete!
		//Log.i(TAG, "Level debug: "+nvl);
		Boolean a,w,e;
		a = w = e = false;
		String el = "";
		try{
			JSONObject level = new JSONObject(nvl);
			a = level.getBoolean("ac");
			w = level.getBoolean("wc");
			el = level.getString("el");
			if(el.equals("HAS") || el.equals("NO_NEED")) e = true;
			JSONObject place = (JSONObject) places.get(index);
			if(a&&w&&e) place.put("adaptedLevel", "TOTAL");
			else if(a||w||e) place.put("adaptedLevel", "PARTIAL");
			else place.put("adaptedLevel", "UNADAPTED");
			places.put(index,place);
		}catch (Exception ex){
			ex.printStackTrace();
		}
	}
	public void votePlace(String nvl, String four_id, Integer index, Activity a) {

		try {
			String setValofd = "https://mobserv.herokuapp.com/valorations/newfour";
			//setValofd += four_id
			String args[] = new String []{setValofd,nvl};
			//Log.e(TAG, "Place before: "+places);
			setLevelForPlace(index,nvl);
			Log.e(TAG, "Place after : "+places.get(index).toString());
			showMarker(index);
			Log.e(TAG, "to post it");
			new PostConnection(this).execute(args);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void showGpsEnable() {
		LayoutInflater inflater = getLayoutInflater();
		View alertLayout = inflater.inflate(R.layout.gps_dialog, null);
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setTitle("Enable GPS");
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
				enableGps();

			}
		});
		AlertDialog dialog = alert.create();
		dialog.show();
	}

    private void showUserValorationOptions(Marker marker){
        // I need to get the index of the place pointed by the marker
	    String title = marker.getTitle();
	    int index = -1;
	    for(int i = 0; i < places.length(); ++i){
		    try {
			    String placeName = ((JSONObject) places.get(i)).getString("name");
			    if (title.equals(placeName)) index = i;
		    }
		    catch (Exception e){
			    e.printStackTrace();
		    }
	    }
	    if(index != -1) showPlaceValorationDialog(marker, index);
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


	@Override
	public void onGetPlacesComplete(JSONArray result, int number) {
		if(result != null) {
			//Log.d(TAG, "onGetPlacesComplete not null/empty: "+result);
			places = result;
			for(int i = 0; i<places.length(); i++){
				getAdaptationLevel(i);
			}
			go();
			// showPlaces cannot be done here, because the places does not have the adaptedLevel yet
			//showPlaces(places, this);
		}
		else{
			// the searchedLocation may not have places, or the connection could be the problem: new aspect on funciton
			String nonp = checkConnections(this);
			Log.d(TAG, "No places found: "+nonp);

		}
    }

	public String checkConnections(Activity mapsActivity) {
		new ConnectionStatusCheck(this).execute();
		return "CHECKING CONNECTIONS";
	}

	@Override
	public void onGetAdaptationComplete(String result, int number){
		// result contains adaptation,four_id level for a place!
		//Log.d(TAG, " Adapted level got: "+result);
		String[] adaptIndex = result.split(",");
		try {
			//why adaptIndex should be an int?
			// because is the number of de index attached at the json we search
			int i = adaptIndex.length;
			int index = Integer.parseInt(adaptIndex[i-1]);
			JSONObject place = (JSONObject) places.get(index);
			String al = adaptIndex[i-2]; // "adaptedLevel":"UNKNOWN"};
			al = al.split(":")[1];
			al = al.replace("\"", "");
			al = al.replace("}", "");
			Log.d(TAG, "put is put, or is append: "+String.valueOf(places.length())+' '+count);
			place.put("adaptedLevel", al);
			places.put(index,place);
			++count;
			if(count == places.length()){
				Log.d(TAG, " Trying hard: "+places.get(index).toString()+' '+count);
				showPlaces(places, this);
				count = 0;
			}


		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		Log.d(TAG, "onGetAdaptedLevel");
		updatePlaces(places);
	}

	@Override
	public void onVotedPlace(JSONObject result) {
		// on place voted: we do had connection
		Log.d(TAG, "onVotedPlace: here the result is diferent from the json posted! "+result);
		if(result != null) afterVote(result, this);
		else {
			String nonp = checkConnections(this);
			Log.d(TAG, "something just happened!");
			return;
		}
		// re-thinkable
		//if("OFFLINE".equals(BACKEND_MODE))dirtyVotesUpdate();
	}

	@Override
	public String onConnectionStatusCheck(String s) {
		// BACK FROM connectionSratusCheck(s is final)
		if("INTERNET_OFFLINE".equals(s)){
			Toast.makeText(getBaseContext(), "We are experiencing connection problems! " +
					"\n You seem to be offline!", Toast.LENGTH_LONG).show();
			keepChecking(this);
		}
		else if("BACKEND_OFFLINE".equals(s)){
			Toast.makeText(getBaseContext(), "We are experiencing connection problems! " +
					"\n Our servers seem to be offline!", Toast.LENGTH_LONG).show();
			keepChecking(this);
		}
		else {
			Toast.makeText(getBaseContext(), "There is no city with this name on Earth! " +
					"\n Or there are no places in this city!", Toast.LENGTH_LONG).show();
		}
		return null;
	}

	@Override
	public String onKeepChecking(String s) {
		// we are back in the game : ONLINE
		// dirty bits should do the job
		dirtyVotesUpdate();
		// - last searchedName should be called
		dirtySearchUpdate();
		return null;
	}

	private void dirtySearchUpdate() {
	}

	private void dirtyVotesUpdate() {
		//Toast, Good news everyone, connection is back. Your votes are being uploaded!
		SharedPreferences sharedprf = context.getSharedPreferences("DirtyVoteCache",Context.MODE_PRIVATE);
		if(sharedprf != null) {
			SharedPreferences.Editor ed = sharedprf.edit();
			Map<String, String> dirtyVoteCache = (Map<String, String>) sharedprf.getAll();
			for (Map.Entry<String, String> entry : dirtyVoteCache.entrySet()) {
				String key = entry.getKey();
				String nvl = entry.getValue();
				String setValofd = "https://mobserv.herokuapp.com/valorations/newfour";
				String args[] = new String []{setValofd,nvl};
				new PostConnection(this).execute(args);
				break; // just one connection at a time.
			}
		}
	}

	public String keepChecking(MapsActivity mapsActivity) {
		BACKEND_MODE = "OFFLINE"; // INTERNET might be offline too, but rest!
		new KeepCheckingConnection(this).execute();
		return null;
	}

	public void afterVote(JSONObject result, Activity mapsActivity) {
		if(result != null) {
			Toast.makeText(getBaseContext(), "Your vote has been saved", Toast.LENGTH_SHORT).show();
			// esborrem la cache (result no needed on votePlace)
		}
		else{
			Toast.makeText(getBaseContext(), "We are experiencing connection problems! " +
					"\n your vote will be saved ASAP!", Toast.LENGTH_SHORT).show();
			keepChecking(this);
		}
	}

	private LatLng minim(JSONArray places){
// useless because of 4square - usefull if get places from /places/getall and
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

    public void enableGps(){
        LocationManager service = (LocationManager) getSystemService(LOCATION_SERVICE);
        boolean enabled = service
                .isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!enabled) {
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);
        }
    }
	public void go(){
		/**
		 * we came here from postExecuteGett places
		 * If lat & long != 0 (somewhere near Guinee Golf)
		 * we need to get location
		 * Else we just try to go there
		 * At least one place in places!
		 */

		if(places != null && places.length() > 0) {
		// checking places has new values!
			try {
				double lat = ((JSONObject) places.get(0)).getDouble("latitude");
				double lng = ((JSONObject) places.get(0)).getDouble("longitude");
				LatLng firstPlace = new LatLng(lat, lng);
				int j = places.length() - 1;
				if (j > 0) {
					// more than a place
					firstPlace = minim(places);
					LatLng secondPlace = maxim(places);
					LatLngBounds llb = new LatLngBounds(firstPlace, secondPlace);
					mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(llb, 60));
				} else {
					// there is just one place
					Toast.makeText(getBaseContext(), "Just one dude!", Toast.LENGTH_SHORT).show();
					mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(firstPlace, 10f));
				}
			} catch (Exception e) {
				Toast.makeText(getBaseContext(), " No place found", Toast.LENGTH_SHORT).show();
				e.printStackTrace();
			}
		}
		else {
			Log.d(TAG, "Aun no hay sitios: places = null");
			Toast.makeText(this, "Sorry: No places found!", Toast.LENGTH_LONG).show();
		}
	}
	// cache places at this point
	public void showPlaces(JSONArray places, Activity activity) {
		// this is called after getting the addptedLevel for every place in places - on get done! in go.
		//Log.e(TAG,"showing places with markers: "+ String.valueOf(places!=null));
		if (places != null) {
			//Log.e(TAG,"showing places with markers"+places.length());
			for (int i = 0; i < places.length(); ++i) {
				showMarker(i);
			}
		}
	}

	/**
	 *      interesting soultion, to update markers, no aspects triggered 20 times!
	 *
	 * @param places
	 */
	public void updatePlaces(JSONArray places) {
		if (places != null) {
			for (int i = 0; i < places.length(); ++i) {
				showMarker(i);
			}
		}
	}
	private void showMarker(int i) {
		double lat = 0.0;
		double lng = 0.0;

		String adaptationLevel ="UNKNOWN";
		String placeName="";
		try {
			JSONObject place = (JSONObject) places.get(i);
			lat = place.getDouble("latitude");
			lng = place.getDouble("longitude");
			adaptationLevel = place.getString("adaptedLevel");
			placeName = place.getString("name");
		} catch (JSONException e) {
			e.printStackTrace();
		}

		LatLng placeLocation = new LatLng(lat, lng);

		if(adaptationLevel.equals(SUNKNOWN)){
			Marker placeMarker = mGoogleMap.addMarker(new MarkerOptions()
					.position(placeLocation)
					.icon(BitmapDescriptorFactory.defaultMarker(CUNKNOWN))
					.alpha(0.21f)
					.rotation(0.5f)
					.title(placeName)
			);
		}

		else if(adaptationLevel.equals(STOTAL)) {
			Marker placeMarker = mGoogleMap.addMarker(new MarkerOptions()
					.position(placeLocation)
					.icon(BitmapDescriptorFactory.defaultMarker(CTOTAL))
					.title(placeName)
			);
		}
		else if(adaptationLevel.equals(SPARTIAL)) {
			Marker placeMarker = mGoogleMap.addMarker(new MarkerOptions()
					.position(placeLocation)
					.icon(BitmapDescriptorFactory.defaultMarker(CPARTIAL))
					.title(placeName)
			);
		}
		else if(adaptationLevel.equals(SUNADAPTED)) {
			Marker placeMarker = mGoogleMap.addMarker(new MarkerOptions()
					.position(placeLocation)
					.icon(BitmapDescriptorFactory.defaultMarker(CUNADAPTED))
					.title(placeName)
			);
		}
	}

	private String getAdaptationLevel(int i) {
		String surl = "https://mobserv.herokuapp.com/places/getfour?id=";
		try {
			//Log.d(TAG, " Getting adaption level for: "+places.get(i).toString());
			String args[] = new String[]{surl, places.get(i).toString(), String.valueOf(i)};
			new GetAdaptationConnection(this).execute(args);
		}
		catch (Exception e){
			e.printStackTrace();
		}
		return "UNKNOWN";
	}
	private String getStart() {
		String surl = "https://mobserv.herokuapp.com/users/getall";
		try {
			String args[] = new String[]{surl};
			new GetStart(this).execute(args);
		}
		catch (Exception e){
			e.printStackTrace();
		}
		return "UNKNOWN";
	}

	@Override
	protected void onStart(){
		super.onStart();
		SharedPreferences sp = getSharedPreferences("run", MODE_PRIVATE);
		SharedPreferences.Editor ed = sp.edit();
		ed.putBoolean("active", true);
		ed.commit();
	}
	@Override
	protected void onStop() {
		super.onStop();
		SharedPreferences sp = getSharedPreferences("run", MODE_PRIVATE);
		SharedPreferences.Editor ed = sp.edit();
		ed.putBoolean("active", false);
		ed.commit();

	}
	private void showShared(){
		Log.i("sharedTag: ","Showing Shared Values");
		SharedPreferences sharedprf = context.getSharedPreferences("SearchCache",Context.MODE_PRIVATE);
		if(sharedprf != null){
			Map<String, String> allEntries = (Map<String, String>) sharedprf.getAll();
			for (Map.Entry<String, String> entry : allEntries.entrySet()) {
				String key = entry.getKey();
				String value = entry.getValue();
				Log.i("sharedTag: ", key+' '+value);
			}
		}
	}
	private void showDirty(){
		Log.i("sharedTag: ","Showing Dirty votes Values");
		SharedPreferences sharedprf = context.getSharedPreferences("DirtyVoteCache",Context.MODE_PRIVATE);
		if(sharedprf != null){
			Map<String, String> allEntries = (Map<String, String>) sharedprf.getAll();
			for (Map.Entry<String, String> entry : allEntries.entrySet()) {
				String key = entry.getKey();
				String value = entry.getValue();
				Log.i("dirtyTag: ", key+' '+value);
			}
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
	    places = null;
	    showShared();
	    showDirty();
	    //gettingPlacesProgressDialog();
		InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        EditText searchedText = (EditText) findViewById(R.id.txtsearch);
        String searchedLocation = searchedText.getText().toString();
	    places = getPlacesName(searchedLocation, this);
	    // if cached > places has the places > so we can show them
	    if(places != null) {
		    go();
		    showPlaces(places, this);
	    }
	    // else > cb will do its job when places is initialized by
	    // TODO: some cases here if shearchedLocation is address, place, city country ...etc

	    /* https://developer.android.com/reference/android/location/Geocoder.html
        try {
           //Address address = (Address) geocoder.getFromLocationName(searchedLocation,1); cool
            geocoder = new Geocoder(this);
            List<android.location.Address> addresses = geocoder.getFromLocationName(searchedLocation,1);
            if(addresses.size() > 0) {

                double latitude= addresses.get(0).getLatitude();
                double longitude= addresses.get(0).getLongitude();
                //getPlaces(latitude,longitude);

            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
            No required anymore, if you search by name, less dependecies!
        */
    }

	private void cacheSearch(String searchedLocation) {
		SharedPreferences sharedPref = context.getSharedPreferences(PREFS_NAME,Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPref.edit();
		editor.putString(searchedLocation,"empty");
		editor.commit();

	}

	private void gettingPlacesProgressDialog() {
		final ProgressDialog barProgressDialog;
		final Handler updateBarHandler = new Handler();
		barProgressDialog = new ProgressDialog(MapsActivity.this);
		barProgressDialog.setTitle("Getting places");
		barProgressDialog.setMessage("We are searching places around this area");
		barProgressDialog.setProgressStyle(barProgressDialog.STYLE_HORIZONTAL);
		barProgressDialog.setProgress(1);
		barProgressDialog.setMax(10);
		barProgressDialog.show();

		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					// time consuming task...
					while (barProgressDialog.getProgress() <= barProgressDialog.getMax()) {
						Thread.sleep(1000);
						updateBarHandler.post(new Runnable() {
							public void run() {
								barProgressDialog.incrementProgressBy(3);
							}
						});
						if (barProgressDialog.getProgress() == barProgressDialog.getMax()) {
							barProgressDialog.dismiss();
						}
					}
				} catch (Exception e) {
				}
			}
		}).start();
	}
}
