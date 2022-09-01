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

enum SwipeState {GONE, RIGHT_VISIBLE}

public class ItemSwipeController extends ItemTouchHelper.Callback {

    // define is action is swipe back
    private Boolean swipeBack = false;
    private static final float refundButtonWidth = 84;
    private SwipeState swipeShownState = SwipeState.GONE;
    private RectF buttonInstance;
    private RecyclerView.ViewHolder currentItemViewHolder = null;


    //tells helper what kind of actions RecyclerView should handle
    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        return makeMovementFlags(0, LEFT);
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
            swipeBack = false;
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

//                    if (buttonInstance != null && buttonInstance.contains(event.getX(), event.getY())) {
//                        if (buttonShowedState == ButtonsState.RIGHT_VISIBLE) {
//                            buttonsActions.onRightClicked(viewHolder.getAdapterPosition());
//                        }
//                    }

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
        float corners = 0;

        RectF rightButton = new RectF(itemView.getRight() - refundButtonWidth, itemView.getTop(), itemView.getRight(), itemView.getBottom());
        p.setColor(Color.RED);
        c.drawRoundRect(rightButton, corners, corners, p);
        drawText("退款", c, rightButton, p);
        buttonInstance = null;
        if(swipeShownState == SwipeState.RIGHT_VISIBLE){
            buttonInstance = rightButton;
        }
    }

    private void drawText(String text, Canvas c, RectF button, Paint p) {
        float textSize = 60;
        p.setColor(Color.WHITE);
        p.setAntiAlias(true);
        p.setTextSize(textSize);

        float textWidth = p.measureText(text);
        c.drawText(text, button.centerX()-(textWidth/2), button.centerY()+(textSize/2), p);
    }
}
