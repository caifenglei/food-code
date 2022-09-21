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

/**
 * create an instance of this fragment.
 */
public class CashierWaitingDialogFragment extends AppCompatDialogFragment {

    private String moneyToPay;
    private StringBuilder sb = new StringBuilder();
    private Handler myHandler = new Handler(Looper.getMainLooper());

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
                if (!waitReceiving) {
                    return false;
                }
                int action = event.getAction();
                switch (action) {
                    case KeyEvent.ACTION_DOWN:
                        int unicodeChar = event.getUnicodeChar();
//                        Log.i("CHAR", String.valueOf(unicodeChar));
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
                                if (len != sb.length()) return;
                                if (sb.length() > 0) {
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

        payCode = payCode.replaceAll("\n", "");

        Log.i("PAYCODE", payCode);
        Log.i("MONEY", moneyToPay);

        cashierText.setText(R.string.receiving_customer_pay);
        cancelButton.setVisibility(View.GONE);
        if (completeListener != null) {
            completeListener.onComplete(payCode, moneyToPay);
        }
    }

    public void receiveMoneyFail(String message) {
        cashierText.setText(message);
        waitReceiving = false;
        //manual
        confirmDialogButton.setVisibility(View.VISIBLE);
    }

    public void receiveMoneySuccess() {
        waitReceiving = false;
        dismiss();
    }

    private void sendMessageToUser(final String msg) {
        Log.i("@@@@@@", msg);
    }

}