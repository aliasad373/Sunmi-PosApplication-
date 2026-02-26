package com.mindcrafts.posapp.activitiese;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.mindcrafts.posapp.R;
import com.mindcrafts.posapp.app.MyApplication;
import com.mindcrafts.posapp.databinding.ActivitySettingsBinding;
import com.sunmi.pay.hardware.aidlv2.AidlConstantsV2;
import com.sunmi.pay.hardware.aidlv2.system.BasicOptV2;

public class Settings extends AppCompatActivity {
    ActivitySettingsBinding binding;
    private final BasicOptV2 basicOptV2 = MyApplication.app.basicOptV2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        binding.btnEnbaleKiosk.setVisibility(View.GONE);

        binding.btnEnbaleKiosk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setStatusBarDropDownMode(AidlConstantsV2.SystemUI.DISABLE_STATUS_BAR_DROP_DOWN);
                setHideNavigationBarItems(AidlConstantsV2.SystemUI.HIDE_NAV_ITEM_BACK_KEY
                        | AidlConstantsV2.SystemUI.HIDE_NAV_ITEM_HOME_KEY
                        | AidlConstantsV2.SystemUI.HIDE_NAV_ITEM_RECENT_KEY);
            }
        });

        binding.btnDisableKiosk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(binding.editText2.getText().toString().equals("3549")){
                    setStatusBarDropDownMode(AidlConstantsV2.SystemUI.ENABLE_STATUS_BAR_DROP_DOWN);
                    setHideNavigationBarItems(0);
                }
            }
        });

        binding.btnNavBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Settings.this, Home.class);
                startActivity(intent);
                finish();
            }
        });
    }


    private void setStatusBarDropDownMode(int key) {
        try {
            basicOptV2.setStatusBarDropDownMode(key);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setHideNavigationBarItems(int key) {
        try {
            basicOptV2.setHideNavigationBarItems(key);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}