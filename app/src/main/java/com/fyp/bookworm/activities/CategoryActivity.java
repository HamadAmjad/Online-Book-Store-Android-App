package com.fyp.bookworm.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.fyp.bookworm.R;
import com.fyp.bookworm.adapters.MaterialAdapter;
import com.fyp.bookworm.models.MaterialModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class CategoryActivity extends AppCompatActivity {

    RecyclerView categoryRecyclerView;
    TextView textView;
    String name;
    MaterialAdapter materialAdapter;
    MaterialModel materialModel;
    List<MaterialModel> materialModelList = new ArrayList<>();
    DatabaseReference databaseReference;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        textView = findViewById(R.id._categoryText);
        Intent intent = getIntent();
        name = intent.getStringExtra("name");
        textView.setText(name);

        databaseReference = FirebaseDatabase.getInstance().getReference("Books");

        //Material RecyclerView
        categoryRecyclerView = findViewById(R.id._categoryRecyclerView);
        categoryRecyclerView.setLayoutManager(new GridLayoutManager(this,2));
        materialAdapter = new MaterialAdapter(materialModelList, this, R.layout.favorite_recycler_design);

        databaseReference.orderByChild("category").equalTo(name).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                materialModelList.clear();
                int counter = 0;
                if (snapshot.exists()) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        materialModel = dataSnapshot.getValue(MaterialModel.class);
                        materialModelList.add(materialModel);
                        counter++;
                    }
                    if (counter == 0) {
                        Toast.makeText(CategoryActivity.this, name + " material not available", Toast.LENGTH_SHORT).show();
                    }
                    categoryRecyclerView.setAdapter(materialAdapter);
                } else {
                    Toast.makeText(CategoryActivity.this, name + " material not available", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}