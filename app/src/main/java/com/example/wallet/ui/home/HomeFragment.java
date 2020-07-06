package com.example.wallet.ui.home;

import android.os.Bundle;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wallet.R;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


public class HomeFragment extends Fragment {

    private ImageView ivMetro;
    private ImageView ivBus;
    private ImageView ivUPI;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_home, container, false);
        ivMetro = view.findViewById(R.id.iv_item_metro_icon);
        ivBus = view.findViewById(R.id.iv_item_bus_icon);
        ivUPI = view.findViewById(R.id.iv_item_upi_icon);
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
        return view;
    }
}