package com.example.foodcode;

import android.graphics.Canvas;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.foodcode.data.model.ConsumeRecord;

import java.util.ArrayList;
import java.util.List;

/**
 * 消费记录
 */
public class ConsumeRecordsFragment extends Fragment {

    private List<ConsumeRecord> consumeRecordList = new ArrayList<>();
    private RecyclerView recordsRecyclerView;
    private RecyclerView.LayoutManager recordsLayoutManager;
    private ConsumeRecordAdapter recordsAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

        ConsumeRecord cr1 = new ConsumeRecord("20220513493435433433333", "宝码（临时额度）", "￥18.00", "2022-08-31 11:04:00", "");
        consumeRecordList.add(cr1);

        ConsumeRecord cr2 = new ConsumeRecord("20220513493435433433301", "支付宝", "￥28.00", "2022-08-31 12:04:00", "");
        consumeRecordList.add(cr2);

        ConsumeRecord cr3 = new ConsumeRecord("20220513493435433433302", "宝码", "￥18.00", "2022-08-31 13:04:00", "");
        consumeRecordList.add(cr3);

        ConsumeRecord cr4 = new ConsumeRecord("20220513493435433433303", "微信", "￥18.00", "2022-08-31 14:04:00", "");
        consumeRecordList.add(cr4);

        ConsumeRecord cr5 = new ConsumeRecord("20220513493435433433304", "宝码（临时额度）", "￥18.00", "2022-08-31 15:04:00", "退款中");
        consumeRecordList.add(cr5);

        ConsumeRecord cr6 = new ConsumeRecord("20220513493435433433305", "宝码（临时额度）", "￥18.00", "2022-08-31 16:04:00", "已退款");
        consumeRecordList.add(cr6);

        ConsumeRecord cr7 = new ConsumeRecord("20220513493435433433306", "宝码（临时额度）", "￥18.00", "2022-08-31 17:04:00", "");
        consumeRecordList.add(cr7);
    }
}