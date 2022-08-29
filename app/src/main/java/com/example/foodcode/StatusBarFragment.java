package com.example.foodcode;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextClock;
import android.widget.TextView;

import com.example.foodcode.databinding.FragmentStatusBarBinding;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class StatusBarFragment extends Fragment {

    private FragmentStatusBarBinding binding;
    private View rootView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

//        TextView textClock = (TextView) getView().findViewById(R.id.textViewDatetime);
//        textClock.setText("某集团某分支某摊点某窗口007");
//        TextClock textClock = (TextClock) getView().findViewById(R.id.textViewTenantName);
//        textClock.setTimeZone("America/Los_Angeles");
//        textClock.setFormat24Hour("yyyy年MM月dd日 EEEE aa HH:mm:ss");

//        String is24HourMode = String.format("is 24 hours mode enabled: %s", )

        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_status_bar, container, false);
//        TextView tenantName = rootView.findViewById(R.id.textViewTenantName);
//        tenantName.setText("某集团某分支某摊点某窗口007");

        return rootView;

//        binding = FragmentStatusBarBinding.inflate(inflater, container, false);
//        binding.textViewTenantName.setText("某集团某分支某摊点某窗口007");
//        binding.textViewDatetime.setTimeZone("Asia/Beijing");
//        binding.textViewDatetime.setTimeZone("America/Los_Angeles");
//        binding.textViewDatetime.setFormat24Hour("yyyy年MM月dd日 EEEE aa HH:mm:ss");
//        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView tenantName = rootView.findViewById(R.id.textViewTenantName);
        tenantName.setText("某集团某分支某摊点某窗口007");
    }
}