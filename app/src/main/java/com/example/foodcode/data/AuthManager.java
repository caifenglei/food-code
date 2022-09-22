package com.example.foodcode.data;

import android.content.Context;
import android.content.SharedPreferences;

public class AuthManager {
    SharedPreferences sharedPreferences;

    Context context;

    SharedPreferences.Editor editor;

    private static final String PREFER_NAME = "AndroidAppAuth";

    private static final String KEY_AUTH_TOKEN = "accessAuthToken";
    private static final String KEY_TENANT_NAME = "tenantName";
    private static final String KEY_DEVICE_CODE = "deviceCode";
    private static final String KEY_CASHIER_TYPE = "cashierType";
    private static final String KEY_CASHIER_AMOUNT = "cashierAmount";

    public static final String CASHIER_MANUAL = "1";
    public static final String CASHIER_AUTO = "2";


    public AuthManager(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences(PREFER_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public void setAuthToken(String token) {
        editor.putString(KEY_AUTH_TOKEN, token);
        editor.commit();
    }

    public String getAuthToken() {
        return sharedPreferences.getString(KEY_AUTH_TOKEN, "");
    }

    public void setTenantName(String name) {
        editor.putString(KEY_TENANT_NAME, name);
        editor.commit();
    }

    public String getTenantName() {
        return sharedPreferences.getString(KEY_TENANT_NAME, "");
    }

    public void setDeviceCode(String deviceCode) {
        editor.putString(KEY_DEVICE_CODE, deviceCode);
        editor.commit();
    }

    public String getDeviceCode() {
        return sharedPreferences.getString(KEY_DEVICE_CODE, "");
    }

    public void setCashierType(String cashierType){
        editor.putString(KEY_CASHIER_TYPE, cashierType);
        editor.commit();
    }

    public String getCashierType(){
        return sharedPreferences.getString(KEY_CASHIER_TYPE, "");
    }

    public void setCashierAmount(String cashierAmount){
        editor.putString(KEY_CASHIER_AMOUNT, cashierAmount);
        editor.commit();
    }

    public String getCashierAmount(){
        return sharedPreferences.getString(KEY_CASHIER_AMOUNT, "");
    }

    public void clearAuth(){
        editor.clear();
        editor.commit();
    }
}
