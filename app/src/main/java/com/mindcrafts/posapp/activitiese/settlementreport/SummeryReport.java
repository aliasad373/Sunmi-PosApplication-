package com.mindcrafts.posapp.activitiese.settlementreport;

import android.content.Context;
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
import com.mindcrafts.posapp.activitiese.Home;
import com.mindcrafts.posapp.activitiese.Settlement;
import com.mindcrafts.posapp.database.DBInstance;
import com.mindcrafts.posapp.database.entity.Transaction;
import com.mindcrafts.posapp.databinding.ActivitySummeryReportBinding;
import com.mindcrafts.posapp.iso8583.isoUtils.IsoUtils;
import com.mindcrafts.posapp.storage.SharedPrefHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SummeryReport extends AppCompatActivity {
    ActivitySummeryReportBinding binding;
    DBInstance db;
    Context context;
    Map<String, String> masterCard = new HashMap<>();
    Map<String, String> visaCard = new HashMap<>();
    Map<String, String> unionCard = new HashMap<>();
    Map<String, String> payPakCard = new HashMap<>();
    Map<String, String> GrandTotal = new HashMap<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivitySummeryReportBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.tvMid.setText("MID: " + SharedPrefHelper.getInstance(this).getMID("MID"));
        binding.tvTid.setText("TID: " + SharedPrefHelper.getInstance(this).getTID("TID"));
        db = DBInstance.getInstance(this);
        context = this;
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;

        });
        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SummeryReport.this, Settlement.class);
                startActivity(intent);
                finish();
            }
        });
        loadingSummer();
    }

    private void loadingSummer(){
        //base info
        binding.textView14.setText("Shop #3 islamabad Workload");
        binding.textView16.setText(SharedPrefHelper.getInstance(this).getMID("MID"));
        binding.textView19.setText(SharedPrefHelper.getInstance(this).getTID("TID"));
        int batch = SharedPrefHelper.getInstance(this).getBatch("batch") -1;
        String batch_s = String.valueOf(batch);
        binding.textView20.setText(batch_s);
        //
        getTransactions();
        //
    }

    private void getTransactions(){
        new Thread(() -> {

            int batch = SharedPrefHelper.getInstance(context).getBatch("batch")-1;
            List<Transaction> transactions = db.userDao().getTransactionsByBatchNumber(String.valueOf(batch));
            List<Transaction> visaList = new ArrayList<>();
            List<Transaction> masterList = new ArrayList<>();
            List<Transaction> unionList = new ArrayList<>();
            List<Transaction> payPakList = new ArrayList<>();



            for (Transaction t : transactions) {
                if (t.AID == null) continue;

                String aid = t.AID.toUpperCase();

                if (aid.startsWith("A000000003")) {
                    visaList.add(t);
                } else if (aid.startsWith("A000000004")) {
                    masterList.add(t);
                } else if (aid.startsWith("A000000333")) {
                    unionList.add(t);
                } else if (aid.startsWith("A000000615")) {
                    payPakList.add(t);
                }

            }
            masterCard = getCounts(masterList);
            visaCard = getCounts(visaList);
            unionCard = getCounts(unionList);
            payPakCard = getCounts(payPakList);
            GrandTotal  = getCounts(transactions);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //master card
                    binding.masterCardCount.setText(masterCard.get("totalSettleCount"));
                    binding.textView27.setText(masterCard.get("totalAmountSettled"));
                    binding.masterCountVoid.setText(masterCard.get("totalVoidCount"));
                    binding.textView29.setText(masterCard.get("totalVoidAmount"));
                    //Visa card
                    binding.visaCount.setText(visaCard.get("totalSettleCount"));
                    binding.valuehtwo.setText(visaCard.get("totalAmountSettled"));
                    binding.visaVoidCount.setText(visaCard.get("totalVoidCount"));
                    binding.hthreevalue.setText(visaCard.get("totalVoidAmount"));
                    //Union Pay
                    binding.unionpayCount.setText(unionCard.get("totalSettleCount"));
                    binding.valuehtwoUnion.setText(unionCard.get("totalAmountSettled"));
                    binding.voidCountUnion.setText(unionCard.get("totalVoidCount"));
                    binding.hthreevalueunion.setText(unionCard.get("totalVoidAmount"));
                    //Paypak
                    binding.paypakCount.setText(payPakCard.get("totalSettleCount"));
                    binding.valuePaktwo.setText(payPakCard.get("totalAmountSettled"));
                    binding.paypakVoidCount.setText(payPakCard.get("totalVoidCount"));
                    binding.PakRowThree.setText(payPakCard.get("totalVoidAmount"));
                    //grand Total

                    binding.valuehtotalTwo.setText(GrandTotal.get("totalSettleCount"));
                    binding.totalRowThree.setText(GrandTotal.get("totalVoidCount"));
                    binding.totalAmount.setText(GrandTotal.get("totalAmountSettled"));

                }
            });

        }).start();
    }

    private Map<String, String > getCounts(List<Transaction> transactions){
        Map<String, String> map = new HashMap<>();
        int totalSettleCount = 0;
        int totalVoidCount = 0;
        double totalSettleAmount = 0.0;
        double totalVoidAmount = 0.0;
        //
        for(Transaction t: transactions){
            if(t.s_status.equals("1")){
                totalSettleCount = totalSettleCount +1;
                String amount= IsoUtils.getClearAmount(t.Amount);
                double amuntsingle = Double.parseDouble(amount);
                totalSettleAmount = totalSettleAmount + amuntsingle;
            }else{
                if(t.v_status.equals("1")){
                    totalVoidCount = totalVoidCount + 1;
                    String amount= IsoUtils.getClearAmount(t.Amount);
                    double amuntsingle = Double.parseDouble(amount);
                    totalVoidAmount = totalVoidAmount + amuntsingle;
                }
            }
        }
        map.put("totalSettleCount", String.valueOf(totalSettleCount));
        map.put("totalAmountSettled", String.valueOf(totalSettleAmount));
        map.put("totalVoidCount", String.valueOf(totalVoidCount));
        map.put("totalVoidAmount", String.valueOf(totalVoidAmount));
        return map;
    };



}