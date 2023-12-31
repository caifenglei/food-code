package com.example.foodcode;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;
import androidx.lifecycle.Observer;

import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.foodcode.data.AuthManager;
import com.example.foodcode.data.PaymentResult;
import com.example.foodcode.data.PaymentViewModel;
import com.example.foodcode.data.model.ConsumeRecord;
import com.example.foodcode.present.TextDisplay;
import com.example.foodcode.utils.Helper;
import com.example.foodcode.utils.ScreenManager;
import com.example.foodcode.utils.ToastUtil;
import com.yzy.voice.VoicePlay;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Objects;

/**
 * A calculator
 */
public class CalculatorFragment extends Fragment {

    //    private static Sound sound = null;
    private PaymentViewModel paymentViewModel;

    CashierWaitingDialogFragment waitingPayDialog;
    private static long lastClickTime = 0L;
    private ScreenManager screenManager = ScreenManager.getInstance();
    private TextDisplay miniScreenDisplay = null;

    private Activity activity;
    private Context context;
    AuthManager authManager;
    private Handler resourceHandler = new Handler();
    SoundPool soundPool = new SoundPool(2, AudioManager.STREAM_MUSIC, 0);
    private int showPayCodeSound;
    private int failReceiveSound;
    private int successReceiveSound;

    Double calResult = 0.00;
    Boolean autoCashier = false;
    private String moneyToCashier = "0.00";
    String numberStack = "";
    View fragmentView;
    TextView calResultView;
    ArrayList<Double> calElements = new ArrayList<>(2);
    int activeOperator;

    public CalculatorFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        activity = getActivity();
        context = activity.getApplicationContext();
        authManager = new AuthManager(context);

        getParentFragmentManager().setFragmentResultListener("startAutoCashier", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle bundle) {
                startAutoCashier();
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        fragmentView = inflater.inflate(R.layout.fragment_calculator, container, false);

        paymentViewModel = new PaymentViewModel(getActivity().getApplicationContext());

//        sound = new Sound(this.getActivity());
//        sound.addSound(0, R.raw.beep);

        initView();
        initMiniScreen();
        initAction();

        //Calculator
        Button keyOne = fragmentView.findViewById(R.id.keyOne);
        Button keyTwo = fragmentView.findViewById(R.id.keyTwo);
        Button keyThree = fragmentView.findViewById(R.id.keyThree);
        Button keyFour = fragmentView.findViewById(R.id.keyFour);
        Button keyFive = fragmentView.findViewById(R.id.keyFive);
        Button keySix = fragmentView.findViewById(R.id.keySix);
        Button keySeven = fragmentView.findViewById(R.id.keySeven);
        Button keyEight = fragmentView.findViewById(R.id.keyEight);
        Button keyNine = fragmentView.findViewById(R.id.keyNine);
        Button keyZero = fragmentView.findViewById(R.id.keyZero);
        Button keyPoint = fragmentView.findViewById(R.id.keyPoint);
        AppCompatImageButton keyDelete = fragmentView.findViewById(R.id.keyDelete);
        Button keyPlus = fragmentView.findViewById(R.id.keyPlus);
        Button keyDeduct = fragmentView.findViewById(R.id.keyDeduct);
        Button keyMultiply = fragmentView.findViewById(R.id.keyMultiply);
        Button keyDivide = fragmentView.findViewById(R.id.keyDivide);
        Button keyCashier = fragmentView.findViewById(R.id.keyCashier);

        keyOne.setOnClickListener(onCalculatorKeyClick);
        keyTwo.setOnClickListener(onCalculatorKeyClick);
        keyThree.setOnClickListener(onCalculatorKeyClick);
        keyFour.setOnClickListener(onCalculatorKeyClick);
        keyFive.setOnClickListener(onCalculatorKeyClick);
        keySix.setOnClickListener(onCalculatorKeyClick);
        keySeven.setOnClickListener(onCalculatorKeyClick);
        keyEight.setOnClickListener(onCalculatorKeyClick);
        keyNine.setOnClickListener(onCalculatorKeyClick);
        keyZero.setOnClickListener(onCalculatorKeyClick);
        keyPoint.setOnClickListener(onCalculatorKeyClick);
        keyDelete.setOnClickListener(onCalculatorKeyClick);
        keyPlus.setOnClickListener(onCalculatorKeyClick);
        keyDeduct.setOnClickListener(onCalculatorKeyClick);
        keyMultiply.setOnClickListener(onCalculatorKeyClick);
        keyDivide.setOnClickListener(onCalculatorKeyClick);
        keyCashier.setOnClickListener(onCalculatorKeyClick);

        return fragmentView;
    }

    private void initView() {
        waitingPayDialog = new CashierWaitingDialogFragment();
        calResultView = fragmentView.findViewById(R.id.amountResult);
    }

    private void initAction() {

        waitingPayDialog.setCompleteListener(new CashierWaitingDialogFragment.OnCompleteListener() {
            @Override
            public void onCancel() {
                Log.i("COMPLETE", "cancelled...");
                //取消收银，重置金额 2023-04-17更改
                tapClear();

                autoCashier = false;
                miniScreenDisplay.hidePay();
            }

            //重启付款：前一付款不成功，自动重新发起收款（不改支付金额）
            public void onRelaunch(){
                autoCashier = false;
                miniScreenDisplay.hidePay();

                resourceHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Log.i("Start cashier again ===", "callback of confirm button");
                        startCashier();
                    }
                }, 200);
            }

            //扫码完成，发起收款请求
            @Override
            public void onComplete(String payQrCode, String money) {
                Log.i("COMPLETE", "scan code complete");

                //API to receive money
                paymentViewModel.receiveMoney(payQrCode, money);
            }
        });

        //listener to receive money
        paymentViewModel.getPaymentResult().observe(getViewLifecycleOwner(), new Observer<PaymentResult>() {
            @Override
            public void onChanged(PaymentResult paymentResult) {
                Log.i("OBSERVE", "payment result changed");
                if (paymentResult == null) {
                    return;
                }

                if (paymentResult.getError() != null) {
                    String reason = paymentResult.getError();
                    if (autoCashier) {
                        ToastUtil.show(activity, reason);
                        waitingPayDialog.scanReceiveAgain();
                    } else {
                        waitingPayDialog.receiveMoneyFail(paymentResult.getError());
                    }
                    // 扫码收款失败，播放提示音
                    playSound(failReceiveSound);
                } else {
                    Log.i("===TRACE===", moneyToCashier);
                    //已扫码，并收款成功

                    //refresh consume list
                    Bundle consume = new Bundle();
                    ConsumeRecord consumeRecord = new ConsumeRecord();
                    try {
                        JSONObject receiveResult = paymentResult.getSuccess();
                        consumeRecord.setOrderId(receiveResult.getString("id"));
                        consumeRecord.setOrderNo(receiveResult.getString("expendOrderNum"));
                        consumeRecord.setOrderAmount(Double.parseDouble(receiveResult.getString("amount")));
                        consumeRecord.setOrderTime(receiveResult.getString("expendTime"));
                        consumeRecord.setPaymentMethod(receiveResult.getInt("expendMethod"));
                        consumeRecord.setPaymentType(receiveResult.getInt("quotaType"));
                        consumeRecord.setOrderStatus(receiveResult.getInt("orderStatus"));
                        consume.putSerializable("record", consumeRecord);
                        getParentFragmentManager().setFragmentResult("moneyPaid", consume);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        ToastUtil.show(activity, "收款成功-返回数据字段有误");
                    }

                    Log.i("===TRACE===", "close dialog");
                    miniScreenDisplay.hidePay();
                    waitingPayDialog.receiveMoneySuccess();

                    Log.i("===TRACE===", "reset money");
                    if (autoCashier) {
                        // 由于报金额较慢，所以自动收款不含金额
                        playAutoCashierPaidSound();
//                        startAutoCashier();
                    } else {
                        playMoneySound(moneyToCashier);
                        tapClear();
                    }
                }
            }
        });

        // 加载音频池
        // 收款成功提示音
        successReceiveSound = soundPool.load(context, R.raw.tradeseccuss, 2);
        // 收款失败提示音
        failReceiveSound = soundPool.load(context, R.raw.failed_to_pay_again, 1);
        // 出示付款码提示音
        showPayCodeSound = soundPool.load(context, R.raw.show_pay_code, 3);

        soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {

            @Override
            public void onLoadComplete(SoundPool soundPool, int i, int i1) {
                Log.i("ON_SOUND_LOAD", String.valueOf(i) + "|" + String.valueOf(i1));
                if (i == 3 && i1 == 0) {
                    //收款方式处理
                    manageCashierType();
                }
            }
        });
    }

    private void manageCashierType() {
        String cashierType = authManager.getCashierType();
        if (cashierType.equals(AuthManager.CASHIER_AUTO)) {
            startAutoCashier();
        }
    }


    private View.OnClickListener onCalculatorKeyClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            int viewId = view.getId();
            switch (viewId) {
                case R.id.keyOne:
                case R.id.keyTwo:
                case R.id.keyThree:
                case R.id.keyFour:
                case R.id.keyFive:
                case R.id.keySix:
                case R.id.keySeven:
                case R.id.keyEight:
                case R.id.keyNine:
                case R.id.keyZero:
                    Button b = (Button) view;
                    tapNumber(b.getText().toString());
                    break;
                case R.id.keyPoint:
                    tapPoint();
                    break;
                case R.id.keyPlus:
                case R.id.keyDeduct:
                case R.id.keyMultiply:
                case R.id.keyDivide:
                    tapOperator(viewId);
                    break;
                case R.id.keyDelete:
                    tapClear();
                    break;
                case R.id.keyCashier:
                    tapCashier();
                    break;
            }
        }
    };

    private void tapNumber(String nbr) {
        if (Objects.equals(numberStack, "")) {
            numberStack = nbr;
        } else {
            //只取两位小数
            if (numberStack.contains(".") && numberStack.substring(numberStack.indexOf(".")).length() == 3) { //substring includes .
                ToastUtil.show(activity, "仅支持两位小数哦~");
            } else if (numberStack.length() <= 6) {
                numberStack = numberStack + nbr;
            } else {
                ToastUtil.show(activity, "金额已达上限了~");
            }
        }
        Log.i("calculator:", numberStack);
        formatResultValue(Double.parseDouble(numberStack));
    }

    private void tapPoint() {
        if (Objects.equals(numberStack, "")) {
            numberStack = "0.";
        } else {
            //避免多个小数输入导致错误
            if (!numberStack.contains(".")) {
                numberStack = numberStack + '.';
            }
        }
    }

    private void tapOperator(int opt) {
        int elementCount = calElements.size();
        if (elementCount == 0) {
            if (!Objects.equals(numberStack, "")) {
                calElements.add(Double.parseDouble(numberStack));
                numberStack = "";
                activeOperator = opt;
            }
        } else if (elementCount == 1) {
            if (!Objects.equals(numberStack, "")) {
                calElements.add(Double.parseDouble(numberStack));
                if (calculateResult(activeOperator)) {
                    calElements.add(calResult);
                    activeOperator = opt;
                }
            } else {
                activeOperator = opt;
            }
        } else {
            //2 factors, then calculate the result
            calculateResult(opt);
        }
    }

    // clear all
    private void tapClear() {
        numberStack = "";
        calElements.clear();
        calResult = 0.00;
        moneyToCashier = getString(R.string.money_zero);
        calResultView.setText(R.string.money_zero);
    }

    // 触发收银的动作
    private void tapCashier() {

        int factorCount = calElements.size();
        Double moneyToPay = 0.00;
        if (factorCount == 0 && !Objects.equals(numberStack, "") && Double.parseDouble(numberStack) != 0) {
            moneyToPay = Double.parseDouble(numberStack);
        } else if (factorCount == 1 && calResult > 0) {
            if (!Objects.equals(numberStack, "")) {
                calElements.add(Double.parseDouble(numberStack));
                if (calculateResult(activeOperator)) {
                    calElements.add(calResult);
                    moneyToPay = calResult;
                }
            } else {
                moneyToPay = calResult;
            }
        } else if (factorCount == 2) {
            if (calculateResult(R.id.keyPlus)) {
                moneyToPay = calResult;
            }
        }
        if (moneyToPay > 0) {
            Log.i("CASHIER", String.valueOf(moneyToPay));

            moneyToCashier = Helper.formatMoney(moneyToPay, false);
            calResultView.setText(moneyToCashier);
            startCashier();
        } else {
            ToastUtil.show(activity, context.getString(R.string.set_receive_money));
        }
    }

    public void startAutoCashier() {

        autoCashier = true;
        String cashierAmount = authManager.getCashierAmount();
        moneyToCashier = Helper.formatMoney(Double.parseDouble(cashierAmount), false);
        calResultView.setText(moneyToCashier);
        numberStack = cashierAmount;

        startCashier();
    }

    /**
     * 弹出收款信息框，等待扫码收款
     */
    private void startCashier() {

        if (isFastClick(500)) {
            ToastUtil.show(activity, "还没反应过来，慢点操作~");
            return;
        }

        //弹框未加载
        if (!waitingPayDialog.isAdded()) {
            Bundle bundle = new Bundle();
            bundle.putString("money", moneyToCashier);
            waitingPayDialog.setArguments(bundle);
            waitingPayDialog.show(getParentFragmentManager(), "refund_dialog");
            miniScreenDisplay.showPay(moneyToCashier);

            //播放请扫码的音频
//        Log.i("+++SOUND+++", String.valueOf(showPayCodeSound));
            playSound(showPayCodeSound);
        } else {
            Log.i("NOT_ADDED", "发生了什么？");
//            ToastUtil.show(activity, "操作太快，请重试");
        }
    }

    private boolean calculateResult(int opt) {

        boolean ok = true;
        if (opt == R.id.keyPlus) {
            calResult = calElements.get(0) + calElements.get(1);
        } else if (opt == R.id.keyDeduct) {
            calResult = calElements.get(0) - calElements.get(1);
        } else if (opt == R.id.keyMultiply) {
            calResult = calElements.get(0) * calElements.get(1);
        } else if (opt == R.id.keyDivide) {
            if (calElements.get(1) == 0) {
                calResultView.setText("错误");
                ok = false;
            } else {
                calResult = calElements.get(0) / calElements.get(1);
            }
        }

        if (ok) {
            numberStack = "";
            calElements.clear();
            formatResultValue(calResult);
        }

        return ok;
    }

    private void formatResultValue(Double value) {
        BigDecimal bd = new BigDecimal(value);
        calResult = bd.setScale(2, RoundingMode.HALF_UP).doubleValue(); //直接截断
        calResultView.setText(Helper.formatMoney(calResult, false));
    }

    private void initMiniScreen() {
        screenManager.init(context);
//        Display[] displays = screenManager.getDisplays();
//        Log.e("SCREEN", "屏幕数量" + displays.length);
//        for (int i = 0; i < displays.length; i++) {
//            Log.e("SCREEN", "屏幕" + displays[i]);
//        }
        Display display = screenManager.getPresentationDisplays();
        if (display != null) {
            miniScreenDisplay = new TextDisplay(context, display);
            miniScreenDisplay.show();
        }
    }

    private void playMoneySound(String money) {
        VoicePlay.with(context).play("收款成功" + money + "元");
    }

    private void playSound(int soundId) {

        resourceHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.i("PLAY===", String.valueOf(soundId));
                soundPool.play(soundId, 1, 1, 10, 0, 1);
            }
        }, 500);
    }

    private void playAutoCashierPaidSound() {

        MediaPlayer player = new MediaPlayer();
        AssetFileDescriptor fileDescriptor = context.getResources().openRawResourceFd(R.raw.tradeseccuss);
        try {
            player.setDataSource(fileDescriptor.getFileDescriptor(), fileDescriptor.getStartOffset(), fileDescriptor.getLength());
            fileDescriptor.close();
            player.prepareAsync();
            player.setOnPreparedListener((mediaPlayer) -> {
                player.start();
            });
            player.setOnCompletionListener((mediaPlayer) -> {
                player.reset();
                startAutoCashier();
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean isFastClick(int millionSeconds) {
        long now = SystemClock.elapsedRealtime();
        if (Math.abs(now - lastClickTime) < millionSeconds) {
            return true;
        } else {
            lastClickTime = now;
            return false;
        }
    }
}