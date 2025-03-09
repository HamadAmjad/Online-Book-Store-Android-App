package com.fyp.bookworm.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Toast;

import com.fyp.bookworm.R;
import com.fyp.bookworm.adapters.ManageOrderAdapter;
import com.fyp.bookworm.models.OrderModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ManageOrdersActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    ArrayList<OrderModel> orderModelArrayList = new ArrayList<>();
    ManageOrderAdapter adapter;
    FirebaseAuth auth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_orders);

        recyclerView=findViewById(R.id._manageOrderRecyclerView);
        adapter=new ManageOrderAdapter(orderModelArrayList,this);
        ReadManageOrder();

    }

    public void ReadManageOrder(){
        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference("Order");
        databaseReference.orderByChild("sellerId").equalTo(auth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                orderModelArrayList.clear();
                if (snapshot.exists()){
                    for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                        OrderModel orderModel=dataSnapshot.getValue(OrderModel.class);
                        orderModelArrayList.add(orderModel);
                    }
                    recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }
                else {
                    Toast.makeText(ManageOrdersActivity.this, "Empty", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}