package com.example.shoppinglist.ui.shoppinglist

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.shoppinglist.data.db.entities.ShoppingItemFirebase
import com.example.shoppinglist.data.repositories.ShoppingRepositoryFirebase
import com.example.shoppinglist.databinding.ActivityShoppingBinding
import com.example.shoppinglist.geo.MapsActivity
import com.example.shoppinglist.geo.ShopViewModel
import com.example.shoppinglist.geo.tracking.LocationService
import com.example.shoppinglist.other.ShoppingItemAdapter
import com.example.shoppinglist.settings.AppSettings
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ShoppingActivity : AppCompatActivity() {
    private lateinit var viewModel: ShoppingViewModel
    private lateinit var shopViewModel: ShopViewModel

    private lateinit var binding: ActivityShoppingBinding
    private lateinit var adapter: ShoppingItemAdapter
    lateinit var auth: FirebaseAuth

    private val itemRef = Firebase.firestore.collection("items")


    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("ActivityShopping", "On create")
        super.onCreate(savedInstanceState)

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                0
            )
        }

        binding = ActivityShoppingBinding.inflate(layoutInflater)
        auth = FirebaseAuth.getInstance()

        setContentView(binding.root)

        val repository = ShoppingRepositoryFirebase()
        val factory = ShoppingViewModelFactory(repository)
        viewModel = ViewModelProviders.of(this, factory).get(ShoppingViewModel::class.java)

        adapter = ShoppingItemAdapter(listOf(), viewModel)

        binding.rvShoppingItems.layoutManager = LinearLayoutManager(this)
        binding.rvShoppingItems.adapter = adapter

        subscribeToItemListUpdates()

        binding.fab.setOnClickListener {
            AddShoppingItemDialog(this,
                object : AddDialogListener {
                    @SuppressLint("RestrictedApi")
                    override fun onAddButtonClicked(item: ShoppingItemFirebase) {
                        val firebaseItem =
                            ShoppingItemFirebase(item.name, item.amount, item.price, false)
                        viewModel.insert(firebaseItem)
                        val intent = Intent("ITEM_ADDED_TO_SHOPPING_LIST").apply {
                            val gson = Gson()
                            val serializedItem = gson.toJson(item)
                            putExtra("ITEM", serializedItem)
                        }
                        sendBroadcast(intent)
                        // broadcast with permissions
                        sendBroadcast(intent, "com.example.shoppinglist.CUSTOM_PERMISSION")
                    }
                }).show()
        }

        binding.settings.setOnClickListener {
            val intent = Intent(this, AppSettings::class.java)
            startActivity(intent)
        }

        binding.map.setOnClickListener{
            val intent = Intent(this, MapsActivity::class.java)
            startActivity(intent)
        }
        Intent(applicationContext, LocationService::class.java).apply {
            action = LocationService.ACTION_START
            startService(this)
        }
    }

    override fun onRestart() {
        super.onRestart()
        Log.d("ActivityShopping", "On restart")
    }

    override fun onResume() {
        super.onResume()
        applySettings()
    }


    override fun onDestroy() {
        super.onDestroy()
        Intent(applicationContext, LocationService::class.java).apply {
            action = LocationService.ACTION_STOP
            startService(this)
        }
    }
    private fun subscribeToItemListUpdates() {
        itemRef.addSnapshotListener { querySnapshot, firebaseFirestoreException ->
            firebaseFirestoreException?.let {
                Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
                return@addSnapshotListener
            }
            querySnapshot?.let {
                val itemListMutable = mutableListOf<ShoppingItemFirebase>()
                for (document in it) {
                    val item = document.toObject(ShoppingItemFirebase::class.java)
                    item?.let {
                        itemListMutable.add(item)
                    }
                }
                val itemList = itemListMutable.toList()
                adapter.items = itemList
                adapter.notifyDataSetChanged()
                binding.rvShoppingItems.layoutManager = LinearLayoutManager(this@ShoppingActivity)
                binding.rvShoppingItems.adapter = adapter
            }
        }
    }

    private fun applySettings() {
        val sharedPreferences = getSharedPreferences("MySettings", Context.MODE_PRIVATE)
        // Retrieve data
        val background = sharedPreferences.getBoolean("background", false)
        val textColor = sharedPreferences.getString("textColor", "").toString()
        val applySettings = sharedPreferences.getBoolean("apply", false)
        if (applySettings) {
            CoroutineScope(Dispatchers.IO).launch {
                // Pass the textColor to the adapter
                val currentListOfItems = viewModel.getAllShoppingItems()
                withContext(Dispatchers.Main) {
                    adapter = ShoppingItemAdapter(currentListOfItems, viewModel, textColor)
                    binding.rvShoppingItems.layoutManager =
                        LinearLayoutManager(this@ShoppingActivity)
                    binding.rvShoppingItems.adapter = adapter
                    if (background) {
                        binding.rvShoppingItems.setBackgroundResource(com.google.android.material.R.color.cardview_dark_background)
                    } else {
                        binding.rvShoppingItems.setBackgroundResource(com.google.android.material.R.color.cardview_light_background)
                    }
                }
            }
        }
    }


}