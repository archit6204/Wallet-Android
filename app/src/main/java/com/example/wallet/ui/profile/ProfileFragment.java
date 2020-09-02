package com.example.wallet.ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wallet.R;
import com.example.wallet.RegisterActivity;
import com.example.wallet.ui.hostCardEmulator.HostCardReaderActivity;
import com.example.wallet.ui.hostCardEmulator.HostWalletReaderActivity;
import com.example.wallet.ui.nfc.NFCAndroidBeamActivity;
import com.example.wallet.ui.profile.utils.TextDrawable;
import com.example.wallet.ui.utils.GlobalVariables;
import com.example.wallet.ui.wallet.UserData;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


public class ProfileFragment extends Fragment {

    private ImageView ivUserImage;
    private ImageView ivLogoutImage;
    private TextView tvUserName;
    private ProgressBar progressBar;
    private View vAccountDetailsVpa;
    private View vAccountDetailsMobile;
    private View vAccountDetailsUserName;
    private TextView tvLogout;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String userName = "";
    private String userMobileNumber = "";
    private String VPA = "";
    private boolean isUserInfoFetched = false;
    private boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GlobalVariables globalVariables = (GlobalVariables) Objects.requireNonNull(getActivity()).getApplication();
        assert globalVariables != null;
        userName = globalVariables.getUserName();
        userMobileNumber = globalVariables.getMobileNumber();
        DocumentReference userRef = db.collection("users").document(userMobileNumber);
        userRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                assert document != null;
                if (document.exists()) {
                    UserData userData = document.toObject(UserData.class);
                    assert userData != null;
                    userName = userData.getUserName();
                    userMobileNumber = userData.getMobileNumber();
                    VPA = userData.getWalletId();
                    if (progressBar != null) {
                        progressBar.setVisibility(View.GONE);
                    }
                    isUserInfoFetched = true;
                    showProfile();
                } else {
                    Log.d("error", "No such document");
                    isUserInfoFetched = false;
                }
            } else {
                Log.d("failed fetch", "get failed with ", task.getException());
                isUserInfoFetched = false;
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_profile, container, false);
        ivUserImage = view.findViewById(R.id.iv_user_image);
        tvUserName = view.findViewById(R.id.tv_user_name);
        Button btnEditProfile = view.findViewById(R.id.btn_edit_profile);
        vAccountDetailsMobile = view.findViewById(R.id.inc_account_details_mobile_number);
        vAccountDetailsVpa = view.findViewById(R.id.inc_account_details_vpa);
        vAccountDetailsUserName = view.findViewById(R.id.inc_account_details_username);
        tvLogout = vAccountDetailsUserName.findViewById(R.id.tv_item_logout);
        ivLogoutImage = vAccountDetailsUserName.findViewById(R.id.iv_item_logout_icon);
        ImageView ivCardImage = vAccountDetailsVpa.findViewById(R.id.iv_item_casual_list_icon);
        progressBar = view.findViewById(R.id.pb_profile);
        btnEditProfile.setOnClickListener(v -> {
            Toast.makeText(getContext(), getResources().getString(R.string.feature_coming_soon),
                    Toast.LENGTH_SHORT).show();
        });
        ivCardImage.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), NFCAndroidBeamActivity.class);
            startActivity(intent);
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
        showMobile(userMobileNumber);
        showVpa(VPA);
        showUserName(userName);
    }

    public void showVpa(String vpa) {
        String[] vpaArray = vpa.split("\\+");
        String userVPA = vpaArray[1].substring(2) + "@hdfc";
        ((ImageView) vAccountDetailsVpa.findViewById(R.id.iv_item_casual_list_icon))
                .setImageDrawable(getResources().getDrawable(R.drawable.ic_transaction));
        ((TextView) vAccountDetailsVpa.findViewById(R.id.tv_item_casual_list_title))
                .setText(userVPA);
        ((TextView) vAccountDetailsVpa.findViewById(R.id.tv_item_casual_list_subtitle))
                .setText(getResources().getString(R.string.vpa));
    }

    public void showMobile(String mobile) {
        String mobileNumber = "+91 - " + mobile.substring(3);
        ((ImageView) vAccountDetailsMobile.findViewById(R.id.iv_item_casual_list_icon))
                .setImageDrawable(getResources().getDrawable(R.drawable.ic_mobile));
        ((TextView) vAccountDetailsMobile.findViewById(R.id.tv_item_casual_list_title))
                .setText(mobileNumber);
        ((TextView) vAccountDetailsMobile.findViewById(R.id.tv_item_casual_list_subtitle))
                .setText(getResources().getString(R.string.mobile));
    }

    public void showUserName(String userName) {
        ((ImageView) vAccountDetailsUserName.findViewById(R.id.iv_item_casual_list_icon))
                .setImageDrawable(getResources().getDrawable(R.drawable.ic_user));
        ((TextView) vAccountDetailsUserName.findViewById(R.id.tv_item_casual_list_title))
                .setText(userName);
        ((TextView) vAccountDetailsUserName.findViewById(R.id.tv_item_casual_list_subtitle))
                .setText(getResources().getString(R.string.user_name));
        ((TextView) vAccountDetailsUserName.findViewById(R.id.tv_item_logout))
                .setText(getResources().getString(R.string.logout));
        ivLogoutImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_logout_button_icon));
        tvLogout.setOnClickListener(v -> {
            if (doubleBackToExitPressedOnce) {
                signOut();
                Toast.makeText(getContext(), getResources().getString(R.string.signing_out),
                        Toast.LENGTH_SHORT).show();
                return;
            }
            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(getContext(), "press again to logout session!", Toast.LENGTH_SHORT).show();
            new Handler().postDelayed((Runnable) () -> doubleBackToExitPressedOnce = false, 2000);

        });
        ivLogoutImage.setOnClickListener(v -> {
            if (doubleBackToExitPressedOnce) {
                Toast.makeText(getContext(), getResources().getString(R.string.signing_out),
                        Toast.LENGTH_SHORT).show();
                signOut();
                return;
            }
            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(getContext(), "press again to logout session!", Toast.LENGTH_SHORT).show();
            new Handler().postDelayed((Runnable) () -> doubleBackToExitPressedOnce = false, 2000);
        });

    }

    public void signOut() {
        FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(getActivity(), RegisterActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
    }
}
