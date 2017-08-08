package com.krav.att.attendance_teacher.Util;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.graphics.Point;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AlertDialog;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.AppCompatRadioButton;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
/*
import com.mind5.microcity.agenda.R;
import com.mind5.microcity.agenda.dialogs.DialogListener;
import com.mind5.microcity.agenda.objects.LocalDate;
import com.mind5.microcity.agenda.objects.LocalTime;
import com.mind5.microcity.agenda.services.HttpRequestTask;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.picasso.OkHttpDownloader;*/

import com.krav.att.attendance_teacher.R;

import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public abstract class Util {

    public static final String SELECTED_ITEM = "S";
    public static final String SHARED_PREFS = "AgendaWarner.SharedPrefs";
    public static final String LATITUDE = "latitude";
    public static final String LONGITUDE = "longitude";

    /*public static OkHttpDownloader getOkHttpDownloader() {
        OkHttpClient okHttpClient = new OkHttpClient();
        final HashMap<String, String> headers = HttpRequestTask.fillImagesHeaderData();

        okHttpClient.interceptors().add(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request.Builder builder = chain.request().newBuilder();

                if (!headers.isEmpty())
                    for (Map.Entry<String, String> entry : headers.entrySet())
                        builder.addHeader(entry.getKey(), entry.getValue());

                Request newRequest = builder.build();
                return chain.proceed(newRequest);
            }
        });
        return new OkHttpDownloader(okHttpClient);
    }*/

    public static boolean isEmpty(String str) {
        if (!TextUtils.isEmpty(str)) {
            return TextUtils.isEmpty(str.trim());
        } else {
            return true;
        }
    }

    public static void askQuestion(Context ctx, String title, String text, boolean cancelable,
                                   String neutralText, String positiveText, String negativeText,
                                   DialogInterface.OnCancelListener cancelCallback,
                                   DialogInterface.OnClickListener neutralCallback,
                                   DialogInterface.OnClickListener positiveCallback,
                                   DialogInterface.OnClickListener negativeCallback) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setTitle(title);
        builder.setMessage(text);

        builder.setCancelable(cancelable);

        if (positiveCallback!=null) {
            builder.setPositiveButton(Util.isEmpty(positiveText)?ctx.getString(R.string.yes):positiveText, positiveCallback);
        }
        if (negativeCallback!=null) {
            builder.setNegativeButton(Util.isEmpty(negativeText) ? ctx.getString(R.string.no) : negativeText, negativeCallback);
        }
        if (neutralCallback!=null) {
            builder.setNeutralButton(Util.isEmpty(neutralText) ? ctx.getString(R.string.cancel) : neutralText, neutralCallback);
        }
        if (cancelCallback!=null) {
            builder.setOnCancelListener(cancelCallback);
        }

        builder.show();
    }


/*     * Return a string that concatenates money symbol (if exists) and value.
     * Ex.: R$ 90,00
     * @param currency may be null.  In this case money prefix won't be concatenated.
     * @param price
     * @return*/


/*    public static String formatMoney(String currency, BigDecimal price) {
        String result = "";

        if (price==null) return result;

        if (!TextUtils.isEmpty(currency)) {
            result = CurrencyEnum.getSymbolFromCurrency(currency);
        }

        NumberFormat nf = NumberFormat.getNumberInstance();
        nf.setMinimumFractionDigits(2);
        nf.setMaximumFractionDigits(2);
        return result + " " + nf.format(price);
    }*/

    public static String padRight(String text, char padChar, int qtd) {
        if (text==null) text = "";
        if (text.length()>=qtd) return text;

        return text + String.format("%1$-" + (qtd-text.length()) + "s", padChar);

    }

    public static String padLeft(String text, char padChar, int qtd) {
        if (text==null) text = "";
        if (text.length()>=qtd) return text;
        return String.format("%1$" + (qtd-text.length()) + "s", padChar) + text;
    }

/*    *//**//**//**//**//**//**//**//**//**//**//**//**//**//**//**//**
     * Returns an object point containing screen's width (point.x) and height (point.y), in pixels.
     * @param ctx
     *//**//**//**//**//**//**//**//**//**//**//**//**//**//**//**//**/
    public static Point getScreenDimensions(Context ctx) {
        WindowManager wm = (WindowManager) ctx.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size;
    }

    public static int dpToPx(Context ctx, int dp) {
        DisplayMetrics displayMetrics = ctx.getResources().getDisplayMetrics();
        return (int)((dp * displayMetrics.density) + 0.5);
    }

    public static int pxToDp(Context ctx, int px) {
        DisplayMetrics displayMetrics = ctx.getResources().getDisplayMetrics();
        return (int) ((px/displayMetrics.density)+0.5);
    }

    private static final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    public static boolean validateEmail(String email) {
        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        return pattern.matcher(email).matches();
    }

    private static final String PHONE_PATTERN = "^[0-9]{2}[0-9]{7,9}$";
    public static boolean validatePhoneNumber(String number) {
        number = PhoneTextWatcherMask.unmask(number);
        Pattern pattern = Pattern.compile(PHONE_PATTERN);
        return pattern.matcher(number).matches();
    }

    public static String firstCapitalLetter(String text) {
        if (isEmpty(text)) return "";
        if (text.length()==1) return text.toUpperCase();
        return text.substring(0,1).toUpperCase()+text.substring(1).toLowerCase();
    }

    public static String formatDate(long date) {
        SimpleDateFormat sdf = (SimpleDateFormat) SimpleDateFormat.getDateInstance();
        sdf.applyPattern("dd MMM - EEEE");
        return sdf.format(new Date(date));
    }

    public static String formatShortDate(long date) {
        SimpleDateFormat sdf = (SimpleDateFormat) SimpleDateFormat.getDateInstance();
        sdf.applyPattern("dd/MM/yyyy");
        return sdf.format(new Date(date));
    }

    public static String getTime(long date) {
        SimpleDateFormat sdf = (SimpleDateFormat) SimpleDateFormat.getDateInstance();
        sdf.applyPattern("HH:mm");
        return sdf.format(new Date(date));
    }

    public static boolean checkDayMonth(int selectedDay, int selectedMonth) {
        try {
            Calendar c = Calendar.getInstance();
            c.setLenient(false);
            //Won't manage leap year, i.e., people born on Feb 29th are going to pick March 1st or Feb 28th.
            c.set(1999, selectedMonth - 1, selectedDay);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /* ------------------------------------------------------
     * Begin - Shows list with radiobuttons
     * ------------------------------------------------------ */
    private static int index;
/*
    public static void showList(Activity ctx, String title, final List<String> list,
                                final Integer defaultSelectedIndex, final DialogListener listener) {
        showList(ctx, title, list, defaultSelectedIndex, null, listener);
    }
*/

    /*public static void showList(Activity ctx, String title, final List<String> list,
                                final Integer defaultSelectedIndex, final Integer style, final DialogListener listener) {
        if (defaultSelectedIndex==null) {
            index=-1;
        } else {
            index = defaultSelectedIndex;
        }

        AlertDialog.Builder builderSingle=null;
        if (style!=null) {
//            builderSingle = new AlertDialog.Builder(ctx, style);
            builderSingle = new AlertDialog.Builder(new ContextThemeWrapper(ctx, style));
        } else {
            builderSingle = new AlertDialog.Builder(ctx);
        }
        if (!isEmpty(title)) {
            builderSingle.setTitle(title);
        }

        *//*final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                ctx, R.layout.select_custom_dialog_item, android.R.id.text1, list);

        builderSingle.setCancelable(false);
        builderSingle.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (listener!=null) {
                    listener.onDialogFinish(null, 0, null);
                }
            }
        });*//*

        builderSingle.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (index<0) return;
                if (listener!=null) {
                    listener.onDialogFinish(null, 0, index);
                }
            }
        });

        builderSingle.setSingleChoiceItems(arrayAdapter ,index,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        AlertDialog a = (AlertDialog)dialog;
                        ListView l = a.getListView();
                        for (int i = 0; i < l.getChildCount(); i++) {
                            ViewGroup vg = (ViewGroup)l.getChildAt(i);
                            AppCompatRadioButton r = (AppCompatRadioButton)vg.findViewById(R.id.rbSelect);
                            r.setChecked(i==which);
                        }
                        index = which;
                    }
                });

        final AlertDialog ad = builderSingle.show();

        //Styling radion buttons on-the-fly
        final ColorStateList colorStateList = new ColorStateList(
                new int[][]{
                        new int[]{-android.R.attr.state_enabled}, //disabled
                        new int[]{android.R.attr.state_enabled} //enabled
                },
                new int[] {
                        R.color.colorPrimary, //disabled
                        R.color.colorPrimary //enabled
                }
        );

        Handler h = new Handler(Looper.getMainLooper());
        h.postDelayed(new Runnable() {
            @Override
            public void run() {
                ListView l = ad.getListView();
                for (int i = 0; i < l.getChildCount(); i++) {
                    ViewGroup vg = (ViewGroup)l.getChildAt(i);
                    AppCompatRadioButton r = (AppCompatRadioButton)vg.findViewById(R.id.rbSelect);
                    r.setTag(new Integer(i));

                    //default checked item, if any defined.
                    r.setChecked(i==index);

                    //styling radiobutton
                    r.setSupportButtonTintList(colorStateList);//set the color tint list

                    //select the radionbutton touched by the user, while
                    //unselect the others.
                    r.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            index = (Integer)v.getTag();
                            ListView l = ad.getListView();
                            for (int i = 0; i < l.getChildCount(); i++) {
                                ViewGroup vg = (ViewGroup)l.getChildAt(i);
                                AppCompatRadioButton r = (AppCompatRadioButton)vg.findViewById(R.id.rbSelect);
                                r.setChecked(i==index);
                            }
                        }
                    });

                    //Click on TextView performs a click on the corresponding radiobutton
                    TextView t = (TextView)vg.findViewById(android.R.id.text1);
                    t.setTag(r);
                    t.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            RadioButton r = (RadioButton)v.getTag();
                            r.performClick();
                        }
                    });
                }
            }
        },300);
    }*/
    /* ------------------------------------------------------
     * End - Shows list with radiobuttons
     * ------------------------------------------------------
*/
    public static void exitApp() {
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(0);
    }

    public static List arrayToList(String[] stringArray, Class listClass) {
        List result = null;
        if (stringArray==null) return result;
        try {
            Constructor<List> c = listClass.getConstructor(null);
            List list = c.newInstance(null);
            for (String s : stringArray) {
                list.add(s);
            }
            result = list;
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static boolean isValidCPFCNPJ(String id) {
        ValidatorCPF_CNPJ validator = ValidatorCPF_CNPJ.getInstance();
        return validator.isCPF(id) || validator.isCNPJ(id);
    }


/*    *
     * Return java.util.Date from string
     * @param strDate format "yyyy-MM-dd"
     * @return*/

    public static Date strToDate(String strDate) {
        SimpleDateFormat sdf = (SimpleDateFormat) SimpleDateFormat.getDateInstance();
        sdf.applyPattern("yyyy-MM-dd");
        try {
            return sdf.parse(strDate);
        } catch (ParseException e) {
            return null;
        }
    }

    public static String readRawData(Context ctx, int fileRawID) {
        InputStream inputStream = ctx.getResources().openRawResource(fileRawID);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        int i;
        try {
            i = inputStream.read();
            while (i != -1) {
                byteArrayOutputStream.write(i);
                i = inputStream.read();
            }
            inputStream.close();
        } catch (IOException e) {
            try {
                inputStream.close();
            } catch (Exception e2) {
            }
            return "";
        }
        return byteArrayOutputStream.toString();
    }

    public long joinDateTime(Calendar c, LocalDate date, LocalTime time) {
        c.set(date.getYear(), date.getMonthOfYear()-1, date.getDayOfMonth(), time.getHourOfDay(), time.getMinuteOfHour(), 0);
        return c.getTime().getTime();
    }

    public static String getSafeDoubleToStr(double number, int decimalPlaces) {
        double tens = Math.pow(10,decimalPlaces);
        return new Double(Math.floor(number*tens) / tens).toString();
    }

    public static void setNavigationIcon(Toolbar toolbar) {
        toolbar.setNavigationIcon(android.support.v7.appcompat.R.drawable.abc_ic_ab_back_material);
    }
}