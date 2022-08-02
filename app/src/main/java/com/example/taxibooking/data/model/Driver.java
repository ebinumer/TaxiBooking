package com.example.taxibooking.data.model;

public class Driver {

    public String username;
    public String phone;
    public String latitude;
    public String longitude;
    public String customer_name;
    public String order_id;

    public Driver() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public Driver(String username, String phone, String latitude, String longitude) {
        this.username = username;
        this.phone = phone;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Driver(String username, String phone, String latitude, String longitude, String customer_name,String order_id) {
        this.username = username;
        this.phone = phone;
        this.latitude = latitude;
        this.longitude = longitude;
        this.customer_name = customer_name;
        this.order_id = order_id;
    }

    public Driver(String latitude, String longitude, String customer_name) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.customer_name = customer_name;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    @Override
    public String toString() {
        return "Driver{" +
                "username='" + username + '\'' +
                ", phone='" + phone + '\'' +
                ", latitude='" + latitude + '\'' +
                ", longitude='" + longitude + '\'' +
                ", customer_name='" + customer_name + '\'' +
                ", order_id='" + order_id + '\'' +
                '}';
    }
}
