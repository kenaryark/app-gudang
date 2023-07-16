package com.pnj.gudang.auth

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.pnj.gudang.R
import com.pnj.gudang.databinding.ActivitySignInBinding
import com.pnj.gudang.databinding.FragmentInventoryBinding
import com.pnj.gudang.databinding.FragmentSettingsBinding

class SettingsFragment : Fragment(R.layout.fragment_settings) {
    private var _binding: FragmentSettingsBinding? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentSettingsBinding.bind(view)
        _binding = binding

        binding.btnLogout.setOnClickListener {
            val intent = Intent(activity, SignInActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onDestroyView() {
        // Consider not storing the binding instance in a field
        // if not needed.
        _binding = null
        super.onDestroyView()
    }
}