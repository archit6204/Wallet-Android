package com.example.wallet.ui.nfc;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.example.wallet.R;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class NFCAndroidBeamActivity extends AppCompatActivity implements NfcAdapter.CreateNdefMessageCallback {

    private EditText etViaNFCAmount;
    private TextView tvNoNfcFound;
    private Button btnViaNFCSendMoney;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nfc_android_beam);
        etViaNFCAmount = (EditText) findViewById(R.id.et_send_nfc_amount);
        tvNoNfcFound = (TextView) findViewById(R.id.tv_no_nfc_found);
        btnViaNFCSendMoney = (Button) findViewById(R.id.btn_nfc_send_money);
        NfcAdapter mAdapter = NfcAdapter.getDefaultAdapter(this);
        if (mAdapter == null) {
            tvNoNfcFound.setVisibility(View.VISIBLE);
            btnViaNFCSendMoney.setEnabled(false);
            etViaNFCAmount.setVisibility(View.GONE);
            return;
        }
        else {
            btnViaNFCSendMoney.setEnabled(true);
            etViaNFCAmount.setVisibility(View.VISIBLE);
            tvNoNfcFound.setVisibility(View.INVISIBLE);
        }

        if (!mAdapter.isEnabled()) {
            Toast.makeText(this, "Please enable NFC via Settings.", Toast.LENGTH_LONG).show();
            tvNoNfcFound.setText("NFC found! Please enable NFC via Settings.");
        } else {
            tvNoNfcFound.setVisibility(View.INVISIBLE);
        }

        mAdapter.setNdefPushMessageCallback(this, this);
    }

    /**
     * Ndef Record that will be sent over via NFC
     * @param nfcEvent
     * @return
     */
    @Override
    public NdefMessage createNdefMessage(NfcEvent nfcEvent) {
        String message = etViaNFCAmount.getText().toString();
        NdefRecord ndefRecord = NdefRecord.createMime("text/plain", message.getBytes());
        NdefMessage ndefMessage = new NdefMessage(ndefRecord);
        return ndefMessage;
    }
}