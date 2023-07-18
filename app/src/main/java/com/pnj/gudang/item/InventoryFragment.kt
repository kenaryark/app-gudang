package com.pnj.gudang.item

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.pnj.gudang.R
import com.pnj.gudang.databinding.FragmentInventoryBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

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
        itemRecyclerView.layoutManager = LinearLayoutManager(context)
        itemRecyclerView.setHasFixedSize(true)

        itemArrayList = arrayListOf()
        itemAdapter = ItemAdapter(itemArrayList)

        itemRecyclerView.adapter = itemAdapter

        load_data()

        binding.searchViewInventory.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextChange(query: String): Boolean {
                search_data(query)
                return true
            }

            override fun onQueryTextSubmit(newText: String): Boolean {
                search_data(newText)
                return true
            }
        })

        swipeDelete()

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

    private fun search_data(keyword: String) {
        itemArrayList.clear()
        db = FirebaseFirestore.getInstance()

        val query = db.collection("item")
            .orderBy("name")
            .startAt(keyword)
            .endAt(keyword + "\uf8ff")
            .addSnapshotListener { querySnapshot, error ->
                if (error != null) {
                    Log.e("Firestore Error", error.message.toString())
                    return@addSnapshotListener
                }

                for (document in querySnapshot!!.documents) {
                   // itemArrayList.add(document.toObject(Item::class.java))
                    val item = document.toObject(Item::class.java)
                    item?.let { itemArrayList.add(it) }
                }
                itemAdapter.notifyDataSetChanged()
            }
    }

    private fun deleteFoto(file_name: String){
        val storage = Firebase.storage
        val storageRef = storage.reference
        val deleteFileRef = storageRef.child(file_name)
        if (deleteFileRef != null){
            deleteFileRef.delete().addOnSuccessListener {
                Log.e("deleted","success")
            }.addOnFailureListener{
                Log.e("deleted","failed")
            }
        }
    }
    fun deleteItem(item: Item,doc_id:String){
        val builder = AlertDialog.Builder(requireContext())
        builder.setMessage("Delete this ${item.name} ?")
            .setCancelable(false)
            .setPositiveButton("Yes"){dialog,id ->
                lifecycleScope.launch{
                    db.collection("item")
                        .document(doc_id).delete()
                    deleteFoto("img_item/${item.name}_${item.invoice}.jpg")
                    Toast.makeText(
                        context,
                        item.name.toString() + " is deleted",
                        Toast.LENGTH_LONG
                    ).show()
                    load_data()
                }
            }
            .setNegativeButton("No"){dialog,id ->
                dialog.dismiss()
                load_data()
            }
        val alert = builder.create()
        alert.show()
    }
    fun swipeDelete(){
        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0,
            ItemTouchHelper.RIGHT){
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition

                lifecycleScope.launch{
                    val item = itemArrayList[position]
                    val personQuery = db.collection("item")
                        .whereEqualTo("name",item.name)
                        .whereEqualTo("quantity",item.quantity)
                        .whereEqualTo("invoice",item.invoice)
                        .whereEqualTo("date",item.date)
//                        .whereEqualTo("warehouse",item.warehouse)
                        .get()
                        .await()
                    if (personQuery.documents.isNotEmpty()){
                        for (document in personQuery){
                            try {
                                deleteItem(item,document.id)
                                load_data()
                            } catch (e: Exception) {
                                withContext(Dispatchers.Main){
                                    Toast.makeText(
                                        context,
                                        e.message.toString(),
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            }
                        }
                    }
                    else {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(
                                context,
                                "Not Found",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                }
            }
        }).attachToRecyclerView(itemRecyclerView)
    }
}
