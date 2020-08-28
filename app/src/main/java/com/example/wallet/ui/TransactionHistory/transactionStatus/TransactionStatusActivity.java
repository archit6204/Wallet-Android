package com.example.wallet.ui.TransactionHistory.transactionStatus;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.example.wallet.BottomNavigator;
import com.example.wallet.R;
import com.example.wallet.ui.TransactionHistory.TransactionHistoryData;
import com.google.firebase.Timestamp;

import java.io.File;
import java.io.FileOutputStream;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;


public class TransactionStatusActivity extends AppCompatActivity {
    private View vPaidToDetails;
    private View vDebitedFromDetails;
    private View vTransactionIdDetails;
    private View vHelpSupportDetails;
    private String transactionId;
    private String transactionAmount;
    private String walletId;
    private Timestamp transactionDateAndTime;
    private String transactionType;
    private TransactionHistoryData transactionItem;
    private String transactionFormattedDateNTime;
    private TextView tvTransactionFormattedDateNTime;
    private String[] transactionTypeArray;
    private String debitedOrCredited;
    private String debitedOrCreditedInstrument;
    private View rootView;
    private boolean doubleBackToExitPressedOnce = false;
    private String previousPage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_status);
        rootView = getWindow().getDecorView().findViewById(android.R.id.content);
        vPaidToDetails = findViewById(R.id.inc_paid_to_details);
        vDebitedFromDetails = findViewById(R.id.inc_debited_from_details);
        vTransactionIdDetails = findViewById(R.id.inc_transaction_id_details);
        vHelpSupportDetails = findViewById(R.id.inc_Help_support_details);
        tvTransactionFormattedDateNTime = findViewById(R.id.tv_transaction_date);
        transactionItem = (TransactionHistoryData) getIntent().getSerializableExtra("transactionItem");
        previousPage = getIntent().getStringExtra("previousPage");
        assert transactionItem != null;
        transactionAmount = transactionItem.transactionAmountWithCurrency();
        transactionFormattedDateNTime = transactionItem.transactionFormattedDateAndTime();
        showTransactionStatus();
        Button btnBackToHome = findViewById(R.id.btn_back_to_home);
        btnBackToHome.setOnClickListener(v -> {
            Intent intentBackToHome = new Intent(TransactionStatusActivity.this, BottomNavigator.class);
            intentBackToHome.putExtra("fragmentName", "home");
            startActivity(intentBackToHome);
        });
        ImageView ivTransactionBack = findViewById(R.id.iv_transaction_back_logo);
        ivTransactionBack.setOnClickListener(v -> {
            assert previousPage != null;
            if (previousPage.equals("TransactionHistoryFragment")) {
                finish();
            } else {
                Intent intentBackToHome = new Intent(TransactionStatusActivity.this, BottomNavigator.class);
                intentBackToHome.putExtra("fragmentName", "history");
                startActivity(intentBackToHome);
            }
        });

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
        String share = "Share";
        String walletId = transactionItem.getWalletId();
        String[] walletIdArray = walletId.split(" ");
        String amount = transactionAmount;
        TextView tvShareDetails = ((TextView) vPaidToDetails.findViewById(R.id.tv_share));
        ProgressBar progressBar = vPaidToDetails.findViewById(R.id.pb_transaction_status_item);
        tvShareDetails.setText(share);
        tvShareDetails.setOnClickListener(v -> {
            progressBar.setVisibility(View.VISIBLE);
            Bitmap bitmap = getScreenShot(rootView);
            File file =  store(bitmap, "Justap Screenshot");
            shareImage(file);
            progressBar.setVisibility(View.GONE);
        });
        ImageView ivBeneficiaryLogo = vPaidToDetails.findViewById(R.id.iv_beneficiary_logo);
        if (walletIdArray.length > 1) {
            String suffixWalletId = walletIdArray[1];
            switch (suffixWalletId.toLowerCase()) {
                case "metro":
                    paidOrReceived = "Paid to";
                    ivBeneficiaryLogo.setImageDrawable(getResources().getDrawable(R.drawable.ic_underground_metro));
                    break;
                case "bus":
                    paidOrReceived = "Paid to";
                    ivBeneficiaryLogo.setImageDrawable(getResources().getDrawable(R.drawable.ic_bus_ticket));
                    break;
                case "wallet":
                    paidOrReceived = "Money added to";
                    ivBeneficiaryLogo.setImageDrawable(getResources().getDrawable(R.drawable.ic_transaction_wallet));
                    break;
                default:
                    paidOrReceived = "Send to";
                    ivBeneficiaryLogo.setImageDrawable(getResources().getDrawable(R.drawable.ic_user));
            }
        } else {
            paidOrReceived = "Send to";
            ivBeneficiaryLogo.setImageDrawable(getResources().getDrawable(R.drawable.ic_user));
        }
        if (walletIdArray[0].equalsIgnoreCase("metro")) {
            ivBeneficiaryLogo.setImageDrawable(getResources().getDrawable(R.drawable.ic_underground_metro));
        }
        if (debitedOrCredited.equalsIgnoreCase("Credited to")) {
            paidOrReceived = "Received from";
        }
        ((TextView) vPaidToDetails.findViewById(R.id.tv_paid_to))
                .setText(paidOrReceived);
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
        String amount = transactionAmount;
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
        vTransactionIdDetails.findViewById(R.id.tv_help_support_name)
                .setLayoutParams(params);
        ((TextView) vTransactionIdDetails.findViewById(R.id.tv_share))
                .setText("copy");
        vTransactionIdDetails.findViewById(R.id.iv_help_support_logo)
                .setVisibility(View.GONE);
        vTransactionIdDetails.findViewById(R.id.tv_help_support_subtitle)
                .setVisibility(View.INVISIBLE);
        vTransactionIdDetails.findViewById(R.id.tv_transaction_id_to)
                .setVisibility(View.VISIBLE);
        vTransactionIdDetails.findViewById(R.id.tv_share)
                .setOnClickListener(v -> {
                    ClipboardManager clipboard = (ClipboardManager) getApplicationContext().getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText(null, transactionId);
                    if (clipboard == null) return;
                    clipboard.setPrimaryClip(clip);
                    Toast.makeText(this, "Copied to clipboard", Toast.LENGTH_SHORT).show();
                });
    }

    public void showHelpSupportDetails() {
        String contactSupport = "Contact 24x7 Help";
        String showBalance = "Show balance";
        ((TextView) vHelpSupportDetails.findViewById(R.id.tv_help_support_name))
                .setText(contactSupport);
        vHelpSupportDetails.findViewById(R.id.tv_share)
                .setVisibility(View.INVISIBLE);
        ImageView ivHelpLogo = vHelpSupportDetails.findViewById(R.id.iv_help_support_logo);
        ivHelpLogo.setImageDrawable(getResources().getDrawable(R.drawable.ic_help_support));
        vHelpSupportDetails.findViewById(R.id.iv_next_logo)
                .setVisibility(View.VISIBLE);
        vHelpSupportDetails.findViewById(R.id.tv_help_support_subtitle)
                .setVisibility(View.INVISIBLE);
        vHelpSupportDetails.findViewById(R.id.tv_transaction_id_to)
                .setVisibility(View.INVISIBLE);
        vHelpSupportDetails.findViewById(R.id.cv_transaction_id)
                .setOnClickListener(v -> {
                    Toast.makeText(TransactionStatusActivity.this, "feature coming soon!", Toast.LENGTH_SHORT).show();
                });
    }

    public static Bitmap getScreenShot(View view) {
        View screenView = view.getRootView();
        screenView.setDrawingCacheEnabled(true);
        Bitmap bitmap = Bitmap.createBitmap(screenView.getDrawingCache());
        screenView.setDrawingCacheEnabled(false);
        return bitmap;
    }

    public File store(Bitmap bm, String fileName) {
        final String dirPath = Environment.getExternalStorageDirectory().toString() + "/" + "screenshot";
        File dir = new File(dirPath);
        if(!dir.exists())
            dir.mkdirs();
        String fileNameq = fileName + ".jpeg";
        File file = new File(dirPath, fileNameq);
        try {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                // Do the file write
                FileOutputStream fOut = new FileOutputStream(file);
                bm.compress(Bitmap.CompressFormat.JPEG, 85, fOut);
                fOut.flush();
                fOut.close();
            } else {
                // Request permission from the user
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return file;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 0) {
            Bitmap bitmap = getScreenShot(rootView);
            File file = store(bitmap, "justap");
            shareImage(file);
        }
    }
    private void shareImage(File file) {
        Uri uri = FileProvider.getUriForFile(this, this.getApplicationContext().getPackageName() + ".provider", file);
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.setType("image/*");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.putExtra(android.content.Intent.EXTRA_SUBJECT, "");
        intent.putExtra(android.content.Intent.EXTRA_TEXT, "");
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        try {
            startActivity(Intent.createChooser(intent, "Share Screenshot"));
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "No App Available", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        assert previousPage != null;
        if (previousPage.equals("TransactionHistoryFragment")) {
            finish();
        } else {
            Intent intentBackToHome = new Intent(TransactionStatusActivity.this, BottomNavigator.class);
            intentBackToHome.putExtra("fragmentName", "history");
            startActivity(intentBackToHome);
        }
    }

}

