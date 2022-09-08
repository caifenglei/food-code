package com.example.foodcode.utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class HttpClient {

    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    private static final String BASE_URL = "https://baomadev.cttq.com/";

    public static void post(String path, Map<String, String> params, okhttp3.Callback callback) {
        OkHttpClient client = new OkHttpClient();

        JSONObject jsonObject = new JSONObject();
        try {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                jsonObject.put(entry.getKey(), entry.getValue());
            }

            RequestBody body = RequestBody.create(jsonObject.toString(), JSON);

            Request request = new Request.Builder()
                    .url(BASE_URL + path)
                    .post(body)
                    .build();
            client.newCall(request).enqueue(callback);

        } catch (JSONException e) {
            e.printStackTrace();
        }


    }
}
