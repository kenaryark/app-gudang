package com.pnj.gudang

import android.content.Intent
import com.pnj.gudang.item.InventoryFragment
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.pnj.gudang.auth.SettingsFragment
import com.pnj.gudang.auth.SignInActivity
import com.pnj.gudang.chat.ChatFragment

class MainActivity : AppCompatActivity() {

    private lateinit var auth : FirebaseAuth

    lateinit var bottomNav : BottomNavigationView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        loadFragment(InventoryFragment())
        bottomNav = findViewById(R.id.bottomNav) as BottomNavigationView
        bottomNav.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.chat -> {
                    loadFragment(ChatFragment())
                    true
                }
                R.id.inventory -> {
                    loadFragment(InventoryFragment())
                    true
                }
                R.id.settings -> {
                    loadFragment(SettingsFragment())
                    true
                }

                else -> false
            }
        }
    }
    private  fun loadFragment(fragment: Fragment){
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container,fragment)
        transaction.commit()
    }

    override fun onBackPressed() {
    }

//    override fun onStart() {
//        super.onStart()
//        if(auth.currentUser == null){
//            auth.signOut()
//            val intent = Intent(this, SignInActivity::class.java)
//            startActivity(intent)
//        }
//    }
}