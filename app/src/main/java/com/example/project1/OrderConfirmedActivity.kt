package com.example.project1

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class OrderConfirmedActivity : AppCompatActivity() {
     var continueShoppingbutton: Button ?= null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_order_confirmed)

        initializeScreenWidgets()
        setClickListeners()
    }

    private fun setClickListeners() {
        continueShoppingbutton?.setOnClickListener {
            val intent = Intent(this,MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun initializeScreenWidgets() {
        continueShoppingbutton = findViewById(R.id.continueShoppingbutton)
    }


}