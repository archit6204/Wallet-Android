package com.example.wallet.ui.wallet;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wallet.R;
import com.example.wallet.ui.TransactionHistory.TransactionHistoryData;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Delayed;

import static android.text.Html.FROM_HTML_MODE_LEGACY;

public class AddMoneyPaymentActivity extends AppCompatActivity {

    private TextView tvAddWalletAmount;
    private int addWalletAmount;
    private DatabaseReference mDatabase;
    private String walletId = "ARCHIT0001";
    private Map<String, Object> user = new HashMap<>();

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_money_payment);

        mDatabase = FirebaseDatabase.getInstance().getReference("AddedMoneyInWallet");
        final DocumentReference userRef = db.collection("users").document("user");
        tvAddWalletAmount = findViewById(R.id.tv_add_wallet_amount);

        Intent intent = getIntent();

        addWalletAmount = intent.getIntExtra("amount", -1);

        if (addWalletAmount > 10) {
            String id = mDatabase.push().getKey();
            String transactionId = "trns" + id;
            final TransactionHistoryData transactionHistoryData = new TransactionHistoryData(
                    transactionId,
                    addWalletAmount,
                    "JusTap",
                    "Debited from: UPI");
            final AddMoneyData addMoneyData = new AddMoneyData(transactionId, addWalletAmount, walletId, transactionHistoryData);

            assert id != null;
            userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        assert document != null;
                        if (document.exists()) {
                            AddMoneyData addMoneyData = document.toObject(AddMoneyData.class);
                            assert addMoneyData != null;
                            int previousAmount = addMoneyData.getTotalAmount();
                            int totalAmount = previousAmount + addWalletAmount;
                            userRef.update(
                                    "transactionHistoryData", FieldValue.arrayUnion(transactionHistoryData),
                                    "lastUpdatedDateAndTime", FieldValue.serverTimestamp(),
                                    "totalAmount", totalAmount
                            );
                            Toast.makeText(AddMoneyPaymentActivity.this, "Transaction successful!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(AddMoneyPaymentActivity.this, "No Balance found!", Toast.LENGTH_SHORT).show();
                            userRef.set(addMoneyData, SetOptions.merge())
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(AddMoneyPaymentActivity.this, "Transaction successful!", Toast.LENGTH_SHORT).show();

                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(AddMoneyPaymentActivity.this, "Transaction failed!", Toast.LENGTH_SHORT).show();

                                        }
                                    });
                        }
                    } else {
                        Toast.makeText(AddMoneyPaymentActivity.this, "Transaction failed!", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        } else {
            Toast.makeText(this, "Minimum amount must be grater than 10...", Toast.LENGTH_LONG).show();
        }

        String stringAddWalletAmount = getString(R.string.add_wallet_amount, addWalletAmount);

        tvAddWalletAmount.setText(Html.fromHtml(stringAddWalletAmount, FROM_HTML_MODE_LEGACY));

    }
}
