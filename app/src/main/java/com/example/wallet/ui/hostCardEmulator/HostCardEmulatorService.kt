package com.example.wallet.ui.hostCardEmulator

import android.nfc.cardemulation.HostApduService
import android.os.Bundle
import android.util.Log
import com.example.wallet.ui.utils.GlobalVariables
import com.google.firebase.auth.FirebaseAuth


class HostCardEmulatorService: HostApduService() {
    var currentUser = FirebaseAuth.getInstance().currentUser
    var globalVariables = application as GlobalVariables
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

        if (hexCommandApdu.substring(10, 24) == AID)  {
            val userMobileNo = globalVariables.mobileNumber
            val userName = globalVariables.userName
            val userFormattedMobileNo = userMobileNo.substring(1)
            val userFormattedData = STATUS_SUCCESS + userFormattedMobileNo + userName
            if (!userMobileNo.isNullOrEmpty() && userMobileNo == currentUser?.phoneNumber && userFormattedMobileNo.length == 12) {
                return Utils.hexStringToByteArray(userFormattedData)
            } else {
                return Utils.hexStringToByteArray(STATUS_FAILED)
            }
        } else {
            return Utils.hexStringToByteArray(STATUS_FAILED)
        }
    }
}