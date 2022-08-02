package com.example.taxibooking.ui.trip;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

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
import android.util.Log;
import android.view.View;

import com.example.taxibooking.BaseActivity;
import com.example.taxibooking.R;
import com.example.taxibooking.data.model.Driver;
import com.example.taxibooking.data.prefrence.SessionManager;
import com.example.taxibooking.databinding.ActivityOnTripBinding;
import com.example.taxibooking.databinding.ActivityTripListBinding;
import com.example.taxibooking.ui.auth.LoginActivity;
import com.example.taxibooking.ui.driver.TripListActivity;
import com.example.taxibooking.ui.home.HomeActivity;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.maps.android.SphericalUtil;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OnTripActivity extends BaseActivity implements OnMapReadyCallback {
    private ActivityOnTripBinding binding;
    private GoogleMap mMap;
    LocationUtil locationUtil;
    private Boolean locationPermissionGranted = false;
    private final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";
    private final float DEFAULT_ZOOM = 15.0f;
    private Location lastKnownLocation = null;
    private LatLng currentLatLng = null;
    Handler handler = new Handler(Looper.getMainLooper() /*UI thread*/);
    Runnable destinationRunnable;
    private Marker CustomerMarker;
    private Marker destinationMarker;
    Polyline polyline;
    double distance, fare;
    private FusedLocationProviderClient fusedLocationProviderClient;
    LocationManager manager;
    boolean statusOfGPS;
    private SessionManager sessionManager;
    LatLng customerLocation;
    LatLng customerDestination;
    private FirebaseFirestore fb;
    private DatabaseReference mDatabase = getDatabaseReferenceInstance();
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOnTripBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        sessionManager = new SessionManager(OnTripActivity.this);
        locationUtil = new LocationUtil(OnTripActivity.this);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_small);
        mapFragment.getMapAsync(this);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        getLocationPermission();
        getDeviceLocation();
        fb = getFireStoreInstance();

        manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        statusOfGPS = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
         reference = mDatabase.child("driver");
        customerLocation = new LatLng(Double.parseDouble(sessionManager.getMyLat()), Double.parseDouble(sessionManager.getMyLang()));
        customerDestination = new LatLng(Double.parseDouble(sessionManager.getDestinationLat()), Double.parseDouble(sessionManager.getDestinationLang()));
        locationUtil.getLocationFromLatLong(customerLocation);
        Address loc = locationUtil.getLocationFromLatLong(customerLocation);
        binding.tvCstmrLocation.setText(loc.getAddressLine(0));
        Address des = locationUtil.getLocationFromLatLong(customerDestination);
        binding.tvDestination.setText(des.getAddressLine(0));
        if (sessionManager.getDriverStatus().equals("Started")) {
            binding.startLayout.setVisibility(View.GONE);
            binding.endLayout.setVisibility(View.VISIBLE);
        }
        else{
            binding.startLayout.setVisibility(View.VISIBLE);
            binding.endLayout.setVisibility(View.GONE);
        }
        binding.btnCustomerl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDeviceLocation();
                Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                        Uri.parse("http://maps.google.com/maps?saddr=" + currentLatLng.latitude + "," + currentLatLng.longitude +
                                "&daddr=" + sessionManager.getMyLat() + "," + sessionManager.getMyLang()));
                startActivity(intent);
            }
        });

        binding.destinationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDeviceLocation();
                Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                        Uri.parse("http://maps.google.com/maps?saddr=" + currentLatLng.latitude + "," + currentLatLng.longitude +
                                "&daddr=" + sessionManager.getDestinationLat() + "," + sessionManager.getDestinationLang()));
                startActivity(intent);
            }
        });

        binding.strtTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fb.collection("Trip")
                        .document(sessionManager.getOrderId())
                        .update("order_status", "Started")
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                sessionManager.setDriverStatus("Started");
                                binding.startLayout.setVisibility(View.GONE);
                                binding.endLayout.setVisibility(View.VISIBLE);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {


                            }
                        });
            }
        });

        binding.endTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fb.collection("Trip")
                        .document(sessionManager.getOrderId())
                        .update("order_status", "Completed")
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                sessionManager.setDriverStatus("no_selected");
                                Intent intent = new Intent(OnTripActivity.this, TripListActivity.class);
                                startActivity(intent);
                                finishAffinity();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {


                            }
                        });
            }
        });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(
                this, R.raw.style_maps
        ));

        getLocationPermission();
        drawPolyLinePath(mMap, customerLocation, customerDestination);

    }

    /**
     * Prompts the user for permission to use the device location.
     */
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

        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    private void drawPolyLinePath(GoogleMap map, LatLng currentLocation, LatLng destination) {

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
        CustomerMarker = mMap.addMarker(new MarkerOptions().position(currentLocation).title("Customer").draggable(true));
        destinationMarker = mMap.addMarker(new MarkerOptions().position(destination).title("destination").draggable(true));
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 14));
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
                               /* Driver driverData = new Driver();
                                driverData.setLatitude(String.valueOf(lastKnownLocation.getLatitude()));
                                driverData.setLongitude(String.valueOf(lastKnownLocation.getLongitude()));*/
                                Map<String,Object> driverMap = new HashMap<>();
                                driverMap.put("latitude",String.valueOf(lastKnownLocation.getLatitude()));
                                driverMap.put("longitude",String.valueOf(lastKnownLocation.getLongitude()));
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
                        } else {

                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage(), e);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        statusOfGPS = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }
}