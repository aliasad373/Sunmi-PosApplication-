package com.mindcrafts.posapp.iso8583.builder;

import android.util.Log;

import com.mindcrafts.posapp.iso8583.isoUtils.IsoConfig;
import com.mindcrafts.posapp.iso8583.isoUtils.IsoUtils;
import com.mindcrafts.posapp.utils.ByteUtil;
import com.mindcrafts.posapp.utils.TLV;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class Sales {
    List<String> selected = Arrays.asList("5F2A", "5F34", "82", "84", "95", "9A", "9C", "9F09", "9F02", "9F03", "9F10", "9F1A", "9F1E", "9F26", "9F27", "9F33", "9F35", "9F36", "9F37", "4F", "9F41");
    byte[] DE55;
    byte[] TPDU;
    byte[] MTI;
    public Sales(){}
    public byte[] create200(Map<String, TLV> map, String MID, String TID, String pinblock){
        DE55  = IsoUtils.DE55Builder(map, selected);
        TPDU = ByteUtil.hexStr2Bytes(IsoConfig.TPDU);
        MTI = ByteUtil.hexStr2Bytes(IsoConfig.mti_200);
        String STAN = map.get("9F41").getValue();
        String STAN_2 = STAN.substring(2);
        byte[] DE62 = IsoUtils.DE62(map);


        Map<Integer, byte[]> fields = new TreeMap<>();
        fields.put(2, IsoUtils.getPAN(map));
        fields.put(3, ByteUtil.hexStr2Bytes(IsoConfig.process_Code));
        fields.put(4, ByteUtil.hexStr2Bytes(map.get("9F02").getValue()));
        fields.put(11, ByteUtil.hexStr2Bytes(STAN_2));
        if(pinblock.isEmpty()){
            fields.put(22,ByteUtil.hexStr2Bytes(IsoConfig.entryMode));
        }else{
            fields.put(22, ByteUtil.hexStr2Bytes(IsoConfig.entryMode_pin));
        }
        fields.put(24, ByteUtil.hexStr2Bytes(IsoConfig.NII));
        fields.put(25, ByteUtil.hexStr2Bytes(IsoConfig.POS_condtion_code));
        fields.put(35, IsoUtils.track2Data(map));
        fields.put(41, ByteUtil.hexStr2Bytes(IsoUtils.paddMIDTID(TID)));
        fields.put(42, ByteUtil.hexStr2Bytes(IsoUtils.paddMIDTID(MID)));

        if(!pinblock.isEmpty()){ // without pin no DE  52
            fields.put(52, ByteUtil.hexStr2Bytes(pinblock));
        }
       fields.put(53, ByteUtil.hexStr2Bytes(IsoConfig.security_control));
       fields.put(55, DE55);
       fields.put(62, DE62);
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
