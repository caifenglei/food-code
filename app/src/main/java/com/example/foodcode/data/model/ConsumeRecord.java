package com.example.foodcode.data.model;

import android.util.Log;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class ConsumeRecord implements Serializable {

    private String orderId;
    private String orderNo;
    private int paymentMethod;
    private int paymentType;
    private BigDecimal orderAmount;
    private String orderTime;
    private int orderStatus;

    public static final int STATUS_NORMAL = 0;
    public static final int STATUS_REFUNDING = 1;
    public static final int STATUS_REFUNDED = 2;

    public static final int PAYMENT_METHOD_BAOMA = 1;
    public static final int PAYMENT_METHOD_VISIT = 2;
    public static final int PAYMENT_METHOD_PARTNER = 3;
    public static final int PAYMENT_METHOD_WECHAT = 4;
    public static final int PAYMENT_METHOD_ALIPAY = 5;
    public static final int PAYMENT_METHOD_UNION = 6;

    public static final int PAYMENT_TYPE_FIXED = 1;
    public static final int PAYMENT_TYPE_TEMP = 2;
    public static final int PAYMENT_TYPE_COMBINE = 3;

    public ConsumeRecord() {
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderStatus(int status) {
        this.orderStatus = status;
    }

    public int getOrderStatus() {
        return orderStatus;
    }

    public void setPaymentMethod(int method) {
        this.paymentMethod = method;
    }

    public int getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentType(int type) {
        this.paymentType = type;
    }

    public int getPaymentType() {
        return paymentType;
    }

    public void setOrderAmount(Double amount) {
        orderAmount = new BigDecimal(amount).setScale(2, RoundingMode.HALF_DOWN);
    }

    public BigDecimal getOrderAmount() {
        return orderAmount;
    }

    public void setOrderTime(String time) {
        this.orderTime = time;
    }

    public String getOrderTime() {
        return orderTime;
    }


}
