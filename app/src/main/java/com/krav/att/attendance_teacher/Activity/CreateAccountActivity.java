package com.krav.att.attendance_teacher.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.krav.att.attendance_teacher.R;
import com.krav.att.attendance_teacher.Shared.UserDataShared;
import com.krav.att.attendance_teacher.Util.PhoneTextWatcherMask;
import com.krav.att.attendance_teacher.Util.SimpleCrypto;
import com.krav.att.attendance_teacher.Util.Util;
import com.krav.att.attendance_teacher.Views.BackAwareEditText;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class CreateAccountActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final int PASSWORD_REQUIRED_LENGTH = 6;
    private static final int EMAIL_JA_CADASTRADO = 1062;

    protected boolean userAuthenticationFail=false;
    protected FragmentManager fragmentManager;
    //    protected boolean initializingData=true;
    protected String token;

    // UI references.
    protected View mProgressView;
    protected ViewGroup viewMainRedScreen;
    protected ImageView mProgressImage;
    protected Animation scaleAnimation;

    private boolean cancelledWhileInProgress;

    private FloatingActionButton fab;
    private UserDataShared userDataSharedObj = null;

    private Spinner spBirthday, spGender;
    private ArrayAdapter<String> dateAdapter, genderAdapter;

    private BackAwareEditText edtEmail, edtPhone, edtName, edtSurname, edtPassword, edtConfirmPassword;
    private CheckBox checkBox;
    private TextView txtAniversario;

    /*private InsertCustomerRequestFragment service;*/
    private ScrollView scroll;

    private TextWatcher textWatcher = new TextWatcher() {
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
        public void onTextChanged(CharSequence s, int start, int before, int count) {}
        public void afterTextChanged(Editable s) {
            if (validateFillingForm()) fab.show(); else fab.hide();
        }
    };

    private TextWatcher passwordWatcher = new TextWatcher() {
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
        public void onTextChanged(CharSequence s, int start, int before, int count) {}
        public void afterTextChanged(Editable s) {
            if (validateFillingForm() & validatePassword(true)) fab.show(); else fab.hide();
        }
    };

    private TextWatcher phoneTextWatcher = null;
    private Date birthday=null;
    private int gender=0;
    private List genderList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_acc);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.adicionarNovaConta);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        scroll = (ScrollView)findViewById(R.id.scroll_form);

        edtEmail = (BackAwareEditText)findViewById(R.id.edtEmail);
        edtEmail.addTextChangedListener(textWatcher);
        edtEmail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    //validating email
                    String aux = edtEmail.getText().toString();
                    if (!Util.isEmpty(aux) && !Util.validateEmail(aux)) {
                        edtEmail.setError(getString(R.string.error_invalid_email));
                    }
                }
            }
        });
        edtEmail.setWhiteBackground(true);

        edtPhone = (BackAwareEditText)findViewById(R.id.edtPhone);
        edtPhone.addTextChangedListener(textWatcher);
        edtPhone.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    if (phoneTextWatcher == null) {
                        phoneTextWatcher = PhoneTextWatcherMask.insert(edtPhone);
                    }
                    edtPhone.addTextChangedListener(phoneTextWatcher);
                } else {
                    if (phoneTextWatcher != null) {
                        edtPhone.removeTextChangedListener(phoneTextWatcher);
                    }

                    //masking phone number on lost focus
                    String aux = edtPhone.getText().toString();
                    edtPhone.setText(PhoneTextWatcherMask.completeMask(aux));

                    //validating phone number
                    if (!aux.isEmpty() && !Util.validatePhoneNumber(aux)) {
                        edtPhone.setError(getString(R.string.invalid_phone));
                    }
                }
            }
        });
        edtPhone.setWhiteBackground(true);

        edtName = (BackAwareEditText)findViewById(R.id.edtName);
        edtName.addTextChangedListener(textWatcher);
        edtName.setWhiteBackground(true);

        edtSurname = (BackAwareEditText)findViewById(R.id.edtLastName);
        edtSurname.addTextChangedListener(textWatcher);
        edtSurname.setWhiteBackground(true);

        edtPassword = (BackAwareEditText)findViewById(R.id.edtPassword);
        edtPassword.setWhiteBackground(true);
        edtPassword.addTextChangedListener(passwordWatcher);
//        edtPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
//                if (!hasFocus) validatePassword(true);
//            }
//        });

        edtConfirmPassword = (BackAwareEditText)findViewById(R.id.edtConfirmPassword);
        edtConfirmPassword.setWhiteBackground(true);
        edtConfirmPassword.addTextChangedListener(passwordWatcher);
//        edtConfirmPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
//                if (!hasFocus) validatePassword(true);
//            }
//        });

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.hide();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateFillingForm()) {
                    /*register();*/
                }
            }
        });

        mProgressView = findViewById(R.id.progress);

        txtAniversario = (TextView)findViewById(R.id.txtAniversario);
        spBirthday = (Spinner) findViewById(R.id.spBirthday);
        /*spBirthday.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction()== MotionEvent.ACTION_UP) {
                    DatePickerFragment datePickerDialog = DatePickerFragment.newInstance(birthday,
                            getString(R.string.birthday), new DialogListener() {
                                @Override
                                public void onDialogFinish(String tag, int action, Object response) {
                                    if (response == null) return;
                                    Calendar c = (Calendar) response;
                                    birthday = c.getTime();
                                    dateAdapter.clear();
                                    dateAdapter.add(Util.formatShortDate(birthday.getTime()));
                                    dateAdapter.notifyDataSetChanged();
                                    if (validateFillingForm()) {
                                        fab.show();
                                    } else {
                                        fab.hide();
                                    }
                                }
                            });
                    datePickerDialog.show(getSupportFragmentManager(), "datePicker");
                }
                return true;
            }
        });*/

        spGender = (Spinner) findViewById(R.id.spGender);
        /*spGender.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction()== MotionEvent.ACTION_UP) {
                    Util.showList(CreateAccountActivity.this, null, genderList, gender-1, new DialogListener() {
                        @Override
                        public void onDialogFinish(String tag, int action, Object response) {
                            if (response == null) return;
                            Integer index = (Integer)response;
                            gender = index+1;
                            genderAdapter.clear();
                            genderAdapter.add((String)genderList.get(index));
                            genderAdapter.notifyDataSetChanged();
                            if (validateFillingForm()) {
                                fab.show();
                            } else {
                                fab.hide();
                            }
                        }
                    });
                }
                return true;
            }
        });*/

        checkBox = (CheckBox)findViewById(R.id.checkbox);
        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateFillingForm()) fab.show(); else fab.hide();
            }
        });
        checkBox.setMovementMethod(LinkMovementMethod.getInstance());

        prepareSpinners();
        /*prepareService();*/
    }

    public void onStart() {
        super.onStart();
    }

    private void prepareSpinners() {
/*        ArrayList<String> dates = new ArrayList<>();
        dates.add(getString(R.string.spinnerItemSelect));
        dateAdapter = new SpinnerArrayAdapter<String>(this, android.R.layout.simple_spinner_item, dates);
        dateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spBirthday.setAdapter(dateAdapter);

        genderList = Util.arrayToList(getResources().getStringArray(R.array.array_gender), ArrayList.class);
        ArrayList<String> genders = new ArrayList<>();
        genders.add(getString(R.string.spinnerItemSelect));
        genderAdapter = new SpinnerArrayAdapter<String>(this, android.R.layout.simple_spinner_item, genders);
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spGender.setAdapter(genderAdapter);*/
    }

    private boolean validateFillingForm() {
        //verify if one field is blank.
        boolean error = (!Util.validateEmail(edtEmail.getText().toString())) ||
                (!Util.validatePhoneNumber(edtPhone.getText().toString())) ||
                Util.isEmpty(edtName.getText().toString()) ||
                Util.isEmpty(edtSurname.getText().toString()) ||
                Util.isEmpty(edtPassword.getText().toString()) ||
                Util.isEmpty(edtConfirmPassword.getText().toString());

        if (!error) {
            //check if month and year were informed
            error = birthday==null || gender==0;
            if (error) {
                txtAniversario.setError("");
            } else {
                txtAniversario.setError(null);
            }
        }

        error = error | !validatePassword(false) || !checkBox.isChecked();

        return !error;
    }

    private boolean validatePassword(boolean visualFeedback) {
        boolean error = false;

        if (!Util.isEmpty(edtPassword.getText().toString()) && !Util.isEmpty(edtConfirmPassword.getText().toString())) {
            //check if password matches confirmation
            error = !(edtPassword.getText().toString().equals(edtConfirmPassword.getText().toString()));
            if (error) {
                if (visualFeedback) {
                    edtPassword.setError(getString(R.string.error_dont_match_password));
                }
            } else {
                //check if passwords have minimum required length
                error = edtPassword.length() < PASSWORD_REQUIRED_LENGTH;
                if (error && visualFeedback) {
                    edtPassword.setError(getString(R.string.error_invalid_password));
                } else {
                    try {
                        //test problems with the crypto engine
                        SimpleCrypto.sha256(edtPassword.getText().toString());
                    } catch (Exception e) {
                        error = true;
                        if (visualFeedback) {
                            edtPassword.setError(getString(R.string.error_incorrect_password));
                        }
                    }
                }
            }
        }
        if (!error) {
            edtPassword.setNoError();
        }

        return !error;
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    /*private DialogListener forgetPasswordDialogListener = new DialogListener() {
        @Override
        public void onDialogFinish(String tag, int action, Object response) {
            if (action==ForgotPasswordViewDialog.ACTION_CANCELED) {
                setResult(RESULT_CANCELED, null);
                finish();
            } else
            if (action==ForgotPasswordViewDialog.ACTION_PASSW_CHANGED) {
                MyToast.makeText(getString(R.string.passwordChanged), CreateAccountActivity.this.getApplicationContext(),
                        Toast.LENGTH_LONG, R.layout.toast_custom, Gravity.CENTER);
                Intent data = new Intent();
                data.putExtra(Intent.EXTRA_INTENT, (String)response); //email
                setResult(RESULT_OK, data);
                finish();
            }
        }
    };*/

    /*private void prepareService() {
        service = InsertCustomerRequestFragment.newInstance(new OnTaskFinished() {
            @Override
            public void postExecute(Intent intent) {
                showProgress(false);
                if (checkBasicTaskResults(intent)) {
                    //check code=1062;  message=Email já cadastrado. Deseja solicitar a recuperação de senha?
                    int code = intent.getIntExtra(HttpRequestTask.SERVICE_CODE, 0);
                    if (code == EMAIL_JA_CADASTRADO) {
                        String msg = intent.getStringExtra(HttpRequestTask.SERVICE_MESSAGE);
                        Util.askQuestion(CreateAccountActivity.this,
                                getString(R.string.title_email_already_exists),
                                msg,
                                //getString(R.string.message_email_already_exists),
                                false, null, null, null, null, null, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        ForgotPasswordViewDialog f =
                                                ForgotPasswordViewDialog.newInstance(edtEmail.getText().toString().trim(),
                                                        forgetPasswordDialogListener);
                                        f.show(getSupportFragmentManager(),ForgotPasswordViewDialog.TAG);
                                    }
                                }, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        edtEmail.setText("");
                                        edtEmail.requestFocus();
                                        scroll.fullScroll(View.FOCUS_UP);
                                    }
                                });
                    } else {
                        Intent data = new Intent();
                        data.putExtra(Intent.EXTRA_INTENT, edtEmail.getText().toString().trim());
                        data.putExtra(Intent.EXTRA_SHORTCUT_NAME, passwSHA256);
                        setResult(RESULT_OK, data);
                        finish();
                    }
                }
            }

            @Override
            public void cancelled() {
                showProgress(false);
            }

            @Override
            public void animation(boolean b) {
                showProgress(b);
            }
        });

        getSupportFragmentManager().beginTransaction().add(service, InsertCustomerRequestFragment.TAG_TASK_SERVICES).commit();

    }*/

    /*private String passwSHA256 = null;
    private void register() {
        try {
            passwSHA256 = SimpleCrypto.sha256(edtPassword.getText().toString().trim());
        } catch (Exception ignore) {}//won't happen

        Customer customer = new Customer();

        LocalDate bornDate = new LocalDate(birthday);
        customer.setBornDate(bornDate);

        customer.setCodGender((byte) gender);
        customer.setEmail(edtEmail.getText().toString().trim());
        customer.setName(edtName.getText().toString().trim() + " " + edtSurname.getText().toString().trim());
        customer.setPassword(passwSHA256);
        customer.setPhone(edtPhone.getText().toString().trim());

        showProgress(true);
        service.execute(customer);
    }*/

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return false;
    }

//    private boolean isKeyboardShown(View rootView) {
//    /* 128dp = 32dp * 4, minimum button height 32dp and generic 4 rows soft keyboard */
//        final int SOFT_KEYBOARD_HEIGHT_DP_THRESHOLD = 128;
//
//        Rect r = new Rect();
//        rootView.getWindowVisibleDisplayFrame(r);
//        DisplayMetrics dm = rootView.getResources().getDisplayMetrics();
//    /* heightDiff = rootView height - status bar height (r.top) - visible frame height (r.bottom - r.top) */
//        int heightDiff = dm.heightPixels - r.bottom;
//    /* Threshold size: dp to pixels, multiply with display density */
//        boolean isKeyboardShown = heightDiff > SOFT_KEYBOARD_HEIGHT_DP_THRESHOLD * dm.density;
//
//        return isKeyboardShown;
//    }
}