package com.mindcrafts.posapp.activitiese;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.mindcrafts.posapp.R;
import com.mindcrafts.posapp.client.ApiClient;
import com.mindcrafts.posapp.client.ApiService;
import com.mindcrafts.posapp.client.models.LoginRequest;
import com.mindcrafts.posapp.client.models.LoginResponse;
import com.mindcrafts.posapp.databinding.ActivityLoginBinding;
import com.mindcrafts.posapp.storage.SharedPrefHelper;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Login extends AppCompatActivity {

    ActivityLoginBinding binding;
    Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        context = this;
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        //
       LoginUser();
    }


    private void LoginUser(){
        binding.btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //
                String userName = binding.etUserName.getText().toString();
                String password = binding.etPass.getText().toString();
                if(userName.equals("01") && password.equals("01")){
                    String MID = SharedPrefHelper.getInstance(context).getMID("MID");
                    String TID = SharedPrefHelper.getInstance(context).getTID("TID");
                    if(MID.isEmpty() && TID.isEmpty()){
                        Toast.makeText(context, "Terminal is not configured", Toast.LENGTH_SHORT).show();
                    }else{
                        Intent intent = new Intent(Login.this, Home.class);
                        startActivity(intent);
                    }
                }else {
                    //API Call
                    if(userName.isEmpty() && password.isEmpty()){
                        Toast.makeText(context, "Please enter username and password", Toast.LENGTH_SHORT).show();
                    }else{
                        binding.progres.setVisibility(View.VISIBLE);
                        LoginRequest request = new LoginRequest(userName, password, "POS");
                        APICall(request);
                    }

                }
                //

            }
        });
    }

    public void APICall(LoginRequest request){
        ApiService api = ApiClient.getClient().create(ApiService.class);
        api.login(request).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
               if(response.code() == 200){
                Log.d("token", response.body().getToken());
                Intent intent = new Intent(Login.this, Configuration.class);
                intent.putExtra("token", response.body().getToken());
                startActivity(intent);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        binding.progres.setVisibility(View.GONE);
                    }
                });
               }else{
                   if(response.code() == 400){
                       runOnUiThread(new Runnable() {
                           @Override
                           public void run() {
                               Toast.makeText(context, "Invalid username password", Toast.LENGTH_SHORT).show();
                               binding.progres.setVisibility(View.GONE);
                           }
                       });
                   }
               }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
             runOnUiThread(new Runnable() {
                 @Override
                 public void run() {
                     binding.progres.setVisibility(View.GONE);
                     Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show();
                 }
             });
            }
        });

    }
}