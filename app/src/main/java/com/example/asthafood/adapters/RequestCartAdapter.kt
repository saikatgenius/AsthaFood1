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

class RequestCartAdapter(val items: ArrayList<ReqProductList>, val context: Context) :
    RecyclerView.Adapter<RequestCartAdapterViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        RequestCartAdapterViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.row_req_cart, parent, false)
        )

    override fun onBindViewHolder(holder: RequestCartAdapterViewHolder, position: Int) {

        holder.Name.text = items[position].name.toString()
        holder.Code.text = items[position].id.toString()
        holder.Qnty.text = items[position].qunt.toString()

        holder.AddPro.setOnClickListener {
            items.removeIf { it.id ==items[position].id }
            notifyDataSetChanged()
        }


    }

    override fun getItemCount() = items.size

    override fun getItemViewType(position: Int) = position

    override fun getItemId(position: Int) = position.toLong()


}

class RequestCartAdapterViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    var Name: TextView = itemView.findViewById<TextView?>(R.id.tv_name12)
    var Code = itemView.findViewById<TextView?>(com.example.asthafood.R.id.Code)
    var Qnty = itemView.findViewById<TextView?>(com.example.asthafood.R.id.etSellingQnty)
    var AddPro = itemView.findViewById<RelativeLayout?>(com.example.asthafood.R.id.add)
}