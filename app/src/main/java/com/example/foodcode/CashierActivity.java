package com.example.foodcode;

import android.os.Bundle;


import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;

import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.foodcode.databinding.ActivityCashierBinding;

public class CashierActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityCashierBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        binding = ActivityCashierBinding.inflate(getLayoutInflater());
//        setContentView(binding.getRoot());
        setContentView(R.layout.activity_cashier);

        if(savedInstanceState == null){
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            ConsumeRecordsFragment consumeRecordsFragment = new ConsumeRecordsFragment();
            CalculatorFragment calculatorFragment = new CalculatorFragment();
            transaction.replace(R.id.consumeRecordsFrame, consumeRecordsFragment);
            transaction.replace(R.id.calculatorFrame, calculatorFragment);
            transaction.commit();
        }

//        TextView tenantName = findViewById(R.id.textViewTenantName);
//        tenantName.setText(authManager.getTenantName());
//        setSupportActionBar(binding.toolbar);

//        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_cashier);
//        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
//        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

//        binding.fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_cashier);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }
}