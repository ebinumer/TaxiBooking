package com.example.taxibooking.data.model;

public class Driver {

    public String username;
    public String phone;
    public String latitude;
    public String longitude;
    public String customer_name;

    public Driver() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public Driver(String username, String phone, String latitude, String longitude) {
        this.username = username;
        this.phone = phone;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Driver(String username, String phone, String latitude, String longitude, String customer_name) {
        this.username = username;
        this.phone = phone;
        this.latitude = latitude;
        this.longitude = longitude;
        this.customer_name = customer_name;
    }

    public Driver(String latitude, String longitude, String customer_name) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.customer_name = customer_name;
    }

    @Override
    public String toString() {
        return "Driver{" +
                "username='" + username + '\'' +
                ", phone='" + phone + '\'' +
                ", latitude='" + latitude + '\'' +
                ", longitude='" + longitude + '\'' +
                ", customer_name='" + customer_name + '\'' +
                '}';
    }
}
