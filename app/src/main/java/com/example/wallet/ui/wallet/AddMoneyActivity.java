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

public class AddMoneyActivity extends AppCompatActivity {

    private int addWalletAmount;
    private Button btnAddMoney;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_money);
        final EditText etAddWalletAmount =  findViewById(R.id.et_add_wallet_amount);
        btnAddMoney = findViewById(R.id.btn_add_money);
        btnAddMoney.setEnabled(false);

        etAddWalletAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if(s.toString().trim().length()==0){
                        btnAddMoney.setEnabled(false);
                        Toast.makeText(getApplicationContext(), "Please Enter Valid Amount !",
                                Toast.LENGTH_SHORT).show();
                    }   else {
                        btnAddMoney.setEnabled(true);
                    }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

            findViewById(R.id.btn_add_money).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                        addWalletAmount = Integer.parseInt(etAddWalletAmount.getText().toString());
                    if(addWalletAmount >= 10) {
                        Intent intent = new Intent(AddMoneyActivity.this, AddMoneyPaymentActivity.class);
                        intent.putExtra("amount", addWalletAmount);
                        startActivity(intent);
                    }
                    else {
                        Toast.makeText(getApplicationContext(), "Minimum amount should be ₹10 !",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }


        }

