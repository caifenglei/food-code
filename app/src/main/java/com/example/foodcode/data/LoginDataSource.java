package com.example.foodcode.data;

import android.os.AsyncTask;
import android.util.Log;

//import com.androidnetworking.AndroidNetworking;
//import com.androidnetworking.common.ANRequest;
//import com.androidnetworking.common.ANResponse;
//import com.androidnetworking.error.ANError;
//import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.example.foodcode.data.model.LoggedInUser;

//import org.json.JSONException;
//import org.json.JSONObject;
//
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
//import java.util.concurrent.CountDownLatch;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
public class LoginDataSource {

    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    public Result<LoggedInUser> login(String username, String password) {

        Log.d("--username", username);
        Log.d("--password", password);


        OkHttpClient client = new OkHttpClient();

        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("account", username);
            jsonObject.put("password", password);

            Runnable runnable = new Runnable() {
                public void run() {
                    RequestBody body = RequestBody.create(jsonObject.toString(), JSON);
                    Request request = new Request.Builder()
                            .url("https://baomadev.cttq.com/app/merchant/account/login")
                            .post(body)
                            .build();

                    try {
                        Response response = client.newCall(request).execute();
                        Log.d("---RESP", response.body().string());

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            };

            new Thread(runnable).start();
        } catch (JSONException je) {
            je.printStackTrace();
        }

        Log.i("---MARK", "h1");

//        try {
//
//
//            HttpClient httpClient = new DefaultHttpClient();
//            HttpPost request = new HttpPost("https://baomadev.cttq.com/app/merchant/account/login");
//
//            JSONObject jsonObject = new JSONObject();
//            jsonObject.put("account", username);
//            jsonObject.put("password", password);
//            StringEntity entity = new StringEntity(jsonObject.toString());
//            request.setEntity(entity);
//            request.setHeader("Accept", "application/json");
//            request.setHeader("Content-type", "application/json");
//
////            ResponseHandler responseHandler = new BasicResponseHandler();
//            HttpResponse response = httpClient.execute(request);
//
//            HttpEntity respEntity = response.getEntity();
//            BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent()));
//            String result = reader.readLine();
//            Log.d("HTTP RESP", result);
//
//
////            List<NameValuePair> postParams = new ArrayList<NameValuePair>();
////            postParams.add(new BasicNameValuePair("account", username));
////            postParams.add(new BasicNameValuePair("password", password));
////
////
////            HttpResponse response = httpClient.execute(request);
//        } catch (JSONException | UnsupportedEncodingException je) {
//
//        } catch (ClientProtocolException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

//        try {
//            JSONObject jsonObject = new JSONObject();
//            jsonObject.put("account", username);
//            jsonObject.put("password", password);
//
//            Object syncLock = new Object();
//            CountDownLatch controller = new CountDownLatch(1);
////            synchronized (syncLock) {
//                AndroidNetworking.post("https://baomadev.cttq.com/app/merchant/account/login")
//                        .addJSONObjectBody(jsonObject)
//                        .build()
//                        .getAsJSONObject(new JSONObjectRequestListener() {
//                            @Override
//                            public void onResponse(JSONObject response) {
//                                Log.i("login resp", response.toString());
//                                try {
//                                    if (response.getString("msgCode") == "100") {
//                                        LoggedInUser fakeUser =
//                                                new LoggedInUser(
//                                                        java.util.UUID.randomUUID().toString(),
//                                                        "Jane Doe");
//                                        result = new Result.Success<>(fakeUser);
//                                    } else {
//                                        result = new Result.Error(new IOException("Error logging in11"));
//                                    }
//                                } catch (JSONException e) {
//                                    result = new Result.Error(new IOException("Error logging in1.1"));
//                                }
////                                synchronized (syncLock) {
////                                    syncLock.notify();
////                                }
//                                controller.countDown();
//
//                            }
//
//                            @Override
//                            public void onError(ANError anError) {
//                                Log.e("error", anError.getMessage());
//                                result = new Result.Error(new IOException("Error logging in22"));
////                                synchronized (syncLock) {
////                                    syncLock.notify();
////                                }
//                                controller.countDown();
//                            }
//                        });
//                try {
////                    syncLock.wait();
//                    controller.await();
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
////            }
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//        return result;

        try {
//            JSONObject jsonObject = new JSONObject();
//            jsonObject.put("account", username);
//            jsonObject.put("password", password);
//
////            ANRequest request =
//                    AndroidNetworking.post("https://baomadev.cttq.com/app/merchant/account/login")
//                    .addJSONObjectBody(jsonObject)
////                    .build();
//                    .build()
//                    .getAsJSONObject(new JSONObjectRequestListener() {
//                        @Override
//                        public void onResponse(JSONObject response) {
//                            try {
////                                Log.i("login resp code", response.get("msgCode"));
//                                Log.i("login resp code", response.getString("msgCode"));
//                                Log.i("login resp", response.toString());
//                            }catch (JSONException je){
//
//                            }
//                        }
//
//                        @Override
//                        public void onError(ANError anError) {
//
//                        }
//                    });
//
////            ANResponse response = request.executeForJSONArray();
////            ANResponse response = request.executeForJSONObject();
////            ANResponse response = request.executeForObject(JSON);
////
////            if(response.isSuccess()){
////                JSONObject result = (JSONObject) response.getResult();
////                Log.i("login resp", result.get("msgCode").toString());
////                Log.i("login resp2", String.valueOf(response.isSuccess()));
////            }else{
////                Log.e("error", response.getError().toString());
////            }
//
//
//            // TODO: handle loggedInUser authentication
            LoggedInUser fakeUser =
                    new LoggedInUser(
                            java.util.UUID.randomUUID().toString(),
                            "Jane Doe");
            return new Result.Success<>(fakeUser);
        } catch (Exception e) {
            return new Result.Error(new IOException("Error logging in", e));
        }
    }

    public void logout() {
        // TODO: revoke authentication
    }
}