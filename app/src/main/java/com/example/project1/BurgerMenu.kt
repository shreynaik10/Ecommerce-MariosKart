package com.example.project1

import android.app.Activity
import android.content.ClipData
import android.content.Intent
import android.view.Gravity
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.view.menu.MenuView
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth

class BurgerMenu (activity: Activity,drawerLayout: DrawerLayout,navView: NavigationView) {

    init {
        lateinit var auth: FirebaseAuth
        var toggle = ActionBarDrawerToggle(activity,drawerLayout,R.string.open,R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        navView?.setNavigationItemSelectedListener {

            when(it.itemId){
                R.id.nav_home -> {

                    if(activity.localClassName !="MainActivity"){
                        var intent = Intent(activity,MainActivity::class.java)
                        activity.startActivity(intent)
                    }
                    drawerLayout?.closeDrawer(Gravity.LEFT)
                }
                R.id.nav_consoles -> {

                    var intent = Intent(activity,ProductsActivity::class.java)
                    intent.putExtra("ProductType",R.string.product_type_console)
                    activity.startActivity(intent)

                    drawerLayout?.closeDrawer(Gravity.LEFT)
                }
                R.id.nav_games -> {
                    var intent = Intent(activity,ProductsActivity::class.java)
                    intent.putExtra("ProductType",R.string.product_type_game)
                    activity.startActivity(intent)
                }
                R.id.nav_accessories -> Toast.makeText(activity,"nav_accessories clicked", Toast.LENGTH_SHORT).show()
                R.id.nav_profile -> Toast.makeText(activity,"nav_profile clicked", Toast.LENGTH_SHORT).show()
                R.id.nav_logout -> {
//                    Toast.makeText(activity, "Logout clicked", Toast.LENGTH_SHORT).show()
                    auth = FirebaseAuth.getInstance()
                    auth.signOut()
                    var intent = Intent(activity,LoginActivity::class.java)
                    activity.startActivity(intent)
                }
                R.id.nav_orders -> {
                    var intent = Intent(activity,OrdersActivity::class.java)
                    activity.startActivity(intent)
                }
                R.id.nav_rate -> Toast.makeText(activity,"nav_rate clicked", Toast.LENGTH_SHORT).show()
                R.id.nav_share -> Toast.makeText(activity,"nav_share clicked", Toast.LENGTH_SHORT).show()
            }
            true
        }
    }

}
