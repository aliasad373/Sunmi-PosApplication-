package com.mindcrafts.posapp.activitiese

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.mindcrafts.posapp.R
import com.mindcrafts.posapp.activitiese.PinCodeLoginActivity
import com.mindcrafts.posapp.databinding.ActivityAmountEnterBinding
import com.mindcrafts.posapp.emv.ICProcessActivity
import java.text.NumberFormat
import java.util.Locale

class AmountEnterActivity : AppCompatActivity() {
    lateinit var binding: ActivityAmountEnterBinding

    private var rawAmount = "" // keeps only digits

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
        val edt = binding.edtAmount

        fun updateDisplay() {
            edt.setText(rawAmount.toCurrencyFormat())
            edt.setSelection(edt.text!!.length) // cursor at end
        }

        binding.btn0.setOnClickListener { rawAmount += "0"; updateDisplay() }
        binding.btn1.setOnClickListener { rawAmount += "1"; updateDisplay() }
        binding.btn2.setOnClickListener { rawAmount += "2"; updateDisplay() }
        binding.btn3.setOnClickListener { rawAmount += "3"; updateDisplay() }
        binding.btn4.setOnClickListener { rawAmount += "4"; updateDisplay() }
        binding.btn5.setOnClickListener { rawAmount += "5"; updateDisplay() }
        binding.btn6.setOnClickListener { rawAmount += "6"; updateDisplay() }
        binding.btn7.setOnClickListener { rawAmount += "7"; updateDisplay() }
        binding.btn8.setOnClickListener { rawAmount += "8"; updateDisplay() }
        binding.btn9.setOnClickListener { rawAmount += "9"; updateDisplay() }

        // backspace
        binding.btnBackSpace.setOnClickListener {
            if (rawAmount.isNotEmpty()) {
                rawAmount = rawAmount.dropLast(1)
                updateDisplay()
            }
        }

        // Proceed
        binding.clProceed.setOnClickListener {
            if (rawAmount.isEmpty()) {
                Toast.makeText(this, "Please Enter Amount First", Toast.LENGTH_SHORT).show()
            } else {
                val amount = rawAmount.toCurrencyFormat()
                Log.d("checkingAmount", "initClickListener:$amount ")
                val intent = Intent(this@AmountEnterActivity, ICProcessActivity::class.java)
                intent.putExtra("amount", amount)
                startActivity(intent)
            }
        }

        binding.btnBack.setOnClickListener {
            val intent = Intent(this@AmountEnterActivity, HomeScreenActivity::class.java)
            startActivity(intent)
        }
    }


}

fun String.toCurrencyFormat(): String {
    // Filter only digits
    val digits = this.filter { it.isDigit() }

    // Ensure at least 3 digits
    val normalized = digits.padStart(3, '0')

    // Split into integer + decimal
    val integerPart = normalized.dropLast(2)
    val decimalPart = normalized.takeLast(2)

    // Format integer part with commas
    val formattedInt = NumberFormat.getNumberInstance(Locale.US).format(integerPart.toLong())

    return "$formattedInt.$decimalPart"
}

private fun String.toDisplayAmount(): String {
    //0 -> 0.00
    //12 -> 0.12
    var result = this
    if (this.length <= 3) {
        result = result.padStart(3, '0')
    }
    // add '.'
    return result.substring(0, result.length - 2) + "." + result.substring(result.length-2)
}
