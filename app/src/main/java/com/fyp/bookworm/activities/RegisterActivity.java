package com.fyp.bookworm.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.fyp.bookworm.R;
import com.fyp.bookworm.models.RegisterModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    EditText registerEmail,registerName,registerPhone,registerAddress,registerPassword;
    Button registerBtn;
    RadioButton radioSellerButton,radioBuyerButton;
    TextView loginTxt;
    ProgressDialog progressDialog;
    RegisterModel registerModel;
    String role;
    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        registerEmail=findViewById(R.id._registerEmail);
        registerName=findViewById(R.id._registerName);
        registerPhone=findViewById(R.id._registerPhone);
        registerAddress=findViewById(R.id._registerAddress);
        registerPassword=findViewById(R.id._registerPassword);
        registerBtn=findViewById(R.id._register_createBtn);
        loginTxt=findViewById(R.id._loginText);
        radioSellerButton=findViewById(R.id._radioSellerBtn);
        radioBuyerButton=findViewById(R.id._radioBuyerBtn);
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Please Wait");

        firebaseAuth=FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("Register");

        loginTxt.setOnClickListener(v -> {
            startActivity(new Intent(RegisterActivity.this,LoginActivity.class));
            finish();
        });

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email,name,phone,address,password;
                email=registerEmail.getText().toString();
                name=registerName.getText().toString();
                phone=registerPhone.getText().toString();
                address=registerAddress.getText().toString();
                password=registerPassword.getText().toString();
                if (email.isEmpty())
                {
                    Toast.makeText(RegisterActivity.this, "Please Enter Email", Toast.LENGTH_SHORT).show();
                    return;
                }
                else if (name.isEmpty())
                {
                    Toast.makeText(RegisterActivity.this, "Please Enter Name", Toast.LENGTH_SHORT).show();
                    return;
                }
                else if (phone.isEmpty())
                {
                    Toast.makeText(RegisterActivity.this, "Please Enter Phone Number", Toast.LENGTH_SHORT).show();
                    return;
                }else if (address.isEmpty())
                {
                    Toast.makeText(RegisterActivity.this, "Please Enter Address", Toast.LENGTH_SHORT).show();
                    return;
                }
                else if (password.isEmpty())
                {
                    Toast.makeText(RegisterActivity.this, "Please Enter Password", Toast.LENGTH_SHORT).show();
                    return;
                }
                else if (!(radioSellerButton.isChecked()| radioBuyerButton.isChecked()))
                {
                    Toast.makeText(RegisterActivity.this, "Please Select a Role", Toast.LENGTH_SHORT).show();
                    return;
                }
                else{
                    progressDialog.show();
                    if (radioSellerButton.isChecked()){
                        role="Seller";
                    } else if (radioBuyerButton.isChecked()){
                        role="Buyer";
                    }

                    firebaseAuth.createUserWithEmailAndPassword(email,password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            String id=authResult.getUser().getUid();
                            registerModel = new RegisterModel(email,name,phone,address,password,role,id,"default");
                            databaseReference.child(id).setValue(registerModel);
                            if (role.equals("Buyer")) {
                                startActivity(new Intent(RegisterActivity.this, BuyerDashboardActivity.class));
                                finish();
                            } else {
                                startActivity(new Intent(RegisterActivity.this, SellerDashboardActivity.class));
                                finish();
                            }
                            progressDialog.dismiss();
                            Toast.makeText(RegisterActivity.this, "Register Successfully", Toast.LENGTH_SHORT).show();
                            registerEmail.setText("");
                            registerName.setText("");
                            registerPhone.setText("");
                            registerAddress.setText("");
                            registerPassword.setText("");
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(RegisterActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                    });
                }

            }
        });
        
    }
}