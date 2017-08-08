package com.krav.att.attendance_teacher.Authentication;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class AttAuthenticatorService extends Service {
    @Override
    public IBinder onBind(Intent intent) {

        AttAuthenticator authenticator = new AttAuthenticator(this);
        return authenticator.getIBinder();
    }
}
