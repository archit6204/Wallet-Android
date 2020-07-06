package com.example.wallet.ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wallet.R;
import com.example.wallet.RegisterActivity;
import com.example.wallet.ui.profile.utis.TextDrawable;
import com.example.wallet.ui.wallet.WalletActivity;
import com.google.firebase.auth.FirebaseAuth;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


public class ProfileFragment extends Fragment {

    private ImageView ivUserImage;
    private ImageView ivLogoutImage;
    private TextView tvUserName;
    private String userName = "Archit";
    private View vAccountDetailsVpa;
    private View vAccountDetailsMobile;
    private View vAccountDetailsUserName;
    private Button btnEditProfile;
    private TextView tvLogout;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_profile, container, false);
        ivUserImage = view.findViewById(R.id.iv_user_image);
        tvUserName = view.findViewById(R.id.tv_user_name);
        btnEditProfile = view.findViewById(R.id.btn_edit_profile);
        vAccountDetailsMobile = view.findViewById(R.id.inc_account_details_mobile_number);
        vAccountDetailsVpa = view.findViewById(R.id.inc_account_details_vpa);
        vAccountDetailsUserName = view.findViewById(R.id.inc_account_details_username);
        tvLogout = vAccountDetailsUserName.findViewById(R.id.tv_item_logout);
        ivLogoutImage = vAccountDetailsUserName.findViewById(R.id.iv_item_logout_icon);
        showProfile();
        btnEditProfile.setOnClickListener(v -> {
            Toast.makeText(getContext(), getResources().getString(R.string.feature_coming_soon),
                    Toast.LENGTH_SHORT).show();
        });

        return view;
    }

    public void showProfile() {
        TextDrawable drawable = TextDrawable.builder().beginConfig()
                .width((int) getResources().getDimension(R.dimen.user_profile_image_size))
                .height((int) getResources().getDimension(R.dimen.user_profile_image_size))
                .endConfig().buildRound(userName.substring(0, 1), R.color.colorAccentBlue);
        ivUserImage.setImageDrawable(drawable);
        tvUserName.setText("Welcome " + userName + "!");
        showMobile("9565600500");
        showVpa("archit007@hdfc");
        showUserName(userName);
    }

    public void showVpa(String vpa) {
        ((ImageView) vAccountDetailsVpa.findViewById(R.id.iv_item_casual_list_icon))
                .setImageDrawable(getResources().getDrawable(R.drawable.ic_transaction));
        ((TextView) vAccountDetailsVpa.findViewById(R.id.tv_item_casual_list_title))
                .setText(vpa);
        ((TextView) vAccountDetailsVpa.findViewById(R.id.tv_item_casual_list_subtitle))
                .setText(getResources().getString(R.string.vpa));
    }

    public void showMobile(String mobile) {
        ((ImageView) vAccountDetailsMobile.findViewById(R.id.iv_item_casual_list_icon))
                .setImageDrawable(getResources().getDrawable(R.drawable.ic_mobile));
        ((TextView) vAccountDetailsMobile.findViewById(R.id.tv_item_casual_list_title))
                .setText(mobile);
        ((TextView) vAccountDetailsMobile.findViewById(R.id.tv_item_casual_list_subtitle))
                .setText(getResources().getString(R.string.mobile));
    }

    public void showUserName(String mobile) {
        ((ImageView) vAccountDetailsUserName.findViewById(R.id.iv_item_casual_list_icon))
                .setImageDrawable(getResources().getDrawable(R.drawable.ic_user));
        ((TextView) vAccountDetailsUserName.findViewById(R.id.tv_item_casual_list_title))
                .setText(mobile);
        ((TextView) vAccountDetailsUserName.findViewById(R.id.tv_item_casual_list_subtitle))
                .setText(getResources().getString(R.string.user_name));
        ((TextView) vAccountDetailsUserName.findViewById(R.id.tv_item_logout))
                .setText(getResources().getString(R.string.logout));
        ivLogoutImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_logout_button_icon));
        tvLogout.setOnClickListener(v -> {
            Toast.makeText(getContext(), getResources().getString(R.string.signing_out),
                    Toast.LENGTH_SHORT).show();
            signOut();
        });
        ivLogoutImage.setOnClickListener(v -> {
            Toast.makeText(getContext(), getResources().getString(R.string.signing_out),
                    Toast.LENGTH_SHORT).show();
            signOut();
        });
    }

    public void signOut() {
        FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(getActivity(), RegisterActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
    }
}
