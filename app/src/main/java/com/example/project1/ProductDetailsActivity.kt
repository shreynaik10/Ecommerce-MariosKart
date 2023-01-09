package com.example.project1

import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.project1.data.Product
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.util.*
import kotlin.collections.HashMap

class ProductDetailsActivity : AppCompatActivity() {
    var prodImage: ImageView?= null
    var prodName: TextView?= null
    var prodRating: RatingBar?= null
    var prodPrice: TextView?= null
    var prodDescription: TextView?= null
    private var backBtn: ImageView?= null
    private var addToCartBtn: Button?= null
    private var product:Product?= null
    private var currentUser:FirebaseUser?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_product_details)

        initializeScreenWidgets()
        setClickListeners()

        product = getProductFromIntent()
        product?.let { displayProduct(it) }
        val userID = currentUser?.uid
        FirebaseDatabase.getInstance().reference.child("users/$userID/").child("cart").child(product!!.Name).get().addOnSuccessListener {
            if(it.exists()){
                addToCartBtn?.text = "Item already in cart"
            }
        }
    }

    private fun getProductFromIntent():Product{
        val name = intent.getStringExtra("Name")
        val description = intent.getStringExtra("Description")
        val price = intent.getStringExtra("Price")
        val rating = intent.getStringExtra("Rating")
        val image = intent.getStringExtra("Image")
        return Product(name.toString(),description.toString(),image.toString(),price!!.toDouble(),rating!!.toDouble())
    }

    private fun displayProduct(item:Product){
//        //getting product details from intent
//        val name = intent.getStringExtra("Name")
//        val description = intent.getStringExtra("Description")
//        val price = intent.getStringExtra("Price")
//        val rating = intent.getStringExtra("Rating")
//        val image = intent.getStringExtra("Image")

        val name = item.Name
        val description = item.Description
        val price = item.Price
        val rating = item.Rating
        val image = item.Image


        prodName?.text = name
        prodDescription?.text = description
        prodPrice?.text = "$"+price.toString()
        prodRating?.rating = rating.toString().toFloat()


        val storRef: StorageReference = FirebaseStorage.getInstance().getReferenceFromUrl(image.toString())
        Glide.with(this).load(storRef).into(prodImage!!)
    }

    private fun setClickListeners(){
        backBtn?.setOnClickListener {
            finish()
        }

        addToCartBtn?.setOnClickListener{
//            Toast.makeText(this,"Add to cart Clicked",Toast.LENGTH_SHORT).show()
            val prod = getProductFromIntent()
            val userID = currentUser?.uid
            FirebaseDatabase.getInstance().reference.child("users/$userID/").child("cart").child(prod.Name).get().addOnSuccessListener {
                if(it.exists()){
//                    Toast.makeText(this,"Found item",Toast.LENGTH_SHORT).show()
                    Log.i("ProductDetailsActivity", "addToCartBtn- Found item")
                    Toast.makeText(this,"Item already in cart",Toast.LENGTH_LONG).show();
                }
                else{ //adding product to cart if it is not inside cart already
                    addProductToCart(prod,currentUser!!)
                }

            }.addOnFailureListener {
                Log.i("ProductDetailsActivity", "Error getting data: ${it.toString()}")
            }
        }
    }


    private fun initializeScreenWidgets(){
        //this function gets reference of all widgets on this activity and initializes them
        backBtn = findViewById(R.id.backButton)
        addToCartBtn = findViewById(R.id.add_to_cart)
        prodImage = findViewById(R.id.productImage)
        prodName = findViewById(R.id.prodName)
        prodRating = findViewById(R.id.prodRating)
        prodPrice = findViewById(R.id.prodPrice)
        prodDescription = findViewById(R.id.prodDescription)
        currentUser = FirebaseAuth.getInstance().currentUser
    }

    private fun addProductToCart(prod: Product,userID:FirebaseUser){
        val userID = currentUser?.uid
        Log.i("ProductDetailsActivity", "addToCartBtn- Could not find item")
        val data = HashMap<String,Any>()
        data["Name"] = prod.Name
        data["Description"] =prod.Description
        data["Price"] = prod.Price
        data["Quantity"] = 1
        data["Image"] = prod.Image
        FirebaseDatabase.getInstance().reference.child("users/$userID/").child("cart").child(prod.Name).setValue(data).addOnSuccessListener {
            Toast.makeText(this,"Added to cart successfully",Toast.LENGTH_SHORT).show()
            addToCartBtn?.text = "Added to cart successfully"
        }
            .addOnFailureListener {
                Toast.makeText(this,"Sorry! Could not add to cart.Please try again",Toast.LENGTH_SHORT).show()
            }
    }
}