package com.mindcrafts.posapp.activitiese.inventory;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mindcrafts.posapp.R;
import com.mindcrafts.posapp.activitiese.EnterAmountV2;
import com.mindcrafts.posapp.activitiese.Home;
import com.mindcrafts.posapp.activitiese.adapters.InventoryAdapter;
import com.mindcrafts.posapp.activitiese.adapters.callbacks.InventoryCallbacks;
import com.mindcrafts.posapp.activitiese.inventory.models.CartProduct;
import com.mindcrafts.posapp.activitiese.inventory.models.InventoryResponse;
import com.mindcrafts.posapp.activitiese.inventory.models.LiveStockResponse;
import com.mindcrafts.posapp.activitiese.inventory.models.Product;
import com.mindcrafts.posapp.activitiese.inventory.network.InventoryApi;
import com.mindcrafts.posapp.activitiese.inventory.network.InventoryClient;
import com.mindcrafts.posapp.database.DAO;
import com.mindcrafts.posapp.database.DBInstance;
import com.mindcrafts.posapp.database.entity.InventoryEntity;
import com.mindcrafts.posapp.databinding.ActivityInventoryHomeBinding;
import com.mindcrafts.posapp.emv.ICProcessActivity;
import com.mindcrafts.posapp.storage.SharedPrefHelper;
import com.mindcrafts.posapp.utils.Utility;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InventoryHome extends AppCompatActivity implements InventoryCallbacks {
   ActivityInventoryHomeBinding binding;

    InventoryAdapter adapter;

    ArrayList<InventoryResponse.Product> list;
    ArrayList<LiveStockResponse.LiveStockItem> liveStockList;

    ArrayList<CartProduct> cartProducts;

    DBInstance db;
    DAO transactionDao;

    int inventoryType;
    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityInventoryHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.tvMid.setText("MID: " + SharedPrefHelper.getInstance(this).getMID("MID"));
        binding.tvTid.setText("TID: " + SharedPrefHelper.getInstance(this).getTID("TID"));
        binding.timeStamp.setText(Utility.getCurrentTimestamp());

        Intent intent = getIntent();
        inventoryType = intent.getIntExtra("type", 1);

        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1  = new Intent(InventoryHome.this, Home.class);
                startActivity(intent1);
                finish();
            }
        });
        //db
         db = DBInstance.getInstance(getApplicationContext());
         transactionDao = db.userDao();
        //
        list = new ArrayList<>();
        cartProducts = new ArrayList<>();
        liveStockList = new ArrayList<>();
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        //
           //loadKisanCard();
           loadOfflineInventory();
           //loadLiveStockInventory();

         //
        binding.main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard();
            }
        });
        binding.btnProceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(cartProducts.isEmpty()){
                    Toast.makeText(InventoryHome.this, "Please first add to cart for purchase", Toast.LENGTH_SHORT).show();
                }else{
                    Gson gson = new Gson();
                    String cartData = gson.toJson(cartProducts);
                    double total = 0.0;
                    for (CartProduct p : cartProducts) {
                        total += p.getCount() * p.getPrice();
                    }
                    String totalAmnt = String.valueOf(total);
                    Intent intent = new Intent(InventoryHome.this, ICProcessActivity.class);
                    intent.putExtra("cart", cartData);
                    intent.putExtra("amount", totalAmnt);
                    startActivity(intent);
                    finish();
                }
            }
        });

        binding.itemRecycler .setOnTouchListener((v, event) -> {
            hideKeyboard();
            return false; // allow RecyclerView to handle touch
        });
    }

    private void loadOfflineInventory(){
        if(inventoryType == 1){
            new Thread(()->{
                InventoryEntity inventory  = transactionDao.getByType(1);
                if(inventory == null){
                    loadKisanCard();
                }else{
                    String data = inventory.getJsonString();
                    Gson gson = new Gson();
                    Type productListType = new TypeToken<List<InventoryResponse.Product>>() {}.getType();
                    list = gson.fromJson(data, productListType);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            loadInventory();
                        }
                    });
                }
            }).start();
        }else{
            new Thread(()->{
                InventoryEntity inventory  = transactionDao.getByType(2);
                if(inventory == null){
                   loadLiveStockInventory();
                }else{
                    String data = inventory.getJsonString();
                    Gson gson = new Gson();
                    Type productListType = new TypeToken<List<InventoryResponse.Product>>() {}.getType();
                    list = gson.fromJson(data, productListType);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            loadInventory();
                        }
                    });
                }
            }).start();
        }
    }
    private void loadInventory(){
        binding.itemRecycler.setLayoutManager(new GridLayoutManager(this, 2));
        adapter = new InventoryAdapter(this, list, this);
        binding.itemRecycler.setAdapter(adapter);
    }

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
                 list.addAll(fertilizer);
                 list.addAll(pesticide);
                 list.addAll(seed);
                 offlineInventory(list, 1);
                 //loadInventory();
                }

            }

            @Override
            public void onFailure(Call<InventoryResponse> call, Throwable t) {

            }
        });

    };

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
                    list = gson.fromJson(data, productListType);
                    offlineInventory(list, 2);
                    //loadInventory();
                }
            }

            @Override
            public void onFailure(Call<LiveStockResponse> call, Throwable t) {

            }
        });
    }
    @Override
    public void getSingleProduct(CartProduct product) {
        if(product.getCount() == 0){
            for (int i = 0; i < cartProducts.size(); i++) {
                if (cartProducts.get(i).getId() == product.getId()) {
                    cartProducts.remove(i);
                    break; // Stop after removing the first match
                }
            }
        }else {
            boolean found = false;
            for (int i = 0; i < cartProducts.size(); i++) {
                CartProduct existing = cartProducts.get(i);
                if (existing.getId() == product.getId()) {
                    // Update existing product
                    cartProducts.set(i, product);
                    found = true;
                    break;
                }
            }
            if (!found) {
                // Add new product if not found
                cartProducts.add(product);
            }

        }
        //
        Gson gson = new Gson();
        String data = gson.toJson(cartProducts);
        Log.d("AddedCart", data);
        double total = 0.0;
        for (CartProduct p : cartProducts) {
            total += p.getCount() * p.getPrice();
        }
        binding.count.setText(String.valueOf(cartProducts.size()));
        binding.totalAmount.setText("Rs. " + String.valueOf(total));

    }

    private void offlineInventory(List<InventoryResponse.Product> list, int type){
        Gson gson = new Gson();
        String dataToStore =  gson.toJson(list);
        InventoryEntity inventory = new InventoryEntity(type, dataToStore);

        new Thread(() -> {
            transactionDao.insertInventory(inventory);
        }).start();
        loadInventory();
    }

    private void hideKeyboard() {
        InputMethodManager imm =
                (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
        }
    }
}