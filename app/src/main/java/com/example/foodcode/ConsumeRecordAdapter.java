package com.example.foodcode;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.foodcode.data.model.ConsumeRecord;

import java.util.ArrayList;
import java.util.List;

public class ConsumeRecordAdapter extends RecyclerView.Adapter<ConsumeRecordAdapter.ViewHolder> {

    private List<ConsumeRecord> recordList = new ArrayList<>();

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView orderNoTextView;
        private final TextView orderAmountTextView;
        private final TextView orderChannelTextView;
        private final TextView orderDatetimeTextView;
        private final TextView orderStatusTextView;

        public ViewHolder(View v) {
            super(v);

            //bind event listener TODO
            orderNoTextView = v.findViewById(R.id.orderNo);
            orderAmountTextView = v.findViewById(R.id.orderAmount);
            orderChannelTextView = v.findViewById(R.id.orderChannel);
            orderDatetimeTextView = v.findViewById(R.id.orderDatetime);
            orderStatusTextView = v.findViewById(R.id.orderStatus);
        }

        public TextView getOrderNoTextView(){
            return orderNoTextView;
        }

        public TextView getOrderAmountTextView(){
            return orderAmountTextView;
        }

        public TextView getOrderChannelTextView(){
            return orderChannelTextView;
        }

        public TextView getOrderDatetimeTextView(){
            return orderDatetimeTextView;
        }

        public TextView getOrderStatusTextView(){
            return orderStatusTextView;
        }
    }

    public ConsumeRecordAdapter(List<ConsumeRecord> records) {
        recordList = records;
    }

    // 每当 RecyclerView 需要创建新的 ViewHolder 时，它都会调用此方法。此方法会创建并初始化 ViewHolder 及其关联的 View，但不会填充视图的内容，因为 ViewHolder 此时尚未绑定到具体数据
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.content_consume_record, viewGroup, false);

        return new ViewHolder(v);
    }

    // RecyclerView 调用此方法将 ViewHolder 与数据相关联。此方法会提取适当的数据，并使用该数据填充 ViewHolder 的布局
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        ConsumeRecord cr = recordList.get(position);
        viewHolder.getOrderNoTextView().setText(cr.getOrderNo());
        viewHolder.getOrderAmountTextView().setText(cr.getOrderAmount());
        viewHolder.getOrderChannelTextView().setText(cr.getOrderChannel());
        viewHolder.getOrderDatetimeTextView().setText(cr.getOrderDatetime());
        viewHolder.getOrderStatusTextView().setText(cr.getOrderStatus());
    }

    @Override
    public int getItemCount() {
        return recordList.size();
    }
}
