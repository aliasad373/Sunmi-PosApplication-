package com.mindcrafts.posapp.activitiese

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.mindcrafts.posapp.R
import com.mindcrafts.posapp.activitiese.adapters.HomeScreenAdapter
import com.mindcrafts.posapp.activitiese.adapters.HomeScreenAdapter.ClickListenerHomeScreen
import com.mindcrafts.posapp.databinding.ActivityHomeScreenBinding
import com.mindcrafts.posapp.utils.HomeItemModel

class HomeScreenActivity : AppCompatActivity() ,ClickListenerHomeScreen{
    lateinit var binding: ActivityHomeScreenBinding
    var itemList: MutableList<HomeItemModel> = mutableListOf()

    val itemAdapter: HomeScreenAdapter by lazy {
        HomeScreenAdapter()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityHomeScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        itemList.add(HomeItemModel(id = 1, Name = "Sales", Image = R.drawable.ic_sales))
        itemList.add(HomeItemModel(id = 2, Name = "Settlement", Image = R.drawable.ic_settlement))
        itemList.add(
            HomeItemModel(
                id = 3, Name = "Pre-\n" + "Authorization", Image = R.drawable.ic_authorization
            )
        )
        itemList.add(
            HomeItemModel(
                id = 4, Name = "Pre-Auth \n" + "Completion", Image = R.drawable.ic_pre_authorization
            )
        )
        itemList.add(
            HomeItemModel(
                id = 5,
                Name = "TIP Transaction",
                Image = R.drawable.ic_tip_transaction
            )
        )
        itemList.add(
            HomeItemModel(
                id = 6,
                Name = "TIP Completion",
                Image = R.drawable.ic_tip_completion
            )
        )
        itemList.add(HomeItemModel(id = 7, Name = "Refund", Image = R.drawable.ic_refund))
        itemList.add(HomeItemModel(id = 8, Name = "Void", Image = R.drawable.ic_void))
        itemList.add(
            HomeItemModel(
                id = 9,
                Name = "Bill Payment",
                Image = R.drawable.ic_bill_payment
            )
        )
        itemList.add(HomeItemModel(id = 10, Name = "Purchases", Image = R.drawable.ic_purchases))
        itemList.add(HomeItemModel(id = 11, Name = "QR Payment", Image = R.drawable.ic_qr))
        itemList.add(HomeItemModel(id = 12, Name = "Settings", Image = R.drawable.ic_setting))


        binding.rvHomeItems.adapter = itemAdapter

        itemAdapter.listener = this
        itemAdapter.setData(itemList)


    }

    override fun itemClicked(item: HomeItemModel) {
        if (item.id == 2){
            val intent = Intent(this@HomeScreenActivity, TransactionSuccessfullActivity::class.java)
            startActivity(intent)
        }
        if (item.id == 3){
            val intent = Intent(this@HomeScreenActivity, TransactionFailedActivity::class.java)
            startActivity(intent)
        }
        if (item.id == 4){
            val intent = Intent(this@HomeScreenActivity, BillPaymentsActivity::class.java)
            startActivity(intent)
        }
        if (item.id == 5){
            val intent = Intent(this@HomeScreenActivity, AmountEnterActivity::class.java)
            startActivity(intent)
        }
        if (item.id == 6){
            val intent = Intent(this@HomeScreenActivity, TransactionDetailActivity::class.java)
            startActivity(intent)
        }
        if (item.id == 7){
            val intent = Intent(this@HomeScreenActivity, AllTransactionActivity::class.java)
            startActivity(intent)
        }

    }
}