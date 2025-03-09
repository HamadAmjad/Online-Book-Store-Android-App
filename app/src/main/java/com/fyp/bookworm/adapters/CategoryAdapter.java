package com.fyp.bookworm.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.fyp.bookworm.R;
import com.fyp.bookworm.activities.CategoryActivity;
import com.fyp.bookworm.models.CategoryModel;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {

    List<CategoryModel> categoryModelList;
    Context context;

    public CategoryAdapter(List<CategoryModel> categoryModelList, Context context) {
        this.categoryModelList = categoryModelList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.category_design, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CategoryModel categoryModel = categoryModelList.get(position);
        Glide.with(context).load(categoryModel.getImage()).into(holder.imageView);
        holder.textView.setText(categoryModel.getName());

        holder.categoryCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String categoryName = categoryModel.getName();
                Intent intent = new Intent(context, CategoryActivity.class);
                intent.putExtra("name", categoryName);
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return categoryModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView textView;
        CardView categoryCard;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            categoryCard = itemView.findViewById(R.id.categoryDesignCard);
            imageView = itemView.findViewById(R.id.categoryDesignImage);
            textView = itemView.findViewById(R.id.categoryDesignText);

        }
    }

}
