package com.example.wallet;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.wallet.ui.utils.GlobalVariables;
import com.example.wallet.ui.wallet.UserData;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

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
        pbActivityRegister.setVisibility(View.VISIBLE);
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
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        GlobalVariables globalVariables = (GlobalVariables)getApplication();
        if (currentUser != null && currentUser.getPhoneNumber() != null && currentUser.getPhoneNumber().length() == 13) {
            if (currentUser.getDisplayName() != null && !currentUser.getDisplayName().isEmpty()) {
                globalVariables.setUserName(currentUser.getDisplayName());
                globalVariables.setMobileNumber(currentUser.getPhoneNumber());
                DocumentReference userRef = db.collection("users").document(currentUser.getDisplayName());
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
                        } else {
                            Log.d("error", "No such document");
                        }
                    } else {
                        Toast.makeText(getApplication(), "Please check your internet connection!",
                                Toast.LENGTH_SHORT).show();
                        Log.d("failed fetch", "get failed with ", task.getException());
                    }
                });
                Intent intent = new Intent(this, BottomNavigator.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.putExtra("fragmentName", "home");
                startActivity(intent);
            } else {
                FirebaseAuth.getInstance().signOut();
            }
            pbActivityRegister.setVisibility(View.GONE);
        }
        pbActivityRegister.setVisibility(View.GONE);
    }
}

