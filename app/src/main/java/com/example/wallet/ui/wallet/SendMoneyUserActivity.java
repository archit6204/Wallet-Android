package com.example.wallet.ui.wallet;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.wallet.R;

public class SendMoneyUserActivity extends AppCompatActivity {

    private int sendMoneyAmount;
    private Button btnSendMoney;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_money_user);
        final EditText etSendMoneyAmount =  findViewById(R.id.et_send_money_amount);
        btnSendMoney = findViewById(R.id.btn_send_money);
        btnSendMoney.setEnabled(false);

        etSendMoneyAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.toString().trim().length()==0){
                    btnSendMoney.setEnabled(false);
                    Toast.makeText(getApplicationContext(), "Please Enter Valid Amount !",
                            Toast.LENGTH_SHORT).show();
                }   else {
                    btnSendMoney.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        findViewById(R.id.btn_send_money).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                sendMoneyAmount = Integer.parseInt(etSendMoneyAmount.getText().toString());
                if(sendMoneyAmount >= 1) {
                    Intent intent = new Intent(SendMoneyUserActivity.this, SendMoneyPaymentActivity.class);
                    intent.putExtra("amount", sendMoneyAmount);
                    startActivity(intent);
                }
                else {
                    Toast.makeText(getApplicationContext(), "Minimum amount should be ₹1 !",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
