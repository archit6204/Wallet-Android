package com.example.wallet.ui.wallet;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.example.wallet.R;
import com.example.wallet.ui.utils.GlobalVariables;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static android.text.Html.FROM_HTML_MODE_LEGACY;

public class AddMoneyActivity extends AppCompatActivity {

    private int addWalletAmount;
    private Button btnAddMoney;
    private DatabaseReference mDatabase;
    private TextView tvTotalWalletAmount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_money);
        final EditText etAddWalletAmount =  findViewById(R.id.et_add_wallet_amount);
        tvTotalWalletAmount = findViewById(R.id.tv_available_wallet_amount);
        btnAddMoney = findViewById(R.id.btn_add_money);
        mDatabase = FirebaseDatabase.getInstance().getReference("TotalAmountInWallet");
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

    @Override
    protected void onStart() {
        super.onStart();
        GlobalVariables globalVariables = (GlobalVariables)getApplication();
        int currentUserTotalAmount = globalVariables.getUserAvailableBalance();
        String stringAvailableWalletAmount = getString(R.string.add_wallet_amount, currentUserTotalAmount);
        tvTotalWalletAmount.setText(stringAvailableWalletAmount);
        tvTotalWalletAmount.setVisibility(View.VISIBLE);
    }

}

