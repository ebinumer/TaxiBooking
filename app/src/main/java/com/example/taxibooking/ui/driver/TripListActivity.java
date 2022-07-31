package com.example.taxibooking.ui.driver;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Location;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.Gravity;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.taxibooking.BaseActivity;
import com.example.taxibooking.R;
import com.example.taxibooking.adapter.TripAdapter;
import com.example.taxibooking.data.model.Driver;
import com.example.taxibooking.data.model.Trip;
import com.example.taxibooking.data.prefrence.SessionManager;
import com.example.taxibooking.databinding.ActivityTripListBinding;
import com.example.taxibooking.ui.auth.LoginActivity;
import com.example.taxibooking.ui.home.HomeActivity;
import com.example.taxibooking.ui.trip.OnTripActivity;
import com.example.taxibooking.utils.LocationUtil;
import com.example.taxibooking.utils.NetworkManager;
import com.example.taxibooking.utils.OnItemClickListener;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class TripListActivity extends BaseActivity implements OnItemClickListener {
    private ActivityTripListBinding binding;
    private long mLastClickTime = 0;
    private FirebaseFirestore fb;
    private DatabaseReference mDatabase = getDatabaseReferenceInstance();
    private LatLng currentLatLng;
    LocationUtil locationUtil;
    private TripAdapter adapter;
    private static final String TAG = "TripListActivity";
    private Boolean locationPermissionGranted = false;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private final ArrayList<Trip> tripList = new ArrayList<>();
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTripListBinding.inflate(getLayoutInflater());
        locationUtil = new LocationUtil(this);
        setContentView(binding.getRoot());
        sessionManager = new SessionManager(TripListActivity.this);
        initView();
    }

    private void initView() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        fb = getFireStoreInstance();
        getLocationPermission();
        setupRestaurantsRecyclerView();
        binding.driverLogout.setOnClickListener((view) -> {
            sessionManager.clear();
            Intent intent = new Intent(TripListActivity.this, LoginActivity.class);
            startActivity(intent);
            finishAffinity();
        });
    }

    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_COARSE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED

        ) {
            locationPermissionGranted = true;
            getDeviceLocation();
            getTrips();

        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        locationPermissionGranted = false;
        if (requestCode == PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION) {// If request is cancelled, the result arrays are empty.
            if (grantResults != null &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[1] == PackageManager.PERMISSION_GRANTED
            ) {
                locationPermissionGranted = true;
                getDeviceLocation();
                getTrips();
            }
        }
    }

    /**
     * Gets the current location of the device, and positions the map's camera.
     */
    @SuppressLint("MissingPermission")
    private void getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (locationPermissionGranted) {
                Task<Location> locationResult = fusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            // Set the map's camera position to the current location of the device.
                            Location lastKnownLocation = task.getResult();
                            if (lastKnownLocation != null) {
                                currentLatLng = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
                                Log.d(TAG, "onComplete: " + currentLatLng);
                            }
                        } else {
                            Log.d(TAG, "Current location is null. Using defaults.");
                            Log.e(TAG, "Exception: %s", task.getException());
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage(), e);
        }
    }

    private void setupRestaurantsRecyclerView() {
        adapter = new TripAdapter(this, tripList, this);
        binding.rvTrips.setLayoutManager(new LinearLayoutManager(this));
        binding.rvTrips.setHasFixedSize(true);
        binding.rvTrips.setAdapter(adapter);
    }

    private void getTrips() {
        showLoading(this);
        if (NetworkManager.isNetworkAvailable(TripListActivity.this)) {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
            }
            mLastClickTime = SystemClock.elapsedRealtime();
            fb.collection("Trip")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                tripList.clear();
                                for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {

                                    Address pickUp = locationUtil.getLocationFromLatLong(
                                            new LatLng(
                                                    Double.valueOf(documentSnapshot.get("current_lat").toString()),
                                                    Double.valueOf(documentSnapshot.get("current_long").toString())));
                                    Address dest = locationUtil.getLocationFromLatLong(
                                            new LatLng(
                                                    Double.valueOf(documentSnapshot.get("destination_lat").toString()),
                                                    Double.valueOf(documentSnapshot.get("destination_long").toString())));

                                    if(!documentSnapshot.get("status").toString().equals("Completed")){
                                    tripList.add(
                                            new Trip(
                                                    documentSnapshot.getId(),
                                                    documentSnapshot.get("username").toString(),
                                                    documentSnapshot.get("mobile").toString(),
                                                    "100", pickUp.getLocality(),documentSnapshot.get("current_lat").toString(),documentSnapshot.get("current_long").toString(),
                                                    dest.getLocality(),documentSnapshot.get("destination_lat").toString(),documentSnapshot.get("destination_long").toString()
                                            ));

                                }}
                                hideLoading();
                                if (tripList.size() > 0) {
                                    binding.rvTrips.setVisibility(View.VISIBLE);
                                    adapter.notifyDataSetChanged();
                                } else {
                                    binding.rvTrips.setVisibility(View.GONE);
                                    showToast(TripListActivity.this, getString(R.string.no_order));
                                }
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            hideLoading();
                            showToast(TripListActivity.this, e.getMessage());
                        }
                    });
        } else {
            hideLoading();
        }
    }

    @Override
    public void onItemClick(Integer position) {
        sessionManager.setMyLat(tripList.get(position).getPickUpLat());
        sessionManager.setMyLang(tripList.get(position).getPickUpLong());

        sessionManager.setDestinationLat(tripList.get(position).getDestinationLat());
        sessionManager.setDestinationLang(tripList.get(position).getDestinationLong());
        sessionManager.setOrderId(tripList.get(position).getId());

        Trip tripData = tripList.get(position);
        Log.d(TAG, tripData.toString());
        Driver updatedDriverData = new Driver(
                "Driver",
                "123456",
                String.valueOf(currentLatLng.latitude),
                String.valueOf(currentLatLng.longitude),
                tripData.getUsername()
        );
        Log.d(TAG, updatedDriverData.toString());
        updateDataToDb(updatedDriverData,position);



    }

    private void updateDataToDb(Driver driverData, Integer position) {

        DatabaseReference reference = mDatabase.child("driver");
        reference.child("driver1").setValue(driverData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        showToast(TripListActivity.this, "Trip Updated successfully");
                        Intent intent = new Intent(TripListActivity.this, OnTripActivity.class);
                        startActivity(intent);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, e.getMessage());
                        showToast(TripListActivity.this, "Trip Update failed");
                    }
                });

        fb.collection("Trip")
                .document(sessionManager.getOrderId())
                .update("status", "Picked")
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        hideLoading();
                        sessionManager.setDriverStatus("Picked");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        hideLoading();
                        Log.e("exception in request", String.valueOf(e));

                    }
                });
    }
}