package com.fyp.bookworm.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.fyp.bookworm.R;

public class SellerDashboardActivity extends AppCompatActivity {

    CardView manageBooks, manageOrders, settings, profile;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
        finish();
    }

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_dashboard);

        manageBooks = findViewById(R.id._manageBooks);
        manageBooks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SellerDashboardActivity.this,ManageBooksActivity.class));
            }
        });

        manageOrders = findViewById(R.id._manageOrder);
        manageOrders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SellerDashboardActivity.this,ManageOrdersActivity.class));
            }
        });

        settings = findViewById(R.id._sellerSettings);
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SellerDashboardActivity.this,SellerSettingsActivity.class));
                finish();
            }
        });

        profile = findViewById(R.id._sellerProfile);
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SellerDashboardActivity.this,ProfileActivity.class));
            }
        });

    }
}