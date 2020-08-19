package com.example.wallet.ui.hostCardEmulator


import android.content.Intent
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.IsoDep
import android.os.Bundle
import android.os.Parcelable
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.wallet.R
import com.example.wallet.ui.TransactionHistory.transactionStatus.TransactionStatusActivity
import com.example.wallet.ui.utils.GlobalVariables


class HostCardReaderActivity : AppCompatActivity(), NfcAdapter.ReaderCallback {

    private var nfcAdapter: NfcAdapter? = null
    private var mTextView: TextView? = null
    private var tvNoNfcFound: TextView? = null
    private val STATUS_SUCCESS = "9000"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_host_card_reader)
        nfcAdapter = NfcAdapter.getDefaultAdapter(this)
        mTextView = findViewById<View>(R.id.text_view) as TextView?
        tvNoNfcFound = findViewById<View>(R.id.tv_no_nfc_found) as TextView?
        val toolbar:Toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        toolbar.title = "NFC card-reader"
        if (nfcAdapter == null) {
            tvNoNfcFound?.visibility = View.VISIBLE
            mTextView?.text = "NFC not found on device."
            return
        } else {
            tvNoNfcFound?.visibility = View.INVISIBLE
            mTextView?.text = R.string.hce_reading_card.toString()
        }

        if (!nfcAdapter!!.isEnabled) {
            Toast.makeText(this, "Please enable NFC via Settings.", Toast.LENGTH_LONG).show()
            tvNoNfcFound?.visibility = View.VISIBLE
            tvNoNfcFound?.text = "NFC found! Please enable NFC via Settings."
        } else {
            tvNoNfcFound?.visibility = View.VISIBLE
            tvNoNfcFound?.text = "Waiting for another device or card..."
        }
    }

    public override fun onResume() {
        super.onResume()
        nfcAdapter?.enableReaderMode(this, this,
                NfcAdapter.FLAG_READER_NFC_A or
                        NfcAdapter.FLAG_READER_SKIP_NDEF_CHECK,
                null)
    }

    public override fun onPause() {
        super.onPause()
        nfcAdapter?.disableReaderMode(this)
    }

    override fun onTagDiscovered(tag: Tag?) {
        val globalVariables = application as GlobalVariables
        val userName = globalVariables.userName
        val isoDep = IsoDep.get(tag)
        isoDep.connect()
        val response = isoDep.transceive(Utils.hexStringToByteArray(
                "00A4040007A0000002471001"))
        val responseUserName = isoDep.transceive(Utils.hexStringToByteArray(
                userName))
        if (Utils.toHex(response) == STATUS_SUCCESS) {
            val isResponseSuccess = onTagResponseSuccess(Utils.toHex(response))
            if (isResponseSuccess) {
                tvNoNfcFound?.text = "Transaction Successful! Redirecting to payment status page."
            }
        } else {
            Toast.makeText(this, "Transaction Failed!", Toast.LENGTH_SHORT).show()
            tvNoNfcFound?.text = "Transaction failed!Please try again."
        }
        runOnUiThread {
            mTextView?.text = ("\nCard Response: " + Utils.toHex(response))
            mTextView?.text = ("\nresponseUserName: " + Utils.toHex(responseUserName))
        }
        isoDep.close()
        /*tvNoNfcFound?.visibility = View.INVISIBLE*/
    }

    private fun onTagResponseSuccess(response: String): Boolean {
        if (response == STATUS_SUCCESS) {
            val intent = Intent(this, TransactionStatusActivity::class.java)
           /* intent.putExtra("transactionItem", transactionItem as Parcelable?)*/
            intent.putExtra("previousPage", "HostCardReaderActivity")
            startActivity(intent)
            return true
        } else {
            Toast.makeText(this, "Transaction Failed!", Toast.LENGTH_SHORT).show()
            return false
        }
    }
}