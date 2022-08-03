package com.example.taxibooking.ui.trip;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import com.example.taxibooking.R;
import com.example.taxibooking.data.prefrence.SessionManager;
import com.example.taxibooking.databinding.ActivityTripCompleteBinding;
import com.example.taxibooking.ui.SplashActivity;
import com.example.taxibooking.ui.driver.TripListActivity;
import com.example.taxibooking.ui.home.HomeActivity;

public class TripCompleteActivity extends AppCompatActivity {
    private ActivityTripCompleteBinding binding;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTripCompleteBinding.inflate(getLayoutInflater());
        sessionManager = new SessionManager(TripCompleteActivity.this);
        setContentView(binding.getRoot());
        showLoadingLottie();
    }

    private void showLoadingLottie() {
      //  binding.lottieView.setAnimation("trip_success.json");
        binding.lottieView.setAnimation("complete.json");
        binding.lottieView.loop(true);
        binding.lottieView.playAnimation();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (sessionManager.getIsDriver()){
                    Intent i;
                    i = new Intent(TripCompleteActivity.this,
                            TripListActivity.class);
                    startActivity(i);
                }
                else{
                    Intent i;
                    i = new Intent(TripCompleteActivity.this,
                            HomeActivity.class);
                    startActivity(i);
                }
            }
        }, 3000);
    }
}