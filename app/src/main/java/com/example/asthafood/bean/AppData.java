package com.example.asthafood.bean;

public class AppData {

    private String userName;
    private String userOriginalName;
    private int userTypeID; // 1 for Admin,201 for Arranger, 202 for Member, other for other user type
    private String officeID;

    private String memberCode;
    private String memberName;
    private String dateOfJoin;
    private String dateOfEnt;
    private String memberDob;
    private String phone;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    private String address;

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    private String arrangerMemberCode;

    public String getArrangerMemberCode() {
        return arrangerMemberCode;
    }

    public void setArrangerMemberCode(String arrangerMemberCode) {
        this.arrangerMemberCode = arrangerMemberCode;
    }

    private String appEasyPin;

    private String appEasyPinSetOrNot;

    public String getAppEasyPin() {
        return appEasyPin;
    }

    public void setAppEasyPin(String appEasyPin) {
        this.appEasyPin = appEasyPin;
    }

    public String getAppEasyPinSetOrNot() {
        return appEasyPinSetOrNot;
    }

    public void setAppEasyPinSetOrNot(String appEasyPinSetOrNot) {
        this.appEasyPinSetOrNot = appEasyPinSetOrNot;
    }

    public String getMemberCode() {
        return memberCode;
    }

    public void setMemberCode(String memberCode) {
        this.memberCode = memberCode;
    }

    public String getMemberName() {
        return memberName;
    }

    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }

    public String getDateOfJoin() {
        return dateOfJoin;
    }

    public void setDateOfJoin(String dateOfJoin) {
        this.dateOfJoin = dateOfJoin;
    }

    public String getDateOfEnt() {
        return dateOfEnt;
    }

    public void setDateOfEnt(String dateOfEnt) {
        this.dateOfEnt = dateOfEnt;
    }

    public String getMemberDob() {
        return memberDob;
    }

    public void setMemberDob(String memberDob) {
        this.memberDob = memberDob;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserOriginalName() {
        return userOriginalName;
    }

    public int getUserTypeID() {
        return userTypeID;
    }

    public String getOfficeID() {
        return officeID;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setUserOriginalName(String userOriginalName) {
        this.userOriginalName = userOriginalName;
    }

    public void setUserTypeID(int userTypeID) {
        this.userTypeID = userTypeID;
    }

    public void setOfficeID(String officeID) {
        this.officeID = officeID;
    }
}
