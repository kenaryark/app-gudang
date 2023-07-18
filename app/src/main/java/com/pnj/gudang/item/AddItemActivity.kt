package com.pnj.gudang.item

import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.pnj.gudang.MainActivity
import com.pnj.gudang.databinding.ActivityAddItemBinding
import java.io.ByteArrayOutputStream
import java.util.Calendar

class AddItemActivity : AppCompatActivity() {
    private lateinit var binding : ActivityAddItemBinding
    private val firestoreDatabase = FirebaseFirestore.getInstance()

    private val REQ_CAM = 101
    private lateinit var imgUrl : Uri
    private var dataPict: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddItemBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.TxtAddDate.setOnClickListener{
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)

            val dpd = DatePickerDialog(this,
                DatePickerDialog.OnDateSetListener{ view, year, monthOfYear, dayOfMonth ->
                    binding.TxtAddDate.setText(""+year+"-"+month+"-"+dayOfMonth)
                }, year, month, day)

            dpd.show()
        }

        binding.BtnAddItem.setOnClickListener {
            addItem()
        }

        binding.BtnImgAddItem.setOnClickListener {
            openCamera()
        }
    }

    fun addItem() {
        var name : String = binding.TxtAddName.text.toString()
        var quantity : String = binding.TxtAddQuantity.text.toString()
        var invoice : String = binding.TxtAddNoInvoice.text.toString()
        // var warehouse : String = binding.TxtAddWarehouse.selectedItem.toString()
        var date : String = binding.TxtAddDate.text.toString()

        val item: MutableMap<String, Any> = HashMap()
        item["name"] = name
        item["quantity"] = quantity
        item["invoice"] = invoice
        // item["warehouse"] = warehouse
        item["date"] = date

        if(dataPict != null){
            uploadPictFirebase(dataPict!!,"${name}_${invoice}")

            firestoreDatabase.collection("item").add(item)
                .addOnSuccessListener {
                    val intentMain = Intent(this, MainActivity::class.java)
                    startActivity(intentMain)
                }
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
            binding.BtnImgAddItem.setImageBitmap(dataPict)
        }
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
                            binding.BtnImgAddItem.setImageBitmap(img_bitmap)
                        }
                    }
                }
            }
    }
}