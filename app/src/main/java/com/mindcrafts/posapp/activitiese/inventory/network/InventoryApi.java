package com.mindcrafts.posapp.activitiese.inventory.network;

import com.mindcrafts.posapp.activitiese.inventory.models.InventoryPurchaseReq;
import com.mindcrafts.posapp.activitiese.inventory.models.InventoryResponse;
import com.mindcrafts.posapp.activitiese.inventory.models.LiveStockResponse;
import com.mindcrafts.posapp.activitiese.inventory.models.PurchaseReq;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface InventoryApi {
    @GET("api/pos/getLiveStockProducts")
    Call<LiveStockResponse> getLiveStockProducts(@Header("Authorization") String authHeader);

    @GET("api/pos/getkessanCardProducts")
    Call<InventoryResponse> getKisanCardProducts(@Header("Authorization") String authHeader);

    @POST("api/pos/requestPurchasesV3")
    Call<Object> requestPurchases(@Header("Authorization") String authHeader, @Body List<InventoryPurchaseReq> reqs);
}

