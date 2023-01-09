package com.example.project1

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import org.w3c.dom.Text

class LoginActivity : AppCompatActivity() {
    private var userEmail: TextView? = null
    private var userPass: TextView? = null
    private var signInBtn: Button? = null
    private var signUpTxt: TextView? = null
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()
        initializeScreenWidgets()

        setClickListeners()
    }

    private fun initializeScreenWidgets(){
        //this function gets reference of all widgets on this activity and initializes them
        userEmail = findViewById(R.id.loginEmail)
        userPass = findViewById(R.id.loginPassword)
        signInBtn = findViewById(R.id.signInBtn)
        signUpTxt = findViewById(R.id.signUpTxt)
    }

    private fun setClickListeners(){

        //setting onClick listener of SigninButton
        signInBtn?.setOnClickListener {
            val email = userEmail?.text?.trim().toString()
            val password = userPass?.text?.trim().toString()
            val validated = validateUserDetails(email,password)
            if(validated){
                loginUser(email,password)
            }
        }

        signUpTxt?.setOnClickListener{
            val intent = Intent(this,RegistrationActivity::class.java)
            startActivity(intent)
        }
    }

    private fun loginUser(email: String,password: String){
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this){
                if(it.isSuccessful){
                    val intent = Intent(this,MainActivity::class.java)
                    startActivity(intent)
                }
                else{
                    Toast.makeText(this,"Sorry! Failed to sign in "+ it.exception?.message,Toast.LENGTH_LONG).show()
                }
            }
    }

    private fun validateUserDetails(email:String, password:String):Boolean{
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
        return true
    }
}