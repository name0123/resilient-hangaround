package com.everis.lucmihai.hangaround;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import butterknife.OnClick;

public class LoginActivity extends Activity {
    private static final String TAG = "LoginActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    @OnClick(R.id.bcontinue)
    public void onClickContinue(View view){
        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
    }


}
