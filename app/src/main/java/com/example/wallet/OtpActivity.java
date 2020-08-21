package com.example.wallet;

//import android.arch.core.executor.TaskExecutor;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wallet.ui.TransactionHistory.TransactionHistoryData;
import com.example.wallet.ui.utils.GlobalVariables;
import com.example.wallet.ui.wallet.UserData;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class OtpActivity extends AppCompatActivity {

    private String verificationid;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private ProgressBar progressBar;
    private ProgressBar progressBarAutoOTP;
    private EditText etReceivedOtp;
    private String userName;
    private String userMobileNumber;
    private TextView tvAutoRead;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference("AddedMoneyInWallet");
        progressBar = findViewById(R.id.pb_activity_otp);
        progressBarAutoOTP = findViewById(R.id.pb_auto_read_otp);
        etReceivedOtp = findViewById(R.id.et_received_otp);
        tvAutoRead = findViewById(R.id.tv_auto_read_otp_msg);
        progressBarAutoOTP.setVisibility(View.VISIBLE);
        userName = getIntent().getStringExtra("userName");
        userMobileNumber = getIntent().getStringExtra("userMobileNumber");
        sendVerificationCode(userMobileNumber);

        findViewById(R.id.btn_create_account).setOnClickListener(v -> {
            String code = etReceivedOtp.getText().toString().trim();
            tvAutoRead.setVisibility(View.GONE);
            progressBarAutoOTP.setVisibility(View.GONE);
            if ((code.isEmpty() || code.length() < 6)){

                etReceivedOtp.setError("Enter code...");
                etReceivedOtp.requestFocus();
                return;
            }
            verifyCode(code);

        });
    }

    private void verifyCode(String code){
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationid, code);
        signInWithCredential(credential);
    }

    private void signInWithCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (userMobileNumber.length() == 13 && userName.length() >= 3) {
                            final DocumentReference userRef = db.collection("users").document(userName);
                            userRef.get().addOnCompleteListener(userTask -> {
                                if (userTask.isSuccessful()) {
                                    DocumentSnapshot document = userTask.getResult();
                                    assert document != null;
                                    if (document.exists()) {
                                        Toast.makeText(OtpActivity.this, "Welcome " + userName + "!",
                                                Toast.LENGTH_SHORT).show();
                                    } else {
                                        String id = mDatabase.push().getKey();
                                        String transactionId = "trns" + id;
                                        String walletId = userName + userMobileNumber;

                                        final UserData addMoneyData = new UserData(transactionId, 0, walletId, null);
                                        addMoneyData.setMobileNumber(userMobileNumber);
                                        addMoneyData.setUserName(userName);
                                        GlobalVariables globalVariables = (GlobalVariables) getApplication();
                                        globalVariables.setUserName(userName);
                                        globalVariables.setMobileNumber(userMobileNumber);
                                        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                                .setDisplayName(userName)
                                                /*.setPhotoUri(Uri.parse("https://example.com/jane-q-user/profile.jpg"))*/
                                                .build();

                                        assert currentUser != null;
                                        currentUser.updateProfile(profileUpdates)
                                                .addOnCompleteListener(task1 -> {
                                                    if (task1.isSuccessful()) {
                                                        Log.d("TAG", "User profile updated.");
                                                    }
                                                });
                                        Toast.makeText(OtpActivity.this, "Account Created!", Toast.LENGTH_SHORT).show();
                                        userRef.set(addMoneyData, SetOptions.merge())
                                                .addOnSuccessListener(aVoid -> Toast.makeText(OtpActivity.this, "Transaction successful!",
                                                        Toast.LENGTH_SHORT).show())
                                                .addOnFailureListener(e -> Toast.makeText(OtpActivity.this, "Transaction failed!",
                                                        Toast.LENGTH_SHORT).show());
                                    }
                                } else {
                                    Toast.makeText(OtpActivity.this, "Transaction failed!", Toast.LENGTH_SHORT).show();
                                }
                            });
                            Intent intent = new Intent(OtpActivity.this, BottomNavigator.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            intent.putExtra("fragmentName", "home");
                            startActivity(intent);
                            progressBar.setVisibility(View.GONE);
                        }
                    } else {
                        Toast.makeText(OtpActivity.this, Objects.requireNonNull(task.getException()).getMessage(),
                                Toast.LENGTH_LONG).show();
                    }

                });
    }

    private void sendVerificationCode(String number){

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                number,
                60,
                TimeUnit.SECONDS,
                TaskExecutors.MAIN_THREAD,
                mCallBack
        );
    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks
            mCallBack = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            verificationid = s;
        }

        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            String code = phoneAuthCredential.getSmsCode();
            if (code != null){
                etReceivedOtp.setText(code);
                tvAutoRead.setText("OTP received.");
                Toast.makeText(OtpActivity.this, "creating account..!", Toast.LENGTH_LONG).show();
                progressBarAutoOTP.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
                verifyCode(code);
            }
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            Toast.makeText(OtpActivity.this, e.getMessage(),Toast.LENGTH_LONG).show();

        }
    };
}
