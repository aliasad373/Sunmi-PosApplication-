package com.mindcrafts.posapp.activitiese

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Build
import android.os.Bundle
import android.os.RemoteException
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.mindcrafts.posapp.R
import com.mindcrafts.posapp.app.BaseAppCompatActivity
import com.mindcrafts.posapp.app.MyApplication
import com.mindcrafts.posapp.database.DBInstance
import com.mindcrafts.posapp.database.entity.Transaction
import com.mindcrafts.posapp.databinding.ActivityTransactionDetailBinding
import com.mindcrafts.posapp.iso8583.builder.Void
import com.mindcrafts.posapp.iso8583.isoUtils.IsoUtils
import com.mindcrafts.posapp.network.NetworkUtil
import com.mindcrafts.posapp.network.callback.NetworkCallback
import com.mindcrafts.posapp.storage.SharedPrefHelper
import com.mindcrafts.posapp.utils.LogUtil
import com.mindcrafts.posapp.utils.Utility
import com.mindcrafts.posapp.view.LoadingDialogNew
import com.sunmi.peripheral.printer.InnerResultCallback
import java.util.concurrent.Executors

class TransactionDetailActivity : AppCompatActivity(),  NetworkCallback{

    lateinit var binding: ActivityTransactionDetailBinding
    lateinit var dialouge:LoadingDialogNew;
    private var db: DBInstance? = null
    private val executor = Executors.newSingleThreadExecutor()
    private lateinit var transaction:Transaction;
    private  var id:Int = 0 ;
    lateinit var context:Context
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding= ActivityTransactionDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        dialouge = LoadingDialogNew(this);
        context = this;
        binding.tvMid.setText("MID: " + SharedPrefHelper.getInstance(this).getMID("MID"))
        binding.tvTid.setText("TID: " + SharedPrefHelper.getInstance(this).getTID("TID"))
        binding.timeStamp.text = Utility.getCurrentTimestamp()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        db = DBInstance.getInstance(this);
        var intent = intent
        id = intent.getIntExtra("id", 0);
        if(id != 0){
            fetchTransaction(id);
        }
        binding.btnMarkVoid.setOnClickListener {
            dialouge.show("Marking Void ... Wait")
           var VoidMessage = Void(transaction);
             var  voidMsg = VoidMessage.createVoid();
            Log.d("VOID-MSG",byteArrayToHexString(voidMsg))
            var network =NetworkUtil();
            network.HostCall(voidMsg, this);
        }

        binding.backBtn.setOnClickListener{
            val back = Intent(
                this,
                AllTransactionActivity::class.java
            )
            startActivity(back)
            finish()
        }

    }

    fun  fetchTransaction(id:Int){
        executor.execute {
             transaction = db?.userDao()!!.getTransactionById(id);
            runOnUiThread {
                binding.txtMerchantName.text = SharedPrefHelper.getInstance(context).getMerchant("bname")
                binding.txtLocation.text = SharedPrefHelper.getInstance(context).getAddress("address")
                binding.txtTime.text = transaction.time
                binding.txtDate.text = transaction.date
                binding.txtCardType.text= transaction.CardScheme
                binding.txtCardNumber.text =IsoUtils.maskedPAN(transaction.PAN)
                binding.txtTotalAmount.text= IsoUtils.getClearAmount(transaction.Amount)
                binding.txtRRN.text = IsoUtils.hexToAscii(transaction.RRN)
             binding.txtBatch.text = IsoUtils.padToSix(transaction.batchNumber)
             binding.txtAuthNo.text = IsoUtils.hexToAscii(transaction.authCode)
             binding.txtInvoice.text =transaction.TC
             binding.txtMerchantId.text =transaction.MID
             binding.txtTransactionId.text =transaction.TID
             bindArqc(binding.llArqc, binding.txtArqc, transaction.cryptogaram)
            }
            if(transaction.v_status == "1" || transaction.s_status == "1"){
                binding.btnMarkVoid.visibility = View.GONE
            }
        }
    }
    override fun onCallSuccess(response: ByteArray?) {
        Log.d("response-succes", byteArrayToHexString(response!!))
        updateTransactionStatus()
    }

    override fun onError(msg: String?) {
        Log.d("response-succes",msg!!);
    }

    fun byteArrayToHexString(byteArray: ByteArray): String {
        return byteArray.joinToString("") { String.format("%02X", it) }
    }

    fun updateTransactionStatus(){
       executor.execute{
          db?.userDao()?.updateVoidStatus(id, "1")
          Log.d("Room DB","record updated")
           runOnUiThread {
               dialouge.dismiss()
               readyTheSlip(transaction)
               showVoidSuccessDialog()
               binding.btnMarkVoid.visibility =View.GONE
           }
       }

    }

    private fun readyTheSlip(Tran: Transaction) {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.slip_print, null)

        val time = view.findViewById<TextView>(R.id.txtTime)
        time.text = Tran.time
        val date = view.findViewById<TextView>(R.id.txtDate)
        date.text = Tran.date
        val location = view.findViewById<TextView>(R.id.txtLocation)
        location.text = SharedPrefHelper.getInstance(this).getAddress("address")
        val merchantBusiness = view.findViewById<TextView>(R.id.txtMerchantName)
        merchantBusiness.text = SharedPrefHelper.getInstance(context).getBusiness("bname")
        val amount = view.findViewById<TextView>(R.id.txtTotalAmount)
        amount.text = IsoUtils.getClearAmount(Tran.Amount) + " Pkr"
        val cardNumber = view.findViewById<TextView>(R.id.txtCardNumber)
        cardNumber.text = IsoUtils.maskedPAN(Tran.PAN)
        val cardScheme = view.findViewById<TextView>(R.id.txtCardType)
        cardScheme.text = Tran.CardScheme
        val TID = view.findViewById<TextView>(R.id.txtTransactionId)
        TID.text = Tran.TID
        val TC = view.findViewById<TextView>(R.id.txtInvoice)
        TC.text = Tran.TC
        val RRn = view.findViewById<TextView>(R.id.txtRNN)
        RRn.text = IsoUtils.hexToAscii(Tran.RRN)
        val batch = view.findViewById<TextView>(R.id.txtBatch)
        batch.text = IsoUtils.padToSix(Tran.batchNumber)
        val auth = view.findViewById<TextView>(R.id.txtAuthNo)
        auth.text = IsoUtils.hexToAscii(Tran.authCode)
        val merchant = view.findViewById<TextView>(R.id.txtMerchantId)
        merchant.text = Tran.MID
        bindArqc(view.findViewById(R.id.llArqc), view.findViewById(R.id.txtArqc), Tran.cryptogaram)
        // Measure and layout the view
        view.measure(
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        )
        view.layout(0, 0, view.measuredWidth, view.measuredHeight)

        val bitmap = Bitmap.createBitmap(
            view.measuredWidth,
            view.measuredHeight,
            Bitmap.Config.ARGB_8888
        )

        // Draw the view onto the canvas
        val canvas = Canvas(bitmap)
        view.draw(canvas)

        printBitmap(bitmap)
    }

    /** Show ARQC (EMV 9F26) when present; omit the row if missing (e.g. magstripe). */
    private fun bindArqc(row: View?, valueView: TextView?, cryptogram: String?) {
        if (row == null || valueView == null) {
            return
        }
        val value = cryptogram?.trim().orEmpty()
        if (value.isNotEmpty()) {
            valueView.text = value.uppercase()
            row.visibility = View.VISIBLE
        } else {
            valueView.text = ""
            row.visibility = View.GONE
        }
    }

    private fun printBitmap(content: Bitmap) {
        try {
            if (MyApplication.app.sunmiPrinterService == null) {
                Utility.showToast("Print not supported")
                return
            }

            val bitmap = content

            //            bitmap = getBinaryzationBitmap(bitmap);
            MyApplication.app.sunmiPrinterService.enterPrinterBuffer(true)
            MyApplication.app.sunmiPrinterService.printBitmap(
                bitmap,
                object : InnerResultCallback() {
                    @Throws(RemoteException::class)
                    override fun onRunResult(isSuccess: Boolean) {
                        //addEndTime("printBitmap()");
                        LogUtil.e(
                            BaseAppCompatActivity.TAG,
                            "onRunResult-->isSuccess:$isSuccess"
                        )


                        //showSpendTime();
                    }

                    @Throws(RemoteException::class)
                    override fun onReturnString(result: String) {
                        //addEndTime("printBitmap()");
                        LogUtil.e(
                            BaseAppCompatActivity.TAG,
                            "onReturnString-->result:$result"
                        )

                        //showSpendTime();
                    }

                    @Throws(RemoteException::class)
                    override fun onRaiseException(code: Int, msg: String) {
                        //addEndTime("printBitmap()");
                        LogUtil.e(
                            BaseAppCompatActivity.TAG,
                            "onRaiseException-->code:$code,msg:$msg"
                        )
                        //showSpendTime();
                    }

                    @Throws(RemoteException::class)
                    override fun onPrintResult(code: Int, msg: String) {
                        //addEndTime("printBitmap()");
                        LogUtil.e(
                            BaseAppCompatActivity.TAG,
                            "onPrintResult-->code:$code,msg:$msg"
                        )
                        //showSpendTime();
                    }
                })
            MyApplication.app.sunmiPrinterService.lineWrap(4, null)
            MyApplication.app.sunmiPrinterService.exitPrinterBuffer(true)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun showVoidSuccessDialog() {
        AlertDialog.Builder(this).apply {
            setTitle("Success")
            setMessage("Void successful")
            setCancelable(false) // Prevents closing the dialog by clicking outside

            // "OK" button using a clean Kotlin lambda
            setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()

                // Go back to the main dashboard (Choose ONE option below)

                // OPTION A: If Dashboard is the previous screen in the stack
                finish()

                /* // OPTION B: Explicitly launch DashboardActivity and clear back stack
                val intent = Intent(this@YourCurrentActivity, DashboardActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                }
                startActivity(intent)
                finish()
                */
            }

            // Create and show the dialog
            create()
            show()
        }
    }
}