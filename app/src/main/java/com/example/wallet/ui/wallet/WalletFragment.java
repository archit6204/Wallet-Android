package com.example.wallet.ui.wallet;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.wallet.R;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class WalletFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_wallet, container, false);

        view.findViewById(R.id.cv_fragment_wallet).setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 Intent intent = new Intent(getActivity(), WalletActivity.class);
                 startActivity(intent);
             }
         });
        return view;
    }




}