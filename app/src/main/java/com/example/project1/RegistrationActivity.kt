package com.example.project1

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase

class RegistrationActivity : AppCompatActivity() {
    private var userFirstName: TextView? = null
    private var userLastName: TextView? = null
    private var userEmail: TextView? = null
    private var userPass: TextView? = null
    private var userPhoneNumber: TextView? = null
    private var registerBtn: Button? = null
    private var signInText: TextView? = null

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_registration)

        auth = FirebaseAuth.getInstance()

        initializeScreenWidgets()
        setClickListeners()


//            Toast.makeText(this,"ck=licked registerBtn",Toast.LENGTH_SHORT)

    }

    private fun initializeScreenWidgets(){
        //this function gets reference of all widgets on this activity and initializes them
        userFirstName = findViewById(R.id.firstName)
        userLastName =  findViewById(R.id.lastName)
        userEmail = findViewById(R.id.email)
        userPass = findViewById(R.id.password)
        userPhoneNumber = findViewById(R.id.phoneNumber)
        registerBtn = findViewById(R.id.register)
        signInText = findViewById(R.id.signInTxt)
    }

    private fun setClickListeners(){
        //setting onClick listener of Register Button
        registerBtn?.setOnClickListener {
            val validated = validateUserDetails()
            if(validated){
                val email = userEmail?.text?.trim()
                val password = userPass?.text?.trim()
                registerUser(email.toString(),password.toString())
            }

        }

        signInText?.setOnClickListener{
            Log.i("RegistrationActivity","inside signInText setOnClickListener")
            val intent = Intent(this,LoginActivity::class.java)
            startActivity(intent)
        }
    }

    private fun registerUser(email:String, password:String){
        auth.createUserWithEmailAndPassword(email,password)
            .addOnCompleteListener(this){
                if(it.isSuccessful){
                    val user = FirebaseAuth.getInstance().currentUser
                    addUserDetailsToFirebase(user!!)
                    val intent = Intent(this,MainActivity::class.java)
                    startActivity(intent)

                }
                else{
                    Toast.makeText(this,"Sorry! Failed to register "+ it.exception?.message,Toast.LENGTH_LONG).show()
                }
            }
    }

    private fun addUserDetailsToFirebase(user:FirebaseUser){
        val userID = user?.uid
//        Log.i("RegistrationActivity","userID=$userID")
        val firstName = userFirstName?.text?.trim()
        val lastName = userLastName?.text?.trim()
        val email = userEmail?.text?.trim()
        val phoneNumber = userPhoneNumber?.text?.trim()
        FirebaseDatabase.getInstance().reference.child("users/$userID/").child("details").child("FirstName").setValue(firstName.toString())
        FirebaseDatabase.getInstance().reference.child("users/$userID/").child("details").child("LastName").setValue(lastName.toString())
        FirebaseDatabase.getInstance().reference.child("users/$userID/").child("details").child("Email").setValue(email.toString())
        FirebaseDatabase.getInstance().reference.child("users/$userID/").child("details").child("PhoneNumber").setValue(phoneNumber.toString())

    }

    private fun validateUserDetails(): Boolean{
        val firstName = userFirstName?.text?.trim()
        val lastName = userLastName?.text?.trim()
        val email = userEmail?.text?.trim()
        val password = userPass?.text?.trim()
        val phoneNumber = userPhoneNumber?.text?.trim()
        if(TextUtils.isEmpty(firstName)){
            Log.i("RegistrationActivity","inside enter your first name")
            Toast.makeText(this,"Please enter your first name",Toast.LENGTH_SHORT).show()
            return false
        }
        if(TextUtils.isEmpty(lastName)){
            Toast.makeText(this,"Please enter your last name",Toast.LENGTH_SHORT).show()
            return false
        }
        if(TextUtils.isEmpty(email)){
            Toast.makeText(this,"Please enter your email",Toast.LENGTH_SHORT).show()
            return false
        }
        else if(!(Patterns.EMAIL_ADDRESS.matcher(email).matches())){
            Toast.makeText(this,"Invalid Email Address",Toast.LENGTH_SHORT).show()
            return false
        }
        if(TextUtils.isEmpty(password)){
            Toast.makeText(this,"Please enter your password",Toast.LENGTH_SHORT).show()
            return false
        }
        else if(password?.length!! < 6){
            //checking if password is has at least 6 characters
            Toast.makeText(this,"Password must have at least 6 characters",Toast.LENGTH_SHORT).show()
            return false
        }
        if(TextUtils.isEmpty(phoneNumber)){
            Toast.makeText(this,"Please enter your phone number",Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }
}