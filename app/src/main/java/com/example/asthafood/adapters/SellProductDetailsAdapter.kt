package com.example.asthafood.adapters


import android.annotation.SuppressLint
import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.asthafood.R
import com.example.asthafood.activity.ReturnProductActivity

import com.example.asthafood.databinding.SellBottomSheetBinding
import com.example.asthafood.mssql.models.SellProductDetailsModel
import com.google.android.material.bottomsheet.BottomSheetDialog


class SellProductDetailsAdapter(val items: List<SellProductDetailsModel>, val context: Context,val Tag : String ) :
    RecyclerView.Adapter<SellProductViewHolder>()  {



     override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = SellProductViewHolder(
         LayoutInflater.from(parent.context)
             .inflate(R.layout.row_assi_selling_pro, parent, false)
     )

     @SuppressLint("SuspiciousIndentation", "ResourceAsColor")
     override fun onBindViewHolder(holder: SellProductViewHolder, position: Int) {


         if (Tag=="ReturnProduct"){
             holder.SellQnty.visibility= View.GONE
         }else{
             holder.SellQnty.visibility= View.VISIBLE
         }


         if (items[position].isNew=="1"){
             holder.mainLL.setBackgroundResource(R.drawable.green_white)
             holder.Name.setTextColor(ContextCompat.getColor(context, R.color.colorWhite))
             holder.newLL.visibility = View.VISIBLE

         }else{
             holder.mainLL.setBackgroundResource(R.drawable.border_background)
             holder.newLL.visibility = View.GONE
         }

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
                         var gstAmt:Double = 0.0
                         var sellAmt:Double = 0.0

                         items[position].sellingQnty = holder.SellQnty.getText().toString().toInt()
                         items[position].sellingQntyFinalPrice = items[position].price.toDouble() * holder.SellQnty.getText().toString().toInt()

                         gstAmt = ((items[position].price.toDouble() * holder.SellQnty.getText().toString().toInt()) * items[position].gst.toDouble()) / 100
                         sellAmt = (items[position].price.toDouble() * holder.SellQnty.getText().toString().toInt()) - gstAmt
                         items[position].sellPrice = sellAmt
                         items[position].gstPrice = gstAmt



                     }else{
                         Toast.makeText(context, "Not Enough Quantity", Toast.LENGTH_LONG).show()
                         holder.SellQnty.error = "Not Enough Quantity"
                     }

                 } else {
                     items[position].sellingQnty =0
                     items[position].sellingQntyFinalPrice =0.0
                 }
             }
         })


         holder.Name.setOnClickListener{
             val context: Context = context // Get the correct context
             val dialog = BottomSheetDialog(context)
             val binding: SellBottomSheetBinding = SellBottomSheetBinding.inflate(LayoutInflater.from(context))
             dialog.setContentView(binding.getRoot())

             binding.tvProductName.text = items[position].productName.toString()
             binding.productid.text = items[position].productID.toString()
             binding.tvProductPrice.text ="₹"+ items[position].mrp.toString()
             binding.tvSellPrice.text ="₹"+ items[position].price.toString()
             if (items[position].expiryDate.isNullOrEmpty()){
                 binding.expdate.text= "No Data"
             }else{
                 binding.expdate.text= items[position].expiryDate.toString()
             }

             binding.gst.text = items[position].gst.toString()+"%"
             binding.assingQnty.text=items[position].assingQnty.toString()
             binding.remainingQunt.text= items[position].remainingQuntity.toString()

             dialog.setCancelable(true)
             dialog.setCanceledOnTouchOutside(true)
             dialog.show()
         }

     }

     override fun getItemCount() = items.size

     override fun getItemViewType(position: Int) = position

     override fun getItemId(position: Int) = position.toLong()
}

class SellProductViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    var Name:TextView = itemView.findViewById<TextView?>(R.id.tv_name1)
    var Code = itemView.findViewById<TextView?>(com.example.asthafood.R.id.Code)
    var stock = itemView.findViewById<TextView?>(com.example.asthafood.R.id.stock)
    var Price = itemView.findViewById<TextView?>(R.id.price)
    var SellQnty = itemView.findViewById<EditText?>(com.example.asthafood.R.id.etSellingQnty)
    var mainLL = itemView.findViewById<LinearLayout?>(R.id.main_ll)
    var newLL = itemView.findViewById<RelativeLayout?>(R.id.newpro)



}