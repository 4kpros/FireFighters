package com.example.firefighters.models;

import java.util.Calendar;

public class WaterSourceModel {
    private String senderId;   //Foreign key user sender

    private int estimatedQte;
    private int sendUtc;
    private float longitude;
    private float latitude;
    private String sourceType;
    private String sendDate;
    private String sendHour;

    public WaterSourceModel() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH) + 1;
        int utc = calendar.get(Calendar.ZONE_OFFSET);
        int hours = calendar.get(Calendar.HOUR_OF_DAY);
        int minutes = calendar.get(Calendar.MINUTE);
        int sec = calendar.get(Calendar.SECOND);
        sendDate = day + "/" + month + "/" + year;
        sendHour = hours + ":" + minutes + ":" + sec;
        sendUtc = utc;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public int getEstimatedQte() {
        return estimatedQte;
    }

    public void setEstimatedQte(int estimatedQte) {
        this.estimatedQte = estimatedQte;
    }

    public int getSendUtc() {
        return sendUtc;
    }

    public void setSendUtc(int sendUtc) {
        this.sendUtc = sendUtc;
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

    public String getSourceType() {
        return sourceType;
    }

    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }

    public String getSendDate() {
        return sendDate;
    }

    public void setSendDate(String sendDate) {
        this.sendDate = sendDate;
    }

    public String getSendHour() {
        return sendHour;
    }

    public void setSendHour(String sendHour) {
        this.sendHour = sendHour;
    }
}
