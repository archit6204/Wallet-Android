package com.example.wallet.ui.TransactionHistory;

import com.google.firebase.Timestamp;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TransactionHistoryData {
    private String transactionId;
    private int transactionAmount;
    private String walletId;
    private Timestamp transactionDateAndTime;
    private String transactionType;

    public TransactionHistoryData() {
    }

    public TransactionHistoryData(String transactionId, int transactionAmount, String walletId, String transactionType) {
        this.transactionId = transactionId;
        this.transactionAmount = transactionAmount;
        this.walletId = walletId;
        this.transactionDateAndTime = new Timestamp(new Date());;
        this.transactionType = transactionType;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public int getTransactionAmount() {
        return transactionAmount;
    }

    public String transactionAmountWithCurrency() {
        return "₹" + transactionAmount;
    }

    public String getWalletId() {
        return walletId;
    }

    public Timestamp getTransactionDateAndTime() {
        return transactionDateAndTime;
    }
    public String getTransactionType() {
        return transactionType;
    }

    public String transactionFormattedDateAndTime() {
        String date = transactionDateAndTime.toString();
        String[] dateArray = transactionDateAndTime.toDate().toString().split(" ");
        String dayName = dateArray[0];
        String month = dateArray[1];
        String dayDate = dateArray[2];
        String time = dateArray[3];
        String timeZone = dateArray[4];
        String year = dateArray[5];
        String formattedDate = dayName + ", " + dayDate + " " + month + " " + year + " | " + time;
        return formattedDate;
    }
}
