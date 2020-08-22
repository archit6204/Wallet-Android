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

import com.example.wallet.R;
import com.example.wallet.ui.utils.GlobalVariables;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static android.text.Html.FROM_HTML_MODE_LEGACY;


public class WalletActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private DatabaseReference addedMoneyDatabase;
    private int totalAmountInWallet = 0;
    private String walletId = "ARCHIT0001";
    private TextView tvTotalWalletAmount;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet);
        progressBar = findViewById(R.id.progressbar);
        addedMoneyDatabase = FirebaseDatabase.getInstance().getReference("AddedMoneyInWallet");
        mDatabase = FirebaseDatabase.getInstance().getReference("TotalAmountInWallet");
        tvTotalWalletAmount = findViewById(R.id.tv_available_wallet_amount);
        findViewById(R.id.btn_add_money_to_wallet).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WalletActivity.this, AddMoneyActivity.class);
                startActivity(intent);


            }
        });

        findViewById(R.id.btn_send_money_to_user).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WalletActivity.this, SendMoneyUserActivity.class);
                startActivity(intent);


            }
        });
        init();
    }

    private void init() {
        progressBar.setVisibility(View.VISIBLE);
        tvTotalWalletAmount.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void onStart() {
        super.onStart();
        GlobalVariables globalVariables = (GlobalVariables)getApplication();
        UserData currentUserData = globalVariables.getCurrentUserData();
        int currentUserTotalAmount = currentUserData.getTotalAmount();
        String stringAvailableWalletAmount = getString(R.string.add_wallet_amount, currentUserTotalAmount);
        tvTotalWalletAmount.setText(stringAvailableWalletAmount);
        tvTotalWalletAmount.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
    }
}
