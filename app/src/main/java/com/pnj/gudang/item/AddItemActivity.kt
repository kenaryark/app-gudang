package com.pnj.gudang.item

import android.app.DatePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.pnj.gudang.databinding.ActivityAddItemBinding
import java.util.Calendar

class AddItemActivity : AppCompatActivity() {
    private lateinit var binding : ActivityAddItemBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddItemBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.TxtDateIn.setOnClickListener{
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)

            val dpd = DatePickerDialog(this,
                DatePickerDialog.OnDateSetListener{ view, year, monthOfYear, dayOfMonth ->
                    binding.TxtDateIn.setText(""+year+"-"+month+"-"+dayOfMonth)
                }, year, month, day)

            dpd.show()
        }
    }
}