package com.mindcrafts.posapp.activitiese

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.mindcrafts.posapp.R
import com.mindcrafts.posapp.activitiese.adapters.AllTransactionAdapter
import com.mindcrafts.posapp.activitiese.adapters.AllTransactionAdapter.ClickListenerAllTransactionScreen
import com.mindcrafts.posapp.database.DBInstance
import com.mindcrafts.posapp.database.entity.Transaction
import com.mindcrafts.posapp.databinding.ActivityAllTransactionBinding
import com.mindcrafts.posapp.storage.SharedPrefHelper
import com.mindcrafts.posapp.utils.Utility
import java.util.concurrent.Executors


class AllTransactionActivity : AppCompatActivity(), ClickListenerAllTransactionScreen {
    lateinit var binding: ActivityAllTransactionBinding
    private var db: DBInstance? = null
    private val executor = Executors.newSingleThreadExecutor()

    val adapter: AllTransactionAdapter by lazy {
        AllTransactionAdapter()
    }

    //var transactionList: MutableList<Transaction> = mutableListOf()
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAllTransactionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.tvMid.setText("MID: " + SharedPrefHelper.getInstance(this).getMID("MID"))
        binding.tvTid.setText("TID: " + SharedPrefHelper.getInstance(this).getTID("TID"))
        binding.timeStamp.text = Utility.getCurrentTimestamp()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        binding.backBtn.setOnClickListener {
            var intent = Intent(this, Home::class.java);
            startActivity(intent)
            finish()
        }
        db = DBInstance.getInstance(this)
        fetchUsersFromDatabase()

    }

    override fun itemClicked(item: Transaction) {
        var id   = item.id
        var intent = Intent(this@AllTransactionActivity, TransactionDetailActivity::class.java);
        intent.putExtra("id",id);
       startActivity(intent);
    }

    private fun fetchUsersFromDatabase() {
        executor.execute {
            val users = db?.userDao()!!.unsettledTransactions

            runOnUiThread {
                if(users.isEmpty()){
                    binding.noRecord.visibility = View.VISIBLE
                    binding.rvAllTransactionItems.visibility = View.GONE
                }else{
                    binding.rvAllTransactionItems.adapter = adapter
                    adapter.listener = this
                    adapter.setData(users)
                }
            }
        }
    }
}