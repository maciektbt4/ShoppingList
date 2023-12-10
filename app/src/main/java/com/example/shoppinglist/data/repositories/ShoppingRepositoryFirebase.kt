package com.example.shoppinglist.data.repositories

import android.util.Log
import com.example.shoppinglist.data.db.entities.ShoppingItemFirebase
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import com.google.firebase.firestore.SetOptions


class ShoppingRepositoryFirebase (
){
    private val itemsCollectionReference = Firebase.firestore.collection("items")
    fun insert(item: ShoppingItemFirebase) = CoroutineScope(Dispatchers.IO).launch {
        try {
            itemsCollectionReference.add(item)
            Log.d("FirebaseSave","Data is saved:\n")
        }
        catch (e:Exception){
            Log.d("FirebaseSave","Data cannot be save:\n" + e.message
            )
        }
    }

    fun update(item: ShoppingItemFirebase, oldName:String = "") = CoroutineScope(Dispatchers.IO).launch {
        var oldName_ = oldName
        if (oldName_ == ""){
            oldName_ = item.name
        }
        try {
            val itemToUpdate = itemsCollectionReference
                .whereEqualTo("name", oldName_)
                .get().await()
            if(itemToUpdate.documents.isNotEmpty()){
                for (document in itemToUpdate){
                    try{
                        itemsCollectionReference.document(document.id).set(
                            item,
                            SetOptions.merge()
                        ).await()
                    }
                    catch (e:Exception){
                        Log.d("FirebaseUpdate", "Error message:\n" + e.message)
                    }
                }
            }

            Log.d("FirebaseUpdate","Data is updated:\n")
        }
        catch (e:Exception){
            Log.d("FirebaseUpdate",
                "Data cannot be updated:\n" + e.message
            )
        }
    }

    fun delete(item: ShoppingItemFirebase) = CoroutineScope(Dispatchers.IO).launch {
        try {
            val itemToUpdate = itemsCollectionReference
                .whereEqualTo("name", item.name)
                .whereEqualTo("price", item.price)
                .whereEqualTo("amount", item.amount)
                .whereEqualTo("bought", item.bought)
                .get().await()
            if(itemToUpdate.documents.isNotEmpty()){
                for (document in itemToUpdate){
                    try{
                        itemsCollectionReference.document(document.id).delete().await()
                    }
                    catch (e:Exception){
                        Log.d("FirebaseDelete", "Error message:\n" + e.message)
                    }
                }
            }

            Log.d("FirebaseDelete","Data is deleted:\n")
        }
        catch (e:Exception){
            Log.d("FirebaseDelete",
                "Data cannot be deleted:\n" + e.message
            )
        }
    }

    suspend fun getAllShoppingItem(): List<ShoppingItemFirebase> {
        return try {
            val queryItems = itemsCollectionReference.get().await()
            val itemListMutable = mutableListOf<ShoppingItemFirebase>()

            for (document in queryItems.documents) {
                val item = document.toObject(ShoppingItemFirebase::class.java)
                item?.let {
                    itemListMutable.add(it)
                }
            }
            itemListMutable.toList()
        } catch (e: Exception) {
            Log.d("FirebaseSave", "Data cannot be read:\n" + e.message)
            listOf() // Return an empty list in case of failure or exception
        }
    }
}

