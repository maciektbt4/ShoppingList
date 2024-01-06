package com.example.shoppinglist.other

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.shoppinglist.R
import com.example.shoppinglist.data.db.entities.ShopFirebase
import com.example.shoppinglist.databinding.ShopBinding
import com.example.shoppinglist.geo.ShopViewModel

class ShopListAdapter(
    var shops: List<ShopFirebase>,
    private val viewModel: ShopViewModel,
    private val textColor: String = "black"
): RecyclerView.Adapter<ShopListAdapter.ShoppingViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShoppingViewHolder {

        val binding = ShopBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ShoppingViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ShoppingViewHolder, position: Int) {
        val currentShopItem =  shops[position]

        holder.binding.tvName.text = currentShopItem.name
        holder.binding.tvDescription.text = currentShopItem.description
        holder.binding.tvRadius.text = "${currentShopItem.radius}"
//        holder.binding.tvLongitude.text = "${currentShopItem.longitude}"
        holder.binding.tvLongitude.text = String.format("%.4f", currentShopItem.longitude)
//        holder.binding.tvLatitude.text = "${currentShopItem.latitude}"
        holder.binding.tvLatitude.text = String.format("%.4f", currentShopItem.latitude)
        holder.binding.ivDelete.setOnClickListener(){
            viewModel.delete(currentShopItem)
        }
    }

    override fun getItemCount(): Int {
        return shops.size
    }

//    inner class ShoppingViewHolder(itemView: View, binding: ActivityShoppingBinding):RecyclerView.ViewHolder(itemView)
    inner class ShoppingViewHolder(val binding: ShopBinding): RecyclerView.ViewHolder(binding.root)
}