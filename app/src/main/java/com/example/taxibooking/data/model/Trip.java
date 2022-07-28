package com.example.taxibooking.data.model;

public class Trip {

    public String username;
    public String phone;
    public String amount;
    public String pickUp;
    public String destination;

    Trip() {}

    public Trip(String username, String phone, String amount, String pickUp, String destination) {
        this.username = username;
        this.phone = phone;
        this.amount = amount;
        this.pickUp = pickUp;
        this.destination = destination;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getPickUp() {
        return pickUp;
    }

    public void setPickUp(String pickUp) {
        this.pickUp = pickUp;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    @Override
    public String toString() {
        return "Trip{" +
                "username='" + username + '\'' +
                ", phone='" + phone + '\'' +
                ", amount='" + amount + '\'' +
                ", pickUp='" + pickUp + '\'' +
                ", destination='" + destination + '\'' +
                '}';
    }
}
