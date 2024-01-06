package com.example.shoppinglist.data.db.entities

data class ShopFirebase (
    var name: String = "",
    var description: String = "",
    var radius: Float = -1.0f,
    var longitude: Double = -1.0,
    var latitude: Double = -1.0,
)