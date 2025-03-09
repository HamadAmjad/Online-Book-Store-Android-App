package com.fyp.bookworm.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fyp.bookworm.R;
import com.fyp.bookworm.models.MaterialModel;
import com.fyp.bookworm.models.OrderModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ManageOrderAdapter extends RecyclerView.Adapter<ManageOrderAdapter.ViewHolder> {

    ArrayList<OrderModel> orderModelArrayList;
    Context context;
    DatabaseReference orderReference = FirebaseDatabase.getInstance().getReference("Order");
    DatabaseReference booksReference = FirebaseDatabase.getInstance().getReference("Product");

    public ManageOrderAdapter(ArrayList<OrderModel> orderModelArrayList, Context context) {
        this.orderModelArrayList = orderModelArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.manage_order_design, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OrderModel orderModel = orderModelArrayList.get(position);
        holder.name.setText("Name: " + orderModel.getOrderName());
        holder.price.setText("Price: " + orderModel.getPrice() + "");
        holder.quantity.setText("Quantity: " + orderModel.getQuantity() + "");
        holder.date.setText("Date: " + orderModel.getDate());
        holder.payment.setText("Payment: " + orderModel.getPayment());
        holder.status.setText("Status: " + orderModel.getStatus());
        Picasso.get().load(orderModel.getImage()).into(holder.orderImg);
        if (orderModel.getStatus().equals("Confirmed") || orderModel.getStatus().equals("Reject")) {
            holder.confirm.setVisibility(View.GONE);
            holder.confirm.setClickable(false);
            holder.confirm.setEnabled(false);
            holder.cancel.setVisibility(View.GONE);
            holder.cancel.setClickable(false);
            holder.cancel.setEnabled(false);
        }

        holder.confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (orderModel.getStatus().equals("Confirmed")) {
                    holder.confirm.setVisibility(View.GONE);
                    holder.confirm.setClickable(false);
                    holder.confirm.setEnabled(false);
                    Toast.makeText(context, "Already Confirm", Toast.LENGTH_SHORT).show();
                } else {
                    booksReference.child(orderModel.getProductId()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                MaterialModel materialModel = snapshot.getValue(MaterialModel.class);
                                int quantity = Integer.parseInt(materialModel.getQuantity()) - orderModel.getQuantity();
                                materialModel.setQuantity(quantity + "");
                                booksReference.child(orderModel.getProductId()).setValue(materialModel);

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                    orderReference.child(orderModel.getOrderId()).child("status").setValue("Confirmed");
                    Toast.makeText(context, "Order Confirm", Toast.LENGTH_SHORT).show();
                }

            }
        });

        holder.cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (orderModel.getStatus().equals("Reject")) {
                    holder.cancel.setVisibility(View.GONE);
                    holder.cancel.setClickable(false);
                    holder.cancel.setEnabled(false);
                    Toast.makeText(context, "Already Cancelled", Toast.LENGTH_SHORT).show();
                } else {
                    orderReference.child(orderModel.getOrderId()).child("status").setValue("Reject");
                    Toast.makeText(context, "Cancel Order", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    @Override
    public int getItemCount() {
        return orderModelArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView orderImg;
        Button confirm, cancel;
        TextView name, price, quantity, date, payment, status;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            orderImg = itemView.findViewById(R.id.manageOrderImage);
            confirm = itemView.findViewById(R.id.manageOrderConfirmBtn);
            cancel = itemView.findViewById(R.id.manageOrderCancelBtn);
            name = itemView.findViewById(R.id.manageOrderName);
            price = itemView.findViewById(R.id.manageOrderPrice);
            quantity = itemView.findViewById(R.id.manageOrderQuantity);
            date = itemView.findViewById(R.id.manageOrderDate);
            payment = itemView.findViewById(R.id.manageOrderPayment);
            status = itemView.findViewById(R.id.manageOrderStatus);
        }
    }

}
