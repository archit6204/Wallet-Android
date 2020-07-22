package com.example.wallet;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.example.wallet.ui.utils.GlobalVariables;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivity extends AppCompatActivity {

    private EditText etUserMobileNo;
    private EditText etUserName;
    private ProgressBar pbActivityRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etUserMobileNo = findViewById(R.id.et_user_mobile_number);
        etUserName = findViewById(R.id.et_user_name);
        pbActivityRegister = findViewById(R.id.pb_activity_register);
        pbActivityRegister.setVisibility(View.INVISIBLE);
        findViewById(R.id.btn_send_otp).setOnClickListener(v -> {
            String countryCode = "91";
            String mobileNumber = etUserMobileNo.getText().toString().trim();
            String userName = etUserName.getText().toString().trim();

            if (mobileNumber.isEmpty() || mobileNumber.length() < 10) {
                etUserMobileNo.setError("Enter valid mobile number!");
                etUserMobileNo.requestFocus();
                return;
            }
            if (userName.isEmpty() || userName.length() < 3) {
                etUserName.setError("Enter valid username!");
                etUserName.requestFocus();
                return;
            }

            String userMobileNumber = "+" + countryCode + mobileNumber;

            Intent intent = new Intent(RegisterActivity.this, OtpActivity.class);
            intent.putExtra("userMobileNumber", userMobileNumber);
            intent.putExtra("userName", userName);
            startActivity(intent);

        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        GlobalVariables globalVariables = (GlobalVariables)getApplication();
        if (currentUser != null) {
            if (currentUser.getDisplayName() != null) {
                globalVariables.setUserName(currentUser.getDisplayName());
            }
            Intent intent = new Intent(this, BottomNavigator.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
        pbActivityRegister.setVisibility(View.INVISIBLE);
    }
}

