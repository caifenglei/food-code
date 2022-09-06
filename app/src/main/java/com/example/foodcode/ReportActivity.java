package com.example.foodcode;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;

import com.example.foodcode.custom.ChartBarMarkerView;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class ReportActivity extends AppCompatActivity {

    private BarChart barChart;
//    private SeekBar seekBarX, seekBarY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        barChart = findViewById(R.id.chart1);
        barChart.setPinchZoom(false);
        barChart.setDrawGridBackground(false);

        //legend
        Legend l = barChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(true);
        l.setYOffset(0f);
        l.setXOffset(10f);
        l.setYEntrySpace(1f);
        l.setXEntrySpace(16f);
        l.setTextSize(16f);

        //xAxis
        XAxis xAxis = barChart.getXAxis();
        xAxis.setCenterAxisLabels(true);
        xAxis.setDrawGridLines(true);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawLabels(true);
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getAxisLabel(float value, AxisBase axis) {
                String[] timeSegments = {"00:00-01:59", "02:00-03:59", "04:00-05:59", "06:00-07:59",
                        "08:00-09:59", "10:00-11:59", "12:00-13:59","14:00-15:59",
                        "16:00-17:59", "18:00-19:59", "20:00-21:59", "22:00-23:59", "xx"};
                int idx = (int) value;
                return timeSegments[idx];
            }
        });
        xAxis.setGranularity(1f);
        xAxis.setAxisMinimum(0);
        xAxis.setAxisMaximum(12f);
        xAxis.setLabelCount(13, true);
        xAxis.setTextSize(12f);
        xAxis.setTextColor(Color.rgb(189, 194, 201));
        xAxis.setAxisLineColor(Color.rgb(189, 194, 201));
        xAxis.setAxisLineWidth(2f);

        //left yAxis
        YAxis leftAxis = barChart.getAxisLeft();
        leftAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getAxisLabel(float value, AxisBase axis) {

                DecimalFormat df = new DecimalFormat("0.00");
                String format = df.format(value);
                return format;
            }
        });
        leftAxis.setDrawGridLines(false);
        leftAxis.setDrawAxisLine(false);
        leftAxis.setSpaceTop(35f);
        leftAxis.setAxisMinimum(0f);
        leftAxis.setTextSize(12f);
        leftAxis.setTextColor(Color.rgb(189, 194, 201));

        //right yAxis
        barChart.getAxisRight().setEnabled(false);

        //marker view
        ChartBarMarkerView mv = new ChartBarMarkerView(this, R.layout.marker_chart_bar);
        mv.setChartView(barChart);
        barChart.setMarker(mv);

        //chart description
        Description description = barChart.getDescription();
        description.setEnabled(false);

        setChartData();
    }

    private void setChartData() {
        float groupSpace = 0.2f;
        float barSpace = 0f;
        int groupCount = 12;

        Float[] baomaAmounts = {0f, 0f, 0f, 402.5f, 100.3f, 10.0f, 890.3f, 201.3f, 0.0f, 599.0f, 50.0f, 0.0f};
        Float[] aiPayAmounts = {0.0f, 0.0f, 0.0f, 102.5f, 10.3f, 0.0f, 90.3f, 21.3f, 0.0f, 59.0f, 0.0f, 0.0f};
        Float[] wechatAmounts = {0.0f, 0.0f, 0.0f, 42.5f, 10.3f, 10.0f, 40.3f, 31.3f, 0.0f, 9.0f, 10.0f, 0.0f};
        Float[] unionPayAmounts = {0.0f, 0.0f, 0.0f, 2.5f, 70.3f, 10.0f, 20.3f, 101.3f, 0.0f, 99.0f, 30.5f, 0.0f};

        List<BarEntry> entriesBaoma = new ArrayList<>();
        List<BarEntry> entriesAliPay = new ArrayList<>();
        List<BarEntry> entriesWechat = new ArrayList<>();
        List<BarEntry> entriesUnionPay = new ArrayList<>();

        for (int i = 0; i < baomaAmounts.length; i++) {
            entriesBaoma.add(new BarEntry(i, baomaAmounts[i]));
            entriesAliPay.add(new BarEntry(i, aiPayAmounts[i]));
            entriesWechat.add(new BarEntry(i, wechatAmounts[i]));
            entriesUnionPay.add(new BarEntry(i, unionPayAmounts[i]));
        }

        BarDataSet baomaIncomeSet = new BarDataSet(entriesBaoma, getString(R.string.pay_channel_baoma));
        baomaIncomeSet.setColor(Color.rgb(255, 201, 35));
        BarDataSet aliIncomeSet = new BarDataSet(entriesAliPay, getString(R.string.pay_channel_ali));
        aliIncomeSet.setColor(Color.rgb(56, 168, 255));
        BarDataSet wechatIncomeSet = new BarDataSet(entriesWechat, getString(R.string.pay_channel_wechat));
        wechatIncomeSet.setColor(Color.rgb(151, 218, 66));
        BarDataSet unionIncomeSet = new BarDataSet(entriesUnionPay, getString(R.string.pay_channel_union));
        unionIncomeSet.setColor(Color.rgb(30, 214, 192));

        BarData data = new BarData(baomaIncomeSet, aliIncomeSet, wechatIncomeSet, unionIncomeSet);
        barChart.setData(data);

        barChart.getBarData().setBarWidth(0.2f);

//        barChart.getXAxis().setAxisMinimum(0);
//        barChart.getXAxis().setAxisMaximum(0 + barChart.getBarData().getGroupWidth(groupSpace, barSpace) * groupCount);
        Log.i("-----max", String.valueOf(0 + barChart.getBarData().getGroupWidth(groupSpace, barSpace) * groupCount));
//        barChart.getXAxis().setAxisMaximum(11);
        barChart.groupBars(0, groupSpace, barSpace);
        barChart.invalidate(); //refresh
    }

//    class XAxisLabelFormatter : ValueFormatter(){
//        @Override
//                public String getAxis
//
//    }
}
