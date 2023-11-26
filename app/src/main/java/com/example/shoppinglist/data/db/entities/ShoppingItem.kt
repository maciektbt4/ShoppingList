package com.example.shoppinglist.data.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "shopping_items")
data class ShoppingItem(
    @ColumnInfo(name="item_name")
    var name: String,
    @ColumnInfo(name="item_amount")
    var amount:Int,
    @ColumnInfo(name="item_price")
    var price:Float,
    @ColumnInfo(name="item_Bought")
    var wasBought:Boolean
):Serializable {
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null
}