package com.example.wallet.ui.utils;

import android.app.Application;

public class GlobalVariables extends Application {

    private int data = 200;
    private String userName = "";
    private String mobileNumber = "";

    public int getData() {
        return this.data;
    }

    public void setData(int d) {
        this.data=d;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserName() {
        return userName;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }
}
