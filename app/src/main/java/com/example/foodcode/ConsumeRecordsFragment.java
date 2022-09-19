package com.example.foodcode;

import android.content.Context;
import android.graphics.Canvas;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.foodcode.data.AuthManager;
import com.example.foodcode.data.PaymentResult;
import com.example.foodcode.data.model.ConsumeRecord;
import com.example.foodcode.utils.HttpClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Response;

/**
 * 消费记录
 */
public class ConsumeRecordsFragment extends Fragment {

    private List<ConsumeRecord> consumeRecordList = new ArrayList<>();
    private RecyclerView recordsRecyclerView;
    private RecyclerView.LayoutManager recordsLayoutManager;
    private ConsumeRecordAdapter recordsAdapter;

    private Context context;
    private AuthManager authManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = getActivity().getApplicationContext();
        authManager = new AuthManager(context);

//        if (getArguments() != null) {
            // Initialize dataset, this data would usually come from a local content provider or
            // remote server.
            initRecords();
//        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_consume_records, container, false);
        recordsRecyclerView = rootView.findViewById(R.id.recyclerView);
        recordsLayoutManager = new LinearLayoutManager(getActivity());
        int scrollPosition = 0;
        recordsRecyclerView.setLayoutManager(recordsLayoutManager);
        recordsRecyclerView.scrollToPosition(scrollPosition);

        //Adapter
        recordsAdapter = new ConsumeRecordAdapter(consumeRecordList);
        recordsRecyclerView.setAdapter(recordsAdapter);

        //Swipe
        ItemSwipeController swipeController = new ItemSwipeController(new SwipeActions() {
            @Override
            public void onRefundClicked(int position) {
                Log.i("swipe clicked position:", String.valueOf(position));
                DialogFragment refundConfirmDialog = new PasswordConfirmDialogFragment();
                refundConfirmDialog.show(getParentFragmentManager(), "refund_dialog");
            }
        });
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(swipeController);
        itemTouchHelper.attachToRecyclerView(recordsRecyclerView);
        recordsRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration(){
            @Override
            public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state){
                swipeController.onDraw(c);
            }
        });

        return rootView;
    }

    private void initRecords() {

        Map<String, Object> params = new HashMap<>();
        params.put("pageNo", 1);
        params.put("pageSize", 10);
        params.put("deviceCode", authManager.getDeviceCode());
        Log.i("START HTTP", "Order List");
        new HttpClient(context).post("/app/merchant/order/list", params, new okhttp3.Callback() {
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

                    //switch to UI main thread
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                if (msgCode.equals("100")) {
                                    JSONObject responseData = responseJson.getJSONObject("responseData");
                                    JSONArray consumeList = responseData.getJSONArray("list");
                                    for(int i = 0; i < consumeList.length(); i++){
                                        JSONObject record = consumeList.getJSONObject(i);
                                        String orderId = record.getString("id");
                                        String orderNo = record.getString("expendOrderNum");
                                        int orderStatus = record.getInt("orderStatus");
                                        int paymentMethod = record.getInt("expendMethod");
                                        int paymentType = record.getInt("quotaType");
                                        Double orderAmount = record.getDouble("amount");
                                        String orderTime = record.getString("expendTime");
                                        ConsumeRecord cr = new ConsumeRecord();
                                        cr.setOrderId(orderId);
                                        cr.setOrderNo(orderNo);
                                        cr.setOrderStatus(orderStatus);
                                        cr.setPaymentMethod(paymentMethod);
                                        cr.setPaymentType(paymentType);
                                        cr.setOrderAmount(orderAmount);
                                        cr.setOrderTime(orderTime);
                                        consumeRecordList.add(cr);
                                    }

                                } else {
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Log.i("CONSUME RESP", responseBody);
            }
        });

//        ConsumeRecord cr1 = new ConsumeRecord("20220513493435433433333", "宝码（临时额度）", "￥18.00", "2022-08-31 11:04:00", "");
//        consumeRecordList.add(cr1);
//
//        ConsumeRecord cr2 = new ConsumeRecord("20220513493435433433301", "支付宝", "￥28.00", "2022-08-31 12:04:00", "");
//        consumeRecordList.add(cr2);
//
//        ConsumeRecord cr3 = new ConsumeRecord("20220513493435433433302", "宝码", "￥18.00", "2022-08-31 13:04:00", "");
//        consumeRecordList.add(cr3);
//
//        ConsumeRecord cr4 = new ConsumeRecord("20220513493435433433303", "微信", "￥18.00", "2022-08-31 14:04:00", "");
//        consumeRecordList.add(cr4);
//
//        ConsumeRecord cr5 = new ConsumeRecord("20220513493435433433304", "宝码（临时额度）", "￥18.00", "2022-08-31 15:04:00", "退款中");
//        consumeRecordList.add(cr5);
//
//        ConsumeRecord cr6 = new ConsumeRecord("20220513493435433433305", "宝码（临时额度）", "￥18.00", "2022-08-31 16:04:00", "已退款");
//        consumeRecordList.add(cr6);
//
//        ConsumeRecord cr7 = new ConsumeRecord("20220513493435433433306", "宝码（临时额度）", "￥18.00", "2022-08-31 17:04:00", "");
//        consumeRecordList.add(cr7);
    }
}