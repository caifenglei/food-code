package com.example.foodcode.login;

import androidx.annotation.Nullable;

import org.json.JSONObject;

/**
 * Authentication result : success (user details) or error message.
 */
public class LoginResult {
    @Nullable
    private JSONObject success;
    @Nullable
    private String error;

    LoginResult(@Nullable String error) {
        this.error = error;
    }

    LoginResult(@Nullable JSONObject success) {
        this.success = success;
    }

    @Nullable
    public JSONObject getSuccess() {
        return success;
    }

    @Nullable
    public String getError() {
        return error;
    }
}