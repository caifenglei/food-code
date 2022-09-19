package com.example.foodcode.data;

import androidx.annotation.Nullable;

import org.json.JSONObject;

public class PaymentResult {
    @Nullable
    private JSONObject success;
    @Nullable
    private String error;

    PaymentResult(@Nullable String error) {
        this.error = error;
    }

    PaymentResult(@Nullable JSONObject success) {
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
