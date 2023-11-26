package com.example.shoppinglist.ui.shoppinglist

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.shoppinglist.data.db.ShoppingDatabase
import com.example.shoppinglist.data.db.entities.ShoppingItem
import com.example.shoppinglist.data.repositories.ShoppingRepository
import com.example.shoppinglist.databinding.ActivityShoppingBinding
import com.example.shoppinglist.other.ShoppingItemAdapter
import com.example.shoppinglist.settings.AppSettings
import com.google.gson.Gson

class ShoppingActivity : AppCompatActivity() {
    private lateinit var viewModel: ShoppingViewModel
    private lateinit var binding: ActivityShoppingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("ActivityShopping", "On create")
        super.onCreate(savedInstanceState)
        binding = ActivityShoppingBinding.inflate(layoutInflater)

        setContentView(binding.root)


        val database = ShoppingDatabase(this)
        val repository = ShoppingRepository(database)
        val factory = ShoppingViewModelFactory(repository)
        viewModel = ViewModelProviders.of(this, factory).get(ShoppingViewModel::class.java)
        val adapter = ShoppingItemAdapter(listOf(), viewModel)

        binding.rvShoppingItems.layoutManager = LinearLayoutManager(this)
        binding.rvShoppingItems.adapter = adapter

        viewModel.getAllShoppingItems().observe(this, Observer {
            adapter.items = it
            adapter.notifyDataSetChanged()
        })
        binding.fab.setOnClickListener{
            AddShoppingItemDialog(this,
                object: AddDialogListener{
                    @SuppressLint("RestrictedApi")
                    override fun onAddButtonClicked(item: ShoppingItem) {
                        viewModel.upsert(item)
                        // broadcast with permissions
//                        val intent = Intent("ITEM_ADDED_TO_SHOPPING_LIST")
//                        intent.setPackage("com.example.broadcastreceiverapp") // Specify the package name of application2
//                        intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES) // Include stopped packages
//                        sendBroadcast(intent.apply {
//                            putExtra("ITEM_NAME", item.name)
//                            putExtra("ITEM_AMOUNT", item.amount.toString())
//                            putExtra("ITEM_PRICE", item.price.toString())
//                        }, "CUSTOM_PERMISSION")

                        sendBroadcast(
                            Intent("ITEM_ADDED_TO_SHOPPING_LIST").apply{
                                val gson = Gson()
                                val serializedItem = gson.toJson(item)
                                putExtra("ITEM", serializedItem)
//                                putExtra("ITEM_NAME", item.name)
//                                putExtra("ITEM_AMOUNT", item.amount.toString())
//                                putExtra("ITEM_PRICE", item.price.toString())
                            })

                    }
                }).show()
        }

        binding.settings.setOnClickListener{
            val intent = Intent(this, AppSettings::class.java)
            startActivity(intent)
        }
    }
    override fun onRestart() {
        super.onRestart()
        Log.d("ActivityShopping", "On restart")
    }

    override fun onResume() {
        super.onResume()
        val sharedPreferences = getSharedPreferences("MySettings", Context.MODE_PRIVATE)
        // Retrieve data
        val background = sharedPreferences.getBoolean("background", false)
        val textColor = sharedPreferences.getString("textColor", "").toString()
        val applySettings = sharedPreferences.getBoolean("apply", false)
        if(applySettings){

            // Pass the textColor to the adapter
            val adapter = ShoppingItemAdapter(listOf(), viewModel, textColor)
            binding.rvShoppingItems.layoutManager = LinearLayoutManager(this)
            binding.rvShoppingItems.adapter = adapter
            if (background){
                binding.rvShoppingItems.setBackgroundResource(com.google.android.material.R.color.cardview_dark_background)
            }
            else{
                binding.rvShoppingItems.setBackgroundResource(com.google.android.material.R.color.cardview_light_background)
            }

            viewModel.getAllShoppingItems().observe(this, Observer {
                adapter.items = it
                adapter.notifyDataSetChanged()
            })
        }
    }
}