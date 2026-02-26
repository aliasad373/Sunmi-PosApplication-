package com.mindcrafts.posapp.workers;


import android.content.Context;
import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import android.util.Log;
import android.widget.Toast;

import com.mindcrafts.posapp.client.ApiClient;
import com.mindcrafts.posapp.client.ApiService;
import com.mindcrafts.posapp.client.models.ServerTransaction;
import com.mindcrafts.posapp.database.DAO;
import com.mindcrafts.posapp.database.DBInstance;
import com.mindcrafts.posapp.database.entity.Transaction;
import com.mindcrafts.posapp.iso8583.isoUtils.IsoUtils;
import com.mindcrafts.posapp.storage.SharedPrefHelper;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DataSyncWorker extends Worker {

    public DataSyncWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        // Your task goes here

        SyncTransaction();
        // Return success
        return Result.success();
    }

    private void SyncTransaction(){
        new Thread(()->{
            int bacth = SharedPrefHelper.getInstance(getApplicationContext()).getBatch("batch")-1;
            DBInstance db = DBInstance.getInstance(getApplicationContext());
            DAO transactionDao = db.userDao();
            List<Transaction> localDBTransactions = transactionDao.getUnsyncedTransactions();
            if(!localDBTransactions.isEmpty()){
                for (int i = 0; i<localDBTransactions.size(); i++){
                    Transaction trans = localDBTransactions.get(i);
                    Boolean SettlementStatus = false;
                    if(trans.v_status.equals("1")){
                        SettlementStatus = false;
                    }
                    if(trans.s_status.equals("1")){
                        SettlementStatus = true;
                    }
                    ServerTransaction transaction = new ServerTransaction(
                            trans.MID,
                            trans.TID ,
                            trans.batchNumber,
                            IsoUtils.getClearAmount( trans.Amount),
                            IsoUtils.maskedPAN(trans.PAN),
                            trans.TransactionTime,
                            trans.TransactionTime,
                            trans.expDate,
                            trans.RRN,
                            trans.responseCode,
                            trans.TC,
                            trans.authCode,
                            trans.CardScheme,
                            "Sale",
                            trans.AID,
                            SettlementStatus
                    );
                    //http call
                    String bearerToken = "Bearer " + SharedPrefHelper.getInstance(getApplicationContext()).getToken("token");
                    ApiService api = ApiClient.getClient().create(ApiService.class);
                    api.SaveTransactionToServer(bearerToken, transaction).enqueue(new Callback<Object>() {
                        @Override
                        public void onResponse(Call<Object> call, Response<Object> response) {
                            if(response.code()  == 201){
                                int id  = trans.id;
                                new Thread(()->{
                                    transactionDao.updateIsSyncedById(id);
                                }).start();
                                Log.d("Sync","Sync Done");
                                //
                            }else{
                                if(response.code() == 404){
                                    Log.d("Sync","SyncCallFailed");
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<Object> call, Throwable t) {
                          Log.d("Worker Sync failed", "Worker failed");
                        }
                    });

                }
            }else{
                Log.d("sync", "No Record to sync");
            }
        }).start();
    }
}

