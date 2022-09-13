package com.example.foodcode;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.datastore.preferences.core.Preferences;
import androidx.datastore.preferences.rxjava2.RxPreferenceDataStoreBuilder;
import androidx.datastore.rxjava2.RxDataStore;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

//import com.androidnetworking.AndroidNetworking;
import com.example.foodcode.data.AuthManager;
import com.example.foodcode.databinding.ActivityMainBinding;
import com.example.foodcode.login.LoginFormState;
import com.example.foodcode.login.LoginResult;
import com.example.foodcode.login.LoginViewModel;
import com.example.foodcode.login.LoginViewModelFactory;
import com.example.foodcode.utils.Helper;

import org.json.JSONException;

import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private LoginViewModel loginViewModel;
    private ActivityMainBinding binding;

    private AuthManager authManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(binding.getRoot());

        authManager = new AuthManager(getApplicationContext());

        //View Model
//        RxDataStore<Preferences> dataStore = new RxPreferenceDataStoreBuilder(getApplicationContext(), "settings").build();
        loginViewModel = new ViewModelProvider(this, new LoginViewModelFactory()).get(LoginViewModel.class);

        //Form Input
        final EditText usernameEditText = binding.textPersonName;
        final EditText passwordEditText = binding.textPassword;
        final Button loginButton = binding.loginButton;
        final ProgressBar loadingProgressBar = binding.progressBar;

        //Form Validation
        loginViewModel.getLoginFormState().observe(this, new Observer<LoginFormState>() {
            @Override
            public void onChanged(@Nullable LoginFormState loginFormState) {
                if (loginFormState == null) {
                    return;
                }
                loginButton.setEnabled(loginFormState.isDataValid());
                if (loginFormState.getUsernameError() != null) {
                    usernameEditText.setError(getString(loginFormState.getUsernameError()));
                }
                if (loginFormState.getPasswordError() != null) {
                    passwordEditText.setError(getString(loginFormState.getPasswordError()));
                }
            }
        });


        //Form Login Result Observe
        loginViewModel.getLoginResult().observe(this, new Observer<LoginResult>() {
            @Override
            public void onChanged(@Nullable LoginResult loginResult) {
                if (loginResult == null) {
                    return;
                }
                loadingProgressBar.setVisibility(View.GONE);
                if (loginResult.getError() != null) {
                    showLoginFailed(loginResult.getError());
                } else if (loginResult.getSuccess() != null) {

                    try {
                        Map<String, Object> responseMap = Helper.jsonToMap(loginResult.getSuccess());
                        authManager.setAuthToken((String) responseMap.get("token"));
                        Log.i("SET_TENANT", (String) responseMap.get("deviceName"));
                        authManager.setTenantName((String) responseMap.get("deviceName"));

                        Intent cashierIntent = new Intent(MainActivity.this, CashierActivity.class);
                        startActivity(cashierIntent);

                        finish();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

//                    updateUiWithUser(loginResult.getSuccess());
                }
//                setResult(Activity.RESULT_OK);

                //Complete and destroy login activity once successful
//                finish();
            }
        });

        //Form Data Process
        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ignore
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ignore
            }

            @Override
            public void afterTextChanged(Editable s) {
                loginViewModel.loginDataChanged(usernameEditText.getText().toString(),
                        passwordEditText.getText().toString());
            }
        };
        usernameEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    loginViewModel.login(usernameEditText.getText().toString(),
                            passwordEditText.getText().toString());
                }
                return false;
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingProgressBar.setVisibility(View.VISIBLE);
                loginViewModel.login(usernameEditText.getText().toString(), passwordEditText.getText().toString());
            }
        });


//        binding.loginButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent cashierIntent = new Intent(MainActivity.this, CashierActivity.class);
//                startActivity(cashierIntent);
//            }
//        });
    }

    private void showLoginFailed(String errorString) {
        Toast.makeText(getApplicationContext(), errorString, Toast.LENGTH_SHORT).show();
    }
}