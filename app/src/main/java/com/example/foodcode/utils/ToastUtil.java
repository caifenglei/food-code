package com.example.foodcode.utils;

import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.foodcode.R;

public class ToastUtil {

    public static void show(Activity activity, CharSequence message) {

        Context context = activity.getApplicationContext();

//        Toast toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
//        toast.setGravity(Gravity.CENTER, 0, 0);
//        toast.show();

        LayoutInflater inflater = activity.getLayoutInflater();
        View layout = inflater.inflate(R.layout.content_toastr, activity.findViewById(R.id.toastrBox));
        TextView toastText = layout.findViewById(R.id.toastrText);
        toastText.setText(message);
        Toast toast = new Toast(context);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        toast.show();
    }
}
