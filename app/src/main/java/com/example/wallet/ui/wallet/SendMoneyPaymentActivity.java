package com.example.wallet.ui.wallet;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wallet.R;
import com.example.wallet.ui.TransactionHistory.TransactionHistoryData;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import static android.text.Html.FROM_HTML_MODE_LEGACY;

public class SendMoneyPaymentActivity extends AppCompatActivity {

    private TextView tvSendMoneyAmount;
    private TextView tvBeneficiaryName;
    private int sendMoneyAmount;
    private String beneficiaryName;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DatabaseReference mDatabase;
    private String walletId = "Beneficiary";
    private String transactionType = "Debited from: JusTap wallet";
    private ProgressBar progressBar;
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_money_payment);
        progressBar = findViewById(R.id.progressbar);
        mDatabase = FirebaseDatabase.getInstance().getReference("AddedMoneyInWallet");
        final DocumentReference userRef = db.collection("users").document("user");
        tvSendMoneyAmount = findViewById(R.id.tv_send_money_amount);
        tvBeneficiaryName = findViewById(R.id.tv_sent_beneficiary_name);

        Intent intent = getIntent();

        sendMoneyAmount = intent.getIntExtra("amount", -1);
        beneficiaryName = intent.getStringExtra("beneficiaryName");
        String stringSendMoneyAmount = getResources().getString(R.string.add_wallet_amount, sendMoneyAmount);

        if (sendMoneyAmount > 1 && beneficiaryName != null) {
            String id = mDatabase.push().getKey();
            String transactionId = "trnstap" + id;
            walletId = beneficiaryName;
            final TransactionHistoryData transactionHistoryData = new TransactionHistoryData(
                    transactionId,
                    sendMoneyAmount,
                    walletId,
                    transactionType);
            final AddMoneyData addMoneyData = new AddMoneyData(transactionId, sendMoneyAmount, walletId, transactionHistoryData);

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
                            int totalAmount = previousAmount - sendMoneyAmount;
                            userRef.update(
                                    "transactionHistoryData", FieldValue.arrayUnion(transactionHistoryData),
                                    "lastUpdatedDateAndTime", FieldValue.serverTimestamp(),
                                    "totalAmount", totalAmount
                            );
                            Toast.makeText(SendMoneyPaymentActivity.this, "Transaction successful!", Toast.LENGTH_SHORT).show();
                            tvSendMoneyAmount.setText(stringSendMoneyAmount);
                            tvSendMoneyAmount.setVisibility(View.VISIBLE);
                            tvBeneficiaryName.setText(beneficiaryName);
                            tvBeneficiaryName.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.GONE);
                        } else {
                            Toast.makeText(SendMoneyPaymentActivity.this, "No Balance found!", Toast.LENGTH_SHORT).show();
                            userRef.set(addMoneyData, SetOptions.merge())
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(SendMoneyPaymentActivity.this, "Transaction successful!", Toast.LENGTH_SHORT).show();

                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(SendMoneyPaymentActivity.this, "Transaction failed!", Toast.LENGTH_SHORT).show();

                                        }
                                    });
                        }
                    } else {
                        Toast.makeText(SendMoneyPaymentActivity.this, "Transaction failed!", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        } else {
            Toast.makeText(this, "Minimum amount must be grater than 10...", Toast.LENGTH_LONG).show();
        }

    }
}
