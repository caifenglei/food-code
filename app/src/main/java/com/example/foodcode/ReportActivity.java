package com.example.foodcode;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;

import com.example.foodcode.custom.ChartBarMarkerView;
import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class ReportActivity extends AppCompatActivity {

    private CombinedChart chart;

    private int COLOR_BAOMA = Color.rgb(255, 201, 35);
    private int COLOR_ALI = Color.rgb(56, 168, 255);
    private int COLOR_WECHAT = Color.rgb(151, 218, 66);
    private int COLOR_UNION = Color.rgb(30, 214, 192);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        chart = findViewById(R.id.combinedChart);
        chart.setPinchZoom(false);
        chart.setDrawGridBackground(false);

        //legend
        Legend l = chart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);
        l.setYOffset(0f);
        l.setXOffset(60f);
        l.setYEntrySpace(1f);
        l.setXEntrySpace(16f);
        l.setTextSize(16f);

        //xAxis
        XAxis xAxis = chart.getXAxis();
        xAxis.setCenterAxisLabels(true);
        xAxis.setDrawGridLines(true);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawLabels(true);
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getAxisLabel(float value, AxisBase axis) {
                String[] timeSegments = {"00:00-01:59", "02:00-03:59", "04:00-05:59", "06:00-07:59",
                        "08:00-09:59", "10:00-11:59", "12:00-13:59", "14:00-15:59",
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
        YAxis leftAxis = chart.getAxisLeft();
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
//        barChart.getAxisRight().setEnabled(false);
        YAxis rightAxis = chart.getAxisRight();
        rightAxis.setDrawGridLines(false);
        rightAxis.setAxisMinimum(0f);

        //marker view
        ChartBarMarkerView mv = new ChartBarMarkerView(this, R.layout.marker_chart_bar);
        mv.setChartView(chart);
        chart.setMarker(mv);

        //chart description
        Description description = chart.getDescription();
        description.setEnabled(false);

        CombinedData combinedData = new CombinedData();
        combinedData.setData(setBarChartData());
        combinedData.setData(setLineChartData());

        chart.setData(combinedData);
        chart.invalidate();
    }

    private BarData setBarChartData() {
        float groupSpace = 0.2f;
        float barSpace = 0f;

        Float[] baomaAmounts = {0f, 0f, 0f, 402.5f, 12000.3f, 9800.0f, 890.3f, 201.3f, 0.0f, 1599.0f, 150.0f, 0.0f};
        Float[] aiPayAmounts = {0.0f, 0.0f, 0.0f, 1200.5f, 100.3f, 0.0f, 90.3f, 421.3f, 0.0f, 2259.0f, 0.0f, 0.0f};
        Float[] wechatAmounts = {0.0f, 0.0f, 0.0f, 542.5f, 10.3f, 1087.0f, 40.3f, 31.3f, 0.0f, 9.0f, 10.0f, 0.0f};
        Float[] unionPayAmounts = {0.0f, 0.0f, 0.0f, 2.5f, 7770.3f, 910.0f, 20.3f, 1018.3f, 0.0f, 599.0f, 30.5f, 0.0f};

        //Draw bar
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
        baomaIncomeSet.setColor(COLOR_BAOMA);
        baomaIncomeSet.setDrawValues(false);

        BarDataSet aliIncomeSet = new BarDataSet(entriesAliPay, getString(R.string.pay_channel_ali));
        aliIncomeSet.setColor(COLOR_ALI);
        aliIncomeSet.setDrawValues(false);

        BarDataSet wechatIncomeSet = new BarDataSet(entriesWechat, getString(R.string.pay_channel_wechat));
        wechatIncomeSet.setColor(COLOR_WECHAT);
        wechatIncomeSet.setDrawValues(false);

        BarDataSet unionIncomeSet = new BarDataSet(entriesUnionPay, getString(R.string.pay_channel_union));
        unionIncomeSet.setColor(COLOR_UNION);
        unionIncomeSet.setDrawValues(false);

        BarData barData = new BarData(baomaIncomeSet, aliIncomeSet, wechatIncomeSet, unionIncomeSet);
        barData.setBarWidth(0.2f);
        barData.groupBars(0, groupSpace, barSpace);

        return barData;
    }

    private LineData setLineChartData() {
        int[] baomaOrderNums = {0, 0, 2, 40, 680, 180, 0, 9, 50, 101, 10, 0};
        int[] aiPayOrderNums = {0, 0, 2, 4, 80, 8, 0, 9, 5, 11, 5, 0};
        int[] wechatOrderNums = {0, 0, 2, 14, 60, 32, 0, 9, 0, 1, 2, 0};
        int[] unionPayOrderNums = {0, 0, 2, 22, 30, 10, 0, 9, 5, 7, 0, 0};


        ArrayList<Entry> entriesBaoma = new ArrayList<>();
        ArrayList<Entry> entriesAliPay = new ArrayList<>();
        ArrayList<Entry> entriesWechat = new ArrayList<>();
        ArrayList<Entry> entriesUnionPay = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            entriesBaoma.add(new Entry(i, baomaOrderNums[i]));
            entriesAliPay.add(new Entry(i, aiPayOrderNums[i]));
            entriesWechat.add(new Entry(i, wechatOrderNums[i]));
            entriesUnionPay.add(new Entry(i, unionPayOrderNums[i]));
        }

        LineDataSet baomaSet = new LineDataSet(entriesBaoma, getString(R.string.pay_channel_baoma));
        baomaSet.setAxisDependency(YAxis.AxisDependency.RIGHT);
        baomaSet.setColor(COLOR_BAOMA);
        baomaSet.setDrawValues(false);
        baomaSet.setForm(Legend.LegendForm.LINE);
        baomaSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        baomaSet.setCircleColor(COLOR_BAOMA);

        LineDataSet aliSet = new LineDataSet(entriesAliPay, getString(R.string.pay_channel_ali));
        aliSet.setAxisDependency(YAxis.AxisDependency.RIGHT);
        aliSet.setColor(COLOR_ALI);
        aliSet.setDrawValues(false);
        aliSet.setForm(Legend.LegendForm.LINE);
        aliSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        aliSet.setCircleColor(COLOR_ALI);

        LineDataSet wechatSet = new LineDataSet(entriesWechat, getString(R.string.pay_channel_wechat));
        wechatSet.setAxisDependency(YAxis.AxisDependency.RIGHT);
        wechatSet.setColor(COLOR_WECHAT);
        wechatSet.setDrawValues(false);
        wechatSet.setForm(Legend.LegendForm.LINE);
        wechatSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        wechatSet.setCircleColor(COLOR_WECHAT);

        LineDataSet unionSet = new LineDataSet(entriesUnionPay, getString(R.string.pay_channel_union));
        unionSet.setAxisDependency(YAxis.AxisDependency.RIGHT);
        unionSet.setColor(COLOR_UNION);
        unionSet.setDrawValues(false);
        unionSet.setForm(Legend.LegendForm.LINE);
        unionSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        unionSet.setCircleColor(COLOR_UNION);

        return new LineData(baomaSet, aliSet, wechatSet, unionSet);
    }
}
