package com.example.foodcode;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.foodcode.data.AuthManager;
import com.example.foodcode.data.model.ConsumeRecord;
import com.example.foodcode.utils.HttpClient;
import com.example.foodcode.utils.ToastUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Response;

/**
 * refund password confirm dialog
 */
public class PasswordConfirmDialogFragment extends DialogFragment implements View.OnClickListener {

    private Button confirmButton;
    private Button cancelButton;
    private TextView refundTitle;
    private EditText passwordEditText;
    private ProgressBar progressBar;

    private ConsumeRecord record;
    private String refundOrderNo;
    private String refundOrderAmount;
    private int adapterPosition;

    private Context context;
    private AuthManager authManager;

    public PasswordConfirmDialogFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_refund_confirm, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        context = getActivity().getApplicationContext();
        authManager = new AuthManager(context);

        initView(view);
        initAction();
        initData();
    }

    private void initView(View view) {
        confirmButton = view.findViewById(R.id.confirmRefundBtn);
        cancelButton = view.findViewById(R.id.cancelRefundBtn);
        refundTitle = view.findViewById(R.id.cashierText);
        passwordEditText = view.findViewById(R.id.confirmPassword);
        progressBar = view.findViewById(R.id.progressRefund);
    }

    private void initAction() {
        confirmButton.setOnClickListener(this);
        cancelButton.setOnClickListener(this);
    }

    private void initData() {
        Bundle bundle = getArguments();
        record = (ConsumeRecord) bundle.getSerializable("consumeRecord");
        refundOrderNo = bundle.getString("orderNo");
        refundOrderAmount = bundle.getString("orderAmount");
        adapterPosition = bundle.getInt("adapterPosition");

        refundTitle.setText(getString(R.string.refund_confirm_title, refundOrderAmount));
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.confirmRefundBtn) {

            String pwd = passwordEditText.getText().toString();
            if (pwd.equals("")) {
                Toast.makeText(context, R.string.refund_empty_password, Toast.LENGTH_SHORT).show();
            } else {
                progressBar.setVisibility(View.VISIBLE);
                confirmButton.setClickable(false);
                cancelButton.setClickable(false);
                postToRefund(pwd);
            }

        } else {
            dismiss();
        }
    }

    private void postToRefund(String password) {

        Map<String, Object> params = new HashMap<>();
        params.put("expendOrderNum", refundOrderNo);
        params.put("password", password);
        new HttpClient(context).post("/app/merchant/payment/refund", params, new okhttp3.Callback() {
            @Override
            public void onFailure(@NonNull Call call, IOException e) {
                Log.e("RECEIVE", "Failure", e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseBody = response.body().string();
                try {
                    final JSONObject responseJson = new JSONObject(responseBody);
                    String msgCode = responseJson.getString("msgCode");
                    String message = responseJson.getString("message");

                    //switch to UI main thread
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            if (msgCode.equals("100")) {
                                // update consume record TODO
                                try {
                                    JSONObject responseData = responseJson.getJSONObject("responseData");
                                    record.setOrderStatus(responseData.getInt("orderStatus"));
                                    Bundle bundle = new Bundle();
                                    bundle.putInt("position", adapterPosition);
                                    bundle.putSerializable("consume", record);
                                    getParentFragmentManager().setFragmentResult("consumeRefunded", bundle);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                dismiss();
                            } else {
                                ToastUtil.show(context, message);
                            }
                            progressBar.setVisibility(View.GONE);
                            confirmButton.setClickable(true);
                            cancelButton.setClickable(true);
                        }
                    });

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.i("RESPONSE", responseBody);
            }
        });
    }


//    @Override
//    public Dialog onCreateDialog(Bundle savedInstanceState){
//        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//
//        LayoutInflater inflater = requireActivity().getLayoutInflater();
//        View contentView = inflater.inflate(R.layout.dialog_refund_confirm, null);
//        builder.setView(contentView);
//
//        Button confirmButton = contentView.findViewById(R.id.confirmRefundBtn);
//        confirmButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Log.i("click", "confirm");
//            }
//        });
//
//        Button cancelButton = contentView.findViewById(R.id.cancelRefundBtn);
//        cancelButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                PasswordConfirmDialogFragment.this.getDialog().cancel();
//            }
//        });
//
//        return builder.create();
//    }

}