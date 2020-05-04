package com.example.wallet.ui.wallet;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.widget.TextView;

import com.example.wallet.R;

import static android.text.Html.FROM_HTML_MODE_LEGACY;

public class AddMoneyPaymentActivity extends AppCompatActivity {

    private TextView tvAddWalletAmount;
    private int addWalletAmount;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_money_payment);

        tvAddWalletAmount = findViewById(R.id.tv_add_wallet_amount);

        Intent intent = getIntent();

        addWalletAmount = intent.getIntExtra("amount", -1);

        String stringAddWalletAmount = getString(R.string.add_wallet_amount, addWalletAmount);

        tvAddWalletAmount.setText(Html.fromHtml(stringAddWalletAmount, FROM_HTML_MODE_LEGACY));

    }
}
