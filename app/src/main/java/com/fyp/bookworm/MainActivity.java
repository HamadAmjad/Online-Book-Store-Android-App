package com.fyp.bookworm;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.fyp.bookworm.activities.BuyerDashboardActivity;
import com.fyp.bookworm.activities.LoginActivity;
import com.fyp.bookworm.activities.SellerDashboardActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    DatabaseReference userReference = FirebaseDatabase.getInstance().getReference("Register");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (firebaseAuth.getCurrentUser() != null) {
                    CheckAccessLevel(firebaseAuth.getCurrentUser().getUid());
                } else {
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    finish();
                }

            }
        }, 3000);

    }

    private void CheckAccessLevel(String id) {
        userReference.child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String role = snapshot.child("role").getValue().toString();
                if (role.equals("Buyer")) {
                    startActivity(new Intent(MainActivity.this, BuyerDashboardActivity.class));
                    finish();
                } else {
                    startActivity(new Intent(MainActivity.this, SellerDashboardActivity.class));
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

}