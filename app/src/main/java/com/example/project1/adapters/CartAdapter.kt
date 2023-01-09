package com.example.project1.adapters

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.project1.ProductDetailsActivity
import com.example.project1.R
import com.example.project1.data.CartItem
import com.example.project1.data.Product
import com.example.project1.interfaces.CartInterface
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class CartAdapter(options: FirebaseRecyclerOptions<CartItem>) : FirebaseRecyclerAdapter<CartItem, CartAdapter.MyViewHolder>(options)  {

    private lateinit var mCartInterface: CartInterface

    fun setCartInterface(crtIntrfce: CartInterface){
        mCartInterface = crtIntrfce
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflater  = LayoutInflater.from(parent.context)
        return MyViewHolder(inflater, parent)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int, model: CartItem) {

        // sets the appropriate values to the widgets
        val storRef: StorageReference = FirebaseStorage.getInstance().getReferenceFromUrl(model.Image)
        Glide.with(holder.image.context).load(storRef).into(holder.image)

        holder.name.text = model.Name;
        holder.price.text = "$"+model.Price.toString();
        holder.quantity.setSelection(model.Quantity-1,false)
        holder.calcPrice.text = "$"+String.format("%.2f",model.Price*model.Quantity)

        holder.quantity.onItemSelectedListener = object :AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long){
                if(position!=RecyclerView.NO_POSITION){
                    val quantity:Int = position+1
//                    holder.calcPrice.text = "$"+(model.Price*quantity)
//                cartInterface?.quantityChanged(model,quantity)
                    mCartInterface.quantityChanged(model,quantity)
                }

            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }

        holder.deleteBtn.setOnClickListener {
            mCartInterface.deleteItemFromCart(model)
        }

    }
    class MyViewHolder(inflater: LayoutInflater, parent: ViewGroup)
        : RecyclerView.ViewHolder(inflater.inflate(R.layout.cart_item_row, parent, false))
    {
        val image: ImageView = itemView.findViewById(R.id.productImage)
        val name: TextView = itemView.findViewById(R.id.productName)
        val calcPrice: TextView = itemView.findViewById(R.id.calc_prod_price)
        val price: TextView = itemView.findViewById(R.id.productPrice)
        val quantity: Spinner = itemView.findViewById(R.id.quantitySpinner)
        val deleteBtn: ImageButton = itemView.findViewById(R.id.deleteButton)
    }

    override fun onDataChanged() {
        super.onDataChanged()
//        Log.i("CartAdapter","Data Changed")
        mCartInterface.updateCartTotal()
    }



}