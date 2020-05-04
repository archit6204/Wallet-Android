package com.example.wallet.ui.wallet;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.wallet.R;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


public class WalletLayoutFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_wallet_layout, container, false);
    }
}