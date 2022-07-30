package com.example.taxibooking.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.example.taxibooking.R;
import com.example.taxibooking.data.prefrence.SessionManager;
import com.example.taxibooking.ui.auth.LoginActivity;
import com.example.taxibooking.ui.driver.TripListActivity;
import com.example.taxibooking.ui.home.HomeActivity;
import com.example.taxibooking.ui.trip.OnTripActivity;
import com.example.taxibooking.ui.trip.TripActivity;

import java.util.Objects;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_SCREEN_TIME_OUT = 2000;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        sessionManager = new SessionManager(SplashActivity.this);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                if (sessionManager.isLoggedin()) {
                    if (sessionManager.getIsDriver()){
                        if (Objects.equals(sessionManager.getDriverStatus(), "no_selected")){
                            Intent i;
                            i = new Intent(SplashActivity.this,
                                    TripListActivity.class);
                            startActivity(i);
                        }
                        else {
                            Intent i;
                            i = new Intent(SplashActivity.this,
                                    OnTripActivity.class);
                            startActivity(i);
                        }
                    }
                    else{
                    if(sessionManager.getReqTrip()) {
                        Intent i;
                        i = new Intent(SplashActivity.this,
                                TripActivity.class);
                        startActivity(i);
                    }
                    else {
                        Intent i;
                        i = new Intent(SplashActivity.this,
                                HomeActivity.class);
                        startActivity(i);
                    }}
                }
                else {
                    Intent i = new Intent(SplashActivity.this,
                            LoginActivity.class);
                    startActivity(i);
                    //the current activity will get finished.
                }
                finishAffinity();
            }
        }, SPLASH_SCREEN_TIME_OUT);
    }
}