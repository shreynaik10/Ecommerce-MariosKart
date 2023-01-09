package com.example.project1

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.Gravity
import android.widget.*
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.project1.adapters.ProductsAdapter
import com.example.project1.data.CartItem
import com.example.project1.data.Product
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import java.util.*
import kotlin.concurrent.schedule
import kotlin.concurrent.timer

class ProductsActivity : AppCompatActivity() {
    private var adapter: ProductsAdapter? = null
    private var menuBtn: ImageView? = null
    private var cartBtn: ImageButton? = null
    private var drawerLayout: DrawerLayout?= null
    private var navView: NavigationView?= null
    private  var searchView:SearchView? = null
    private var rView : RecyclerView?= null
    private var cartTotal: TextView?= null

    private lateinit var auth: FirebaseAuth
    private var currentUser: FirebaseUser?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_products)

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

        if(intent.getIntExtra("ProductType",0)==R.string.product_type_game){
//            Log.i("ProductsActivity","Game type")
//            Log.i("ProductsActivity",intent.getIntExtra("ProductType",0).toString())
//            Log.i("ProductsActivity",R.string.product_type_game.toString())
            displayAllGames()
        }
        else if(intent.getIntExtra("ProductType",0)==R.string.product_type_console){
//            Log.i("ProductsActivity","Console type")
//            Log.i("ProductsActivity",intent.getIntExtra("ProductType",0).toString())
//            Log.i("ProductsActivity",R.string.product_type_console.toString())
            displayAllConsoles()
        }
        else if(intent.getStringExtra("SearchProduct")!=null){
            Log.i("ProductsActivity",intent.getStringExtra("SearchProduct")!!)
            searchProduct(intent.getStringExtra("SearchProduct")!!)
        }
        else{
            Log.i("ProductsActivity","All type")
            displayAllProducts()
        }

    }

    private fun displayAllConsoles() {
        val query = FirebaseDatabase.getInstance().reference.child("products").orderByChild("Type").equalTo(getString(R.string.product_type_console))
//        val query = FirebaseDatabase.getInstance().reference.child("products").orderByChild("Name").startAt("P")
        val options = FirebaseRecyclerOptions.Builder<Product>().setQuery(query, Product::class.java).build()
        adapter = ProductsAdapter(options)
//        Log.i("options1",options.toString());
        rView?.layoutManager = LinearLayoutManager(this)
        rView?.adapter = adapter
    }

    private fun displayAllGames() {
        val query = FirebaseDatabase.getInstance().reference.child("products").orderByChild("Type").equalTo(getString(R.string.product_type_game))
        Log.i("ProductsActivity",FirebaseDatabase.getInstance().reference.child("products").child("Type").toString())
//        val query = FirebaseDatabase.getInstance().reference.child("products").orderByChild("Name").startAt("P")
        val options = FirebaseRecyclerOptions.Builder<Product>().setQuery(query, Product::class.java).build()
        adapter = ProductsAdapter(options)
//        Log.i("options1",options.toString());
        rView?.layoutManager = LinearLayoutManager(this)
        rView?.adapter = adapter
    }


    private fun displayAllProducts() {
        val query = FirebaseDatabase.getInstance().reference.child("products")
//        val query = FirebaseDatabase.getInstance().reference.child("products").orderByChild("Name").startAt("P")
        val options = FirebaseRecyclerOptions.Builder<Product>().setQuery(query, Product::class.java).build()
        adapter = ProductsAdapter(options)
//        Log.i("options1",options.toString());
        rView?.layoutManager = LinearLayoutManager(this)
        rView?.adapter = adapter
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
    private fun initializeScreenWidgets(){
        //this function gets reference of all widgets on this activity and initializes them
        menuBtn = findViewById(R.id.menuButton)
        drawerLayout = findViewById(R.id.drawer_layout_main_activity)
        navView = findViewById(R.id.navigationView)
        searchView = findViewById(R.id.searchBar)
        rView = findViewById(R.id.consoles_recycler_view)
        cartBtn = findViewById(R.id.cartButton)
        cartTotal = findViewById(R.id.numCartItems)
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

    private fun searchProduct(Query:String){
        val query = FirebaseDatabase.getInstance().reference.child("products").orderByChild("Name").startAt(Query?.uppercase()).endAt(Query?.lowercase()+ "\uf8ff")
        val options = FirebaseRecyclerOptions.Builder<Product>().setQuery(query, Product::class.java).build()
        Log.i("ProductsActivity","searchProduct options :${options.toString()}")
        if(options!=null) {
            Log.i("ProductsActivity","options not null ")
            adapter = ProductsAdapter(options)
            rView?.layoutManager = LinearLayoutManager(this)
            rView?.adapter = adapter
            adapter?.startListening()
        }
    }

    override fun onStart() {
        super.onStart()
        adapter?.startListening()
        updateCartItemCount()

        searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(Query: String?): Boolean {
//                Log.i("ProductsActivity","onQueryTextSubmit")
                searchProduct(Query!!)
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
//                Log.i("ProductsActivity","onQueryTextChange :$newText")
                if(TextUtils.isEmpty(newText?.trim())){
                    displayAllProducts()
                    adapter?.startListening()
                }
                return false
            }
        })
    }

}


