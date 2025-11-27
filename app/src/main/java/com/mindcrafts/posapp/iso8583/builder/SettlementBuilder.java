
package com.mindcrafts.posapp.iso8583.builder;


import com.mindcrafts.posapp.iso8583.isoUtils.IsoConfig;
import com.mindcrafts.posapp.iso8583.isoUtils.IsoUtils;
import com.mindcrafts.posapp.utils.ByteUtil;

import java.util.Map;
import java.util.TreeMap;

public class SettlementBuilder {
    byte[] TPDU;
    byte[] MTI;
    public SettlementBuilder(){}
    public byte[] create500(String p_code, String total_amount, String total_count, String Stan, String MID, String TID, String batch){
        TPDU = ByteUtil.hexStr2Bytes(IsoConfig.TPDU);
        MTI = ByteUtil.hexStr2Bytes(IsoConfig.mti_500);;
        String STAN_2 = Stan.substring(2);
        //DE62
        String paddbatch = IsoUtils.padToSix(batch);
        String AssciBatch = "0006" + IsoUtils.paddMIDTID(paddbatch);
        //
        String count  = IsoUtils.padToThree(total_count);
        String amount  = IsoUtils.padToTwelve(total_amount);
        String DE63  = count + amount + "000"+" 000000000000";
        String padded63 = "0030" + IsoUtils.paddMIDTID(DE63);
         //
        Map<Integer, byte[]> fields = new TreeMap<>();
        fields.put(3, ByteUtil.hexStr2Bytes(p_code));
        fields.put(11, ByteUtil.hexStr2Bytes(STAN_2));
        fields.put(24, ByteUtil.hexStr2Bytes(IsoConfig.NII));
        fields.put(25, ByteUtil.hexStr2Bytes(IsoConfig.POS_condtion_code));
        fields.put(41, ByteUtil.hexStr2Bytes(IsoUtils.paddMIDTID(TID)));
        fields.put(42, ByteUtil.hexStr2Bytes(IsoUtils.paddMIDTID(MID)));
        fields.put(53, ByteUtil.hexStr2Bytes(IsoConfig.security_control));
        fields.put(60, ByteUtil.hexStr2Bytes(AssciBatch));
        fields.put(63, ByteUtil.hexStr2Bytes(padded63));

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
