package com.example.foodcode.data;

import androidx.annotation.Nullable;

import org.json.JSONObject;

public class PaymentResult {
    @Nullable
    private JSONObject result;
    @Nullable
    private String error;

    PaymentResult(@Nullable String error) {
        this.error = error;
    }

    PaymentResult(@Nullable JSONObject result) {
        this.result = result;
    }

    @Nullable
    public JSONObject getSuccess() {
        return result;
    }

    @Nullable
    public String getError() {
        return error;
    }
}
