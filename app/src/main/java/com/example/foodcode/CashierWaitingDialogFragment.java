package com.example.foodcode;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.foodcode.utils.ToastUtil;

/**
 * create an instance of this fragment.
 */
public class CashierWaitingDialogFragment extends AppCompatDialogFragment {

    private String moneyToPay;
    private StringBuilder sb = new StringBuilder();
    private Handler myHandler = new Handler(Looper.getMainLooper());

    private Boolean callingPayAfterReadingCode = false;

    private Boolean waitReceiving = false;

    Button cancelButton;
    Button confirmDialogButton;
    TextView cashierText;

    public interface OnCompleteListener {
        void onCancel();

        void onComplete(String payQrCode, String money);
    }

    public OnCompleteListener completeListener = null;

    public void setCompleteListener(OnCompleteListener completeListener) {
        this.completeListener = completeListener;
    }

    @Override
    public void onStart() {
        super.onStart();

        Dialog dialog = getDialog();
//        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_cashier_waiting, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        initAction();
        initData();
    }

    private void initView(View view) {
        cancelButton = view.findViewById(R.id.cancelCashierBtn);
        confirmDialogButton = view.findViewById(R.id.confirmDialogBtn);
        cashierText = view.findViewById(R.id.cashierText);
    }

    private void initAction() {

        waitReceiving = true;

        //handle key down event action
        getDialog().setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialogInterface, int i, KeyEvent event) {

//                StringBuilder tsb = new StringBuilder();
//                tsb.append((char) event.getUnicodeChar());
//                Log.i("===ON_KEY===", String.valueOf(event.getUnicodeChar()) + ":" + tsb.toString());

                //不在等待付款，或正在调用支付，不处理扫码输入
                if (!waitReceiving || callingPayAfterReadingCode) {
                    sb.setLength(0);
                    return false;
                }
                int action = event.getAction();
                switch (action) {
                    case KeyEvent.ACTION_DOWN:
                        int unicodeChar = event.getUnicodeChar();

                        if (unicodeChar != 0) {
                            sb.append((char) unicodeChar);
                        }
                        int keyCode = event.getKeyCode();
                        if (keyCode == KeyEvent.KEYCODE_VOLUME_UP || keyCode == KeyEvent.KEYCODE_VOLUME_DOWN || keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_MENU || keyCode == KeyEvent.KEYCODE_HOME || keyCode == KeyEvent.KEYCODE_POWER) {
                            return false;
                        }

                        final int len = sb.length();
//                        sendMessageToUser(sb.toString());
                        myHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {

                                // 200ms内字符长度有变化，说明还在读取字符
                                if (len != sb.length()) return;

                                // Log.i("DELAY===", sb.toString());
                                //扫码读取数据结束且前一笔读码还未结束，发起收款，并清空码数据
                                if (sb.length() > 0 && !callingPayAfterReadingCode) {
                                    payByCode(sb.toString());
                                    sb.setLength(0);
                                }
                            }
                        }, 200);
                        break;
                    default:
                        break;
                }
                return true;
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("CANCEL_BTN===", "called???" + view.toString());
                    waitReceiving = false;
//                CashierWaitingDialogFragment.this.getDialog().cancel();
                    if (completeListener != null) {
                        completeListener.onCancel();
                    }
                    dismiss();
            }
        });

        confirmDialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (completeListener != null) {
                    completeListener.onCancel();
                }
                dismiss();
            }
        });
    }

    private void initData() {
        Bundle bundle = getArguments();
        moneyToPay = bundle.getString("money");
    }

    //扫码支付
    public void payByCode(String payCode) {

        callingPayAfterReadingCode = true;
        cancelButton.setClickable(false);

        payCode = payCode.replaceAll("\n", "");
//        cashierText.setText(R.string.receiving_customer_pay);
//        cancelButton.setVisibility(View.GONE);
        if (completeListener != null) {
            completeListener.onComplete(payCode, moneyToPay);
        }
    }

    public void scanReceiveAgain() {
        cancelButton.setClickable(true);
        callingPayAfterReadingCode = false;
    }

    public void receiveMoneyFail(String message) {
        //manual
        waitReceiving = false;
        cancelButton.setClickable(true);
        callingPayAfterReadingCode = false;

        cashierText.setText(message);
        cancelButton.setVisibility(View.GONE);
        confirmDialogButton.setVisibility(View.VISIBLE);
    }

    public void receiveMoneySuccess() {
        waitReceiving = false;
        cancelButton.setClickable(true);
        callingPayAfterReadingCode = false;
        dismiss();
    }

    private void sendMessageToUser(final String msg) {
        Log.i("@@@@@@", msg);
    }

}