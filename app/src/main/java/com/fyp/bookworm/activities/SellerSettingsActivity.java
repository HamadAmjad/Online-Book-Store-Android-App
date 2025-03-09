package com.fyp.bookworm.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.fyp.bookworm.R;
import com.google.firebase.auth.FirebaseAuth;

public class SellerSettingsActivity extends AppCompatActivity {

    TextView changePassword, contactUs, logout;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(SellerSettingsActivity.this, SellerDashboardActivity.class));
        finish();
    }

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_settings);

        //Change Password
        changePassword = findViewById(R.id._sellerChangePassword);
        changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SellerSettingsActivity.this, ChangePasswordActivity.class));
            }
        });

        //contact us
        contactUs = findViewById(R.id._sellerContactUs);
        contactUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SellerSettingsActivity.this, ContactUsActivity.class));
            }
        });

        //Logout
        logout = findViewById(R.id._sellerLogout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(SellerSettingsActivity.this, LoginActivity.class));
                finish();
            }
        });

    }
}