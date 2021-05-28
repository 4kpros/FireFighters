package com.example.firefighters.models;

import java.util.Date;

public class HydrantModel {
    private int senderId;
    private float longitude;
    private float latitude;
    private Date sendDate;
    private Date updateDate;
    private Date finishWorkDate;
    private String sendHour;
    private String updateHour;
    private String finishWorkHour;
    private String gravity;
    private String city;
    private String state;

    public int getSenderId() {
        return senderId;
    }

    public void setSenderId(int senderId) {
        this.senderId = senderId;
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

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public Date getFinishWorkDate() {
        return finishWorkDate;
    }

    public void setFinishWorkDate(Date finishWorkDate) {
        this.finishWorkDate = finishWorkDate;
    }

    public String getSendHour() {
        return sendHour;
    }

    public void setSendHour(String sendHour) {
        this.sendHour = sendHour;
    }

    public String getUpdateHour() {
        return updateHour;
    }

    public void setUpdateHour(String updateHour) {
        this.updateHour = updateHour;
    }

    public String getFinishWorkHour() {
        return finishWorkHour;
    }

    public void setFinishWorkHour(String finishWorkHour) {
        this.finishWorkHour = finishWorkHour;
    }

    public String getGravity() {
        return gravity;
    }

    public void setGravity(String gravity) {
        this.gravity = gravity;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
