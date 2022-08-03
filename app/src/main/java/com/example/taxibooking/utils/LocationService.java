package com.example.taxibooking.utils;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.IBinder;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.example.taxibooking.R;
import com.example.taxibooking.data.prefrence.SessionManager;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class LocationService extends Service {
    private static final String CHANNEL_ID = "1";
    private FusedLocationProviderClient locationProviderClient;
    LocationRequest locationRequest;
    SessionManager sessionManager;
    private DatabaseReference mDatabase;
    DatabaseReference reference;

    public LocationService() {
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onCreate() {
        this.sessionManager = new SessionManager(this);
        this.mDatabase = FirebaseDatabase.getInstance().getReference();
        this.reference = mDatabase.child("driver");
        this.locationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(Priority.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(1500);
        runForeground();
        super.onCreate();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        runForeground();
        startLocationService();
        return START_STICKY;
    }

    private void startLocationService() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
        }
        locationProviderClient.requestLocationUpdates(locationRequest, new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    if (location != null) {
                        double wayLatitude = location.getLatitude();
                        double wayLongitude = location.getLongitude();
                        LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                        // showToast(OnTripActivity.this, "new lat and long" + wayLatitude + wayLongitude);
                        Map<String, Object> driverMap = new HashMap<>();
                        driverMap.put("latitude", String.valueOf(wayLatitude));
                        driverMap.put("longitude", String.valueOf(wayLongitude));
                        driverMap.put("order_id", sessionManager.getOrderId());
                        driverMap.put("customer_name", sessionManager.getUserName());
                        driverMap.put("user_name", "driver");
                        driverMap.put("trip_status", sessionManager.getTripStatus());
                        driverMap.put("phone", "123456");
                        reference.child("driver1").updateChildren(driverMap)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        // Toast.makeText(LocationService.this, "Success", Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        //  Toast.makeText(LocationService.this, "Failed", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                }
                super.onLocationResult(locationResult);
            }
        }, Looper.myLooper());
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onTaskRemoved(Intent rootIntent) {
        startLocationService();
        super.onTaskRemoved(rootIntent);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void runForeground() {
//... Pending intent if you want attach it to the notification
        NotificationChannel chan = new NotificationChannel(
                "100",
                "My Foreground Service",
                NotificationManager.IMPORTANCE_LOW);
        chan.setLightColor(Color.BLUE);
        chan.setLockscreenVisibility(Notification.VISIBILITY_SECRET);

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert manager != null;
        manager.createNotificationChannel(chan);

        Notification notification = new NotificationCompat.Builder(this, "100")
                .setSmallIcon(R.drawable.car_icon)
                .setContentText(getString(R.string.app_name))
                .build();

        startForeground(1, notification);

    }
}
