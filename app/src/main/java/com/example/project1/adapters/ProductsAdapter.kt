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
import com.example.project1.ProductDetailsActivity
import com.example.project1.R
import com.example.project1.data.Product
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class ProductsAdapter (options: FirebaseRecyclerOptions<Product>) : FirebaseRecyclerAdapter<Product, ProductsAdapter.MyViewHolder>(options)  {
//    private var listener:OnItemClickListener?= null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflater  = LayoutInflater.from(parent.context)
        return MyViewHolder(inflater, parent)
    }
    override fun onBindViewHolder(holder: MyViewHolder, position: Int, model: Product) {

        // sets the appropriate values to the widgets
        val storRef: StorageReference = FirebaseStorage.getInstance().getReferenceFromUrl(model.Image)
        Glide.with(holder.image.context).load(storRef).into(holder.image)

        holder.name.text = model.Name;
        holder.price.text = "$"+String.format("%.2f",model.Price);
        holder.rating.rating = model.Rating.toFloat()

        holder.itemView.setOnClickListener{
            var context:Context = it.context
            var intent = Intent(context,ProductDetailsActivity::class.java)
            intent.putExtra("Name",model.Name)
            intent.putExtra("Price",model.Price.toString())
            intent.putExtra("Rating",model.Rating.toString())
            intent.putExtra("Image",model.Image)
            intent.putExtra("Description",model.Description)
            context.startActivity(intent)
        }

    }
    class MyViewHolder(inflater: LayoutInflater, parent: ViewGroup)
        : RecyclerView.ViewHolder(inflater.inflate(R.layout.product_row, parent, false))
    {
        val image: ImageView = itemView.findViewById(R.id.productImage)
        val name: TextView = itemView.findViewById(R.id.product_name)
        val rating: RatingBar = itemView.findViewById(R.id.ratingBar)
        val price: TextView = itemView.findViewById(R.id.price)


    }

//    public interface OnItemClickListener{
//        fun onItemClick(documentSnapshot: String, position:Int)
//    }
//    public fun setOnItemClickListener(listnr:OnItemClickListener){
//        listener = listnr
//    }
}