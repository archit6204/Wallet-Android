package com.example.wallet.ui.TransactionHistory;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.Timestamp;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TransactionHistoryData implements Serializable, Parcelable {
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

    protected TransactionHistoryData(Parcel in) {
        transactionId = in.readString();
        transactionAmount = in.readInt();
        walletId = in.readString();
        transactionDateAndTime = (Timestamp) in.readValue(Timestamp.class.getClassLoader());
        transactionType = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(transactionId);
        dest.writeInt(transactionAmount);
        dest.writeString(walletId);
        dest.writeValue(transactionDateAndTime);
        dest.writeString(transactionType);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<TransactionHistoryData> CREATOR = new Parcelable.Creator<TransactionHistoryData>() {
        @Override
        public TransactionHistoryData createFromParcel(Parcel in) {
            return new TransactionHistoryData(in);
        }

        @Override
        public TransactionHistoryData[] newArray(int size) {
            return new TransactionHistoryData[size];
        }
    };
}
