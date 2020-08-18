package com.example.wallet.ui.wallet;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wallet.R;
import com.example.wallet.ui.TransactionHistory.TransactionHistoryData;
import com.example.wallet.ui.TransactionHistory.transactionStatus.TransactionStatusActivity;
import com.example.wallet.ui.utils.GlobalVariables;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

public class SendMoneyPaymentActivity extends AppCompatActivity {

    private TextView tvSendMoneyAmount;
    private TextView tvBeneficiaryName;
    private int sendMoneyAmount;
    private String beneficiaryName;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DatabaseReference mDatabase;
    private String walletId = "Beneficiary";
    private String userTransactionType = "Debited from: JusTap wallet";
    private ProgressBar progressBar;
    private String userName;
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_money_payment);
        progressBar = findViewById(R.id.progressbar);
        GlobalVariables globalVariables = (GlobalVariables)getApplication();
        userName = globalVariables.getUserName();
        mDatabase = FirebaseDatabase.getInstance().getReference("AddedMoneyInWallet");
        final DocumentReference userRef = db.collection("users").document(userName);

        tvSendMoneyAmount = findViewById(R.id.tv_send_money_amount);
        tvBeneficiaryName = findViewById(R.id.tv_sent_beneficiary_name);

        Intent intent = getIntent();

        sendMoneyAmount = intent.getIntExtra("amount", -1);
        beneficiaryName = intent.getStringExtra("beneficiaryName");
        String stringSendMoneyAmount = getResources().getString(R.string.add_wallet_amount, sendMoneyAmount);
        final DocumentReference beneficiaryRef = db.collection("users").document(beneficiaryName);
        if (sendMoneyAmount >= 1 && beneficiaryName != null && !beneficiaryName.isEmpty()) {
            String id = mDatabase.push().getKey();
            String transactionId = "trnstap" + id;
            walletId = beneficiaryName;
            final TransactionHistoryData userTransactionHistoryData = new TransactionHistoryData(
                    transactionId,
                    sendMoneyAmount,
                    beneficiaryName,
                    userTransactionType);
            String beneficiaryTransactionType = "Credited to: JusTap wallet";
            final TransactionHistoryData beneficiaryTransactionHistoryData = new TransactionHistoryData(
                    transactionId,
                    sendMoneyAmount,
                    userName,
                    beneficiaryTransactionType);
            final UserData addMoneyData = new UserData(transactionId, sendMoneyAmount, walletId, userTransactionHistoryData);

            assert id != null;
            userRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    assert document != null;
                    if (document.exists()) {
                        UserData addMoneyData1 = document.toObject(UserData.class);
                        assert addMoneyData1 != null;
                        int previousAmount = addMoneyData1.getTotalAmount();
                        if (previousAmount >= sendMoneyAmount) {
                            int userTotalAmount = previousAmount - sendMoneyAmount;
                            userRef.update(
                                    "transactionHistoryData", FieldValue.arrayUnion(userTransactionHistoryData),
                                    "lastUpdatedDateAndTime", FieldValue.serverTimestamp(),
                                    "totalAmount", userTotalAmount
                            );
                            Toast.makeText(SendMoneyPaymentActivity.this, "Redirecting to transaction status page...", Toast.LENGTH_SHORT).show();
                            beneficiaryRef.get().addOnCompleteListener(taskBeneficiaryRef -> {
                                        if (task.isSuccessful()) {
                                            DocumentSnapshot documentBeneficiaryRef = taskBeneficiaryRef.getResult();
                                            assert documentBeneficiaryRef != null;
                                            if (documentBeneficiaryRef.exists()) {
                                                UserData beneficiaryAddMoneyData = documentBeneficiaryRef.toObject(UserData.class);
                                                assert beneficiaryAddMoneyData != null;
                                                int beneficiaryPreviousAmount = beneficiaryAddMoneyData.getTotalAmount();
                                                int beneficiaryTotalAmount = beneficiaryPreviousAmount + sendMoneyAmount;
                                                beneficiaryRef.update(
                                                        "transactionHistoryData", FieldValue.arrayUnion(beneficiaryTransactionHistoryData),
                                                        "lastUpdatedDateAndTime", FieldValue.serverTimestamp(),
                                                        "totalAmount", beneficiaryTotalAmount
                                                );
                                            }
                                        }
                                    });
                            tvSendMoneyAmount.setText(stringSendMoneyAmount);
                            tvSendMoneyAmount.setVisibility(View.VISIBLE);
                            tvBeneficiaryName.setText(beneficiaryName);
                            tvBeneficiaryName.setVisibility(View.VISIBLE);
                            Intent intentTransactionStatus = new Intent(SendMoneyPaymentActivity.this, TransactionStatusActivity.class);
                            intentTransactionStatus.putExtra("transactionItem", (Parcelable) userTransactionHistoryData);
                            startActivity(intentTransactionStatus);
                            progressBar.setVisibility(View.GONE);
                        } else {
                            Toast.makeText(SendMoneyPaymentActivity.this, "your wallet Balance is low..!", Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    } else {
                        Toast.makeText(SendMoneyPaymentActivity.this, "your wallet Balance is low. Please add ₹ in wallet.", Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                    }
                } else {
                    Toast.makeText(SendMoneyPaymentActivity.this, "Transaction failed!", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                }
            });

        } else {
            Toast.makeText(this, "Minimum amount must be grater than ₹1...", Toast.LENGTH_LONG).show();
        }

    }
}
