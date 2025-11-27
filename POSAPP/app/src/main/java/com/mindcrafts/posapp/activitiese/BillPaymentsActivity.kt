package com.mindcrafts.posapp.activitiese

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.mindcrafts.posapp.R
import com.mindcrafts.posapp.activitiese.HomeScreenActivity
import com.mindcrafts.posapp.databinding.ActivityBillPaymentsBinding

class BillPaymentsActivity : AppCompatActivity() {
    lateinit var binding: ActivityBillPaymentsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding= ActivityBillPaymentsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.btnBack.setOnClickListener {
            val intent = Intent(this@BillPaymentsActivity, HomeScreenActivity::class.java)
            startActivity(intent)
        }
    }
}