package com.example.shoppinglist.ui.shoppinglist

import androidx.lifecycle.ViewModel
import com.example.shoppinglist.data.db.entities.ShoppingItemFirebase
import com.example.shoppinglist.data.repositories.ShoppingRepositoryFirebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ShoppingViewModel(
//    private val repository: ShoppingRepository,
    private val repositoryFirebase: ShoppingRepositoryFirebase
): ViewModel(){
    fun insert(item:ShoppingItemFirebase) = CoroutineScope(Dispatchers.Main).launch {
        repositoryFirebase.insert(item)
    }
    fun update(item:ShoppingItemFirebase, oldName:String = "") = CoroutineScope(Dispatchers.Main).launch {
        repositoryFirebase.update(item, oldName)
    }

    fun delete(item:ShoppingItemFirebase) = CoroutineScope(Dispatchers.Main).launch {
        repositoryFirebase.delete(item)
    }

    suspend fun getAllShoppingItems(): List<ShoppingItemFirebase> {
        return try {
            repositoryFirebase.getAllShoppingItem()
        } catch (e: Exception) {
            // Handle the exception, log it, or return an empty list as needed
            emptyList()
        }
    }

}