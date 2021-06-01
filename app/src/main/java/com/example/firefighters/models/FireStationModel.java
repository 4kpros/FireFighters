package com.example.firefighters.models;


import java.util.Date;

public class FireStationModel {
    private int fireStationId; //Primary key
    private float longitude;
    private float latitude;
    private Date sendDate;
    private Date updatedDate;

    public FireStationModel() {
    }

    public int getFireStationId() {
        return fireStationId;
    }

    public void setFireStationId(int fireStationId) {
        this.fireStationId = fireStationId;
    }

    public float getLongitude() {
        return longitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }

    public float getLatitude() {
        return latitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public Date getSendDate() {
        return sendDate;
    }

    public void setSendDate(Date sendDate) {
        this.sendDate = sendDate;
    }

    public Date getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(Date updatedDate) {
        this.updatedDate = updatedDate;
    }
}
