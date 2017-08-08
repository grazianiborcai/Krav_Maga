package com.krav.att.attendance_teacher.Views;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.AppCompatEditText;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.widget.EditText;

import com.krav.att.attendance_teacher.R;


public class BackAwareEditText extends AppCompatEditText {

    private BackPressedListener mOnImeBack;
    private boolean whiteBackground = false;

    @TargetApi(21)
  //  public BackAwareEditText(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
  //      super(context, attrs, defStyleAttr, defStyleRes);
   // }

//    private void defineLineColor(int color) {
//        getBackground().mutate().setColorFilter(getResources().getColor(color), PorterDuff.Mode.SRC_ATOP);
//    }

    public BackAwareEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public BackAwareEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BackAwareEditText(Context context) {
        super(context);
    }

    public void setWhiteBackground(boolean whiteBackground) {
        this.whiteBackground = whiteBackground;
//        if (whiteBackground) {
//            defineLineColor(R.color.colorPrimary);
//        }
    }

    @Override
    public boolean onKeyPreIme(int keyCode, KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
            if (mOnImeBack != null) mOnImeBack.onImeBack(this);
        }
        return super.onKeyPreIme(keyCode, event);
    }

    public void setBackPressedListener(BackPressedListener listener) {
        mOnImeBack = listener;
    }

    public interface BackPressedListener {
        void onImeBack(BackAwareEditText editText);
    }

    public void setError() {
        this.setError("");
    }

    public void setNoError() {
        setCompoundDrawables(null, null, null, null);
        setError(null);
    }

    @Override
    public void setError(CharSequence error, Drawable icon) {
//        setCompoundDrawables(null, null, getResources().getDrawable(R.drawable.ic_alert_circle_white_18dp), null);
        if (!whiteBackground && icon!=null) {
            setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_alert_circle_white_18dp, 0);
        } else {
            super.setError(error, icon);
        }
    }

    @Override
    protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter);
        setCompoundDrawables(null, null, null, null);
    }
}