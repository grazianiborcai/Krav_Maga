package com.krav.att.attendance_teacher.Requests.FragmentRequest;

import com.krav.att.attendance_teacher.Parcelable.People;
import com.krav.att.attendance_teacher.Requests.FragmentRequestMaster.RequestFragment;
import com.krav.att.attendance_teacher.Requests.Interface.OnTaskFinished;
import com.krav.att.attendance_teacher.Requests.Util.HTTPMethodEnum;

public class ClassRequestFragment extends RequestFragment {

    public static final String TAG_TASK_CLASS = "task_class";
    private static final String URL = "Class/selectClass";
    public static final int ID_SERVICE = 1;

    public static ClassRequestFragment newInstance(OnTaskFinished callback) {
        ClassRequestFragment f = new ClassRequestFragment();
        f.mCallbacks = callback;
        f.setIdentifying(ID_SERVICE);
        return f;
    }

    public boolean execute (String authtoken, String params) {
        return execute(URL, HTTPMethodEnum.HTTP_GET, params, People.class, authtoken);
    }

}
