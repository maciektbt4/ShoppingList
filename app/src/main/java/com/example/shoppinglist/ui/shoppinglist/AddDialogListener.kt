package com.example.shoppinglist.ui.shoppinglist

import com.example.shoppinglist.data.db.entities.ShoppingItemFirebase

interface AddDialogListener {
    fun onAddButtonClicked(item:ShoppingItemFirebase)
}