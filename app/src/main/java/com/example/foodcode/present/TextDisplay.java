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
    private TextView payTipText;
    private ImageView payBgImageView;

//    private LinearLayout llPresentChoosePayMode;
//    private LinearLayout llPresentInfo;
//    private TextView tvPaySuccess;
//    private TextView paymodeOne;
//    private TextView paymodeTwo;
//    private TextView paymodeThree;
//    private ImageView ivTitle;
//    private ProgressBar presentProgress;
//
//
//    private LinearLayout llPresentPayFail;
//    private TextView presentFailOne;
//    private TextView presentFailTwo;
//    private TextView presentFailThree;
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
        payBgImageView = (ImageView) findViewById(R.id.payBgImage);
        payTipText = (TextView) findViewById(R.id.showPayCodeTip);

        root.setClipToOutline(true);
        root.setOutlineProvider(new ViewOutlineProvider() {
            @Override
            public void getOutline(View view, Outline outline) {
                outline.setRoundRect(0, 0, view.getWidth(), view.getHeight(), 15);
            }
        });


    }


    public void showPay(String money) {
        payBgImageView.setVisibility(View.VISIBLE);
        payTipText.setVisibility(View.VISIBLE);
        payAmountText.setText(getResources().getString(R.string.unit_RMB) + money);
        payAmountText.setVisibility(View.VISIBLE);
    }

    public void hidePay() {
        payBgImageView.setVisibility(View.GONE);
        payAmountText.setVisibility(View.GONE);
        payTipText.setVisibility(View.GONE);
    }


    public void update(String tip, final int state) {
//        this.state = state;
//        String unit = ResourcesUtils.getString(R.string.units_money_units);
//        String[] strings = tip.split(unit);
//        if(unit.equals("$")){
//            strings = tip.split("\\$");
//        }
//        llPresentPayFail.setVisibility(View.GONE);
//        presentProgress.setVisibility(View.GONE);
//        switch (state) {
//            case 0:
//                llPresentInfo.setVisibility(View.VISIBLE);
//                tvPaySuccess.setVisibility(View.GONE);
//                llPresentChoosePayMode.setVisibility(View.VISIBLE);
//                root.setBackgroundResource(R.drawable.present_bg_text1);
//                ivTitle.setImageResource(R.drawable.present_pay_iv1);
//
//                setSelect(0);
//                tvTitle.setText(strings[0].replace(":", ""));
//                tv.setText(zoomString(unit + strings[1]));
//                tv.setTextSize(ScreenManager.getInstance().isMinScreen()?136:68);
//                break;
//            case 1:
//                tvPaySuccess.setVisibility(View.VISIBLE);
//                llPresentChoosePayMode.setVisibility(View.GONE);
//                root.setBackgroundResource(R.drawable.present_bg_text2);
//                ivTitle.setImageResource(R.drawable.present_pay_iv2);
//
//
//                tvTitle.setText(strings[0].replace(":", ""));
//                tvPaySuccess.setText(R.string.pay_thank_you);
//
//                tv.setText(zoomString(unit + strings[1]));
//                tv.setTextSize(ScreenManager.getInstance().isMinScreen()?136:68);
//                playAnim();
//
//                break;
//            case 2:
//
//                llPresentInfo.setVisibility(View.GONE);
//                root.setBackgroundResource(R.drawable.present_bg_text3);
//                ivTitle.setImageResource(R.drawable.present_pay_iv3);
//                tvTitle.setText(ResourcesUtils.getString(R.string.tips_bye_again));
//
//                tv.setText(tip);
//                tv.setTextSize(ScreenManager.getInstance().isMinScreen()?90:45);
//                break;
//            case 3:
//                tvTitle.setText(R.string.pay_paying);
//                tv.setText(zoomString(tip));
//
//                presentProgress.setVisibility(View.VISIBLE);
//                tvPaySuccess.setVisibility(View.VISIBLE);
//                llPresentChoosePayMode.setVisibility(View.GONE);
//                llPresentPayFail.setVisibility(View.GONE);
//
//                root.setBackgroundResource(R.drawable.present_bg_text1);
//                ivTitle.setImageResource(R.drawable.present_pay_iv1);
//
//                tvPaySuccess.setText(R.string.pay_paying_wait);
//                break;
//            case 4:
//                tvTitle.setText(R.string.pay_fail);
//                tv.setText(zoomString(tip));
//
//                llPresentInfo.setVisibility(View.VISIBLE);
//                presentProgress.setVisibility(View.GONE);
//                tvPaySuccess.setVisibility(View.GONE);
//                llPresentChoosePayMode.setVisibility(View.GONE);
//                llPresentPayFail.setVisibility(View.VISIBLE);
//
//                setSelect(0);
//
//                root.setBackgroundResource(R.drawable.present_bg_text4);
//                ivTitle.setImageResource(R.drawable.present_pay_iv4);
//                break;
//        }

    }


    private SpannableString zoomString(String strings) {
        SpannableString ss = new SpannableString(strings);
        ss.setSpan(new RelativeSizeSpan(0.65f), 0, 1, Spanned.SPAN_INCLUSIVE_EXCLUSIVE); // set size
        return ss;
    }

//    void playAnim() {
//        AnimationDrawable animationDrawable = (AnimationDrawable) ivTitle.getDrawable();
//        animationDrawable.start();
//    }


//    public void setSelect(int index) {
//        paymodeOne.setSelected(index == 0 ? true : false);
//        paymodeTwo.setSelected(index == 1 ? true : false);
//        paymodeThree.setSelected(index == 2 ? true : false);
//    }

    @Override
    public void show() {
        super.show();

        Log.i("SCREEN>>>", "SHOW");
        root.setBackgroundResource(R.drawable.bg_mini_screen);
        payBgImageView.setImageResource(R.drawable.bg_prepare_scan);
//        int payMode = (int) SharePreferenceUtil.getParam(getContext(), PayDialog.PAY_MODE_KEY, 7);
//        switch (payMode) {
//            case PayDialog.PAY_FACE:
//                paymodeOne.setVisibility(View.GONE);
//                paymodeTwo.setVisibility(View.GONE);
//                paymodeThree.setVisibility(View.VISIBLE);
//                presentFailTwo.setVisibility(View.GONE);
//                break;
//            case PayDialog.PAY_CODE |PayDialog.PAY_FACE:
//                paymodeOne.setVisibility(View.GONE);
//                paymodeTwo.setVisibility(View.VISIBLE);
//                paymodeThree.setVisibility(View.VISIBLE);
//                presentFailTwo.setVisibility(View.VISIBLE);
//
//                break;
//
//            case PayDialog.PAY_FACE | PayDialog.PAY_CODE | PayDialog.PAY_CASH:
//                paymodeOne.setVisibility(View.VISIBLE);
//                paymodeTwo.setVisibility(View.VISIBLE);
//                paymodeThree.setVisibility(View.VISIBLE);
//                presentFailTwo.setVisibility(View.VISIBLE);
//
//                break;
//        }
    }

//    @Override
//    public void onClick(View v) {
//        super.onClick(v);
//        switch (v.getId()) {
//            case R.id.paymode_one:
//                setSelect(0);
//                break;
//            case R.id.paymode_two:
//                setSelect(1);
//                break;
//            case R.id.paymode_three:
//                setSelect(2);
//                break;
//            case R.id.present_fail_one:
//
//                break;
//            case R.id.present_fail_two:
//                break;
//            case R.id.present_fail_three:
//                break;
//
//
//        }
//
//    }

    @Override
    public void onSelect(boolean isShow) {

    }
}
