package com.mindcrafts.posapp.activitiese

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.mindcrafts.posapp.R
import com.mindcrafts.posapp.activitiese.adapters.HomeScreenAdapter
import com.mindcrafts.posapp.activitiese.adapters.ReportsTopAdapter
import com.mindcrafts.posapp.activitiese.reportfragments.OpenBatchFragment
import com.mindcrafts.posapp.activitiese.reportfragments.SettledBatchesFragment
import com.mindcrafts.posapp.activitiese.reportfragments.SettlementSummaryFragment
import com.mindcrafts.posapp.activitiese.reportfragments.VoidedTransactionsFragment
import com.mindcrafts.posapp.databinding.ActivityReportsBinding
import com.mindcrafts.posapp.utils.HomeItemModel
import com.mindcrafts.posapp.utils.ReportsTopModel

class ReportsActivity : AppCompatActivity(), ReportsTopAdapter.ClickListenerReports {
    var binding: ActivityReportsBinding? = null

    val itemAdapter: ReportsTopAdapter by lazy {
        ReportsTopAdapter()
    }
    var fragmentList: MutableList<ReportsTopModel> = mutableListOf()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityReportsBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        binding?.btnBack?.setOnClickListener {
            val intent = Intent(this@ReportsActivity, HomeScreenActivity::class.java);
            startActivity(intent);
        }

        fragmentList.add(ReportsTopModel(id = 1, title = "Open Batch", isSelected = true))
        fragmentList.add(ReportsTopModel(id = 2, title = "Voided Transaction", isSelected = false))
        fragmentList.add(ReportsTopModel(id = 3, title = "Settled Batches", isSelected = false))
        fragmentList.add(ReportsTopModel(id = 4, title = "Settlement Summary", isSelected = false))


        binding?.rvTopBar?.adapter = itemAdapter

        itemAdapter.listener = this

        itemAdapter.setData(fragmentList)

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentFrameLayout, OpenBatchFragment())
            .commit()

    }

    override fun selectFragment(item: ReportsTopModel) {
        fragmentList.forEach { fragmentItem ->
            fragmentItem.isSelected = fragmentItem.id == item.id
        }
        itemAdapter.notifyDataSetChanged()
        if (item.id == 1) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentFrameLayout, OpenBatchFragment())
                .commit()
        } else if (item.id == 2) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentFrameLayout, VoidedTransactionsFragment())
                .commit()
        } else if (item.id == 3) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentFrameLayout, SettledBatchesFragment())
                .commit()
        } else {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentFrameLayout, SettlementSummaryFragment())
                .commit()
        }


    }
}