package com.example.dialer;

public class CallRecord {
    public static final int INCOMING = 0;
    public static final int OUTGOING = 1;
    public static final int MISSED = 2;

    public String name;
    public String number;
    public int type;
    public String time;
    public String duration;

    public CallRecord(String name, String number, int type, String time, String duration) {
        this.name = name;
        this.number = number;
        this.type = type;
        this.time = time;
        this.duration = duration;
    }
}
