package com.krav.att.attendance_teacher.Requests.FragmentRequest;

import com.krav.att.attendance_teacher.Parcelable.People;
import com.krav.att.attendance_teacher.Requests.FragmentRequestMaster.RequestFragment;
import com.krav.att.attendance_teacher.Requests.Interface.OnTaskFinished;
import com.krav.att.attendance_teacher.Requests.Util.HTTPMethodEnum;

public class LoginRequestFragment extends RequestFragment {

    public static final String TAG_TASK_LOGIN = "task_login";
    public static final String TAG_TASK_SELECT_USER = "select_user";
    private static final String URL1 = "People/selectPeople";
    private static final String URL2 = "People/loginPeople";
    public static final int ID_SERVICE = 1;

    public static LoginRequestFragment newInstance(OnTaskFinished callback) {
        LoginRequestFragment f = new LoginRequestFragment();
        f.mCallbacks = callback;
        f.setIdentifying(ID_SERVICE);
        return f;
    }

    public boolean execute (String authtoken) {
        return execute(URL1, HTTPMethodEnum.HTTP_GET, People.class, authtoken);
    }

    public boolean execute (String email, String password) {
        return execute(URL2, HTTPMethodEnum.HTTP_GET, People.class, email, password);
    }
}
