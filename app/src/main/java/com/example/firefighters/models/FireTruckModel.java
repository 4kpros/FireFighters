package com.example.firefighters.models;

import java.util.Calendar;

public class FireTruckModel {
    private String id;

    private String fireStationId;  //Foreign key

    private int truckNo;
    private int sendUtc;
    private float waterQte;
    private String sendDate;
    private String sendHour;

    public FireTruckModel() {
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFireStationId() {
        return fireStationId;
    }

    public void setFireStationId(String fireStationId) {
        this.fireStationId = fireStationId;
    }

    public int getTruckNo() {
        return truckNo;
    }

    public void setTruckNo(int truckNo) {
        this.truckNo = truckNo;
    }

    public int getSendUtc() {
        return sendUtc;
    }

    public void setSendUtc(int sendUtc) {
        this.sendUtc = sendUtc;
    }

    public float getWaterQte() {
        return waterQte;
    }

    public void setWaterQte(float waterQte) {
        this.waterQte = waterQte;
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
