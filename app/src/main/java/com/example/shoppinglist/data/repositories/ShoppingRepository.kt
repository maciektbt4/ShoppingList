package com.example.shoppinglist.data.repositories

import com.example.shoppinglist.data.db.ShoppingDatabase
import com.example.shoppinglist.data.db.entities.ShoppingItem

class ShoppingRepository (
    private val db:ShoppingDatabase
){
    suspend fun upsert(item: ShoppingItem) = db.getShoppingDao().upsert(item)
    suspend fun delete(item: ShoppingItem) = db.getShoppingDao().delete(item)
    fun getAllShoppingItem() = db.getShoppingDao().getAllShoppingItems()
    suspend fun updateItem(oldName:String, item:ShoppingItem) = db.getShoppingDao().updateItemByName(oldName, item.name, item.amount, item.price)
}
