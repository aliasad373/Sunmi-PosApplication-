package com.mindcrafts.posapp.activitiese

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.mindcrafts.posapp.AsteriskPasswordTransformation
import com.mindcrafts.posapp.R
import com.mindcrafts.posapp.activitiese.TransactionFailedActivity
import com.mindcrafts.posapp.databinding.ActivityPinCodeLoginBinding

class PinCodeLoginActivity : AppCompatActivity() {
    lateinit var binding: ActivityPinCodeLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityPinCodeLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.edtPinCode.showSoftInputOnFocus = false
        binding.edtPinCode.transformationMethod = AsteriskPasswordTransformation()

        initClickListener()
    }

    fun initClickListener(){
        binding.btn0.setOnClickListener {
            binding.edtPinCode.append("0")
        }
        binding.btn1.setOnClickListener {
            binding.edtPinCode.append("1")
        }
        binding.btn2.setOnClickListener {
            binding.edtPinCode.append("2")
        }
        binding.btn3.setOnClickListener {
            binding.edtPinCode.append("3")
        }
        binding.btn4.setOnClickListener {
            binding.edtPinCode.append("4")
        }
        binding.btn5.setOnClickListener {
            binding.edtPinCode.append("5")
        }
        binding.btn6.setOnClickListener {
            binding.edtPinCode.append("6")
        }
        binding.btn7.setOnClickListener {
            binding.edtPinCode.append("7")
        }
        binding.btn8.setOnClickListener {
            binding.edtPinCode.append("8")
        }
        binding.btn9.setOnClickListener {
            binding.edtPinCode.append("9")
        }

        binding.btnLogin.setOnClickListener {
            val intent = Intent(this@PinCodeLoginActivity, HomeScreenActivity::class.java)
            startActivity(intent)
        }
    }
}