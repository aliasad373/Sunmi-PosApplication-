package com.mindcrafts.posapp.activitiese.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mindcrafts.posapp.databinding.ItemAllTransactionBinding
import com.mindcrafts.posapp.utils.AllTransactionModel

class AllTransactionAdapter : RecyclerView.Adapter<AllTransactionAdapter.TransactionViewHolder>() {

    var items: MutableList<AllTransactionModel> = mutableListOf()
    var listener: ClickListenerAllTransactionScreen? = null

    inner class TransactionViewHolder(val binding: ItemAllTransactionBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val binding = ItemAllTransactionBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return TransactionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        val item = items[position]
        holder.binding.txtAmount.text = item.amount
        holder.binding.txtPan.text = item.panNumber
        holder.binding.txtDate.text = item.date
        holder.binding.txtTime.text = item.time
        holder.binding.txtBatchNo.text = item.batchNo
        holder.binding.txtStatus.text = item.status


        holder.binding.root.setOnClickListener {
            listener?.itemClicked(item)
        }
    }

    override fun getItemCount(): Int = items.size


    fun setData(list: MutableList<AllTransactionModel>) {
        items.clear()
        items.addAll(list)
        notifyDataSetChanged()
    }

    interface ClickListenerAllTransactionScreen {
        fun itemClicked(item: AllTransactionModel)
    }
}