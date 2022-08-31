package com.example.foodcode.data.model;

public class ConsumeRecord {

    private String orderNo;
    private String channelName;
    private String orderAmount;
    private String consumeDatetime;
    private String recordStatus;

    public ConsumeRecord(String orderNo, String channelName, String orderAmount, String consumeDatetime, String recordStatus) {
        this.orderNo = orderNo;
        this.channelName = channelName;
        this.orderAmount = orderAmount;
        this.consumeDatetime = consumeDatetime;
        this.recordStatus = recordStatus;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public String getOrderChannel() {
        return channelName;
    }

    public String getOrderAmount() {
        return orderAmount;
    }

    public String getOrderDatetime() {
        return consumeDatetime;
    }

    public String getOrderStatus() {
        return recordStatus;
    }
}
