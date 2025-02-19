package com.example.asthafood.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.asthafood.R
import com.example.asthafood.Util.ReqProductList
import com.example.asthafood.mssql.models.SellProductDetailsModel

class PreviewItemAdapter (val items: ArrayList<SellProductDetailsModel>, val context: Context) :
    RecyclerView.Adapter<PreviewItemAdapterViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)= PreviewItemAdapterViewHolder (
        LayoutInflater.from(parent.context).inflate(R.layout.row_preview_items, parent, false)

    )

    override fun onBindViewHolder(holder: PreviewItemAdapterViewHolder, position: Int) {
        holder.Name.text = items[position].productName
        holder.code.text = items[position].productID
        holder.qunt.text = items[position].sellingQnty.toInt().toString()
        holder.proPrice.text = items[position].price
        holder.TotalPrice.text = items[position].sellingQntyFinalPrice.toString()
    }

    override fun getItemCount() = items.size

    override fun getItemViewType(position: Int) = position

    override fun getItemId(position: Int) = position.toLong()
}

class PreviewItemAdapterViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    var Name: TextView = itemView.findViewById<TextView?>(R.id.itemName)
    var code: TextView = itemView.findViewById<TextView?>(R.id.Code)
    var qunt: TextView = itemView.findViewById<TextView?>(R.id.etQnty)
    var proPrice: TextView = itemView.findViewById<TextView?>(R.id.pro_price)
    var TotalPrice: TextView = itemView.findViewById<TextView?>(R.id.total_price)

}