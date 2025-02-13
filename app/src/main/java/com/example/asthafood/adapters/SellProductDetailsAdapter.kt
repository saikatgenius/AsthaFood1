package com.example.asthafood.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.asthafood.R
import com.example.asthafood.mssql.models.SellProductDetailsModel

class SellProductDetailsAdapter(val items: List<SellProductDetailsModel>, val context: Context) :
    RecyclerView.Adapter<SellProductViewHolder>()  {


     override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = SellProductViewHolder(
         LayoutInflater.from(parent.context)
             .inflate(R.layout.row_assi_selling_pro, parent, false)
     )

     @SuppressLint("SuspiciousIndentation")
     override fun onBindViewHolder(holder: SellProductViewHolder, position: Int) {

         holder.Name.text = items[position].productName.toString()
         holder.Code.text = items[position].productID.toString()
         holder.stock.text = items[position].remainingQuntity.toString()
         holder.Price.text = items[position].price.toString()

         holder.SellQnty.addTextChangedListener(object : TextWatcher {
             override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
             }

             override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
             }

             override fun afterTextChanged(editable: Editable) {
                 if (holder.SellQnty.getText().toString().isNotBlank()) {
                     if(holder.SellQnty.getText().toString().toInt()<=items[position].remainingQuntity){
                         items[position].sellingQnty = holder.SellQnty.getText().toString().toInt()
                         items[position].sellingQntyFinalPrice = items[position].price.toDouble() * holder.SellQnty.getText().toString().toInt()
                     }else{
                         Toast.makeText(
                             context,
                             "Not Enough Quantity",
                             Toast.LENGTH_LONG
                         ).show()
                     }

                 } else {
                     items[position].sellingQnty =0
                     items[position].sellingQntyFinalPrice =0.0
                 }
             }
         })
     }

     override fun getItemCount() = items.size

     override fun getItemViewType(position: Int) = position

     override fun getItemId(position: Int) = position.toLong()
}

class SellProductViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    var Name:TextView = itemView.findViewById<TextView?>(R.id.tv_name)
    var Code = itemView.findViewById<TextView?>(R.id.Code)
    var stock = itemView.findViewById<TextView?>(R.id.stock)
    var Price = itemView.findViewById<TextView?>(R.id.price)
    var SellQnty = itemView.findViewById<EditText?>(R.id.etSellingQnty)



}