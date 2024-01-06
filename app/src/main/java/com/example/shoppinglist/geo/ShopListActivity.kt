package com.example.shoppinglist.geo

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.shoppinglist.data.db.entities.ShopFirebase
import com.example.shoppinglist.data.db.entities.ShoppingItemFirebase
import com.example.shoppinglist.data.repositories.ShopRepositoryFirebase
import com.example.shoppinglist.data.repositories.ShoppingRepositoryFirebase
import com.example.shoppinglist.databinding.ActivityShopListBinding
import com.example.shoppinglist.other.ShopListAdapter
import com.example.shoppinglist.other.ShoppingItemAdapter
import com.example.shoppinglist.ui.shoppinglist.ShoppingViewModel
import com.example.shoppinglist.ui.shoppinglist.ShoppingViewModelFactory
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class ShopListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityShopListBinding
    private val shopRef = Firebase.firestore.collection("shops")
    private lateinit var viewModel: ShopViewModel
    private lateinit var adapter: ShopListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShopListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val repository = ShopRepositoryFirebase()
        val factory = ShopViewModelFactory(repository)
        viewModel = ViewModelProviders.of(this, factory).get(ShopViewModel::class.java)
        adapter = ShopListAdapter(listOf(), viewModel)

        binding.rvShopList.layoutManager = LinearLayoutManager(this)
        binding.rvShopList.adapter = adapter

        subscribeToShopListUpdates()


        binding.map.setOnClickListener{
            finish()
        }

        binding.fab.setOnClickListener{
            AddShopDialog(this,
                object : AddShopDialogListener {
                    @SuppressLint("RestrictedApi")
                    override fun onAddButtonClicked(shop: ShopFirebase) {
                        val firebaseShop =
                            ShopFirebase(shop.name, shop.description, shop.radius, shop.longitude, shop.latitude)
                        viewModel.insert(firebaseShop)
//                        val intent = Intent("ITEM_ADDED_TO_SHOPPING_LIST").apply {
//                            val gson = Gson()
//                            val serializedItem = gson.toJson(item)
//                            putExtra("ITEM", serializedItem)
//                        }
//                        sendBroadcast(intent)
//                        // broadcast with permissions
//                        sendBroadcast(intent, "com.example.shoppinglist.CUSTOM_PERMISSION")
                    }
                }).show()
        }

    }

    private fun subscribeToShopListUpdates() {
        shopRef.addSnapshotListener { querySnapshot, firebaseFirestoreException ->
            firebaseFirestoreException?.let {
                Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
                return@addSnapshotListener
            }
            querySnapshot?.let {
                val shopListMutable = mutableListOf<ShopFirebase>()
                for (document in it) {
                    val shop = document.toObject(ShopFirebase::class.java)
                    shop?.let {
                        shopListMutable.add(shop)
                    }
                }
                val shopList = shopListMutable.toList()
                adapter.shops = shopList
                adapter.notifyDataSetChanged()
                binding.rvShopList.layoutManager = LinearLayoutManager(this@ShopListActivity)
                binding.rvShopList.adapter = adapter
            }
        }
    }
}