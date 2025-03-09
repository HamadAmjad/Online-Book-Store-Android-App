package com.fyp.bookworm.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.fyp.bookworm.R;
import com.fyp.bookworm.models.AddToCartModel;
import com.fyp.bookworm.models.MaterialModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

public class DetailsActivity extends AppCompatActivity {
    ImageView detailsImage;
    TextView detailsName, detailsPrice, detailsLocation, detailsQuantity, detailsContact, detailsTotalPrice, detailsDecrease, detailsIncrease, detailsValue;
    Button btnCart;
    MaterialModel materialModel;
    String name, location, contact, sellerId, materialId;
    int price, quantity, totalPrice, totalValue = 0;
    ProgressDialog progressDialog;
    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        initializeUI();
        initializeFirebase();

        // Get and set data
        String data = getIntent().getStringExtra("data");
        if (data != null) {
            materialModel = new Gson().fromJson(data, MaterialModel.class);
            setData(materialModel);
        }

        // Increase
        detailsIncrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                increaseQuantity();
            }
        });

        // Decrease
        detailsDecrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                decreaseQuantity();
            }
        });

        btnCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addToCart();
            }
        });
    }

    private void initializeUI() {
        detailsImage = findViewById(R.id._detailsImage);
        detailsName = findViewById(R.id._detailsName);
        detailsPrice = findViewById(R.id._detailsPrice);
        detailsLocation = findViewById(R.id._detailsLocation);
        detailsQuantity = findViewById(R.id._detailsQuantity);
        detailsContact = findViewById(R.id._detailsContact);
        detailsTotalPrice = findViewById(R.id._detailsTotalPrice);
        detailsDecrease = findViewById(R.id._detailsDecrease);
        detailsIncrease = findViewById(R.id._detailsIncrease);
        detailsValue = findViewById(R.id._detailsValue);
        btnCart = findViewById(R.id._btnCart);
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Add to Cart");
        progressDialog.setMessage("Please Wait");
    }

    private void initializeFirebase() {
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("Cart");
    }

    private void setData(MaterialModel materialModel) {
        name = materialModel.getName();
        price = Integer.parseInt(materialModel.getPrice());
        location = materialModel.getCity();
        quantity = Integer.parseInt(materialModel.getQuantity());
        contact = materialModel.getContactNo();
        sellerId = materialModel.getUid();
        materialId = materialModel.getId();

        Picasso.get().load(materialModel.getImage()).into(detailsImage);
        detailsName.setText(name);
        detailsPrice.setText(String.valueOf(price));
        detailsLocation.setText(location);
        detailsQuantity.setText(String.valueOf(quantity));
        detailsContact.setText(contact);
        detailsValue.setText(String.valueOf(totalValue));
    }

    private void increaseQuantity() {
        if (totalValue < quantity) {
            totalValue++;
            totalPrice = price * totalValue;
            detailsTotalPrice.setText(String.valueOf(totalPrice));
            detailsValue.setText(String.valueOf(totalValue));
        } else {
            Toast.makeText(DetailsActivity.this, "Limit Exceeded", Toast.LENGTH_SHORT).show();
        }
    }

    private void decreaseQuantity() {
        if (totalValue > 0) {
            totalValue--;
            totalPrice = price * totalValue;
            detailsTotalPrice.setText(String.valueOf(totalPrice));
            detailsValue.setText(String.valueOf(totalValue));
        }
    }

    private void addToCart() {
        if (totalValue == 0) {
            Toast.makeText(DetailsActivity.this, "Add Quantity", Toast.LENGTH_SHORT).show();
        } else {
            progressDialog.show();
            String id = databaseReference.push().getKey();
            String uid = firebaseAuth.getCurrentUser().getUid();
            AddToCartModel addToCartModel = new AddToCartModel(name, materialId, sellerId, materialModel.getImage(), id, uid, totalPrice, totalValue);
            databaseReference.child(uid).child(id).setValue(addToCartModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    Toast.makeText(DetailsActivity.this, "Added", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                    resetUI();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(DetailsActivity.this, "Not Added: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            });
        }
    }

    private void resetUI() {
        detailsTotalPrice.setText("0");
        detailsValue.setText("0");
        totalValue = 0;
        totalPrice = 0;
    }
}
