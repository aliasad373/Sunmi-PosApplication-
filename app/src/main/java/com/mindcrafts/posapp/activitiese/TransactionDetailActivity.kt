package com.mindcrafts.posapp.activitiese

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.mindcrafts.posapp.R
import com.mindcrafts.posapp.activitiese.settlementreport.SummeryReport
import com.mindcrafts.posapp.database.DBInstance
import com.mindcrafts.posapp.database.entity.Transaction
import com.mindcrafts.posapp.databinding.ActivityTransactionDetailBinding
import com.mindcrafts.posapp.iso8583.builder.Void
import com.mindcrafts.posapp.iso8583.isoUtils.IsoUtils
import com.mindcrafts.posapp.network.NetworkUtil
import com.mindcrafts.posapp.network.callback.NetworkCallback
import com.mindcrafts.posapp.storage.SharedPrefHelper
import com.mindcrafts.posapp.utils.Utility
import com.mindcrafts.posapp.view.LoadingDialogNew
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
               binding.btnMarkVoid.visibility =View.GONE
           }
       }

    }
}