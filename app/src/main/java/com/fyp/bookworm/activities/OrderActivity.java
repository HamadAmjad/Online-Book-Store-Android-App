package com.fyp.bookworm.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Toast;

import com.fyp.bookworm.R;
import com.fyp.bookworm.adapters.OrderAdapter;
import com.fyp.bookworm.models.OrderModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class OrderActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    ArrayList<OrderModel> orderModelArrayList = new ArrayList<>();
    OrderAdapter adapter;
    FirebaseAuth auth;
    DatabaseReference orderReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        auth = FirebaseAuth.getInstance();
        orderReference = FirebaseDatabase.getInstance().getReference("Order");

        recyclerView = findViewById(R.id._orderRecyclerView);
        adapter = new OrderAdapter(orderModelArrayList, this);
        ReadOrders();

    }

    private void ReadOrders() {

        orderReference.orderByChild("uid").equalTo(auth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                orderModelArrayList.clear();
                if (snapshot.exists()) {
                    for (DataSnapshot data : snapshot.getChildren()) {
                        OrderModel orderModel = data.getValue(OrderModel.class);
                        orderModelArrayList.add(orderModel);

                    }
                    recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();

                } else {
                    Toast.makeText(OrderActivity.this, "Empty", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}