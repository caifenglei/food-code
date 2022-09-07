package com.example.foodcode.custom;

import android.content.Context;
import android.util.Log;
import android.widget.TextView;

import com.example.foodcode.R;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;
import com.github.mikephil.charting.utils.Utils;

import java.text.DecimalFormat;

public class ChartBarMarkerView extends MarkerView {
    private final TextView tvContent;

    public ChartBarMarkerView(Context context, int layoutResource) {
        super(context, layoutResource);

        tvContent = findViewById(R.id.tvContent);
    }

    // runs every time the MarkerView is redrawn, can be used to update the
    // content (user-interface)
    @Override
    public void refreshContent(Entry e, Highlight highlight) {

        if (e instanceof CandleEntry) {
            CandleEntry ce = (CandleEntry) e;
            tvContent.setText(Utils.formatNumber(ce.getHigh(), 0, true));
        } else if(e instanceof BarEntry){
            DecimalFormat df = new DecimalFormat("0.00");
            String format = df.format(e.getY());
            tvContent.setText("￥" + format);
        }else {
            int value = (int) e.getY();
//            Log.i("highlight", highlight.getAxis().name() + ":" + highlight.getDataIndex());
            tvContent.setText(String.valueOf(value) + "单");
        }

        super.refreshContent(e, highlight);
    }

    @Override
    public MPPointF getOffset() {
        return new MPPointF(-(getWidth() / 2), -getHeight());
    }
}
