package com.mindcrafts.posapp.storage;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPrefHelper {

    private static final String PREF_NAME = "dgk_pref";
    private static SharedPrefHelper instance;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    // Private constructor (Singleton pattern)
    private SharedPrefHelper(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    // Get instance
    public static synchronized SharedPrefHelper getInstance(Context context) {
        if (instance == null) {
            instance = new SharedPrefHelper(context.getApplicationContext());
        }
        return instance;
    }

    // ---------- Save methods ----------

    public void saveBatchNo(String key, int value) {
        editor.putInt(key, value).apply();
    }



    // ---------- Get methods ----------
    public int getBatch(String key) {
        return sharedPreferences.getInt(key, 1);
    }


    public void saveMID(String Key, String value){
        editor.putString(Key, value).apply();
    }

    public void saveTID(String key, String value){
        editor.putString(key, value).apply();
    }

    public void saveBusinessName(String key, String value){
        editor.putString(key, value).apply();

    }

    public void saveAddress(String key, String value){
        editor.putString(key, value).apply();

    }



    public void saveMerchantName(String key, String value){
        editor.putString(key, value).apply();
    }

    public String getMID(String key){
        return sharedPreferences.getString(key, "");
    }

    public String getTID(String key){
        return sharedPreferences.getString(key, "");
    }

    public String getAddress(String key){
        return sharedPreferences.getString(key, "");
    }

    public String getBusiness(String key){
        return sharedPreferences.getString(key, "");
    }
    public String getMerchant(String key){
        return sharedPreferences.getString(key, "");
    }
    // ---------- Remove / Clear ----------
    public void remove(String key) {
        editor.remove(key).apply();
    }

    public void clearAll() {
        editor.clear().apply();
    }

    public void saveToken(String key, String value){
        editor.putString(key, value).apply();
    }

    public String getToken(String key){
        return sharedPreferences.getString(key, "");
    }

}
