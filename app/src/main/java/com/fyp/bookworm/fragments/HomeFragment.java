package com.fyp.bookworm.fragments;

import static java.util.Locale.filter;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.fyp.bookworm.R;
import com.fyp.bookworm.activities.ManageBooksActivity;
import com.fyp.bookworm.activities.MaterialActivity;
import com.fyp.bookworm.activities.ProfileActivity;
import com.fyp.bookworm.adapters.CategoryAdapter;
import com.fyp.bookworm.adapters.MaterialAdapter;
import com.fyp.bookworm.models.CategoryModel;
import com.fyp.bookworm.models.MaterialModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.nio.file.attribute.GroupPrincipal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class HomeFragment extends Fragment {

    RecyclerView categoryRecyclerView, materialRecyclerView;
    CategoryAdapter categoryAdapter;
    MaterialAdapter materialAdapter;
    MaterialModel materialModel;
    List<CategoryModel> categoryModelList = new ArrayList<>();
    List<MaterialModel> materialModelList = new ArrayList<>();
    ImageView profileIcon;
    TextView seeAllTxt;
    EditText searchBar;
    DatabaseReference databaseReference;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Categories
        categoryModelList.add(new CategoryModel(R.drawable.book_worm_logo,"Books"));
        categoryModelList.add(new CategoryModel(R.drawable.book_worm_logo,"Newspapers"));
        categoryModelList.add(new CategoryModel(R.drawable.book_worm_logo,"Thesis"));

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        databaseReference = FirebaseDatabase.getInstance().getReference("Books");

        //Category RecyclerView
        categoryRecyclerView = view.findViewById(R.id._categoriesRecyclerView);
        categoryRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        categoryAdapter = new CategoryAdapter(categoryModelList, getContext());
        categoryRecyclerView.setAdapter(categoryAdapter);

        //Material RecyclerView
        materialRecyclerView = view.findViewById(R.id._materialRecyclerView);
        materialRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        materialAdapter = new MaterialAdapter(materialModelList, getContext());

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
                    Toast.makeText(requireContext(), "Database is empty please add data first", Toast.LENGTH_SHORT).show();
                }
                materialRecyclerView.setAdapter(materialAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //Profile Click Listener
        profileIcon = view.findViewById(R.id.profileIcon);
        profileIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(requireContext(), ProfileActivity.class));
            }
        });

        //seeAll
        seeAllTxt = view.findViewById(R.id._seeAll);
        seeAllTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(requireContext(), MaterialActivity.class));
            }
        });

        //Search

        searchBar=view.findViewById(R.id._search);
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                filter(s.toString());
            }
        });

        return view;
    }

    private void filter(String text) {
        if (text == null)
            return;
        if (text.isEmpty())
            return;
        List<MaterialModel> filterList = new ArrayList<>();
        for (MaterialModel items : materialModelList) {
            boolean isExist = items.getName().toLowerCase().contains(text.toLowerCase()) || items.getCity().toLowerCase(Locale.ROOT).contains(text.toLowerCase(Locale.ROOT));
            if (isExist) {
                filterList.add(items);
            }
        }
        if (filterList.isEmpty()) {
            filterList.addAll(materialModelList);
        }
        materialAdapter.filterList(filterList);
    }
}