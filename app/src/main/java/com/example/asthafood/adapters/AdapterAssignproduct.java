package com.example.asthafood.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.asthafood.R;
import com.example.asthafood.mssql.models.SetGetAssignProduct;
import com.example.asthafood.mssql.models.SetGetSellReport;

import java.util.ArrayList;

public class AdapterAssignproduct extends RecyclerView.Adapter<AdapterAssignproduct.DueReportViewHolder> {

    private Context context;
    private ArrayList<SetGetAssignProduct> arrayList;

    public AdapterAssignproduct(Context context, ArrayList<SetGetAssignProduct> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public DueReportViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_assign_product, parent, false);
        return new DueReportViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DueReportViewHolder holder, int position) {
        holder.mTv_productNameTextView.setText(arrayList.get(position).getProductName());
        holder.mTv_productDetails.setText(arrayList.get(position).getProductDetails());
        holder.mTv_productIdTextView.setText(arrayList.get(position).getProductId());
        holder.mTv_billNumberTextView.setText(arrayList.get(position).getBillNo());
        holder.mTv_sellDateTextView.setText(arrayList.get(position).getDate());
        holder.mTv_amountTextView.setText(arrayList.get(position).getAmount());
        holder.mTv_buyerNameTextView.setText(arrayList.get(position).getBuyer());
        holder.mTv_quantity.setText(arrayList.get(position).getProductQuantity());
        holder.mTv_sell_quantity.setText(arrayList.get(position).getSellQnty());
        holder.mTv_rem_quantity.setText(arrayList.get(position).getReamingQuenty());
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    class DueReportViewHolder extends RecyclerView.ViewHolder {

        TextView mTv_productNameTextView;
        TextView mTv_productDetails;
        TextView mTv_productIdTextView;
        TextView mTv_billNumberTextView;
        TextView mTv_sellDateTextView;
        TextView mTv_amountTextView;
        TextView mTv_buyerNameTextView;
        TextView mTv_quantity;
        TextView mTv_sell_quantity;
        TextView mTv_rem_quantity;


        public DueReportViewHolder(View itemView) {
            super(itemView);

            mTv_productNameTextView = itemView.findViewById(R.id.productNameTextView);
            mTv_productDetails = itemView.findViewById(R.id.productDetails);
            mTv_productIdTextView = itemView.findViewById(R.id.productIdTextView);
            mTv_billNumberTextView = itemView.findViewById(R.id.billNumberTextView);
            mTv_sellDateTextView = itemView.findViewById(R.id.sellDateTextView);
            mTv_amountTextView = itemView.findViewById(R.id.amountTextView);
            mTv_buyerNameTextView = itemView.findViewById(R.id.buyerNameTextView);
            mTv_quantity = itemView.findViewById(R.id.quantity);
            mTv_sell_quantity= itemView.findViewById(R.id.sell_quenty);
          mTv_rem_quantity= itemView.findViewById(R.id.assing_Qnty);


        }
    }
}
