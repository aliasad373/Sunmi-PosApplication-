package com.mindcrafts.posapp.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.mindcrafts.posapp.R;
import com.mindcrafts.posapp.utils.Constant;
import com.mindcrafts.posapp.utils.LogUtil;

import com.mindcrafts.posapp.view.LoadingDialogNew;
import com.mindcrafts.posapp.view.SwingCardHintDialog;
import com.sunmi.pay.hardware.aidlv2.AidlErrorCodeV2;

import java.util.LinkedHashMap;
import java.util.Map;

import sunmi.sunmiui.dialog.LoadingDialog;


public abstract class BaseAppCompatActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String TAG = Constant.TAG;

    private LoadingDialogNew loadDialog;
    private  Context context;
    private AlertDialog removeCardDialog;
    private SwingCardHintDialog swingCardHintDlg;
    private final Handler dlgHandler = new Handler();
    private final Map<String, Long> timeMap = new LinkedHashMap<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setStatusBarColor(Color.TRANSPARENT);
        loadDialog = new LoadingDialogNew(this);
        context =  this;
        MyApplication.initLocaleLanguage();
    }

    public void setStatusBarColor(int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(color);
        }
    }

    public void initToolbarBringBack() {
        //Toolbar toolbar = findViewById(R.id.toolbar);
        // Navigation Icon 要设定在 setSupportActionBar 才有作用 否则会出現 back button
        //toolbar.setNavigationIcon(R.drawable.ic_back_white);
        /*setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(
                v -> finish()
        );*/
    }

    public void initToolbarBringBack(int resId) {
        initToolbarBringBack(getString(resId));
    }

    public void initToolbarBringBack(String title) {
        /*Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(title);
        toolbar.setNavigationIcon(R.drawable.ic_back_white);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(
                v -> finish()
        );*/
    }

    public void showToast(int redId) {
        showToastOnUI(getString(redId));
    }


    public void showToast(String msg) {
        showToastOnUI(msg);
    }

    private void showToastOnUI(final String msg) {
        runOnUiThread(
                () -> Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
        );
    }

    public void toastHint(int code) {
        if (code == 0) {
            //showToast(R.string);
        } else {
            String msg = AidlErrorCodeV2.valueOf(code).getMsg();
            String error = msg + ":" + code;
            showToast(error);
        }
    }

    protected void showLoadingDialog(int resId) {
        //runOnUiThread(() -> loadDialog.show("Please tap or insert card", false));
    }

   /* protected void showLoadingDialog(final String msg) {
        runOnUiThread(() -> _showLoadingDialog(msg));
    }*/

    /** This method should be called in UI thread */
 /*   private void _showLoadingDialog(final String msg) {
        if (loadDialog == null) {
            loadDialog = new LoadingDialog(this, msg);
        } else {
            loadDialog.setMessage(msg);
        }
        if (!loadDialog.isShowing()) {
            loadDialog.show();
        }
    }*/

    protected void dismissLoadingDialog() {
        runOnUiThread(
                () -> {
                   loadDialog.dismiss();
                }
        );
    }

    /**
     * 显示检卡dialog
     *
     * @param dlgType 0-NFC卡，1-IC卡，2-NFC和IC
     */
    protected void showSwingCardHintDialog(int dlgType) {
        runOnUiThread(
                () -> {
                    if (swingCardHintDlg == null) {
                        swingCardHintDlg = new SwingCardHintDialog(this, dlgType);
                        swingCardHintDlg.setOwnerActivity(this);
                    }
                    if (swingCardHintDlg.isShowing() || isDestroyed()) {
                        return;
                    }
                    swingCardHintDlg.show();
                }
        );
    }

    protected void dismissSwingCardHintDialog() {
        runOnUiThread(
                () -> {
                    if (swingCardHintDlg != null) {
                        swingCardHintDlg.dismiss();
                    }
                }
        );
    }

    protected void addTextViewText(TextView tv, CharSequence msg) {
        runOnUiThread(() -> {
            CharSequence preMsg = tv.getText();
            tv.setText(TextUtils.concat(preMsg, "\n", msg));
        });
    }

    protected void openActivity(Class<? extends Activity> clazz) {
        Intent intent = new Intent(this, clazz);
        openActivity(intent, false);
    }

    protected void openActivity(Class<? extends Activity> clazz, boolean finishSelf) {
        Intent intent = new Intent(this, clazz);
        openActivity(intent, finishSelf);
    }

    protected void openActivity(Intent intent, boolean finishSelf) {
        startActivity(intent);
        if (finishSelf) {
            finish();
        }
    }

    protected void openActivityForResult(Class<? extends Activity> clazz, int requestCode) {
        Intent intent = new Intent(this, clazz);
        openActivityForResult(intent, requestCode);
    }

    protected void openActivityForResult(Intent intent, int requestCode) {
        startActivityForResult(intent, requestCode);
    }


    @Override
    public void onClick(View v) {

    }

    protected void addStartTime(String key) {
        timeMap.put("start_" + key, SystemClock.elapsedRealtime());
    }

    protected void addStartTimeWithClear(String key) {
        timeMap.clear();
        timeMap.put("start_" + key, SystemClock.elapsedRealtime());
    }

    protected void addEndTime(String key) {
        timeMap.put("end_" + key, SystemClock.elapsedRealtime());
    }

    protected void showSpendTime() {
       /* Map<String, Long> map = new LinkedHashMap<>(timeMap);
        Long startValue = null, endValue = null;
        for (String key : map.keySet()) {
            if (!key.startsWith("start_")) {
                continue;
            }
            key = key.substring("start_".length());
            startValue = map.get("start_" + key);
            endValue = map.get("end_" + key);
            if (startValue == null || endValue == null) {
                continue;
            }
            LogUtil.e(TAG, key + ", spend time(ms):" + (endValue - startValue));
        }*/
    }


}
