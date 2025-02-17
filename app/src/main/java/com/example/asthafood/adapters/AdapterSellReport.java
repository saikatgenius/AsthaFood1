package com.example.asthafood.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.example.asthafood.R;
import com.example.asthafood.mssql.models.SetGetSellReport;

import java.util.ArrayList;

public class AdapterSellReport extends RecyclerView.Adapter<AdapterSellReport.DueReportViewHolder> {

    private Context context;
    private ArrayList<SetGetSellReport> arrayList;

    public AdapterSellReport(Context context, ArrayList<SetGetSellReport> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public DueReportViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_sell_report, parent, false);
        return new DueReportViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DueReportViewHolder holder, int position) {
        holder.mTv_productNameTextView.setText(arrayList.get(position).getItemName());
        holder.mTv_amountTextView.setText(arrayList.get(position).getSalePrice());
        holder.mTv_sellDateTextView.setText(arrayList.get(position).getSaleDate());
        holder.mTv_buyerNameTextView.setText(arrayList.get(position).getCoustomerName());
        holder.mTv_quantity.setText(arrayList.get(position).getQuantity());
       // holder.mTv_productDetails.setText(arrayList.get(position).getSaleDate());
        holder.mTv_productIdTextView.setText(arrayList.get(position).getItemID());
        holder.mTv_batchno.setText(arrayList.get(position).getBatchNo());



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
        TextView mTv_batchno;
        TextView mTv_quantity;


        public DueReportViewHolder(View itemView) {
            super(itemView);

            mTv_productNameTextView = itemView.findViewById(R.id.productNameTextView);
            mTv_productDetails = itemView.findViewById(R.id.productDetails);
            mTv_productIdTextView = itemView.findViewById(R.id.productIdTextView);
            mTv_batchno = itemView.findViewById(R.id.batchno);
            mTv_sellDateTextView = itemView.findViewById(R.id.sellDateTextView);
            mTv_amountTextView = itemView.findViewById(R.id.amountTextView);
            mTv_buyerNameTextView = itemView.findViewById(R.id.buyerNameTextView);
            mTv_quantity = itemView.findViewById(R.id.quantity);

        }
    }
}
