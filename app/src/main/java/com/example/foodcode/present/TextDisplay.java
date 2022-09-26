package com.example.foodcode.present;

import android.content.Context;
import android.graphics.Outline;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.foodcode.R;
import com.example.foodcode.utils.ScreenManager;
//import com.sunmi.sunmit2demo.R;
//import com.sunmi.sunmit2demo.dialog.PayDialog;
//import com.sunmi.sunmit2demo.utils.ResourcesUtils;
//import com.sunmi.sunmit2demo.utils.SharePreferenceUtil;

/**
 * Created by highsixty on 2018/3/23.
 * mail  gaolulin@sunmi.com
 */

public class TextDisplay extends BasePresentation {

    private ConstraintLayout root;
    private TextView payAmountText;
    private TextView payAmountUnitText;

    public int state;

    public TextDisplay(Context outerContext, Display display) {
        super(outerContext, display);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.screen_mini_present);

        root = (ConstraintLayout) findViewById(R.id.miniScreenRoot);
        payAmountText = (TextView) findViewById(R.id.payAmount);
        payAmountUnitText = (TextView) findViewById(R.id.payAmountUnit);

        root.setClipToOutline(true);
        root.setOutlineProvider(new ViewOutlineProvider() {
            @Override
            public void getOutline(View view, Outline outline) {
                outline.setRoundRect(0, 0, view.getWidth(), view.getHeight(), 0);
            }
        });
    }


    public void showPay(String money) {
        root.setBackgroundResource(R.drawable.bg_prepare_scan);
        payAmountUnitText.setVisibility(View.VISIBLE);
        payAmountText.setText(money);
        payAmountText.setVisibility(View.VISIBLE);
    }

    public void hidePay() {
        root.setBackgroundResource(R.drawable.bg_mini_screen);
        payAmountText.setVisibility(View.GONE);
        payAmountUnitText.setVisibility(View.GONE);
    }

    @Override
    public void show() {
        super.show();

        root.setBackgroundResource(R.drawable.bg_mini_screen);
    }

    @Override
    public void onSelect(boolean isShow) {

    }
}
