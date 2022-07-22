package com.example.taxibooking.data.model;

public class Driver {

    public String username;
    public String phone;
    public String latitude;
    public String longitude;

    public Driver() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public Driver(String username, String phone, String latitude, String longitude) {
        this.username = username;
        this.phone = phone;
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
