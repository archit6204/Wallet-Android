package com.example.wallet;

import android.annotation.SuppressLint;
import android.content.Intent;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wallet.ui.utils.GlobalVariables;
import com.example.wallet.ui.wallet.UserData;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


@SuppressLint("SetTextI18n")
public class RegisterActivity extends AppCompatActivity {

    private EditText etUserMobileNo;
    private EditText etUserName;
    private ProgressBar pbActivityRegister;
    private boolean isUserDataFetched = false;
    private Button btnSendOtp;
    private TextInputLayout tilUserName;
    private TextInputLayout tilUserMobileNo;
    private TextView tvSignUpMsg;
    private ImageView ivTurnstilesGate;


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        pbActivityRegister = findViewById(R.id.pb_activity_register);
        ivTurnstilesGate = findViewById(R.id.iv_turnstiles_gate_logo);
        etUserMobileNo = findViewById(R.id.et_user_mobile_number);
        etUserName = findViewById(R.id.et_user_name);
        btnSendOtp = findViewById(R.id.btn_send_otp);
        tilUserMobileNo = findViewById(R.id.til_user_mobile_number);
        tilUserName = findViewById(R.id.til_user_user_name);
        tvSignUpMsg = findViewById(R.id.tv_signup_msg);
        pbActivityRegister.setVisibility(View.VISIBLE);
        btnSendOtp.setOnClickListener(v -> {
            String countryCode = "91";
            String mobileNumber = etUserMobileNo.getText().toString().replaceAll("\\s+", "");
            String userName = etUserName.getText().toString().trim();

            if (mobileNumber.length() != 13) {
                etUserMobileNo.setError("Enter valid mobile number!");
                etUserMobileNo.requestFocus();
                return;
            }
            if (userName.isEmpty() || userName.length() < 3) {
                etUserName.setError("Enter valid username!");
                etUserName.requestFocus();
                return;
            }

            Intent intent = new Intent(RegisterActivity.this, OtpActivity.class);
            intent.putExtra("userMobileNumber", mobileNumber);
            intent.putExtra("userName", userName);
            startActivity(intent);
        });
        updatingStatusBarColor();
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        GlobalVariables globalVariables = (GlobalVariables)getApplication();
        if (currentUser != null && currentUser.getPhoneNumber() != null && currentUser.getPhoneNumber().length() == 13) {
            ivTurnstilesGate.setVisibility(View.VISIBLE);
            tvSignUpMsg.setText("Welcome to TouchPe!");
            if (currentUser.getDisplayName() != null && !currentUser.getDisplayName().isEmpty()) {
                globalVariables.setUserName(currentUser.getDisplayName());
                globalVariables.setMobileNumber(currentUser.getPhoneNumber());
                DocumentReference userRef = db.collection("users").document(currentUser.getPhoneNumber());
                userRef.get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        assert document != null;
                        if (document.exists()) {
                            UserData currentUserData = document.toObject(UserData.class);
                            assert currentUserData != null;
                            globalVariables.setCurrentUserData(currentUserData);
                            Toast.makeText(getApplication(), "Welcome "+  currentUser.getDisplayName() + "!",
                                    Toast.LENGTH_LONG).show();
                            isUserDataFetched = true;
                            Intent intent = new Intent(this, BottomNavigator.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            intent.putExtra("fragmentName", "home");
                            startActivity(intent);
                            pbActivityRegister.setVisibility(View.GONE);
                        } else {
                            init();
                            pbActivityRegister.setVisibility(View.GONE);
                            Log.d("error", "No such document");
                            isUserDataFetched = false;
                        }
                    } else {
                        init();
                        Toast.makeText(getApplication(), "Please check your internet connection!",
                                Toast.LENGTH_SHORT).show();
                        Log.d("failed fetch", "get failed with ", task.getException());
                        isUserDataFetched = false;
                        pbActivityRegister.setVisibility(View.GONE);
                    }
                });

            } else {
                init();
                FirebaseAuth.getInstance().signOut();
                isUserDataFetched = false;
                pbActivityRegister.setVisibility(View.GONE);
            }
        } else {
            init();
            FirebaseAuth.getInstance().signOut();
            isUserDataFetched = false;
            pbActivityRegister.setVisibility(View.GONE);
        }
    }


    private void init() {
        tilUserMobileNo.setVisibility(View.VISIBLE);
        tilUserName.setVisibility(View.VISIBLE);
        etUserMobileNo.setVisibility(View.VISIBLE);
        etUserName.setVisibility(View.VISIBLE);
        btnSendOtp.setVisibility(View.VISIBLE);
        Toast.makeText(getApplication(), "Please enter details!",
                Toast.LENGTH_SHORT).show();
        tvSignUpMsg.setText("Please enter details to proceed.");
        setupEditTextMobileNo();
        ivTurnstilesGate.setVisibility(View.GONE);
    }

    private void setupEditTextMobileNo() {
        etUserMobileNo.setText("+91   ");
        Selection.setSelection(etUserMobileNo.getText(), etUserMobileNo.getText().length());
        etUserMobileNo.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!s.toString().startsWith("+91   ")){
                    etUserMobileNo.setText("+91   ");
                    Selection.setSelection(etUserMobileNo.getText(), etUserMobileNo.getText().length());

                }

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

