package com.example.asthafood.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.asthafood.IncActivities.IncRequestProductItems;
import com.example.asthafood.R;
import com.example.asthafood.mssql.models.ItemCategory;

import java.util.ArrayList;

public class AdapterIncItemCategory extends RecyclerView.Adapter<AdapterIncItemCategory.AdapterIncItemCategoryViewHolder>{

    private Context context;
    private ArrayList<ItemCategory> arrayList;

    public AdapterIncItemCategory(Context context, ArrayList<ItemCategory> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public AdapterIncItemCategory.AdapterIncItemCategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.row_category_list, parent, false);
        return new AdapterIncItemCategory.AdapterIncItemCategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterIncItemCategory.AdapterIncItemCategoryViewHolder holder, int position) {
        holder.mtv_categoryName.setText(arrayList.get(position).getCategoriName());
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, IncRequestProductItems.class);
                intent.putExtra("categoryNo",arrayList.get(position).getCategoriyNo());
                context.startActivity(intent);

            }
        });

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class AdapterIncItemCategoryViewHolder extends RecyclerView.ViewHolder{
        TextView mtv_categoryName;
        CardView cardView;
        public AdapterIncItemCategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            mtv_categoryName=  itemView.findViewById(R.id.tv_category_name);
            cardView = itemView.findViewById(R.id.cv_category);
        }
    }
}
