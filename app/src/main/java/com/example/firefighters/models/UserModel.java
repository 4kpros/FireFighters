package com.example.firefighters.models;

public class UserModel {
    int phoneNumber;
    String mail;
    String userName;
    String firstName;
    String lastName;
    String picture;

    float latestLatitude;
    float latestLongitude;
    boolean isWorking;
    boolean isFireFighter;
    boolean isChief;
    String unit;

    public boolean isChief() {
        return isChief;
    }

    public void setChief(boolean chief) {
        isChief = chief;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public boolean isWorking() {
        return isWorking;
    }

    public void setWorking(boolean working) {
        isWorking = working;
    }

    public float getLatestLatitude() {
        return latestLatitude;
    }

    public void setLatestLatitude(float latestLatitude) {
        this.latestLatitude = latestLatitude;
    }

    public float getLatestLongitude() {
        return latestLongitude;
    }

    public void setLatestLongitude(float latestLongitude) {
        this.latestLongitude = latestLongitude;
    }

    public UserModel() {
        isFireFighter = false;
    }

    public boolean isFireFighter() {
        return isFireFighter;
    }

    public void setFireFighter(boolean fireFighter) {
        isFireFighter = fireFighter;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public int getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(int phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }
}
