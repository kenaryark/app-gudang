package com.pnj.gudang.auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.pnj.gudang.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.pnj.gudang.MainActivity
import com.pnj.gudang.databinding.ActivitySignInBinding


class SignInActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var binding : ActivitySignInBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        binding.buttonLogin.setOnClickListener {
            val email = binding.editTextEmail.text.toString()
            val password = binding.editTextPassword.text.toString()
            login(email,password)
        }

        binding.buttonSignUp.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }

    }


    private fun login(email: String, password: String) {
        if(email.isNotEmpty()&&password.isNotEmpty()){
            auth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
                if(it.isSuccessful){
                    val intent = Intent(this,MainActivity::class.java)
                    startActivity(intent)
                }
                else {
                    Toast.makeText(this,it.exception.toString(),Toast.LENGTH_SHORT).show()
                }
            }
        }
        else{
            Toast.makeText(this,"Complete The Input",Toast.LENGTH_SHORT).show()
        }
    }

    override fun onBackPressed() {
    }

//    override fun onStart() {
//        super.onStart()
//
//        if(auth.currentUser != null){
//            val intent = Intent(this,MainActivity::class.java)
//            startActivity(intent)
//        }
//    }
}