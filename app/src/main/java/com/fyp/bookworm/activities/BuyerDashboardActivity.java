package com.fyp.bookworm.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.fyp.bookworm.R;
import com.fyp.bookworm.fragments.CartFragment;
import com.fyp.bookworm.fragments.FavoriteFragment;
import com.fyp.bookworm.fragments.HomeFragment;
import com.fyp.bookworm.fragments.SettingsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.razorpay.PaymentResultListener;

public class BuyerDashboardActivity extends AppCompatActivity implements PaymentResultListener {

    BottomNavigationView bottomNavigationView;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buyer_dashboard);

        bottomNavigationView = findViewById(R.id._userBottomNavigation);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id._containerFrame, new HomeFragment()).commit();
        }

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @SuppressLint({"NonConstantResourceId", "UseCompatLoadingForDrawables"})
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                // Unselect all items before selecting the current one
                Menu menu = bottomNavigationView.getMenu();
                for (int i = 0; i < menu.size(); i++) {
                    MenuItem menuItem = menu.getItem(i);
                    menuItem.setIcon(getDrawable(getInactiveIcon(menuItem.getItemId())));
                }

                // Select the current item
                item.setIcon(getDrawable(getActiveIcon(item.getItemId())));

                if (item.getItemId() == R.id._home_Bottom_navigation_icon) {
                    getSupportFragmentManager().beginTransaction().replace(R.id._containerFrame, new HomeFragment()).commit();
                } else if (item.getItemId() == R.id._favorite_bottom_navigation_icon) {
                    getSupportFragmentManager().beginTransaction().replace(R.id._containerFrame, new FavoriteFragment()).commit();
                } else if (item.getItemId() == R.id._cart_bottom_navigation_icon) {
                    getSupportFragmentManager().beginTransaction().replace(R.id._containerFrame, new CartFragment()).commit();
                } else if (item.getItemId() == R.id._setting_bottom_navigation_icon) {
                    getSupportFragmentManager().beginTransaction().replace(R.id._containerFrame, new SettingsFragment()).commit();
                } else {
                    Toast.makeText(BuyerDashboardActivity.this, "Invalid position", Toast.LENGTH_SHORT).show();
                }

                return true;
            }
        });
    }

    // Helper method to get the active icon resource ID
    private int getActiveIcon(int itemId) {
        if (itemId == R.id._home_Bottom_navigation_icon) {
            return R.drawable.baseline_home_24;
        } else if (itemId == R.id._favorite_bottom_navigation_icon) {
            return R.drawable._favorite_24;
        } else if (itemId == R.id._cart_bottom_navigation_icon) {
            return R.drawable.baseline_cart_24;
        } else if (itemId == R.id._setting_bottom_navigation_icon) {
            return R.drawable.baseline_settings_24;
        } else {
            return 0;
        }
    }

    // Helper method to get the inactive icon resource ID
    private int getInactiveIcon(int itemId) {
        if (itemId == R.id._home_Bottom_navigation_icon) {
            return R.drawable._home_24;
        } else if (itemId == R.id._favorite_bottom_navigation_icon) {
            return R.drawable._favorite_border_24;
        } else if (itemId == R.id._cart_bottom_navigation_icon) {
            return R.drawable._cart_24;
        } else if (itemId == R.id._setting_bottom_navigation_icon) {
            return R.drawable._settings_24;
        } else {
            return 0;
        }
    }

    @Override
    public void onPaymentSuccess(String s) {
        CartFragment cartFragment = (CartFragment) getSupportFragmentManager().findFragmentById(R.id._containerFrame);
        if (cartFragment != null && cartFragment.isVisible()) {
            cartFragment.onPaymentSuccess(s);
        } else {
            Toast.makeText(this, "Payment Successful, but CartFragment is not visible", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onPaymentError(int i, String s) {
        CartFragment cartFragment = (CartFragment) getSupportFragmentManager().findFragmentById(R.id._containerFrame);
        if (cartFragment != null && cartFragment.isVisible()) {
            cartFragment.onPaymentError(i, s);
        } else {
            Toast.makeText(this, "Payment Failed, but CartFragment is not visible", Toast.LENGTH_SHORT).show();
        }
    }
}
