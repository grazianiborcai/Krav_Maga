package com.krav.att.attendance_teacher.Util;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.TextView;
import android.widget.Toast;

public class MyToast {

    public final static void makeText(String text, Context context, int duration, int layout, int gravity) {
        Toast toast = Toast.makeText(context.getApplicationContext(), "", duration);
        LayoutInflater inflater =  (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        TextView txtView = (TextView) inflater.inflate(layout, null);
        txtView.setText(text);
        toast.setView(txtView);
        toast.setGravity(gravity, 0, 0);
        toast.show();
    }
}
