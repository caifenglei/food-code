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
}
