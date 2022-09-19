package com.example.foodcode;

import android.content.Context;
import android.graphics.Canvas;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

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

    private SwipeRefreshLayout swipeRefreshLayout;

    private Context context;
    private AuthManager authManager;

    private final int FIRST_PAGE = 1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = getActivity().getApplicationContext();
        authManager = new AuthManager(context);

//        if (getArguments() != null) {
            // Initialize dataset, this data would usually come from a local content provider or
            // remote server.
//            initRecords();
//        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_consume_records, container, false);

        swipeRefreshLayout = rootView.findViewById(R.id.swipeRefreshLayout);

        recordsRecyclerView = rootView.findViewById(R.id.recyclerView);
        recordsLayoutManager = new LinearLayoutManager(getActivity());
        int scrollPosition = 0;
        recordsRecyclerView.setLayoutManager(recordsLayoutManager);
        recordsRecyclerView.scrollToPosition(scrollPosition);

        //Adapter
        recordsAdapter = new ConsumeRecordAdapter(consumeRecordList);
        recordsRecyclerView.setAdapter(recordsAdapter);

        //Swipe left
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

        //Swipe down (pull down)
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(false);
                refreshRecords();
            }
        });

        recordsRecyclerView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View view, int i, int i1, int i2, int i3) {
                // TODO
                Log.i("OnScroll", String.valueOf(i) + "," +  String.valueOf(i1) + "," +  String.valueOf(i2) + "," +  String.valueOf(i3));
            }
        });

        fetchRecords(FIRST_PAGE);

        return rootView;
    }

    private void refreshRecords(){
        fetchRecords(FIRST_PAGE);
    }

    private void fetchRecords(int pageNum) {

        Map<String, Object> params = new HashMap<>();
        params.put("pageNo", pageNum);
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
                                    List<ConsumeRecord> records = new ArrayList<>();
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
                                        records.add(cr);
                                    }

                                    //Adapter
                                    recordsAdapter = new ConsumeRecordAdapter(records);
                                    recordsRecyclerView.setAdapter(recordsAdapter);

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
    }
}