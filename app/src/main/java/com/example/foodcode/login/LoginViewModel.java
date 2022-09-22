package com.example.foodcode.login;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.util.Patterns;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.foodcode.MainActivity;
import com.example.foodcode.R;
import com.example.foodcode.data.AuthManager;
import com.example.foodcode.data.LoginRepository;
import com.example.foodcode.data.Result;
import com.example.foodcode.data.model.LoggedInUser;
import com.example.foodcode.utils.Helper;
import com.example.foodcode.utils.HttpClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Response;

public class LoginViewModel extends ViewModel {

    private static final String TAG = "---LoginViewModel:";
    private MutableLiveData<LoginFormState> loginFormState = new MutableLiveData<>();
    private MutableLiveData<LoginResult> loginResult = new MutableLiveData<>();
    private LoginRepository loginRepository;

    private AuthManager authManager;
    private Context context;

    LoginViewModel(LoginRepository loginRepository, Context context) {
        this.loginRepository = loginRepository;
        this.context = context;
    }

    public LiveData<LoginFormState> getLoginFormState() {
        return loginFormState;
    }

    public LiveData<LoginResult> getLoginResult() {
        return loginResult;
    }

    public void login(String username, String password) {

        // new try
        Map<String, Object> params = new HashMap<>();
        params.put("account", username);
        params.put("password", password);
        new HttpClient(context).post("/app/merchant/account/login", params, new okhttp3.Callback(){
            @Override
            public void onFailure(@NonNull Call call, IOException e){
                Log.e("LOGIN", "Failure", e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException{
                String responseBody = response.body().string();
                try {
                    final JSONObject responseJson = new JSONObject(responseBody);
                    String msgCode = responseJson.getString("msgCode");

                    //switch to UI main thread
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            if(msgCode.equals("100")){

                                try {
                                    loginResult.setValue(new LoginResult(responseJson.getJSONObject("responseData")));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }else{
                                try {
                                    loginResult.setValue(new LoginResult(responseJson.getString("message")));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Log.i("LOGIN RESP", responseBody);
            }
        });
    }

    public void loginDataChanged(String username, String password) {
        if (!isUserNameValid(username)) {
            loginFormState.setValue(new LoginFormState(R.string.invalid_username, null));
        } else if (!isPasswordValid(password)) {
            loginFormState.setValue(new LoginFormState(null, R.string.invalid_password));
        } else {
            loginFormState.setValue(new LoginFormState(true));
        }
    }

    // A placeholder username validation check
    private boolean isUserNameValid(String username) {
        if (username == null) {
            return false;
        }
        if (username.contains("@")) {
            return Patterns.EMAIL_ADDRESS.matcher(username).matches();
        } else {
            return !username.trim().isEmpty();
        }
    }

    // A placeholder password validation check
    private boolean isPasswordValid(String password) {
        return password != null && password.trim().length() > 5;
    }
}