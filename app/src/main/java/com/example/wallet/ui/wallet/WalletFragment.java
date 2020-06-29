package com.example.wallet.ui.wallet;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wallet.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainer;

import static android.text.Html.FROM_HTML_MODE_LEGACY;

public class WalletFragment extends Fragment {

    private TextView mTvTapToShowBalance;
    private TextView mTvTapToHideBalance;
    private String stringAvailableWalletAmount;
    private ViewGroup homeScreenContainer;
    private AvailableBalanceData availableBalanceData;
    private ProgressBar progressBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("TotalAmountInWallet");
        mDatabase.addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                     availableBalanceData = postSnapshot.getValue(AvailableBalanceData.class);
                }
                mTvTapToShowBalance.setText(getResources().getString(R.string.tap_to_show));
                if (progressBar != null) {
                    progressBar.setVisibility(View.GONE);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

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
                if (availableBalanceData != null) {
                    stringAvailableWalletAmount = getResources().getString(R.string.add_wallet_amount, availableBalanceData.getMoneyAmount());
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