package com.mindcrafts.posapp.iso8583.isoUtils;

import android.util.Log;

import com.mindcrafts.posapp.utils.ByteUtil;
import com.mindcrafts.posapp.utils.TLV;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class IsoUtils {
    private IsoUtils(){}
    public static byte[] DE55Builder(
            Map<String, TLV> allTags,
            List<String> selectedKeys
    ) {
        StringBuilder combinedHex = new StringBuilder();

        for (String key : selectedKeys) {
            TLV tagData = allTags.get(key);
            if (tagData != null) {
                String lengthHex = String.format("%02X", tagData.getLength());
                combinedHex.append(tagData.getTag())
                        .append(lengthHex)
                        .append(tagData.getValue());
            }
        }

        // Convert hex string to byte array
        String hexString = combinedHex.toString();
        int len = hexString.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) Integer.parseInt(hexString.substring(i, i + 2), 16);
        }

        // Ensure even length
      /*  if (data.length % 2 != 0) {
            byte[] padded = new byte[data.length + 1];
            //padded[0] = 0x00;
            System.arraycopy(data, 0, padded, 1, data.length);
            data = padded;
        }*/

        // Convert length to 4-digit decimal (2 bytes BCD-style)
        int length = data.length;
        String lenStr = String.format("%04d", length); // e.g. "0158"
        byte[] dataLength = ByteUtil.hexStr2Bytes(lenStr);

        // Combine
        byte[] result = new byte[2 + data.length];
        System.arraycopy(dataLength, 0, result, 0, 2);
        System.arraycopy(data, 0, result, 2, data.length);

        return result;
    }

    private static byte[] buildBitmap(Set<Integer> fields) {
        byte[] bitmap = new byte[8]; // 64 bits
        for (int field : fields) {
            if (field < 1 || field > 64) continue; // only primary bitmap fields
            int index = (field - 1) / 8;
            int bit = 7 - ((field - 1) % 8);
            bitmap[index] |= (1 << bit);
        }
        return bitmap;
    }

    public static byte[] buildIsoMessage(byte[] tpdu,byte[] mti, Map<Integer, byte[]> fieldMap) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            out.write(tpdu);
            // 1. MTI
            out.write(mti);

            // 2. Primary bitmap
            byte[] bitmap = buildBitmap(fieldMap.keySet());
            out.write(bitmap);

            // 3. Fields (in ascending order)
            fieldMap.keySet().stream().sorted().forEach(field -> {
                try {
                    out.write(fieldMap.get(field)); // just write raw bytes
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });

            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static byte[] getPAN(Map<String, TLV> map){
        TLV tlv_pan = map.get("5A");
        assert tlv_pan != null;
        String PAN = tlv_pan.getValue();
        if(PAN.isEmpty() || PAN.equals("0")){
            String tlv_pan_57 = map.get("57").getValue();
            int index = tlv_pan_57.indexOf('D');
            if (index != -1) {
                String pan  = tlv_pan_57.substring(0, index); // keep everything before D
                int size = pan.length();
                String _pan_with_length = String.valueOf(size) + pan;
                return ByteUtil.hexStr2Bytes(_pan_with_length);
            }
        }else {
            int size = PAN.length();
            String _pan_with_length = String.valueOf(size) + PAN;
            return ByteUtil.hexStr2Bytes(_pan_with_length);
        }
        return null;
    }
public static byte[] getPanforVoid(String PAN){
    int size = PAN.length();
    String _pan_with_length = String.valueOf(size) + PAN;
    return ByteUtil.hexStr2Bytes(_pan_with_length);
}
    public static byte[] track2Data(Map<String, TLV> map){
        TLV tlv_track2 = map.get("57");
        assert tlv_track2 != null;
        String track2data = tlv_track2.getValue();
        int lastF = track2data.lastIndexOf('F');
        StringBuilder sb = new StringBuilder(track2data);
        sb.setCharAt(lastF, '0');
        track2data = sb.toString();
        int size = track2data.length();
        String _track2_with_length = String.valueOf(size-1) + track2data;
        return ByteUtil.hexStr2Bytes(_track2_with_length);
    }

    public static String paddMIDTID(String input) {
        StringBuilder result = new StringBuilder();
        for (char c : input.toCharArray()) {
            result.append("3").append(c);
        }
        return result.toString();
    }

    public static byte[] DE62(Map<String, TLV> map){
        String stan = map.get("9F41").getValue();
        String data = stan.substring(2);
        data = "0006" + paddMIDTID(data);
        return  ByteUtil.hexStr2Bytes(data);
    }

    public static String dropFirstTwo(String input) {
        if (input == null || input.length() <= 2) {
            return ""; // or return input if you prefer
        }
        return input.substring(2);
    }
    public static String dropLastTwo(String input) {
        if (input == null || input.length() <= 2) {
            return ""; // return empty if string is null or has 2 or fewer characters
        }
        return input.substring(0, input.length() - 2);
    }

    public static String padToSix(String input) {
        if (input == null) {
            return "000000";
        }

        // If length is less than 6, pad with leading zeros
        if (input.length() < 6) {
            return String.format("%6s", input).replace(' ', '0');
        }

        // If length is already 6 or more, just return the first 6 characters
        return input.substring(0, 6);
    }
    public static String padToThree(String input) {
        if (input == null) {
            return "000"; // handle null safely
        }
        if (input.length() >= 3) {
            return input;
        }
        // Pad with leading zeros
        return String.format("%3s", input).replace(' ', '0');
    }

    public static String padToTwelve(String input) {
        if (input == null) {
            return "000000000000"; // handle null safely
        }
        if (input.length() >= 12) {
            return input;
        }
        return String.format("%12s", input).replace(' ', '0');
    }
    public static String getClearAmount(String amount) {
        // Remove leading zeros
        String noLeadingZeros = amount.replaceFirst("^0+", "");

        // If string becomes empty, treat as "0"
        if (noLeadingZeros.isEmpty()) {
            noLeadingZeros = "0";
        }

        // Ensure the string has at least 3 characters to place the decimal
        if (noLeadingZeros.length() < 3) {
            // Pad with leading zeros if needed
            noLeadingZeros = String.format("%03d", Integer.parseInt(noLeadingZeros));
        }

        // Insert decimal point before last two digits
        String result = noLeadingZeros.substring(0, noLeadingZeros.length() - 2)
                + "."
                + noLeadingZeros.substring(noLeadingZeros.length() - 2);

        return result;
    }

    public static String formatAmountforsettle(String amount) {
        if (!amount.contains(".")) {
            // No decimal point: just append "00"
            return amount + "00";
        }

        String[] parts = amount.split("\\.");
        String whole = parts[0];
        String fraction = parts.length > 1 ? parts[1] : "";

        // Pad or trim the fraction to exactly 2 digits
        if (fraction.length() == 0) {
            fraction = "00";
        } else if (fraction.length() == 1) {
            fraction = fraction + "0";
        } else if (fraction.length() > 2) {
            fraction = fraction.substring(0, 2);
        }

        return whole + fraction;
    }

    public static String maskedPAN(String input) {
        if (input == null || input.length() != 16) {
            throw new IllegalArgumentException("Input must be exactly 16 characters long.");
        }

        String first4 = input.substring(0, 4);
        String last4 = input.substring(12);
        String masked = "********"; // 8 asterisks

        return first4 + masked + last4;
    }

    public static String hexToAscii(String hexString) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < hexString.length(); i += 2) {
            // Get the hex value of each pair of characters
            String hexChar = hexString.substring(i, i + 2);
            // Convert hex to an integer, then to a char
            char asciiChar = (char) Integer.parseInt(hexChar, 16);
            sb.append(asciiChar);
        }
        return sb.toString();
    }


}
