package com.example.wallet.ui.wallet;

import com.example.wallet.ui.TransactionHistory.TransactionHistoryData;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.squareup.okhttp.internal.Internal.instance;

public class UserData {

    private String transactionId;
    private int totalAmount;
    private String walletId;
    private Timestamp lastUpdatedDateAndTime;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private UserData addMoneyData;
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
        /*final DocumentReference userRef = db.collection("users").document("user");
        userRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                addMoneyData = documentSnapshot.toObject(AddMoneyData.class);
                assert addMoneyData != null;
                int previousAmount = addMoneyData.getTotalAmount();
                totalAmount = previousAmount + totalAmount;
                Log.d("data", "aaaaddMoneyDataaaaa data: " + totalAmount);
            }
        });*/
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

    public String setUserName(String userName) {
        return this.userName = userName;
    }

    public String getUserName() {
        return userName;
    }

    public String setMobileNumber(String mobileNumber) {
        return this.mobileNumber = mobileNumber;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }
}