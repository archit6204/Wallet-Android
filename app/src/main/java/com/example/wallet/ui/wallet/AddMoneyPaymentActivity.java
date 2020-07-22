package com.example.wallet.ui.wallet;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wallet.R;
import com.example.wallet.ui.TransactionHistory.TransactionHistoryData;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

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
                    "JusTap wallet",
                    "Debited from: UPI");
            final UserData addMoneyData = new UserData(transactionId, addWalletAmount, walletId, transactionHistoryData);

            assert id != null;
            userRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    assert document != null;
                    if (document.exists()) {
                        UserData addMoneyData1 = document.toObject(UserData.class);
                        assert addMoneyData1 != null;
                        int previousAmount = addMoneyData1.getTotalAmount();
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
                                .addOnSuccessListener(aVoid -> Toast.makeText(AddMoneyPaymentActivity.this, "Transaction successful!", Toast.LENGTH_SHORT).show())
                                .addOnFailureListener(e -> Toast.makeText(AddMoneyPaymentActivity.this, "Transaction failed!", Toast.LENGTH_SHORT).show());
                    }
                } else {
                    Toast.makeText(AddMoneyPaymentActivity.this, "Transaction failed!", Toast.LENGTH_SHORT).show();
                }
            });

        } else {
            Toast.makeText(this, "Minimum amount must be grater than 10...", Toast.LENGTH_LONG).show();
        }

        String stringAddWalletAmount = getString(R.string.add_wallet_amount, addWalletAmount);

        tvAddWalletAmount.setText(Html.fromHtml(stringAddWalletAmount, FROM_HTML_MODE_LEGACY));

    }
}
