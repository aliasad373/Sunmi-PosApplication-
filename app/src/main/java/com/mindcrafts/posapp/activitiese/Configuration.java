package com.mindcrafts.posapp.activitiese;

import static com.google.android.material.internal.ViewUtils.hideKeyboard;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.mindcrafts.posapp.R;
import com.mindcrafts.posapp.client.ApiClient;
import com.mindcrafts.posapp.client.ApiService;
import com.mindcrafts.posapp.client.models.ConfigResponse;
import com.mindcrafts.posapp.client.models.Configrequest;
import com.mindcrafts.posapp.databinding.ActivityConfigurationBinding;
import com.mindcrafts.posapp.storage.SharedPrefHelper;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Configuration extends AppCompatActivity {
    ActivityConfigurationBinding binding;
    Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityConfigurationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        context = this;
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        binding.main.setOnClickListener(v -> {
            hideKeyboard();
        });
        Intent intent = getIntent();
        String token = intent.getStringExtra("token");
        String Serialnumber = "";

        binding.etUserName.setText(Serialnumber);

        binding.btnConfigure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(binding.etUserName.getText().toString().isEmpty()){
                    Toast.makeText(getBaseContext(), "Please input serial number! ", Toast.LENGTH_LONG).show();
                }else{
                    configureTerminal(token);
                }
            }
        });
        binding.btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void configureTerminal(String token){
        binding.progres.setVisibility(View.VISIBLE);
        String bearerToken = "Bearer " + token;
        Configrequest req = new Configrequest(binding.etUserName.getText().toString());
        ApiService api = ApiClient.getClient().create(ApiService.class);
        api.config(bearerToken, req).enqueue(new Callback<ConfigResponse>() {
            @Override
            public void onResponse(Call<ConfigResponse> call, Response<ConfigResponse> response) {
                if(response.code()  == 200){
                    String MID = response.body().getMerchantDetails().getMID();
                    String TID = response.body().getTID();
                    String BusinessName = response.body().getMerchantDetails().getBusinessName();
                    String MerchantName = response.body().getMerchantDetails().getMerchantName();
                    String Address = response.body().getMerchantDetails().getAddress();
                    int merchantType = response.body().getMerchantDetails().getFlag();
                    String posType = response.body().getPosType();
                    //setting up parameters
                    Log.d("POStype", posType);
                    Log.d("merchant type", String.valueOf(merchantType));
                    SharedPrefHelper.getInstance(context).saveMID("MID", MID);
                    SharedPrefHelper.getInstance(context).saveTID("TID", TID);
                    SharedPrefHelper.getInstance(context).saveBusinessName("bname", BusinessName);
                    SharedPrefHelper.getInstance(context).saveMerchantName("merchant", MerchantName);
                    SharedPrefHelper.getInstance(context).savePosType("pos", posType);
                    SharedPrefHelper.getInstance(context).saveMerchantType("mType", merchantType);
                    Log.d("Adress", Address);
                    SharedPrefHelper.getInstance(context).saveAddress("address", Address);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            binding.progres.setVisibility(View.GONE);
                            binding.loginContainer.setVisibility(View.GONE);
                            binding.btnConfigure.setVisibility(View.GONE);
                            binding.approved.setVisibility(View.VISIBLE);
                            binding.allset.setVisibility(View.VISIBLE);
                            binding.btnLogout.setVisibility(View.VISIBLE);
                        }
                    });

                    //
                }else{
                    if(response.code() == 404){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                binding.progres.setVisibility(View.GONE);
                                Toast.makeText(context,"Serial Number is invalid", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            }

            @Override
            public void onFailure(Call<ConfigResponse> call, Throwable t) {
             runOnUiThread(new Runnable() {
                 @Override
                 public void run() {
                     binding.progres.setVisibility(View.GONE);
                     Toast.makeText(context,"Serial Number is invalid", Toast.LENGTH_SHORT).show();
                 }
             });
            }
        });
    }

    private void hideKeyboard() {
        InputMethodManager imm =
                (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
        }
    }
}