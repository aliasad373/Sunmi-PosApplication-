package com.mindcrafts.posapp.iso8583.builder;

import com.mindcrafts.posapp.database.entity.Transaction;
import com.mindcrafts.posapp.iso8583.isoUtils.IsoConfig;
import com.mindcrafts.posapp.iso8583.isoUtils.IsoUtils;
import com.mindcrafts.posapp.iso8583.parser.ISOParser;
import com.mindcrafts.posapp.utils.ByteUtil;
import com.mindcrafts.posapp.utils.TLV;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
public class Void {
    Transaction transaction;
    byte[] DE55;
    byte[] TPDU;
    byte[] MTI;
    public Void(Transaction transaction){
        this.transaction = transaction;
    }
    public byte[] createVoid(){
        TPDU = ByteUtil.hexStr2Bytes(IsoConfig.TPDU);
        MTI = ByteUtil.hexStr2Bytes(IsoConfig.mti_200);
        String STAN = transaction.TC;
        String STAN_2 = STAN.substring(2);
        String DE60 =  IsoUtils.paddMIDTID(IsoConfig.mti_200 + transaction.TC + transaction.date + transaction.time) + transaction.RRN;
        String exp = IsoUtils.dropLastTwo(transaction.expDate);
        Map<Integer, byte[]> fields = new TreeMap<>();
        fields.put(2, IsoUtils.getPanforVoid(transaction.PAN));
        fields.put(3, ByteUtil.hexStr2Bytes(IsoConfig.process_Code));
        fields.put(4, ByteUtil.hexStr2Bytes(transaction.Amount));
        fields.put(11, ByteUtil.hexStr2Bytes(STAN_2));
        fields.put(12, ByteUtil.hexStr2Bytes(transaction.TransactionTime));
        fields.put(13, ByteUtil.hexStr2Bytes(transaction.TransactionDate));
        fields.put(14, ByteUtil.hexStr2Bytes(exp));
        fields.put(22,ByteUtil.hexStr2Bytes(transaction.entrymode));
        fields.put(24, ByteUtil.hexStr2Bytes(IsoConfig.NII));
        fields.put(25, ByteUtil.hexStr2Bytes(IsoConfig.POS_condtion_code));
        fields.put(37, ByteUtil.hexStr2Bytes(transaction.RRN));
        fields.put(41, ByteUtil.hexStr2Bytes(IsoUtils.paddMIDTID(transaction.TID)));
        fields.put(42, ByteUtil.hexStr2Bytes(IsoUtils.paddMIDTID(transaction.MID)));
        fields.put(60, ByteUtil.hexStr2Bytes(DE60));
        byte [] message = IsoUtils.buildIsoMessage(TPDU, MTI, fields);
        int size = message.length;
        String hexLength = String.format("%04X", size); // "009E"
        byte[] lengthBytes = ByteUtil.hexStr2Bytes(hexLength);
        //
        byte[] combined = new byte[lengthBytes.length + message.length];
        System.arraycopy(lengthBytes, 0, combined, 0, lengthBytes.length);
        System.arraycopy(message, 0, combined, lengthBytes.length, message.length);

        return combined;
    }
}
