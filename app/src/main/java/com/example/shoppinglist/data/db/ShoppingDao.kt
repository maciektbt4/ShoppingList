package com.example.shoppinglist.data.db
import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.shoppinglist.data.db.entities.ShoppingItem

@Dao
interface ShoppingDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(item: ShoppingItem)

    @Delete
    suspend fun delete(item: ShoppingItem)

    @Query("SELECT * FROM shopping_items")
    fun getAllShoppingItems(): LiveData<List<ShoppingItem>>

    @Query("UPDATE shopping_items SET item_name = :newName, item_amount = :newAmount, item_price = :newPrice WHERE item_name = :oldName")
    suspend fun updateItemByName(oldName:String, newName: String, newAmount: Int, newPrice: Float)
}