package com.example.shoppinglist.geo

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AppCompatDialog
import com.example.shoppinglist.data.db.entities.ShopFirebase
import com.example.shoppinglist.databinding.DialogAddShopBinding

class AddShopDialog(context: Context, var addDialogListener: AddShopDialogListener): AppCompatDialog(context) {
    private lateinit var binding: DialogAddShopBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DialogAddShopBinding.inflate(LayoutInflater.from(context))
        setContentView(binding.root)
        binding.tvAdd.setOnClickListener(){
            val name = binding.etName.text.toString()
            val description = binding.etDescription.text.toString()
            val radius = binding.etRadius.text.toString()
            val longitude = binding.etLongitude.text.toString()
            val latitude = binding.etLatitude.text.toString()

            if(name.isEmpty() || description.isEmpty() || radius.isEmpty() || longitude.isEmpty() || latitude.isEmpty()){
                Toast.makeText(context, "Please enter all information", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val shop = ShopFirebase(name, description, radius.toFloat(), longitude.toDouble(), latitude.toDouble())
            addDialogListener.onAddButtonClicked(shop)

            dismiss()
        }

        binding.tvCancel.setOnClickListener(){
            cancel()
        }
    }
}