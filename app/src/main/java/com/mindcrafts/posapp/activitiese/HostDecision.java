package com.mindcrafts.posapp.activitiese;

import static com.mindcrafts.posapp.app.BaseAppCompatActivity.TAG;
import static com.mindcrafts.posapp.utils.Utility.generateOfflineId;
import static com.mindcrafts.posapp.utils.Utility.showToast;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Build;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mindcrafts.posapp.R;
import com.mindcrafts.posapp.activitiese.inventory.models.CartProduct;
import com.mindcrafts.posapp.activitiese.inventory.models.InventoryPurchaseReq;
import com.mindcrafts.posapp.activitiese.inventory.models.InventoryResponse;
import com.mindcrafts.posapp.activitiese.inventory.models.LiveStockResponse;
import com.mindcrafts.posapp.activitiese.inventory.models.PurchaseReq;
import com.mindcrafts.posapp.activitiese.inventory.network.InventoryApi;
import com.mindcrafts.posapp.activitiese.inventory.network.InventoryClient;
import com.mindcrafts.posapp.app.MyApplication;
import com.mindcrafts.posapp.database.DAO;
import com.mindcrafts.posapp.database.DBInstance;
import com.mindcrafts.posapp.database.entity.Transaction;
import com.mindcrafts.posapp.databinding.ActivityHostDecisionBinding;
import com.mindcrafts.posapp.iso8583.builder.Batch;
import com.mindcrafts.posapp.iso8583.builder.Sales;
import com.mindcrafts.posapp.iso8583.isoUtils.IsoConfig;
import com.mindcrafts.posapp.iso8583.isoUtils.IsoUtils;
import com.mindcrafts.posapp.iso8583.parser.ISOParser;
import com.mindcrafts.posapp.network.NetworkUtil;
import com.mindcrafts.posapp.network.callback.NetworkCallback;
import com.mindcrafts.posapp.storage.SharedPrefHelper;
import com.mindcrafts.posapp.utils.ByteUtil;
import com.mindcrafts.posapp.utils.LoadingDialog;
import com.mindcrafts.posapp.utils.LogUtil;
import com.mindcrafts.posapp.utils.TLV;
import com.mindcrafts.posapp.utils.TLVUtil;
import com.mindcrafts.posapp.utils.Utility;

import com.mindcrafts.posapp.view.LoadingDialogNew;
import com.sunmi.peripheral.printer.InnerResultCallback;

import org.w3c.dom.Text;

import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HostDecision extends AppCompatActivity  {
    ActivityHostDecisionBinding binding;
    NetworkCallback callback;
    Map<String, TLV> TLVMap;
    Map<String, String> resp210;
    private String pinBlock;
    private String MID;
    private String TID;
    LoadingDialogNew dialog;
    private String Amount;
    private String RRN;
    private String STAN;
    private Context context;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityHostDecisionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        //
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        //
        binding.tvMid.setText("MID: " + SharedPrefHelper.getInstance(this).getMID("MID"));
        binding.tvTid.setText("TID: " + SharedPrefHelper.getInstance(this).getTID("TID"));
        binding.txtMerchantName.setText(SharedPrefHelper.getInstance(this).getBusiness("bname"));
        binding.main.setVisibility(View.VISIBLE);
        context =this;
        Intent intent = getIntent();
        String data = intent.getStringExtra("Tran");
        String cartData = intent.getStringExtra("cart");
        Amount = intent.getStringExtra("amount");

        Gson gson  = new Gson();
        Transaction Tran = gson.fromJson(data, Transaction.class);
        RRN = IsoUtils.hexToAscii(Tran.RRN);
        STAN = Tran.TC;
        save_data(Tran);
        MID = SharedPrefHelper.getInstance(this).getMID("MID");
        TID = SharedPrefHelper.getInstance(this).getTID("TID");

        assert cartData != null;
        if(!cartData.isEmpty()){
            callForInventoryPurchase(cartData);
        }
        //readyTheSlip(Tran);
    }
    public void save_data(Transaction Tran) {
        new Thread(() -> {

                runOnUiThread(() -> {
                    // This runs on the main thread
                   Log.d("businessName", SharedPrefHelper.getInstance(this).getBusiness("bname"));
                    binding.main.setVisibility(View.VISIBLE);
                    binding.txtMerchantName.setText(SharedPrefHelper.getInstance(this).getBusiness("bname"));
                    binding.txtLocation.setText(SharedPrefHelper.getInstance(this).getAddress("address"));
                    binding.txtTotalAmount.setText(IsoUtils.getClearAmount(Tran.Amount));
                    binding.txtTime.setText(Tran.time);
                    binding.txtDate.setText(Tran.date);
                    binding.txtCardNumber.setText(IsoUtils.maskedPAN(Tran.PAN));
                    binding.txtCardType.setText(Tran.CardScheme);
                    binding.txtMerchantId.setText(Tran.MID);
                    binding.txtTransactionId.setText(Tran.TID);
                    binding.txtInvoice.setText(Tran.TC);
                    binding.txtRRN.setText(IsoUtils.hexToAscii(Tran.RRN));
                    binding.txtBatch.setText(IsoUtils.padToSix(Tran.batchNumber));
                    binding.txtAuthNo.setText(IsoUtils.hexToAscii(Tran.authCode));
//                    dialog.dismiss();
                    //Toast.makeText(HostDecision.this, "Inserted!", Toast.LENGTH_SHORT).show();
                    // update RecyclerView or TextView here

                    binding.btnClose.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(HostDecision.this, Home.class);
                            startActivity(intent);
                            finish();  // closes HostDecision activity
                        }
                    });

                    binding.btnPrint.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            readyTheSlip(Tran);
                        }
                    });
                });

        }).start();

    }

    private void readyTheSlip(Transaction Tran) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.slip_print, null);

        TextView time = view.findViewById(R.id.txtTime);
        time.setText(Tran.time);
        TextView date = view.findViewById(R.id.txtDate);
        date.setText(Tran.date);
        TextView location = view.findViewById(R.id.txtLocation);
        location.setText(SharedPrefHelper.getInstance(this).getAddress("address"));
        TextView merchantBusiness = view.findViewById(R.id.txtMerchantName);
        merchantBusiness.setText(SharedPrefHelper.getInstance(context).getBusiness("bname"));
        TextView amount = view.findViewById(R.id.txtTotalAmount);
        amount.setText(IsoUtils.getClearAmount(Tran.Amount) + " Pkr");
        TextView cardNumber = view.findViewById(R.id.txtCardNumber);
        cardNumber.setText(IsoUtils.maskedPAN(Tran.PAN));
        TextView cardScheme = view.findViewById(R.id.txtCardType);
        cardScheme.setText(Tran.CardScheme);
        TextView TID = view.findViewById(R.id.txtTransactionId);
        TID.setText(Tran.TID);
        TextView TC= view.findViewById(R.id.txtInvoice);
        TC.setText(Tran.TC);
        TextView RRn= view.findViewById(R.id.txtRNN);
        RRn.setText(IsoUtils.hexToAscii(Tran.RRN));
        TextView batch = view.findViewById(R.id.txtBatch);
        batch.setText(IsoUtils.padToSix(Tran.batchNumber));
        TextView auth = view.findViewById(R.id.txtAuthNo);
        auth.setText(IsoUtils.hexToAscii(Tran.authCode));
        TextView merchant = view.findViewById(R.id.txtMerchantId);
        merchant.setText(Tran.MID);
        // Measure and layout the view
        view.measure(
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());

        Bitmap bitmap = Bitmap.createBitmap(
                view.getMeasuredWidth(),
                view.getMeasuredHeight(),
                Bitmap.Config.ARGB_8888);

        // Draw the view onto the canvas
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);

        printBitmap(bitmap);
    }


    //printing services
    private void printBitmap(Bitmap content) {
        try {
            if (MyApplication.app.sunmiPrinterService == null) {
                showToast("Print not supported");
                return;
            }

            Bitmap bitmap = content;
//            bitmap = getBinaryzationBitmap(bitmap);

            MyApplication.app.sunmiPrinterService.enterPrinterBuffer(true);
            MyApplication.app.sunmiPrinterService.printBitmap(bitmap, new InnerResultCallback() {
                @Override
                public void onRunResult(boolean isSuccess) throws RemoteException {
                    //addEndTime("printBitmap()");
                    LogUtil.e(TAG, "onRunResult-->isSuccess:" + isSuccess);
                    //showSpendTime();
                }

                @Override
                public void onReturnString(String result) throws RemoteException {
                    //addEndTime("printBitmap()");
                    LogUtil.e(TAG, "onReturnString-->result:" + result);
                    //showSpendTime();
                }

                @Override
                public void onRaiseException(int code, String msg) throws RemoteException {
                    //addEndTime("printBitmap()");
                    LogUtil.e(TAG, "onRaiseException-->code:" + code + ",msg:" + msg);
                    //showSpendTime();
                }

                @Override
                public void onPrintResult(int code, String msg) throws RemoteException {
                    //addEndTime("printBitmap()");
                    LogUtil.e(TAG, "onPrintResult-->code:" + code + ",msg:" + msg);
                    //showSpendTime();
                }
            });
            MyApplication.app.sunmiPrinterService.lineWrap(4, null);
            MyApplication.app.sunmiPrinterService.exitPrinterBuffer(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Create Bitmap by View
     */
    private Bitmap createViewBitmap(View v) {
        long start = System.currentTimeMillis();
        Bitmap bitmap = Bitmap.createBitmap(v.getWidth(), v.getHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        v.draw(canvas);
        LogUtil.e(TAG, "createViewBitmap time:" + (System.currentTimeMillis() - start));
        return bitmap;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void callForInventoryPurchase(String inventoryData){
        Gson gson = new Gson();
        Type productListType = new TypeToken<List<CartProduct>>() {}.getType();
        List<CartProduct> list = gson.fromJson(inventoryData, productListType);
        List<InventoryPurchaseReq> req = new ArrayList<>();

        for (int i = 0; i < list.size(); i++) {
            String timeStamp = Utility.getCurrentTimestamp();
            CartProduct product = list.get(i);
            double totalAmount = product.getCount() * product.getPrice();
            String offlineID = Utility.generateOfflineId();
            InventoryPurchaseReq request = new InventoryPurchaseReq(
                    "1234567809813456578",  //string
                    String.valueOf(product.getId()), //string
                     totalAmount,  //double
                     0.0,  // double
                     0,
                     product.getCount(),
                     product.getPrice(),
                    0,
                     timeStamp, timeStamp,
                      timeStamp,
                      "DEVICE_001",
                       product.getName(),
                       "liveStock/Kissan",
                         MID,
                         TID,
                        RRN,
                        STAN,
                  "Live/Kissan Purchase",
              "sale"
            );
           req.add(request);
        }

        inventoryPurchase(req);
    }

    private void inventoryPurchase(List<InventoryPurchaseReq> request){
        InventoryApi apiService = InventoryClient.getClient().create(InventoryApi.class);
        String authHeader = "Basic RGlnaWtoYXRhOkRrQDc4OTIkNTIlJQ==";
        Call<Object> call = apiService.requestPurchases(authHeader, request);
        call.enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                if(response.code() == 200){
                  Log.d("purchase", "Purchase Completed");
                    //loadInventory();
                }
            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                Log.d("purchase", "Purchase Failed");
            }
        });
    }

}