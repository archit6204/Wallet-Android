package com.example.wallet.ui.wallet;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wallet.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static android.text.Html.FROM_HTML_MODE_LEGACY;

public class AddMoneyPaymentActivity extends AppCompatActivity {

    private TextView tvAddWalletAmount;
    private int addWalletAmount;
    private DatabaseReference mDatabase;
    private String walletId = "ARCHIT0001";

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_money_payment);

        mDatabase = FirebaseDatabase.getInstance().getReference("AddedMoneyInWallet");

        tvAddWalletAmount = findViewById(R.id.tv_add_wallet_amount);

        Intent intent = getIntent();

        addWalletAmount = intent.getIntExtra("amount", -1);

        if (addWalletAmount > 10) {

            //getting a unique id using push().getKey() method
            //it will create a unique id and we will use it as the Primary Key for our Artist
            String id = mDatabase.push().getKey();

            //creating an AddMoneyData Object
            AddMoneyData addMoneyData = new AddMoneyData(id, addWalletAmount, walletId);

            //Saving the AddMoneyData
            assert id != null;
            mDatabase.child(id).setValue(addMoneyData);

            //displaying a success toast
            Toast.makeText(this, "Amount added to your wallet!", Toast.LENGTH_LONG).show();
        } else {
            //if the value is not given displaying a toast
            Toast.makeText(this, "Minimum amount must be grater than 10...", Toast.LENGTH_LONG).show();
        }

        String stringAddWalletAmount = getString(R.string.add_wallet_amount, addWalletAmount);

        tvAddWalletAmount.setText(Html.fromHtml(stringAddWalletAmount, FROM_HTML_MODE_LEGACY));

    }
}
