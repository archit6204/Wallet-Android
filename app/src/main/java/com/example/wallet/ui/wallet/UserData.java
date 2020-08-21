package com.example.wallet.ui.wallet;

import com.example.wallet.ui.TransactionHistory.TransactionHistoryData;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class UserData {

    private String transactionId;
    private int totalAmount;
    private String walletId;
    private Timestamp lastUpdatedDateAndTime;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private List<TransactionHistoryData> transactionHistoryData = new ArrayList<>();
    private String mobileNumber;
    private String userName;
    private static UserData instance;


    public UserData(){
    }
    public UserData(String transactionId, int totalAmount, String walletId, TransactionHistoryData transactionHistoryData) {
        this.transactionId = transactionId;
        this.totalAmount = totalAmount;
        this.walletId = walletId;
        this.lastUpdatedDateAndTime = new Timestamp(new Date());
        this.transactionHistoryData.add(transactionHistoryData);
    }

    public static synchronized UserData getInstance() {
        if (instance == null) {
            instance = new UserData();
        }
        return instance;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public int getTotalAmount() {
        return totalAmount;
    }

    public String getWalletId() {
        return walletId;
    }

    public Timestamp getLastUpdatedDateAndTime() {
        return lastUpdatedDateAndTime;
    }

    public List<TransactionHistoryData> getTransactionHistoryData() {
        return transactionHistoryData;
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