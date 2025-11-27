package com.mindcrafts.posapp.activitiese;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.mindcrafts.posapp.R;
import com.mindcrafts.posapp.databinding.ActivityEnterAmountV2Binding;
import com.mindcrafts.posapp.emv.ICProcessActivity;
import com.mindcrafts.posapp.storage.SharedPrefHelper;

public class EnterAmountV2 extends AppCompatActivity {
   ActivityEnterAmountV2Binding binding;
   String rawAmount;
    private StringBuilder amountBuilder = new StringBuilder();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEnterAmountV2Binding.inflate(getLayoutInflater());
        EdgeToEdge.enable(this);
        setContentView(binding.getRoot());
       //
        binding.tvMid.setText("MID: " + SharedPrefHelper.getInstance(this).getMID("MID"));
        binding.tvTid.setText("TID: " + SharedPrefHelper.getInstance(this).getTID("TID"));
         //
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
      iniAmountPad();
      initControl();
    }

    private void initControl() {
        binding.btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String amount  = binding.amnt.getText().toString();
                if(!amount.equals("0.00")){
                    Intent intent = new Intent(EnterAmountV2.this, ICProcessActivity.class);
                    intent.putExtra("cart", "");
                    intent.putExtra("amount", amount);
                    startActivity(intent);
                    finish();
                }else{
                    Toast.makeText(getBaseContext(), "Please enter amount", Toast.LENGTH_SHORT).show();
                }

            }
        });

        binding.btnBackSpace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rawAmount = "0";
                amountBuilder.setLength(0);
                binding.amnt.setText("0.00");

            }
        });
        binding.btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EnterAmountV2.this, Home.class);
                startActivity(intent);
                finish();
            }
        });
        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EnterAmountV2.this, Home.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void iniAmountPad() {

        int[] buttonIds = {
                R.id.btn0,R.id.btn00, R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4,
                R.id.btn5, R.id.btn6, R.id.btn7, R.id.btn8, R.id.btn9
        };

        // Set number button click listeners
        for (int id : buttonIds) {
            TextView btn = findViewById(id);
            btn.setOnClickListener(v -> onNumberClick(((TextView)v).getText().toString()));
        }
    }
    private void onNumberClick(String number) {
        amountBuilder.append(number);
        rawAmount = amountBuilder.toString();
        binding.amnt.setText(toDisplayAmount(rawAmount));
    }

    private String toDisplayAmount(String value) {
        if (value == null || value.isEmpty()) {
            return "0.00";
        }

        String result = value;

        // Pad to ensure at least 3 digits (like 0.12)
        if (result.length() <= 3) {
            result = String.format("%03d", Integer.parseInt(result));
        }

        // Insert decimal point before the last two digits
        int len = result.length();
        return result.substring(0, len - 2) + "." + result.substring(len - 2);
    }


}