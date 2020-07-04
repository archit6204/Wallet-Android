package com.example.wallet.ui.wallet;

import android.util.Log;

import com.example.wallet.ui.TransactionHistory.TransactionHistoryData;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AddMoneyData {

    private String transactionId;
    private int totalAmount;
    private String walletId;
    private Timestamp lastUpdatedDateAndTime;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private AddMoneyData addMoneyData;
    private List<TransactionHistoryData> transactionHistoryData = new ArrayList<>();

    public AddMoneyData(){
    }
    public AddMoneyData(String transactionId, int totalAmount, String walletId, TransactionHistoryData transactionHistoryData) {
        this.transactionId = transactionId;
        this.totalAmount = totalAmount;
        this.walletId = walletId;
        this.lastUpdatedDateAndTime = new Timestamp(new Date());
        this.transactionHistoryData.add(transactionHistoryData);
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
}