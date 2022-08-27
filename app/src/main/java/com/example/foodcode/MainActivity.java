package com.example.foodcode;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.example.foodcode.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(binding.getRoot());
//        setContentView(R.layout.activity_main);

//        final Button loginButton = binding.loginButton;
//        Button btn1=(Button)findViewById(R.id.loginButton);

        binding.loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent cashierIntent = new Intent(MainActivity.this, CashierActivity.class);
                startActivity(cashierIntent);
            }
        });
    }
}