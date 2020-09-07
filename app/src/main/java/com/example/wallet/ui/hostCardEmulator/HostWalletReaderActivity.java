package com.example.wallet.ui.hostCardEmulator;

import android.content.Intent;
import android.nfc.NfcAdapter;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wallet.R;
import com.example.wallet.ui.TransactionHistory.TransactionHistoryData;
import com.example.wallet.ui.TransactionHistory.transactionStatus.TransactionStatusActivity;
import com.example.wallet.ui.utils.GlobalVariables;
import com.example.wallet.ui.wallet.UserData;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

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
    private TextView tvNoNfcFound;
    private EditText etViaNFCAmount;

    /** Called when sample is created. Displays generic UI with welcome text. */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host_wallet_reader);
        mAccountField = findViewById(R.id.text_view);
        tvNoNfcFound = findViewById(R.id.tv_no_nfc_found);
        mLoyaltyCardReader = new NfcCardReader(this);
        etViaNFCAmount = (EditText) findViewById(R.id.et_pay_nfc_amount);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Receive payments");
        // Disable Android Beam and register our card reader callback
        enableReaderMode();
        updatingStatusBarColor();
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
            mAccountField.setVisibility(View.GONE);
            etViaNFCAmount.setVisibility(View.VISIBLE);
            if (nfc.isEnabled()) {
                tvNoNfcFound.setText("Waiting for payments...");
            } else {
                tvNoNfcFound.setText("Please enable NFC via settings!");
            }

        } else {
            mAccountField.setText("Sorry, this phone does not have NFC!");
            tvNoNfcFound.setVisibility(View.VISIBLE);
            etViaNFCAmount.setVisibility(View.GONE);
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
       this.runOnUiThread(() -> {
           tvNoNfcFound.setText("Customer Mobile Number:" + account);
           if (account.length() == 12) {
               tvNoNfcFound.setText("Payments completed!");
               Toast.makeText(HostWalletReaderActivity.this, "Payments completed!", Toast.LENGTH_SHORT).show();
               checkingUserTransferPayments(account);
           } else {
               Toast.makeText(HostWalletReaderActivity.this, "Transaction failed!", Toast.LENGTH_SHORT).show();
           }
       });
    }

    private void checkingUserTransferPayments(final String otherUserMobileNumber) {
        GlobalVariables globalVariables = (GlobalVariables)getApplication();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        assert currentUser != null;
        String beneficiaryMobileNumber = "+919305748712";
        String otherMobileNumber = "+" + otherUserMobileNumber;
        DatabaseReference mDatabase;
        mDatabase = FirebaseDatabase.getInstance().getReference("AddedMoneyInWallet");
        final DocumentReference userRef = db.collection("users").document(otherMobileNumber);
        final DocumentReference beneficiaryRef = db.collection("users").document(beneficiaryMobileNumber);
        int sendMoneyAmount = 28;
        String id = mDatabase.push().getKey();
        assert id != null;
        String transactionId = "trnstap" + id;
         String userTransactionType = "Debited from: JusTap wallet";
         String userName = globalVariables.getUserName();
         String beneficiaryName = "metro";
        final TransactionHistoryData userTransactionHistoryData = new TransactionHistoryData(
                transactionId,
                sendMoneyAmount,
                beneficiaryName,
                userTransactionType);
        String beneficiaryTransactionType = "Credited to: JusTap wallet";
        final TransactionHistoryData beneficiaryTransactionHistoryData = new TransactionHistoryData(
                transactionId,
                sendMoneyAmount,
                otherMobileNumber,
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
                                    ).addOnCompleteListener(task1 -> {
                                        Intent intentTransactionStatus = new Intent(HostWalletReaderActivity.this, TransactionStatusActivity.class);
                                        intentTransactionStatus.putExtra("transactionItem", (Parcelable) userTransactionHistoryData);
                                        intentTransactionStatus.putExtra("previousPage", "SendMoneyPaymentActivity");
                                        startActivity(intentTransactionStatus);
                                    });
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

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private  void updatingStatusBarColor() {
        Window window = this.getWindow();

        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        // finally change the color
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.my_statusbar_color));
    }

}