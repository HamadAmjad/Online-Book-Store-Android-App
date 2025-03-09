package com.fyp.bookworm.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.fyp.bookworm.R;
import com.fyp.bookworm.activities.DetailsActivity;
import com.fyp.bookworm.models.MaterialModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.List;

public class MaterialAdapter extends RecyclerView.Adapter<MaterialAdapter.ViewHolder>{

    List<MaterialModel> materialModelList;
    Context context;
    private final int layoutId;
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    DatabaseReference favouriteReference;
    boolean check = false;


    public MaterialAdapter(List<MaterialModel> materialModelList, Context context) {
        this(materialModelList, context, R.layout.material_design);
    }

    public MaterialAdapter(List<MaterialModel> materialModelList, Context context, int layoutId) {
        this.materialModelList = materialModelList;
        this.context = context;
        this.layoutId = layoutId;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(layoutId, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MaterialModel materialModel = materialModelList.get(position);
        Glide.with(context).load(materialModel.getImage()).into(holder.imageView);
        holder.txtTitle.setText(materialModel.getName());
        holder.txtPrice.setText(materialModel.getPrice());

        //clickListener
        holder.materialCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, DetailsActivity.class);
                intent.putExtra("data", new Gson().toJson(materialModel));
                context.startActivity(intent);
            }
        });

        //Favorites
        favouriteReference = FirebaseDatabase.getInstance().getReference("Favourite");
        favouriteReference.child(firebaseAuth.getCurrentUser().getUid()).child(materialModel.getId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    holder.designFavorite.setBackgroundResource(R.drawable._favorite_24);
                } else {
                    holder.designFavorite.setBackgroundResource(R.drawable._favorite_border_24);
                }
                holder.designFavorite.setOnClickListener(v -> {
                    if (!snapshot.exists()) {
                        favouriteReference.child(firebaseAuth.getCurrentUser().getUid()).child(materialModel.getId()).setValue(materialModel);
                        Toast.makeText(context, "Add Successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        favouriteReference.child(firebaseAuth.getCurrentUser().getUid()).child(materialModel.getId()).removeValue();
                        Toast.makeText(context, "Remove Successfully", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    public int getItemCount() {
        return materialModelList.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void filterList(List<MaterialModel> filterList) {
        materialModelList = filterList;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        ImageView imageView, designFavorite;
        TextView txtTitle, txtPrice;
        CardView materialCard;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.designImage);
            txtTitle = itemView.findViewById(R.id.designTitle);
            txtPrice = itemView.findViewById(R.id.designPrice);
            designFavorite = itemView.findViewById(R.id.designFavorite);
            materialCard = itemView.findViewById(R.id._designCard);

        }
    }

}
