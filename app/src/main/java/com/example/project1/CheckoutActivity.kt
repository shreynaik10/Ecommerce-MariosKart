package com.example.project1

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.project1.data.CartItem
import com.example.project1.data.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import java.time.LocalDateTime
import kotlin.streams.asSequence

class CheckoutActivity : AppCompatActivity() {
    private var cartTotal:Double ?= null
    private val shippingCharge:Int = 30

    private var firstName: EditText ?= null
    private var lastName: EditText ?= null
    private var street: EditText ?= null
    private var aptNo: EditText ?= null
    private var postalCode: EditText ?= null
    private var province: EditText ?= null
    private var phoneNumber: EditText ?= null
    private var cardName: EditText ?= null
    private var cardNumber: EditText ?= null
    private var cardExpiry: EditText ?= null
    private var cardCVV: EditText ?= null

    private var backBtn: ImageButton ?=null
    private var payNowBtn: Button ?=null

    private lateinit var auth: FirebaseAuth
    private var currentUser: FirebaseUser?= null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_checkout)

        auth = FirebaseAuth.getInstance()
        currentUser = auth.currentUser

        initializeScreenWidgets()
        setClickListeners()

        fillUserDataInCheckoutForm()

        //getting cart total from intent
        cartTotal = intent.getDoubleExtra("CartTotal",0.0)

        //setting text for pay now button
        payNowBtn?.text = payNowBtn?.text.toString() + " ($"+cartTotal+")"




    }

    private fun fillUserDataInCheckoutForm() {
        //getting user details from
        val userID = currentUser?.uid
        FirebaseDatabase.getInstance().reference.child("users/$userID/").child("details").get().addOnSuccessListener{
            if(it.exists()){
                val user: User? =  it.getValue(User::class.java)
                firstName!!.setText(user?.FirstName)
                lastName!!.setText(user?.LastName)
                phoneNumber!!.setText(user?.PhoneNumber)
                street!!.setText(user?.Address?.get("Street"))
                province!!.setText(user?.Address?.get("Province"))
                aptNo!!.setText(user?.Address?.get("AptNo"))
                postalCode!!.setText(user?.Address?.get("PostalCode"))

            }
        }
    }

    private fun setClickListeners() {
        backBtn?.setOnClickListener {
            finish()
        }
        payNowBtn?.setOnClickListener {
            //validating user details
            var validated = validateUser()
//            var validated = true              //for testing
            if(validated){
                //save order details in db
                saveOrderDetailsInDB()

            }
        }
    }


    private fun saveOrderDetailsInDB() {
        val userID = currentUser?.uid
        var orderID:String = ""
        val current = LocalDateTime.now()

        orderID = generateOrderID()
        Log.i("CheckoutActivity","orderID:$orderID")

        var cart = ArrayList<CartItem> ()

        val address = HashMap<String,String>()
        address["Street"] = street?.text.toString().trim()
        address["AptNo"] = aptNo?.text.toString().trim()
        address["PostalCode"] = postalCode?.text.toString().trim()
        address["Province"] = province?.text.toString().trim()

        //save Address for future orders
        saveUserAddress(address)

        //saving order ID,Date
        FirebaseDatabase.getInstance().reference.child("users/$userID/").child("orders").child(orderID).child("ID").setValue(orderID)
        FirebaseDatabase.getInstance().reference.child("users/$userID/").child("orders").child(orderID).child("Address").setValue(address)
        FirebaseDatabase.getInstance().reference.child("users/$userID/").child("orders").child(orderID).child("Date").setValue(current)
        FirebaseDatabase.getInstance().reference.child("users/$userID/").child("orders").child(orderID).child("Total").setValue(cartTotal)


        saveCartInOrdersDB(orderID)
        Log.i("CheckoutActivity",cart.toString())

    }

    private fun saveUserAddress(address: HashMap<String, String>) {
        val userID = currentUser?.uid
        FirebaseDatabase.getInstance().reference.child("users/$userID/").child("details").child("Address").setValue(address)
    }

    private fun saveCartInOrdersDB(orderID: String) {
        val cart = ArrayList<CartItem> ()
        val userID = currentUser?.uid

        //getting cart items from db
        FirebaseDatabase.getInstance().reference.child("users/$userID/").child("cart").get()
            .addOnCompleteListener{
            if(it.isSuccessful){
                for (item in it.result.children) {
                    var product = item.getValue(CartItem::class.java)
                    Log.i("CheckoutActivity",product.toString())
                    if (product != null) {
                        cart.add(product)
                        Log.i("CheckoutActivity","cart-"+product.toString())
                    }
                }
                Log.i("CheckoutActivity","cart-"+cart.toString())

                //converting cartItem to hashmap
                val cartHashMap: ArrayList<HashMap<String,Any>> = ArrayList<HashMap<String,Any>>()
                for(item in cart){
                    val itemMap = HashMap<String,Any>()
                    itemMap["Name"] = item.Name
                    itemMap["Description"] = item.Description
                    itemMap["Image"] = item.Image
                    itemMap["Price"] = item.Price
                    itemMap["Quantity"] = item.Quantity
                    cartHashMap.add(itemMap)
                }

                //saving cart in orders
                FirebaseDatabase.getInstance().reference.child("users/$userID/").child("orders").child(orderID).child("Cart").setValue(cartHashMap).addOnCompleteListener {
                    if(it.isSuccessful){
                        //clearing cart from DB
                        clearUserCart()
                        val intent = Intent(this,OrderConfirmedActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                    else{
                        Log.i("CheckoutActivity","error:"+it.exception.toString())
                    }
                }

            }
        }

    }

    private fun clearUserCart() {
        val userID = currentUser?.uid
        FirebaseDatabase.getInstance().reference.child("users/$userID/").child("cart").removeValue()
    }

    private fun generateOrderID():String {
        var orderID:String = ""
        val currentDateTime = LocalDateTime.now()
//        val current = currentDateTime.toString().dropLast(3)
        val current = currentDateTime.toString().replace(".",":")
        val alphabetSource = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
        val digitSource = "1234567890"
        val operatorsSource = "-_"
        val source=alphabetSource+alphabetSource.lowercase()+digitSource+operatorsSource
        val outputStrLength = 10

        orderID = java.util.Random().ints(outputStrLength.toLong(), 0, source.length)
            .asSequence()
            .map(source::get)
            .joinToString("")
        return orderID+"&D$current"
    }

    private fun validateUser(): Boolean {
        if (TextUtils.isEmpty(firstName!!.text)) {
            Toast.makeText(this, "Please enter your first name", Toast.LENGTH_SHORT).show()
            return false
        }
        if (TextUtils.isEmpty(lastName!!.text)) {
            Toast.makeText(this, "Please enter your last name", Toast.LENGTH_SHORT).show()
            return false
        }
        if (TextUtils.isEmpty(street!!.text)) {
            Toast.makeText(this, "Please enter rhe street number and name", Toast.LENGTH_SHORT).show()
            return false
        }
        if (TextUtils.isEmpty(aptNo!!.text)) {
            Toast.makeText(this, "Please enter the Apartment/Unit number", Toast.LENGTH_SHORT).show()
            return false
        }
        if (TextUtils.isEmpty(postalCode!!.text)) {
            Toast.makeText(this, "Please enter your Postal code", Toast.LENGTH_SHORT).show()
            return false
        }
        if (TextUtils.isEmpty(province!!.text)) {
            Toast.makeText(this, "Please enter the Province", Toast.LENGTH_SHORT).show()
            return false
        }

        if (TextUtils.isEmpty(phoneNumber!!.text)) {
            Toast.makeText(this, "Please enter phone number", Toast.LENGTH_SHORT).show()
            return false
        }

        if (TextUtils.isEmpty(cardName!!.text)) {
            Toast.makeText(this, "Please enter the name on the card", Toast.LENGTH_SHORT).show()
            return false
        }
        if (TextUtils.isEmpty(cardNumber!!.text)) {
            Toast.makeText(this, "Please enter the card number", Toast.LENGTH_SHORT).show()
            return false
        }
        if (TextUtils.isEmpty(cardExpiry!!.text)) {
            Toast.makeText(this, "Please enter the expiry date of the card", Toast.LENGTH_SHORT).show()
            return false
        }
        if (TextUtils.isEmpty(cardCVV!!.text)) {
            Toast.makeText(this, "Please enter the CVV", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    private fun initializeScreenWidgets() {
        firstName = findViewById(R.id.firstName)
        lastName = findViewById(R.id.lastName)
        street = findViewById(R.id.street)
        aptNo = findViewById(R.id.aptNo)
        postalCode = findViewById(R.id.postalCode)
        province = findViewById(R.id.province)
        phoneNumber = findViewById(R.id.phoneNumber)
        cardName = findViewById(R.id.cardName)
        cardNumber = findViewById(R.id.cardNumber)
        cardExpiry = findViewById(R.id.cardExpiry)
        cardCVV = findViewById(R.id.cardCVV)

        backBtn = findViewById(R.id.backButton)
        payNowBtn = findViewById(R.id.pay)
    }
}