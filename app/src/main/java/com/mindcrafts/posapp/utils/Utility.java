package com.mindcrafts.posapp.utils;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;


import androidx.annotation.RequiresApi;

import com.mindcrafts.posapp.app.MyApplication;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

public final class Utility {
    private Utility() {
        throw new AssertionError("Create instance of Utility is forbidden.");
    }

    /** Bundle对象转换成字符串 */
    public static String bundle2String(Bundle bundle) {
        return bundle2String(bundle, 1);
    }

    /**
     * 根据key排序后将Bundle内容拼接成字符串
     *
     * @param bundle 要处理的bundle
     * @param order  排序规则，0-不排序，1-升序，2-降序
     * @return 拼接后的字符串
     */
    public static String bundle2String(Bundle bundle, int order) {
        if (bundle == null || bundle.keySet().isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        List<String> list = new ArrayList<>(bundle.keySet());
        if (order == 1) { //升序
            Collections.sort(list, String::compareTo);
        } else if (order == 2) {//降序
            Collections.sort(list, Collections.reverseOrder());
        }
        for (String key : list) {
            sb.append(key);
            sb.append(":");
            Object value = bundle.get(key);
            if (value instanceof byte[]) {
                sb.append(ByteUtil.bytes2HexStr((byte[]) value));
            } else {
                sb.append(value);
            }
            sb.append("\n");
        }
        if (sb.length() > 0) {
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }

    /** 将null转换成空串 */
    public static String null2String(String str) {
        return str == null ? "" : str;
    }

    public static String formatStr(String format, Object... params) {
        return String.format(Locale.ENGLISH, format, params);
    }

    /** check whether src is hex format */
    public static boolean checkHexValue(String src) {
        return Pattern.matches("[0-9a-fA-F]+", src);
    }

    /** 显示Toast */
    public static void showToast(final String msg) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(() -> Toast.makeText(MyApplication.app, msg, Toast.LENGTH_SHORT).show());
    }

    /** 显示Toast */
    public static void showToast(int resId) {
        showToast(MyApplication.app.getString(resId));
    }

    /** 根据结果码获取成功失败信息 */
    public static String getStateString(int code) {
        return code == 0 ? "success" : "failed, code:" + code;
    }

    /** 根据结果状态获取成功失败信息 */
    public static String getStateString(boolean state) {
        return state ? "success" : "failed";
    }

    /** 将dp转成px */
    public static int dp2px(int dp) {
        float density = MyApplication.app.getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }

    /** 将px转成dp */
    public static int px2dp(int px) {
        float density = MyApplication.app.getResources().getDisplayMetrics().density;
        return Math.round(px / density);
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String getLocalDateString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return LocalDate.now().format(formatter);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String getCurrentTimeHMS() {
        LocalTime now = LocalTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        return now.format(formatter);
    }

    public static String decodeAsciiHex(String hex) {
        StringBuilder output = new StringBuilder();

        // Process two characters (one byte) at a time
        for (int i = 0; i < hex.length(); i += 2) {
            String str = hex.substring(i, i + 2);
            // Convert hex pair (like "30") to an ASCII char ('0')
            output.append((char) Integer.parseInt(str, 16));
        }

        return output.toString();
    }

    public static String getResponseReason(String code) {
        if (code == null) return "Invalid code";

        switch (code) {
            case "00": return "Approved or completed successfully";
            case "01": return "Refer to card issuer";
            case "02": return "Refer to card issuer, special condition";
            case "03": return "Invalid merchant";
            case "04": return "Pick up card";
            case "05": return "Do not honor";
            case "06": return "Error";
            case "07": return "Pick up card, special condition";
            case "08": return "Honor with identification";
            case "09": return "Request in progress";
            case "10": return "Partial approval";
            case "11": return "Approved (VIP approval)";
            case "12": return "Invalid transaction";
            case "13": return "Invalid amount";
            case "14": return "Invalid card number";
            case "15": return "No such issuer";
            case "16": return "Approved, update track 3";
            case "17": return "Customer cancellation";
            case "18": return "Customer dispute";
            case "19": return "Re-enter transaction";
            case "20": return "Invalid response";
            case "21": return "No action taken";
            case "22": return "Suspected malfunction";
            case "23": return "Unacceptable transaction fee";
            case "24": return "File update not supported";
            case "25": return "Unable to locate record";
            case "26": return "Duplicate record";
            case "27": return "File update field edit error";
            case "28": return "File temporarily unavailable";
            case "29": return "File update rejected";
            case "30": return "Format error";
            case "31": return "Bank not supported by switch";
            case "32": return "Completed partially";
            case "33": return "Expired card, pick up";
            case "34": return "Suspected fraud, pick up card";
            case "35": return "Contact acquirer, pick up card";
            case "36": return "Restricted card, pick up";
            case "37": return "Call acquirer security, pick up";
            case "38": return "PIN tries exceeded, pick up";
            case "39": return "No credit account";
            case "40": return "Requested function not supported";
            case "41": return "Lost card, pick up";
            case "42": return "No universal account";
            case "43": return "Stolen card, pick up";
            case "44": return "No investment account";
            case "45": return "Account closed";
            case "46": return "Identification required";
            case "47": return "Identification cross-check required";
            case "48": return "No customer record";
            case "49": return "Reserved for future use";
            case "50": return "No checking account";
            case "51": return "Insufficient funds";
            case "52": return "No savings account";
            case "53": return "No checking account";
            case "54": return "Expired card";
            case "55": return "Incorrect PIN";
            case "56": return "No card record";
            case "57": return "Transaction not permitted to cardholder";
            case "58": return "Transaction not permitted to terminal";
            case "59": return "Suspected fraud";
            case "60": return "Contact acquirer";
            case "61": return "Exceeds withdrawal limit";
            case "62": return "Restricted card";
            case "63": return "Security violation";
            case "64": return "Original amount incorrect";
            case "65": return "Exceeds withdrawal frequency limit";
            case "66": return "Call acquirer security";
            case "67": return "Hard capture (pick up card)";
            case "68": return "Response received too late";
            case "69": return "Advice received too late";
            case "70": return "Contact card issuer";
            case "71": return "PIN not changed";
            case "72": return "Reserved for future use";
            case "73": return "Error in card acceptor settlement";
            case "74": return "Error in batch number sequence";
            case "75": return "Allowable number of PIN tries exceeded";
            case "76": return "Key synchronization error";
            case "77": return "Reserved for future use";
            case "78": return "Customer not eligible for transaction";
            case "79": return "Already reversed";
            case "80": return "Network error";
            case "81": return "Foreign network error";
            case "82": return "Time-out at issuer";
            case "83": return "Unable to verify PIN";
            case "84": return "Invalid authorization life cycle";
            case "85": return "No reason to decline";
            case "86": return "Cannot verify PIN";
            case "87": return "Purchase limit exceeded";
            case "88": return "ATM limit exceeded";
            case "89": return "Invalid terminal ID";
            case "90": return "Cutover in process";
            case "91": return "Issuer or switch inoperative";
            case "92": return "Financial institution not found";
            case "93": return "Transaction cannot be completed – violation of law";
            case "94": return "Duplicate transmission";
            case "95": return "Reconcile error";
            case "96": return "System malfunction";
            case "97": return "Reserved for future use";
            case "98": return "Exceeds cash limit";
            case "99": return "Reserved for national use";
            default: return "Unknown response code";
        }
    }

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final SecureRandom random = new SecureRandom();

    public static String generateOfflineId() {
        StringBuilder sb = new StringBuilder(19);
        for (int i = 0; i < 19; i++) {
            int index = random.nextInt(CHARACTERS.length());
            sb.append(CHARACTERS.charAt(index));
        }
        return sb.toString();
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String getCurrentTimestamp() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return now.format(formatter);
    }

}
