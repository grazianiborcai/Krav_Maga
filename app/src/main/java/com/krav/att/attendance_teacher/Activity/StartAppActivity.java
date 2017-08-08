package com.krav.att.attendance_teacher.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;

import com.krav.att.attendance_teacher.Authentication.AttAccountManagerHelper;
import com.krav.att.attendance_teacher.Authentication.OnAuthTokenFinished;

/**
 * Created by 01547598 on 7/26/2017.
 */

public class StartAppActivity extends AppCompatActivity {

    protected String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //userAuthenticationFail=false;

        if (!AttAccountManagerHelper.getInstance(this).checkAuthentication()) {
            loginActivity();
        } else {
            AttAccountManagerHelper accountHelper = AttAccountManagerHelper.getInstance(this);
            accountHelper.getAuthToken(new OnAuthTokenFinished() {
                @Override
                public void authenticationFinished(String tokenRead) {
                    token = tokenRead;
                    if (!TextUtils.isEmpty(token)) {
                       // userAuthenticationFail = false;
                        onAuthenticated();
                    } else {
                       // userAuthenticationFail = true;
                        loginActivity();
                    }
                }
            });
        }
    }

    public void loginActivity() {
        Intent i = new Intent(this, LoginActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(i);
        finish();
    }

    protected void onAuthenticated() {
        Intent it = new Intent(this, MainActivity.class);
        it.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(it);
    }
}
