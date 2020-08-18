package com.example.wallet;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.wallet.ui.utils.GlobalVariables;
import com.example.wallet.ui.wallet.WalletFragment;
import com.example.wallet.ui.home.HomeFragment;
import com.example.wallet.ui.TransactionHistory.TransactionHistoryFragment;
import com.example.wallet.ui.profile.ProfileFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;


public class BottomNavigator extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bottom_navigator);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);
        //I added this if statement to keep the selected fragment when rotating the device
        if (savedInstanceState == null) {
            String fragmentName = getIntent().getStringExtra("fragmentName");
            if (fragmentName != null && !fragmentName.isEmpty()) {
                switch (fragmentName) {
                    case "history":
                        bottomNav.getMenu().findItem(R.id.action_payments).setChecked(true);
                        getSupportFragmentManager().beginTransaction().replace(R.id.bottom_navigation_fragment_container,
                                new TransactionHistoryFragment()).commit();
                        break;

                    case "wallet":
                        bottomNav.getMenu().findItem(R.id.action_wallet).setChecked(true);
                        getSupportFragmentManager().beginTransaction().replace(R.id.bottom_navigation_fragment_container,
                                new WalletFragment()).commit();
                        break;

                    case "profile":
                        bottomNav.getMenu().findItem(R.id.action_profile).setChecked(true);
                        getSupportFragmentManager().beginTransaction().replace(R.id.bottom_navigation_fragment_container,
                                new ProfileFragment()).commit();
                        break;

                    default:
                        bottomNav.getMenu().findItem(R.id.action_home).setChecked(true);
                        getSupportFragmentManager().beginTransaction().replace(R.id.bottom_navigation_fragment_container,
                                new HomeFragment()).commit();
                }
            } else {
                bottomNav.getMenu().findItem(R.id.action_home).setChecked(true);
                getSupportFragmentManager().beginTransaction().replace(R.id.bottom_navigation_fragment_container,
                        new HomeFragment()).commit();
            }
        }


    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            item -> {
                Fragment selectedFragment = null;

                switch (item.getItemId()) {
                    case R.id.action_home:
                        selectedFragment = new HomeFragment();
                        break;
                    case R.id.action_payments:
                        selectedFragment = new TransactionHistoryFragment();
                        break;
                    case R.id.action_wallet:
                        selectedFragment = new WalletFragment();
                        break;
                    case R.id.action_profile:
                        selectedFragment = new ProfileFragment();
                        break;

                }
                if(selectedFragment!=null) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.bottom_navigation_fragment_container,
                            selectedFragment).commit();
                }
                else {
                    Toast.makeText(getApplication(), "No Item selected!",
                            Toast.LENGTH_SHORT).show();
                }

                return true;
            };

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        GlobalVariables globalVariables = (GlobalVariables)getApplication();
        if (currentUser != null) {
            if (currentUser.getDisplayName() != null && !currentUser.getDisplayName().isEmpty()){
                globalVariables.setUserName(currentUser.getDisplayName());
            } else {
                FirebaseAuth.getInstance().signOut();
            }
        }
    }

}
