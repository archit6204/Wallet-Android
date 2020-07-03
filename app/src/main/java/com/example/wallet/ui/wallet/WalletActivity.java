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
        addedMoneyDatabase.addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            int totalAmount = 0;
            for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                AddMoneyData addMoneyData = postSnapshot.getValue(AddMoneyData.class);
                assert addMoneyData != null;
                totalAmount = totalAmount + addMoneyData.getTotalAmount();
            }
            String id = mDatabase.push().getKey();
            AvailableBalanceData availableBalanceData = new AvailableBalanceData(id, totalAmount, walletId);
            assert id != null;
            mDatabase.child("UserWallet").setValue(availableBalanceData);
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    });
        mDatabase.addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                progressBar.setVisibility(View.GONE);
                tvTotalWalletAmount.setVisibility(View.VISIBLE);
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    AvailableBalanceData availableBalanceData = postSnapshot.getValue(AvailableBalanceData.class);

                    assert availableBalanceData != null;
                    String stringAvailableWalletAmount = getString(R.string.add_wallet_amount, availableBalanceData.getMoneyAmount());
                    tvTotalWalletAmount.setText(Html.fromHtml(stringAvailableWalletAmount, FROM_HTML_MODE_LEGACY));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }
}
