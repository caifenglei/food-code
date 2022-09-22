package com.example.foodcode;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.foodcode.data.AuthManager;

import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class StatusBarFragment extends Fragment {

    private View rootView;

    private ImageView logoTitleView;
    private TextView navTitleView;
    private TextView tenantNameView;
    private TextView autoCashierView;
    private ImageView viewChartView;
    private ImageView logoutView;

    private AuthManager authManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        authManager = new AuthManager(getActivity().getApplicationContext());

        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_status_bar, container, false);

        initView(rootView);
        initAction();

        return rootView;
    }

    private void initView(View view) {
        logoTitleView = view.findViewById(R.id.logoTitle);
        navTitleView = view.findViewById(R.id.navTodayReport);
        autoCashierView = view.findViewById(R.id.autoCashierBtn);
        viewChartView = view.findViewById(R.id.viewChartBtn);
        logoutView = view.findViewById(R.id.logoutBtn);
        tenantNameView = view.findViewById(R.id.textViewTenantName);

        tenantNameView.setText(authManager.getTenantName());

        Intent activityIntent = getActivity().getIntent();
        String pageName = activityIntent.getStringExtra("pageName");
        if (Objects.equals(pageName, "report")) {
            navTitleView.setVisibility(View.VISIBLE);
            logoTitleView.setVisibility(View.GONE);
            autoCashierView.setVisibility(View.GONE);
            viewChartView.setVisibility(View.GONE);
        } else {
            navTitleView.setVisibility(View.GONE);
            logoTitleView.setVisibility(View.VISIBLE);
            autoCashierView.setVisibility(View.VISIBLE);
            viewChartView.setVisibility(View.VISIBLE);
        }

    }

    private void initAction() {

        viewChartView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent chartIntent = new Intent(getActivity(), ReportActivity.class);
                chartIntent.putExtra("pageName", "report");
                startActivity(chartIntent);
            }
        });

        navTitleView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent chartIntent = new Intent(getActivity(), CashierActivity.class);
                chartIntent.putExtra("pageName", "cashier");
                startActivity(chartIntent);
            }
        });

        logoutView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                DialogFragment dialogFragment = new ConfirmLogoutDialogFragment();
                dialogFragment.show(getActivity().getSupportFragmentManager(), "confirm_logout");
            }
        });
    }
}