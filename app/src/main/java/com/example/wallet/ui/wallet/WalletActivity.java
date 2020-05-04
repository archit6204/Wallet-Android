package com.example.wallet.ui.wallet;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.wallet.R;
import com.google.android.material.card.MaterialCardView;

public class WalletActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet);

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
    }

}
