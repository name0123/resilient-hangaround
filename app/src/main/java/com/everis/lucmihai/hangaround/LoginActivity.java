package com.everis.lucmihai.hangaround;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import butterknife.OnClick;

public class LoginActivity extends Activity {
    private static final String TAG = "LoginActivity";
    private TextView info;
    private LoginButton loginButton;
    private CallbackManager callbackManager;

	public Context getContext() {
		return context;
	}

	public void setContext(Context context) {
		this.context = context;
	}

	private Context context;
	private ProfileTracker fbProfile;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getApplicationContext();
	    checkExternalDependencies(this);
        FacebookSdk.sdkInitialize(this);
        callbackManager = CallbackManager.Factory.create();
        setContentView(R.layout.activity_login);
        info = (TextView)findViewById(R.id.info);
	    Button blogin = (Button) findViewById(R.id.bcontinue);

		if(Profile.getCurrentProfile() != null)   blogin.setText("Continue as: "+Profile.getCurrentProfile().getName());
	    else blogin.setText("Continue as: GUEST");
	    //Log.v("facebook - profile", profile.getFirstName());
        loginButton = (LoginButton)findViewById(R.id.login_button);
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Intent intent = new Intent(context, MapsActivity.class);

	            if(Profile.getCurrentProfile() == null) {
		            fbProfile = new ProfileTracker() {
			            @Override
			            protected void onCurrentProfileChanged(Profile profile, Profile profile2) {
				            // profile2 is the new profile
				            //Log.v("facebook - profile", profile2.getFirstName());
				            Button blogin = (Button) findViewById(R.id.bcontinue);
				            blogin.setText("Continue as: "+profile2.getName());
				            fbProfile.stopTracking();
			            }
		            };
	            }
                intent.putExtra("logged", Profile.getCurrentProfile().getName());
                startActivity(intent);
            }

            @Override
            public void onCancel() {

                info.setText("Login attempt canceled: Guest session initialized");
                Intent intent = new Intent(context, MapsActivity.class);
                String user = "guest";
	            Button blogin = (Button) findViewById(R.id.bcontinue);
	            blogin.setText("Continue as: "+user);
                intent.putExtra("logged", user);
                startActivity(intent);
            }

            @Override
            public void onError(FacebookException e) {
                info.setText("Login attempt canceled: Guest session initialized");
                Intent intent = new Intent(context, MapsActivity.class);
                String user = "guest";
	            Button blogin = (Button) findViewById(R.id.bcontinue);
	            blogin.setText("Continue as: "+user);
                intent.putExtra("logged", user);
                startActivity(intent);
            }
        });
    }
/*
	checkExternalDependencies maintain one or more threads
	- internet signal
	- gps signal
	- database a
	- facebook
	- google
	- foursquare
	every

 */
	private void checkExternalDependencies(Activity a) {
		// this method has an aspect
		Log.e("This is old: ", "Are we done with the aspect?");
	}

	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @OnClick(R.id.bcontinue)
    public void onClickContinue(View view){
		Profile fb = Profile.getCurrentProfile();
	    Intent intent = new Intent(this, MapsActivity.class);
	    if(fb != null){
		    intent.putExtra("logged", fb.getName());
	    }
	    else{
		    // TODO: arregla el numero!
 		    intent.putExtra("logged","GUEST");
	    }

        startActivity(intent);
    }
/*    public boolean isLoggedIn() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        return accessToken != null;
    }*/
}
