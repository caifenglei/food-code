package com.example.foodcode;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.foodcode.data.AuthManager;
import com.example.foodcode.databinding.ActivityMainBinding;
import com.example.foodcode.login.LoginResult;
import com.example.foodcode.login.LoginViewModel;
import com.example.foodcode.login.LoginViewModelFactory;
import com.example.foodcode.present.TextDisplay;
import com.example.foodcode.utils.Helper;
import com.example.foodcode.utils.ScreenManager;
import com.example.foodcode.utils.ToastUtil;

import org.json.JSONException;

import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private LoginViewModel loginViewModel;
    private ActivityMainBinding binding;
    private ProgressBar loadingProgressBar;
    private EditText usernameEditText;
    private EditText passwordEditText;
    private Button loginButton;

    private AuthManager authManager;
    private ScreenManager screenManager = ScreenManager.getInstance();

    TextDisplay miniScreenDisplay = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(binding.getRoot());

        authManager = new AuthManager(getApplicationContext());

        //View Model
        loginViewModel = new ViewModelProvider(this, new LoginViewModelFactory(getApplicationContext())).get(LoginViewModel.class);

        //Form Input
        usernameEditText = binding.textPersonName;
        passwordEditText = binding.textPassword;
        loginButton = binding.loginButton;
        loadingProgressBar = binding.progressBar;



        //Form Login Result Observe
        Activity activity = this;
        loginViewModel.getLoginResult().observe(this, new Observer<LoginResult>() {
            @Override
            public void onChanged(@Nullable LoginResult loginResult) {
                if (loginResult == null) {
                    return;
                }
                if (loginResult.getError() != null) {
                    loadingProgressBar.setVisibility(View.GONE);
                    loginButton.setClickable(true);
                    ToastUtil.show(activity, loginResult.getError());
                } else if (loginResult.getSuccess() != null) {

                    try {
                        Map<String, Object> responseMap = Helper.jsonToMap(loginResult.getSuccess());
                        authManager.setAuthToken((String) responseMap.get("token"));
                        authManager.setTenantName((String) responseMap.get("deviceName"));
                        authManager.setDeviceCode((String) responseMap.get("deviceCode"));
                        authManager.setCashierType((String) responseMap.get("cashierType"));
                        authManager.setCashierAmount(String.valueOf(responseMap.get("amount")));

                        Intent cashierIntent = new Intent(MainActivity.this, CashierActivity.class);
                        startActivity(cashierIntent);

                        //Complete and destroy login activity once successful
                        finish();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        loadingProgressBar.setVisibility(View.GONE);
                        loginButton.setClickable(true);
                    }
                }
            }
        });

        passwordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    handleLogin();
                }
                return false;
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleLogin();
            }
        });

        manageMiniScreen();
    }

    private void handleLogin() {

        String username = usernameEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        if (username.trim().equals("")) {
            usernameEditText.setError(getString(R.string.please_enter_username));
        } else if (password.trim().equals("")) {
            passwordEditText.setError(getString(R.string.please_enter_password));
        } else {
            loadingProgressBar.setVisibility(View.VISIBLE);
            loginButton.setClickable(false);
            loginViewModel.login(username, password);
        }
    }

    private void manageMiniScreen() {
        screenManager.init(this);
//        Display[] displays = screenManager.getDisplays();
//        Log.e("SCREEN", "屏幕数量" + displays.length);
//        for (int i = 0; i < displays.length; i++) {
//            Log.e("SCREEN", "屏幕" + displays[i]);
//        }
        Display display = screenManager.getPresentationDisplays();
        if (display != null) {
            miniScreenDisplay = new TextDisplay(this, display);
            miniScreenDisplay.show();
        }
    }
}