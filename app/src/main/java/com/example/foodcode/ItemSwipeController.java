package com.example.foodcode;

import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import static androidx.recyclerview.widget.ItemTouchHelper.*;

public class ItemSwipeController extends ItemTouchHelper.Callback{

    //tells helper what kind of actions RecyclerView should handle
    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder){
        return makeMovementFlags(0, LEFT);
    }

    //what to do on given actions: move
    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target){
        return false;
    }

    //what to do on given actions: swipe
    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction){

    }
}
