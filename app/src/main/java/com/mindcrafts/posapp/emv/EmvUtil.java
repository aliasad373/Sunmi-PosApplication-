package com.mindcrafts.posapp.emv;

import static com.sunmi.pay.hardware.aidl.AidlConstants.Security.KEY_ALG_TYPE_3DES;

import com.mindcrafts.posapp.utils.Constant;
import com.mindcrafts.posapp.app.MyApplication;
import com.mindcrafts.posapp.utils.ByteUtil;
import com.mindcrafts.posapp.utils.LogUtil;
import com.mindcrafts.posapp.utils.TLV;
import com.mindcrafts.posapp.utils.TLVUtil;
import com.sunmi.pay.hardware.aidlv2.AidlConstantsV2;
import com.sunmi.pay.hardware.aidlv2.bean.AidV2;
import com.sunmi.pay.hardware.aidlv2.bean.CapkV2;
import com.sunmi.pay.hardware.aidlv2.bean.EmvTermParamV2;
import com.sunmi.pay.hardware.aidlv2.emv.EMVOptV2;
import com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2;

import java.util.HashMap;
import java.util.Map;

public final class EmvUtil {
    public static final String COUNTRY_PAKISTAN = "Pakistan";
    public static final String COUNTRY_CHINA = "China";
    public static final String COUNTRY_RUSSIA = "Russia";

    public static final String COUNTRY_INDIA = "India";



    private EmvUtil() {
    }

    /**
     * Config country code,capability,transaction currency code,transaction currency code exponent
     * <br/> Client should config their own data according their country.
     */
    public static Map<String, String> getConfig(String country) {
        Map<String, String> map = new HashMap<>();
        switch (country) {
            case COUNTRY_RUSSIA:
                map.put("countryCode", "0643");
                map.put("capability", "E0F8C8");
                map.put("5F2A", "0643");
                map.put("5F36", "00");
                break;
            case COUNTRY_INDIA:
                map.put("countryCode", "0356");
                map.put("capability", "E0F8C8");
                map.put("5F2A", "0356");
                map.put("5F36", "02");
                break;
            case COUNTRY_PAKISTAN:
                map.put("countryCode", "0586");
                map.put("capability", "E0F8C8");
                map.put("5F2A", "0586");
                map.put("5F36", "02");
                //map.put("")
                break;
            case COUNTRY_CHINA:
            default:
                map.put("countryCode", "0586");     // country code(国家代码)
                map.put("capability", "E0F8C8");    // capability(终端性能)
                map.put("5F2A", "0586");            // transaction currency code(交易货币代码)
                map.put("5F36", "02");              // transaction currency code exponent(交易货币代码指数)
        }
        return map;
    }

    /**
     * Initialize keys.
     * <br/>Client App should save their own keys.
     */
    public static void initKey() {
        try {
            SecurityOptV2 mSecurityOptV2 = MyApplication.app.securityOptV2;



            byte[] cvByte = ByteUtil.hexStr2Bytes("0");
            // byte[] dataByte = ByteUtil.hexStr2Bytes("11111111111111111111111111111111");
            int result;

            /*
            // save KEK
            int result = mSecurityOptV2.savePlaintextKey(AidlConstantsV2.Security.KEY_TYPE_KEK, dataByte, cvByte, AidlConstantsV2.Security.KEY_ALG_TYPE_3DES, 10);
            LogUtil.e(Constant.TAG, "save KEK result:" + result);
            if (result == 0) {
                LogUtil.e("KEY", "kkk PASS");
            }
            else {
                LogUtil.e("KEY", "kkk FAIL");
            }
*/
            // result = mSecurityOptV2.saveTerminalKey();
            // save TMK
            byte[] dataByte = ByteUtil.hexStr2Bytes("2979107FA4A740B96E04F7BA97D5A14F");
            //result = mSecurityOptV2.saveCiphertextKey(AidlConstantsV2.Security.KEY_TYPE_TMK, dataByte, cvByte, 10, AidlConstantsV2.Security.KEY_ALG_TYPE_3DES, 11);
            result = mSecurityOptV2.savePlaintextKey(AidlConstantsV2.Security.KEY_TYPE_TMK, dataByte, cvByte, KEY_ALG_TYPE_3DES, 10);
            LogUtil.e(Constant.TAG, "save TMK result:" + result);
            if (result == 0) {
                LogUtil.e("KEY", "TMK PASS");
            } else {
                LogUtil.e("KEY", "KEK FAIL");
            }
            byte[] dataByte1 = ByteUtil.hexStr2Bytes("39D3E1CC481A87A3985699F55018B72E");
            // save PIK
            result = mSecurityOptV2.saveCiphertextKey(AidlConstantsV2.Security.KEY_TYPE_PIK, dataByte1, cvByte, 10,KEY_ALG_TYPE_3DES, 11);
            LogUtil.e(Constant.TAG, "save PIK result:" + result);
            if (result == 0) {
                LogUtil.e("KEY", "TPK PASS");
            } else {
                LogUtil.e("KEY", "TPK FAIL");
            }
/*
            // save MAK
            result = mSecurityOptV2.saveCiphertextKey(AidlConstantsV2.Security.KEY_TYPE_MAK, dataByte1, cvByte, 11, AidlConstantsV2.Security.KEY_ALG_TYPE_3DES, 13);
            LogUtil.e(Constant.TAG, "save MAK result:" + result);
            if (result == 0) {
                LogUtil.e("KEY", "pik PASS");
            }
            else {
                LogUtil.e("KEY", "pik FAIL");
            }

            // save TDK
            result = mSecurityOptV2.saveCiphertextKey(AidlConstantsV2.Security.KEY_TYPE_TDK, dataByte1, cvByte, 11, AidlConstantsV2.Security.KEY_ALG_TYPE_3DES, 14);
            LogUtil.e(Constant.TAG, "save TDK result:" + result);
            if (result == 0) {
                LogUtil.e("KEY", "MAK PASS");
            }
            else {
                LogUtil.e("KEY", "MAK FAIL");
            }
*/
            LogUtil.e("KEY", "Key Success");
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.e("KEY", "Key Fail");
        }

    }

    /**
     * Initialize AIDs and RIDs.
     * <br/>Client App should save their own AIDs and RIDs
     * <br/> Note: The AIDs and RIDs shown in this method may not
     * useful for your App, please save your own AIDs and RIDs at
     * this step.
     * <br/> SDK have several built-in AIDs and Capks, if dont want them,
     * call EMVOptV2.deleteAid(null) or EMVOptV2.deleteCapk(null,null)
     * firstly.
     */
    public static void initAidAndRid() {
        try {
            EMVOptV2 emvOptV2 = MyApplication.app.emvOptV2;
//            emvOptV2.deleteAid(null);//clear SDK built-in AIDs
            // clear SDK built-in AIDs and CAPKs
            emvOptV2.deleteAid(null);
            emvOptV2.deleteCapk(null, null);
            CapkV2 capkV2 = null;


            //UnionPay Aid Contact and contactless
            AidV2 aid = EmvUtil.hexStr2Aid("9F0608A000000333010101DF0101009F08020030DF1105D84000A800DF1205DC4004F800DF130500000000009F1B0400000000DF150400000000DF160199DF170199DF14039F3704DF1801019F7B06100000000000DF1906000000000000DF2006000000200000DF21060000000300005F2A0205869F660436004080");
            emvOptV2.addAid(aid);

            aid = EmvUtil.hexStr2Aid("9F0608A000000333010102DF0101009F08020030DF1105D84000A800DF1205DC4004F800DF130500000000009F1B0400000000DF150400000000DF160199DF170199DF14039F3704DF1801019F7B06100000000000DF1906000000000000DF2006000000200000DF21060000000300005F2A0205869F660436004080");
            emvOptV2.addAid(aid);

            aid = EmvUtil.hexStr2Aid("9F0608A000000333010103DF0101009F08020030DF1105D84000A800DF1205DC4004F800DF130500000000009F1B0400000000DF150400000000DF160199DF170199DF14039F3704DF1801019F7B06100000000000DF1906000000000000DF2006000000200000DF21060000000300005F2A0205869F660436004080");
            emvOptV2.addAid(aid);


            aid = EmvUtil.hexStr2Aid("9F0608A000000333010106DF0101009F08020030DF1105D84000A800DF1205DC4004F800DF130500000000009F1B0400000000DF150400000000DF160199DF170199DF14039F3704DF1801019F7B06100000000000DF1906000000000000DF2006000000200000DF21060000000300005F2A0205869F660436004080");
            emvOptV2.addAid(aid);


            aid = EmvUtil.hexStr2Aid("9F0608A000000333010108DF0101009F08020030DF1105D84000A800DF1205DC4004F800DF130500000000009F1B0400000000DF150400000000DF160199DF170199DF14039F3704DF1801019F7B06100000000000DF1906000000000000DF2006000000200000DF21060000000300005F2A0205869F660436004080");
            emvOptV2.addAid(aid);




            //pure

            aid = EmvUtil.hexStr2Aid("9F0607A0000007361010DF010100DFC10A01099F08020001DF1105BC78BCA800DF1205BC78BCF800DF130500000000009F1B040000C350DF150400000000DF160199DF170199DF14039F3704DF1801019F7B06000000000000DF1906000000000000DF2006000000200000DF21060000000300005F2A020586");
            emvOptV2.addAid(aid);
            aid = EmvUtil.hexStr2Aid("9F0607A0000007362010DF010100DFC10A01099F08020001DF1105BC78BCA800DF1205BC78BCF800DF130500000000009F1B040000C350DF150400000000DF160199DF170199DF14039F3704DF1801019F7B06000000000000DF1906000000000000DF2006000000200000DF21060000000300005F2A020586");
            emvOptV2.addAid(aid);
            aid = EmvUtil.hexStr2Aid("9F0607A0000007363010DF010100DFC10A01099F08020001DF1105BC78BCA800DF1205BC78BCF800DF130500000000009F1B040000C350DF150400000000DF160199DF170199DF14039F3704DF1801019F7B06000000000000DF1906000000000000DF2006000000200000DF21060000000300005F2A020586");
            emvOptV2.addAid(aid);




            //Master AID
            aid = EmvUtil.hexStr2Aid("9F0607A0000000041010DF0101009F09020002DF1105FE50BCA000DF1205FE50BCF800DF130500000000009F1B0400000000DF150400000000DF160100DF170100DF180101DF2106000000010000DF2006000000100000DF1906000000000010DFC10B0100");
            emvOptV2.addAid(aid);


            //Visa Contactless and contact Aid


            aid = EmvUtil.hexStr2Aid("9F0607A0000000031010DF13050010000000DF1105D84000A800DF1205D84004F800DF010101DF14039F37049F1B04000000005F3601029F3C020586DF8101020586DF8102039F37045F2A0205869F01051234567891DF1906000000015000DF2006000002000000DF21060000000300009F660436204000DFC1080101");
            emvOptV2.addAid(aid);

            aid = EmvUtil.hexStr2Aid("9F0608A000000003101005DF010101DF14039F37049F1B04000000009F160F3030303030303030303030303030305F3601029F3C020586DF8101020586DF8102039F37045F2A0205869F01051234567891DF1906000000015000DF2006000002000000DF21060000000300009F660436204000DFC1080101");
            emvOptV2.addAid(aid);

            aid = EmvUtil.hexStr2Aid("9F0608A000000003101003DF010101DF14039F37049F1B04000000009F160F3030303030303030303030303030305F3601029F3C020586DF8101020586DF8102039F37045F2A0205869F01051234567891DF1906000000015000DF2006000002000000DF21060000000300009F660436204000DFC1080101");
            emvOptV2.addAid(aid);

            aid = EmvUtil.hexStr2Aid("9F0608A000000003101002DF010101DF14039F37049F1B04000000009F160F3030303030303030303030303030305F3601029F3C020586DF8101020586DF8102039F37045F2A0205869F01051234567891DF1906000000015000DF2006000002000000DF21060000000300009F660436204000DFC1080101");
            emvOptV2.addAid(aid);

            aid = EmvUtil.hexStr2Aid("9F0608A000000003101001DF010101DF14039F37049F1B04000000009F160F3030303030303030303030303030305F3601029F3C020586DF8101020586DF8102039F37045F2A0205869F01051234567891DF1906000000015000DF2006000002000000DF21060000000300009F660436204000DFC1080101");
            emvOptV2.addAid(aid);

            aid = EmvUtil.hexStr2Aid("9F0605A000000003DF010101DF14039F37049F1B04000000009F160F3030303030303030303030303030305F3601029F3C020586DF8101020586DF8102039F37045F2A0205869F01051234567891DF1906000000015000DF2006000002000000DF21060000000300009F660436204000DFC1080101");
            emvOptV2.addAid(aid);

            //Unionpay
            //0A
            capkV2 = EmvUtil.hexStr2Rid("9F0605A0000003339F22010ADF0503202001DF060101DF070101DF028180B2AB1B6E9AC55A75ADFD5BBC34490E53C4C3381F34E60E7FAC21CC2B26DD34462B64A6FAE2495ED1DD383B8138BEA100FF9B7A111817E7B9869A9742B19E5C9DAC56F8B8827F11B05A08ECCF9E8D5E85B0F7CFA644EFF3E9B796688F38E006DEB21E101C01028903A06023AC5AAB8635F8E307A53AC742BDCE6A283F585F48EFDF040103DF0314C88BE6B2417C4F941C9371EA35A377158767E4E3");
            emvOptV2.addCapk(capkV2);

            //08
            capkV2 = EmvUtil.hexStr2Rid("9F0605A0000003339F220108DF0503202001DF060101DF070101DF028190B61645EDFD5498FB246444037A0FA18C0F101EBD8EFA54573CE6E6A7FBF63ED21D66340852B0211CF5EEF6A1CD989F66AF21A8EB19DBD8DBC3706D135363A0D683D046304F5A836BC1BC632821AFE7A2F75DA3C50AC74C545A754562204137169663CFCC0B06E67E2109EBA41BC67FF20CC8AC80D7B6EE1A95465B3B2657533EA56D92D539E5064360EA4850FED2D1BFDF040103DF0314EE23B616C95C02652AD18860E48787C079E8E85A");
            emvOptV2.addCapk(capkV2);


            //09
            capkV2 = EmvUtil.hexStr2Rid("9F0605A0000003339F220109DF0503202001DF060101DF070101DF0281B0EB374DFC5A96B71D2863875EDA2EAFB96B1B439D3ECE0B1826A2672EEEFA7990286776F8BD989A15141A75C384DFC14FEF9243AAB32707659BE9E4797A247C2F0B6D99372F384AF62FE23BC54BCDC57A9ACD1D5585C303F201EF4E8B806AFB809DB1A3DB1CD112AC884F164A67B99C7D6E5A8A6DF1D3CAE6D7ED3D5BE725B2DE4ADE23FA679BF4EB15A93D8A6E29C7FFA1A70DE2E54F593D908A3BF9EBBD760BBFDC8DB8B54497E6C5BE0E4A4DAC29E5DF040103DF0314A075306EAB0045BAF72CDD33B3B678779DE1F527");
            emvOptV2.addCapk(capkV2);


            //0B
            capkV2 = EmvUtil.hexStr2Rid("9F0605A0000003339F22010BDF0503202001DF060101DF070101DF0281F8CF9FDF46B356378E9AF311B0F981B21A1F22F250FB11F55C958709E3C7241918293483289EAE688A094C02C344E2999F315A72841F489E24B1BA0056CFAB3B479D0E826452375DCDBB67E97EC2AA66F4601D774FEAEF775ACCC621BFEB65FB0053FC5F392AA5E1D4C41A4DE9FFDFDF1327C4BB874F1F63A599EE3902FE95E729FD78D4234DC7E6CF1ABABAA3F6DB29B7F05D1D901D2E76A606A8CBFFFFECBD918FA2D278BDB43B0434F5D45134BE1C2781D157D501FF43E5F1C470967CD57CE53B64D82974C8275937C5D8502A1252A8A5D6088A259B694F98648D9AF2CB0EFD9D943C69F896D49FA39702162ACB5AF29B90BADE005BC157DF040103DF0314BD331F9996A490B33C13441066A09AD3FEB5F66C");
            emvOptV2.addCapk(capkV2);


            //04
            capkV2 = EmvUtil.hexStr2Rid("9F0605A0000003339F220104DF0503202001DF060101DF070101DF0281F8BC853E6B5365E89E7EE9317C94B02D0ABB0DBD91C05A224A2554AA29ED9FCB9D86EB9CCBB322A57811F86188AAC7351C72BD9EF196C5A01ACEF7A4EB0D2AD63D9E6AC2E7836547CB1595C68BCBAFD0F6728760F3A7CA7B97301B7E0220184EFC4F653008D93CE098C0D93B45201096D1ADFF4CF1F9FC02AF759DA27CD6DFD6D789B099F16F378B6100334E63F3D35F3251A5EC78693731F5233519CDB380F5AB8C0F02728E91D469ABD0EAE0D93B1CC66CE127B29C7D77441A49D09FCA5D6D9762FC74C31BB506C8BAE3C79AD6C2578775B95956B5370D1D0519E37906B384736233251E8F09AD79DFBE2C6ABFADAC8E4D8624318C27DAF1DF040103DF0314F527081CF371DD7E1FD4FA414A665036E0F5E6E5");
            emvOptV2.addCapk(capkV2);


            //03
            capkV2 = EmvUtil.hexStr2Rid("9F0605A0000003339F220103DF0503202001DF060101DF070101DF0281B0B0627DEE87864F9C18C13B9A1F025448BF13C58380C91F4CEBA9F9BCB214FF8414E9B59D6ABA10F941C7331768F47B2127907D857FA39AAF8CE02045DD01619D689EE731C551159BE7EB2D51A372FF56B556E5CB2FDE36E23073A44CA215D6C26CA68847B388E39520E0026E62294B557D6470440CA0AEFC9438C923AEC9B2098D6D3A1AF5E8B1DE36F4B53040109D89B77CAFAF70C26C601ABDF59EEC0FDC8A99089140CD2E817E335175B03B7AA33DDF040103DF031487F0CD7C0E86F38F89A66F8C47071A8B88586F26");
            emvOptV2.addCapk(capkV2);

            //Visa Capkeys
            //95
            capkV2 = EmvUtil.hexStr2Rid("9F0605A0000000039F220195DF0503202001DF060101DF070101DF028190BE9E1FA5E9A803852999C4AB432DB28600DCD9DAB76DFAAA47355A0FE37B1508AC6BF38860D3C6C2E5B12A3CAAF2A7005A7241EBAA7771112C74CF9A0634652FBCA0E5980C54A64761EA101A114E0F0B5572ADD57D010B7C9C887E104CA4EE1272DA66D997B9A90B5A6D624AB6C57E73C8F919000EB5F684898EF8C3DBEFB330C62660BED88EA78E909AFF05F6DA627BDF040103DF0314EE1511CEC71020A9B90443B37B1D5F6E703030F6");
            emvOptV2.addCapk(capkV2);

            //92
            capkV2 = EmvUtil.hexStr2Rid("9F0605A0000000039F220192DF0503202001DF060101DF070101DF0281B0996AF56F569187D09293C14810450ED8EE3357397B18A2458EFAA92DA3B6DF6514EC060195318FD43BE9B8F0CC669E3F844057CBDDF8BDA191BB64473BC8DC9A730DB8F6B4EDE3924186FFD9B8C7735789C23A36BA0B8AF65372EB57EA5D89E7D14E9C7B6B557460F10885DA16AC923F15AF3758F0F03EBD3C5C2C949CBA306DB44E6A2C076C5F67E281D7EF56785DC4D75945E491F01918800A9E2DC66F60080566CE0DAF8D17EAD46AD8E30A247C9FDF040103DF0314429C954A3859CEF91295F663C963E582ED6EB253");
            emvOptV2.addCapk(capkV2);

            //94
            capkV2 = EmvUtil.hexStr2Rid("9F0605A0000000039F220194DF0503202001DF060101DF070101DF0281F8ACD2B12302EE644F3F835ABD1FC7A6F62CCE48FFEC622AA8EF062BEF6FB8BA8BC68BBF6AB5870EED579BC3973E121303D34841A796D6DCBC41DBF9E52C4609795C0CCF7EE86FA1D5CB041071ED2C51D2202F63F1156C58A92D38BC60BDF424E1776E2BC9648078A03B36FB554375FC53D57C73F5160EA59F3AFC5398EC7B67758D65C9BFF7828B6B82D4BE124A416AB7301914311EA462C19F771F31B3B57336000DFF732D3B83DE07052D730354D297BEC72871DCCF0E193F171ABA27EE464C6A97690943D59BDABB2A27EB71CEEBDAFA1176046478FD62FEC452D5CA393296530AA3F41927ADFE434A2DF2AE3054F8840657A26E0FC617DF040103DF0314C4A3C43CCF87327D136B804160E47D43B60E6E0F");
            emvOptV2.addCapk(capkV2);

            //08
            capkV2 = EmvUtil.hexStr2Rid("9F0605A0000000039F220108DF0503202001DF060101DF070101DF0281B0D9FD6ED75D51D0E30664BD157023EAA1FFA871E4DA65672B863D255E81E137A51DE4F72BCC9E44ACE12127F87E263D3AF9DD9CF35CA4A7B01E907000BA85D24954C2FCA3074825DDD4C0C8F186CB020F683E02F2DEAD3969133F06F7845166ACEB57CA0FC2603445469811D293BFEFBAFAB57631B3DD91E796BF850A25012F1AE38F05AA5C4D6D03B1DC2E568612785938BBC9B3CD3A910C1DA55A5A9218ACE0F7A21287752682F15832A678D6E1ED0BDF040103DF031420D213126955DE205ADC2FD2822BD22DE21CF9A8");
            emvOptV2.addCapk(capkV2);

            //09
            capkV2 = EmvUtil.hexStr2Rid("9F0605A0000000049F220109DF0503202001DF060101DF070101DF0281F89D912248DE0A4E39C1A7DDE3F6D2588992C1A4095AFBD1824D1BA74847F2BC4926D2EFD904B4B54954CD189A54C5D1179654F8F9B0D2AB5F0357EB642FEDA95D3912C6576945FAB897E7062CAA44A4AA06B8FE6E3DBA18AF6AE3738E30429EE9BE03427C9D64F695FA8CAB4BFE376853EA34AD1D76BFCAD15908C077FFE6DC5521ECEF5D278A96E26F57359FFAEDA19434B937F1AD999DC5C41EB11935B44C18100E857F431A4A5A6BB65114F174C2D7B59FDF237D6BB1DD0916E644D709DED56481477C75D95CDD68254615F7740EC07F330AC5D67BCD75BF23D28A140826C026DBDE971A37CD3EF9B8DF644AC385010501EFC6509D7A41DF040103DF03141FF80A40173F52D7D27E0F26A146A1C8CCB29046");
            emvOptV2.addCapk(capkV2);

            // MASTER CAPKEYS


            capkV2 = EmvUtil.hexStr2Rid("9F0605A0000000049F2201FADF0503202001DF060101DF070101DF028190A90FCD55AA2D5D9963E35ED0F440177699832F49C6BAB15CDAE5794BE93F934D4462D5D12762E48C38BA83D8445DEAA74195A301A102B2F114EADA0D180EE5E7A5C73E0C4E11F67A43DDAB5D55683B1474CC0627F44B8D3088A492FFAADAD4F42422D0E7013536C3C49AD3D0FAE96459B0F6B1B6056538A3D6D44640F94467B108867DEC40FAAECD740C00E2B7A8852DDF040103DF03145BED4068D96EA16D2D77E03D6036FC7A160EA99C");
            emvOptV2.addCapk(capkV2);
            //F1
            capkV2 = EmvUtil.hexStr2Rid("9F0605A0000000049F2201F1DF0503202001DF060101DF070101DF0281b0A0DCF4BDE19C3546B4B6F0414D174DDE294AABBB828C5A834D73AAE27C99B0B053A90278007239B6459FF0BBCD7B4B9C6C50AC02CE91368DA1BD21AAEADBC65347337D89B68F5C99A09D05BE02DD1F8C5BA20E2F13FB2A27C41D3F85CAD5CF6668E75851EC66EDBF98851FD4E42C44C1D59F5984703B27D5B9F21B8FA0D93279FBBF69E090642909C9EA27F898959541AA6757F5F624104F6E1D3A9532F2A6E51515AEAD1B43B3D7835088A2FAFA7BE7DF040103DF0314D8E68DA167AB5A85D8C3D55ECB9B0517A1A5B4BB");
            emvOptV2.addCapk(capkV2);
            //EF
            capkV2 = EmvUtil.hexStr2Rid("9F0605A0000000049F2201EFDF0503202001DF060101DF070101DF0281f8A191CB87473F29349B5D60A88B3EAEE0973AA6F1A082F358D849FDDFF9C091F899EDA9792CAF09EF28F5D22404B88A2293EEBBC1949C43BEA4D60CFD879A1539544E09E0F09F60F065B2BF2A13ECC705F3D468B9D33AE77AD9D3F19CA40F23DCF5EB7C04DC8F69EBA565B1EBCB4686CD274785530FF6F6E9EE43AA43FDB02CE00DAEC15C7B8FD6A9B394BABA419D3F6DC85E16569BE8E76989688EFEA2DF22FF7D35C043338DEAA982A02B866DE5328519EBBCD6F03CDD686673847F84DB651AB86C28CF1462562C577B853564A290C8556D818531268D25CC98A4CC6A0BDFFFDA2DCCA3A94C998559E307FDDF915006D9A987B07DDAEB3BDF040103DF031421766EBB0EE122AFB65D7845B73DB46BAB65427A");
            emvOptV2.addCapk(capkV2);
            //06
            capkV2 = EmvUtil.hexStr2Rid("9F0605A0000000049F220106DF0503231231DF060101DF070101DF0281F8CB26FC830B43785B2BCE37C81ED334622F9622F4C89AAE641046B2353433883F307FB7C974162DA72F7A4EC75D9D657336865B8D3023D3D645667625C9A07A6B7A137CF0C64198AE38FC238006FB2603F41F4F3BB9DA1347270F2F5D8C606E420958C5F7D50A71DE30142F70DE468889B5E3A08695B938A50FC980393A9CBCE44AD2D64F630BB33AD3F5F5FD495D31F37818C1D94071342E07F1BEC2194F6035BA5DED3936500EB82DFDA6E8AFB655B1EF3D0D7EBF86B66DD9F29F6B1D324FE8B26CE38AB2013DD13F611E7A594D675C4432350EA244CC34F3873CBA06592987A1D7E852ADC22EF5A2EE28132031E48F74037E3B34AB747FDF040103DF0314F910A1504D5FFB793D94F3B500765E1ABCAD72D9");
            emvOptV2.addCapk(capkV2);

            //01
            capkV2 = EmvUtil.hexStr2Rid("9F0605A0000000049F220101DF0503231231DF060101DF070101DF028160D2010716C9FB5264D8C91A14F4F32F8981EE954F20087ED77CDC5868431728D3637C632CCF2718A4F5D92EA8AB166AB992D2DE24E9FBDC7CAB9729401E91C502D72B39F6866F5C098B1243B132AFEE65F5036E168323116338F8040834B98725DF040103DF0314EA950DD4234FEB7C900C0BE817F64DE66EEEF7C4");
            emvOptV2.addCapk(capkV2);

            //05
            capkV2 = EmvUtil.hexStr2Rid("9F0605A0000000049F220105DF0503231231DF060101DF070101DF0281B0B8048ABC30C90D976336543E3FD7091C8FE4800DF820ED55E7E94813ED00555B573FECA3D84AF6131A651D66CFF4284FB13B635EDD0EE40176D8BF04B7FD1C7BACF9AC7327DFAA8AA72D10DB3B8E70B2DDD811CB4196525EA386ACC33C0D9D4575916469C4E4F53E8E1C912CC618CB22DDE7C3568E90022E6BBA770202E4522A2DD623D180E215BD1D1507FE3DC90CA310D27B3EFCCD8F83DE3052CAD1E48938C68D095AAC91B5F37E28BB49EC7ED597DF040103DF0314EBFA0D5D06D8CE702DA3EAE890701D45E274C845");
            emvOptV2.addCapk(capkV2);

            //F5
            capkV2 = EmvUtil.hexStr2Rid("9F0605A0000000049F2201F5DF0503231231DF060101DF070101DF0281F8A6E6FB72179506F860CCCA8C27F99CECD94C7D4F3191D303BBEE37481C7AA15F233BA755E9E4376345A9A67E7994BDC1C680BB3522D8C93EB0CCC91AD31AD450DA30D337662D19AC03E2B4EF5F6EC18282D491E19767D7B24542DFDEFF6F62185503532069BBB369E3BB9FB19AC6F1C30B97D249EEE764E0BAC97F25C873D973953E5153A42064BBFABFD06A4BB486860BF6637406C9FC36813A4A75F75C31CCA9F69F8DE59ADECEF6BDE7E07800FCBE035D3176AF8473E23E9AA3DFEE221196D1148302677C720CFE2544A03DB553E7F1B8427BA1CC72B0F29B12DFEF4C081D076D353E71880AADFF386352AF0AB7B28ED49E1E672D11F9DF040103DF0314C2239804C8098170BE52D6D5D4159E81CE8466BF");
            emvOptV2.addCapk(capkV2);

            //F3
            capkV2 = EmvUtil.hexStr2Rid("9F0605A0000000049F2201F3DF0503231231DF060101DF070101DF02819098F0C770F23864C2E766DF02D1E833DFF4FFE92D696E1642F0A88C5694C6479D16DB1537BFE29E4FDC6E6E8AFD1B0EB7EA0124723C333179BF19E93F10658B2F776E829E87DAEDA9C94A8B3382199A350C077977C97AFF08FD11310AC950A72C3CA5002EF513FCCC286E646E3C5387535D509514B3B326E1234F9CB48C36DDD44B416D23654034A66F403BA511C5EFA3DF040103DF0314A69AC7603DAF566E972DEDC2CB433E07E8B01A9A");
            emvOptV2.addCapk(capkV2);

            //F9
            capkV2 = EmvUtil.hexStr2Rid("9F0605A0000000049F2201F9DF0503231231DF060101DF070101DF0281C0A99A6D3E071889ED9E3A0C391C69B0B804FC160B2B4BDD570C92DD5A0F45F53E8621F7C96C40224266735E1EE1B3C06238AE35046320FD8E81F8CEB3F8B4C97B940930A3AC5E790086DAD41A6A4F5117BA1CE2438A51AC053EB002AED866D2C458FD73359021A12029A0C043045C11664FE0219EC63C10BF2155BB2784609A106421D45163799738C1C30909BB6C6FE52BBB76397B9740CE064A613FF8411185F08842A423EAD20EDFFBFF1CD6C3FE0C9821479199C26D8572CC8AFFF087A9C3DF040103DF0314336712DCC28554809C6AA9B02358DE6F755164DB");
            emvOptV2.addCapk(capkV2);

            //FA
            capkV2 = EmvUtil.hexStr2Rid("9F0605A0000000049F2201FADF0503231231DF060101DF070101DF0281A09C6BE5ADB10B4BE3DCE2099B4B210672B89656EBA091204F613ECC623BEDC9C6D77B660E8BAEEA7F7CE30F1B153879A4E36459343D1FE47ACDBD41FCD710030C2BA1D9461597982C6E1BDD08554B726F5EFF7913CE59E79E357295C321E26D0B8BE270A9442345C753E2AA2ACFC9D30850602FE6CAC00C6DDF6B8D9D9B4879B2826B042A07F0E5AE526A3D3C4D22C72B9EAA52EED8893866F866387AC05A1399DF040103DF03140ABCADAD2C7558CA9C7081AE55DDDC714F8D45F8");
            emvOptV2.addCapk(capkV2);

            //FE
            capkV2 = EmvUtil.hexStr2Rid("9F0605A0000000049F2201FEDF0503231231DF060101DF070101DF028180A653EAC1C0F786C8724F737F172997D63D1C3251C44402049B865BAE877D0F398CBFBE8A6035E24AFA086BEFDE9351E54B95708EE672F0968BCD50DCE40F783322B2ABA04EF137EF18ABF03C7DBC5813AEAEF3AA7797BA15DF7D5BA1CBAF7FD520B5A482D8D3FEE105077871113E23A49AF3926554A70FE10ED728CF793B62A1DF040103DF03149A295B05FB390EF7923F57618A9FDA2941FC34E0");
            emvOptV2.addCapk(capkV2);

            LogUtil.e(Constant.TAG, "init AID and CAPKs success5");
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.e(Constant.TAG, "initAIDAndRid fail");
        }
    }

    public static void initAIDs(){
        try{
            EMVOptV2 emvOptV2 = MyApplication.app.emvOptV2;
            //emvOptV2.deleteAid(null);
            //emvOptV2.deleteCapk(null, null);
            //Master card -Chip
            AidV2 aid_master = new AidV2();
            aid_master.aid = ByteUtil.hexStr2Bytes("A0000007361010");
            aid_master.selFlag = ByteUtil.hexStr2Byte("01");
            aid_master.floorLimit = ByteUtil.hexStr2Bytes("00");
            aid_master.termClssOfflineFloorLmt = ByteUtil.hexStr2Bytes("000000000000");
            aid_master.termOfflineFloorLmt = ByteUtil.hexStr2Bytes("000000000000");
            aid_master.version = ByteUtil.hexStr2Bytes("0001");
            emvOptV2.addAid(aid_master);

            aid_master.aid = ByteUtil.hexStr2Bytes("A0000007362010");
            aid_master.selFlag = ByteUtil.hexStr2Byte("01");
            aid_master.floorLimit = ByteUtil.hexStr2Bytes("00");
            aid_master.termClssOfflineFloorLmt = ByteUtil.hexStr2Bytes("000000000000");
            aid_master.termOfflineFloorLmt = ByteUtil.hexStr2Bytes("000000000000");
            aid_master.version = ByteUtil.hexStr2Bytes("0001");
            emvOptV2.addAid(aid_master);

            aid_master.aid = ByteUtil.hexStr2Bytes("A0000007363010");
            aid_master.selFlag = ByteUtil.hexStr2Byte("01");
            aid_master.floorLimit = ByteUtil.hexStr2Bytes("00");
            aid_master.termClssOfflineFloorLmt = ByteUtil.hexStr2Bytes("000000000000");
            aid_master.termOfflineFloorLmt = ByteUtil.hexStr2Bytes("000000000000");
            aid_master.version = ByteUtil.hexStr2Bytes("0001");
            emvOptV2.addAid(aid_master);


        }catch (Exception ex){
            ex.printStackTrace();
        }


    }

    public static void initAIDs_VISA(EMVOptV2 emvOptV2 ){
        try{
            AidV2 aid_visa_chip_v1 = new AidV2();
            aid_visa_chip_v1.aid = ByteUtil.hexStr2Bytes("A0000000031010");
            aid_visa_chip_v1.floorLimit = ByteUtil.hexStr2Bytes("00");
            aid_visa_chip_v1.termClssOfflineFloorLmt = ByteUtil.hexStr2Bytes("000000000000");
            aid_visa_chip_v1.termOfflineFloorLmt = ByteUtil.hexStr2Bytes("000000000000");
            aid_visa_chip_v1.TACDenial = ByteUtil.hexStr2Bytes("0010000000");
            aid_visa_chip_v1.TACDefault = ByteUtil.hexStr2Bytes("D84000A800");
            aid_visa_chip_v1.TACOnline = ByteUtil.hexStr2Bytes("D84004F800");
            aid_visa_chip_v1.tDOL = ByteUtil.hexStr2Bytes("099F1A0295059A039C01");
            aid_visa_chip_v1.dDOL = ByteUtil.hexStr2Bytes("9f3704");
            aid_visa_chip_v1.version = ByteUtil.hexStr2Bytes("008D");
            emvOptV2.addAid(aid_visa_chip_v1);
            //

            AidV2 aid_visa_chip_v2 = new AidV2();
            aid_visa_chip_v2.aid = ByteUtil.hexStr2Bytes("A0000000031020");
            aid_visa_chip_v2.floorLimit = ByteUtil.hexStr2Bytes("00");
            aid_visa_chip_v2.termClssOfflineFloorLmt = ByteUtil.hexStr2Bytes("000000000000");
            aid_visa_chip_v2.termOfflineFloorLmt = ByteUtil.hexStr2Bytes("000000000000");
            aid_visa_chip_v2.TACDenial = ByteUtil.hexStr2Bytes("0010000000");
            aid_visa_chip_v2.TACDefault = ByteUtil.hexStr2Bytes("D84000A800");
            aid_visa_chip_v2.TACOnline = ByteUtil.hexStr2Bytes("D84004F800");
            aid_visa_chip_v2.tDOL = ByteUtil.hexStr2Bytes("099F1A0295059A039C01");
            aid_visa_chip_v2.dDOL = ByteUtil.hexStr2Bytes("9f3704");
            aid_visa_chip_v2.version = ByteUtil.hexStr2Bytes("008D");
            emvOptV2.addAid(aid_visa_chip_v2);

            AidV2 aid_visa_chip_v3 = new AidV2();
            aid_visa_chip_v3.aid = ByteUtil.hexStr2Bytes("A0000000032010");
            aid_visa_chip_v3.floorLimit = ByteUtil.hexStr2Bytes("00");
            aid_visa_chip_v3.termClssOfflineFloorLmt = ByteUtil.hexStr2Bytes("000000000000");
            aid_visa_chip_v3.termOfflineFloorLmt = ByteUtil.hexStr2Bytes("000000000000");
            aid_visa_chip_v3.TACDenial = ByteUtil.hexStr2Bytes("0010000000");
            aid_visa_chip_v3.TACDefault = ByteUtil.hexStr2Bytes("D84000A800");
            aid_visa_chip_v3.TACOnline = ByteUtil.hexStr2Bytes("D84004F800");
            aid_visa_chip_v3.tDOL = ByteUtil.hexStr2Bytes("099F1A0295059A039C01");
            aid_visa_chip_v3.dDOL = ByteUtil.hexStr2Bytes("9f3704");
            aid_visa_chip_v3.version = ByteUtil.hexStr2Bytes("008D");
            emvOptV2.addAid(aid_visa_chip_v3);

            AidV2 aid_visa_chip_v4 = new AidV2();
            aid_visa_chip_v4.aid = ByteUtil.hexStr2Bytes("A0000000033010");
            aid_visa_chip_v4.floorLimit = ByteUtil.hexStr2Bytes("00");
            aid_visa_chip_v4.termClssOfflineFloorLmt = ByteUtil.hexStr2Bytes("000000000000");
            aid_visa_chip_v4.termOfflineFloorLmt = ByteUtil.hexStr2Bytes("000000000000");
            aid_visa_chip_v4.TACDenial = ByteUtil.hexStr2Bytes("0010000000");
            aid_visa_chip_v4.TACDefault = ByteUtil.hexStr2Bytes("D84000A800");
            aid_visa_chip_v4.TACOnline = ByteUtil.hexStr2Bytes("D84004F800");
            aid_visa_chip_v4.tDOL = ByteUtil.hexStr2Bytes("099F1A0295059A039C01");
            aid_visa_chip_v4.dDOL = ByteUtil.hexStr2Bytes("9f3704");
            aid_visa_chip_v4.version = ByteUtil.hexStr2Bytes("008D");
            emvOptV2.addAid(aid_visa_chip_v4);
            //contactless
            AidV2 aid_visa_chip_v5 = new AidV2();
            aid_visa_chip_v5.aid = ByteUtil.hexStr2Bytes("A000000003");
            aid_visa_chip_v5.ttq = ByteUtil.hexStr2Bytes("36204000");
            aid_visa_chip_v5.floorLimit = ByteUtil.hexStr2Bytes("00");
            aid_visa_chip_v5.termClssOfflineFloorLmt = ByteUtil.hexStr2Bytes("000000000000");
            aid_visa_chip_v5.termOfflineFloorLmt = ByteUtil.hexStr2Bytes("000000000000");
            emvOptV2.addAid(aid_visa_chip_v5);

            AidV2 aid_visa_chip_v6 = new AidV2();
            aid_visa_chip_v6.aid = ByteUtil.hexStr2Bytes("A000000003101002");
            aid_visa_chip_v6.ttq = ByteUtil.hexStr2Bytes("36204000");
            aid_visa_chip_v6.floorLimit = ByteUtil.hexStr2Bytes("00");
            aid_visa_chip_v6.termClssOfflineFloorLmt = ByteUtil.hexStr2Bytes("000000000000");
            aid_visa_chip_v6.termOfflineFloorLmt = ByteUtil.hexStr2Bytes("000000000000");
            emvOptV2.addAid(aid_visa_chip_v6);

            AidV2 aid_visa_chip_v7 = new AidV2();
            aid_visa_chip_v7.aid = ByteUtil.hexStr2Bytes("A000000003101004");
            aid_visa_chip_v7.ttq = ByteUtil.hexStr2Bytes("36204000");
            aid_visa_chip_v7.floorLimit = ByteUtil.hexStr2Bytes("00");
            aid_visa_chip_v7.termClssOfflineFloorLmt = ByteUtil.hexStr2Bytes("000000000000");
            aid_visa_chip_v7.termOfflineFloorLmt = ByteUtil.hexStr2Bytes("000000000000");
            emvOptV2.addAid(aid_visa_chip_v7);

            AidV2 aid_visa_chip_v8 = new AidV2();
            aid_visa_chip_v8.aid = ByteUtil.hexStr2Bytes("A000000003101005");
            aid_visa_chip_v8.ttq = ByteUtil.hexStr2Bytes("36204000");
            aid_visa_chip_v8.floorLimit = ByteUtil.hexStr2Bytes("00");
            aid_visa_chip_v8.termClssOfflineFloorLmt = ByteUtil.hexStr2Bytes("000000000000");
            aid_visa_chip_v8.termOfflineFloorLmt = ByteUtil.hexStr2Bytes("000000000000");
            emvOptV2.addAid(aid_visa_chip_v8);



        }catch (Exception ex){
            ex.printStackTrace()
            ;}

    }

    /**
     * Initialize NFC-Speedup aid and rid
     */


    /**
     * Set EMV terminal parameters.
     * <br/> Client app should save their own terminal parameters
     */
    public static void setTerminalParam(Map<String, String> map) {
        try {

            EMVOptV2 emvOptV2 = MyApplication.app.emvOptV2;
            //chip base
            EmvTermParamV2 emvTermParam_chip = new EmvTermParamV2();
            emvTermParam_chip.countryCode = "0586";
            emvTermParam_chip.currencyCode = "0586";
            emvTermParam_chip.terminalType = "22";
            emvTermParam_chip.useTermAIPFlg = true;
            emvTermParam_chip.termAIP = true;
            emvTermParam_chip.capability = "E0F8C8";
            emvTermParam_chip.addCapability = "FF80F0A001";
            emvTermParam_chip.bypassPin = false;
            emvTermParam_chip.ectTlFlg = false;

            int result = emvOptV2.setTerminalParam(emvTermParam_chip);
            //contactless
            LogUtil.e(Constant.TAG, "setTerminalParam result:" + result);
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.e(Constant.TAG, "setTerminalParam fail");
        }
    }

    /** Convert Hex string AID to AidV2 */
    public static AidV2 hexStr2Aid(String hexStr) {
        AidV2 aidV2 = new AidV2();
        Map<String, TLV> map = TLVUtil.buildTLVMap(hexStr);
        TLV tlv = map.get("DF21");
        if (tlv != null) {
            aidV2.cvmLmt = ByteUtil.hexStr2Bytes(tlv.getValue());
        }
        tlv = map.get("DF20");
        if (tlv != null) {
            aidV2.termClssLmt = ByteUtil.hexStr2Bytes(tlv.getValue());
        }
        tlv = map.get("DF19");
        if (tlv != null) {
            aidV2.termClssOfflineFloorLmt = ByteUtil.hexStr2Bytes(tlv.getValue());
        }
        tlv = map.get("9F7B");
        if (tlv != null) {
            aidV2.termOfflineFloorLmt = ByteUtil.hexStr2Bytes(tlv.getValue());
        }
        tlv = map.get("9F06");
        if (tlv != null) {
            aidV2.aid = ByteUtil.hexStr2Bytes(tlv.getValue());
        }
        tlv = map.get("DF01");
        if (tlv != null) {
            aidV2.selFlag = ByteUtil.hexStr2Byte(tlv.getValue());
        }
        tlv = map.get("DF17");
        if (tlv != null) {
            aidV2.targetPer = ByteUtil.hexStr2Byte(tlv.getValue());
        }
        tlv = map.get("DF16");
        if (tlv != null) {
            aidV2.maxTargetPer = ByteUtil.hexStr2Byte(tlv.getValue());
        }
        tlv = map.get("9F1B");
        if (tlv != null) {
            aidV2.floorLimit = ByteUtil.hexStr2Bytes(tlv.getValue());
        }
        tlv = map.get("DF15");
        if (tlv != null) {
            aidV2.threshold = ByteUtil.hexStr2Bytes(tlv.getValue());
        }
        tlv = map.get("DF13");
        if (tlv != null) {
            aidV2.TACDenial = ByteUtil.hexStr2Bytes(tlv.getValue());
        }
        tlv = map.get("DF12");
        if (tlv != null) {
            aidV2.TACOnline = ByteUtil.hexStr2Bytes(tlv.getValue());
        }
        tlv = map.get("DF11");
        if (tlv != null) {
            aidV2.TACDefault = ByteUtil.hexStr2Bytes(tlv.getValue());
        }
        tlv = map.get("9F01");
        if (tlv != null) {
            aidV2.AcquierId = ByteUtil.hexStr2Bytes(tlv.getValue());
        }
        tlv = map.get("DF14");
        if (tlv != null) {
            aidV2.dDOL = ByteUtil.hexStr2Bytes(tlv.getValue());
        }
        tlv = map.get("9F09");
        if (tlv != null) {
            aidV2.version = ByteUtil.hexStr2Bytes(tlv.getValue());
        }
        tlv = map.get("9F4E");
        if (tlv != null) {
            aidV2.merchName = ByteUtil.hexStr2Bytes(tlv.getValue());
        }
        tlv = map.get("9F15");
        if (tlv != null) {
            aidV2.merchCateCode = ByteUtil.hexStr2Bytes(tlv.getValue());
        }
        tlv = map.get("9F16");
        if (tlv != null) {
            aidV2.merchId = ByteUtil.hexStr2Bytes(tlv.getValue());
        }
        tlv = map.get("9F3C");
        if (tlv != null) {
            aidV2.referCurrCode = ByteUtil.hexStr2Bytes(tlv.getValue());
        }
        tlv = map.get("9F3D");
        if (tlv != null) {
            aidV2.referCurrExp = ByteUtil.hexStr2Byte(tlv.getValue());
        }
        tlv = map.get("DFC108");
        if (tlv != null) {
            aidV2.clsStatusCheck = ByteUtil.hexStr2Byte(tlv.getValue());
        }
        tlv = map.get("DFC109");
        if (tlv != null) {
            aidV2.zeroCheck = ByteUtil.hexStr2Byte(tlv.getValue());
        }
        tlv = map.get("DFC10A");
        if (tlv != null) {
            aidV2. kernelType = ByteUtil.hexStr2Byte(tlv.getValue());
        }
        tlv = map.get("DFC10B");
        if (tlv != null) {
            aidV2.paramType = ByteUtil.hexStr2Byte(tlv.getValue());
        }
        tlv = map.get("9F66");
        if (tlv != null) {
            aidV2.ttq = ByteUtil.hexStr2Bytes(tlv.getValue());
        }
        tlv = map.get("9F1C");
        if (tlv != null) {
            aidV2.termId = ByteUtil.hexStr2Bytes(tlv.getValue());
        }
        tlv = map.get("9F1D");
        if (tlv != null) {
            aidV2.riskManData = ByteUtil.hexStr2Bytes(tlv.getValue());
        }
        tlv = map.get("DF8101");
        if (tlv != null) {
            aidV2.referCurrCon = ByteUtil.hexStr2Bytes(tlv.getValue());
        }
        tlv = map.get("DF8102");
        if (tlv != null) {
            aidV2.tDOL = ByteUtil.hexStr2Bytes(tlv.getValue());
        }
        tlv = map.get("DFC10C");
        if (tlv != null) {
            aidV2.kernelID = ByteUtil.hexStr2Bytes(tlv.getValue());
        }
        return aidV2;
    }

    /** Convert Hex string RID to CapkV2 */
    public static CapkV2 hexStr2Rid(String hexStr) {
        CapkV2 capkV2 = new CapkV2();
        Map<String, TLV> map = TLVUtil.buildTLVMap(hexStr);
        TLV tlv = map.get("9F06");
        if (tlv != null) {
            capkV2.rid = ByteUtil.hexStr2Bytes(tlv.getValue());
        }
        tlv = map.get("9F22");
        if (tlv != null) {
            capkV2.index = ByteUtil.hexStr2Byte(tlv.getValue());
        }
        tlv = map.get("DF06");
        if (tlv != null) {
            capkV2.hashInd = ByteUtil.hexStr2Byte(tlv.getValue());
        }
        tlv = map.get("DF07");
        if (tlv != null) {
            capkV2.arithInd = ByteUtil.hexStr2Byte(tlv.getValue());
        }
        tlv = map.get("DF02");
        if (tlv != null) {
            capkV2.modul = ByteUtil.hexStr2Bytes(tlv.getValue());
        }
        tlv = map.get("DF04");
        if (tlv != null) {
            capkV2.exponent = ByteUtil.hexStr2Bytes(tlv.getValue());
        }
        tlv = map.get("DF05");
        if (tlv != null) {
            capkV2.expDate = ByteUtil.hexStr2Bytes(tlv.getValue());
        }
        tlv = map.get("DF03");
        if (tlv != null) {
            capkV2.checkSum = ByteUtil.hexStr2Bytes(tlv.getValue());
        }
        return capkV2;
    }

    public static String maskCardNumber(String cardNumber) {
        // Remove all spaces for processing
        String cleanNumber = cardNumber.replaceAll("\\s+", "");

        if (cleanNumber.length() <= 6) {
            return cleanNumber; // Not enough digits to mask
        }

        // Keep first 4 and last 2
        String firstFour = cleanNumber.substring(0, 4);
        String lastTwo = cleanNumber.substring(cleanNumber.length() - 2);

        // Mask the middle part with '*'
        String maskedMiddle = "*".repeat(cleanNumber.length() - 6);

        // Combine the parts
        String maskedNumber = firstFour + maskedMiddle + lastTwo;

        // Optionally, format it in groups of 4 again for readability
        return maskedNumber.replaceAll("(.{4})(?=.)", "$1 ");
    }


    public static String getPan(String input) {
        if (input == null) {
            return null;
        }

        int index = input.indexOf('D');
        if (index != -1) {
            return input.substring(0, index);
        }

        // If 'D' is not found, return first 16 characters (if available)
        return input.length() >= 16 ? input.substring(0, 16) : input;
    }
}
