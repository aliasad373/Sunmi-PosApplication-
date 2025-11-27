package com.mindcrafts.posapp.client;

import com.mindcrafts.posapp.client.models.ConfigResponse;
import com.mindcrafts.posapp.client.models.Configrequest;
import com.mindcrafts.posapp.client.models.LoginRequest;
import com.mindcrafts.posapp.client.models.LoginResponse;
import com.mindcrafts.posapp.client.models.ServerTransaction;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface ApiService {
    @POST("api/login")
    Call<LoginResponse> login(@Body LoginRequest request);

    @POST("api/terminalConfiguration")
    Call<ConfigResponse> config(@Header("Authorization") String token,  @Body Configrequest req);

    @POST("api/createTransaction")
    Call<Object> SaveTransactionToServer(@Header("Authorization") String token,  @Body ServerTransaction req);


}
