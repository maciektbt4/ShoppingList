package com.example.shoppinglist.ui.shoppinglist

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import com.example.shoppinglist.data.db.entities.ShoppingItemFirebase
import com.example.shoppinglist.data.repositories.ShoppingRepositoryFirebase
import com.example.shoppinglist.databinding.ActivityEditShoppingItemBinding
import com.google.gson.Gson

class EditShoppingItem: AppCompatActivity() {
    private lateinit var binding: ActivityEditShoppingItemBinding
    private lateinit var viewModel: ShoppingViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditShoppingItemBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val serializedItem = intent.getStringExtra("ITEM")
        var itemExist:Boolean = false
        if (serializedItem != null) {
            val item = Gson().fromJson(serializedItem, ShoppingItemFirebase::class.java)
            binding.etName.setText(item.name)
            binding.etAmount.setText(item.amount.toString())
            binding.etPrice.setText(item.price.toString())
            itemExist = true
        }

        binding.tvEdit.setOnClickListener() {
            if (itemExist){
                val repository = ShoppingRepositoryFirebase()
                val factory = ShoppingViewModelFactory(repository)
                var item = Gson().fromJson(serializedItem, ShoppingItemFirebase::class.java)
                val oldItemName = item.name
                item.name = binding.etName.text.toString()
                val priceString = binding.etPrice.text.toString()
                val price = priceString.toFloatOrNull() ?: 0.0f // Convert String to Float or use a default value
                item.price = price
                val amountString = binding.etAmount.text.toString()
                val amount = amountString.toIntOrNull() ?: 0 // Convert String to Float or use a default value
                item.amount = amount
                viewModel = ViewModelProviders.of(this, factory).get(ShoppingViewModel::class.java)
                Log.d("ITEM","$item")

                viewModel.update(item, oldItemName)
                Log.d("ITEM","$item")
            }

            finish()
        }

        binding.tvCancel.setOnClickListener() {
            finish()
        }
    }
}