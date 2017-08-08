package com.krav.att.attendance_teacher.Requests.Util;

public class NamedPairParameter {
    private String key, value;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setPair(String key, String value) {
        setKey(key);
        setValue(value);
    }
}
