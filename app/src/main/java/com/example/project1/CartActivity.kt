package com.example.project1

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.project1.adapters.CartAdapter
import com.example.project1.adapters.ProductsAdapter
import com.example.project1.data.CartItem
import com.example.project1.data.Product
import com.example.project1.interfaces.CartInterface
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase

class CartActivity : AppCompatActivity(),CartInterface  {
    private var backBtn: ImageView?= null
    private var cartRecycler: RecyclerView?= null
    private var cartTotalLbl: TextView?= null
    private var checkOutBtn: Button?= null

    private var cartTotal: Double?= null

    private var adapter: CartAdapter? = null

    private var currentUser: FirebaseUser?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_cart)

        currentUser = FirebaseAuth.getInstance().currentUser
        initializeScreenWidgets()
        setClickListeners()

        displayCart()
    }

    private fun displayCart(){
        val userID = currentUser?.uid
        val query = FirebaseDatabase.getInstance().reference.child("users/$userID/").child("cart")
        val options = FirebaseRecyclerOptions.Builder<CartItem>().setQuery(query, CartItem::class.java).build()
        adapter = CartAdapter(options)
        adapter?.setCartInterface(this@CartActivity)
//        Log.i("options1",options.toString());
        cartRecycler?.layoutManager = LinearLayoutManager(this)
        cartRecycler?.adapter = adapter

    }

    private fun initializeScreenWidgets(){
        backBtn = findViewById(R.id.backButton)
        cartRecycler = findViewById(R.id.cartRecyclerView)
        cartTotalLbl = findViewById(R.id.cart_total_label)
        checkOutBtn = findViewById(R.id.check_out_button)
    }

    private fun setClickListeners(){
        backBtn?.setOnClickListener {
            finish()
        }

        checkOutBtn?.setOnClickListener {
            if(cartTotal != null) {
                //take user to checkout page
                val intent = Intent(this,CheckoutActivity::class.java)
                intent.putExtra("CartTotal",cartTotal)
                startActivity(intent)
            }
        }
    }

    override fun quantityChanged(cartItem: CartItem,quantity:Int){
//        Log.i("CartActivity","quantity: $quantity")
//        Log.i("CartActivity","cartItem Name: ${cartItem.Name}")

        val userID = currentUser?.uid

        //update product in DB
        FirebaseDatabase.getInstance().reference.child("users/$userID/").child("cart").child(cartItem.Name).child("Quantity").setValue(quantity)

    }

    override fun deleteItemFromCart(item: CartItem) {
        val userID = currentUser?.uid
        FirebaseDatabase.getInstance().reference.child("users/$userID/").child("cart").child(item.Name).removeValue()
    }

    override fun updateCartTotal() {
        val userID = currentUser?.uid
        var total:Double = 0.0
        //calculating cart total
        FirebaseDatabase.getInstance().reference.child("users/$userID/").child("cart").get().addOnCompleteListener{
            if(it.isSuccessful()){
                var itemCount = 0
                for (item in it.result.children){
                    var product = item.getValue(CartItem::class.java)
                    total+=(product!!.Quantity*product.Price)
                    itemCount++
                }
                if(itemCount==0){
                    cartTotalLbl?.text ="Cart is empty"
                    cartTotal = null
                }
                else{
                    cartTotal = String.format("%.2f",total).toDouble()
                    cartTotalLbl?.text = "$"+cartTotal
                }
            }
        }
    }




    override fun onStart() {
        super.onStart()
        adapter?.startListening()
    }
}