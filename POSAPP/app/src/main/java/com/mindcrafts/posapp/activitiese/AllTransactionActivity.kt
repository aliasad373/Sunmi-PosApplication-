package com.mindcrafts.posapp.activitiese

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.mindcrafts.posapp.R
import com.mindcrafts.posapp.activitiese.HomeScreenActivity
import com.mindcrafts.posapp.activitiese.adapters.AllTransactionAdapter
import com.mindcrafts.posapp.activitiese.adapters.AllTransactionAdapter.ClickListenerAllTransactionScreen
import com.mindcrafts.posapp.databinding.ActivityAllTransactionBinding
import com.mindcrafts.posapp.utils.AllTransactionModel

class AllTransactionActivity : AppCompatActivity(), ClickListenerAllTransactionScreen {
    lateinit var binding: ActivityAllTransactionBinding
    val adapter: AllTransactionAdapter by lazy {
        AllTransactionAdapter()
    }

    var transactionList: MutableList<AllTransactionModel> = mutableListOf()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAllTransactionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.rvAllTransactionItems.adapter = adapter

        adapter.listener = this

        transactionList.add(AllTransactionModel(amount =" Rs 20000", panNumber =  "1234 **** **** 7890", date = "20-12-2025", time = "08:00Am", batchNo = "5", status = "settled"))
        transactionList.add(AllTransactionModel(amount =" Rs 20000", panNumber =  "1234 **** **** 7890", date = "20-12-2025", time = "08:00Am", batchNo = "5", status = "Not Settled"))
        transactionList.add(AllTransactionModel(amount =" Rs 20000", panNumber =  "1234 **** **** 7890", date = "20-12-2025", time = "08:00Am", batchNo = "5", status = "Void"))

        adapter.setData(transactionList)
        binding.btnBack.setOnClickListener {
            val intent = Intent(this@AllTransactionActivity, HomeScreenActivity::class.java)
            startActivity(intent)
        }
    }

    override fun itemClicked(item: AllTransactionModel) {
        TODO("Not yet implemented")
    }
}