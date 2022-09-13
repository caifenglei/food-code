package com.example.foodcode.login;

import org.json.JSONObject;

/**
 * Class exposing authenticated user details to the UI.
 */
class LoggedInUserView {
    private String displayName;
    //... other data fields that may be accessible to the UI
    private JSONObject authJson;

    LoggedInUserView(String displayName) {
        this.displayName = displayName;
    }

    LoggedInUserView(JSONObject authJson) {
        this.authJson = authJson;
    }

    String getDisplayName() {
        return displayName;
    }

    JSONObject getAuthJson() {
        return authJson;
    }
}