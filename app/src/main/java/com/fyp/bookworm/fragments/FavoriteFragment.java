package com.fyp.bookworm.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fyp.bookworm.R;
import com.fyp.bookworm.adapters.MaterialAdapter;
import com.fyp.bookworm.models.MaterialModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FavoriteFragment extends Fragment {

    RecyclerView favoriteRecyclerView;
    MaterialAdapter materialAdapter;
    List<MaterialModel> materialModelList = new ArrayList<>();
    MaterialModel materialModel;
    DatabaseReference favouriteReference;
    FirebaseAuth firebaseAuth;

    public FavoriteFragment() {
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
        View view = inflater.inflate(R.layout.fragment_favorite, container, false);
        favoriteRecyclerView = view.findViewById(R.id._favoriteRecyclerView);
        firebaseAuth = FirebaseAuth.getInstance();
        favouriteReference = FirebaseDatabase.getInstance().getReference("Favourite");
        favoriteRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        materialAdapter = new MaterialAdapter(materialModelList, getContext(), R.layout.favorite_recycler_design);

        favouriteReference.child(firebaseAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                materialModelList.clear();
                if (snapshot.exists()) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        materialModel = dataSnapshot.getValue(MaterialModel.class);
                        materialModelList.add(materialModel);
                    }
                    favoriteRecyclerView.setAdapter(materialAdapter);
                    materialAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return view;
    }
}