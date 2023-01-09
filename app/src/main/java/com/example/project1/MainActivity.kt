package com.example.project1

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.Gravity
import android.widget.*
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.denzcoskun.imageslider.ImageSlider
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.interfaces.ItemClickListener
import com.denzcoskun.imageslider.models.SlideModel
import com.example.project1.data.CartItem
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase

class MainActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private var menuBtn: ImageView? = null
    private var cartBtn: ImageView?= null
    private var imageSlider: ImageSlider?= null
    private var drawerLayout: DrawerLayout?= null
    private var navView: NavigationView?= null
    private var shopNowBtn: Button?= null
    private var cartTotal:TextView?= null
    private var searchView: SearchView?= null
    private var currentUser: FirebaseUser?= null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_main)

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
        setupImageSlider()


        setupNavigationMenu()
    }

    private fun setupNavigationMenu(){
        drawerLayout?.let { navView?.let { it1 -> BurgerMenu(this, it, it1) } }
    }




    private fun initializeScreenWidgets(){
        //this function gets reference of all widgets on this activity and initializes them
        menuBtn = findViewById(R.id.menuButton)
        imageSlider = findViewById(R.id.imageSlider)
        drawerLayout = findViewById(R.id.drawer_layout_main_activity)
        navView = findViewById(R.id.navigationView)
        shopNowBtn = findViewById(R.id.shop_now)
        cartBtn = findViewById(R.id.cartButton)
        cartTotal = findViewById(R.id.numCartItems)
        searchView = findViewById(R.id.searchBar)
    }

    private fun setClickListeners(){
        menuBtn?.setOnClickListener {
            drawerLayout?.openDrawer(Gravity.LEFT)
        }

        cartBtn?.setOnClickListener {
            val intent = Intent(this,CartActivity::class.java)
            startActivity(intent)
        }

        shopNowBtn?.setOnClickListener{
            val intent = Intent(this,ProductsActivity::class.java)
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

    private  fun setupImageSlider(){
        val imageList = ArrayList<SlideModel>()
        imageList.add(SlideModel(R.drawable.ps52))
        imageList.add(SlideModel(R.drawable.xbox1))
        imageList.add(SlideModel(R.drawable.fifa))
        imageSlider?.setImageList(imageList,ScaleTypes.CENTER_CROP)

        // to do: oN CLICKLISTENERS ON EACH ITEM
        imageSlider?.setItemClickListener(object : ItemClickListener {
            override fun onItemSelected(position: Int) {
                if(position == 0){
                    intent = Intent(this@MainActivity,ProductDetailsActivity::class.java)
                    intent.putExtra("Name","PS5 Black Edition")
                    intent.putExtra("Description","The PS5 console which now comes in black, unleashes new gaming possibilities that you never anticipated. Experience lightning fast loading with an ultra-high speed SSD, deeper immersion with support for haptic feedback, adaptive triggers, and 3D Audio, and an all-new generation of incredible PlayStation games.")
                    intent.putExtra("Price","399.95")
                    intent.putExtra("Rating","4.77")
                    intent.putExtra("Image","gs://project1-ecommerce-501ef.appspot.com/ps5black6.jpeg")
                    startActivity(intent);
                }
                else if(position == 1){
                    intent = Intent(this@MainActivity,ProductDetailsActivity::class.java)
                    intent.putExtra("Name","XBox OneX with 1 controller")
                    intent.putExtra("Description","The Xbox One is a home video game console developed by Microsoft. Announced in May 2013, it is the successor to Xbox 360 and the third base console in the Xbox series of video game consoles. It was first released in North America, parts of Europe, Australia, and South America in November 2013 and in Japan, China, and other European countries in September 2014. ")
                    intent.putExtra("Price","400.99")
                    intent.putExtra("Rating","3.5")
                    intent.putExtra("Image","gs://project1-ecommerce-501ef.appspot.com/xboxonexproduct1.jpg")
                    startActivity(intent)
                }
                else if(position == 2){
                    intent = Intent(this@MainActivity,ProductDetailsActivity::class.java)
                    intent.putExtra("Name","Fifa 23 ( XBox One)")
                    intent.putExtra("Description","It is the 30th and final installment in the FIFA series, and is scheduled to be released worldwide on 30 September 2022 for PC, Nintendo Switch, PlayStation 4, PlayStation 5, Xbox One, Xbox Series X/S and Google Stadia.")
                    intent.putExtra("Price","65.99")
                    intent.putExtra("Rating","4.5")
                    intent.putExtra("Image","gs://project1-ecommerce-501ef.appspot.com/fifa23.jpeg")
                    startActivity(intent)
                }
            }
        })
    }

    override fun onStart() {
        super.onStart()
        updateCartItemCount();

        searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(Query: String?): Boolean {
                val intent = Intent(this@MainActivity,ProductsActivity::class.java)
                intent.putExtra("SearchProduct",Query)
                startActivity(intent)
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                return false
            }
        })
    }



}


