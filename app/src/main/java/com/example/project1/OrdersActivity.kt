package com.example.project1

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.widget.ImageView
import android.widget.TextView
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.project1.adapters.CartAdapter
import com.example.project1.adapters.OrdersAdapter
import com.example.project1.adapters.ProductsAdapter
import com.example.project1.data.CartItem
import com.example.project1.data.Order
import com.example.project1.data.Product
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase

class OrdersActivity : AppCompatActivity() {

    private var menuBtn: ImageView ?= null
    private var cartBtn: ImageView ?= null
    private var cartTotal: TextView ?= null
    private var drawerLayout: DrawerLayout ?= null
    private var navView: NavigationView ?= null
    private var rView : RecyclerView ?= null

    private lateinit var auth: FirebaseAuth
    private var currentUser: FirebaseUser?= null

    private var adapter: OrdersAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_orders)
        supportActionBar?.hide()

        auth = FirebaseAuth.getInstance()
        if(auth.currentUser == null){
            startActivity(Intent(Intent(this,LoginActivity::class.java)))
            finish()
        }
        else{
            currentUser = auth.currentUser
        }


        initializeScreenWidgets()
        setClickListeners()
        setupNavigationMenu()

        displayAllOrders()
    }

    private fun displayAllOrders() {
        val userID = currentUser?.uid
        val query = FirebaseDatabase.getInstance().reference.child("users/$userID/").child("orders")
        val options = FirebaseRecyclerOptions.Builder<Order>().setQuery(query, Order::class.java).build()
        adapter = OrdersAdapter(options)
        Log.i("OrdersActivity",options.toString());
        rView?.layoutManager = LinearLayoutManager(this)
        rView?.adapter = adapter

//        query.get().addOnCompleteListener {
//            if(it.isSuccessful()){
//                Log.i("OrdersActivity","it exists")
//                for (item in it.result.children){
//                    var product = item.getValue(Order::class.java)
//                    Log.i("OrdersActivity",product.toString())
//                }
//            }
//        }
    }

    private fun setupNavigationMenu(){
        drawerLayout?.let { navView?.let { it1 -> BurgerMenu(this, it, it1) } }
    }
    private fun setClickListeners(){
        menuBtn?.setOnClickListener {
            drawerLayout?.openDrawer(Gravity.LEFT)
        }

        cartBtn?.setOnClickListener{
            val intent = Intent(this,CartActivity::class.java)
            startActivity(intent)
        }

    }

    private fun updateCartItemCount(){
        val userID = currentUser?.uid
        FirebaseDatabase.getInstance().reference.child("users/$userID/").child("cart").get().addOnCompleteListener{
            if(it.isSuccessful()){
                var itemCount = 0
                for (item in it.result.children){
                    var product = item.getValue(CartItem::class.java)
                    itemCount+=product!!.Quantity
                }
                cartTotal?.text = itemCount.toString()
            }
        }
    }

    private fun initializeScreenWidgets(){
        //this function gets reference of all widgets on this activity and initializes them
        menuBtn = findViewById(R.id.menuButton)
        cartBtn = findViewById(R.id.cartButton)
        cartTotal = findViewById(R.id.numCartItems)
        drawerLayout = findViewById(R.id.drawer_layout_main_activity)
        navView = findViewById(R.id.navigationView)

        rView = findViewById(R.id.ordersRecyclerView)
    }

    override fun onStart() {
        super.onStart()
        adapter?.startListening()
        updateCartItemCount()
    }
}