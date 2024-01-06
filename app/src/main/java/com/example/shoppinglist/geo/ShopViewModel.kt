package com.example.shoppinglist.geo

import androidx.lifecycle.ViewModel
import com.example.shoppinglist.data.db.entities.ShopFirebase
import com.example.shoppinglist.data.db.entities.ShoppingItemFirebase
import com.example.shoppinglist.data.repositories.ShopRepositoryFirebase
import com.example.shoppinglist.data.repositories.ShoppingRepositoryFirebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ShopViewModel(
//    private val repository: ShoppingRepository,
    private val repositoryFirebase: ShopRepositoryFirebase
): ViewModel(){
    fun insert(shop:ShopFirebase) = CoroutineScope(Dispatchers.Main).launch {
        repositoryFirebase.insert(shop)
    }
//    fun update(item:ShoppingItemFirebase, oldName:String = "") = CoroutineScope(Dispatchers.Main).launch {
//        repositoryFirebase.update(item, oldName)
//    }

    fun delete(item:ShopFirebase) = CoroutineScope(Dispatchers.Main).launch {
        repositoryFirebase.delete(item)
    }

    suspend fun getAllShops(): List<ShopFirebase> {
        return try {
            repositoryFirebase.getAllShops()
        } catch (e: Exception) {
            // Handle the exception, log it, or return an empty list as needed
            emptyList()
        }
    }

}