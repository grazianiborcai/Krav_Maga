package com.krav.att.attendance_teacher.Util;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

public abstract class PhoneTextWatcherMask {
    private static String mask = "(##)#########";

    public static String unmask(String s) {
        return s.replaceAll("[-]", "")
                .replaceAll("[ ]", "")
                .replaceAll("[(]", "")
                .replaceAll("[)]", "");
    }

    public static String completeMask(String s) {
        String unmaskStr = unmask(s);
        if (Util.isEmpty(unmaskStr) || (unmaskStr.length()<9)) return s;

        return "("+unmaskStr.substring(0,2)+") "+
                unmaskStr.substring(2,unmaskStr.length()-4)+"-"+
                unmaskStr.substring(unmaskStr.length()-4);
    }

    public static TextWatcher insert(final EditText ediTxt) {
        return new TextWatcher() {
            boolean isUpdating;
            String old = "";

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String str = PhoneTextWatcherMask.unmask(s.toString());
                String mascara = "";
                if (isUpdating) {
                    old = str;
                    isUpdating = false;
                    return;
                }

                int i = 0;
                for (char m : mask.toCharArray()) {
                    if (m != '#' && str.length() > old.length()) {
                        mascara += m;
                        continue;
                    }
                    try {
                        mascara += str.charAt(i);
                    } catch (Exception e) {
                        break;
                    }
                    i++;
                }
                isUpdating = true;
                ediTxt.setText(mascara);
                ediTxt.setSelection(mascara.length());
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            public void afterTextChanged(Editable s) {}
        };
    }
}