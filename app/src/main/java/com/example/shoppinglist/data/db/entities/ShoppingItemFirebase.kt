package com.example.shoppinglist.data.db.entities

import java.io.Serializable

data class ShoppingItemFirebase(
    var name: String = "",
    var amount: Int = -1,
    var price: Float = -1.0f,
    var bought:Boolean = false
): Serializable