package com.mindcrafts.posapp.workers;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mindcrafts.posapp.activitiese.inventory.models.InventoryResponse;
import com.mindcrafts.posapp.activitiese.inventory.models.LiveStockResponse;
import com.mindcrafts.posapp.activitiese.inventory.models.Product;
import com.mindcrafts.posapp.activitiese.inventory.network.InventoryApi;
import com.mindcrafts.posapp.activitiese.inventory.network.InventoryClient;
import com.mindcrafts.posapp.database.DAO;
import com.mindcrafts.posapp.database.DBInstance;
import com.mindcrafts.posapp.database.entity.InventoryEntity;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class InventoryUpdateWorker extends Worker {
    DBInstance db;
    DAO transactionDao;
    int type;
    public InventoryUpdateWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        db = DBInstance.getInstance(getApplicationContext());
        transactionDao = db.userDao();
    }

    @NonNull
    @Override
    public Result doWork() {
        try {
            type = getInputData().getInt("type", 1);
            if(type == 1){
                loadKisanCard();
            }else{
               loadLiveStockInventory();
            }
            return Result.success();

        } catch (Exception e) {
            e.printStackTrace();
            return Result.retry();
        }
        }
   //kissan and live stock
   private void loadKisanCard(){
       InventoryApi apiService = InventoryClient.getClient().create(InventoryApi.class);
       String authHeader = "Basic RGlnaWtoYXRhOkRrQDc4OTIkNTIlJQ==";
       Call<InventoryResponse> call = apiService.getKisanCardProducts(authHeader);
       call.enqueue(new Callback<InventoryResponse>() {
           @Override
           public void onResponse(Call<InventoryResponse> call, Response<InventoryResponse> response) {
               if(response.code() == 200){
                   List<InventoryResponse.Product> fertilizer = response.body().getData().getProducts().getFertilizer();
                   List<InventoryResponse.Product> pesticide = response.body().getData().getProducts().getPesticides();
                   List<InventoryResponse.Product> seed = response.body().getData().getProducts().getSeed();
                   List<InventoryResponse.Product> list = new ArrayList<>();
                   list.addAll(fertilizer);
                   list.addAll(pesticide);
                   list.addAll(seed);
                   Gson gson = new Gson();
                   String updatRecord = gson.toJson(list);
                   new Thread(()->{
                       if (transactionDao.countByType(type) > 0) {
                           // Record exists → update
                           transactionDao.updateByType(type, updatRecord);
                       } else {
                           // Record does not exist → insert
                           InventoryEntity entity = new InventoryEntity(type, updatRecord);
                           transactionDao.insertInventory(entity);
                       }
                   }).start();

                   //loadInventory();
               }

           }

           @Override
           public void onFailure(Call<InventoryResponse> call, Throwable t) {
               Log.d("Http", "onFailure: " + t);
           }
       });

   };
   //
   private void loadLiveStockInventory(){
       InventoryApi apiService = InventoryClient.getClient().create(InventoryApi.class);
       String authHeader = "Basic RGlnaWtoYXRhOkRrQDc4OTIkNTIlJQ==";
       Call<LiveStockResponse> call = apiService.getLiveStockProducts(authHeader);
       call.enqueue(new Callback<LiveStockResponse>() {
           @Override
           public void onResponse(Call<LiveStockResponse> call, Response<LiveStockResponse> response) {
               if(response.code() == 200){
                   List<LiveStockResponse.LiveStockItem> liveStock = response.body().getData().getProducts().getLiveStock();
                   Gson gson = new Gson();
                   String data = gson.toJson(liveStock);
                   Type productListType = new TypeToken<List<InventoryResponse.Product>>() {}.getType();
                   List<Product>list = gson.fromJson(data, productListType);
                   String updatRecord = gson.toJson(list);
                   new Thread(()->{
                       if (transactionDao.countByType(type) > 0) {
                           // Record exists → update
                           transactionDao.updateByType(type, updatRecord);
                       } else {
                           // Record does not exist → insert
                           InventoryEntity entity = new InventoryEntity(type, updatRecord);
                           transactionDao.insertInventory(entity);
                       }
                   }).start();

                   //loadInventory();
               }
           }

           @Override
           public void onFailure(Call<LiveStockResponse> call, Throwable t) {
             Log.d("http", t.toString());
           }
       });
   }
}





