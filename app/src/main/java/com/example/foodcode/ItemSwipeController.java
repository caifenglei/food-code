package com.example.foodcode;

import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import static androidx.recyclerview.widget.ItemTouchHelper.*;

import android.annotation.SuppressLint;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.example.foodcode.data.model.ConsumeRecord;

import java.text.SimpleDateFormat;
import java.util.Date;

enum SwipeState {GONE, RIGHT_VISIBLE}

public class ItemSwipeController extends ItemTouchHelper.Callback {

    // define is action is swipe back
    private Boolean swipeBack = false;
    private static final float refundButtonWidth = 104;
    private SwipeState swipeShownState = SwipeState.GONE;
    private SwipeActions swipeActions = null;
    private RectF buttonInstance;
    private RecyclerView.ViewHolder currentItemViewHolder = null;

    public ItemSwipeController(SwipeActions actions){
        this.swipeActions = actions;
    }

    //tells helper what kind of actions RecyclerView should handle
    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
//
        ConsumeRecordAdapter.ViewHolder adapterViewHolder = (ConsumeRecordAdapter.ViewHolder) viewHolder;
        ConsumeRecord cr = adapterViewHolder.getDataRecord();
        int status = cr.getOrderStatus();
        int paymentType = cr.getPaymentType();
        String orderTime = cr.getOrderTime();

        //Disabled case
        Log.i("STATUS_PAY", String.valueOf(status) + "," + String.valueOf(paymentType));
        //退款中，已退款，以及非当前月，不可退款
        if(status == ConsumeRecord.STATUS_REFUNDING || status == ConsumeRecord.STATUS_REFUNDED
//                || paymentType == ConsumeRecord.PAYMENT_TYPE_TEMP || paymentType == ConsumeRecord.PAYMENT_TYPE_COMBINE
            || !isInCurrentMonth(orderTime)){
            return makeMovementFlags(0, 0);
        }else{
            return makeMovementFlags(0, LEFT);
        }
    }

    private boolean isInCurrentMonth(String orderTime){
        SimpleDateFormat monthFormat = new SimpleDateFormat("yyyy-MM");
        String thisMonth = monthFormat.format(new Date());
        String orderMonth = orderTime.substring(0, 7);
        Log.i("DATE", thisMonth + "," + orderMonth);
        return thisMonth.equals(orderMonth);
    }

    //what to do on given actions: move
    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        return false;
    }

    //what to do on given actions: swipe
    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

    }

    // block swipe somehow
    @Override
    public int convertToAbsoluteDirection(int flags, int layoutDirection) {
        if (swipeBack) {
            swipeBack = swipeShownState != SwipeState.GONE;;
            return 0;
        }
        return super.convertToAbsoluteDirection(flags, layoutDirection);
    }

    public void onDraw(Canvas c){
        if(currentItemViewHolder != null){
            drawSwipeButtons(c, currentItemViewHolder);
        }
    }

    // draw child event handler
    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        if (actionState == ACTION_STATE_SWIPE) {
            if(swipeShownState != SwipeState.GONE){
                if(swipeShownState == SwipeState.RIGHT_VISIBLE){
                    dX = Math.min(dX, -refundButtonWidth);
                }
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }else {
                setTouchListener(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        }

        if(swipeShownState == SwipeState.GONE){
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }

        currentItemViewHolder = viewHolder;
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setTouchListener(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

        recyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                Log.i("set touch listener...", String.valueOf(dX) + "<-dx, event action ->" + String.valueOf(event.getAction()));
                swipeBack = event.getAction() == MotionEvent.ACTION_CANCEL || event.getAction() == MotionEvent.ACTION_UP;
                if (swipeBack) {
                    //move enough to set shown state
                    if (dX < -refundButtonWidth) {
                        swipeShownState = SwipeState.RIGHT_VISIBLE;
                    }

                    // swipe visible
                    if (swipeShownState != SwipeState.GONE) {
                        setTouchDownListener(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                        setItemsClickable(recyclerView, false);
                    }
                }
                return false;
            }
        });
    }

    //simulate click on RecyclerView
    @SuppressLint("ClickableViewAccessibility")
    private void setTouchDownListener(final Canvas c, final RecyclerView recyclerView, final RecyclerView.ViewHolder viewHolder, final float dX, final float dY, final int actionState, final boolean isCurrentActive) {

        recyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    setTouchUpListener(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentActive);
                }
                return false;
            }
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setTouchUpListener(final Canvas c, final RecyclerView recyclerView, final RecyclerView.ViewHolder viewHolder, final float dX, final float dY, final int actionState, final boolean isCurrentlyActive) {
        recyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    ItemSwipeController.super.onChildDraw(c, recyclerView, viewHolder, 0F, dY, actionState, isCurrentlyActive);
                    recyclerView.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            return false;
                        }
                    });
                    setItemsClickable(recyclerView, true);
                    swipeBack = false;

                    if (buttonInstance != null && buttonInstance.contains(event.getX(), event.getY())) {
                        if (swipeShownState == SwipeState.RIGHT_VISIBLE) {
                            // 触发退款
                            ConsumeRecordAdapter.ViewHolder adapterViewHolder = (ConsumeRecordAdapter.ViewHolder) viewHolder;
                            ConsumeRecord cr = adapterViewHolder.getDataRecord();
                            swipeActions.onRefundClicked(cr, viewHolder.getAdapterPosition());
                        }
                    }

                    swipeShownState = SwipeState.GONE;
                    currentItemViewHolder = null;
                }
                return false;
            }
        });
    }

    private void setItemsClickable(RecyclerView recyclerView, boolean isClickable){
        for(int i = 0; i < recyclerView.getChildCount(); ++i){
            recyclerView.getChildAt(i).setClickable(isClickable);
        }
    }

    private void drawSwipeButtons(Canvas c, RecyclerView.ViewHolder viewHolder){

        View itemView = viewHolder.itemView;
        Paint p = new Paint();
        float buttonWidthWithoutPadding = refundButtonWidth - 20;
        float corners = 0;

        RectF rightButton = new RectF(itemView.getRight() - buttonWidthWithoutPadding, itemView.getTop(), itemView.getRight(), itemView.getBottom());
        p.setColor(Color.RED);
        c.drawRoundRect(rightButton, corners, corners, p);
        drawText("退款", c, rightButton, p);
        buttonInstance = null;
        if(swipeShownState == SwipeState.RIGHT_VISIBLE){
            buttonInstance = rightButton;
        }
    }

    private void drawText(String text, Canvas c, RectF button, Paint p) {
        float textSize = 16;
        p.setColor(Color.WHITE);
        p.setAntiAlias(true);
        p.setTextSize(textSize);

        float textWidth = p.measureText(text);
        c.drawText(text, button.centerX()-(textWidth/2), button.centerY()+(textSize/2), p);
    }
}
