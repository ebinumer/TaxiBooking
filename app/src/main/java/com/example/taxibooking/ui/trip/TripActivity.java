package com.example.taxibooking.ui.trip;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;

import com.example.taxibooking.BaseActivity;
import com.example.taxibooking.R;
import com.example.taxibooking.data.model.Driver;
import com.example.taxibooking.data.prefrence.SessionManager;
import com.example.taxibooking.databinding.ActivityTripBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class TripActivity extends BaseActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    private ActivityTripBinding binding;
    private DatabaseReference mDatabase = getDatabaseReferenceInstance();
    private String TAG = "TripActivity";
    private Marker carMarker;
    private SessionManager sessionManager;
    private final float DEFAULT_ZOOM = 15.0f;
    private FusedLocationProviderClient fusedLocationProviderClient;
    ProgressDialog progressDoalog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTripBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        sessionManager = new SessionManager(TripActivity.this);
        initView();
        setupListener();

    }

    private void setupListener() {
        DatabaseReference reference = mDatabase.child("driver");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Driver driver = snapshot.child("driver1").getValue(Driver.class);
                Log.d(TAG, "onDataChange:" + driver.toString());
                observeMarker(driver);
                setupDriverDetails(driver);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "onCancelled: " + error.getMessage());
            }
        });
    }

    private void setupDriverDetails(Driver driver) {

        binding.driverSheet.tvDriverName.setText(driver.username);
    }

    private void observeMarker(Driver driver) {
Log.e("u id","= "+sessionManager.getOrderId());
        if(!Objects.equals(driver.order_id, sessionManager.getOrderId())){

            progressDoalog = new ProgressDialog(TripActivity.this);
            progressDoalog.setMax(100);
            progressDoalog.setMessage("Waiting for Driver....");
            progressDoalog.setTitle("Waiting for Driver to pick up this order...");
            progressDoalog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDoalog.show();

            binding.textView4.setText("Waiting for a driver....");
            binding.myLocationButton.setVisibility(View.GONE);
            binding.driverSheet.mainDriverDetail.setVisibility(View.GONE);
            mMap.clear();

            getDeviceLocation();
        } else{
            if (progressDoalog != null) {
                progressDoalog.cancel();
            }
            binding.textView4.setText("Your ride is on the way");
            binding.myLocationButton.setVisibility(View.VISIBLE);
            binding.driverSheet.mainDriverDetail.setVisibility(View.VISIBLE);
        if (carMarker != null)
            carMarker.remove();

        LatLng carLatLng = new LatLng(Double.valueOf(driver.latitude), Double.valueOf(driver.longitude));
        carMarker = mMap.addMarker(new MarkerOptions().position(carLatLng).draggable(false).icon(BitmapDescriptorFactory.fromBitmap(mapBitmapIcon())));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(carLatLng, DEFAULT_ZOOM));
    }
    }

    private void initView() {
        binding.textView4.setText("Waiting for a driver....");
        binding.myLocationButton.setVisibility(View.GONE);
        binding.driverSheet.mainDriverDetail.setVisibility(View.GONE);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        binding.myLocationButton.setOnClickListener((view) -> {
            getDeviceLocation();
        });
        binding.driverSheet.callBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:000000000"));
            }});

    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        updateLocationUI();

        getDeviceLocation();

        googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(
                this, R.raw.style_maps
        ));

    }

    /**
     * Updates the map's UI settings based on whether the user has granted location permission.
     */
    @SuppressLint("MissingPermission")
    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }
        try {
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
            mMap.getUiSettings().setCompassEnabled(false);
            mMap.setMyLocationEnabled(true);
            getDeviceLocation();
        } catch (
                Exception e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    @SuppressLint("MissingPermission")
    private void getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            Task<Location> locationResult = fusedLocationProviderClient.getLastLocation();
            locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    if (task.isSuccessful()) {
                        // Set the map's camera position to the current location of the device.
                        Location lastKnownLocation = task.getResult();
                        LatLng currentLatLng = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
                        if (lastKnownLocation != null) {
                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                                    currentLatLng, 15.0f));
                        }
                    }
                }
            });
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage(), e);
        }
    }

    private Bitmap mapBitmapIcon() {
        int height = 80;
        int width = 80;
        BitmapDrawable bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.drawable.car);
        Bitmap b = bitmapdraw.getBitmap();
        Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);
        return smallMarker;
    }
}