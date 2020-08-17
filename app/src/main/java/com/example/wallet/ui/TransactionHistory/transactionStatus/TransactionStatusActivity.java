package com.example.wallet.ui.TransactionHistory.transactionStatus;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.wallet.R;
import com.example.wallet.ui.TransactionHistory.TransactionHistoryData;
import com.google.firebase.Timestamp;

public class TransactionStatusActivity extends AppCompatActivity {
    private View vPaidToDetails;
    private View vDebitedFromDetails;
    private String transactionId;
    private int transactionAmount;
    private String walletId;
    private Timestamp transactionDateAndTime;
    private String transactionType;
    private TransactionHistoryData transactionItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_status);
        vPaidToDetails = findViewById(R.id.inc_paid_to_details);
        vDebitedFromDetails = findViewById(R.id.inc_debited_from_details);
        transactionItem = (TransactionHistoryData) getIntent().getSerializableExtra("transactionItem");
        assert transactionItem != null;
        transactionAmount = transactionItem.getTransactionAmount();
        showTransactionStatus();
    }

    public void showTransactionStatus() {
        String paidTo = "Paid to";
        String share = "Share";
        showPaidToDetails(paidTo);
        showDebitedFromDetails(share);
    }

    public void showPaidToDetails(String vpa) {
        String paidTo = "Paid to";
        String share = "Share";
        String amount = Integer.toString(transactionAmount);
        ((TextView) vPaidToDetails.findViewById(R.id.tv_paid_to))
                .setText(paidTo);
        ((TextView) vPaidToDetails.findViewById(R.id.tv_share))
                .setText(share);
        ((ImageView) vPaidToDetails.findViewById(R.id.iv_beneficiary_logo))
                .setImageDrawable(getResources().getDrawable(R.drawable.ic_user));
        ((ImageView) vPaidToDetails.findViewById(R.id.iv_share_logo))
                .setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_share_24));
        ((TextView) vPaidToDetails.findViewById(R.id.tv_transaction_amount))
                .setText(amount);
        ((TextView) vPaidToDetails.findViewById(R.id.tv_beneficiary_name))
                .setText(getResources().getString(R.string.vpa));
    }

    @SuppressLint("SetTextI18n")
    public void showDebitedFromDetails(String vpa) {
        String paidTo = "Debited from";
        String showBalance = "Show balance";
        ((TextView) vDebitedFromDetails.findViewById(R.id.tv_paid_to))
                .setText(paidTo);
        ((TextView) vDebitedFromDetails.findViewById(R.id.tv_share))
                .setText(showBalance);
        ((ImageView) vDebitedFromDetails.findViewById(R.id.iv_beneficiary_logo))
                .setImageDrawable(getResources().getDrawable(R.drawable.ic_transaction_wallet));
        ((ImageView) vDebitedFromDetails.findViewById(R.id.iv_share_logo))
                .setImageDrawable(getResources().getDrawable(R.drawable.rupee));
        ((TextView) vDebitedFromDetails.findViewById(R.id.tv_transaction_amount))
                .setText(getResources().getString(R.string.vpa));
        ((TextView) vDebitedFromDetails.findViewById(R.id.tv_beneficiary_name))
                .setText(getResources().getString(R.string.vpa));
        ((TextView) vDebitedFromDetails.findViewById(R.id.tv_beneficiary_subtitle))
                .setText("UTR: 09812375699876");
    }

    public void showTransactionIdDetails(String vpa) {
        String paidTo = "Debited from";
        String showBalance = "Show balance";
        ((TextView) vDebitedFromDetails.findViewById(R.id.tv_paid_to))
                .setText(paidTo);
        ((TextView) vDebitedFromDetails.findViewById(R.id.tv_share))
                .setText(showBalance);
        ((ImageView) vDebitedFromDetails.findViewById(R.id.iv_beneficiary_logo))
                .setImageDrawable(getResources().getDrawable(R.drawable.ic_transaction_wallet));
        ((ImageView) vDebitedFromDetails.findViewById(R.id.iv_share_logo))
                .setImageDrawable(getResources().getDrawable(R.drawable.rupee));
        ((TextView) vDebitedFromDetails.findViewById(R.id.tv_transaction_amount))
                .setText(getResources().getString(R.string.vpa));
        ((TextView) vDebitedFromDetails.findViewById(R.id.tv_beneficiary_name))
                .setText(getResources().getString(R.string.vpa));
        ((TextView) vDebitedFromDetails.findViewById(R.id.tv_beneficiary_subtitle))
                .setText("UTR: 09812375699876");
    }

    public void showHelpSupportDetails(String vpa) {
        String paidTo = "Debited from";
        String showBalance = "Show balance";
        ((TextView) vDebitedFromDetails.findViewById(R.id.tv_paid_to))
                .setText(paidTo);
        ((TextView) vDebitedFromDetails.findViewById(R.id.tv_share))
                .setText(showBalance);
        ((ImageView) vDebitedFromDetails.findViewById(R.id.iv_beneficiary_logo))
                .setImageDrawable(getResources().getDrawable(R.drawable.ic_transaction_wallet));
        ((ImageView) vDebitedFromDetails.findViewById(R.id.iv_share_logo))
                .setImageDrawable(getResources().getDrawable(R.drawable.rupee));
        ((TextView) vDebitedFromDetails.findViewById(R.id.tv_transaction_amount))
                .setText(getResources().getString(R.string.vpa));
        ((TextView) vDebitedFromDetails.findViewById(R.id.tv_beneficiary_name))
                .setText(getResources().getString(R.string.vpa));
        ((TextView) vDebitedFromDetails.findViewById(R.id.tv_beneficiary_subtitle))
                .setText("UTR: 09812375699876");
    }
}