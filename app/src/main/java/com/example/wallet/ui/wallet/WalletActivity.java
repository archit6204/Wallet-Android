package com.example.wallet.ui.wallet;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.wallet.R;
import com.example.wallet.ui.utils.GlobalVariables;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;


public class WalletActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private DatabaseReference addedMoneyDatabase;
    private int totalAmountInWallet = 0;
    private String walletId = "ARCHIT0001";
    private TextView tvTotalWalletAmount;
    private ProgressBar progressBar;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
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

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void init() {
        progressBar.setVisibility(View.VISIBLE);
        tvTotalWalletAmount.setVisibility(View.INVISIBLE);
        updatingStatusBarColor();
    }

    @Override
    protected void onStart() {
        super.onStart();
        GlobalVariables globalVariables = (GlobalVariables)getApplication();
        int currentUserTotalAmount = globalVariables.getUserAvailableBalance();
        String stringAvailableWalletAmount = getString(R.string.add_wallet_amount, currentUserTotalAmount);
        tvTotalWalletAmount.setText(stringAvailableWalletAmount);
        tvTotalWalletAmount.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private  void updatingStatusBarColor() {
        Window window = this.getWindow();

        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        // finally change the color
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.my_statusbar_color));
    }

}
