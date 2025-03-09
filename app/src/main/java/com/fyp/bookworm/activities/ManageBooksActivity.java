package com.fyp.bookworm.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.fyp.bookworm.R;
import com.fyp.bookworm.adapters.ManageBooksAdapter;
import com.fyp.bookworm.models.MaterialModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ManageBooksActivity extends AppCompatActivity {

    RecyclerView manageBooksRecyclerView;
    FloatingActionButton addNewBooksButton;
    MaterialModel materialModel;
    ManageBooksAdapter manageBooksAdapter;
    List<MaterialModel> materialModelList = new ArrayList<>();
    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_books);

        manageBooksRecyclerView = findViewById(R.id._manageBooksRecyclerView);
        manageBooksRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        manageBooksAdapter = new ManageBooksAdapter(materialModelList, this);
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("Books");

        //Add new books
        addNewBooksButton = findViewById(R.id._addNewBooksFloatingBtn);
        addNewBooksButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ManageBooksActivity.this, AddBookActivity.class));
            }
        });

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    materialModelList.clear();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        materialModel = dataSnapshot.getValue(MaterialModel.class);
                        if (materialModel.getUid().equals(firebaseAuth.getCurrentUser().getUid())) {
                            materialModelList.add(materialModel);
                        }
                    }
                } else {
                    Toast.makeText(ManageBooksActivity.this, "Database is empty please add data first", Toast.LENGTH_SHORT).show();
                }
                manageBooksRecyclerView.setAdapter(manageBooksAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}