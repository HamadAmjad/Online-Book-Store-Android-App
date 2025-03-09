package com.fyp.bookworm.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;
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

public class MaterialActivity extends AppCompatActivity {

    RecyclerView materialRecyclerView;
    MaterialAdapter materialAdapter;
    MaterialModel materialModel;
    List<MaterialModel> materialModelList = new ArrayList<>();
    DatabaseReference databaseReference;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_material);

        databaseReference = FirebaseDatabase.getInstance().getReference("Books");

        //Material RecyclerView
        materialRecyclerView = findViewById(R.id._seeAllMaterialRecyclerView);
        materialRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        materialAdapter = new MaterialAdapter(materialModelList, this,R.layout.favorite_recycler_design);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    materialModelList.clear();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        materialModel = dataSnapshot.getValue(MaterialModel.class);
                        materialModelList.add(materialModel);
                    }
                } else {
                    Toast.makeText(MaterialActivity.this, "Database is empty please add data first", Toast.LENGTH_SHORT).show();
                }
                materialRecyclerView.setAdapter(materialAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}