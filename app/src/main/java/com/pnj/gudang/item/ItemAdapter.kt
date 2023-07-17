package com.pnj.gudang.item

import android.content.Intent
import android.graphics.BitmapFactory
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.menu.MenuView.ItemView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.storage.FirebaseStorage
import com.pnj.gudang.R
import java.io.File

class ItemAdapter(private val itemList : ArrayList<Item>) :
    RecyclerView.Adapter<ItemAdapter.ItemViewHolder>(){
    private lateinit var activity: AppCompatActivity
    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var name: TextView = itemView.findViewById(R.id.TVLName)
        var quantity: TextView = itemView.findViewById(R.id.TVLQuantity)
        var invoice: TextView = itemView.findViewById(R.id.TVLInvoice)
        var date: TextView = itemView.findViewById(R.id.TVLDate)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_list_layout, parent, false)
        return ItemViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item: Item = itemList[position]
        holder.name.text = item.name
        holder.quantity.text = item.quantity
        holder.invoice.text = item.invoice
        holder.date.text = item.date

        holder.itemView.setOnClickListener {
            activity = it.context as AppCompatActivity
            activity.startActivity(Intent(activity, EditItemActivity::class.java).apply {
                putExtra("name", item.name)
                putExtra("quantity", item.quantity)
                putExtra("invoice", item.invoice)
                putExtra("date", item.date)
            })
        }
    }

//        val storageRef = FirebaseStorage.getInstance().reference.child("img_item/${item.name}_${item.invoice}.jpg")
//        val localfile = File.createTempFile("tempImage","jpg")
//        storageRef.getFile(localfile).addOnSuccessListener {
//            val bitmap = BitmapFactory.decodeFile(localfile.absolutePath)
//            holder.img_item.setImageBitmap(bitmap)
//        }.addOnFailureListener {
//            Log.e("foto ?","gagal")
//        }
//    }
}