package com.pnj.gudang.item

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot
import com.pnj.gudang.R
import com.pnj.gudang.databinding.FragmentInventoryBinding

class InventoryFragment : Fragment(R.layout.fragment_inventory) {

    private var _binding: FragmentInventoryBinding? = null

    private lateinit var itemRecyclerView: RecyclerView
    private lateinit var itemArrayList : ArrayList <Item>
    private lateinit var itemAdapter: ItemAdapter
    private lateinit var db : FirebaseFirestore

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

        itemRecyclerView = binding.itemListView
        itemRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        itemRecyclerView.setHasFixedSize(true)

        itemArrayList = arrayListOf()
        itemAdapter = ItemAdapter(itemArrayList)

        itemRecyclerView.adapter = itemAdapter
    }

    override fun onDestroyView() {
        // Consider not storing the binding instance in a field
        // if not needed.
        _binding = null
        super.onDestroyView()
    }

    private fun load_data(){
        itemArrayList.clear()
        db = FirebaseFirestore.getInstance()
        db.collection("item").
        addSnapshotListener(object : EventListener<QuerySnapshot> {
            override fun onEvent(
                value: QuerySnapshot?,
                error: FirebaseFirestoreException?
            ) {
                if (error != null){
                    Log.e("Firestore Error",error.message.toString())
                    return
                }
                for (dc: DocumentChange in value?.documentChanges!!){
                    if(dc.type == DocumentChange.Type.ADDED)
                        itemArrayList.add(dc.document.toObject(Item::class.java))
                }
                itemAdapter.notifyDataSetChanged()
            }
        })
    }

}
