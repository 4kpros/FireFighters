package com.example.firefighters.models;
import java.util.Date;

public class FireTruckModel {
    private int fireTruckId; //Primary key
    private int truckNo;
    private float waterQte;
    private String fireStationId;
    private Date sendDate;
    private Date updatedDate;

    public FireTruckModel() {
    }

    public int getFireTruckId() {
        return fireTruckId;
    }

    public void setFireTruckId(int fireTruckId) {
        this.fireTruckId = fireTruckId;
    }

    public int getTruckNo() {
        return truckNo;
    }

    public void setTruckNo(int truckNo) {
        this.truckNo = truckNo;
    }

    public float getWaterQte() {
        return waterQte;
    }

    public void setWaterQte(float waterQte) {
        this.waterQte = waterQte;
    }

    public String getFireStationId() {
        return fireStationId;
    }

    public void setFireStationId(String fireStationId) {
        this.fireStationId = fireStationId;
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
