package com.example.shoppinglist.other

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.shoppinglist.R
import com.example.shoppinglist.data.db.entities.ShoppingItem
import com.example.shoppinglist.ui.shoppinglist.ShoppingViewModel
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
//import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import com.example.shoppinglist.data.db.ShoppingDatabase
import com.example.shoppinglist.data.repositories.ShoppingRepository
import com.example.shoppinglist.databinding.ActivityShoppingBinding
import com.example.shoppinglist.databinding.ShoppingItemBinding

class ShoppingItemAdapter(
    var items: List<ShoppingItem>,
    private val viewModel: ShoppingViewModel
): RecyclerView.Adapter<ShoppingItemAdapter.ShoppingViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShoppingViewHolder {
        val binding = ShoppingItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ShoppingViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ShoppingViewHolder, position: Int) {
        val currentShoppingItem =  items[position]
        holder.binding.tvName.text = currentShoppingItem.name
        holder.binding.tvAmount.text = "${currentShoppingItem.amount}"

        holder.binding.ivDelete.setOnClickListener(){
            viewModel.delete(currentShoppingItem)
        }

        holder.binding.ivMinus.setOnClickListener(){
            if(currentShoppingItem.amount > 0){
                currentShoppingItem.amount--
                viewModel.upsert(currentShoppingItem)
            }
        }

        holder.binding.ivPlus.setOnClickListener(){
            currentShoppingItem.amount++
            viewModel.upsert(currentShoppingItem)
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

//    inner class ShoppingViewHolder(itemView: View, binding: ActivityShoppingBinding):RecyclerView.ViewHolder(itemView)
    inner class ShoppingViewHolder(val binding: ShoppingItemBinding): RecyclerView.ViewHolder(binding.root)
}