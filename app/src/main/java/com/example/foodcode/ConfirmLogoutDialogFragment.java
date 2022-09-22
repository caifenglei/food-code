package com.example.foodcode;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;

import com.example.foodcode.data.AuthManager;

/**
 * 确认退出登录对话框
 */
public class ConfirmLogoutDialogFragment extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Activity activity = getActivity();
        Context context = activity.getApplicationContext();
        AuthManager authManager = new AuthManager(context);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setMessage(R.string.confirm_logout_text)
                .setPositiveButton(R.string.button_text_confirm_logout, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        authManager.clearAuth();
                        Intent intent = new Intent(activity, MainActivity.class);
                        startActivity(intent);
                        dismiss();
                        activity.finish();
                    }
                })
                .setNegativeButton(R.string.button_text_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dismiss();
                    }
                });

        return builder.create();
    }
}