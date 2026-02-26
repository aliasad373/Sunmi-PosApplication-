package com.mindcrafts.posapp.iso8583.isoUtils;

import org.bouncycastle.crypto.agreement.srp.SRP6Client;

public class IsoConfig {
    public static String ip = "103.109.121.82";
    public static int Port = 5858;

    public static String TPDU = "6000330000";      //production
   //public static String TPDU ="6000200000";      // Test

   public static String NII = "0330";   // production
   //public static String NII = "0200";     //test

    public static String mti_200 = "0200";
    public static String mti_320 = "0320";
    public static String mti_400 = "0400";
    public static String mti_500 = "0500";

    public static String process_Code = "000000";

    public static  String Process_Code_void = "020000";

    public static  String entryMode = "0070";
    public static String  entryMode_pin = "0071";

    public static String POS_condtion_code = "00";

    public  static String security_control = "0003207001900000";

    public static String p_code_settlement = "920000";
    public static String p_code_settlement_second = "960000";


}
