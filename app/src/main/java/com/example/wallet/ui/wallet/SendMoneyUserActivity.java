package com.example.wallet.ui.wallet;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wallet.R;

public class SendMoneyUserActivity extends AppCompatActivity {

    private int sendMoneyAmount;
    private String beneficiaryName;
    private Button btnSendMoney;
    private TextView tvBeneficiaryName;
    private TextView tvPayAmount;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_money_user);
        final EditText etSendMoneyAmount =  findViewById(R.id.et_send_money_amount);
        final EditText etBeneficiaryName =  findViewById(R.id.et_beneficiary_name);
        tvBeneficiaryName = findViewById(R.id.tv_beneficiary_name);
        tvPayAmount = findViewById(R.id.tv_pay_amount);
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
                    tvPayAmount.setText("Please enter amount ₹");
                    Toast.makeText(getApplicationContext(), "Please Enter Valid Amount!",
                            Toast.LENGTH_SHORT).show();
                }   else {
                    btnSendMoney.setEnabled(true);
                    tvPayAmount.setText("₹ " + s.toString());
                    tvPayAmount.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        etBeneficiaryName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.toString().trim().length() == 0 || s.toString().trim().length() >= 20){
                    btnSendMoney.setEnabled(false);
                    tvBeneficiaryName.setText("Please enter beneficiary name");
                    Toast.makeText(getApplicationContext(), "Please Enter Valid beneficiary name!",
                            Toast.LENGTH_SHORT).show();

                }   else {
                    btnSendMoney.setEnabled(true);
                    tvBeneficiaryName.setText(s.toString());
                    tvBeneficiaryName.setVisibility(View.VISIBLE);
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
                beneficiaryName = etBeneficiaryName.getText().toString();
                if(sendMoneyAmount >= 1 && beneficiaryName.trim().length() >= 3) {
                    Intent intent = new Intent(SendMoneyUserActivity.this, SendMoneyPaymentActivity.class);
                    intent.putExtra("amount", sendMoneyAmount);
                    intent.putExtra("beneficiaryName", beneficiaryName);
                    startActivity(intent);
                }
                else {
                    Toast.makeText(getApplicationContext(), "Minimum amount should be ₹1!",
                            Toast.LENGTH_SHORT).show();
                    if (!(beneficiaryName.trim().length() >= 3)) {
                        Toast.makeText(getApplicationContext(), "Please Enter Valid Name!",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
}
