package com.mindcrafts.posapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.mindcrafts.posapp.app.MyApplication;
import com.mindcrafts.posapp.databinding.ActivityMainBinding;
import com.mindcrafts.posapp.emv.ICProcessActivity;
import com.sunmi.pay.hardware.aidlv2.emv.EMVOptV2;

public class MainActivity extends AppCompatActivity {
   ActivityMainBinding binding;
    private EMVOptV2 mEMVOptV2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.test.setText("Hello World");

        binding.startTransaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ICProcessActivity.class);
                intent.putExtra("amnt", "10");
                startActivity(intent);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!MyApplication.app.isConnectPaySDK()) {
            MyApplication.app.bindPaySDKService();
        }
    }
}