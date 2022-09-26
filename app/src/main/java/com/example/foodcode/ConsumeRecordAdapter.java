package com.example.foodcode;

import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.foodcode.data.model.ConsumeRecord;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConsumeRecordAdapter extends RecyclerView.Adapter<ConsumeRecordAdapter.ViewHolder> {

    private Resources resources;
    private List<ConsumeRecord> recordList = new ArrayList<>();

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView orderNoTextView;
        private final TextView orderAmountTextView;
        private final TextView orderChannelTextView;
        private final TextView orderDatetimeTextView;
        private final TextView orderStatusTextView;
        private ConsumeRecord dataRecord;

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

        public void setDataRecord(ConsumeRecord record){
            dataRecord = record;
        }

        public ConsumeRecord getDataRecord(){
            return dataRecord;
        }
    }

    public ConsumeRecordAdapter(List<ConsumeRecord> records) {
        this.recordList = records;
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

        resources = viewHolder.itemView.getResources();

        ConsumeRecord cr = recordList.get(position);
        viewHolder.setDataRecord(cr);

        viewHolder.getOrderNoTextView().setText(cr.getOrderNo());
        String amountText = resources.getString(R.string.prefix_rmb) + String.valueOf(cr.getOrderAmount());
        viewHolder.getOrderAmountTextView().setText(amountText);
        viewHolder.getOrderChannelTextView().setText(getPaymentText(cr));
        viewHolder.getOrderDatetimeTextView().setText(cr.getOrderTime());
        viewHolder.getOrderStatusTextView().setText(getOrderStatusText(cr.getOrderStatus()));
    }

    @Override
    public int getItemCount() {
        return recordList.size();
    }

    private String getPaymentText(ConsumeRecord cr){

        Map<Integer, String> paymentMethods= new HashMap<Integer, String>();
        paymentMethods.put(ConsumeRecord.PAYMENT_METHOD_BAOMA, resources.getString(R.string.pay_channel_baoma));
        paymentMethods.put(ConsumeRecord.PAYMENT_METHOD_VISIT, resources.getString(R.string.pay_channel_visit));
        paymentMethods.put(ConsumeRecord.PAYMENT_METHOD_PARTNER, resources.getString(R.string.pay_channel_partner));
        paymentMethods.put(ConsumeRecord.PAYMENT_METHOD_WECHAT, resources.getString(R.string.pay_channel_wechat));
        paymentMethods.put(ConsumeRecord.PAYMENT_METHOD_ALIPAY, resources.getString(R.string.pay_channel_ali));
        paymentMethods.put(ConsumeRecord.PAYMENT_METHOD_UNION, resources.getString(R.string.pay_channel_union));

        Map<Integer, String> paymentTypes= new HashMap<Integer, String>();
        paymentTypes.put(ConsumeRecord.PAYMENT_TYPE_FIXED, resources.getString(R.string.pay_type_fixed));
        paymentTypes.put(ConsumeRecord.PAYMENT_TYPE_TEMP, resources.getString(R.string.pay_type_temp));
        paymentTypes.put(ConsumeRecord.PAYMENT_TYPE_COMBINE, resources.getString(R.string.pay_type_combine));
        paymentTypes.put(ConsumeRecord.PAYMENT_TYPE_OTHER, resources.getString(R.string.pay_type_other));

        int paymentMethod = cr.getPaymentMethod();
        StringBuilder textSb = new StringBuilder();
        textSb.append(paymentMethods.get(paymentMethod));
        if(paymentMethod == ConsumeRecord.PAYMENT_METHOD_BAOMA || paymentMethod == ConsumeRecord.PAYMENT_METHOD_VISIT || paymentMethod == ConsumeRecord.PAYMENT_METHOD_PARTNER){
            textSb.append(resources.getString(R.string.left_bracket));
            textSb.append(paymentTypes.get(cr.getPaymentType()));
            textSb.append(resources.getString(R.string.right_bracket));
        }

        return textSb.toString();
    }

    private String getOrderStatusText(int status){
        Map<Integer, String> statusMap = new HashMap<Integer, String>();
        statusMap.put(ConsumeRecord.STATUS_NORMAL, resources.getString(R.string.order_status_normal));
        statusMap.put(ConsumeRecord.STATUS_REFUNDING, resources.getString(R.string.order_status_refunding));
        statusMap.put(ConsumeRecord.STATUS_REFUNDED, resources.getString(R.string.order_status_refunded));
        if(status == ConsumeRecord.STATUS_NORMAL){
            return "";
        }else{
            return statusMap.get(status);
        }
    }
}
