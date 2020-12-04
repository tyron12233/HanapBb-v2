package com.tyron.hanapbb.messenger;

import android.util.Log;

public class FileLog {
    public static void e(StackTraceElement e){
        Log.e("HanapBb", e.toString());
    }
    public static void d(StackTraceElement e){
        Log.e("HanapBb", e.toString());
    }
    public static void d(String e){
        Log.e("HanapBb", e);
    }

    public static void e(Exception e) {
        Log.e("HanapBb", e.toString());
    }

    public static void e(String toString) {
        Log.e("hanapbb", toString);
    }
}
