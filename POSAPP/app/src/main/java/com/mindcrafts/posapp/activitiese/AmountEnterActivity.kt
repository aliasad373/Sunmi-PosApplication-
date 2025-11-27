package com.mindcrafts.posapp.activitiese

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.mindcrafts.posapp.R
import com.mindcrafts.posapp.activitiese.PinCodeLoginActivity
import com.mindcrafts.posapp.databinding.ActivityAmountEnterBinding

class AmountEnterActivity : AppCompatActivity() {
    lateinit var binding: ActivityAmountEnterBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAmountEnterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        binding.edtAmount.showSoftInputOnFocus = false
        initClickListener()


    }

    fun initClickListener() {
        binding.btn0.setOnClickListener {
            binding.edtAmount.append("0")
        }
        binding.btn1.setOnClickListener {
            binding.edtAmount.append("1")
        }
        binding.btn2.setOnClickListener {
            binding.edtAmount.append("2")
        }
        binding.btn3.setOnClickListener {
            binding.edtAmount.append("3")
        }
        binding.btn4.setOnClickListener {
            binding.edtAmount.append("4")
        }
        binding.btn5.setOnClickListener {
            binding.edtAmount.append("5")
        }
        binding.btn6.setOnClickListener {
            binding.edtAmount.append("6")
        }
        binding.btn7.setOnClickListener {
            binding.edtAmount.append("7")
        }
        binding.btn8.setOnClickListener {
            binding.edtAmount.append("8")
        }
        binding.btn9.setOnClickListener {
            binding.edtAmount.append("9")
        }
        binding.btndot.setOnClickListener {
            binding.edtAmount.append(".")
        }
        binding.btnBackSpace.setOnClickListener {

            val current = binding.edtAmount.text.toString()
            if (current.isNotEmpty()) {
                binding.edtAmount.setText(current.dropLast(1))
                binding.edtAmount.setSelection(binding.edtAmount.text!!.length)
            }
        }
        binding.clProceed.setOnClickListener {
            val intent = Intent(this@AmountEnterActivity, HomeScreenActivity::class.java)
            startActivity(intent)
        }
        binding.btnBack.setOnClickListener {
            val intent = Intent(this@AmountEnterActivity, HomeScreenActivity::class.java)
            startActivity(intent)
        }
    }
}