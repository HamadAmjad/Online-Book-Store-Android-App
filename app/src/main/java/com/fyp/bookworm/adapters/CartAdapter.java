package com.fyp.bookworm.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fyp.bookworm.R;
import com.fyp.bookworm.models.AddToCartModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {

    private Context context;
    private List<AddToCartModel> addToCartModelList;
    private DatabaseReference databaseReference;
    private String userId;

    public CartAdapter(Context context, List<AddToCartModel> addToCartModelList) {
        this.context = context;
        this.addToCartModelList = addToCartModelList;
        this.databaseReference = FirebaseDatabase.getInstance().getReference("Cart");
        this.userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.cart_recycler_design, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        AddToCartModel addToCartModel = addToCartModelList.get(position);
        holder.name.setText(addToCartModel.getName());
        holder.price.setText("Price: " + addToCartModel.getPrice());
        holder.quantity.setText("Quantity: " + addToCartModel.getQuantity());
        Picasso.get().load(addToCartModel.getImage()).into(holder.image);

        holder.remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                databaseReference.child(userId).child(addToCartModel.getId()).removeValue();
                addToCartModelList.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, addToCartModelList.size());
            }
        });
    }

    @Override
    public int getItemCount() {
        return addToCartModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, price, quantity;
        ImageView image, remove;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.designCartName);
            price = itemView.findViewById(R.id.designCartPrice);
            quantity = itemView.findViewById(R.id.designCartQuantity);
            image = itemView.findViewById(R.id.designCartImage);
            remove = itemView.findViewById(R.id.designRemoveCart);
        }
    }
}