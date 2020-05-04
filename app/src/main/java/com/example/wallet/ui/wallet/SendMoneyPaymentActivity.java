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

public class SendMoneyPaymentActivity extends AppCompatActivity {

    private TextView tvSendMoneyAmount;
    private int sendMoneyAmount;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_money_payment);

        tvSendMoneyAmount = findViewById(R.id.tv_send_money_amount);

        Intent intent = getIntent();

        sendMoneyAmount = intent.getIntExtra("amount", -1);

        String stringSendMoneyAmount = getString(R.string.add_wallet_amount, sendMoneyAmount);

        tvSendMoneyAmount.setText(Html.fromHtml(stringSendMoneyAmount, FROM_HTML_MODE_LEGACY));

    }
}
