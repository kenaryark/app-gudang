package com.pnj.gudang.item

import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import com.pnj.gudang.MainActivity
import com.pnj.gudang.R
import com.pnj.gudang.databinding.ActivityEditItemBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.Calendar

class EditItemActivity : AppCompatActivity() {
    private lateinit var binding : ActivityEditItemBinding
    private val db = FirebaseFirestore.getInstance()

    private val REQ_CAM = 101
    private lateinit var imgUrl : Uri
    private var dataPict: Bitmap? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityEditItemBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val (year, month, day, curr_item) = setDefaultValue()

        binding.TxtEditDate.setOnClickListener {
            val dpd = DatePickerDialog(this,
                DatePickerDialog.OnDateSetListener{view, year, monthOfYear, dayOfMonth ->
                    binding.TxtEditDate.setText(
                        ""+year+"-"+(monthOfYear+1)+"-"+dayOfMonth)
                }, year.toString().toInt(), month.toString().toInt(), day.toString().toInt()
            )
            dpd.show()
        }

        showPict()

        binding.BtnEditImgItem.setOnClickListener {
            openCamera()
        }

        binding.BtnEditItem.setOnClickListener {
            val new_item = newItem()
            updateItem( curr_item as Item, new_item)

            val intent = Intent(this,MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    fun setDefaultValue(): Array<Any>{
        val intent = intent
        val name = intent.getStringExtra("name").toString()
        val quantity = intent.getStringExtra("quantity").toString()
        val invoice = intent.getStringExtra("invoice").toString()
//        val warehouse = intent.getStringExtra("warehouse").toString()
        val date = intent.getStringExtra("date").toString()

        binding.TxtEditName.setText(name)
        binding.TxtEditQuantity.setText(quantity)
        binding.TxtEditNoInvoice.setText(invoice)
//        binding.TxtEditWarehouse.setText(warehouse)
        binding.TxtEditDate.setText(date)


        val date_split = intent.getStringExtra("date")
            .toString().split("-").toTypedArray()
        val year = date_split[0].toInt()
        val month = date_split[1].toInt() -1
        val day = date_split[2].toInt()
        val curr_item = Item(name, quantity, invoice, date)
        return arrayOf(year, month, day, curr_item)
    }

    fun showPict(){
        val intent = intent
        val name = intent.getStringExtra("name").toString()
        val invoice = intent.getStringExtra("invoice").toString()

        val storageRef = FirebaseStorage.getInstance().reference.child("img_item/${name}_${invoice}.jpg")
        val localfile = File.createTempFile("tempImage","jpg")
        storageRef.getFile(localfile).addOnSuccessListener {
            val bitmap = BitmapFactory.decodeFile(localfile.absolutePath)
            binding.BtnEditImgItem.setImageBitmap(bitmap)
        }.addOnFailureListener {
            Log.e("foto ?","gagal")
        }
    }

    private fun openCamera(){
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { intent ->
            this.packageManager?.let {
                intent?.resolveActivity(it).also {
                    startActivityForResult(intent,REQ_CAM)
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQ_CAM && resultCode == RESULT_OK){
            dataPict = data?.extras?.get("data") as Bitmap
            binding.BtnEditImgItem.setImageBitmap(dataPict)
        }
    }

    fun newItem():Map<String, Any>{
        var name : String = binding.TxtEditName.text.toString()
        var quantity : String = binding.TxtEditQuantity.text.toString()
        var invoice : String = binding.TxtEditNoInvoice.text.toString()
        var warehouse : String = binding.TxtEditWarehouse.selectedItem.toString()
        var date : String = binding.TxtEditDate.text.toString()

        val item: MutableMap<String, Any> = HashMap()
        item["name"] = name
        item["quantity"] = quantity
        item["invoice"] = invoice
        item["warehouse"] = warehouse
        item["date"] = date

        if(dataPict != null){
            uploadPictFirebase(dataPict!!,"${name}_${invoice}")
        }

        return item
    }
    private fun uploadPictFirebase(img_bitmap: Bitmap, file_name: String){
        val baos = ByteArrayOutputStream()
        val ref = FirebaseStorage.getInstance().reference.child("img_item/${file_name}.jpg")
        img_bitmap.compress(Bitmap.CompressFormat.JPEG,100,baos )

        val img = baos.toByteArray()
        ref.putBytes(img)
            .addOnCompleteListener() {
                if(it.isSuccessful){
                    ref.downloadUrl.addOnCompleteListener { Task ->
                        Task.result.let { Uri ->
                            imgUrl = Uri
                            binding.BtnEditImgItem.setImageBitmap(img_bitmap)
                        }
                    }
                }
            }
    }

    private fun updateItem(item: Item, newItemMap: Map<String, Any>) =
        CoroutineScope(Dispatchers.IO).launch {
            val personQuery = db.collection("item")
                .whereEqualTo("name",item.name)
                .whereEqualTo("quantity",item.quantity)
                .whereEqualTo("invoice",item.invoice)
//                .whereEqualTo("warehouse",item.warehouse)
                .whereEqualTo("date",item.date)
                .get()
                .await()
            if (personQuery.documents.isNotEmpty()){
                for (document in personQuery){
                    try {
                        db.collection("item").document(document.id).set(
                            newItemMap,
                            SetOptions.merge()
                        )
                    } catch (e: Exception) {
                        withContext(Dispatchers.Main){
                            Toast.makeText(this@EditItemActivity,
                                e.message, Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
            else {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@EditItemActivity,
                        "No persons matched the query.", Toast.LENGTH_LONG).show()
                }
            }
        }
}