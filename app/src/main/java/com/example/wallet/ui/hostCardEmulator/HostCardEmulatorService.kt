package com.example.wallet.ui.hostCardEmulator

import android.nfc.cardemulation.HostApduService
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.example.wallet.ui.TransactionHistory.TransactionHistoryData
import com.example.wallet.ui.utils.GlobalVariables
import com.google.firebase.auth.FirebaseAuth


class HostCardEmulatorService: HostApduService() {
    private var currentUser = FirebaseAuth.getInstance().currentUser
    private var isTransactionDataFetched = false
    private val TAG = "HostCardEmulatorService"
    private var userTransactionData: TransactionHistoryData? = null
    companion object {
        const val TAG = "Host Card Emulator"
        const val STATUS_SUCCESS = "9000"
        const val STATUS_FAILED = "6F00"
        const val CLA_NOT_SUPPORTED = "6E00"
        const val INS_NOT_SUPPORTED = "6D00"
        const val AID = "A0000002471001"
        const val SELECT_INS = "A4"
        const val DEFAULT_CLA = "00"
        const val MIN_APDU_LENGTH = 12
    }


    override fun onDeactivated(reason: Int) {
        Log.d(TAG, "Deactivated: $reason")
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun processCommandApdu(commandApdu: ByteArray?, extras: Bundle?): ByteArray {
        if (commandApdu == null) {
            return Utils.hexStringToByteArray(STATUS_FAILED)
        }

        val hexCommandApdu = Utils.toHex(commandApdu)
        if (hexCommandApdu.length < MIN_APDU_LENGTH) {
            return Utils.hexStringToByteArray(STATUS_FAILED)
        }

        if (hexCommandApdu.substring(0, 2) != DEFAULT_CLA) {
            return Utils.hexStringToByteArray(CLA_NOT_SUPPORTED)
        }

        if (hexCommandApdu.substring(2, 4) != SELECT_INS) {
            return Utils.hexStringToByteArray(INS_NOT_SUPPORTED)
        }

        if (hexCommandApdu.substring(10, 24) == AID) {
            Toast.makeText(this, "Payment requested!", Toast.LENGTH_SHORT).show()
            val globalVariables = application as GlobalVariables
            var userMobileNo = globalVariables.mobileNumber
            userMobileNo = userMobileNo.substring(1) + STATUS_SUCCESS
            /*val byteArrayStatusSuccess = Utils.hexStringToByteArray(STATUS_SUCCESS)*/
            /*val globalVariables = application as GlobalVariables
            val userMobileNo = globalVariables.mobileNumber
            val userName = globalVariables.userName
            val userFormattedMobileNo = userMobileNo.substring(1)
            val userFormattedData = STATUS_SUCCESS + userFormattedMobileNo + userName
            if (!userMobileNo.isNullOrEmpty() && userMobileNo == currentUser?.phoneNumber && userFormattedMobileNo.length == 12 && hexCommandApdu.length == 24) {
                return Utils.hexStringToByteArray(userFormattedData)
            }
            else if (!userMobileNo.isNullOrEmpty() && userMobileNo == currentUser?.phoneNumber && userFormattedMobileNo.length == 12 && hexCommandApdu.length == 28) {
                    val onTransactionSuccessResponse = hexCommandApdu.substring(24)
                Toast.makeText(this, "onTransactionSuccessResponse...", Toast.LENGTH_SHORT).show()
                if (onTransactionSuccessResponse == STATUS_SUCCESS) {

                    if(fetchingTransactionData() && userTransactionData != null) {
                        val intent = Intent(this, TransactionStatusActivity::class.java)
                        intent.putExtra("transactionItem", userTransactionData as Parcelable?)
                        intent.putExtra("previousPage", "HostCardEmulatorService")
                        startActivity(intent)
                    } else {
                        val intentBackToHome = Intent(this, BottomNavigator::class.java)
                        intentBackToHome.putExtra("fragmentName", "history")
                        startActivity(intentBackToHome)
                    }

                }*/
            Log.i(TAG, "APDUResponse: $userMobileNo")
            return Utils.hexStringToByteArray(userMobileNo)
            } else {
                return Utils.hexStringToByteArray(STATUS_FAILED)
            }
        }

    /**
     * Utility method to concatenate two byte arrays.
     * @param first First array
     * @param rest Any remaining arrays
     * @return Concatenated copy of input arrays
     */
    fun concatArrays(first: ByteArray, vararg rest: ByteArray): ByteArray {
        var totalLength = first.size
        for (array in rest) {
            totalLength += array.size
        }
        val result: ByteArray = first.copyOf(totalLength)
        var offset = first.size
        for (array in rest) {
            System.arraycopy(array, 0, result, offset, array.size)
            offset += array.size
        }
        return result
    }

    }

    /*@RequiresApi(Build.VERSION_CODES.N)
    private fun fetchingTransactionData():Boolean {
        Toast.makeText(this, "fetchingTransactionData...", Toast.LENGTH_SHORT).show()
        val currentUser = FirebaseAuth.getInstance().currentUser
        val db = FirebaseFirestore.getInstance()
        val userRef: DocumentReference? = currentUser?.displayName?.let { userName -> db.collection("users").document(userName) }
        userRef?.get()?.addOnCompleteListener { task: Task<DocumentSnapshot?> ->
            if (task.isSuccessful) {
                val document = task.result!!
                if (document.exists()) {
                    val currentUserData = document.toObject(UserData::class.java)!!
                    val transactionHistoryDataList = currentUserData.transactionHistoryData
                    if (transactionHistoryDataList.size >= 1) {
                        val transactionHistoryData: TransactionHistoryData = transactionHistoryDataList[0]
                        if (transactionHistoryData == null) {
                            userRef.update("transactionHistoryData", FieldValue.arrayRemove(null as TransactionHistoryData?))
                        } else {
                            val transactionSortedList = transactionHistoryDataList.sortedWith(compareBy { it.transactionDateAndTime }).reversed()
                            userTransactionData = transactionSortedList[0]
                            isTransactionDataFetched = true
                            Toast.makeText(this, "Redirecting to transaction status page...", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this, "Redirecting to history page...", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    isTransactionDataFetched = false
                    Log.d("error", "No such document")
                    Toast.makeText(this, "Redirecting to history page...", Toast.LENGTH_SHORT).show()
                }
            } else {
                isTransactionDataFetched = false
                Toast.makeText(application, "Please check your internet connection!",
                        Toast.LENGTH_SHORT).show()
                Log.d("failed fetch", "get failed with ", task.exception)
                Toast.makeText(this, "Redirecting to history page...", Toast.LENGTH_SHORT).show()
            }
        }
        return isTransactionDataFetched
    }*/
/*
}*/
