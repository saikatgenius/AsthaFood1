package com.example.asthafood.adapters

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.asthafood.R
import com.example.asthafood.Util.GlobalReqProductList
import com.example.asthafood.Util.ReqProductList
import com.example.asthafood.mssql.models.ItemCategoryItems
import com.example.asthafood.mssql.models.SellProductDetailsModel

class CategoryItemsAdapter (val items: ArrayList<ItemCategoryItems>, val context: Context) :
    RecyclerView.Adapter<CategoryItemsViewHolder>()   {



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = CategoryItemsViewHolder (
        LayoutInflater.from(parent.context)
            .inflate(R.layout.row_req_item_pro, parent, false)
    )

    override fun onBindViewHolder(holder: CategoryItemsViewHolder, position: Int) {

        holder.Name.text = items[position].productName.toString()
        holder.Code.text = items[position].batchNo.toString()
        holder.Price.text = "₹" + items[position].price.toString()

        holder.AddPro.setOnClickListener{
            if(holder.Qnty.text.toString().isNotEmpty() && holder.Qnty.text.toString().toDouble()>0.0 ){
                val reqProductList  =  ReqProductList();
                reqProductList.id = items[position].productID.toString()
                reqProductList.name = items[position].productName.toString()
                reqProductList.batchNo = items[position].batchNo.toString()
                reqProductList.price = items[position].price.toString()
                reqProductList.qunt = holder.Qnty.text.toString().toDouble()
                GlobalReqProductList.ReqData.add(reqProductList)
                Toast.makeText(context, "Product Added to Request List", Toast.LENGTH_LONG).show()
                holder.imageClick.visibility = View.VISIBLE
                holder.buttonText.text = "Added"
            }else{
                Toast.makeText(context, "Please Enter Quantity", Toast.LENGTH_LONG).show()
                holder.Qnty.error = "Please Enter Quantity"
            }


        }

        holder.Qnty.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
            }

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
            }

            override fun afterTextChanged(editable: Editable) {
                holder.imageClick.visibility = View.GONE
                holder.buttonText.text = "Add To Queue"
                GlobalReqProductList.ReqData.removeIf { it.id ==items[position].productID }
            }
        })

    }

    override fun getItemCount() = items.size

    override fun getItemViewType(position: Int) = position

    override fun getItemId(position: Int) = position.toLong()
}

class CategoryItemsViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    var Name = itemView.findViewById<TextView?>(R.id.tv_name12)
    var Code = itemView.findViewById<TextView?>(com.example.asthafood.R.id.Code)
    var Price = itemView.findViewById<TextView?>(R.id.priceEmp)
    var Qnty = itemView.findViewById<EditText?>(R.id.etSellingQnty)
    var AddPro = itemView.findViewById<RelativeLayout?>(R.id.add)


    var imageClick = itemView.findViewById<ImageView?>(com.example.asthafood.R.id.imageClick)
    var buttonText = itemView.findViewById<TextView?>(com.example.asthafood.R.id.buttonText)



}