package com.mindcrafts.posapp.client;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    private static Retrofit retrofit = null;
    public static Retrofit getClient() {
       // String baseUrl= "http://203.175.74.144:2000/";
        String baseUrl= "https://pos.digikhata.pk/";
        if (retrofit == null) {

            // Create logging interceptor
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY); // Logs request and response body

            // Create OkHttpClient and add interceptor
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(logging)
                    .build();

            // Build Retrofit instance
            retrofit = new Retrofit.Builder()
                    .baseUrl(baseUrl) // Replace with your API base URL
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client) // attach OkHttp client
                    .build();
        }

        return retrofit;
    }
}


