package com.fyp.bookworm.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.fyp.bookworm.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class ChangePasswordActivity extends AppCompatActivity {

    EditText currentPassword, newPassword, confirmNewPassword;
    Button changePasswordBtn;
    DatabaseReference databaseReference;
    String dbPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        currentPassword = findViewById(R.id._currentPassword);
        newPassword = findViewById(R.id._newPassword);
        confirmNewPassword = findViewById(R.id._confirmNewPassword);
        changePasswordBtn = findViewById(R.id._changePasswordBtn);

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference("Register").child(uid);

        //get current password
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                dbPassword = snapshot.child("password").getValue(String.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        changePasswordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String cPass, newPass, cNewPass;
                cPass = currentPassword.getText().toString();
                newPass = newPassword.getText().toString();
                cNewPass = confirmNewPassword.getText().toString();

                if (cPass.isEmpty()) {
                    Toast.makeText(ChangePasswordActivity.this, "Please Enter Current Password", Toast.LENGTH_SHORT).show();
                    currentPassword.requestFocus();
                    return;
                }
                if (newPass.isEmpty()) {
                    Toast.makeText(ChangePasswordActivity.this, "Please Enter New Password", Toast.LENGTH_SHORT).show();
                    newPassword.requestFocus();
                    return;
                }
                if (cNewPass.isEmpty()) {
                    Toast.makeText(ChangePasswordActivity.this, "Please Confirm New Password", Toast.LENGTH_SHORT).show();
                    confirmNewPassword.requestFocus();
                    return;
                }
                if (!cNewPass.equals(newPass)) {
                    Toast.makeText(ChangePasswordActivity.this, "Password not match", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!cPass.equals(dbPassword)) {
                    Toast.makeText(ChangePasswordActivity.this, "Incorrect Current Password", Toast.LENGTH_SHORT).show();
                    return;
                }

                ChangePassword(newPass);
            }
        });

    }

    private void ChangePassword(String newPass) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("password", newPass);

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        firebaseUser.updatePassword(newPass).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    databaseReference.updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(ChangePasswordActivity.this, "Change Password Successfully", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(ChangePasswordActivity.this, "Something wrong", Toast.LENGTH_SHORT).show();
                        }
                    });

                } else {
                    Toast.makeText(ChangePasswordActivity.this, "Please Login again and Then change password", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}