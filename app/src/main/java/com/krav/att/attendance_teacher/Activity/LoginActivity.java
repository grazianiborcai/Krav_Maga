package com.krav.att.attendance_teacher.Activity;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.krav.att.attendance_teacher.Authentication.AttAccountGeneral;
import com.krav.att.attendance_teacher.Authentication.AttAccountManagerHelper;
import com.krav.att.attendance_teacher.Parcelable.People;
import com.krav.att.attendance_teacher.R;
import com.krav.att.attendance_teacher.Requests.AsyncTask.HttpRequestTask;
import com.krav.att.attendance_teacher.Requests.FragmentRequest.LoginRequestFragment;
import com.krav.att.attendance_teacher.Requests.Interface.OnTaskFinished;
import com.krav.att.attendance_teacher.Shared.UserDataShared;
import com.krav.att.attendance_teacher.Util.MyToast;
import com.krav.att.attendance_teacher.Util.SimpleCrypto;
import com.krav.att.attendance_teacher.Util.Util;
import com.krav.att.attendance_teacher.Views.AutoCompBackAwareEditText;
import com.krav.att.attendance_teacher.Views.BackAwareEditText;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

import static android.Manifest.permission.READ_CONTACTS;

public class LoginActivity extends AppCompatActivity implements LoaderCallbacks<Cursor>, OnTaskFinished {

    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;
    public static final String SOFT_KEY = "SoftKey";
    public static final String LOGIN_PF = "LoginPF";

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private LoginRequestFragment mRequestFragment;

    // UI references.
    private View activityRootView;

    private AutoCompBackAwareEditText mEmailView;
    private BackAwareEditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private TextView mForgotLogin;
    private TextView mCreateAccount;
    private LinearLayout mLinearLayout;
    private RelativeLayout.LayoutParams parameter;
    private ImageView mImageView;
    private Button mFbSignInButton;
    private Button mGpSignInButton;
    private boolean isSoftKey = false;
    private boolean checkKeyboard = true;
    private Animation scaleAnimation;

    private Handler h;

    /*private DialogListener forgetPasswordDialogListener = new DialogListener() {
        @Override
        public void onDialogFinish(String tag, int action, Object response) {
            if (action==ForgotPasswordViewDialog.ACTION_PASSW_CHANGED) {
                MyToast.makeText(getString(R.string.passwordChanged), LoginActivity.this.getApplicationContext(),
                        Toast.LENGTH_LONG, R.layout.toast_custom, Gravity.CENTER);
                mEmailView.setText((String)response); //email
                mPasswordView.requestFocus();
            }
        }
    };*/

    /**
     * Retreives the AccountAuthenticatorResponse from either the intent of the savedInstanceState, if the
     * savedInstanceState is non-zero.
     * @param savedInstanceState the save instance data of this Activity, may be null
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        h = new Handler();

        mAccountAuthenticatorResponse =
                getIntent().getParcelableExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE);

        if (mAccountAuthenticatorResponse != null) {
            mAccountAuthenticatorResponse.onRequestContinued();
        }

        setContentView(R.layout.activity_login);

        FragmentManager fm = getSupportFragmentManager();
        mRequestFragment = (LoginRequestFragment) fm.findFragmentByTag(LoginRequestFragment.TAG_TASK_LOGIN);

        // If the Fragment is non-null, then it is currently being
        // retained across a configuration change.
        if (mRequestFragment == null) {
            mRequestFragment = LoginRequestFragment.newInstance(this);
            fm.beginTransaction().add(mRequestFragment, LoginRequestFragment.TAG_TASK_LOGIN).commit();
        }

        AttAccountManagerHelper.getInstance(this).removeAccount();

        activityRootView = findViewById(R.id.root);
        activityRootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                verifyFlexibleComponents();
            }
        });

        // Set up the login form.
        mEmailView = (AutoCompBackAwareEditText) findViewById(R.id.email);

        populateAutoComplete();

        mEmailView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_NEXT || id == EditorInfo.IME_ACTION_DONE) {
                    mPasswordView.requestFocus();
                    return true;
                }
                return false;
            }
        });


        mPasswordView = (BackAwareEditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE) {
                    attemptLogin(textView);
                    return true;
                }
                return false;
            }
        });

        mForgotLogin = (TextView) findViewById(R.id.forgot_login);
        /*mForgotLogin.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mEmailView.clearFocus();
                ForgotPasswordViewDialog f =
                        ForgotPasswordViewDialog.newInstance(mEmailView.getText().toString().trim(),
                                forgetPasswordDialogListener);
//                f.setStyle(DialogFragment.STYLE_NORMAL, R.style.PasswordDialogStyle);
                f.show(getSupportFragmentManager(),ForgotPasswordViewDialog.TAG);
            }
        });*/

        mLinearLayout = (LinearLayout) findViewById(R.id.layout_background);

        mEmailView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mEmailView.setNoError();
            }
        });

        mEmailView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (v.equals(mEmailView) && !v.hasFocus() && event.getAction() == MotionEvent.ACTION_UP) {
                    mEmailView.setNoError();
                }
                return false;
            }
        });

        mPasswordView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mPasswordView.setNoError();
            }
        });

        mPasswordView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (v.equals(mPasswordView) && !v.hasFocus() && event.getAction() == MotionEvent.ACTION_UP) {
                    mPasswordView.setNoError();
                }
                return false;
            }
        });

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin(view);
            }
        });

        mFbSignInButton = (Button) findViewById(R.id.fb_sign_in_button);
        mFbSignInButton.setVisibility(View.GONE);

        mGpSignInButton = (Button) findViewById(R.id.gp_sign_in_button);
        mGpSignInButton.setVisibility(View.GONE);

        mLoginFormView = findViewById(R.id.login_form);
        mCreateAccount = (TextView) findViewById(R.id.create_account_button);

        mCreateAccount.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                callCreateAccountActivity();
            }
        });

        mProgressView = findViewById(R.id.login_progress);

        mImageView = (ImageView) findViewById(R.id.logo);

        if (savedInstanceState == null) {
            Animation moveAnimation = AnimationUtils.loadAnimation(this, R.anim.move_logo);
            mImageView.startAnimation(moveAnimation);


            /*scaleAnimation = AnimationUtils.loadAnimation(this, R.anim.scale_logo);

            moveAnimation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    mImageView.startAnimation(scaleAnimation);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });*/

            LinearLayout form = (LinearLayout) findViewById(R.id.form);
            Animation fadeAnimation = AnimationUtils.loadAnimation(this, R.anim.show_form);
            form.startAnimation(fadeAnimation);
            mForgotLogin.startAnimation(fadeAnimation);

            mCreateAccount.startAnimation(fadeAnimation);

        } else {
            SharedPreferences settings = getSharedPreferences(LOGIN_PF, 0);
            isSoftKey = settings.getBoolean(SOFT_KEY, false);
        }

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE || isSoftKey) {
            hideForgetPass();
        }
    }

    private void verifyFlexibleComponents() {
        boolean cont = isKeyboardShown(activityRootView);
        if (checkKeyboard)
            if (cont && !isSoftKey) {
                isSoftKey = !isSoftKey;
                hideForgetPass();
            } else if (!cont && isSoftKey) {
                isSoftKey = !isSoftKey;
                showForgetPass();
            }
    }

    private void callCreateAccountActivity() {
        Intent i = new Intent(this, CreateAccountActivity.class);
        startActivityForResult(i, 2);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
  /*      if (requestCode == 2) {
            if(resultCode == RESULT_OK){
                String email = data.getStringExtra(Intent.EXTRA_INTENT);
                String passw = data.getStringExtra(Intent.EXTRA_SHORTCUT_NAME);
                mEmailView.setText(email);
                mPasswordView.requestFocus();
                if (!passw.isEmpty()) {
                    mRequestFragment.execute(email, passw);
                }
            }
        }*/
    }

    @Override
    protected void onPause() {
        super.onPause();

        SharedPreferences settings = getSharedPreferences(LOGIN_PF, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(SOFT_KEY, isSoftKey);

        editor.commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        isSoftKey = false;
        if (mRequestFragment.isRunning()) {
            showProgress(true);
        } else {
            showProgress(false);
        }

        verifyFlexibleComponents();

    }

    private void showForgetPass() {
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            mForgotLogin.setVisibility(View.VISIBLE);
//            mFbSignInButton.setVisibility(View.VISIBLE);
//            mGpSignInButton.setVisibility(View.VISIBLE);
            mCreateAccount.setVisibility(View.VISIBLE);
            mImageView.setVisibility(View.VISIBLE);
           /* mImageView.startAnimation(scaleAnimation);*/
            parameter = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
            parameter.addRule(RelativeLayout.ABOVE, R.id.forgot_login);
            mLinearLayout.setLayoutParams(parameter);
        }
    }

    private void hideForgetPass() {
        mForgotLogin.setVisibility(View.GONE);
//        mFbSignInButton.setVisibility(View.GONE);
//        mGpSignInButton.setVisibility(View.GONE);
        mCreateAccount.setVisibility(View.GONE);
        mImageView.clearAnimation();
        mImageView.setVisibility(View.GONE);
        parameter = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        parameter.addRule(RelativeLayout.ABOVE, R.id.create_account_button);
        mLinearLayout.setLayoutParams(parameter);
    }

    private void populateAutoComplete() {
        if (!mayRequestContacts()) {
            return;
        }

        getLoaderManager().initLoader(0, null, this);
    }

    private boolean mayRequestContacts() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
            Snackbar.make(mEmailView, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {
                            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
                        }
                    });
        } else {
            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
        }
        return false;
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                populateAutoComplete();
            }
        }
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin(View view) {

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        String error = null;

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(password)) {
//            mPasswordView.setError(getString(R.string.error_field_required));
            error = getString(R.string.error_field_required);
            mPasswordView.setError();
            focusView = mPasswordView;
            cancel = true;
        } else
            if (!isPasswordValid(password)) {
                error = getString(R.string.error_invalid_password);
                mPasswordView.setError();
                focusView = mPasswordView;
                cancel = true;
            }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
//            mEmailView.setError(getString(R.string.error_field_required));
            error = getString(R.string.error_field_required);
            h.post(new Runnable() {
                @Override
                public void run() {
                    mEmailView.setError();
                }
            });
            focusView = mEmailView;
            cancel = true;
        } else if (!Util.validateEmail(email)) {
//            mEmailView.setError(getString(R.string.error_invalid_email));
            error = getString(R.string.error_invalid_email);
            h.post(new Runnable() {
                @Override
                public void run() {
                    mEmailView.setError();
                }
            });
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
            showError(error);
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            in.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            isSoftKey = false;

            String passwSHA256 = null;
            try {
                passwSHA256 = SimpleCrypto.sha256(password);
                mRequestFragment.execute(email, passwSHA256);
                showProgress(true);

                /*AttAccountManagerHelper h = AttAccountManagerHelper.getInstance(this);
                final Account account = new Account(AttAccountGeneral.ACCOUNT_NAME, AttAccountGeneral.ACCOUNT_TYPE);
                h.getAccountManager().addAccountExplicitly(account, AttAccountGeneral.ACCOUNT_PASSWORD, null);
                h.getAccountManager().setAuthToken(account, AttAccountGeneral.AUTHTOKEN_TYPE_FULL_ACCESS, passwSHA256);
                Intent i = new Intent(this, MainActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(i);
                finish();
                overridePendingTransition(0, 0);*/
            } catch (Exception e) {  //crypto engine won't work, something very wrong is happening.
                showProgress(false);
                mPasswordView.setError();
                mPasswordView.requestFocus();
                showError(getString(R.string.error_incorrect_password));
            }
        }
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() >= 4;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {

        if (show) {
            checkKeyboard = false;
            hideForgetPass();
        }
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            /*mCreateAccount.setVisibility(show ? View.GONE : View.VISIBLE);*/
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

//            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            /*mCreateAccount.setVisibility(show ? View.GONE : View.VISIBLE);*/
        }
        if (show)
            /*mProgressView.setAnimation(scaleAnimation);*/

        /*if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            mForgotLogin.setVisibility(show ? View.GONE : View.VISIBLE);
            mCreateAccount.setVisibility(show ? View.GONE : View.VISIBLE);
        } else {
            mForgotLogin.setVisibility(View.GONE);
            mCreateAccount.setVisibility(View.GONE);
        }*/

        if (!show)
            checkKeyboard = true;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

        addEmailsToAutoComplete(emails);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }


    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompBackAwareEditText what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(LoginActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        mEmailView.setAdapter(adapter);
    }

    private void finishLogin(Intent intent) {

        String accountName = intent.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
        String accountPassword = intent.getStringExtra(AccountManager.KEY_PASSWORD);
        String accountType = intent.getStringExtra(AccountManager.KEY_ACCOUNT_TYPE);
        ArrayList<People> pList = intent.getParcelableArrayListExtra(HttpRequestTask.RESULTS);
        String authtoken = pList.get(0).getoAuth();;

        AttAccountManagerHelper h = AttAccountManagerHelper.getInstance(this);
        final Account account = new Account(accountName, accountType);
        h.getAccountManager().addAccountExplicitly(account, accountPassword, null);
        h.getAccountManager().setAuthToken(account, AttAccountGeneral.AUTHTOKEN_TYPE_FULL_ACCESS, authtoken);

        setAccountAuthenticatorResult(intent.getExtras());

        persistUserData(intent);

        Intent i = new Intent(this, MainActivity.class);
        i.putExtras(intent.getExtras());
        i.putExtra("from_login_activity", true);
        i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(i);
        finish();
        overridePendingTransition(0, 0);
    }

    private void persistUserData(Intent intent) {
        try {
            ArrayList<People> pList = intent.getParcelableArrayListExtra(HttpRequestTask.RESULTS);
            People userData = pList.get(0);
            UserDataShared obj = UserDataShared.carregar(this);
            obj.setPeopleID(userData.getPeopleID());
            obj.setCountryID(userData.getCountryID());
            obj.setName(userData.getName());
            obj.setBirthDate(userData.getBirthDate());
            obj.setEnrollmentNumber(userData.getEnrollmentNumber());
            obj.setGradeDate(userData.getGradeDate());
            obj.setEmail(userData.getEmail());
            obj.setCelphone(userData.getCelphone());
            obj.setPhone(userData.getPhone());
            obj.setAddress1(userData.getAddress1());
            obj.setAddress2(userData.getAddress2());
            obj.setPostalCode(userData.getPostalCode());
            obj.setBloodType(userData.getBloodType());
            obj.setAllergy(userData.getAllergy());
            obj.setAllergyDesc(userData.getAllergyDesc());
            obj.setNextGradeExam(userData.getNextGradeExam());
            obj.setWhereOther(userData.getWhereOther());
            obj.setLookingOther(userData.getLookingOther());
            obj.setPassword(userData.getPassword());
            obj.setEnrTypeID(userData.getEnrTypeID());
            obj.setoAuth(userData.getoAuth());
            obj.setoAuthDate(userData.getoAuthDate());
            obj.setUserAgent(userData.getUserAgent());
            obj.setRegionID(userData.getRegionID());
            obj.setGradeID(userData.getGradeID());
            obj.setBirthDateS(userData.getBirthDateS());
            obj.setGradeDateS(userData.getGradeDateS());
            obj.setNextGradeExamS(userData.getNextGradeExamS());
            obj.setGenderID(userData.getGenderID());
            obj.setWhereID(userData.getWhereID());
            obj.setLookID(userData.getLookID());
            obj.save(this);
        } catch (Exception e) {}
    }

    private void showError(String name) {
        MyToast.makeText(name, this, Toast.LENGTH_LONG, R.layout.toast_custom, Gravity.CENTER);
    }

    @Override
    public void postExecute(Intent intent) {

        int responseCode = intent.getIntExtra(HttpRequestTask.HTTP_CODE, 0);
        if (responseCode == HttpURLConnection.HTTP_UNAUTHORIZED) {
            showError(getString(R.string.unauthorized));
            mRequestFragment.animation(false);
        } else if (responseCode == 0) {
            showError(getString(R.string.server_unavailable));
            mRequestFragment.animation(false);
        } else if (responseCode != 200) {
            showError(getString(R.string.server_error));
            mRequestFragment.animation(false);
        } else {
            if (intent.getIntExtra(HttpRequestTask.SERVICE_CODE, 0) != 200) {
                showError(getString(R.string.db_error));
                mRequestFragment.animation(false);
            } else {
                finishLogin(intent);
            }
        }
    }

    @Override
    public void cancelled() {
        mRequestFragment.animation(false);
    }

    @Override
    public void animation(boolean b) {
        showForgetPass();
        showProgress(b);
//        mEmailView.setError();
//        mPasswordView.setError();
        mEmailView.requestFocus();
    }

    private boolean isKeyboardShown(View rootView) {
    /* 128dp = 32dp * 4, minimum button height 32dp and generic 4 rows soft keyboard */
        final int SOFT_KEYBOARD_HEIGHT_DP_THRESHOLD = 128;

        Rect r = new Rect();
        rootView.getWindowVisibleDisplayFrame(r);
        DisplayMetrics dm = rootView.getResources().getDisplayMetrics();
    /* heightDiff = rootView height - status bar height (r.top) - visible frame height (r.bottom - r.top) */
        int i1 = rootView.getBottom();
        int i2 = r.bottom;
        int heightDiff = rootView.getBottom() - r.bottom;
    /* Threshold size: dp to pixels, multiply with display density */
        float i3 = SOFT_KEYBOARD_HEIGHT_DP_THRESHOLD * dm.density;
        boolean isKeyboardShown = heightDiff > SOFT_KEYBOARD_HEIGHT_DP_THRESHOLD * dm.density;

        return isKeyboardShown;
    }

    /*  -------------------------------------------------------------------------------
        The code below was extracted from the helper class AccountAuthenticatorActivity,
        avoiding extend it and allowing use of SupportFragmentManager.
    */
    private AccountAuthenticatorResponse mAccountAuthenticatorResponse = null;
    private Bundle mResultBundle = null;

    /**
     * Set the result that is to be sent as the result of the request that caused this
     * Activity to be launched. If result is null or this method is never called then
     * the request will be canceled.
     * @param result this is returned as the result of the AbstractAccountAuthenticator request
     */
    public final void setAccountAuthenticatorResult(Bundle result) {
        mResultBundle = result;
    }

    /**
     * Sends the result or a Constants.ERROR_CODE_CANCELED error if a result isn't present.
     */
    public void finish() {
        if (mAccountAuthenticatorResponse != null) {
            // send the result bundle back if set, otherwise send an error.
            if (mResultBundle != null) {
                mAccountAuthenticatorResponse.onResult(mResultBundle);
            } else {
                mAccountAuthenticatorResponse.onError(AccountManager.ERROR_CODE_CANCELED, "canceled");
            }
            mAccountAuthenticatorResponse = null;
        }
        super.finish();
    }

}