package com.example.wallet.ui.TransactionHistory.transactionStatus;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.wallet.R;
import com.example.wallet.ui.TransactionHistory.TransactionHistoryData;
import com.google.firebase.Timestamp;

public class TransactionStatusActivity extends AppCompatActivity {
    private View vPaidToDetails;
    private View vDebitedFromDetails;
    private View vTransactionIdDetails;
    private View vHelpSupportDetails;
    private String transactionId;
    private int transactionAmount;
    private String walletId;
    private Timestamp transactionDateAndTime;
    private String transactionType;
    private TransactionHistoryData transactionItem;
    private String transactionFormattedDateNTime;
    private TextView tvTransactionFormattedDateNTime;
    private String[] transactionTypeArray;
    private String debitedOrCredited;
    private String debitedOrCreditedInstrument;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_status);
        vPaidToDetails = findViewById(R.id.inc_paid_to_details);
        vDebitedFromDetails = findViewById(R.id.inc_debited_from_details);
        vTransactionIdDetails = findViewById(R.id.inc_transaction_id_details);
        vHelpSupportDetails = findViewById(R.id.inc_Help_support_details);
        tvTransactionFormattedDateNTime = findViewById(R.id.tv_transaction_date);
        transactionItem = (TransactionHistoryData) getIntent().getSerializableExtra("transactionItem");
        assert transactionItem != null;
        transactionAmount = transactionItem.getTransactionAmount();
        transactionFormattedDateNTime = transactionItem.transactionFormattedDateAndTime();
        showTransactionStatus();
    }

    public void showTransactionStatus() {
        tvTransactionFormattedDateNTime.setText(transactionFormattedDateNTime);
        transactionType = transactionItem.getTransactionType();
        transactionTypeArray = transactionType.split(":");
        debitedOrCredited = transactionTypeArray[0];
        debitedOrCreditedInstrument = transactionTypeArray[1];
        showPaidToDetails();
        showDebitedFromDetails();
        showTransactionIdDetails();
        showHelpSupportDetails();
    }

    public void showPaidToDetails() {
        String paidOrReceived = "Paid to";
        if (debitedOrCredited.equalsIgnoreCase("Credited to")) {
            paidOrReceived = "Received from";
        }
        String share = "Share";
        String walletId = transactionItem.getWalletId();
        String amount = Integer.toString(transactionAmount);
        ((TextView) vPaidToDetails.findViewById(R.id.tv_paid_to))
                .setText(paidOrReceived);
        ((TextView) vPaidToDetails.findViewById(R.id.tv_share))
                .setText(share);
        ((ImageView) vPaidToDetails.findViewById(R.id.iv_beneficiary_logo))
                .setImageDrawable(getResources().getDrawable(R.drawable.ic_user));
        ((ImageView) vPaidToDetails.findViewById(R.id.iv_share_logo))
                .setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_share_24));
        ((TextView) vPaidToDetails.findViewById(R.id.tv_transaction_amount))
                .setText(amount);
        ((TextView) vPaidToDetails.findViewById(R.id.tv_beneficiary_name))
                .setText(walletId);
    }

    @SuppressLint("SetTextI18n")
    public void showDebitedFromDetails() {
        String showBalance = "Show balance";
        String amount = Integer.toString(transactionAmount);
        ((TextView) vDebitedFromDetails.findViewById(R.id.tv_paid_to))
                .setText(debitedOrCredited);
        ((TextView) vDebitedFromDetails.findViewById(R.id.tv_share))
                .setText(showBalance);
        ((ImageView) vDebitedFromDetails.findViewById(R.id.iv_beneficiary_logo))
                .setImageDrawable(getResources().getDrawable(R.drawable.ic_transaction_wallet));
        ((ImageView) vDebitedFromDetails.findViewById(R.id.iv_share_logo))
                .setImageDrawable(getResources().getDrawable(R.drawable.rupee));
        ((TextView) vDebitedFromDetails.findViewById(R.id.tv_transaction_amount))
                .setText(amount);
        ((TextView) vDebitedFromDetails.findViewById(R.id.tv_beneficiary_name))
                .setText(debitedOrCreditedInstrument);
        ((TextView) vDebitedFromDetails.findViewById(R.id.tv_beneficiary_subtitle))
                .setText("UTR: 09812375699876");
    }

    public void showTransactionIdDetails() {
        String transactionId = transactionItem.getTransactionId();
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
        );
        float d = getApplicationContext().getResources().getDisplayMetrics().density;
        int value16dp = (int) (16 * d);
        int value42dp = (int) (42* d);
        int value0dp = (int) (0 * d);
        int value22dp = (int) (16 * d);
        params.setMargins(value16dp, value42dp, value0dp, value0dp);
        ((TextView) vTransactionIdDetails.findViewById(R.id.tv_help_support_name))
                .setText(transactionId);
        ((TextView) vTransactionIdDetails.findViewById(R.id.tv_help_support_name))
                .setLayoutParams(params);
        ((TextView) vTransactionIdDetails.findViewById(R.id.tv_share))
                .setText("copy");
        ((ImageView) vTransactionIdDetails.findViewById(R.id.iv_help_support_logo))
                .setVisibility(View.GONE);
        ((TextView) vTransactionIdDetails.findViewById(R.id.tv_help_support_subtitle))
                .setVisibility(View.INVISIBLE);
        ((TextView) vTransactionIdDetails.findViewById(R.id.tv_transaction_id_to))
                .setVisibility(View.VISIBLE);
    }

    public void showHelpSupportDetails() {
        String contactSupport = "Contact 24x7 Help";
        String showBalance = "Show balance";
        ((TextView) vHelpSupportDetails.findViewById(R.id.tv_help_support_name))
                .setText(contactSupport);
        ((TextView) vHelpSupportDetails.findViewById(R.id.tv_share))
                .setVisibility(View.INVISIBLE);
        ((ImageView) vHelpSupportDetails.findViewById(R.id.iv_help_support_logo))
                .setImageDrawable(getResources().getDrawable(R.drawable.ic_help_support));
        ((TextView) vHelpSupportDetails.findViewById(R.id.tv_help_support_subtitle))
                .setVisibility(View.INVISIBLE);
        ((TextView) vHelpSupportDetails.findViewById(R.id.tv_transaction_id_to))
                .setVisibility(View.INVISIBLE);
    }
}