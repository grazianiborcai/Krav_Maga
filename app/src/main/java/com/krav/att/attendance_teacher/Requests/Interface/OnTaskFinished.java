package com.krav.att.attendance_teacher.Requests.Interface;

import android.content.Intent;

public interface OnTaskFinished {
    void postExecute(Intent intent);
    void cancelled();
    void animation(boolean b);
}