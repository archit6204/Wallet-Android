package com.example.wallet;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity {

    private EditText etUserMobileNo;
    private ProgressBar pbActivityRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etUserMobileNo = findViewById(R.id.et_user_mobile_number);
        pbActivityRegister = findViewById(R.id.pb_activity_register);
        pbActivityRegister.setVisibility(View.INVISIBLE);
        findViewById(R.id.btn_send_otp).setOnClickListener(v -> {
            String countryCode = "91";

            String mobileNumber = etUserMobileNo.getText().toString().trim();

            if (mobileNumber.isEmpty() || mobileNumber.length() < 10) {
                etUserMobileNo.setError("Enter valid mobile number!");
                etUserMobileNo.requestFocus();
                return;
            }

            String userMobileNumber = "+" + countryCode + mobileNumber;

            Intent intent = new Intent(RegisterActivity.this, OtpActivity.class);
            intent.putExtra("userMobileNumber", userMobileNumber);
            startActivity(intent);

        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            Intent intent = new Intent(this, BottomNavigator.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
        pbActivityRegister.setVisibility(View.INVISIBLE);
    }
}

