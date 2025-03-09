package com.fyp.bookworm.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.fyp.bookworm.R;
import com.fyp.bookworm.activities.UpdateMaterialActivity;
import com.fyp.bookworm.models.CategoryModel;
import com.fyp.bookworm.models.MaterialModel;
import com.google.gson.Gson;

import java.util.List;

public class ManageBooksAdapter extends RecyclerView.Adapter<ManageBooksAdapter.ViewHolder> {

    List<MaterialModel> materialModelList;
    Context context;

    public ManageBooksAdapter(List<MaterialModel> materialModelList, Context context) {
        this.materialModelList = materialModelList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.manage_books_design, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MaterialModel materialModel = materialModelList.get(position);
        holder.name.setText(materialModel.getName());
        Glide.with(context).load(materialModel.getImage()).into(holder.image);
        holder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, UpdateMaterialActivity.class);
                intent.putExtra("Data", new Gson().toJson(materialModel));
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return materialModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        ImageView image, edit, delete;
        TextView name;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id._manageBookImage);
            name = itemView.findViewById(R.id._manageBookName);
            edit = itemView.findViewById(R.id._manageBookEdit);

        }
    }
}
