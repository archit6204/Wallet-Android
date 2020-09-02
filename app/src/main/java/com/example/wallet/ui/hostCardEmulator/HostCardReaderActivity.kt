package com.example.wallet.ui.hostCardEmulator


import android.app.PendingIntent.getActivity
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
import com.example.wallet.ui.TransactionHistory.TransactionHistoryData
import com.example.wallet.ui.TransactionHistory.transactionStatus.TransactionStatusActivity
import com.example.wallet.ui.utils.GlobalVariables
import com.example.wallet.ui.wallet.UserData
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore


class HostCardReaderActivity : AppCompatActivity() {

    private var nfcAdapter: NfcAdapter? = null
    private var mTextView: TextView? = null
    private var tvNoNfcFound: TextView? = null
    private val STATUS_SUCCESS = "9000"
    private val db = FirebaseFirestore.getInstance()
    private val mDatabase: DatabaseReference? = null
    private val userTransactionType = "Debited from: JusTap wallet"
    private var otherUserName = ""
    private var beneficiaryName = ""
    private var isTransactionSuccessful = false
    var mNfcCardReader: NfcCardReader? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_host_card_reader)
        nfcAdapter = NfcAdapter.getDefaultAdapter(this)
        mTextView = findViewById<View>(R.id.text_view) as TextView?
        tvNoNfcFound = findViewById<View>(R.id.tv_no_nfc_found) as TextView?
        val toolbar:Toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        toolbar.title = "Receive payments"
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
        /*mNfcCardReader =  NfcCardReader(this);*/
    }

    public override fun onResume() {
        super.onResume()
        nfcAdapter?.enableReaderMode(this, mNfcCardReader,
                NfcAdapter.FLAG_READER_NFC_A or
                        NfcAdapter.FLAG_READER_SKIP_NDEF_CHECK,
                null)
    }

    public override fun onPause() {
        super.onPause()
        nfcAdapter?.disableReaderMode(this)
    }

    /*private fun onTagResponseSuccess(response: String): Boolean {
        val beneficiaryTransactionHistoryData = checkingUserTransferPayments()
        if (response == STATUS_SUCCESS) {
                if (isTransactionSuccessful && beneficiaryTransactionHistoryData != null) {
                   *//* val intent = Intent(this, TransactionStatusActivity::class.java)
                    intent.putExtra("transactionItem", beneficiaryTransactionHistoryData as Parcelable?)
                    intent.putExtra("previousPage", "HostCardReaderActivity")
                    startActivity(intent)*//*
                    return true
                } else {
                    Toast.makeText(this, "Transaction Failed!", Toast.LENGTH_SHORT).show()
                    return false
                }
        } else {
            Toast.makeText(this, "Transaction Failed!", Toast.LENGTH_SHORT).show()
            return false
        }
    }*/

    private fun checkingUserTransferPayments() {
        val currentUser = FirebaseAuth.getInstance().currentUser
        otherUserName = currentUser?.phoneNumber.toString()
        beneficiaryName = "+919565600500"
        Toast.makeText(this, "Transaction Successful!", Toast.LENGTH_SHORT).show()
        val userRef: DocumentReference = db.collection("users").document(otherUserName)
        val beneficiaryRef: DocumentReference = db.collection("users").document(beneficiaryName)
        val id: String? = mDatabase?.push()?.key
        val transactionId = "trnstap$id"
        val sendMoneyAmount = 20
        val userTransactionHistoryData = TransactionHistoryData(
                transactionId,
                sendMoneyAmount,
                beneficiaryName,
                userTransactionType)
        val beneficiaryTransactionType = "Credited to: JusTap wallet"
        val beneficiaryTransactionHistoryData = TransactionHistoryData(
                transactionId,
                sendMoneyAmount,
                otherUserName,
                beneficiaryTransactionType)
        userRef.get().addOnCompleteListener { task: Task<DocumentSnapshot?> ->
            if (task.isSuccessful) {
                val document = task.result!!
                if (document.exists()) {
                    val currentUserData = document.toObject(UserData::class.java)!!
                    val previousAmount = currentUserData.totalAmount
                    if (previousAmount >= sendMoneyAmount) {
                        val userTotalAmount: Int = previousAmount - sendMoneyAmount
                        userRef.update(
                                "transactionHistoryData", FieldValue.arrayUnion(userTransactionHistoryData),
                                "lastUpdatedDateAndTime", FieldValue.serverTimestamp(),
                                "totalAmount", userTotalAmount
                        )
                        Toast.makeText(this, "Redirecting to transaction status page...", Toast.LENGTH_SHORT).show()
                        beneficiaryRef.get().addOnCompleteListener { taskBeneficiaryRef: Task<DocumentSnapshot?> ->
                            if (task.isSuccessful) {
                                val documentBeneficiaryRef = taskBeneficiaryRef.result!!
                                if (documentBeneficiaryRef.exists()) {
                                    val beneficiaryUserData = documentBeneficiaryRef.toObject(UserData::class.java)!!
                                    val beneficiaryPreviousAmount = beneficiaryUserData.totalAmount
                                    val beneficiaryTotalAmount: Int = beneficiaryPreviousAmount + sendMoneyAmount
                                    beneficiaryRef.update(
                                            "transactionHistoryData", FieldValue.arrayUnion(beneficiaryTransactionHistoryData),
                                            "lastUpdatedDateAndTime", FieldValue.serverTimestamp(),
                                            "totalAmount", beneficiaryTotalAmount
                                    ).addOnCompleteListener {
                                        tvNoNfcFound?.text = "Transaction Successful! Redirecting to payment status page."
                                        isTransactionSuccessful = true
                                        runOnUiThread {
                                            val intent = Intent(this, TransactionStatusActivity::class.java)
                                            intent.putExtra("transactionItem", beneficiaryTransactionHistoryData as Parcelable?)
                                            intent.putExtra("previousPage", "HostCardReaderActivity")
                                            startActivity(intent)
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        Toast.makeText(this, "your wallet Balance is low..!", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "your wallet Balance is low. Please add ₹ in wallet.", Toast.LENGTH_SHORT).show()

                }
            } else {
                Toast.makeText(this, "Transaction failed!", Toast.LENGTH_SHORT).show()
            }
        }
    }

}