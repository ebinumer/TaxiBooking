package com.example.taxibooking.ui.trip;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.taxibooking.BaseActivity;
import com.example.taxibooking.data.prefrence.SessionManager;
import com.example.taxibooking.databinding.ActivityTripCompleteBinding;
import com.example.taxibooking.ui.driver.TripListActivity;
import com.example.taxibooking.ui.home.HomeActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;

import java.util.HashMap;
import java.util.Map;

public class TripCompleteActivity extends BaseActivity {
    private ActivityTripCompleteBinding binding;
    private SessionManager sessionManager;
    private DatabaseReference mDatabase = getDatabaseReferenceInstance();
    DatabaseReference reference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTripCompleteBinding.inflate(getLayoutInflater());
        sessionManager = new SessionManager(TripCompleteActivity.this);
        setContentView(binding.getRoot());
        reference = mDatabase.child("driver");
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
                if (sessionManager.getIsDriver()) {
                    updateToDb();
                    Intent i;
                    i = new Intent(TripCompleteActivity.this,
                            TripListActivity.class);
                    startActivity(i);
                } else {
                    sessionManager.setReqTrip(false);
                    Intent i;
                    i = new Intent(TripCompleteActivity.this,
                            HomeActivity.class);
                    startActivity(i);
                }
                finishAffinity();
            }
        }, 3000);
    }

    private void updateToDb() {
        Map<String, Object> driverMap = new HashMap<>();
        driverMap.put("latitude", "0.0");
        driverMap.put("longitude", "0.0");
        driverMap.put("order_id", "");
        driverMap.put("customer_name", sessionManager.getUserName());
        driverMap.put("user_name", "driver");
        driverMap.put("trip_status", "waiting");
        driverMap.put("phone", "123456");
        reference.child("driver1").updateChildren(driverMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }
}