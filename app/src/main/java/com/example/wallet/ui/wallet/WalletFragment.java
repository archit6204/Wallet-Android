package com.example.wallet.ui.wallet;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wallet.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

public class WalletFragment extends Fragment {

    private TextView mTvTapToShowBalance;
    private TextView mTvTapToHideBalance;
    private String stringAvailableWalletAmount;
    private ViewGroup homeScreenContainer;
    private int totalAvailableBalance = -1;
    private ProgressBar progressBar;
    private boolean isDataFetched = false;
    private String tapToShow;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tapToShow = getResources().getString(R.string.tap_to_show);
        DocumentReference userRef = db.collection("users").document("user");
        userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    assert document != null;
                    if (document.exists()) {
                        AddMoneyData addMoneyData = document.toObject(AddMoneyData.class);
                        assert addMoneyData != null;
                        totalAvailableBalance = addMoneyData.getTotalAmount();
                        if (totalAvailableBalance != -1 && tapToShow != null) {
                            isDataFetched = true;
                            mTvTapToShowBalance.setText(tapToShow);
                        }
                        if (progressBar != null) {
                            progressBar.setVisibility(View.GONE);
                        }

                    } else {
                        Log.d("error", "No such document");
                    }
                } else {
                    Log.d("failed fetch", "get failed with ", task.getException());
                }
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_wallet, container, false);
        homeScreenContainer = view.findViewById(R.id.wallet_fragment_frame_container);
        mTvTapToShowBalance = view.findViewById(R.id.tv_tap_to_show_balance);
        mTvTapToHideBalance = view.findViewById(R.id.tv_hide_balance);
        progressBar = view.findViewById(R.id.pb_wallet);



        view.findViewById(R.id.cv_fragment_wallet).setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 Intent intent = new Intent(getActivity(), WalletActivity.class);
                 startActivity(intent);
             }
         });

        mTvTapToShowBalance.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                if (totalAvailableBalance != -1) {
                    stringAvailableWalletAmount = getResources().getString(R.string.add_wallet_amount, totalAvailableBalance);
                }
                if (mTvTapToShowBalance.getText().toString().equals(getResources().getString(R.string.tap_to_show)) ||
                        mTvTapToShowBalance.getText().toString().equals(getResources().getString(R.string.fetching_balance))) {
                    TransitionManager.beginDelayedTransition(homeScreenContainer);
                    if (stringAvailableWalletAmount != null) {
                        mTvTapToShowBalance.setText(stringAvailableWalletAmount);
                        mTvTapToHideBalance.setVisibility(View.VISIBLE);
                    } else {
                        mTvTapToShowBalance.setText(getResources().getString(R.string.fetching_balance));
                        Toast.makeText(getContext(), getResources().getString(R.string.fetching_balance),
                                Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        mTvTapToHideBalance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TransitionManager.beginDelayedTransition(homeScreenContainer);
                mTvTapToShowBalance.setText(getResources().getString(R.string.tap_to_show));
                mTvTapToHideBalance.setVisibility(View.INVISIBLE);
            }
        });

        return view;
    }

    private void init() {

    }

    @Override
    public void onStart() {
        super.onStart();
    }

}