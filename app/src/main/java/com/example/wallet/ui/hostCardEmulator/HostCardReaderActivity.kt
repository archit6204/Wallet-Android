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
import com.example.wallet.ui.TransactionHistory.TransactionHistoryData
import com.example.wallet.ui.TransactionHistory.transactionStatus.TransactionStatusActivity
import com.example.wallet.ui.utils.GlobalVariables
import com.example.wallet.ui.wallet.UserData
import com.google.android.gms.tasks.Task
import com.google.firebase.database.DatabaseReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore


class HostCardReaderActivity : AppCompatActivity(), NfcAdapter.ReaderCallback {

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
        val mobileNo = globalVariables.mobileNumber
        val isoDep = IsoDep.get(tag)
        isoDep.connect()
        val response = isoDep.transceive(Utils.hexStringToByteArray(
                "00A4040007A0000002471001"))
        /*val responseUserName = isoDep.transceive(Utils.hexStringToByteArray(
                userName))*/
        val responseInHex = Utils.toHex(response)
        val responseHexStatus = responseInHex.substring(0, 4)
        val responseHexUserMobileNo = responseInHex.substring(4, 17)
        val responseHexUserName = responseInHex.substring(17)
        otherUserName = responseHexUserName
        beneficiaryName = userName
        if (responseHexStatus == STATUS_SUCCESS && responseHexUserMobileNo.length == 12 && responseHexUserName.isNotEmpty()) {
            val isResponseSuccess = onTagResponseSuccess(responseHexStatus)
            if (isResponseSuccess) {

            }
        } else {
            Toast.makeText(this, "Transaction Failed!", Toast.LENGTH_SHORT).show()
            tvNoNfcFound?.text = "Transaction failed!Please try again."
        }
        runOnUiThread {
            mTextView?.text = ("\nCard Response: " + Utils.toHex(response))
            /*mTextView?.text = ("\nresponseUserName: " + Utils.toHex(responseUserName))*/
        }
        isoDep.close()
        /*tvNoNfcFound?.visibility = View.INVISIBLE*/
    }

    private fun onTagResponseSuccess(response: String): Boolean {
        if (response == STATUS_SUCCESS) {
            val beneficiaryTransactionHistoryData = checkingUserTransferPayments()
                if (isTransactionSuccessful && beneficiaryTransactionHistoryData != null) {
                    val intent = Intent(this, TransactionStatusActivity::class.java)
                    intent.putExtra("transactionItem", beneficiaryTransactionHistoryData as Parcelable?)
                    intent.putExtra("previousPage", "HostCardReaderActivity")
                    startActivity(intent)
                    return true
                } else {
                    Toast.makeText(this, "Transaction Failed!", Toast.LENGTH_SHORT).show()
                    return false
                }
        } else {
            Toast.makeText(this, "Transaction Failed!", Toast.LENGTH_SHORT).show()
            return false
        }
    }

    private fun checkingUserTransferPayments(): TransactionHistoryData? {
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
                                    )
                                    tvNoNfcFound?.text = "Transaction Successful! Redirecting to payment status page."
                                    isTransactionSuccessful = true
                                }
                            }
                        }
                        /*val intentTransactionStatus = Intent(this, TransactionStatusActivity::class.java)
                        intentTransactionStatus.putExtra("transactionItem", userTransactionHistoryData as Parcelable)
                        intentTransactionStatus.putExtra("previousPage", "SendMoneyPaymentActivity")
                        startActivity(intentTransactionStatus)*/
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
        if (isTransactionSuccessful) {
            return beneficiaryTransactionHistoryData
        } else {
            return null
        }
    }
}