package com.mindcrafts.posapp.activitiese.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mindcrafts.posapp.database.entity.Transaction
import com.mindcrafts.posapp.databinding.ItemAllTransactionBinding
import com.mindcrafts.posapp.iso8583.isoUtils.IsoUtils
import com.mindcrafts.posapp.utils.AllTransactionModel

class AllTransactionAdapter : RecyclerView.Adapter<AllTransactionAdapter.TransactionViewHolder>() {

    var items: MutableList<Transaction> = mutableListOf()
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
        holder.binding.amount.text =  IsoUtils.getClearAmount(item.Amount);
        holder.binding.PAN.text = IsoUtils.maskedPAN(item.PAN)
        holder.binding.datetime.text = item.date + " " + item.time
        holder.binding.root.setOnClickListener {
            listener?.itemClicked(item)
        }
    }

    override fun getItemCount(): Int = items.size


    fun setData(list: MutableList<Transaction>) {
        items.clear()
        items.addAll(list)
        notifyDataSetChanged()
    }

    interface ClickListenerAllTransactionScreen {
        fun itemClicked(item: Transaction);
    }
}