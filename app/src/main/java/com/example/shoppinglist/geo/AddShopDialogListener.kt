package com.example.shoppinglist.geo

import com.example.shoppinglist.data.db.entities.ShopFirebase

interface AddShopDialogListener {
    fun onAddButtonClicked(item:ShopFirebase)
}