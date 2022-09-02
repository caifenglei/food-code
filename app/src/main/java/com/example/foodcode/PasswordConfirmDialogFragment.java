package com.example.foodcode;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import androidx.fragment.app.DialogFragment;
/**
 * refund password confirm dialog
 */
public class PasswordConfirmDialogFragment extends DialogFragment {

    public PasswordConfirmDialogFragment() {
        // Required empty public constructor
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View contentView = inflater.inflate(R.layout.dialog_refund_confirm, null);
        builder.setView(contentView);

        Button confirmButton = contentView.findViewById(R.id.confirmRefundBtn);
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("click", "confirm");
            }
        });

        Button cancelButton = contentView.findViewById(R.id.cancelRefundBtn);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PasswordConfirmDialogFragment.this.getDialog().cancel();
            }
        });

        return builder.create();
    }

}