package com.example.shoppinglist.other

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.shoppinglist.R
import com.example.shoppinglist.data.db.entities.ShoppingItem
import com.example.shoppinglist.ui.shoppinglist.ShoppingViewModel
import androidx.core.content.ContextCompat
import com.example.shoppinglist.databinding.ShoppingItemBinding

class ShoppingItemAdapter(
    var items: List<ShoppingItem>,
    private val viewModel: ShoppingViewModel,
    private val textColor: String = "black"
): RecyclerView.Adapter<ShoppingItemAdapter.ShoppingViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShoppingViewHolder {
        val binding = ShoppingItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ShoppingViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ShoppingViewHolder, position: Int) {
        val currentShoppingItem =  items[position]

        // Get the color resource ID based on your textColor value
        val textColorResourceId = when (textColor) {
            "white" -> R.color.white
            "black" -> R.color.black
            "green" -> R.color.green
            "red" -> R.color.red
            "blue" -> R.color.blue
            else -> R.color.black // Default to black if an invalid color is provided
        }
        // Set the text color using the resource ID
        holder.binding.tvName.setTextColor(ContextCompat.getColor(holder.itemView.context, textColorResourceId))
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