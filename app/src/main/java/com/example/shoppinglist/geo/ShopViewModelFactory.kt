package com.example.shoppinglist.geo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.shoppinglist.data.repositories.ShopRepositoryFirebase
import com.example.shoppinglist.data.repositories.ShoppingRepositoryFirebase

@Suppress("UNCHECKED_CAST")
class ShopViewModelFactory(
    private val repository: ShopRepositoryFirebase
): ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ShopViewModel(repository) as T
    }
}