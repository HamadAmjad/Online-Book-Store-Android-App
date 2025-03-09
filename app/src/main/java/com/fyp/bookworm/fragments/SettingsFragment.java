package com.fyp.bookworm.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fyp.bookworm.R;
import com.fyp.bookworm.activities.BuyerDashboardActivity;
import com.fyp.bookworm.activities.ChangePasswordActivity;
import com.fyp.bookworm.activities.ContactUsActivity;
import com.fyp.bookworm.activities.LoginActivity;
import com.fyp.bookworm.activities.OrderActivity;
import com.fyp.bookworm.activities.SellerSettingsActivity;
import com.google.firebase.auth.FirebaseAuth;

public class SettingsFragment extends Fragment {

    TextView orders, changePassword, contactUs, logout;

    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        //orders
        orders = view.findViewById(R.id._settingOrderHistory);
        orders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(requireContext(), OrderActivity.class));
            }
        });

        //Change Password
        changePassword = view.findViewById(R.id._settingChangePassword);
        changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(requireContext(), ChangePasswordActivity.class));
            }
        });

        //contact us
        contactUs = view.findViewById(R.id._settingContactUs);
        contactUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(requireContext(), ContactUsActivity.class));
            }
        });

        //Logout
        logout = view.findViewById(R.id._logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(requireContext(), LoginActivity.class));
                requireActivity().finish();
            }
        });

        return view;
    }
}