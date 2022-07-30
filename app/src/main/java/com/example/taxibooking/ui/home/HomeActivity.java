package com.example.taxibooking.ui.home;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.PersistableBundle;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.example.taxibooking.R;
import com.example.taxibooking.data.prefrence.SessionManager;
import com.example.taxibooking.databinding.ActivityHomeMapBinding;
import com.example.taxibooking.ui.SplashActivity;
import com.example.taxibooking.ui.auth.LoginActivity;
import com.example.taxibooking.ui.profile.ProfileActivity;
import com.example.taxibooking.utils.LocationUtil;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Dash;
import com.google.android.gms.maps.model.Gap;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PatternItem;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.maps.android.SphericalUtil;

import java.util.Arrays;
import java.util.List;

public class HomeActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    LocationUtil locationUtil;
    private ActivityHomeMapBinding binding;
    private Boolean locationPermissionGranted = false;
    private final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";
    private final float DEFAULT_ZOOM = 15.0f;
    private Location lastKnownLocation = null;
    private LatLng currentLatLng = null;
    Handler handler = new Handler(Looper.getMainLooper() /*UI thread*/);
    Runnable destinationRunnable;
    private Marker destinationMarker;
    Polyline polyline;
    double distance, fare;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private static final String TAG = "Home Activity";
    private final LatLng defaultLocation = new LatLng(-33.8523341, 151.2106085);
    LocationManager manager;
    boolean statusOfGPS;
    private SessionManager sessionManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            lastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
        }
        sessionManager = new SessionManager(HomeActivity.this);
        binding = ActivityHomeMapBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initView();
    }

    private void initView() {
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        locationUtil = new LocationUtil(HomeActivity.this);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        statusOfGPS = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        destinationRunnable = () -> {
            setUpLocationSearch(binding.searchLayout.etDestination.getText().toString());
        };

        binding.myLocationButton.setOnClickListener((view) -> {
            getDeviceLocation();
        });

        binding.searchLayout.etDestination.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                handler.removeCallbacks(destinationRunnable);
                handler.postDelayed(destinationRunnable, 1000);
            }
        });

        binding.btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showConfirmDialog();
            }
        });

        binding.searchLayout.ivDrawer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.drawer.openDrawer(Gravity.LEFT);
            }
        });

        binding.navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                switch (id) {

                    case R.id.profile_item: {
                        Intent intent = new Intent(HomeActivity.this, ProfileActivity.class);
                        binding.drawer.closeDrawer(Gravity.LEFT);
                        startActivity(intent);
                        break;
                    }

                    case R.id.signout_item: {
                        sessionManager.clear();
                        Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
                        binding.drawer.closeDrawer(Gravity.LEFT);
                        startActivity(intent);
                        finishAffinity();
                        break;
                    }


                    }
                    return true;
                }
            });


        }

        private void setUpLocationSearch (String location){
            LatLng latLng = locationUtil.getLatLongFromLocation(location);
            if (latLng != null) {
                if (destinationMarker != null)
                    destinationMarker.remove();
                destinationMarker = mMap.addMarker(new MarkerOptions().position(latLng).title(location).draggable(true));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_ZOOM));
                binding.btnConfirm.setVisibility(View.VISIBLE);
                drawPolyLinePath(mMap, currentLatLng, latLng);
            } else {
                mMap.clear();
                binding.btnConfirm.setVisibility(View.GONE);
            }
        }

        private void drawPolyLinePath (GoogleMap map, LatLng currentLocation, LatLng destination){
            sessionManager.setMyLat(String.valueOf(currentLocation.latitude));
            sessionManager.setMyLang(String.valueOf(currentLocation.longitude));
            sessionManager.setDestinationLat(String.valueOf(destination.latitude));
            sessionManager.setDestinationLang(String.valueOf(destination.longitude));
            if (polyline != null)
                polyline.remove();
            List<PatternItem> pattern = Arrays.asList(
                    new Gap(8), new Dash(20), new Gap(8));
            polyline = map.addPolyline(new PolylineOptions().add(currentLocation, destination)
                    .width(5)
                    .color(Color.BLACK)
                    .pattern(pattern)
                    .jointType(JointType.ROUND)
                    .geodesic(true));
            distance = SphericalUtil.computeDistanceBetween(currentLocation, destination);
            Log.d("Map Distance", String.valueOf(distance / 1000));

            // map.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 12));
        }

        @Override
        public void onMapReady (GoogleMap googleMap){
            mMap = googleMap;

            googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(
                    this, R.raw.style_maps
            ));

            getLocationPermission();

            updateLocationUI();

            getDeviceLocation();

            mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
                @Override
                public void onMarkerDrag(@NonNull Marker marker) {
                }

                @Override
                public void onMarkerDragEnd(@NonNull Marker marker) {
                    LatLng markerLocation = marker.getPosition();
                    Address location = locationUtil.getLocationFromLatLong(markerLocation);
                    binding.searchLayout.etDestination.setText(location.getAddressLine(0));
                    drawPolyLinePath(mMap, currentLatLng, markerLocation);
                }

                @Override
                public void onMarkerDragStart(@NonNull Marker marker) {
                }
            });
        }

        /**
         * Prompts the user for permission to use the device location.
         */
        private void getLocationPermission () {
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

            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                        PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            }
        }

        /**
         * Updates the map's UI settings based on whether the user has granted location permission.
         */
        @SuppressLint("MissingPermission")
        private void updateLocationUI () {
            if (mMap == null) {
                return;
            }
            try {
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                mMap.getUiSettings().setCompassEnabled(false);
                if (locationPermissionGranted) {
                    mMap.setMyLocationEnabled(true);
                    // mMap.getUiSettings().setMyLocationButtonEnabled(true);
                    binding.myLocationButton.setVisibility(View.VISIBLE);
                    getDeviceLocation();
                } else {
                    mMap.setMyLocationEnabled(false);
                    //  mMap.getUiSettings().setMyLocationButtonEnabled(false);
                    binding.myLocationButton.setVisibility(View.GONE);
                    lastKnownLocation = null;
                    getLocationPermission();
                }
            } catch (Exception e) {
                Log.e("Exception: %s", e.getMessage());
            }
        }

        /**
         * Gets the current location of the device, and positions the map's camera.
         */
        @SuppressLint("MissingPermission")
        private void getDeviceLocation () {
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
                                lastKnownLocation = task.getResult();
//                            if(lastKnownLocation.getLatitude() != null)
                                Log.e("location", "" + statusOfGPS);
                                if (statusOfGPS) {
                                    currentLatLng = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
                                    sessionManager.setMyLat(String.valueOf(lastKnownLocation.getLatitude()));
                                    sessionManager.setMyLang(String.valueOf(lastKnownLocation.getLongitude()));
                                } else {
                                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                    startActivity(intent);
                                }

                                if (lastKnownLocation != null) {
                                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                                            currentLatLng, DEFAULT_ZOOM));
                                    setupPickupLocation(currentLatLng);
                                }
                            } else {
                                Log.d(TAG, "Current location is null. Using defaults.");
                                Log.e(TAG, "Exception: %s", task.getException());
                                mMap.moveCamera(CameraUpdateFactory
                                        .newLatLngZoom(defaultLocation, DEFAULT_ZOOM));
                                //  mMap.getUiSettings().setMyLocationButtonEnabled(false);
                            }
                        }
                    });
                }
            } catch (SecurityException e) {
                Log.e("Exception: %s", e.getMessage(), e);
            }
        }

        private void setupPickupLocation (LatLng lastLatLong){
            Address locationAddress = locationUtil.getLocationFromLatLong(lastLatLong);
            binding.searchLayout.etLocation.setText(locationAddress.getAddressLine(0));
        }

        @Override
        public void onSaveInstanceState (@NonNull Bundle outState, @NonNull PersistableBundle
        outPersistentState){
            if (mMap != null) {
                outState.putParcelable(KEY_CAMERA_POSITION, mMap.getCameraPosition());
                outState.putParcelable(KEY_LOCATION, lastKnownLocation);
            }
            super.onSaveInstanceState(outState, outPersistentState);
        }

        @Override
        public void onRequestPermissionsResult ( int requestCode, @NonNull String[] permissions,
        @NonNull int[] grantResults){
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            locationPermissionGranted = false;
            if (requestCode == PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION) {// If request is cancelled, the result arrays are empty.
                if (grantResults != null &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                        grantResults[1] == PackageManager.PERMISSION_GRANTED
                ) {
                    locationPermissionGranted = true;
                }
            }
            updateLocationUI();
        }

        private void showConfirmDialog () {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                    currentLatLng, 12.0f));
            BottomDialogFragment bottomSheetDialog = new BottomDialogFragment(distance);
            bottomSheetDialog.show(getSupportFragmentManager(), "Bottom Sheet Dialog Fragment");
        }

        @Override
        protected void onResume () {
            super.onResume();
            manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            statusOfGPS = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            if (!statusOfGPS) {

                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        }
    }