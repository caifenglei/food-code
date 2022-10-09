package com.example.foodcode;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;
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
import com.example.foodcode.utils.ToastUtil;

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
    private LinearLayoutManager recordsLayoutManager;
    private ConsumeRecordAdapter recordsAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;

    private Activity activity;
    private Context context;
    private AuthManager authManager;

    private int pageIndex = 1;
    // 可见的item数
    private int visibleItemCount;
    // 第一个可见的item index
    private int firstVisibleItem;
    // 已加载的总item数
    private int totalRenderItemCount;
    // 数据库中的总数
    private int totalItemCount;
    private boolean loadingMore = false;
    private boolean itemAllLoaded = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activity = getActivity();
        context = activity.getApplicationContext();
        authManager = new AuthManager(context);

        getParentFragmentManager().setFragmentResultListener("moneyPaid", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle bundle) {
                ConsumeRecord consumeRecord = (ConsumeRecord) bundle.getSerializable("record");
                Log.i("LISTEN-CASHIER", String.valueOf(consumeRecord.getOrderAmount()));
                consumeRecordList.add(0, consumeRecord);
                totalItemCount++;
                recordsAdapter.notifyItemInserted(0);
                recordsRecyclerView.scrollToPosition(0);
            }
        });

        getParentFragmentManager().setFragmentResultListener("consumeRefunded", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle bundle) {
                ConsumeRecord consumeRecord = (ConsumeRecord) bundle.getSerializable("consume");
                int adapterPosition = bundle.getInt("position");
                Log.i("LISTEN-REFUND", String.valueOf(consumeRecord.getOrderAmount()));
                consumeRecordList.set(adapterPosition, consumeRecord);
                recordsAdapter.notifyItemChanged(adapterPosition);
            }
        });
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
            public void onRefundClicked(ConsumeRecord record, int position) {
                DialogFragment refundConfirmDialog = new PasswordConfirmDialogFragment();
                Bundle dialogBundle = new Bundle();
                dialogBundle.putSerializable("consumeRecord", record);
                dialogBundle.putInt("adapterPosition", position);
                dialogBundle.putString("orderNo", record.getOrderNo());
                dialogBundle.putString("orderAmount", String.valueOf(record.getOrderAmount()));
                refundConfirmDialog.setArguments(dialogBundle);
                refundConfirmDialog.show(getParentFragmentManager(), "refund_dialog");
            }
        });
        // Draw refund button
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

        // Swipe pull up (load more)
        recordsRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                visibleItemCount = recyclerView.getChildCount();
                totalRenderItemCount = recordsLayoutManager.getItemCount();
                firstVisibleItem = recordsLayoutManager.findFirstVisibleItemPosition();

                //TODO
                if(!loadingMore && !itemAllLoaded){
                    if(firstVisibleItem + visibleItemCount >= totalRenderItemCount){
                       pageIndex++;
                       loadingMore = true;
                       fetchRecords();
                    }
                }

                Log.i("COUNT",  "First:" + String.valueOf(firstVisibleItem) + ", Visible:" + String.valueOf(visibleItemCount) + ", Total:" + String.valueOf(totalItemCount));
            }
        });

        fetchRecords();

        return rootView;
    }

    private void refreshRecords(){
        pageIndex = 1;
        fetchRecords();
    }

    private void fetchRecords() {

        Map<String, Object> params = new HashMap<>();
        params.put("pageNo", pageIndex);
        params.put("pageSize", 10);
        params.put("deviceCode", authManager.getDeviceCode());
        new HttpClient(context).post("app/merchant/order/list", params, new okhttp3.Callback() {
            @Override
            public void onFailure(@NonNull Call call, IOException e) {
                Log.e("RECEIVE", "Failure", e);
                ToastUtil.show(activity, "获取收款列表失败");
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
                                    if(pageIndex == 1){
                                        consumeRecordList.clear();
                                    }
                                    int currentSize = consumeList.length();
                                    for(int i = 0; i < currentSize; i++){
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

                                    totalItemCount = responseData.getInt("totalRecords");
                                    if(totalRenderItemCount >= totalItemCount){
                                        itemAllLoaded = true;
                                    }

                                    Log.i("RECORD SIZE", String.valueOf(currentSize) + "," + String.valueOf(consumeRecordList.size()));
                                    recordsAdapter.notifyItemRangeChanged(consumeRecordList.size(), currentSize);
                                }

                                loadingMore = false;
                            } catch (JSONException e) {
                                e.printStackTrace();
                                ToastUtil.show(activity, "列表解析失败-返回数据错误1");
                            }
                        }
                    });

                } catch (JSONException e) {
                    e.printStackTrace();
                    ToastUtil.show(activity, "列表解析失败-返回数据错误2");
                }

                Log.i("CONSUME RESP", responseBody);
            }
        });
    }
}