package com.example.wallet.ui.hostCardEmulator;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wallet.R;
import com.example.wallet.ui.TransactionHistory.TransactionHistoryData;
import com.example.wallet.ui.TransactionHistory.transactionStatus.TransactionStatusActivity;
import com.example.wallet.ui.utils.GlobalVariables;
import com.example.wallet.ui.wallet.SendMoneyPaymentActivity;
import com.example.wallet.ui.wallet.UserData;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

public class HostWalletReaderActivity extends AppCompatActivity implements NfcCardReader.AccountCallback {

    public static final String TAG = "CardReaderFragment";
    // Recommend NfcAdapter flags for reading from other Android devices. Indicates that this
    // activity is interested in NFC-A devices (including other Android devices), and that the
    // system should not check for the presence of NDEF-formatted data (e.g. Android Beam).
    public static int READER_FLAGS =
            NfcAdapter.FLAG_READER_NFC_A | NfcAdapter.FLAG_READER_SKIP_NDEF_CHECK;
    public NfcCardReader mLoyaltyCardReader;
    private TextView mAccountField;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    /** Called when sample is created. Displays generic UI with welcome text. */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host_wallet_reader);
        mAccountField = findViewById(R.id.text_view);
        mAccountField.setText("Waiting...");

        mLoyaltyCardReader = new NfcCardReader(this);

        // Disable Android Beam and register our card reader callback
        enableReaderMode();
    }

    @Override
    public void onPause() {
        super.onPause();
        disableReaderMode();
    }

    @Override
    public void onResume() {
        super.onResume();
        enableReaderMode();
    }

    private void enableReaderMode() {
        Log.i(TAG, "Enabling reader mode");
        NfcAdapter nfc = NfcAdapter.getDefaultAdapter(this);
        if (nfc != null) {
            nfc.enableReaderMode(this, mLoyaltyCardReader, READER_FLAGS, null);
        }
    }

    private void disableReaderMode() {
        Log.i(TAG, "Disabling reader mode");
        NfcAdapter nfc = NfcAdapter.getDefaultAdapter(this);
        if (nfc != null) {
            nfc.disableReaderMode(this);
        }
    }

    @Override
    public void onAccountReceived(final String account) {
        // This callback is run on a background thread, but updates to UI elements must be performed
        // on the UI thread.
       this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mAccountField.setText("archit" + account);
                checkingUserTransferPayments();
            }
        });
    }

    private void checkingUserTransferPayments() {
        String userMobileNumber = "+917661960630";
        String beneficiaryName = "+917800075706";
        DatabaseReference mDatabase;
        GlobalVariables globalVariables = (GlobalVariables)getApplication();
        mDatabase = FirebaseDatabase.getInstance().getReference("AddedMoneyInWallet");
        final DocumentReference userRef = db.collection("users").document(userMobileNumber);
        final DocumentReference beneficiaryRef = db.collection("users").document(beneficiaryName);
        int sendMoneyAmount = 37;
        String id = mDatabase.push().getKey();
        assert id != null;
        String transactionId = "trnstap" + id;
         String userTransactionType = "Debited from: JusTap wallet";
         String userName = globalVariables.getUserName();
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

        userRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                assert document != null;
                if (document.exists()) {
                    UserData currentUserData = document.toObject(UserData.class);
                    assert currentUserData != null;
                    int previousAmount = currentUserData.getTotalAmount();
                    if (previousAmount >= sendMoneyAmount) {
                        int userTotalAmount = previousAmount - sendMoneyAmount;
                        userRef.update(
                                "transactionHistoryData", FieldValue.arrayUnion(userTransactionHistoryData),
                                "lastUpdatedDateAndTime", FieldValue.serverTimestamp(),
                                "totalAmount", userTotalAmount
                        );
                        beneficiaryRef.get().addOnCompleteListener(taskBeneficiaryRef -> {
                            if (task.isSuccessful()) {
                                DocumentSnapshot documentBeneficiaryRef = taskBeneficiaryRef.getResult();
                                assert documentBeneficiaryRef != null;
                                if (documentBeneficiaryRef.exists()) {
                                    UserData beneficiaryUserData = documentBeneficiaryRef.toObject(UserData.class);
                                    assert beneficiaryUserData != null;
                                    int beneficiaryPreviousAmount = beneficiaryUserData.getTotalAmount();
                                    int beneficiaryTotalAmount = beneficiaryPreviousAmount + sendMoneyAmount;
                                    beneficiaryRef.update(
                                            "transactionHistoryData", FieldValue.arrayUnion(beneficiaryTransactionHistoryData),
                                            "lastUpdatedDateAndTime", FieldValue.serverTimestamp(),
                                            "totalAmount", beneficiaryTotalAmount
                                    );
                                    /*tvSendMoneyAmount.setText(stringSendMoneyAmount);
                                    tvSendMoneyAmount.setVisibility(View.VISIBLE);
                                    tvBeneficiaryName.setText(beneficiaryName);
                                    tvBeneficiaryName.setVisibility(View.VISIBLE);*/
                                    Intent intentTransactionStatus = new Intent(HostWalletReaderActivity.this, TransactionStatusActivity.class);
                                    intentTransactionStatus.putExtra("transactionItem", (Parcelable) userTransactionHistoryData);
                                    intentTransactionStatus.putExtra("previousPage", "SendMoneyPaymentActivity");
                                    startActivity(intentTransactionStatus);
                                    /*progressBar.setVisibility(View.GONE);*/
                                }
                            }
                        });
                    } else {
                        Toast.makeText(HostWalletReaderActivity.this, "your wallet Balance is low..!", Toast.LENGTH_SHORT).show();
                        /*progressBar.setVisibility(View.GONE);*/
                    }
                } else {
                    Toast.makeText(HostWalletReaderActivity.this, "your wallet Balance is low. Please add ₹ in wallet.", Toast.LENGTH_SHORT).show();
                    /*progressBar.setVisibility(View.GONE);*/
                }
            } else {
                Toast.makeText(HostWalletReaderActivity.this, "Transaction failed!", Toast.LENGTH_SHORT).show();
                /*progressBar.setVisibility(View.GONE);*/
            }
        });
    }


}