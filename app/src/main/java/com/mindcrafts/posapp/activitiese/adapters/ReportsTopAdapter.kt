package com.mindcrafts.posapp.activitiese.adapters

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.mindcrafts.posapp.R
import com.mindcrafts.posapp.databinding.ItemHomeScreenBinding
import com.mindcrafts.posapp.databinding.ItemReportTopBarBinding
import com.mindcrafts.posapp.utils.HomeItemModel
import com.mindcrafts.posapp.utils.ReportsTopModel

class ReportsTopAdapter : RecyclerView.Adapter<ReportsTopAdapter.UserViewHolder>() {

    var items: MutableList<ReportsTopModel> = mutableListOf()
    var listener: ClickListenerReports? = null

    inner class UserViewHolder(val binding: ItemReportTopBarBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val binding = ItemReportTopBarBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return UserViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val item = items[position]
        holder.binding.title.text = item.title

        if (item.isSelected) {
            holder.binding.title.backgroundTintList =
                ColorStateList.valueOf(ContextCompat.getColor(holder.itemView.context, R.color.colorPrimary))
        } else {
            holder.binding.title.backgroundTintList =
                ColorStateList.valueOf(ContextCompat.getColor(holder.itemView.context, R.color.white))
        }


        holder.binding.root.setOnClickListener {
            listener?.selectFragment(item)
        }
    }

    override fun getItemCount(): Int = items.size


    fun setData(list: MutableList<ReportsTopModel>) {
        items.clear()
        items.addAll(list)
        notifyDataSetChanged()
    }

    interface ClickListenerReports {
        fun selectFragment(item: ReportsTopModel)
    }
}