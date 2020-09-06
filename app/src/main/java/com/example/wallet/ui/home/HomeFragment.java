package com.example.wallet.ui.home;

import android.content.Intent;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.wallet.R;
import com.example.wallet.ui.hostCardEmulator.HostWalletReaderActivity;
import com.example.wallet.ui.utils.GlobalVariables;
import com.example.wallet.ui.wallet.AddMoneyActivity;
import com.example.wallet.ui.wallet.SendMoneyUserActivity;
import com.example.wallet.ui.wallet.UserData;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


public class HomeFragment extends Fragment {

    private ImageView ivMetro;
    private ImageView ivBus;
    private ImageView ivUPI;
    private ImageView ivAddMoney;
    private ImageView ivSendMoney;
    private ImageView ivNFCSendMoney;
    private ProgressBar progressBar;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_home, container, false);
        ivMetro = view.findViewById(R.id.iv_item_metro_icon);
        ivBus = view.findViewById(R.id.iv_item_bus_icon);
        ivUPI = view.findViewById(R.id.iv_item_upi_icon);
        ivAddMoney = view.findViewById(R.id.iv_item_addMoney_icon);
        ivSendMoney = view.findViewById(R.id.iv_item_sendMoney_icon);
        ivNFCSendMoney = view.findViewById(R.id.iv_nfc_sendMoney_icon);
        progressBar = view.findViewById(R.id.pb_transaction_history);
        progressBar.setVisibility(View.VISIBLE);
        ivMetro.setOnClickListener(v -> {
            Toast.makeText(getContext(), getResources().getString(R.string.feature_coming_soon),
                    Toast.LENGTH_SHORT).show();
        });
        ivBus.setOnClickListener(v -> {
            Toast.makeText(getContext(), getResources().getString(R.string.feature_coming_soon),
                    Toast.LENGTH_SHORT).show();
        });
        ivUPI.setOnClickListener(v -> {
            Toast.makeText(getContext(), getResources().getString(R.string.feature_coming_soon),
                    Toast.LENGTH_SHORT).show();
        });
        ivAddMoney.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), AddMoneyActivity.class);
            startActivity(intent);
        });
        ivSendMoney.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), SendMoneyUserActivity.class);
            startActivity(intent);
        });
        ivNFCSendMoney.setOnClickListener(v -> {
            NfcAdapter nfc = NfcAdapter.getDefaultAdapter(getContext());
            if (nfc == null) {
                Toast.makeText(getContext(), "Sorry! this mobile does not have NFC.", Toast.LENGTH_LONG).show();
            } else {
                if (nfc.isEnabled()) {
                    Intent intent = new Intent(getActivity(), HostWalletReaderActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(getContext(), "To receive payments please enable NFC in Settings!", Toast.LENGTH_LONG).show();
                }
            }

        });
        prepareTransactionData();
        return view;
    }

    private void prepareTransactionData() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        GlobalVariables globalVariables = (GlobalVariables) Objects.requireNonNull(getActivity()).getApplication();
        assert globalVariables != null;
        assert currentUser != null;
        if (globalVariables.getMobileNumber() != null && currentUser.getPhoneNumber() != null && currentUser.getPhoneNumber().length() == 13) {
            DocumentReference userRef = db.collection("users").document(currentUser.getPhoneNumber());
            userRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    assert document != null;
                    if (document.exists()) {
                        UserData currentUserData = document.toObject(UserData.class);
                        assert currentUserData != null;
                        globalVariables.setCurrentUserData(currentUserData);
                        globalVariables.setUserAvailableBalance(currentUserData.getTotalAmount());
                    } else {
                        Log.d("error", "No sucList<TransactionHistoryData>h document");
                    }
                    progressBar.setVisibility(View.GONE);
                } else {
                    Log.d("failed fetch", "get failed with ", task.getException());
                    progressBar.setVisibility(View.GONE);
                }
            });
        }
    }
}