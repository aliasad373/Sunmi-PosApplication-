package com.mindcrafts.posapp.iso8583.parser;

import java.util.*;
import java.nio.charset.StandardCharsets;

public class ISOParser {

    public static Map<String, String> parseMessage(byte[] data, Map<Integer, Integer> fieldLengths) {
        Map<String, String> result = new LinkedHashMap<>();
        int offset = 0;

        // 1: Total length (2 bytes)
        byte[] totalLengthBytes = Arrays.copyOfRange(data, offset, offset + 2);
        int totalLength = ((totalLengthBytes[0] & 0xFF) << 8) | (totalLengthBytes[1] & 0xFF);
        result.put("Length", String.valueOf(totalLength));
        offset += 2;

        // 2: TPDU (5 bytes)
        byte[] tpduBytes = Arrays.copyOfRange(data, offset, offset + 5);
        result.put("TPDU", bytesToHex(tpduBytes));
        offset += 5;

        // 3: MTI (2 bytes)
        byte[] mtiBytes = Arrays.copyOfRange(data, offset, offset + 2);
        result.put("MTI", bytesToHex(mtiBytes));
        offset += 2;

        // 4: Bitmap (8 bytes)
        byte[] bitmapBytes = Arrays.copyOfRange(data, offset, offset + 8);
        result.put("Bitmap", bytesToHex(bitmapBytes));
        offset += 8;

        // Parse bitmap bits to find which fields are present
        List<Integer> presentFields = getActiveFields(bitmapBytes);

        // 5: For each present field in provided field map, extract value
        for (Integer field : presentFields) {
            if (fieldLengths.containsKey(field)) {
                int len = fieldLengths.get(field);
                if (offset + len <= data.length) {
                    byte[] valueBytes = Arrays.copyOfRange(data, offset, offset + len);
                    result.put("Field_" + field, bytesToHex(valueBytes));
                    offset += len;
                } else {
                    break; // Safety guard
                }
            }
        }

        return result;
    }

    private static List<Integer> getActiveFields(byte[] bitmap) {
        List<Integer> activeFields = new ArrayList<>();
        for (int i = 0; i < bitmap.length; i++) {
            int b = bitmap[i] & 0xFF;
            for (int bit = 0; bit < 8; bit++) {
                if ((b & (1 << (7 - bit))) != 0) {
                    int fieldNo = (i * 8) + (bit + 1); // fields are 1-indexed
                    activeFields.add(fieldNo);
                }
            }
        }
        return activeFields;
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes)
            sb.append(String.format("%02X", b));
        return sb.toString();
    }
}
