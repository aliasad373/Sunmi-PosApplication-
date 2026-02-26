package com.mindcrafts.posapp.activitiese;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.work.Data;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.bumptech.glide.util.Util;
import com.mindcrafts.posapp.R;
import com.mindcrafts.posapp.activitiese.inventory.InventoryHome;
import com.mindcrafts.posapp.activitiese.reprint.Reprint;
import com.mindcrafts.posapp.app.MyApplication;
import com.mindcrafts.posapp.client.ApiClient;
import com.mindcrafts.posapp.client.ApiService;
import com.mindcrafts.posapp.client.models.ConfigResponse;
import com.mindcrafts.posapp.client.models.Configrequest;
import com.mindcrafts.posapp.client.models.LoginRequest;
import com.mindcrafts.posapp.client.models.LoginResponse;
import com.mindcrafts.posapp.client.models.ServerTransaction;
import com.mindcrafts.posapp.client.models.getTokenReq;
import com.mindcrafts.posapp.client.models.getTokenResponse;
import com.mindcrafts.posapp.database.DAO;
import com.mindcrafts.posapp.database.DBInstance;
import com.mindcrafts.posapp.database.entity.Transaction;
import com.mindcrafts.posapp.databinding.ActivityHomeBinding;
import com.mindcrafts.posapp.iso8583.isoUtils.IsoUtils;
import com.mindcrafts.posapp.storage.SharedPrefHelper;
import com.mindcrafts.posapp.utils.Utility;
import com.mindcrafts.posapp.workers.DataSyncWorker;
import com.mindcrafts.posapp.workers.InventoryUpdateWorker;

import java.util.List;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Home extends AppCompatActivity {
   ActivityHomeBinding binding;
  /*  String MID = "267142799700121";
    String TID = "26420121";*/
    int type = 0;
    Context context;
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        context = this;
      /*  SharedPrefHelper.getInstance(this).saveMID("MID", MID);
        SharedPrefHelper.getInstance(this).saveTID("TID", TID);*/
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        //
        scheduleWorkManager();
        //
        binding.tvMid.setText("MID: " + SharedPrefHelper.getInstance(this).getMID("MID"));
        binding.tvTid.setText("TID: " + SharedPrefHelper.getInstance(this).getTID("TID"));
        binding.mName.setText("Hi " + SharedPrefHelper.getInstance(this).getMerchant("merchant"));
        binding.timeStamp.setText(Utility.getCurrentTimestamp());
        //
        initMerchantType();
        //
        binding.sales.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Home.this, EnterAmountV2.class);
                startActivity(intent);
                finish();
            }
        });

        binding.amountReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Home.this, AllTransactionActivity.class);
                startActivity(intent);
                finish();
            }
        });
        binding.settlement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Home.this, Settlement.class);
                startActivity(intent);
                finish();
            }
        });
        binding.reprint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Home.this, Reprint.class);
                startActivity(intent);
                finish();
            }
        });
        binding.helpLine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showHelplineDialog();
            }
        });

        binding.BOPCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(type == 3){
                    Intent intent = new Intent(Home.this, EnterAmountV2.class);
                    startActivity(intent);
                    finish();
                }else{
                    Intent intent = new Intent(Home.this, InventoryHome.class);
                    intent.putExtra("type", type);
                    startActivity(intent);
                    finish();
                }

            }
        });
     binding.settings.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             Intent intent = new Intent(Home.this, Settings.class);
             startActivity(intent);
         }
     });
        //
        getToken();
        ScheduleDataSync();
        //SyncTransaction();
    }

    private void initMerchantType(){
        type = SharedPrefHelper.getInstance(context).getMerchantType("mType");
        if(type == 1){
            binding.constraintLayout20.setVisibility(View.GONE);
        }else{
            if(type == 2){
                binding.constraintLayout20.setVisibility(View.GONE);
                binding.teBOPCard.setText("Livestock card");
            }else{
                if(type == 3){
                    binding.constraintLayout20.setVisibility(View.GONE);
                    binding.teBOPCard.setText("Ration Card");
                }else{
                    if(type == 0){
                        binding.BOPCard.setVisibility(View.GONE);
                        binding.constraintLayout20.setVisibility(View.VISIBLE);
                    }
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!MyApplication.app.isConnectPaySDK()) {
            MyApplication.app.bindPaySDKService();
        }
        getToken();
    }

    private void scheduleWorkManager(){
        // Pass type as input data
        Data inputData = new Data.Builder()
                .putInt("type", type)
                .build();

        // Create a periodic work request to run every 15 minutes
        PeriodicWorkRequest inventoryWorker =
                new PeriodicWorkRequest.Builder(InventoryUpdateWorker.class, 15, TimeUnit.MINUTES)
                        .setInputData(inputData)
                        .addTag("inventory_worker") // optional tag
                        .build();

        // Enqueue as a unique periodic work to avoid duplicates
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
                "inventory_worker_unique",             // unique name
                ExistingPeriodicWorkPolicy.KEEP,       // keep existing worker if already scheduled
                inventoryWorker
        );
        }


    private void showHelplineDialog() {
        // Create dialog
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.helpline);

        // Make background transparent if desired
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        Window window = dialog.getWindow();
        if (window != null) {
            window.setLayout(
                    (int) (getResources().getDisplayMetrics().widthPixels * 0.9), // 90% of screen width
                    WindowManager.LayoutParams.WRAP_CONTENT
            );
        }
        // Find views

        TextView btnOk = dialog.findViewById(R.id.btnOk);


        // Handle button click
        btnOk.setOnClickListener(v -> dialog.dismiss());

        // Show dialog
        dialog.show();
    }

    private void getToken(){
     getTokenReq request = new getTokenReq(SharedPrefHelper.getInstance(context).getTID("TID"),
             SharedPrefHelper.getInstance(context).getMID("MID"));
     APICall(request);
    }

    public void APICall(getTokenReq request){
        ApiService api = ApiClient.getClient().create(ApiService.class);
        api.getToken(request).enqueue(new Callback<getTokenResponse>() {
            @Override
            public void onResponse(Call<getTokenResponse> call, Response<getTokenResponse> response) {
                if(response.code() == 200){
                   String token =  response.body().getToken();
                   SharedPrefHelper.getInstance(context).saveToken("token",token);
                }else{
                    if(response.code() == 400){
                     Log.d("Token", "Token Fetch Failed");
                    }
                }
            }

            @Override
            public void onFailure(Call<getTokenResponse> call, Throwable t) {
              Log.d("Token", "Token Fetch failed");
            }
        });

    }

   private void ScheduleDataSync(){


       // Create a periodic work request to run every 15 minutes
       PeriodicWorkRequest inventoryWorker =
               new PeriodicWorkRequest.Builder(DataSyncWorker.class, 15, TimeUnit.MINUTES)
                       .addTag("Data_Sync_Worker") // optional tag
                       .build();

       // Enqueue as a unique periodic work to avoid duplicates
       WorkManager.getInstance(this).enqueueUniquePeriodicWork(
               "Data_Sync_Worker_unique",             // unique name
               ExistingPeriodicWorkPolicy.KEEP,       // keep existing worker if already scheduled
               inventoryWorker
       );
   }

}