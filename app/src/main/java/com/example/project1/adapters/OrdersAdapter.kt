package com.example.project1.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.project1.R
import com.example.project1.data.Order
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.util.jar.Attributes

class OrdersAdapter (options: FirebaseRecyclerOptions<Order>) : FirebaseRecyclerAdapter<Order, OrdersAdapter.MyViewHolder>(options)  {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflater  = LayoutInflater.from(parent.context)
        return MyViewHolder(inflater, parent)
    }
    override fun onBindViewHolder(holder: MyViewHolder, position: Int, model: Order) {

        // sets the appropriate values to the widgets
        val storRef: StorageReference = FirebaseStorage.getInstance().getReferenceFromUrl(model.Cart[0].Image)
        Glide.with(holder.image.context).load(storRef).into(holder.image)

        holder.name.text = model.ID
        holder.price.text = "$"+String.format("%.2f",model.Total)
        holder.orderDate.text = model.Date.get("dayOfWeek").toString().lowercase().capitalize()+ ", "+model.Date.get("dayOfMonth").toString()+ " "+model.Date.get("month").toString().lowercase().capitalize()+ " "+model.Date.get("year").toString()

        var NameTxt = ""
        var count = 0;
        for(item in model.Cart){
            if(count!=0){
                NameTxt+=",\n"
            }
            NameTxt += (item.Name +" (${item.Quantity}) ")
            count++
        }
        holder.name.text = NameTxt
    }
    class MyViewHolder(inflater: LayoutInflater, parent: ViewGroup)
        : RecyclerView.ViewHolder(inflater.inflate(R.layout.order_row, parent, false))
    {
        val image: ImageView = itemView.findViewById(R.id.productImage)
        val name: TextView = itemView.findViewById(R.id.productNames)
        val price: TextView = itemView.findViewById(R.id.amountPaid)
        val orderDate:TextView = itemView.findViewById(R.id.orderDate)

    }


}