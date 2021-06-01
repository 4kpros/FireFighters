package com.example.firefighters.models;
import com.example.firefighters.tools.ConstantsValues;

import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;

public class EmergencyModel {
    private int emergencyId; //Primary key

    private float longitude;
    private float latitude;
    private String streetName;
    private String gravity;
    private String status;

    private boolean verified;
    private boolean signaled;
    private boolean paused;
    private boolean finished;
    private int senderId;
    private int verifiedId;
    private int signaledId;
    private int pausedId;
    private int finishedId;
    private DateTimeModel sendDate;
    private DateTimeModel updatedDate;
    private DateTimeModel verifiedDate;
    private DateTimeModel signaledDate;
    private DateTimeModel pausedDate;
    private DateTimeModel finishedDate;

    public EmergencyModel() {
        senderId = -1;
        streetName = "Street Name";
        longitude = 0;
        latitude = 0;
        gravity = ConstantsValues.GRAVITY_NORMAL;
        status = ConstantsValues.NOT_WORKING;
        verified = false;
        signaled = false;
        paused = false;
        finished = false;
        verifiedId = -1;
        signaledId = -1;
        pausedId = -1;
        finishedId = -1;
        sendDate = new DateTimeModel();
        updatedDate = null;
        verifiedDate = null;
        signaledDate = null;
        pausedDate = null;
        finishedDate = null;
    }

    public int getEmergencyId() {
        return emergencyId;
    }

    public String getStreetName() {
        return streetName;
    }

    public void setStreetName(String streetName) {
        this.streetName = streetName;
    }

    public void setEmergencyId(int emergencyId) {
        this.emergencyId = emergencyId;
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

    public String getGravity() {
        return gravity;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setGravity(String gravity) {
        this.gravity = gravity;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    public boolean isSignaled() {
        return signaled;
    }

    public void setSignaled(boolean signaled) {
        this.signaled = signaled;
    }

    public boolean isPaused() {
        return paused;
    }

    public void setPaused(boolean paused) {
        this.paused = paused;
    }

    public boolean isFinished() {
        return finished;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
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

    public int getPausedId() {
        return pausedId;
    }

    public void setPausedId(int pausedId) {
        this.pausedId = pausedId;
    }

    public int getFinishedId() {
        return finishedId;
    }

    public void setFinishedId(int finishedId) {
        this.finishedId = finishedId;
    }

    public DateTimeModel getSendDate() {
        return sendDate;
    }

    public void setSendDate(DateTimeModel sendDate) {
        this.sendDate = sendDate;
    }

    public DateTimeModel getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(DateTimeModel updatedDate) {
        this.updatedDate = updatedDate;
    }

    public DateTimeModel getVerifiedDate() {
        return verifiedDate;
    }

    public void setVerifiedDate(DateTimeModel verifiedDate) {
        this.verifiedDate = verifiedDate;
    }

    public DateTimeModel getSignaledDate() {
        return signaledDate;
    }

    public void setSignaledDate(DateTimeModel signaledDate) {
        this.signaledDate = signaledDate;
    }

    public DateTimeModel getPausedDate() {
        return pausedDate;
    }

    public void setPausedDate(DateTimeModel pausedDate) {
        this.pausedDate = pausedDate;
    }

    public DateTimeModel getFinishedDate() {
        return finishedDate;
    }

    public void setFinishedDate(DateTimeModel finishedDate) {
        this.finishedDate = finishedDate;
    }
}
