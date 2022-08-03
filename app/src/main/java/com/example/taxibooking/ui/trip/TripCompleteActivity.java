package com.example.taxibooking.ui.trip;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.example.taxibooking.R;
import com.example.taxibooking.databinding.ActivityTripCompleteBinding;

public class TripCompleteActivity extends AppCompatActivity {
    private ActivityTripCompleteBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTripCompleteBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        showLoadingLottie();
    }

    private void showLoadingLottie() {
      //  binding.lottieView.setAnimation("trip_success.json");
        binding.lottieView.setAnimation("complete.json");
        binding.lottieView.loop(true);
        binding.lottieView.playAnimation();
    }
}