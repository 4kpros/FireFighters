package com.example.firefighters.models;

import java.util.Date;

public class WaterSourceModel {
    private int waterSourceId; //Primary key
    private int estimatedQte;
    private String type;

    private float longitude;
    private float latitude;

    private int senderId;
    private int verifiedId;
    private int signaledId;
    private Date sendDate;
    private Date updatedDate;
    private Date verifiedDate;
    private Date signaledDate;

    public WaterSourceModel() {
    }

    public int getWaterSourceId() {
        return waterSourceId;
    }

    public void setWaterSourceId(int waterSourceId) {
        this.waterSourceId = waterSourceId;
    }

    public int getEstimatedQte() {
        return estimatedQte;
    }

    public void setEstimatedQte(int estimatedQte) {
        this.estimatedQte = estimatedQte;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public int getSenderId() {
        return senderId;
    }

    public void setSenderId(int senderId) {
        this.senderId = senderId;
    }

    public int getVerifiedId() {
        return verifiedId;
    }

    public void setVerifiedId(int verifiedId) {
        this.verifiedId = verifiedId;
    }

    public int getSignaledId() {
        return signaledId;
    }

    public void setSignaledId(int signaledId) {
        this.signaledId = signaledId;
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

    public Date getVerifiedDate() {
        return verifiedDate;
    }

    public void setVerifiedDate(Date verifiedDate) {
        this.verifiedDate = verifiedDate;
    }

    public Date getSignaledDate() {
        return signaledDate;
    }

    public void setSignaledDate(Date signaledDate) {
        this.signaledDate = signaledDate;
    }
}
