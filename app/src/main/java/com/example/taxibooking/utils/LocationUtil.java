package com.example.taxibooking.utils;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.List;

public class LocationUtil {
    Geocoder geocoder;

    public LocationUtil(Context context) {
        geocoder = new Geocoder(context);
    }

    public LatLng getLatLongFromLocation(String location) {
        List<Address> locationList = null;
        LatLng latLng = null;
        try {
            locationList = geocoder.getFromLocationName(location, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (locationList != null && !locationList.isEmpty()) {
            Address address = locationList.get(0);
            latLng = new LatLng(address.getLatitude(), address.getLongitude());
        }
        return latLng;
    }

    public Address getLocationFromLatLong(LatLng latLng) {
        List<Address> locationList = null;
        Address locationData = null;
        try {
            locationList = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (locationList != null && !locationList.isEmpty()) {
            locationData = locationList.get(0);
            Log.d("LatLongLocation", locationData.toString());
        }
        return locationData;
    }
}
