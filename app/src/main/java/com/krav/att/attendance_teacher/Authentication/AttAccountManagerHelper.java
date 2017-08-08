package com.krav.att.attendance_teacher.Authentication;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;

/**
 * Created by 01547598 on 7/25/2017.
 */

public class AttAccountManagerHelper {

    private static AttAccountManagerHelper instance;
    private Context context;

    private AccountManager mAccountManager;
    private Account account;

    private AttAccountManagerHelper() {}

    public static AttAccountManagerHelper getInstance(Context ctx) {
        if (instance==null) {
            instance = new AttAccountManagerHelper();
        }
        instance.context = ctx;
        return instance;
    }

    public void removeAccount() {
        mAccountManager = AccountManager.get(this.context);
        final Account[] accounts = mAccountManager.getAccountsByType(AttAccountGeneral.ACCOUNT_TYPE);
        if (accounts.length != 0) {
            final Account account = accounts[0];
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                mAccountManager.removeAccountExplicitly(account);
            } else {
                final AccountManagerFuture<Boolean> future = mAccountManager.removeAccount(account, new AccountManagerCallback<Boolean>() {
                    @Override
                    public void run(AccountManagerFuture<Boolean> future) {}
                }, null);
            }
        }
    }

    /**
     * Assynchronous method that returns authorization token
     * from AccountManager.
     */
    public void getAuthToken(final OnAuthTokenFinished callback) {
        if (callback==null) return;
        if (!checkAuthentication()) {
            callback.authenticationFinished(null);
        }

        final AccountManagerFuture<Bundle> future = mAccountManager.getAuthToken(
                account, AttAccountGeneral.AUTHTOKEN_TYPE_FULL_ACCESS, null, false,
                new AccountManagerCallback<Bundle>() {
                    @Override
                    public void run(AccountManagerFuture<Bundle> future) {
                        Bundle bnd = null;
                        try {
                            bnd = future.getResult();
                            String token = bnd.getString(AccountManager.KEY_AUTHTOKEN);
                            callback.authenticationFinished(token);
                        } catch (Exception e) {
                            callback.authenticationFinished(null);
                        }
                    }
                }
                , null);
    }

    public AccountManager getAccountManager() {
        return mAccountManager = AccountManager.get(this.context);
    }

    public boolean checkAuthentication() {
     //   UserDataSharedObj user = UserDataSharedObj.carregar(this.context);
     //   if (Util.isEmpty(user.getEmail()) || Util.isEmpty(user.getName()) || Util.isEmpty(user.getPasswordSHA256())) {
     //       return false;
     //   }

        mAccountManager = AccountManager.get(this.context);
        Account[] accounts = mAccountManager.getAccountsByType(AttAccountGeneral.ACCOUNT_TYPE);
        if (accounts.length > 0) {
            account = accounts[0];
            return true;
        }
        return false;
    }
}
