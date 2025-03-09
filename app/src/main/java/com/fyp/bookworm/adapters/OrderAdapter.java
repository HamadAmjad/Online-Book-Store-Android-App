package com.fyp.bookworm.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fyp.bookworm.R;
import com.fyp.bookworm.models.OrderModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ViewHolder> {

    ArrayList<OrderModel> orderModelArrayList;
    Context context;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Order");

    public OrderAdapter(ArrayList<OrderModel> orderModelArrayList, Context context) {
        this.orderModelArrayList = orderModelArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.order_design, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        OrderModel orderModel = orderModelArrayList.get(position);
        holder.name.setText("Name: " + orderModel.getOrderName());
        holder.price.setText("Price: " + orderModel.getPrice() + "");
        holder.quantity.setText("Quantity: " + orderModel.getQuantity() + "");
        holder.date.setText("Date: " + orderModel.getDate() + "");
        holder.payment.setText("Payment: " + orderModel.getPayment() + "");
        holder.status.setText("Status: " + orderModel.getStatus() + "");
        Picasso.get().load(orderModel.getImage()).into(holder.image);

        if (orderModel.getStatus().equals("In Process")) {
            holder.cancel.setVisibility(View.VISIBLE);
        }

        if (orderModel.getStatus().equals("Confirmed")) {
            holder.feedback.setVisibility(View.VISIBLE);
        }

        holder.cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                databaseReference.child(orderModel.getOrderId()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, orderModelArrayList.size());
                        notifyItemChanged(position);
                    }
                });


            }
        });
    }

    @Override
    public int getItemCount() {
        return orderModelArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView name, price, quantity, date, status, payment;
        ImageView image;
        Button cancel, feedback;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.orderName);
            price = itemView.findViewById(R.id.orderPrice);
            quantity = itemView.findViewById(R.id.orderQuantity);
            date = itemView.findViewById(R.id.orderDate);
            status = itemView.findViewById(R.id.orderStatus);
            payment = itemView.findViewById(R.id.orderPayment);
            image = itemView.findViewById(R.id.orderImage);
            cancel = itemView.findViewById(R.id.orderCancelBtn);
            feedback = itemView.findViewById(R.id.feedBackBtn);

        }
    }

}
