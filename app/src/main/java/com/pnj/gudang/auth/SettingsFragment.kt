package com.pnj.gudang.auth

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.pnj.gudang.R
import com.pnj.gudang.databinding.ActivitySignInBinding
import com.pnj.gudang.databinding.FragmentInventoryBinding
import com.pnj.gudang.databinding.FragmentSettingsBinding

class SettingsFragment : Fragment(R.layout.fragment_settings) {
    private lateinit var  auth : FirebaseAuth
    private var _binding: FragmentSettingsBinding? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentSettingsBinding.bind(view)
        _binding = binding

        binding.btnLogout.setOnClickListener {
//            auth.signOut()
            val intent = Intent(context, SignInActivity::class.java)
            startActivity(intent)
        }

        binding.btnChangePass.setOnClickListener {
            val new_password = binding.txtChangePass.text.toString()
            edit_password(new_password)
        }

        val user = FirebaseAuth.getInstance().currentUser?.email
        binding.username.text = user.toString()
    }

    private fun edit_password(new_password: String){
        val user = FirebaseAuth.getInstance().currentUser
        val new_password = new_password

        user!!.updatePassword(new_password).addOnCompleteListener {task ->
            if(task.isSuccessful){
                Toast.makeText(context,"Password Changed", Toast.LENGTH_SHORT).show()
            }
            else{
                Toast.makeText(context,"Password Cant Changed", Toast.LENGTH_SHORT).show()
            }
        }
    }
//    override fun onStart() {
//        super.onStart()
//        if(auth.currentUser == null){
//            auth.signOut()
//            val intent = Intent(context,SignInActivity::class.java)
//            startActivity(intent)
//        }
//    }
}