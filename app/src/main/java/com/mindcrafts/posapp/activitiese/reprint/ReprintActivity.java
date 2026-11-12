package com.mindcrafts.posapp.activitiese.reprint;

import static com.mindcrafts.posapp.app.BaseAppCompatActivity.TAG;
import static com.mindcrafts.posapp.utils.Utility.showToast;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Build;
import android.os.Bundle;
import android.os.RemoteException;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.mindcrafts.posapp.R;
import com.mindcrafts.posapp.app.MyApplication;
import com.mindcrafts.posapp.database.DBInstance;
import com.mindcrafts.posapp.database.entity.Transaction;
import com.mindcrafts.posapp.databinding.ActivityReprint2Binding;
import com.mindcrafts.posapp.iso8583.isoUtils.IsoUtils;
import com.mindcrafts.posapp.storage.SharedPrefHelper;
import com.mindcrafts.posapp.utils.LogUtil;
import com.mindcrafts.posapp.utils.Utility;
import com.sunmi.peripheral.printer.InnerResultCallback;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ReprintActivity extends AppCompatActivity {
     ActivityReprint2Binding binding;
    private String MID;
    private String TID;
    private DBInstance instance;
    Transaction transaction;
    Context context;
    private ExecutorService executor = Executors.newSingleThreadExecutor();
    private int id = 0 ;
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityReprint2Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
         context = this;
        //
        binding.tvMid.setText("MID: " + SharedPrefHelper.getInstance(this).getMID("MID"));
        binding.tvTid.setText("TID: " + SharedPrefHelper.getInstance(this).getTID("TID"));
        binding.txtMerchantName.setText(SharedPrefHelper.getInstance(this).getBusiness("bname"));
        binding.timeStamp.setText(Utility.getCurrentTimestamp());
         // DB init
        instance = DBInstance.getInstance(this);
        Intent intent = getIntent();
        id  = intent.getIntExtra("id", 0);
        if(id != 0){
            fetchTransaction(id);
        }
         //

        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(ReprintActivity.this, Reprint.class);
                startActivity(intent1);
                finish();
            }
        });

        binding.btnReprint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                readyTheSlip(transaction);
            }
        });
    }

    private void fetchTransaction(int id) {
        executor.execute(new Runnable() {
            @Override
            public void run() {

                transaction = instance.userDao().getTransactionById(id);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        binding.txtMerchantName.setText(
                                SharedPrefHelper.getInstance(context).getMerchant("bname")
                        );

                        binding.txtLocation.setText(
                                SharedPrefHelper.getInstance(context).getAddress("address")
                        );

                        binding.txtTime.setText(transaction.time);
                        binding.txtDate.setText(transaction.date);
                        binding.txtCardType.setText(transaction.CardScheme);
                        binding.txtCardNumber.setText(IsoUtils.maskedPAN(transaction.PAN));
                        binding.txtTotalAmount.setText(IsoUtils.getClearAmount(transaction.Amount));
                        binding.txtRRN.setText(IsoUtils.hexToAscii(transaction.RRN));
                        binding.txtBatch.setText(IsoUtils.padToSix(transaction.batchNumber));
                        binding.txtAuthNo.setText(IsoUtils.hexToAscii(transaction.authCode));
                        binding.txtInvoice.setText(transaction.TC);
                        binding.txtMerchantId.setText(transaction.MID);
                        binding.txtTransactionId.setText(transaction.TID);
                        bindArqc(binding.llArqc, binding.txtArqc, transaction.cryptogaram);
                    }
                });
            }
        });
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
        bindArqc(view.findViewById(R.id.llArqc), view.findViewById(R.id.txtArqc), Tran.cryptogaram);
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

    /** Show ARQC (EMV 9F26) when present; omit the row if missing (e.g. magstripe). */
    private void bindArqc(View row, TextView valueView, String cryptogram) {
        if (row == null || valueView == null) {
            return;
        }
        if (cryptogram != null && !cryptogram.trim().isEmpty()) {
            valueView.setText(cryptogram.trim().toUpperCase());
            row.setVisibility(View.VISIBLE);
        } else {
            valueView.setText("");
            row.setVisibility(View.GONE);
        }
    }

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
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showPrintDialogue();
                        }
                    });

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

    private void showPrintDialogue() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // 1. Set the Dialog Title and Message
        builder.setTitle("Success")
                .setMessage("Slip reprinted successfully")
                .setCancelable(false); // Prevents closing the dialog by clicking outside of it

        // 2. Set the "OK" button and its action
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Dismiss the dialog
                finish();
                dialog.dismiss();
            }
        });

        // 3. Create and display the dialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

}