package com.krav.att.attendance_teacher.Requests.FragmentRequestMaster;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.TextUtils;

import com.krav.att.attendance_teacher.R;
import com.krav.att.attendance_teacher.Requests.AsyncTask.HttpRequestTask;
import com.krav.att.attendance_teacher.Requests.Interface.OnTaskFinished;
import com.krav.att.attendance_teacher.Requests.Util.HTTPMethodEnum;
import com.krav.att.attendance_teacher.Requests.Util.NamedPairParameter;

import java.util.ArrayList;

public abstract class RequestFragment extends Fragment {

    public static final String DEFAULT_OWNER_PARAMETER = "codOwner=1";
    public static final int OK_CODE = 200;

    protected OnTaskFinished mCallbacks;
    protected HttpRequestTask mTask;

    private int identifying;

    /**
     * This method will only be called once when the retained
     * Fragment is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public String buildQueryString(ArrayList<NamedPairParameter> params) {
        if (params==null) return "";
        Uri.Builder builder = new Uri.Builder();
        for (NamedPairParameter param:params) {
            builder.appendQueryParameter(param.getKey(), param.getValue());
        }
        return builder.build().getEncodedQuery();
    }

    protected boolean execute(@NonNull String path, HTTPMethodEnum method, String inputParams, @NonNull Class<?> mclass) {
        return execute(path, method, inputParams, mclass, null);
    }

    /**
     *
     * @param path  URI to the webservice
     * @param method enum referring to GET or POST
     * @param inputParams if using GET method, a query string is expected, is any input parameter is intended.
     *                    Ex.: codOwner=1&beginDate=20160222&beginDate=20160223
     *                    On the other hand, the POST method forces a JSON format string.
     *                    Ex.:
     *                    [
                            {
                                "codOwner": 1,
                                "codStore": 1,
                                "codEmployee": 1,
                                "beginDate": {
                                "year": 2016,
                                "month": 2,
                                "day": 22
                                },
                                "beginTime": {
                                "hour": 8,
                                "minute": 0,
                                "second": 0,
                                "nano": 0
                                }
                                }
                            ]
     * @param mclass    Class whose a object will be created to keep the data returned from server
     * @param authtoken Authentication token
     * @return true if a background task is created, false otherwise.
     */
    protected boolean execute(@NonNull String path, HTTPMethodEnum method, String inputParams, @NonNull Class<?> mclass, String authtoken) {
        if (mTask == null || !mTask.isRunning()) {
            if (method==HTTPMethodEnum.HTTP_GET) {
                inputParams = includeDefaultParameters(inputParams);
            }
            mTask = new HttpRequestTask(path, method, mclass, authtoken, mCallbacks, identifying);
            mTask.setBaseURL(getString(R.string.baseurl));
            mTask.execute(inputParams);
            return true;
        } else {
            return false;
        }
    }

    protected boolean executeHeader(@NonNull String path, ArrayList<NamedPairParameter> listHeaders, Class<?> mclass) {
        if (mTask == null || !mTask.isRunning()) {
            mTask = new HttpRequestTask(path, HTTPMethodEnum.HTTP_GET, mclass, null, mCallbacks, identifying);
            mTask.setBaseURL(getString(R.string.baseurl));
            mTask.setHeaders(listHeaders);
            mTask.execute();
            return true;
        } else {
            return false;
        }
    }

    protected String includeDefaultParameters(String inputParams) {
        StringBuilder sb = new StringBuilder();
        if (!TextUtils.isEmpty(inputParams)) {
            sb.append(inputParams);
        }
        return sb.toString();
    }

    protected boolean execute(@NonNull String path, HTTPMethodEnum method, @NonNull Class<?> mclass, @NonNull String authtoken) {
        return execute(path, method, null, mclass, authtoken);
    }

    protected boolean execute(@NonNull String path, HTTPMethodEnum method, @NonNull Class<?> mclass, @NonNull String email, @NonNull String password) {
        if (mTask == null || !mTask.isRunning()) {
            mTask = new HttpRequestTask(path, method, mclass, email, password, mCallbacks, identifying);
            mTask.setBaseURL(getString(R.string.baseurl));
            mTask.execute();
            return true;
        } else {
            return false;
        }
    }

    public boolean isRunning() {
        return mTask != null ? mTask.isRunning() : false;
    }

    public void animation(boolean b) {
        mCallbacks.animation(b);
    }

    public boolean cancel() {
        if (mTask != null && mTask.isRunning()) {
            mTask.cancel(true);
            mTask = null;
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        cancel();
    }

    public int getIdentifying() {
        return identifying;
    }

    public void setIdentifying(int identifying) {
        this.identifying = identifying;
    }
}