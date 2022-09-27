package com.example.foodcode.utils;

import android.content.Context;
import android.util.Log;

import com.example.foodcode.data.AuthManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class HttpClient {

    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

//    private static final String BASE_URL = "https://baomadev.cttq.com/";
    private static final String BASE_URL = "https://baomaqas.cttq.com/";

    AuthManager authManager;

    public HttpClient(Context context){

        this.authManager = new AuthManager(context);
    }

    public void post(String path, Map<String, Object> params, okhttp3.Callback callback) {
        OkHttpClient client = new OkHttpClient();

        JSONObject jsonObject = new JSONObject();
        try {
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                jsonObject.put(entry.getKey(), entry.getValue());
            }

            Log.i("URL", BASE_URL + path);
            Log.i("PARAMS", jsonObject.toString());

            String reqSource = "android";
            String appSource = "baomaApp";
            RequestBody body = RequestBody.create(jsonObject.toString(), JSON);
            Request.Builder builder = new Request.Builder()
                    .url(BASE_URL + path)
                    .header("appSource", appSource)
                    .addHeader("reqSource", reqSource)
                    .post(body);
            String token = authManager.getAuthToken();

            if(!Objects.equals(token, "")){
                builder.addHeader("token", token);

                MessageDigest md = MessageDigest.getInstance("MD5");
                byte[] security = md.digest("UuOs!MjhJjQjkfOlke".getBytes());
                byte[] digest = md.digest((reqSource + token + bytesToHex(security)).getBytes());
                builder.addHeader("digest", bytesToHex(digest));
            }

            Request request = builder.build();
            Log.i("REQUEST HEADER: ", request.headers().toString());
            client.newCall(request).enqueue(callback);

        } catch (JSONException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }


    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
