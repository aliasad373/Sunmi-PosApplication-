package com.mindcrafts.posapp.activitiese;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.mindcrafts.posapp.R;
import com.mindcrafts.posapp.activitiese.settlementreport.SummeryReport;
import com.mindcrafts.posapp.database.DBInstance;
import com.mindcrafts.posapp.database.entity.Transaction;
import com.mindcrafts.posapp.databinding.ActivitySettlementBinding;
import com.mindcrafts.posapp.iso8583.builder.SettlementBuilder;
import com.mindcrafts.posapp.iso8583.isoUtils.IsoConfig;
import com.mindcrafts.posapp.iso8583.isoUtils.IsoUtils;
import com.mindcrafts.posapp.iso8583.parser.ISOParser;
import com.mindcrafts.posapp.network.BatchUpload;
import com.mindcrafts.posapp.network.NetworkUtil;
import com.mindcrafts.posapp.network.callback.BatchUploadCallback;
import com.mindcrafts.posapp.network.callback.NetworkCallback;
import com.mindcrafts.posapp.storage.SharedPrefHelper;
import com.mindcrafts.posapp.utils.ByteUtil;
import com.mindcrafts.posapp.view.LoadingDialogNew;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Settlement extends AppCompatActivity {
    DBInstance db;
    List<byte[]> batch;
    LoadingDialogNew dialogNew;

    ActivitySettlementBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        binding = ActivitySettlementBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        batch = new ArrayList<>();
        binding.tvMid.setText("MID: " + SharedPrefHelper.getInstance(this).getMID("MID"));
        binding.tvTid.setText("TID: " + SharedPrefHelper.getInstance(this).getTID("TID"));
        dialogNew = new LoadingDialogNew(this);
        db = DBInstance.getInstance(this);
        //
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
       //
       binding.settlement.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               Intent intent = new Intent(Settlement.this, RunSettlement.class);
               startActivity(intent);
           }
       });

       binding.constraintLayout6.setVisibility(GONE);

        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Settlement.this, Home.class);
                startActivity(intent);
                finish();
            }
        });
        binding.btnSumReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Settlement.this, SummeryReport.class);
                startActivity(intent);
                finish();
            }
        });
        //fetchTransaction();
    }

}