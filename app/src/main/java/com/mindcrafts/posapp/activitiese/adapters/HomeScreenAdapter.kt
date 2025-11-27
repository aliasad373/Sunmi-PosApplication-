package com.mindcrafts.posapp.activitiese.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mindcrafts.posapp.databinding.ItemHomeScreenBinding
import com.mindcrafts.posapp.utils.HomeItemModel

class HomeScreenAdapter : RecyclerView.Adapter<HomeScreenAdapter.UserViewHolder>() {

    var items: MutableList<HomeItemModel> = mutableListOf()
    var listener: ClickListenerHomeScreen? = null

    inner class UserViewHolder(val binding: ItemHomeScreenBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val binding = ItemHomeScreenBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return UserViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val item = items[position]
        holder.binding.txtItem.text = item.Name
        holder.binding.ivImage.setImageResource(item.Image)

        holder.binding.root.setOnClickListener {
            listener?.itemClicked(item)
        }
    }

    override fun getItemCount(): Int = items.size


    fun setData(list: MutableList<HomeItemModel>) {
        items.clear()
        items.addAll(list)
        notifyDataSetChanged()
    }

    interface ClickListenerHomeScreen {
        fun itemClicked(item: HomeItemModel)
    }
}