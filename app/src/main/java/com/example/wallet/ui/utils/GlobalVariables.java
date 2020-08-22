package com.example.wallet.ui.utils;

import android.app.Application;

import com.example.wallet.ui.wallet.UserData;

public class GlobalVariables extends Application {

    private String userName = "";
    private String mobileNumber = "";
    private UserData currentUserData;
    private int userAvailableBalance;


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

    public void setCurrentUserData(UserData currentUserData) {
        this.currentUserData = currentUserData;
    }

    public UserData getCurrentUserData() {
        return currentUserData;
    }

    public void setUserAvailableBalance(int userAvailableBalance) {
        this.userAvailableBalance = userAvailableBalance;
    }

    public int getUserAvailableBalance() {
        return userAvailableBalance;
    }
}
