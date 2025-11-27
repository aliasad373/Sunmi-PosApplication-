package com.mindcrafts.posapp.emv;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.content.res.AppCompatResources;


import com.google.gson.Gson;
import com.mindcrafts.posapp.R;
import com.mindcrafts.posapp.activitiese.Home;
import com.mindcrafts.posapp.activitiese.HostDecision;
import com.mindcrafts.posapp.app.BaseAppCompatActivity;
import com.mindcrafts.posapp.app.MyApplication;
import com.mindcrafts.posapp.database.DAO;
import com.mindcrafts.posapp.database.DBInstance;
import com.mindcrafts.posapp.database.entity.Transaction;
import com.mindcrafts.posapp.databinding.ActivityEmvIcBinding;
import com.mindcrafts.posapp.iso8583.builder.Batch;
import com.mindcrafts.posapp.iso8583.builder.Sales;
import com.mindcrafts.posapp.iso8583.isoUtils.IsoConfig;
import com.mindcrafts.posapp.iso8583.parser.ISOParser;
import com.mindcrafts.posapp.network.NetworkUtil;
import com.mindcrafts.posapp.network.callback.NetworkCallback;
import com.mindcrafts.posapp.storage.SharedPrefHelper;
import com.mindcrafts.posapp.utils.ByteUtil;
import com.mindcrafts.posapp.utils.Constant;
import com.mindcrafts.posapp.utils.LogUtil;
import com.mindcrafts.posapp.utils.SettingUtil;
import com.mindcrafts.posapp.utils.TLV;
import com.mindcrafts.posapp.utils.TLVUtil;
import com.mindcrafts.posapp.utils.ThreadPoolUtil;
import com.mindcrafts.posapp.utils.Utility;
import com.mindcrafts.posapp.wraper.CheckCardCallbackV2Wrapper;
import com.mindcrafts.posapp.wraper.PinPadListenerV2Wrapper;
import com.sunmi.pay.hardware.aidl.bean.CardInfo;
import com.sunmi.pay.hardware.aidlv2.AidlConstantsV2;
import com.sunmi.pay.hardware.aidlv2.AidlErrorCodeV2;
import com.sunmi.pay.hardware.aidlv2.bean.EMVCandidateV2;
import com.sunmi.pay.hardware.aidlv2.bean.PinPadConfigV2;
import com.sunmi.pay.hardware.aidlv2.emv.EMVListenerV2;
import com.sunmi.pay.hardware.aidlv2.emv.EMVOptV2;
import com.sunmi.pay.hardware.aidlv2.pinpad.PinPadListenerV2;
import com.sunmi.pay.hardware.aidlv2.pinpad.PinPadOptV2;
import com.sunmi.pay.hardware.aidlv2.readcard.CheckCardCallbackV2;
import com.sunmi.pay.hardware.aidlv2.readcard.ReadCardOptV2;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This page show the emv procedure.
 * Any transaction which should do emv process can refer
 * to this page.
 */
public class ICProcessActivity extends BaseAppCompatActivity implements NetworkCallback{

    ActivityEmvIcBinding binding;
    String MID;
    String TID;
    private android.app.AlertDialog removeCardDialog;
    NetworkCallback callback;

    Map<String, String> resp210;

    Context context;
    private EMVOptV2 mEMVOptV2;

   private Transaction Tran;
    private PinPadOptV2 mPinPadOptV2;
    private ReadCardOptV2 mReadCardOptV2;
    private EditText mEditAmount;
    private TextView mTvShowInfo;
    private Button mBtnOperate;

    Map<String, TLV> TLVMap;
    String pinBlock;
    private int mCardType;  // card type
    private String mCardNo; // card number
    private int mPinType;   // 0-online pin, 1-offline pin
    private String mCertInfo;
    private int mSelectIndex;

    private int mAppSelect = 0;

    private int mProcessStep;

    private  String PinBlock;
    private boolean goToSlip = false;
    private boolean isRfID = false;
    private AlertDialog mAppSelectDialog;
    private Map<String, String> configMap;

    private static final int EMV_APP_SELECT = 1;
    private static final int EMV_FINAL_APP_SELECT = 2;
    private static final int EMV_CONFIRM_CARD_NO = 3;
    private static final int EMV_CERT_VERIFY = 4;
    private static final int EMV_SHOW_PIN_PAD = 5;
    private static final int EMV_ONLINE_PROCESS = 6;
    private static final int EMV_SIGNATURE = 7;
    private static final int EMV_TRANS_SUCCESS = 888;
    private static final int EMV_TRANS_FAIL = 999;
    private static final int REMOVE_CARD = 1000;

    private static final int PIN_CLICK_NUMBER = 50;
    private static final int PIN_CLICK_PIN = 51;
    private static final int PIN_CLICK_CONFIRM = 52;
    private static final int PIN_CLICK_CANCEL = 53;
    private static final int PIN_ERROR = 54;

    private static final int START_TRANSACTION = 55;
    boolean showPin = false;
    private String amount;
    private  String cartProducts;

    private final Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case EMV_FINAL_APP_SELECT:
                    importFinalAppSelectStatus(0);
                    break;
                case EMV_APP_SELECT:
                    dismissLoadingDialog();
                    String[] candiNames = (String[]) msg.obj;
                    mAppSelectDialog = new AlertDialog.Builder(ICProcessActivity.this)
                            .setTitle(R.string.emv_app_select)
                            .setNegativeButton(R.string.cancel, (dialog, which) -> {
                                        importAppSelect(-1);
                                    }
                            )
                            .setPositiveButton(R.string.ok, (dialog, which) -> {
                                        //showLoadingDialog(R.string.handling);
                                        importAppSelect(mSelectIndex);
                                    }
                            )
                            .setSingleChoiceItems(candiNames, 0, (dialog, which) -> {
                                        mSelectIndex = which;
                                        LogUtil.e(Constant.TAG, "singleChoiceItems which:" + which);
                                    }
                            ).create();
                    mSelectIndex = 0;
                    mAppSelectDialog.show();
                    break;
                case EMV_CONFIRM_CARD_NO:

                    if(!isRfID){
                        importCardNoStatus(0);
                        binding.cardNumberContainer.setVisibility(View.VISIBLE);
                        String maskedNo = EmvUtil.maskCardNumber(mCardNo);
                        binding.cardNo.setText(maskedNo);
                    }else{
                        importCardNoStatus(0);
                    }
                   /* dismissLoadingDialog();
                    mTvShowInfo.setText(mCardNo);
                    mBtnOperate.setVisibility(View.VISIBLE);
                    mBtnOperate.setText(R.string.emv_confirm_card_no);*/
                    break;
                case EMV_CERT_VERIFY:
                    dismissLoadingDialog();
                    //mTvShowInfo.setText(mCertInfo);
                    mBtnOperate.setText(R.string.emv_confirm_cert);
                    break;
                case EMV_SHOW_PIN_PAD:
                    dismissLoadingDialog();
                    double value = Double.parseDouble(amount);
                    if(isRfID && value < 3000){
                        importPinInputStatus(0);
                    }else{
                        initPinPad();
                    }
                    break;
                case EMV_ONLINE_PROCESS:
                    mockRequestToServer();
                    break;
                case EMV_SIGNATURE:
                    importSignatureStatus(0);
                    break;
                case PIN_CLICK_NUMBER:
                    break;
                case PIN_CLICK_PIN:
                    //Log.d("message-pinblock");
                    byte[] pin = (byte[])msg.obj;
                    PinBlock = ByteUtil.bytes2HexStr(pin);
                    LogUtil.e("encrypted-PinBlock", ByteUtil.bytes2HexStr(pin));
                    importPinInputStatus(0);
                    break;
                case PIN_CLICK_CONFIRM:
                    importPinInputStatus(2);
                    break;
                case PIN_CLICK_CANCEL:
                    showToast("user cancel");
                    importPinInputStatus(1);
                    break;
                case PIN_ERROR:
                    showToast("error:" + msg.obj + " -- " + msg.arg1);
                    importPinInputStatus(3);
                    break;
                case EMV_TRANS_FAIL:
                    resetUI();
                    dismissLoadingDialog();
                    //showToast("error:" + msg.obj + " -- " + msg.arg1);
                    break;
                case EMV_TRANS_SUCCESS:
                    resetUI();
                    checkAndRemoveCard();
                    //showToast(R.string.success);
                    break;
                case REMOVE_CARD:
                    checkAndRemoveCard();
                    Log.d("removeCard", "remove card called");
                    break;
                case START_TRANSACTION:
                       doTran();
                       break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEmvIcBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        //
        binding.tvMid.setText("MID: " + SharedPrefHelper.getInstance(this).getMID("MID"));
        binding.tvTid.setText("TID: " + SharedPrefHelper.getInstance(this).getTID("TID"));
        PinBlock = "";
         //
         //
        callback = this;
        context = this;
        pinBlock = "";
        MID = SharedPrefHelper.getInstance(this).getMID("MID");
        TID = SharedPrefHelper.getInstance(this).getTID("TID");

        //
        Intent intent = getIntent();

        amount = intent.getStringExtra("amount");
        cartProducts = intent.getStringExtra("cart");
        //amount = "3500.00";
        initData();
        initView();
    }

    private void initView() {
        mEMVOptV2 = MyApplication.app.emvOptV2;
        mPinPadOptV2 = MyApplication.app.pinPadOptV2;
        mReadCardOptV2 = MyApplication.app.readCardOptV2;

       // mEditAmount = findViewById(R.id.edit_amount);
       // mTvShowInfo = findViewById(R.id.tv_info);
        //mBtnOperate = findViewById(R.id.mb_ok);
        //mBtnOperate.setOnClickListener(this);
        binding.btnCancel.setOnClickListener(this);
    }

    /**
     * Do essential initialization, client App should
     * initialize their own data at this step.
     * Note: this method just show how to initialize data before
     * the emv process, the initialized data may not useful for your
     * App. Please init your own data.
     */
    private void initData() {
        // disable check card buzzer
        SettingUtil.setBuzzerEnable(false);
        // Client should config their own countryCode,capability,etc...
        configMap = EmvUtil.getConfig(EmvUtil.COUNTRY_PAKISTAN);
        ThreadPoolUtil.executeInCachePool(
                () -> {
                    EmvUtil.initKey();
                    EmvUtil.initAidAndRid();
                    //EmvUtil.initAIDs();
                    EmvUtil.setTerminalParam(configMap);
                    //showToast("emv init process finished.");
                    mHandler.obtainMessage(START_TRANSACTION).sendToTarget();
                }
        );
    }

    @Override
    public void onBackPressed() {
        if (mProcessStep == EMV_APP_SELECT) {
            importAppSelect(-1);
        } else if (mProcessStep == EMV_FINAL_APP_SELECT) {
            importFinalAppSelectStatus(-1);
        } else if (mProcessStep == EMV_CONFIRM_CARD_NO) {
            importCardNoStatus(1);
        } else if (mProcessStep == EMV_CERT_VERIFY) {
            importCertStatus(1);
        } else if (mProcessStep == PIN_ERROR) {
            importPinInputStatus(3);
        } else if (mProcessStep == EMV_ONLINE_PROCESS) {
            importOnlineProcessStatus(1);
        } else if (mProcessStep == EMV_SIGNATURE) {
            importSignatureStatus(1);
        }
        super.onBackPressed();
    }
   public void doTran(){
       if (mProcessStep == 0) {
           LogUtil.e(Constant.TAG, "***************************************************************");
           LogUtil.e(Constant.TAG, "****************************Start Process**********************");
           LogUtil.e(Constant.TAG, "***************************************************************");
           //mTvShowInfo.setText("");
           //Log.d("Amount", amount);
           try {
               // Before check card, initialize emv process(clear all TLV)
               addStartTimeWithClear("start");
               addStartTime("initEmvProcess");
               mEMVOptV2.initEmvProcess();
               addEndTime("initEmvProcess");
               addStartTime("initEmvTlvData");
               initEmvTlvData();
               addEndTime("initEmvTlvData");
               // Convert to double first
               amount = amount.replace(",", "");
               double value = Double.parseDouble(amount);

// Multiply by 100 and cast to long
               long parseLong = (long) (value * 100);

               Log.d("checkingAmount", "doTran:"+amount+"and"+ parseLong);
               if (value > 0) {
                   checkCard();
               } else {
                   showToast(R.string.card_cost_hint);
               }
           } catch (Exception e) {
               Log.d("checkingAmount", "catch:"+amount+"and"+e);
               e.printStackTrace();
               showToast(R.string.card_cost_hint);
           }
       } else if (mProcessStep == EMV_CONFIRM_CARD_NO) {
           //showLoadingDialog(R.string.handling);
           importCardNoStatus(0);
       } else if (mProcessStep == EMV_CERT_VERIFY) {
           //showLoadingDialog(R.string.handling);
           importCertStatus(0);
       }
   }
    @Override
    public void onClick(View v) {
        final int id = v.getId();
        if(id == R.id.btnCancel){
            finish();
        }
    }

    /**
     * Set tlv essential tlv data
     */
    private void initEmvTlvData() {
        try {
            // set PayPass(MasterCard) tlv data
            String[] tagsPayPass = {"DF8117", "DF8118", "DF8119", "DF811F", "DF811E", "DF812C",
                    "DF8123", "DF8124", "DF8125", "DF8126",
                    "DF811B", "DF811D", "DF8122", "DF8120", "DF8121"};
            String[] valuesPayPass = {"E0", "F8", "F0", "C8", "00", "00",
                    "000000000000", "000002000000", "999999999999", "000000200000",
                    "B0", "02", "F45084800C", "F45084800C", "0000000000"};
            mEMVOptV2.setTlvList(AidlConstantsV2.EMV.TLVOpCode.OP_PAYPASS, tagsPayPass, valuesPayPass);

            // set AMEX(AmericanExpress) tlv data
            String[] tagsAE = {"9F6D", "9F6E", "9F33", "9F35", "DF8168", "DF8167", "DF8169", "DF8170"};
            String[] valuesAE = {"C0", "D8E00000", "E0E888", "22", "00", "00", "00", "60"};
            mEMVOptV2.setTlvList(AidlConstantsV2.EMV.TLVOpCode.OP_AE, tagsAE, valuesAE);

            String[] tagsJCB = {"9F53", "DF8161"};
            String[] valuesJCB = {"708000", "7F00"};
            mEMVOptV2.setTlvList(AidlConstantsV2.EMV.TLVOpCode.OP_JCB, tagsJCB, valuesJCB);

            String[] tagsPure = {
                    "DF8134", "DF8133"
            };
            String[] valuesPure = {
                    "01","3600F002F9"
                  };
            mEMVOptV2.setTlvList(AidlConstantsV2.EMV.TLVOpCode.OP_PURE, tagsPure, valuesPure);


        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * Start check card
     */
    private void checkCard() {
        try {
            //showLoadingDialog(R.string.emv_swing_card_ic);
            int cardType = AidlConstantsV2.CardType.NFC.getValue() | AidlConstantsV2.CardType.IC.getValue();
            addStartTime("checkCard()");
            mReadCardOptV2.checkCard(cardType, mCheckCardCallback, 60);
            // mReadCardOptV2.checkCardEx(cardType, 0, 1, mCheckCardCallback, 60);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showRemoveCardDialog(Context context) {
        // Check if dialog is already showing
        if (removeCardDialog != null && removeCardDialog.isShowing()) {
            return;
        }

        // Build the dialog
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
        builder.setTitle("Notice");
        builder.setMessage("Please remove the card");
        builder.setCancelable(false);
        // Create and show
        removeCardDialog = builder.create();
        removeCardDialog.show();
    }
    private void dismissRemoveCardDialog() {
        if (removeCardDialog != null && removeCardDialog.isShowing()) {
            removeCardDialog.dismiss();
            removeCardDialog = null; // Reset after dismissing
        }
    }

    /**
     * Check card callback
     */
    private final CheckCardCallbackV2 mCheckCardCallback = new CheckCardCallbackV2Wrapper() {

        @Override
        public void findMagCard(Bundle bundle) throws RemoteException {
            addEndTime("checkCard()");
            LogUtil.e(Constant.TAG, "findMagCard:" + bundle);
        }

        @Override
        public void findICCard(String atr) throws RemoteException {
            addEndTime("checkCard()");
            LogUtil.e(Constant.TAG, "findICCard:" + atr);
            //IC card Beep buzzer when check card success

            MyApplication.app.basicOptV2.buzzerOnDevice(1, 2750, 200, 0);
            mCardType = AidlConstantsV2.CardType.IC.getValue();
            transactProcess();
        }

        @Override
        public void findRFCard(String uuid) throws RemoteException {
            addEndTime("checkCard()");
            isRfID = true;
            LogUtil.e(Constant.TAG, "findRFCard:" + uuid);
            mCardType = AidlConstantsV2.CardType.NFC.getValue();
            transactProcess();
        }

        @Override
        public void onError(int code, String message) throws RemoteException {
            addEndTime("checkCard()");
            String error = "onError:" + message + " -- " + code;
            LogUtil.e(Constant.TAG, error);
            showToast(error);
            dismissLoadingDialog();
        }
    };

    /**
     * Start emv transact process
     */
    private void transactProcess() {
        LogUtil.e(Constant.TAG, "transactProcess");
        try {
            amount = amount.replace(",", "");
            double value = Double.parseDouble(amount);

// Multiply by 100 and cast to long
            long parseLong = (long) (value * 100);
            Bundle bundle = new Bundle();
            bundle.putString("amount", String.valueOf(parseLong));
            bundle.putString("transType", "00");
            //flowType:0x01-emv standard, 0x04：NFC-Speedup
            //Note:(1) flowType=0x04 only valid for QPBOC,PayPass,PayWave contactless transaction
            //     (2) set fowType=0x04, only EMVListenerV2.onRequestShowPinPad(),
            //         EMVListenerV2.onCardDataExchangeComplete() and EMVListenerV2.onTransResult() may will be called.
            if (mCardType == AidlConstantsV2.CardType.NFC.getValue()) {
                bundle.putInt("flowType", AidlConstantsV2.EMV.FlowType.TYPE_NFC_SPEEDUP);
            } else {
                bundle.putInt("flowType", AidlConstantsV2.EMV.FlowType.TYPE_EMV_STANDARD);
            }
            bundle.putInt("cardType", mCardType);
//            bundle.putBoolean("preProcessCompleted", false);
//            bundle.putInt("emvAuthLevel", 0);
            addTransactionStartTimes();
            mEMVOptV2.transactProcessEx(bundle, mEMVListener);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void addTransactionStartTimes() {
        addStartTime("transactProcess");
        addStartTime("onTransactionStart");
        addStartTime("onWaitAppSelect()");
        addStartTime("onAppFinalSelect()");
        addStartTime("onConfirmCardNo()");
        addStartTime("onRequestShowPinPad()");
        addStartTime("onRequestSignature()");
        addStartTime("onCertVerify()");
        addStartTime("onOnlineProc()");
        addStartTime("onCardDataExchangeComplete()");
        addStartTime("onTransResult()");
        addStartTime("onConfirmationCodeVerified()");
        addStartTime("onRequestDataExchange()");
        addStartTime("onTermRiskManagement()");
    }

    /**
     * EMV process callback
     */
    private final EMVListenerV2 mEMVListener = new EMVListenerV2.Stub() {
        /**
         * Notify client to do multi App selection, this method may called when card have more than one Application
         * <br/> For Contactless and flowType set as AidlConstants.FlowType.TYPE_NFC_SPEEDUP, this
         * method will not be called
         *
         * @param appNameList   The App list for selection
         * @param isFirstSelect is first time selection
         */
        @Override
        public void onWaitAppSelect(List<EMVCandidateV2> appNameList, boolean isFirstSelect) throws RemoteException {
            addEndTime("onWaitAppSelect()");
            LogUtil.e(Constant.TAG, "onWaitAppSelect isFirstSelect:" + isFirstSelect);
            mProcessStep = EMV_APP_SELECT;
            String[] candidateNames = getCandidateNames(appNameList);
            mHandler.obtainMessage(EMV_APP_SELECT, candidateNames).sendToTarget();
        }

        /**
         * Notify client the final selected Application
         * <br/> For Contactless and flowType set as AidlConstants.FlowType.TYPE_NFC_SPEEDUP, this
         * method will not be called
         *
         * @param tag9F06Value The final selected Application id
         */
        @Override
        public void onAppFinalSelect(String tag9F06Value) throws RemoteException {
            addEndTime("onAppFinalSelect()");

            LogUtil.e(Constant.TAG, "onAppFinalSelect tag9F06Value:" + tag9F06Value);
            if (tag9F06Value != null && tag9F06Value.length() > 0) {
                boolean isUnionPay = tag9F06Value.startsWith("A000000333");
                boolean isVisa = tag9F06Value.startsWith("A000000003");
                boolean isMaster = tag9F06Value.startsWith("A000000004")
                        || tag9F06Value.startsWith("A000000005");
                boolean isAmericanExpress = tag9F06Value.startsWith("A000000025");
                boolean isJCB = tag9F06Value.startsWith("A000000065");
                boolean isRupay = tag9F06Value.startsWith("A000000524");
                boolean isPure = tag9F06Value.startsWith("D999999999")
                        || tag9F06Value.startsWith("D888888888")
                        || tag9F06Value.startsWith("D777777777")
                        || tag9F06Value.startsWith("D666666666")
                        || tag9F06Value.startsWith("A000000615");
                String paymentType = "unknown";
                if (isUnionPay) {
                    paymentType = "UnionPay";
                    mAppSelect = 0;
                } else if (isVisa) {
                    paymentType = "Visa";
                    mAppSelect = 1;
                } else if (isMaster) {
                    paymentType = "MasterCard";
                    mAppSelect = 2;
                } else if (isAmericanExpress) {
                    paymentType = "AmericanExpress";
                } else if (isJCB) {
                    paymentType = "JCB";
                } else if (isRupay) {
                    paymentType = "Rupay";
                } else if (isPure) {
                    paymentType = "Pure";
                }
                LogUtil.e(Constant.TAG, "detect " + paymentType + " card");
            }
            mProcessStep = EMV_FINAL_APP_SELECT;
            mHandler.obtainMessage(EMV_FINAL_APP_SELECT, tag9F06Value).sendToTarget();
        }

        /**
         * Notify client to confirm card number
         * <br/> For Contactless and flowType set as AidlConstants.FlowType.TYPE_NFC_SPEEDUP, this
         * method will not be called
         *
         * @param cardNo The card number
         */
        @Override
        public void onConfirmCardNo(String cardNo) throws RemoteException {
            addEndTime("onConfirmCardNo()");
            LogUtil.e(Constant.TAG, "onConfirmCardNo cardNo:" + cardNo);
            mCardNo = cardNo;
            mProcessStep = EMV_CONFIRM_CARD_NO;
            mHandler.obtainMessage(EMV_CONFIRM_CARD_NO).sendToTarget();
//            importCardNoStatus(0);
        }

        /**
         * Notify client to input PIN
         *
         * @param pinType    The PIN type, 0-online PIN，1-offline PIN
         * @param remainTime The the remain retry times of offline PIN, for online PIN, this param
         *                   value is always -1, and if this is the first time to input PIN, value
         *                   is -1 too.
         */
        @Override
        public void onRequestShowPinPad(int pinType, int remainTime) throws RemoteException {
            addEndTime("onRequestShowPinPad()");
            LogUtil.e(Constant.TAG, "onRequestShowPinPad pinType:" + pinType + " remainTime:" + remainTime);
            mPinType = pinType;
            if (mCardNo == null) {
                mCardNo = getCardNo();
            }
            mProcessStep = EMV_SHOW_PIN_PAD;
            mHandler.obtainMessage(EMV_SHOW_PIN_PAD).sendToTarget();
        }

        /**
         * Notify  client to do signature
         */
        @Override
        public void onRequestSignature() throws RemoteException {
            addEndTime("onRequestSignature()");
            LogUtil.e(Constant.TAG, "onRequestSignature");
            mProcessStep = EMV_SIGNATURE;
            mHandler.obtainMessage(EMV_SIGNATURE).sendToTarget();
        }

        /**
         * Notify client to do certificate verification
         *
         * @param certType The certificate type, refer to AidlConstants.CertType
         * @param certInfo The certificate info
         */
        @Override
        public void onCertVerify(int certType, String certInfo) throws RemoteException {
            addEndTime("onCertVerify()");
            LogUtil.e(Constant.TAG, "onCertVerify certType:" + certType + " certInfo:" + certInfo);
            mCertInfo = certInfo;
            mProcessStep = EMV_CERT_VERIFY;
            mHandler.obtainMessage(EMV_CERT_VERIFY).sendToTarget();
        }

        /**
         * Notify client to do online process
         */
        @Override
        public void onOnlineProc() throws RemoteException {
            addEndTime("onOnlineProc()");
            LogUtil.e(Constant.TAG, "onOnlineProcess");
            mProcessStep = EMV_ONLINE_PROCESS;
            mHandler.obtainMessage(EMV_ONLINE_PROCESS).sendToTarget();
        }

        /**
         * Notify client EMV kernel and card data exchange finished, client can remove card
         */
        @Override
        public void onCardDataExchangeComplete() throws RemoteException {
            addEndTime("onCardDataExchangeComplete()");
            LogUtil.e(Constant.TAG, "onCardDataExchangeComplete");
            if (mCardType == AidlConstantsV2.CardType.NFC.getValue()) {
                //NFC card Beep buzzer to notify remove card
                addStartTime("buzzerOnDevice()");
                MyApplication.app.basicOptV2.buzzerOnDevice(1, 2750, 200, 0);
                addEndTime("buzzerOnDevice()");
            }
        }

        /**
         * Notify client EMV process ended
         *
         * @param code The transaction result code, 0-success, 1-offline approval, 2-offline denial,
         *             4-try again, other value-error code
         * @param desc The corresponding message of this code
         */
        @Override
        public void onTransResult(int code, String desc) throws RemoteException {
            addEndTime("onTransResult()");
            if (mCardNo == null) {
                mCardNo = getCardNo();
            }
            LogUtil.e(Constant.TAG, "onTransResult code:" + code + " desc:" + desc);
            LogUtil.e(Constant.TAG, "***************************************************************");
            LogUtil.e(Constant.TAG, "****************************End Process************************");
            LogUtil.e(Constant.TAG, "***************************************************************");
            if (code == 0) {
                mHandler.obtainMessage(EMV_TRANS_SUCCESS, code, code, desc).sendToTarget();
            } else if (code == 4) {
                tryAgain();
            } else {
                mHandler.obtainMessage(EMV_TRANS_FAIL, code, code, desc).sendToTarget();
            }
            showSpendTime();
        }

        /**
         * Notify client the confirmation code verified(See phone)
         */
        @Override
        public void onConfirmationCodeVerified() throws RemoteException {
            addEndTime("onConfirmationCodeVerified()");
            showSpendTime();
            LogUtil.e(Constant.TAG, "onConfirmationCodeVerified");

            byte[] outData = new byte[512];
            int len = mEMVOptV2.getTlv(AidlConstantsV2.EMV.TLVOpCode.OP_PAYPASS, "DF8129", outData);
            if (len > 0) {
                byte[] data = new byte[len];
                System.arraycopy(outData, 0, data, 0, len);
                String hexStr = ByteUtil.bytes2HexStr(data);
                LogUtil.e(Constant.TAG, "DF8129: " + hexStr);
            }
            // card off
            mReadCardOptV2.cardOff(mCardType);
            runOnUiThread(() -> new AlertDialog.Builder(ICProcessActivity.this)
                    .setTitle("See Phone")
                    .setMessage("execute See Phone flow")
                    .setPositiveButton("OK", (dia, which) -> {
                                dia.dismiss();
                                // Restart transaction procedure.
                                try {
                                    mEMVOptV2.initEmvProcess();
                                    checkCard();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                    ).show()
            );
        }

        /**
         * Notify client to exchange data
         * <br/> This method only used for Russia MIR
         *
         * @param cardNo The card number
         */
        @Override
        public void onRequestDataExchange(String cardNo) throws RemoteException {
            addEndTime("onRequestDataExchange()");
            LogUtil.e(Constant.TAG, "onRequestDataExchange,cardNo:" + cardNo);
            mEMVOptV2.importDataExchangeStatus(0);
        }

        @Override
        public void onTermRiskManagement() throws RemoteException {
            addEndTime("onTermRiskManagement()");
            LogUtil.e(Constant.TAG, "onTermRiskManagement");
            mEMVOptV2.importTermRiskManagementStatus(0);
        }

        @Override
        public void onPreFirstGenAC() throws RemoteException {
            addEndTime("onPreFirstGenAC()");
            LogUtil.e(Constant.TAG, "onPreFirstGenAC");
            mEMVOptV2.importPreFirstGenACStatus(0);
        }

        @Override
        public void onDataStorageProc(String[] containerID, String[] containerContent) throws RemoteException {
            addEndTime("onDataStorageProc()");
            LogUtil.e(Constant.TAG, "onDataStorageProc,");
            //此回调为Dpas2.0专用
            //根据需求配置tag及values
            String[] tags = new String[0];
            String[] values = new String[0];
            mEMVOptV2.importDataStorage(tags, values);
        }

    };

    /** getCard number */
    private String getCardNo() {
        LogUtil.e(Constant.TAG, "getCardNo");
        try {
            String[] tagList = {"57", "5A"};
            byte[] outData = new byte[256];
            addStartTime("getCardNo()");
            int len = mEMVOptV2.getTlvList(AidlConstantsV2.EMV.TLVOpCode.OP_NORMAL, tagList, outData);
            addEndTime("getCardNo()");
            if (len <= 0) {
                LogUtil.e(Constant.TAG, "getCardNo error,code:" + len);
                return "";
            }
            byte[] bytes = Arrays.copyOf(outData, len);
            Map<String, TLV> tlvMap = TLVUtil.buildTLVMap(bytes);
            if (!TextUtils.isEmpty(Objects.requireNonNull(tlvMap.get("57")).getValue())) {
                TLV tlv57 = tlvMap.get("57");
                CardInfo cardInfo = parseTrack2(tlv57.getValue());
                return cardInfo.cardNo;
            }
            if (!TextUtils.isEmpty(Objects.requireNonNull(tlvMap.get("5A")).getValue())) {
                return Objects.requireNonNull(tlvMap.get("5A")).getValue();
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * Parse track2 data
     */
    public static CardInfo parseTrack2(String track2) {
        LogUtil.e(Constant.TAG, "track2:" + track2);
        String track_2 = stringFilter(track2);
        int index = track_2.indexOf("=");
        if (index == -1) {
            index = track_2.indexOf("D");
        }
        CardInfo cardInfo = new CardInfo();
        if (index == -1) {
            return cardInfo;
        }
        String cardNumber = "";
        if (track_2.length() > index) {
            cardNumber = track_2.substring(0, index);
        }
        String expiryDate = "";
        if (track_2.length() > index + 5) {
            expiryDate = track_2.substring(index + 1, index + 5);
        }
        String serviceCode = "";
        if (track_2.length() > index + 8) {
            serviceCode = track_2.substring(index + 5, index + 8);
        }
        LogUtil.e(Constant.TAG, "cardNumber:" + cardNumber + " expireDate:" + expiryDate + " serviceCode:" + serviceCode);
        cardInfo.cardNo = cardNumber;
        cardInfo.expireDate = expiryDate;
        cardInfo.serviceCode = serviceCode;
        return cardInfo;
    }

    /**
     * remove characters not number,=,D
     */
    static String stringFilter(String str) {
        String regEx = "[^0-9=D]";
        Pattern p = Pattern.compile(regEx);
        Matcher matcher = p.matcher(str);
        return matcher.replaceAll("").trim();
    }

    /**
     * Start show PinPad
     */
    private void initPinPad() {
        LogUtil.e(Constant.TAG, "initPinPad");
        try {
            PinPadConfigV2 pinPadConfig = new PinPadConfigV2();
            pinPadConfig.setPinPadType(0);
            pinPadConfig.setPinType(mPinType);
            pinPadConfig.setOrderNumKey(false);
            byte[] panBytes = mCardNo.substring(mCardNo.length() - 13, mCardNo.length() - 1).getBytes("US-ASCII");
            pinPadConfig.setPan(panBytes);
            pinPadConfig.setTimeout(60 * 1000); // input password timeout
            pinPadConfig.setPinKeyIndex(12);    // pik index
            pinPadConfig.setMaxInput(12);
            pinPadConfig.setMinInput(0);
            pinPadConfig.setKeySystem(0);
            pinPadConfig.setAlgorithmType(0);
            addStartTime("initPinPad()");
            mPinPadOptV2.initPinPad(pinPadConfig, mPinPadListener);
        } catch (Exception e) {
            e.printStackTrace();
            mHandler.obtainMessage(PIN_ERROR, Integer.MIN_VALUE, Integer.MIN_VALUE, "initPinPad() failure").sendToTarget();
        }
    }

    /**
     * Input pin callback
     */
    private final PinPadListenerV2 mPinPadListener = new PinPadListenerV2Wrapper() {

        @Override
        public void onPinLength(int len) {
            LogUtil.e(Constant.TAG, "onPinLength:" + len);
            mHandler.obtainMessage(PIN_CLICK_NUMBER, len).sendToTarget();
        }

        @Override
        public void onConfirm(int i, byte[] pinBlock) {
            addEndTime("initPinPad()");
            if (pinBlock != null) {
                String hexStr = ByteUtil.bytes2HexStr(pinBlock);
                LogUtil.e(Constant.TAG, "onConfirm pin block:" + hexStr);
                mHandler.obtainMessage(PIN_CLICK_PIN, pinBlock).sendToTarget();
            } else {
                mHandler.obtainMessage(PIN_CLICK_CONFIRM).sendToTarget();
            }
        }

        @Override
        public void onCancel() {
            addEndTime("initPinPad()");
            LogUtil.e(Constant.TAG, "onCancel");
            mHandler.obtainMessage(PIN_CLICK_CANCEL).sendToTarget();
        }

        @Override
        public void onError(int code) {
            addEndTime("initPinPad()");
            LogUtil.e(Constant.TAG, "onError:" + code);
            String msg = AidlErrorCodeV2.valueOf(code).getMsg();
            mHandler.obtainMessage(PIN_ERROR, code, code, msg).sendToTarget();
        }
    };

    /**
     * Notify emv process the Application select result
     *
     * @param selectIndex the index of selected App, start from 0
     */
    private void importAppSelect(int selectIndex) {
        LogUtil.e(Constant.TAG, "importAppSelect selectIndex:" + selectIndex);
        try {
            mEMVOptV2.importAppSelect(selectIndex);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Notify emv process the final Application select result
     *
     * @param status 0:success, other value:failed
     */
    private void importFinalAppSelectStatus(int status) {
        try {
            LogUtil.e(Constant.TAG, "importFinalAppSelectStatus status:" + status);
            mEMVOptV2.importAppFinalSelectStatus(status);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * Notify emv process the card number confirm status
     *
     * @param status 0:success, other value:failed
     */
    private void importCardNoStatus(int status) {
        LogUtil.e(Constant.TAG, "importCardNoStatus status:" + status);
        try {
            mEMVOptV2.importCardNoStatus(status);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Notify emv process the certification verify status
     *
     * @param status 0:success, other value:failed
     */
    private void importCertStatus(int status) {
        LogUtil.e(Constant.TAG, "importCertStatus status:" + status);
        try {
            mEMVOptV2.importCertStatus(status);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Notify emv process the PIN input result
     *
     * @param inputResult 0:success,1:input PIN canceled,2:input PIN skipped,3:PINPAD problem,4:input PIN timeout
     */
    private void importPinInputStatus(int inputResult) {
        LogUtil.e(Constant.TAG, "importPinInputStatus:" + inputResult);
        try {
            mEMVOptV2.importPinInputStatus(mPinType, inputResult);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Import signature result to emv process
     *
     * @param status 0:success, other value:failed
     */
    private void importSignatureStatus(int status) {
        LogUtil.e(Constant.TAG, "importSignatureStatus status:" + status);
        try {
            mEMVOptV2.importSignatureStatus(status);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Import online process result data(eg: field 55 ) to emv process.
     * if no date to import, set param tags and values as empty array
     *
     * @param status 0:online approval, 1:online denial, 2:online failed
     */
    private void importOnlineProcessStatus(int status) {
        LogUtil.e(Constant.TAG, "importOnlineProcessStatus status:" + status);
        try {
            String[] tags = {"71", "72", "91", "8A", "89"};
            String[] values = {"", "", "", "", ""};
            byte[] out = new byte[1024];
            int len = mEMVOptV2.importOnlineProcStatus(status, tags, values, out);
            if (len < 0) {
                LogUtil.e(Constant.TAG, "importOnlineProcessStatus error,code:" + len);
            } else {
                byte[] bytes = Arrays.copyOf(out, len);
                String hexStr = ByteUtil.bytes2HexStr(bytes);
                LogUtil.e(Constant.TAG, "importOnlineProcessStatus outData:" + hexStr);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Mock a POSP to do some data exchange(online process), we don't have a really POSP,
     * client should connect to a really POSP at this step.
     */
    private void mockRequestToServer() {
        new Thread(() -> {
            try {
                //showLoadingDialog(R.string.requesting);
                if (AidlConstantsV2.CardType.MAGNETIC.getValue() != mCardType) {
                    getTlvData();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            binding.textView8.setText("ٹرانزیکشن بینک کی جانب سے پروسیس میں ہے");
                            binding.textView9.setText("Tranaction is under process by bank");
                            binding.cardNumberContainer.setVisibility(View.GONE);
                            binding.status.setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.bank_v2));
                        }
                    });
                }
                Thread.sleep(1000);
                // notice  ==  import the online result to SDK and end the process.
                importOnlineProcessStatus(0);
            } catch (Exception e) {
                e.printStackTrace();
                importOnlineProcessStatus(-1);
            } finally {
                dismissLoadingDialog();
            }
        }).start();
    }

    /**
     * Read we interested tlv data
     */
    private void getTlvData() {
        try {
            String[] tagList = {
                    "DF02", "5F34", "9F06", "FF30", "FF31", "95", "9B", "9F36", "9F26",
                    "9F27", "DF31", "5A", "57", "5F24", "9F1A", "9F33", "9F35", "9F40",
                    "9F03", "9F10", "9F37", "9C", "9A", "9F02", "5F2A", "82", "9F34", "9F1E",
                    "84", "4F", "9F66", "9F6C", "9F09", "9F41", "9F63", "5F20", "9F12", "50",
            };
            byte[] outData = new byte[2048];
            Map<String, TLV> map = new TreeMap<>();
            int tlvOpCode;
            if (AidlConstantsV2.CardType.NFC.getValue() == mCardType) {
                if (mAppSelect == 2) {
                    tlvOpCode = AidlConstantsV2.EMV.TLVOpCode.OP_PAYPASS;
                } else if (mAppSelect == 1) {
                    tlvOpCode = AidlConstantsV2.EMV.TLVOpCode.OP_PAYWAVE;
                } else {
                    tlvOpCode = AidlConstantsV2.EMV.TLVOpCode.OP_NORMAL;
                }
            } else {
                tlvOpCode = AidlConstantsV2.EMV.TLVOpCode.OP_NORMAL;
            }
            int len = mEMVOptV2.getTlvList(tlvOpCode, tagList, outData);
            if (len > 0) {
                byte[] bytes = Arrays.copyOf(outData, len);
                String hexStr = ByteUtil.bytes2HexStr(bytes);
                Log.d("tlv-data", hexStr);
                Map<String, TLV> tlvMap = TLVUtil.buildTLVMap(hexStr);
                map.putAll(tlvMap);
                Log.d("AllLVData", "first"+ map);
            }

            // payPassTags
            String[] payPassTags = {
                    "DF811E", "DF812C", "DF8118", "DF8119", "DF811F", "DF8117", "DF8124",
                    "DF8125", "9F6D", "DF811B", "9F53", "DF810C", "9F1D", "DF8130", "DF812D",
                    "DF811C", "DF811D", "9F7C",
            };
            len = mEMVOptV2.getTlvList(AidlConstantsV2.EMV.TLVOpCode.OP_PAYPASS, payPassTags, outData);
            if (len > 0) {
                byte[] bytes = Arrays.copyOf(outData, len);
                String hexStr = ByteUtil.bytes2HexStr(bytes);
                Log.d("All-TLV data",hexStr);
                Map<String, TLV> tlvMap = TLVUtil.buildTLVMap(hexStr);
                map.putAll(tlvMap);
                Log.d("AllLVData", "sencond"+ map);
            }
            //geting pan from map
            TLV track2data = map.get("57");
            assert track2data != null;
            String rawTrack2data = track2data.getValue();
            String pan  = EmvUtil.getPan(rawTrack2data);
            TLV PanProccesed = new TLV("5A", 16, pan);
            //country code
            TLV countryCode = new TLV("5F2A", 02, "0586");

            //
            map.put("5A",PanProccesed);
            map.put("5F2A", countryCode);
             //
            //dismissLoadingDialog();
          /*  Gson gson = new Gson();
            String json_map = gson.toJson(map);
            Intent intent = new Intent(ICProcessActivity.this, HostDecision.class);
            intent.putExtra("map", json_map);

            intent.putExtra("pin", PinBlock);
            startActivity(intent);*/
            TLVMap  = map;
            pinBlock = PinBlock;
            if(isRfID){
                double value = Double.parseDouble(amount);
                if(value < 3000){
                    networkCall(map, PinBlock);
                }else{
                    if(pinBlock.isEmpty()){
                      runOnUiThread(new Runnable() {
                          @Override
                          public void run() {
                              showPinError();
                          }
                      });
                    }else{
                        networkCall(map, PinBlock);
                    }
                }
            }else {
                if(pinBlock.isEmpty()){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showPinError();
                        }
                    });
                }else{
                    networkCall(map, PinBlock);
                }
            }


             //
           /* final StringBuilder sb = new StringBuilder();
            Set<String> keySet = map.keySet();
            for (String key : keySet) {
                TLV tlv = map.get(key);
                sb.append(key);
                sb.append(":");
                if (tlv != null) {
                    String value = tlv.getValue();
                    sb.append(value);
                }
                sb.append("\n");
            }
            runOnUiThread(
                    () -> mTvShowInfo.setText(sb)
            );*/
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    public void networkCall(Map<String, TLV> map, String pinBlock){
        Sales builder = new Sales();
        byte[] message200 = builder.create200(map, MID, TID, Objects.requireNonNullElse(pinBlock, ""));
        Log.d("message200", ByteUtil.bytes2HexStr(message200));
        NetworkUtil util = new NetworkUtil();
        util.HostCall(message200, callback);
    }

    private void resetUI() {
        runOnUiThread(() -> {
                    mProcessStep = 0;
                    //mEditAmount.setText("");
                    //mBtnOperate.setText(R.string.ok);
                    dismissLoadingDialog();
                    dismissAppSelectDialog();
                }
        );
    }

    private void dismissAppSelectDialog() {
        runOnUiThread(() -> {
                    if (mAppSelectDialog != null) {
                        try {
                            mAppSelectDialog.dismiss();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        mAppSelectDialog = null;
                    }
                }
        );
    }

    /**
     * Create Candidate names
     */
    private String[] getCandidateNames(List<EMVCandidateV2> candiList) {
        if (candiList == null || candiList.size() == 0) return new String[0];
        String[] result = new String[candiList.size()];
        for (int i = 0; i < candiList.size(); i++) {
            EMVCandidateV2 candi = candiList.get(i);
            String name = candi.appPreName;
            name = TextUtils.isEmpty(name) ? candi.appLabel : name;
            name = TextUtils.isEmpty(name) ? candi.appName : name;
            name = TextUtils.isEmpty(name) ? "" : name;
            result[i] = name;
            LogUtil.e(Constant.TAG, "EMVCandidateV2: " + name);
        }
        return result;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cancelCheckCard();
    }

    private void cancelCheckCard() {
        try {
            mReadCardOptV2.cardOff(AidlConstantsV2.CardType.NFC.getValue());
            mReadCardOptV2.cardOff(AidlConstantsV2.CardType.IC.getValue());
            mReadCardOptV2.cancelCheckCard();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void tryAgain() {
        try {
            runOnUiThread(() -> new AlertDialog.Builder(this)
                    .setTitle("Try again")
                    .setMessage("Please read the card again")
                    .setPositiveButton("OK", (dia, which) -> {
                                dia.dismiss();
                                checkCard();
                            }
                    )
                    .show()
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /** Check and notify remove card */
    private void checkAndRemoveCard() {
        try {
            addStartTimeWithClear("getCardExistStatus()");
            int status = mReadCardOptV2.getCardExistStatus(mCardType);
            addEndTime("getCardExistStatus()");
            showSpendTime();
            if (status < 0) {
                LogUtil.e(Constant.TAG, "getCardExistStatus error, code:" + status);
                //dismissLoadingDialog();
                dismissRemoveCardDialog();
                return;
            }
            if (status == AidlConstantsV2.CardExistStatus.CARD_ABSENT) {
                if(goToSlip){
                    dismissRemoveCardDialog();
                    generateSlip();
                }else{
                    dismissRemoveCardDialog();
                }
            } else if (status == AidlConstantsV2.CardExistStatus.CARD_PRESENT) {
                //showLoadingDialog(R.string.emv_remove_card);
                showRemoveCardDialog(context);
                addStartTimeWithClear("buzzerOnDevice()");
                MyApplication.app.basicOptV2.buzzerOnDevice(1, 2750, 200, 0);
                addEndTime("buzzerOnDevice()");
                showSpendTime();
                mHandler.sendEmptyMessageDelayed(REMOVE_CARD, 1000);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCallSuccess(byte[] response) {
     Log.d("Host-Response", ByteUtil.bytes2HexStr(response));
     runOnUiThread(new Runnable() {
         @RequiresApi(api = Build.VERSION_CODES.O)
         @Override
         public void run() {
            //String resp  = ByteUtil.bytes2HexStr(response);
             //Log.d("server210", resp);
             String test = "003260000000200210203801000E8000000000000001011700050915020030303031303120202020202031323334353630303431313130303438";
             Map<Integer, Integer> fields = new HashMap<>();
             fields.put(3, 2);
             fields.put(11, 4);
             fields.put(12, 3);
             fields.put(13, 2);
             fields.put(24, 2);
             fields.put(37, 12);
             fields.put(38, 6);
             fields.put(39, 2);
             fields.put(41, 8);
             resp210 = ISOParser.parseMessage(ByteUtil.hexStr2Bytes(test), fields);
             LogUtil.d("message210", resp210.toString());
             if (Objects.equals(resp210.get("Field_39"), "3030")) {
                 binding.textView8.setText("ٹرانزیکشن مکمل ہو گئی، کارڈ ہٹا لیں۔");
                 binding.textView9.setText("Transaction completed, remove the card");
                 LogUtil.d("Transaction", "successful");
                 importOnlineProcessStatus(0);
                 createbatch();
             } else {
                 importOnlineProcessStatus(0);
                 String rawCode  = resp210.get("Field_39");
                 String code  = Utility.decodeAsciiHex(rawCode);
                 String Reason = Utility.getResponseReason(code);
                binding.containerStart.setVisibility(View.GONE);
                binding.containerDecline.setVisibility(View.VISIBLE);
                binding.tvCode.setText(code);
                binding.tvReason.setText(Reason);
                binding.retry.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //recreate();
                        Intent intent = getIntent();
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        finish();
                        overridePendingTransition(0, 0);
                        startActivity(intent);
                        overridePendingTransition(0, 0);
                    }
                });
                binding.home.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(ICProcessActivity.this, Home.class);
                        startActivity(intent);
                        finish();
                    }
                });
             }
         }
     });
    }

    @Override
    public void onError(String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                binding.containerStart.setVisibility(View.GONE);
                binding.containerDecline.setVisibility(View.VISIBLE);
                binding.tvCode.setText("97");
                binding.tvReason.setText("server not responding");
                binding.retry.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //recreate();
                        Intent intent = getIntent();
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        finish();
                        overridePendingTransition(0, 0);
                        startActivity(intent);
                        overridePendingTransition(0, 0);
                    }
                });
                binding.home.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(ICProcessActivity.this, Home.class);
                        startActivity(intent);
                        finish();
                    }
                });
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createbatch() {
        Batch batch = new Batch();
        byte[] message320 = batch.create320(TLVMap, resp210, MID, TID, pinBlock);
        Log.d("message320", ByteUtil.bytes2HexStr(message320));
        save_data(ByteUtil.bytes2HexStr(message320));
        //saving Data in Database

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void save_data(String msg_320) {
        int batch_no = SharedPrefHelper.getInstance(this).getBatch("batch");
        String batch = String.valueOf(batch_no);
        Log.d("batch", String.valueOf(batch_no));
        String entryMode = "";
        if (pinBlock.isEmpty()) {
            entryMode = IsoConfig.entryMode;
        } else {
            entryMode = IsoConfig.entryMode_pin;
        }
        String cardScheme = TLVUtil.getCardTypeByAID(TLVMap.get("4F").getValue());
        Tran = new Transaction(msg_320, MID, TID, batch,
                resp210.get("Field_38"),
                resp210.get("Field_39"),
                TLVMap.get("9F02").getValue(),
                resp210.get("Field_12"),
                resp210.get("Field_13"),
                cardScheme,
                TLVMap.get("4F").getValue(),
                TLVMap.get("5A").getValue(),
                TLVMap.get("9F41").getValue(),
                Utility.getLocalDateString(),
                Utility.getCurrentTimeHMS(),
                TLVMap.get("5F24").getValue(),
                entryMode,
                resp210.get("Field_37"),
                TLVMap.get("9F26").getValue(),
                "0",
                "0",
                0
        );
        new Thread(() -> {
            DBInstance db = DBInstance.getInstance(getApplicationContext());
            DAO transactionDao = db.userDao();
            long result = transactionDao.insert(Tran);
            goToSlip = true;
            if (result != -1) {
                runOnUiThread(() -> {
                    // This runs on the main thread
                    if (removeCardDialog == null) {
                      generateSlip();
                    }

                });
            }
        }).start();

    }

    private void generateSlip(){

                Gson gson = new Gson();
                String jsonString = gson.toJson(Tran);
                Intent intent = new Intent(ICProcessActivity.this, HostDecision.class);
                intent.putExtra("Tran", jsonString);
                intent.putExtra("amount", amount);
                intent.putExtra("cart", cartProducts);
                startActivity(intent);
                finish();

    }

    private  void showPinError(){
        binding.containerStart.setVisibility(View.GONE);
        binding.containerDecline.setVisibility(View.VISIBLE);
        binding.tvCode.setText("");
        binding.tvReason.setText("Pin is required for transaction");
        binding.retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //recreate();
                Intent intent = getIntent();
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                finish();
                overridePendingTransition(0, 0);
                startActivity(intent);
                overridePendingTransition(0, 0);
            }
        });
        binding.home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ICProcessActivity.this, Home.class);
                startActivity(intent);
                finish();
            }
        });

    }
    //reversal Call
    private void sendReversal(){

    }
}
