package com.example.taxibooking.data.model;

public class Trip {

    public String id;
    public String username;
    public String phone;
    public String amount;
    public String pickUp;
    public String pickUpLat;
    public String pickUpLong;
    public String destination;
    public String destinationLat;
    public String destinationLong;

    Trip() {}

    public Trip(String id,String username, String phone, String amount, String pickUp,String pickUpLat,String pickUpLong, String destination,String destinationLat,String destinationLong) {
      this.id = id;
        this.username = username;
        this.phone = phone;
        this.amount = amount;
        this.pickUp = pickUp;
        this.pickUpLat =pickUpLat;
        this.pickUpLong = pickUpLong;
        this.destination = destination;
        this.destinationLat = destinationLat;
        this.destinationLong = destinationLong;
    }

    public String getId() {
        return id;
    }

    public void setId(String username) {
        this.id = id;
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

    public String getPickUpLat() {
        return pickUpLat;
    }

    public void setPickUpLat(String pickUp) {
        this.pickUpLat = pickUp;
    }

    public String getPickUpLong() {
        return pickUpLong;
    }

    public void setPickUpLong(String pickUp) {
        this.pickUpLong = pickUp;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getDestinationLat() {
        return destinationLat;
    }

    public void setDestinationLat(String destination) {
        this.destinationLat = destination;
    }

    public String getDestinationLong() {
        return destinationLong;
    }

    public void setDestinationLong(String destination) {
        this.destinationLong = destination;
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
