package com.example.foodcode.data;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.foodcode.login.LoginResult;
import com.example.foodcode.utils.HttpClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Response;

public class PaymentViewModel {

    private AuthManager authManager;

    private Context context;

    private MutableLiveData<PaymentResult> paymentResult = new MutableLiveData<>();

    public PaymentViewModel(Context context) {
        this.authManager = new AuthManager(context);
        this.context = context;
    }

    //payment async callback result
    public LiveData<PaymentResult> getPaymentResult() {
        return paymentResult;
    }

    public void receiveMoney(String qrCode, String amount) {

        Map<String, Object> params = new HashMap<>();
        params.put("qrCode", qrCode);
        params.put("amount", amount);
        params.put("terminalNum", authManager.getDeviceCode());
        new HttpClient(context).post("app/merchant/payment/pay", params, new okhttp3.Callback() {
            @Override
            public void onFailure(@NonNull Call call, IOException e) {
                Log.e("HTTP onFailure", e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseBody = response.body().string();
                try {
                    final JSONObject responseJson = new JSONObject(responseBody);
                    String msgCode = responseJson.getString("msgCode");

                    //switch to UI main thread
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                if (msgCode.equals("100")) {
                                    paymentResult.setValue(new PaymentResult(responseJson.getJSONObject("responseData")));
                                } else {
                                    paymentResult.setValue(new PaymentResult(responseJson.getString("message")));
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                paymentResult.setValue(new PaymentResult("收款错误-返回数据错误1"));
                            }
                        }
                    });

                } catch (JSONException e) {
                    e.printStackTrace();
                    paymentResult.setValue(new PaymentResult("收款错误-返回数据错误2"));
                }

                Log.i("PAYMENT RESP", responseBody);
            }
        });
    }
}
