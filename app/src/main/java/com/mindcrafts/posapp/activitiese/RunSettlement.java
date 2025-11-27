package com.mindcrafts.posapp.activitiese;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.mindcrafts.posapp.R;
import com.mindcrafts.posapp.activitiese.adapters.AllTransactionAdapter;
import com.mindcrafts.posapp.database.DBInstance;
import com.mindcrafts.posapp.database.entity.Transaction;
import com.mindcrafts.posapp.databinding.ActivityRunSettlementBinding;
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
import com.mindcrafts.posapp.utils.LoadingDialog;
import com.mindcrafts.posapp.view.LoadingDialogNew;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class RunSettlement extends AppCompatActivity implements AllTransactionAdapter.ClickListenerAllTransactionScreen, NetworkCallback, BatchUploadCallback {
     ActivityRunSettlementBinding binding;
     DBInstance instance;
     Context context;
    AllTransactionAdapter adapter;
    List<Transaction> transactions;
     String total_count_s;
    String total_amount_s;
    String Stan;
    String BatchNo;
    String MID;
    String TID;
    List<byte[]> batch;
    LoadingDialogNew dialogNew;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityRunSettlementBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        instance = DBInstance.getInstance(this);
        context = this;
        adapter  = new AllTransactionAdapter();
        adapter.setListener(this);
        dialogNew = new LoadingDialogNew(this);
        binding.tvMid.setText("MID: " + SharedPrefHelper.getInstance(this).getMID("MID"));
        binding.tvTid.setText("TID: " + SharedPrefHelper.getInstance(this).getTID("TID"));
        batch = new ArrayList<>();
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        LoadTransactions();
        binding.btnRunSettlement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.progressSett.setVisibility(View.VISIBLE);
                fetchTransaction();
            }
        });
        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RunSettlement.this, Settlement.class);
                startActivity(intent);
                finish();
            }
        });
    }

    public void LoadTransactions(){
      new Thread(new Runnable() {
          @Override
          public void run() {
              transactions = instance.userDao().getUnsettledTransactions();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(transactions.isEmpty()){
                                int batch = SharedPrefHelper.getInstance(context).getBatch("batch");
                                binding.textView11.setText( "Batch No: "+String.valueOf(batch));
                                binding.settleRecycler.setVisibility(View.GONE);
                                binding.noRecord.setVisibility(View.VISIBLE);
                                binding.amount.setText("0.0");
                            }else{
                                double sum = 0;
                                for (Transaction t : transactions) {
                                    String clearAmount = IsoUtils.getClearAmount(t.Amount);
                                    Double amount_s = Double.parseDouble(clearAmount);
                                    sum = sum + amount_s;
                                }
                                int batch = SharedPrefHelper.getInstance(context).getBatch("batch");
                                binding.textView11.setText("Batch No: " + String.valueOf(batch));
                                binding.amount.setText(String.valueOf(sum) + " PKR");
                                adapter.setData(transactions);
                                binding.settleRecycler.setLayoutManager(new LinearLayoutManager(context));
                                binding.settleRecycler.setAdapter(adapter);
                            }
                        }
                    });
          }
      }).start();
    }

    @Override
    public void itemClicked(@NonNull Transaction item) {

    }


    private void fetchTransaction() {
        new Thread(() -> {
            runOnUiThread(() -> {
                if (!isFinishing()) {
                   /// dialogNew.show("Running Settlement", false);
                }
            });

            List<Transaction> transactions = new ArrayList<>();
            List<String> amount = new ArrayList<>();
            double sum = 0;
            transactions = instance.userDao().getUnsettledTransactions();
            // Now you can update UI on main thread
            if (transactions.isEmpty()) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //dialogNew.dismiss();
                        binding.progressSett.setVisibility(View.GONE);
                    }
                });
                Log.d("Settlement", "No Record Found");
            } else {
                for (Transaction t : transactions) {
                    batch.add(ByteUtil.hexStr2Bytes(t.message320));
                    String clearAmount = IsoUtils.getClearAmount(t.Amount);
                    amount.add(clearAmount);
                    Double amount_s = Double.parseDouble(clearAmount);
                    sum = sum + amount_s;
                }
                Log.d("sum of amount", String.valueOf(sum));
                Log.d("total-count", String.valueOf(amount.size()));
                String Total_count = String.valueOf(amount.size());
                String sum_amount = IsoUtils.formatAmountforsettle(String.valueOf(sum));
                Log.d("sum of amount", sum_amount);
                Log.d("total-count", Total_count);
                Log.d("Batch", transactions.get(0).batchNumber);
                total_count_s = Total_count;
                total_amount_s = sum_amount;
                Stan = transactions.get(0).TC;
                MID = transactions.get(0).MID;
                TID = transactions.get(0).TID;
                BatchNo = transactions.get(0).batchNumber;

                SettlementBuilder builder = new SettlementBuilder();
                byte[] Data = builder.create500(IsoConfig.p_code_settlement, sum_amount, Total_count, transactions.get(0).TC, transactions.get(0).MID, transactions.get(0).TID, transactions.get(0).batchNumber);
                Log.d("500-message", ByteUtil.bytes2HexStr(Data));
                NetworkUtil util = new NetworkUtil();
                util.HostCall(Data, this);
            }

        }).start();
    }

    @Override
    public void onCallSuccess(byte[] response) {
        //String resp = ByteUtil.bytes2HexStr(response);
        Map<Integer, Integer> fields = new HashMap<>();
        fields.put(3, 2);
        fields.put(11, 4);
        fields.put(12, 3);
        fields.put(13, 2);
        fields.put(24, 2);
        fields.put(37, 12);
        fields.put(38, 6);
        fields.put(39, 2);
        fields.put(41, 8);
        Map<String, String> map = ISOParser.parseMessage(response, fields);
        Log.d("map-510", map.toString());
        if (Objects.equals(map.get("Field_39"), "3935")) {
            Log.d("Settlement", "Go for batch upload");
            BatchUpload upload = new BatchUpload(batch, IsoConfig.ip, IsoConfig.Port, this);
            upload.start();
        } else {
            if (Objects.equals(map.get("Field_39"), "3030")) {
                Log.d("Settlement", "Settlement Successful!");
                instance.userDao().updateSStatusWhere();
                int batch = SharedPrefHelper.getInstance(this).getBatch("batch");
                batch = batch + 1;
                SharedPrefHelper.getInstance(this).saveBatchNo("batch", batch);
                runOnUiThread(() -> {
                    binding.progressSett.setVisibility(View.GONE);
                    LoadTransactions();
                  /*  dialogNew.dismiss();
                    binding.clReceipt.setVisibility(VISIBLE);
                    binding.txtTotalAmount.setText(total_amount_s);
                    binding.txtTotalCount.setText(total_count_s);
                    binding.txtMID.setText(MID);
                    binding.txtTID.setText(TID);*/
                });

            } else {
                Log.d("Settlement", "Go for force settle");
//

                instance.userDao().updateSStatusWhere();
                int batch = SharedPrefHelper.getInstance(this).getBatch("batch");
                batch = batch + 1;
                SharedPrefHelper.getInstance(this).saveBatchNo("batch", batch);
                runOnUiThread(() -> {
                  LoadTransactions();
                  binding.progressSett.setVisibility(View.GONE);
                });

            }
        }
    }

    @Override
    public void onError(String msg) {
        Log.e("Failed", "please try after sometime" + msg);
    }


    @Override
    public void onBatchUploadSuccess(String response) {
        if (Objects.equals(response, "200")) {
            SettlementBuilder builder = new SettlementBuilder();
            byte[] Data = builder.create500(IsoConfig.p_code_settlement_second, total_count_s, total_amount_s, Stan, MID, TID, BatchNo);
            Log.d("500-message", ByteUtil.bytes2HexStr(Data));
            NetworkUtil util = new NetworkUtil();
            util.HostCall(Data, this);
        }
    }

    @Override
    public void onBatchUploadError(String msg) {
        Log.e("Failed", "please try after sometime");
    }
}