package com.example.shoppinglist.ui.shoppinglist

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AppCompatDialog
import com.example.shoppinglist.data.db.entities.ShoppingItemFirebase
import com.example.shoppinglist.databinding.DialogAddShoppingItemBinding

class AddShoppingItemDialog(context: Context, var addDialogListener: AddDialogListener):AppCompatDialog(context) {
    private lateinit var binding: DialogAddShoppingItemBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DialogAddShoppingItemBinding.inflate(LayoutInflater.from(context))
        setContentView(binding.root)
        binding.tvAdd.setOnClickListener(){
            val name = binding.etName.text.toString()
            val amount = binding.etAmount.text.toString()
            val price = binding.etPrice.text.toString()

            if(name.isEmpty() || amount.isEmpty() || price.isEmpty()){
                Toast.makeText(context, "Please enter all information", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val item = ShoppingItemFirebase(name, amount.toInt(), price.toFloat(), false)
            addDialogListener.onAddButtonClicked(item)

            dismiss()
        }

        binding.tvCancel.setOnClickListener(){
            cancel()
        }
    }
}