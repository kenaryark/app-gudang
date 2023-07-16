package com.pnj.gudang.item

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.pnj.gudang.R
import com.pnj.gudang.databinding.FragmentInventoryBinding

class InventoryFragment : Fragment(R.layout.fragment_inventory) {

    private var _binding: FragmentInventoryBinding? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentInventoryBinding.bind(view)
        _binding = binding

        // Set click listener for "Add Item" button using data binding
        binding.btnAddItem.setOnClickListener {
            // Navigate to the new activity when the button is clicked
            val intent = Intent(activity, AddItemActivity::class.java)
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
