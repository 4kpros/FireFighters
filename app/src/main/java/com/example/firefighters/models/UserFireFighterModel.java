package com.example.firefighters.models;

public class UserFireFighterModel {
    int fireFighterId;
    String cIN;
    String matNo;
    String mail;
    String grade;
    String userName;
    String firstName;
    String lastName;
    int phoneNumber;
    String picture;

    public UserFireFighterModel() {
    }

    public int getFireFighterId() {
        return fireFighterId;
    }

    public void setFireFighterId(int fireFighterId) {
        this.fireFighterId = fireFighterId;
    }

    public String getcIN() {
        return cIN;
    }

    public void setcIN(String cIN) {
        this.cIN = cIN;
    }

    public String getMatNo() {
        return matNo;
    }

    public void setMatNo(String matNo) {
        this.matNo = matNo;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
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
