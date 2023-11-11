package com.example.shoppinglist.settings

import com.example.shoppinglist.data.db.entities.ShoppingItem

interface AppSettingsChangedListener {
    fun changeAppSettings()
}